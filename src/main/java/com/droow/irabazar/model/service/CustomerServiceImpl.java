package com.droow.irabazar.model.service;

import com.droow.irabazar.model.dao.CustomerDAO;
import com.droow.irabazar.model.entity.CustomersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerDAO customerDAO;

    @Override
    public void addCustomer(CustomersEntity customer) {
        customerDAO.addCustomer(customer);
    }

    @Override
    public List<CustomersEntity> listCustomer() {
        return customerDAO.listCustomer();
    }

    @Override
    public CustomersEntity getCustomerById(Integer id) { return customerDAO.getCustomerById(id); }

    @Override
    public void removeCustomer(Integer id) {
        customerDAO.removeCustomer(id);
    }

    @Override
    public void updateCustomer(CustomersEntity customer) {
        customerDAO.updateCustomer(customer);
    }

    @Override
    public String getCustomerCode() {
        return customerDAO.getCustomerCode();
    }
}