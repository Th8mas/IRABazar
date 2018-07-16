/**
 * 
 */
package com.droow.irabazar.utils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.model.service.ProductService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

/**
 * @author Tomas Brynda
 *
 */
public class StockingContentGenerator {

	public static String getContent(ProductService productService){
		STGroup g = new STGroupDir("assets/templates");
        ST template = g.getInstanceOf("stocking");

        ObservableList<ProductsEntity> productList = FXCollections.observableArrayList(productService.listAllProducts());
        FilteredList<ProductsEntity> filteredData = new FilteredList<>(productList, product -> product.getCount() > 0);
        SortedList<ProductsEntity> sortedList = filteredData.sorted((ProductsEntity p1, ProductsEntity p2) -> p1.getCode().compareTo(p2.getCode()));

        template.add("cur_date", Utils.formatDate(new Date(), "dd.MM.yyyy"));
        template.add("product_list", getProductList(sortedList));
        
        return template.render();
	}
	
	private static String getProductList(SortedList<ProductsEntity> sortedList){
		String content = "";
		
	    for (ProductsEntity product : sortedList) {
	    		
	        content += String.format("%-10s %-25s    %-25s    %d",
	            product.getCode(),
	            limitString(product.getName().replace("\n", " "), 25),
	            limitString(product.getDescription().replace("\n", " "), 25),
	            product.getCount()
	        );
	        
	        content += "\n";
	    }
	    
	    return content.trim();
	}
	
	private static String limitString(String input, int chars){
		return input.substring(0, Math.min(input.length(), chars));
	}
	
}
