package com.jakala.menarini.core.service;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.jakala.menarini.core.service.interfaces.MysqlConnectionInterface;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;



//@Component(service = MysqlConnectionInterface.class, immediate = true)
public class MysqlConnection implements MysqlConnectionInterface  {
    private static final Logger log = LoggerFactory.getLogger(MysqlConnection.class);

    // The datasource.name value of the OSGi configuration containing the connection this OSGi component will use.
    private static final String DATA_PER_NAME = "persistence-fondazione";

    @PersistenceContext(unitName = DATA_PER_NAME)
    private EntityManagerFactory entityManagerFactory;

    
    public void activate() {
        entityManagerFactory = Persistence.createEntityManagerFactory(DATA_PER_NAME);
    }


    public void deactivate() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}