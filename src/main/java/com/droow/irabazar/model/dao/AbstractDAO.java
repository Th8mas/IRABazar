package com.droow.irabazar.model.dao;

import org.hibernate.PersistentObjectException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by droow on 29.12.16.
 */

@Transactional
public abstract class AbstractDAO<T extends Serializable> {

    private Class< T > clazz;

    @PersistenceContext
    EntityManager entityManager;

    public final void setClazz( Class< T > clazzToSet ){
        this.clazz = clazzToSet;
    }

    public T findOne( Integer id ){
        return entityManager.find( clazz, id );
    }
    public List< T > findAll(){
        return entityManager.createQuery("from " + clazz.getName()).getResultList();
    }

    public void create( T entity ){
        entityManager.persist(entity);
    }

    public void merge ( T entity ) {
        entityManager.merge(entity);
        entityManager.flush();
    }

    public T update( T entity ){
        return entityManager.merge( entity );
    }

    public void delete( T entity ){
        entityManager.remove( entity );
    }
    public void deleteById( Integer entityId ){
        T entity = findOne( entityId );
        delete( entity );
    }
}