package com.droow.irabazar.controllers.product;

import com.droow.irabazar.model.entity.CategoriesEntity;
import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.model.entity.OrdersEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.model.service.*;
import com.droow.irabazar.controllers.BaseScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by droow on 8.11.15.
 */
public abstract class BaseProductController extends BaseScreenController {
    // Services
    @Autowired
    protected ProductService productService;
    @Autowired
    protected CustomerService customerService;
    @Autowired
    protected CategoryService categoryService;
    @Autowired
    protected OrderService orderService;
    
    // Lists
    protected ObservableList<ProductsEntity> productList = FXCollections.observableArrayList();
    protected ObservableList<CategoriesEntity> categoriesList = FXCollections.observableArrayList();

    protected ProductsEntity currentProduct = null;
    protected CategoriesEntity rootCategory = null;

    public static String rootCategoryName = "Vše";

    public void addProduct(ProductsEntity product) {
        productService.addProduct(product);
    }

    public ObservableList<ProductsEntity> getProductList() {
        if (!productList.isEmpty()) productList.clear();
        productList = FXCollections.observableList((List<ProductsEntity>) productService.listProduct());
        return productList;
    }
    
    public ObservableList<ProductsEntity> getAllProductsList() {
        if (!productList.isEmpty()) productList.clear();
        productList = FXCollections.observableList((List<ProductsEntity>) productService.listAllProducts());
        return productList;
    }

    public ProductsEntity getProductById(Integer id) {
        return productService.getProductById(id);
    }

    public void removeProduct(Integer id) {
        productService.removeProduct(id);
    }

    public void updateProduct(ProductsEntity product) {
        productService.updateProduct(product);
    }

    /**
     * Get categories list as observable list
     * @return Observable list of CategoriesEntity
     */
    public ObservableList<CategoriesEntity> getCategoriesList() {
        this.refreshCategoriesList();
        return categoriesList;
    }

    public void refreshCategoriesList() {
        if (!categoriesList.isEmpty()) categoriesList.clear();
        categoriesList = FXCollections.observableList(categoryService.listCategories());
    }
    
    public ObservableList<OrderItemsEntity> getProductOrders(){
    	List<OrderItemsEntity> orders = new ArrayList<>();
    	
    	if(currentProduct != null){
	    	for(OrdersEntity order : orderService.listOrders()){
	    		for(OrderItemsEntity orderItem : order.getOrderItems()){
	    			if(orderItem.getProduct().getCode().equals(currentProduct.getCode())){
	    				orders.add(orderItem);
	    				break;
	    			}
	    		}
	    	}
    	}
    	
    	return FXCollections.observableList(orders);
    }
}
