package com.droow.irabazar.model.dao;

import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.model.entity.OrdersEntity;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository
public class OrderDAOImpl extends AbstractDAO<OrdersEntity> implements OrderDAO {

    OrderDAOImpl() {
        this.setClazz(OrdersEntity.class);
    }

    public void addOrder(OrdersEntity order) {
    	this.create(order);
    }

    @Transactional
    public List<OrdersEntity> listOrder() {
        List<OrdersEntity> list = entityManager.createQuery(
                "SELECT o FROM OrdersEntity o", OrdersEntity.class).getResultList();
        return list;
    }

    public OrdersEntity getOrderById(Integer id) {
        return this.findOne(id);
    }

    public void removeOrder(Integer id) {
        this.deleteById(id);
    }

    public void updateOrder(OrdersEntity order) {
        this.update(order);
    }

    public void addOrderItem(OrderItemsEntity orderItem) {
        entityManager.merge( orderItem );
    }

    @Transactional
    public String getOrderCode() {
        Calendar cal = Calendar.getInstance();
        int lastTwoDigits = cal.get(Calendar.YEAR) % 100;
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Timestamp start = new Timestamp(cal.getTimeInMillis());

        int count = entityManager.createNamedQuery("findOrdersAfterDate", Long.class)
                .setParameter("created", start)
                .getSingleResult().intValue();

        return lastTwoDigits+String.format("%03d", count+1);
    }
}
