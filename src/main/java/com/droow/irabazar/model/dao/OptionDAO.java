package com.droow.irabazar.model.dao;

import com.droow.irabazar.model.entity.OptionsEntity;

import java.util.List;


/**
 * Created by droow on 8.11.15.
 */
public interface OptionDAO {
    public void addOption(OptionsEntity option);
    public List<OptionsEntity> listOption();
    public OptionsEntity getOptionById(Integer id);
    public OptionsEntity getOptionByName(String name);
    public void removeOption(Integer id);
    public void updateOption(OptionsEntity option);
}