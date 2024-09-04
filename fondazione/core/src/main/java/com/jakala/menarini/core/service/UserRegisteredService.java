package com.jakala.menarini.core.service;

import java.nio.file.AccessDeniedException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import com.jakala.menarini.core.dto.*;
import com.jakala.menarini.core.entities.RegisteredUser;
import com.jakala.menarini.core.entities.RegisteredUserTopic;
import com.jakala.menarini.core.entities.records.RegisteredUserRoleRecord;
import com.jakala.menarini.core.entities.records.RegisteredUserTopicRecord;
import org.jooq.DSLContext;
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
    public boolean addUser(RegisteredUserDto user, Set<Acl> acls) throws AccessDeniedException {

        AclValidator.isAccessAllowed(AclRolePermissions.ADD_REGISTERED_USERS, acls);

        try (Connection connection = dataSource.getConnection()) {

            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            RegisteredUserRecord rur = create.newRecord(com.jakala.menarini.core.entities.RegisteredUser.REGISTERED_USER, user);
            rur.store();
            create.executeInsert(rur);

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
            e.printStackTrace();
            return false;
        }
    }

}
