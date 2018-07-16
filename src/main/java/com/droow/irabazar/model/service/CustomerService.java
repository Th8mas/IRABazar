package com.droow.irabazar.model.service;

import com.droow.irabazar.model.entity.CustomersEntity;

import java.util.List;

public interface CustomerService {
    public void addCustomer(CustomersEntity customer);

    public List<CustomersEntity> listCustomer();

    public CustomersEntity getCustomerById(Integer id);

    public void removeCustomer(Integer id);

    public void updateCustomer(CustomersEntity customer);

    public String getCustomerCode();
}
