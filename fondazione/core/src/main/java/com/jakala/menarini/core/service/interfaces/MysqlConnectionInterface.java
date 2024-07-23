package com.jakala.menarini.core.service.interfaces;

import jakarta.persistence.EntityManagerFactory;

public interface MysqlConnectionInterface {
    public void activate();
    public void deactivate();
    public EntityManagerFactory getEntityManagerFactory();
}
