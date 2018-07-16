package com.droow.irabazar.model.dao;

import com.droow.irabazar.model.entity.CustomersEntity;

import java.util.List;


/**
 * Created by droow on 8.11.15.
 */
public interface CustomerDAO {
    public void addCustomer(CustomersEntity customer);
    public List<CustomersEntity> listCustomer();
    public CustomersEntity getCustomerById(Integer id);
    public void removeCustomer(Integer id);
    public void updateCustomer(CustomersEntity customer);
    public String getCustomerCode();
}