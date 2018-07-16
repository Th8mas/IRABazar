package com.droow.irabazar.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Base DAO
 *
 * Created by droow on 17.7.16.
 */
public class BaseDAO {
    //@Autowired
    SessionFactory sessionFactory;

    @PersistenceContext
    protected EntityManager entityManager;

    protected Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }

    /**
     * Adds new entity instance to database
     * @param item
     */
    protected void addItem(Object item) {
        Session s = getCurrentSession();
        s.beginTransaction();
        s.save(item);
        s.getTransaction().commit();
    }

    /**
     * Updates existing entity instance in database
     * @param item
     */
    protected void updateItem(Object item) {
        Session s = getCurrentSession();
        s.beginTransaction();
        s.update(item);
        s.getTransaction().commit();
    }

    protected boolean removeItem(Class className, Integer id) {
        Session s = getCurrentSession();
        Object item = s.load(className, id);
        if(item != null) {
            s.beginTransaction();
            s.delete(item);
            s.getTransaction().commit();
            return true;
        }
        return false;
    }

    protected Object getItemById(Class className, Integer id) {
        Session s = getCurrentSession();
        s.beginTransaction();
        Object item = s.get(className, id);
        s.getTransaction().commit();
        return item;
    }
}
