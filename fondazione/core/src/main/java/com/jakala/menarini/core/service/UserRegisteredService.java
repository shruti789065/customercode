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
import com.jakala.menarini.core.entities.RegisteredUserRole;
import com.jakala.menarini.core.entities.RegisteredUserTopic;
import com.jakala.menarini.core.entities.records.RegisteredUserRoleRecord;
import com.jakala.menarini.core.entities.records.RegisteredUserTopicRecord;
import com.jakala.menarini.core.service.interfaces.RoleServiceInterface;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.jakala.menarini.core.entities.records.RegisteredUserRecord;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.security.AclRolePermissions;
import com.jakala.menarini.core.security.AclValidator;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;


@Component(service = UserRegisteredServiceInterface.class)
public class UserRegisteredService implements UserRegisteredServiceInterface {


    private static final String USER_ROLE_DOCTOR = "UserDoctor";
    private static final String USER_ROLE_HEALTH = "UserHealthCare";
    private static final String COUNTRY_ID_ITALY = "1";


    private static final Map<String,String> MAP_PROFESSION_TO_ROLE;
    static {
        MAP_PROFESSION_TO_ROLE  = new HashMap<>();
        MAP_PROFESSION_TO_ROLE .put("Biologist", USER_ROLE_HEALTH);
        MAP_PROFESSION_TO_ROLE .put("Diagnostic Laboratory Technician", USER_ROLE_HEALTH);
        MAP_PROFESSION_TO_ROLE .put("Doctor", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Healthcare Worker", USER_ROLE_HEALTH);
        MAP_PROFESSION_TO_ROLE .put("No Healthcare", "User");
        MAP_PROFESSION_TO_ROLE .put("Nurse", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Nurse’s Assistant", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Obstetrician", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Pharmacist", USER_ROLE_HEALTH);
        MAP_PROFESSION_TO_ROLE .put("Psichologist", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Student", "User");
    }


    @Reference(target = "(datasource.name=fondazione-mysql)")
    private DataSource dataSource;

    @Reference
    private  RoleServiceInterface roleService;



    @Override
    public List<RegisteredUserDto> getUsers(Set<Acl> acls) throws AccessDeniedException {
        List<RegisteredUserDto> users = new ArrayList<>();

        AclValidator.isAccessAllowed(AclRolePermissions.VIEW_REGISTERED_USERS, acls);

        try (Connection connection = dataSource.getConnection()) {

            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            return create.select().from(RegisteredUserDto.table).fetch().into(RegisteredUserDto.class);

        } catch (SQLException e) {
            return users;
        }

    }


    @Override
    public RegisteredUserDto getUserByEmail(String email, Set<Acl> acls, List<RoleDto> roles ) throws AccessDeniedException {
        AclValidator.isAccessAllowed(AclRolePermissions.VIEW_REGISTERED_USERS, acls);
        try(Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            RegisteredUserDto user = Objects.requireNonNull(create
                    .select()
                    .from(RegisteredUserDto.table)
                    .where(RegisteredUser.REGISTERED_USER.EMAIL.eq(email))
                    .fetchOne()).into(RegisteredUserDto.class);

            if(this.checkShouldBeHaveToken(roles)) {
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
            }
            return user;
        }catch (SQLException e) {
            return null;
        }
    }

    @Override
    public RegisteredUseServletResponseDto updateUserData(String email, RegisteredUserDto user, List<String> updateTopics, Set<Acl> acls, List<RoleDto> roles) throws AccessDeniedException, SQLException {
        RegisteredUseServletResponseDto response = new RegisteredUseServletResponseDto();
        String errorMessage = "";
        AclValidator.isAccessAllowed(AclRolePermissions.VIEW_REGISTERED_USERS, acls);
        try {
            RegisteredUserDto oldData = this.getUserByEmail(email, acls,roles);
            user.setId(oldData.getId());
            String country = user.getCountry() == null ? oldData.getCountry() : user.getCountry();
            return this.updateUser(user,oldData,updateTopics,email,country,roles);
        } catch (AccessDeniedException e) {
            response.setSuccess(false);
            errorMessage = e.getMessage();
            response.setErrorMessage(errorMessage);
            return response;
        }
    }

    private RegisteredUseServletResponseDto updateUser(
            RegisteredUserDto user,
            RegisteredUserDto oldData,
            List<String> updateTopics,
            String email,
            String country,
            List<RoleDto> roles) {

        RegisteredUseServletResponseDto response = new RegisteredUseServletResponseDto();
        String errorMessage = "";
        Savepoint savepoint = null;
        try(Connection connection = dataSource.getConnection()) {

            savepoint = connection.setSavepoint();
            DSLContext create = DSL.using(connection);
            Record dataSet = this.setUpdateQuery(user, create);
            create.update(RegisteredUser.REGISTERED_USER).set(dataSet)
                    .where(RegisteredUser.REGISTERED_USER.EMAIL.eq(email)).execute();
            if(!user.getOccupation().equals(oldData.getOccupation())) {
                ArrayList<Long> oldRoles = new ArrayList<>();
                for(RoleDto role : roles) {
                    oldRoles.add(role.getId());
                }

                this.updateRole(user,oldRoles,user.getOccupation(),oldData.getId(),create,connection,savepoint);
                response.setIslogOut(true);
            }
            if(!updateTopics.isEmpty() && this.checkShouldBeHaveToken(roles)) {
                List<String> oldTopicRefs = TopicUtils.getTopicsRefForUser(oldData.getId(),create);
                List<String> topicToSaveRefers =  new ArrayList<>();
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
                boolean isCreated = this.saveNewTopic(topicToSaveRefers,user,create,connection,savepoint);
                if(!isCreated) {
                    response.setSuccess(false);
                    errorMessage = "Error on add topics";
                    response.setErrorMessage(errorMessage);
                    return response;
                }
            }
            ArrayList<RoleDto> actualRoles = (ArrayList<RoleDto>)roleService.getRolesUser(oldData.getId());
            RoleDto[] arrayRoles = new RoleDto[actualRoles.size()];
            actualRoles.toArray(arrayRoles);
            RegisteredUserPermissionDto permissions = this.generateUserPermission(arrayRoles[0],country);
            response.setUserPermission(permissions);
            response.setSuccess(true);
            response.setUpdatedUser(user);
            return response;
        }catch (SQLException e) {
            response.setSuccess(false);
            errorMessage = e.getMessage();
            response.setErrorMessage(errorMessage);
            return response;
        }
    }

    private boolean updateRole(RegisteredUserDto user,
                               List<Long> rolesToDelete,
                               String occupation,
                               Long idUser, DSLContext create,
                               Connection connection,
                               Savepoint savepoint) throws SQLException {
        try{
            ArrayList<RoleDto> newRoles  = new ArrayList<>();
            List<RoleDto> allRoles = roleService.getRoles();
            for(RoleDto aRoles: allRoles) {
                if(aRoles.getName().equals(MAP_PROFESSION_TO_ROLE.get(occupation))) {
                    newRoles.add(aRoles);
                }
            }

            create.deleteFrom(RegisteredUserRole.REGISTERED_USER_ROLE).
                    where(RegisteredUserRole.REGISTERED_USER_ROLE.ROLE_ID.in(rolesToDelete))
                    .and(RegisteredUserRole.REGISTERED_USER_ROLE.REGISTERED_USER_ID.eq(idUser))
                    .execute();

            newRoles.forEach(role -> {
                RegisteredUserRoleDto roleUsr = new RegisteredUserRoleDto();
                roleUsr.setRole(role);
                roleUsr.setRegisteredUser(user);
                RegisteredUserRoleRecord rowData = create.
                        newRecord(RegisteredUserRole.REGISTERED_USER_ROLE, roleUsr);
                rowData.setRegisteredUserId(user.getId());
                rowData.setRoleId(role.getId());
                rowData.setCreatedOn(LocalDateTime.now());
                rowData.setLastUpdatedOn(LocalDateTime.now());
                rowData.store();
            });
            return true;
        }catch (DataAccessException e) {
            connection.rollback(savepoint);
            connection.close();
            return false;
        }

    }



    private boolean deleteTopics(Long idUser,
                                 List<String> updateTopics,
                                 List<String> oldTopic,
                                 DSLContext create,
                                 Connection connection,
                                 Savepoint savepoint) throws SQLException {

        oldTopic.removeIf(updateTopics::contains);
        List<String> deleteTopics = new ArrayList<>(oldTopic);
        try{
            if(!deleteTopics.isEmpty()) {
                create.deleteFrom(RegisteredUserTopic.REGISTERED_USER_TOPIC).
                        where(RegisteredUserTopic.REGISTERED_USER_TOPIC.TOPIC_ID.in(deleteTopics))
                        .and(RegisteredUserTopic.REGISTERED_USER_TOPIC.REGISTERED_USER_ID.eq(idUser))
                        .execute();
            }
            return true;
          }catch (DataAccessException e) {
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
        }catch (DataAccessException e) {
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
    public RegisteredUserPermissionDto generateUserPermission(RoleDto role, String idCountry) {
        RegisteredUserPermissionDto permissionDto = new RegisteredUserPermissionDto();
        String roleName = role.getName();
        switch (roleName) {
            case USER_ROLE_HEALTH:
                permissionDto.setEventSubscription(true);
                permissionDto.setMaterialAccess(true);
                permissionDto.setMagazineSubscription(false);
                break;
            case USER_ROLE_DOCTOR:
                permissionDto.setEventSubscription(true);
                permissionDto.setMaterialAccess(true);
                permissionDto.setMagazineSubscription(idCountry.equals(COUNTRY_ID_ITALY));
                break;
            default:
                permissionDto.setEventSubscription(true);
                permissionDto.setMaterialAccess(false);
                permissionDto.setMagazineSubscription(false);

        }
        return permissionDto;
    }



    @Override
    public boolean addUser(RegisteredUserDto user, Set<Acl> acls) throws AccessDeniedException {
        AclValidator.isAccessAllowed(AclRolePermissions.ADD_REGISTERED_USERS, acls);

        try (Connection connection = dataSource.getConnection()) {

            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            RegisteredUserRecord rur = create.newRecord(com.jakala.menarini.core.entities.RegisteredUser.REGISTERED_USER, user);
            rur.store();
            create.executeInsert(rur);
            return true;

        } catch (SQLException e) {
            return false;
        }
    }



    @Override
    public boolean isActiveUser(String username) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            RegisteredUserDto userDto =  Objects.requireNonNull(create
                    .select()
                    .from(RegisteredUserDto.table)
                    .where(RegisteredUser.REGISTERED_USER.USERNAME.eq(username))
                    .fetchOne()).into(RegisteredUserDto.class);
            return Objects.equals(userDto.getRegistrationStatus(), "confirmed");
        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }


    @Override
    public boolean addUserForSignUp(RegisteredUserDto user, List<RoleDto> roles, List<TopicDto> topics){
        try(Connection connection = dataSource.getConnection())  {
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
                if(this.checkShouldBeHaveToken(roles)) {
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
                        topicRecord.setRegisteredUserId(user.getId());
                        topicRecord.setTopicId(topic.getId());
                        topicRecord.store();
                    });
                    if(!listTopics.isEmpty()) {
                        user.setRegisteredUserTopics(listTopics);
                    }
                }
                rur.store();
                create.executeInsert(rur);
            }
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    private boolean checkShouldBeHaveToken(List<RoleDto> roles) {
        for(RoleDto role : roles) {
            if(role.getName().equals(USER_ROLE_DOCTOR) || role.getName().equals(USER_ROLE_HEALTH)) {
                return true;
            }
         }
        return false;
    }

}
