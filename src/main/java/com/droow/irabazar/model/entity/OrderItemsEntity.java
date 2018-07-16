package com.droow.irabazar.model.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

/**
 * Created by droow on 8.11.15.
 */
@Entity
@Table(name = "order_items")
public class OrderItemsEntity {
    private int id;
    private OrdersEntity order;
    private ProductsEntity product;

    private int count;
    private Double price;
    private Double totalPrice;

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public OrderItemsEntity setId(int id) {
        this.id = id;
        return this;
    }

    @Basic
    @Column(name = "count")
    public int getCount() {
        return count;
    }

    public OrderItemsEntity setCount(int count) {
        this.count = count;
        return this;
    }

    @Basic
    @Column(name = "price")
    public Double getPrice() {
        return price;
    }

    public OrderItemsEntity setPrice(Double price) {
        this.price = price;
        return this;
    }

    @Basic
    @Column(name = "total_price")
    public Double getTotalPrice() {
        return totalPrice;
    }

    public OrderItemsEntity setTotalPrice(Double price) {
        this.totalPrice = price;
        return this;
    }

    @Fetch(FetchMode.SELECT)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    public ProductsEntity getProduct() {
        return product;
    }

    public OrderItemsEntity setProduct(ProductsEntity product) {
        this.product = product;
        return this;
    }

    @Fetch(FetchMode.SELECT)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    public OrdersEntity getOrder() {
        return order;
    }

    public OrderItemsEntity setOrder(OrdersEntity order) {
        this.order = order;
        return this;
    }
}
