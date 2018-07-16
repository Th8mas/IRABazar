package com.droow.irabazar.controllers.customer;

import com.droow.irabazar.model.entity.CustomersEntity;
import com.droow.irabazar.model.service.CustomerService;
import com.droow.irabazar.controllers.BaseScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Droow
 */
abstract public class BaseCustomerController extends BaseScreenController {

    @Autowired
    protected CustomerService customerService;
    protected ObservableList<CustomersEntity> customerList = FXCollections.observableArrayList();
    protected CustomersEntity currentCustomer = null;

    public void addCustomer(CustomersEntity customer) {
        customerService.addCustomer(customer);
    }

    public ObservableList<CustomersEntity> getCustomerList() {
        if (!customerList.isEmpty()) customerList.clear();
        customerList = FXCollections.observableList((List<CustomersEntity>) customerService.listCustomer());
        return customerList;
    }

    public CustomersEntity getCustomerById(Integer id) {
        return customerService.getCustomerById(id);
    }

    public void removeCustomer(Integer id) {
        customerService.removeCustomer(id);
    }

    public void updateCustomer(CustomersEntity customer) {
        customerService.updateCustomer(customer);
    }

}