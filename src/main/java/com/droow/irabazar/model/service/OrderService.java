package com.droow.irabazar.model.service;

import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.model.entity.OrdersEntity;

import java.util.List;

public interface OrderService {
    public void addOrder(OrdersEntity order);

    public void addOrderItem(OrderItemsEntity orderItem);

    public List<OrdersEntity> listOrders();

    public OrdersEntity getOrderById(Integer id);

    public void removeOrder(Integer id);

    public void updateOrder(OrdersEntity order);

    public String getOrderCode();
}
