package com.droow.irabazar.model.dao;

import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.model.entity.OrdersEntity;

import java.util.List;


/**
 * Created by droow on 8.11.15.
 */
public interface OrderDAO {
    public void addOrder(OrdersEntity order);
    public List<OrdersEntity> listOrder();
    public OrdersEntity getOrderById(Integer id);
    public void removeOrder(Integer id);
    public void updateOrder(OrdersEntity order);
    public void addOrderItem(OrderItemsEntity orderItem);
    public String getOrderCode();
}