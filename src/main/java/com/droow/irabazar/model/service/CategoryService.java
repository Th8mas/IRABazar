package com.droow.irabazar.model.service;

import com.droow.irabazar.model.entity.CategoriesEntity;

import java.util.List;

public interface CategoryService {
    public void addCategory(CategoriesEntity category);

    public List<CategoriesEntity> listCategories();

    public CategoriesEntity getCategoryById(Integer id);

    public CategoriesEntity getCategoryByName(String name);

    public void removeCategory(Integer id);

    public void updateCategory(CategoriesEntity category);

    public void merge(CategoriesEntity category);
}
