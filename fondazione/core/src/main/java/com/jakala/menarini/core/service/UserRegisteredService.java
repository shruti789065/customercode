package com.jakala.menarini.core.service;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.jakala.menarini.core.entities.RegisteredUser;
import com.jakala.menarini.core.service.interfaces.MysqlConnectionInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Component(service = UserRegisteredServiceInterface.class)
public class UserRegisteredService implements UserRegisteredServiceInterface {

    /*@PersistenceContext(unitName = "persistence-fondazione")
    private EntityManagerFactory entityManagerFactory;*/

    
    //private EntityManager em;
    
    /*@Reference
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }*/


    @Override
    public List<RegisteredUser> getUsers() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence-fondazione");
        EntityManager em = null;
        List<RegisteredUser> list = new ArrayList<>();
        RegisteredUser test = new RegisteredUser();
        if (entityManagerFactory == null) {
            test.setId(3);
            list.add(test);
            return list;
        } else {
            em = entityManagerFactory.createEntityManager();
            if (em == null) {
                test.setId(4);
                list.add(test);
                return list;
            }
        }
         TypedQuery<RegisteredUser> query = em.createQuery("SELECT u FROM REGISTERED_USER u", RegisteredUser.class);
         return  query.getResultList();
    }
    
}
