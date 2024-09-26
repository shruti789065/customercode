package com.jakala.menarini.core.service;

import java.nio.file.AccessDeniedException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import javax.sql.DataSource;

import com.jakala.menarini.core.dto.*;
import com.jakala.menarini.core.entities.RegisteredUser;
import com.jakala.menarini.core.entities.RegisteredUserTopic;
import com.jakala.menarini.core.entities.records.RegisteredUserRoleRecord;
import com.jakala.menarini.core.entities.records.RegisteredUserTopicRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.jakala.menarini.core.entities.records.RegisteredUserRecord;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.security.AclRolePermissions;
import com.jakala.menarini.core.security.AclValidator;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = UserRegisteredServiceInterface.class)
public class UserRegisteredService implements UserRegisteredServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegisteredService.class);

    @Reference(target = "(datasource.name=fondazione-mysql)")
    private DataSource dataSource;


    @Override
    public List<RegisteredUserDto> getUsers(Set<Acl> acls) throws AccessDeniedException {
        List<RegisteredUserDto> users = new ArrayList<>();

        AclValidator.isAccessAllowed(AclRolePermissions.VIEW_REGISTERED_USERS, acls);

        try (Connection connection = dataSource.getConnection()) {

            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            return create.select().from(RegisteredUserDto.table).fetch().into(RegisteredUserDto.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }


    @Override
    public RegisteredUserDto getUserByEmail(String email, Set<Acl> acls) throws AccessDeniedException, SQLException {

        AclValidator.isAccessAllowed(AclRolePermissions.VIEW_REGISTERED_USERS, acls);
        Connection connection = dataSource.getConnection();
        DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
        RegisteredUserDto user = (RegisteredUserDto) Objects.requireNonNull(create
                .select()
                .from(RegisteredUserDto.table)
                .where(RegisteredUser.REGISTERED_USER.EMAIL.eq(email))
                .fetchOne()).into(RegisteredUserDto.class);
        if(user == null) {
            connection.close();
            return null;
        }
        ArrayList<String> topicsUser = (ArrayList<String>) TopicUtils.getTopicsRefForUser(user.getId(), create);
        if(topicsUser != null && !topicsUser.isEmpty()) {
            ArrayList<RegisteredUserTopicDto> topics = new ArrayList<>();
            for(String topic :  topicsUser) {
                TopicDto topicData = new TopicDto();
                topicData.setId(topic);
                topicData.setName(null);
                RegisteredUserTopicDto topicDto = new RegisteredUserTopicDto();
                topicDto.setTopic(topicData);
                topics.add(topicDto);
            }
            user.setRegisteredUserTopics(topics);
        }
        connection.close();
        return user;

    }

    @Override
    public RegisteredUseServletResponseDto updateUserData(String email, RegisteredUserDto user, List<String> updateTopics, Set<Acl> acls) throws AccessDeniedException, SQLException {
        RegisteredUseServletResponseDto response = new RegisteredUseServletResponseDto();
        String errorMessage = "";
        Connection connection = null;
        Savepoint savepoint = null;
        AclValidator.isAccessAllowed(AclRolePermissions.VIEW_REGISTERED_USERS, acls);
        try {
            RegisteredUserDto oldData = this.getUserByEmail(email, acls);
            user.setId(oldData.getId());
            connection = dataSource.getConnection();
            savepoint = connection.setSavepoint();
            DSLContext create = DSL.using(connection);
            Record dataSet = this.setUpdateQuery(user, create);
            int x = create.update(RegisteredUser.REGISTERED_USER).set(dataSet)
                    .where(RegisteredUser.REGISTERED_USER.EMAIL.eq(email)).execute();

            if(!updateTopics.isEmpty()) {

                List<String> oldTopicRefs = TopicUtils.getTopicsRefForUser(oldData.getId(),create);
                List<String> topicToSaveRefers =  new ArrayList<String>();
                for(String topic : updateTopics) {
                    if(!oldTopicRefs.contains(topic)) {
                        topicToSaveRefers.add(topic);
                    }
                }
                boolean isDeleted = this.deleteTopics(user.getId(),updateTopics,oldTopicRefs,create,connection,savepoint);
                if(!isDeleted) {
                    response.setSuccess(false);
                    errorMessage = "Error on delete topics";
                    response.setErrorMessage(errorMessage);
                    return response;

                }
                for(String topicRef : topicToSaveRefers) {
                    LOGGER.error("id topics to save: "+topicRef);
                }
                boolean isCreated = this.saveNewTopic(topicToSaveRefers,user,create,connection,savepoint);
                if(!isCreated) {
                    response.setSuccess(false);
                    errorMessage = "Error on add topics";
                    response.setErrorMessage(errorMessage);
                    return response;
                }
            }
            connection.close();
            response.setSuccess(true);
            response.setUpdatedUser(user);
            return response;
        } catch (SQLException e) {
            e.printStackTrace();
            //connection.rollback(savepoint);
            //connection.close();
            errorMessage = e.getMessage();
            response.setSuccess(false);
            response.setErrorMessage(errorMessage);
            return response;
        } catch (Exception e) {
            errorMessage = e.getMessage();
            response.setSuccess(false);
            response.setErrorMessage(errorMessage);
            return response;
        }
    }

    private List<String> convertTopicsNameToId(List<String> topics, List<TopicDto> topicsRefs) {
        List<String> convertedTopics = new ArrayList<>();
        for(String topic : topics) {
            for(TopicDto topicRef : topicsRefs) {
                if(topicRef.getName().equals(topic)) {
                    convertedTopics.add(topicRef.getId());
                    break;
                }
            }
        }
        return convertedTopics;
    }

    private boolean deleteTopics(Long idUser,
                                 List<String> updateTopics,
                                 List<String> oldTopic,
                                 DSLContext create,
                                 Connection connection,
                                 Savepoint savepoint) throws SQLException {

        oldTopic.removeIf(topic -> updateTopics.contains(topic));
        List<String> deleteTopics = new ArrayList<>(oldTopic);
        LOGGER.error("------- on Delete topic func --------");
        for(String topic : oldTopic) {
            LOGGER.error("id topic to delete"+topic);
        }
        try{
            if(!deleteTopics.isEmpty()) {
                create.deleteFrom(RegisteredUserTopic.REGISTERED_USER_TOPIC).
                        where(RegisteredUserTopic.REGISTERED_USER_TOPIC.TOPIC_ID.in(deleteTopics))
                        .and(RegisteredUserTopic.REGISTERED_USER_TOPIC.REGISTERED_USER_ID.eq(idUser))
                        .execute();
            }
            return true;
          }catch (Exception e) {
            e.printStackTrace();
            connection.rollback(savepoint);
            connection.close();
            return false;
        }
    }

    private boolean saveNewTopic(
            List<String> updateTopicRefs,
            RegisteredUserDto user,
            DSLContext create,
            Connection connection,
            Savepoint savepoint) throws SQLException {

        if(updateTopicRefs.isEmpty()) {
            return true;
        }
        try {
            updateTopicRefs.forEach(topicRef -> {
                LOGGER.error("on save topic");
                TopicDto topic = new TopicDto();
                topic.setId(topicRef);
                RegisteredUserTopicDto topicUsr = new RegisteredUserTopicDto();
                topicUsr.setTopic(topic);
                topicUsr.setRegisteredUser(user);
                RegisteredUserTopicRecord rowData = create.
                        newRecord(RegisteredUserTopic.REGISTERED_USER_TOPIC, topicUsr);
                rowData.setRegisteredUserId(user.getId());
                rowData.setTopicId(topic.getId());
                rowData.setCreatedOn(LocalDateTime.now());
                rowData.setLastUpdatedOn(LocalDateTime.now());
                rowData.store();
            });
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            connection.rollback(savepoint);
            connection.close();
            return false;
        }
    }



    private Record setUpdateQuery(RegisteredUserDto user, DSLContext create) {
        LocalDateTime now = LocalDateTime.now();
        Record dataSet = create.newRecord(RegisteredUser.REGISTERED_USER);
        dataSet.set(RegisteredUser.REGISTERED_USER.LAST_UPDATED_ON,now);
        if(user.getFirstname() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.FIRSTNAME, user.getFirstname());
        }
        if(user.getLastname() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.LASTNAME, user.getLastname());
        }
        if(user.getCountry() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.COUNTRY, user.getCountry());
        }
        if(user.getBirthDate() != null) {
            LocalDate date = LocalDate.ofInstant(user.getBirthDate().toInstant(), ZoneId.systemDefault());
            dataSet.set(RegisteredUser.REGISTERED_USER.BIRTH_DATE,date);
        }
        if(user.getPhone() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.PHONE, user.getPhone());
        }
        if(user.getGender() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.GENDER, user.getGender());
        }
        if(user.getLinkedinProfile() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.LINKEDIN_PROFILE, user.getLinkedinProfile());
        }
        if(user.getTaxIdCode() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.TAX_ID_CODE, user.getTaxIdCode());
        }
        if(user.getOccupation() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.OCCUPATION, user.getOccupation());
        }
        if(user.getPersonalDataProcessingConsent() != null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.PERSONAL_DATA_PROCESSING_CONSENT, user.getPersonalDataProcessingConsent());
            dataSet.set(RegisteredUser.REGISTERED_USER.PERSONAL_DATA_PROCESSING_CONSENT_TS,now);
        }
        if(user.getProfilingConsent()!= null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.PROFILING_CONSENT, user.getProfilingConsent());
            dataSet.set(RegisteredUser.REGISTERED_USER.PROFILING_CONSENT_TS,now);
        }
        if(user.getProfilingConsent()!= null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.PROFILING_CONSENT, user.getProfilingConsent());
            dataSet.set(RegisteredUser.REGISTERED_USER.PROFILING_CONSENT_TS,now);
        }
        if(user.getProfilingConsent()!= null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.PROFILING_CONSENT, user.getProfilingConsent());
            dataSet.set(RegisteredUser.REGISTERED_USER.PROFILING_CONSENT_TS,now);
        }
        if(user.getNewsletterSubscription()!= null) {
            dataSet.set(RegisteredUser.REGISTERED_USER.NEWSLETTER_SUBSCRIPTION, user.getNewsletterSubscription());
            dataSet.set(RegisteredUser.REGISTERED_USER.NEWSLETTER_SUBSCRIPTION_TS,now);
        }
        return dataSet;
    }



    @Override
    public boolean addUser(RegisteredUserDto user, Set<Acl> acls) throws AccessDeniedException {
        AclValidator.isAccessAllowed(AclRolePermissions.ADD_REGISTERED_USERS, acls);

        try (Connection connection = dataSource.getConnection()) {

            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            RegisteredUserRecord rur = create.newRecord(com.jakala.menarini.core.entities.RegisteredUser.REGISTERED_USER, user);
            rur.store();
            create.executeInsert(rur);
            connection.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }



    @Override
    public boolean isActiveUser(String username) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            RegisteredUserDto userDto = (RegisteredUserDto) Objects.requireNonNull(create
                    .select()
                    .from(RegisteredUserDto.table)
                    .where(RegisteredUser.REGISTERED_USER.USERNAME.eq(username))
                    .fetchOne()).into(RegisteredUserDto.class);
            connection.close();
            return Objects.equals(userDto.getRegistrationStatus(), "confirmed");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean addUserForSignUp(RegisteredUserDto user, ArrayList<RoleDto> roles, ArrayList<TopicDto> topics){
        Connection connection = null;
        Savepoint savepoint = null;
        try  {
            connection = dataSource.getConnection();
            savepoint = connection.setSavepoint();
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            RegisteredUserRecord rur = create.newRecord(com.jakala.menarini.core.entities.RegisteredUser.REGISTERED_USER, user);
            rur.store();
            if(rur.getId() != null) {
                final Long id = rur.getId();
                user.setId(id);
                ArrayList<RegisteredUserRoleDto> listRoles = new ArrayList<>();
                roles.forEach(role -> {
                    RegisteredUserRoleDto ur = new RegisteredUserRoleDto();
                    ur.setRegisteredUser(user);
                    ur.setRole(role);
                    ur.setCreatedOn(new Timestamp(System.currentTimeMillis()));
                    ur.setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
                    listRoles.add(ur);
                    RegisteredUserRoleRecord roleRecord = create.
                            newRecord(com.jakala.menarini.core.entities.RegisteredUserRole.REGISTERED_USER_ROLE, ur);
                    roleRecord.setRegisteredUserId(user.getId());
                    roleRecord.setRoleId(role.getId());
                    roleRecord.store();
                });
                if(!listRoles.isEmpty()) {
                    user.setRegisteredUserRoles(listRoles);
                    rur.store();
                }
                ArrayList<RegisteredUserTopicDto> listTopics = new ArrayList<>();
                topics.forEach(topic -> {
                    RegisteredUserTopicDto tur = new RegisteredUserTopicDto();
                    tur.setRegisteredUser(user);
                    tur.setTopic(topic);
                    tur.setCreatedOn(new Timestamp(System.currentTimeMillis()));
                    tur.setLastUpdatedOn(new Timestamp(System.currentTimeMillis()));
                    listTopics.add(tur);
                    RegisteredUserTopicRecord topicRecord = create.
                            newRecord(RegisteredUserTopic.REGISTERED_USER_TOPIC, tur);
                    //LOGGER.error(topicRecord.getId().toString());
                    topicRecord.setRegisteredUserId(user.getId());
                    topicRecord.setTopicId(topic.getId());
                    topicRecord.store();
                    /*create.executeInsert(topicRecord);*/
                });
                if(!listTopics.isEmpty()) {
                    user.setRegisteredUserTopics(listTopics);
                }
                rur.store();
                create.executeInsert(rur);
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if(connection != null && savepoint != null) {
                try {
                    connection.rollback(savepoint);
                } catch (SQLException ex) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
    }

}
