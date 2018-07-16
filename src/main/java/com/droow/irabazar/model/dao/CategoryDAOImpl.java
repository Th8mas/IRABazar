package com.droow.irabazar.model.dao;

/**
 * Created by droow on 8.11.15.
 */

import com.droow.irabazar.model.entity.CategoriesEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryDAOImpl extends AbstractDAO< CategoriesEntity > implements CategoryDAO {

    CategoryDAOImpl() {
        this.setClazz(CategoriesEntity.class);
    }

    public void addCategory(CategoriesEntity category) {
        this.create(category);
    }

    @Transactional
    public List<CategoriesEntity> listCategory() {
        //Session s = getCurrentSession();
        List<CategoriesEntity> list = entityManager.createQuery(
            "SELECT DISTINCT c FROM CategoriesEntity c", CategoriesEntity.class
        ).getResultList();
                //.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                //.list();
        return list;
    }

    public CategoriesEntity getCategoryById(Integer id) {
        return this.findOne(id);
    }

    public CategoriesEntity getCategoryByName(String name) {
        return entityManager.createQuery(
                "SELECT DISTINCT c FROM CategoriesEntity c WHERE c.name = :name", CategoriesEntity.class
        ).setParameter("name", name).getSingleResult();
    }

    public void removeCategory(Integer id) {
        this.deleteById(id);
    }

    public void updateCategory(CategoriesEntity category) {
        this.update(category);
    }
}
