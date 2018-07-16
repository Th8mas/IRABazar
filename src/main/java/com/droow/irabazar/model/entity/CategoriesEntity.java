package com.droow.irabazar.model.entity;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Created by droow on 8.11.15.
 */
@Entity
@Table(name = "categories")
public class CategoriesEntity extends BaseEntity {
    private int id;
    private String name;
    private CategoriesEntity parent;
    private Set<CategoriesEntity> childs;
    private Set<ProductsEntity> products;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "id", unique = true)
    public int getId() {
        return id;
    }

    public CategoriesEntity setId(int id) {
        this.id = id;
        return this;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public CategoriesEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoriesEntity that = (CategoriesEntity) o;

        if (id != that.id) return false;
        if (parent != that.parent) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @ManyToOne
    @JoinColumn(name="parent_id")
    public CategoriesEntity getParent() {
        return parent;
    }

    public CategoriesEntity setParent(CategoriesEntity parent) {
        this.parent = parent;
        return this;
    }

    @OneToMany(mappedBy="parent", fetch = FetchType.EAGER, cascade={CascadeType.ALL})
    public Set<CategoriesEntity> getChilds() {
        return childs;
    }

    public CategoriesEntity setChilds(Set<CategoriesEntity> childs) {
        this.childs = childs;
        return this;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy = "categories", fetch = FetchType.EAGER)
    public Set<ProductsEntity> getProducts() {
        return this.products;
    }

    public CategoriesEntity setProducts(Set<ProductsEntity> products) {
        this.products = products;
        return this;
    }

}
