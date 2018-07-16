package com.droow.irabazar.model.service;

import com.droow.irabazar.model.dao.OrderDAO;
import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.model.entity.OrdersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDAO orderDAO;

    @Override
    public void addOrder(OrdersEntity order) {
        orderDAO.addOrder(order);
    }

    @Override
    public List<OrdersEntity> listOrders() {
        return orderDAO.listOrder();
    }

    @Override
    public OrdersEntity getOrderById(Integer id) { return orderDAO.getOrderById(id); }

    @Override
    public void removeOrder(Integer id) {
        orderDAO.removeOrder(id);
    }

    @Override
    public void updateOrder(OrdersEntity order) {
        orderDAO.updateOrder(order);
    }

    @Override
    public void addOrderItem(OrderItemsEntity orderItem) {
        orderDAO.addOrderItem(orderItem);
    }

    @Override
    public String getOrderCode() { return orderDAO.getOrderCode(); }
}