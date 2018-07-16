package com.droow.irabazar.model.dao;

import com.droow.irabazar.model.entity.CategoriesEntity;

import java.util.List;


/**
 * Created by droow on 8.11.15.
 */
public interface CategoryDAO {
    public void addCategory(CategoriesEntity category);
    public List<CategoriesEntity> listCategory();
    public CategoriesEntity getCategoryById(Integer id);
    public void removeCategory(Integer id);
    public void updateCategory(CategoriesEntity category);
    public void merge(CategoriesEntity category);
    public CategoriesEntity getCategoryByName(String name);
}