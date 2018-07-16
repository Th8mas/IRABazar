package com.droow.irabazar.model.service;

import com.droow.irabazar.model.dao.CategoryDAO;
import com.droow.irabazar.model.entity.CategoriesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    public void addCategory(CategoriesEntity category) {
        categoryDAO.addCategory(category);
    }

    @Override
    public List<CategoriesEntity> listCategories() {
        return categoryDAO.listCategory();
    }

    @Override
    public CategoriesEntity getCategoryById(Integer id) { return categoryDAO.getCategoryById(id); }

    public CategoriesEntity getCategoryByName(String name) {
        return categoryDAO.getCategoryByName(name);
    }

    @Override
    public void removeCategory(Integer id) {
        categoryDAO.removeCategory(id);
    }

    @Override
    public void updateCategory(CategoriesEntity category) {
        categoryDAO.updateCategory(category);
    }

    public void merge(CategoriesEntity category) {
        categoryDAO.merge(category);
    }
}