package com.droow.irabazar.controllers.order;

import com.droow.irabazar.controllers.product.BaseProductController;
import com.droow.irabazar.model.entity.OrdersEntity;
import com.droow.irabazar.model.service.OrderService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by droow on 30.8.16.
 */
public abstract class BaseOrderController extends BaseProductController {
    protected OrdersEntity currentOrder;

    protected ObservableList<OrdersEntity> ordersList = FXCollections.observableArrayList();

    @Autowired
    protected OrderService orderService;

    public ObservableList<OrdersEntity> getOrdersList() {
        if (!ordersList.isEmpty()) ordersList.clear();
        ordersList = FXCollections.observableList(orderService.listOrders());
        return ordersList;
    }

    protected OrdersEntity getOrderById(Integer id) {
        return orderService.getOrderById(id);
    }
}
