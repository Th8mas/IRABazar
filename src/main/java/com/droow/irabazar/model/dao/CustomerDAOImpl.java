package com.droow.irabazar.model.dao;

/**
 * Created by droow on 8.11.15.
 */

import com.droow.irabazar.model.entity.CustomersEntity;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository
public class CustomerDAOImpl extends AbstractDAO < CustomersEntity > implements CustomerDAO {

    CustomerDAOImpl() {
        this.setClazz(CustomersEntity.class);
    }

    @Transactional
    public List<CustomersEntity> listCustomer() {
        List<CustomersEntity> list = entityManager.createQuery(
            "SELECT c FROM CustomersEntity c", CustomersEntity.class
        ).getResultList();
        return list;
    }

    public CustomersEntity getCustomerById(Integer id) {
        return this.findOne(id);
    }

    public void removeCustomer(Integer id) {
        this.deleteById(id);
    }

    public void updateCustomer(CustomersEntity customer) {
        this.update(customer);
    }

    public void addCustomer(CustomersEntity customer) {
        this.create(customer);
    }

    @Transactional
    public String getCustomerCode() {
        Calendar cal = Calendar.getInstance();
        int lastTwoDigits = cal.get(Calendar.YEAR) % 100;
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Timestamp start = new Timestamp(cal.getTimeInMillis());

        int count = entityManager.createNamedQuery("findCustomersAfterDate", Long.class)
                .setParameter("created", start)
                .getSingleResult().intValue();

        return lastTwoDigits+String.format("%03d", count+1);
    }
}
