package com.jakala.menarini.core.service;

import com.day.commons.datasource.poolservice.DataSourcePool;  // Add this import
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.entities.RegisteredUserRole;
import com.jakala.menarini.core.entities.Role;
import com.jakala.menarini.core.service.interfaces.RoleServiceInterface;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component(service = RoleServiceInterface.class)
public class RoleService implements RoleServiceInterface {

    private static final String DATA_SOURCE_NAME = "fondazione-mysql";

    @Reference
    private DataSourcePool dataSourcePool;  // Change this

    @Override
    public List<RoleDto> getRoles() {
        try {
            DataSource dataSource = (DataSource) dataSourcePool.getDataSource(DATA_SOURCE_NAME);
            try (Connection connection = dataSource.getConnection()) {
                DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
                return create.select()
                        .from(RoleDto.table)
                        .fetch().into(RoleDto.class);
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<RoleDto> getRolesUser(long id) {
        try {
            DataSource dataSource = (DataSource) dataSourcePool.getDataSource(DATA_SOURCE_NAME);
            try (Connection connection = dataSource.getConnection()) {
                DSLContext context = DSL.using(connection, SQLDialect.MYSQL);
                SelectConditionStep<Record> roleQuery = context
                        .select().from(Role.ROLE)
                        .where(Role.ROLE.ID.eq(
                                context.select(RegisteredUserRole.REGISTERED_USER_ROLE.ROLE_ID)
                                        .from(RegisteredUserRole.REGISTERED_USER_ROLE)
                                        .where(RegisteredUserRole.REGISTERED_USER_ROLE.REGISTERED_USER_ID.eq(id))
                        ));
                return roleQuery.fetch().into(RoleDto.class);
            }
        } catch (Exception e) {
            return null;
        }
    }
}