package com.droow.irabazar.model.dao;

/**
 * Created by droow on 8.11.15.
 */

import com.droow.irabazar.model.entity.ProductsEntity;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository
public class ProductDAOImpl extends AbstractDAO< ProductsEntity > implements ProductDAO {

    ProductDAOImpl() {
        this.setClazz(ProductsEntity.class);
    }
    public void addProduct(ProductsEntity product) {
        this.create(product);
    }

    @Transactional
    public List<ProductsEntity> listProduct() {
        List<ProductsEntity> list = entityManager.createQuery(
                "SELECT p FROM ProductsEntity p WHERE p.count <> 0 AND p.status <> :status"
        ).setParameter("status", ProductsEntity.Status.RENEWED).getResultList();
                //.setFetchMode("categories", FetchMode.SELECT)
                //.add(Restrictions.ne("count", 0))
                //.list();

        return list;
    }

    @Transactional
    public List<ProductsEntity> listAllProducts() {
        List<ProductsEntity> list = entityManager.createQuery(
                "SELECT p FROM ProductsEntity p"
        ).getResultList();

        return list;
    }
    
    public ProductsEntity getProductById(Integer id) {
        return this.findOne(id);
    }

    @Transactional
    public String getProductCode(Integer toAdd) {
        Calendar cal = Calendar.getInstance();
        int lastTwoDigits = cal.get(Calendar.YEAR) % 100;
        int month = cal.get(Calendar.MONTH) + 1;

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Timestamp start = new Timestamp(cal.getTimeInMillis());
        int count = entityManager.createNamedQuery("findProductsAfterDate", Long.class)
                .setParameter("created", start)
                .getSingleResult().intValue();

        return lastTwoDigits + String.format("%02d", month) + String.format("%03d", count + toAdd);
    }

    public void removeProduct(Integer id) {
        this.deleteById(id);
    }

    public void updateProduct(ProductsEntity product) {
        this.update(product);
    }
}
