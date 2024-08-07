package com.jakala.menarini.core.service;

import java.nio.file.AccessDeniedException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.entities.records.RegisteredUserRecord;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.security.AclRolePermissions;
import com.jakala.menarini.core.security.AclValidator;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;


@Component(service = UserRegisteredServiceInterface.class)
public class UserRegisteredService implements UserRegisteredServiceInterface {

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

}
