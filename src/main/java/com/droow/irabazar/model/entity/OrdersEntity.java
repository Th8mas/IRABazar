package com.droow.irabazar.model.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by droow on 8.11.15.
 */
@NamedQueries({
    @NamedQuery(
        name = "findOrdersAfterDate",
        query = "SELECT COUNT(o.id) FROM OrdersEntity o WHERE o.created >= :created"
    )
})
@Entity
@Table(name = "orders")
public class OrdersEntity extends BaseEntity {
    private int id;
    private String code;
    private Double totalPrice;
    private Set<OrderItemsEntity> orderItems = new HashSet<OrderItemsEntity>();
    private Status status;

    public enum Status { NEW, PAID, RETURNED } // TODO

    public OrdersEntity() {
        this.status = Status.NEW;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public OrdersEntity setId(int id) {
        this.id = id;
        return this;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public OrdersEntity setCode(String code) {
        this.code = code;
        return this;
    }

    @Basic
    @Column(name = "totalprice")
    public Double getTotalPrice() {
        return totalPrice;
    }

    public OrdersEntity setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER)
    public Set<OrderItemsEntity> getOrderItems() {
        return orderItems;
    }

    public OrdersEntity setOrderItems(Set<OrderItemsEntity> orderItems) {
        this.orderItems = orderItems;
        return this;
    }

    @Basic
    @Column(name = "status")
    public Status getStatus() {
        return status;
    }

    public OrdersEntity setStatus(Status status) {
        this.status = status;
        return this;
    }
    
    public OrderItemsEntity addProduct(ProductsEntity product, Integer count) {
        OrderItemsEntity oi = null;
        for(OrderItemsEntity orderItem : orderItems) {
            if(orderItem.getProduct() == product) {
                oi = orderItem;
            }
        }
        if(oi == null) {
            oi = new OrderItemsEntity();
            oi.setCount(count)
                .setOrder(this)
                .setProduct(product)
                .setPrice(product.getCurrentSalePrice() / count)
                .setTotalPrice(product.getCurrentSalePrice());

            this.orderItems.add(oi);
        } else {
            oi.setCount(oi.getCount()+count)
                .setPrice(oi.getPrice() + (product.getCurrentSalePrice() / count))
                .setTotalPrice(oi.getTotalPrice() + product.getCurrentSalePrice());
        }
        return oi;
    }
    
    public void removeProduct(ProductsEntity product) {
        Iterator<OrderItemsEntity> iterator = orderItems.iterator();
        
        while(iterator.hasNext()){
        	OrderItemsEntity orderItem = iterator.next();
        	if(orderItem.getProduct() == product) {
                orderItems.remove(orderItem);
                return;
            }
        }
    }
    
    
}
