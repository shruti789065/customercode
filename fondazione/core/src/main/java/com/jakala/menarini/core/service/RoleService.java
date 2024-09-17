package com.jakala.menarini.core.service;


import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.service.interfaces.RoleServiceInterface;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@Component(service = RoleServiceInterface.class)
public class RoleService implements RoleServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegisteredService.class);

    @Reference(target = "(datasource.name=fondazione-mysql)")
    private DataSource dataSource;


    @Override
    public List<RoleDto> getRoles() {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.MYSQL);
            List<RoleDto> roles = create.select()
                    .from(RoleDto.table)
                    .fetch().into(RoleDto.class);
            return roles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
