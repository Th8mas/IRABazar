package com.droow.irabazar.model.dao;

import com.droow.irabazar.model.entity.ProductsEntity;

import java.util.List;


/**
 * Created by droow on 8.11.15.
 */
public interface ProductDAO {
    public void addProduct(ProductsEntity product);
    public List<ProductsEntity> listProduct();
    public List<ProductsEntity> listAllProducts();
    public ProductsEntity getProductById(Integer id);
    public void removeProduct(Integer id);
    public void updateProduct(ProductsEntity product);
    public String getProductCode(Integer toAdd);
    public void merge(ProductsEntity product);
	
}