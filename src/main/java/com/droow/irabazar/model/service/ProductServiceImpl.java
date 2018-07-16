package com.droow.irabazar.model.service;

import com.droow.irabazar.model.dao.ProductDAO;
import com.droow.irabazar.model.entity.ProductsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
	
    @Autowired
    private ProductDAO productDAO;

    @Override
    public void addProduct(ProductsEntity product) {
        productDAO.addProduct(product);
    }

    public void merge(ProductsEntity product) {
        productDAO.merge(product);
    }

    @Override
    public List<ProductsEntity> listProduct() {
        return productDAO.listProduct();
    }
    
	@Override
	public List<ProductsEntity> listAllProducts() {
		return productDAO.listAllProducts();
	}

    @Override
    public ProductsEntity getProductById(Integer id) { return productDAO.getProductById(id); }

    @Override
    public void removeProduct(Integer id) {
        productDAO.removeProduct(id);
    }

    @Override
    public void updateProduct(ProductsEntity product) {
        productDAO.updateProduct(product);
    }

    @Override
    public String getProductCode(Integer toAdd) {
        return productDAO.getProductCode(toAdd);
    }

}