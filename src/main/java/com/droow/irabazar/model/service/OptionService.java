package com.droow.irabazar.model.service;

import com.droow.irabazar.model.entity.OptionsEntity;

import java.util.HashMap;
import java.util.List;

public interface OptionService {
    public void addOption(OptionsEntity option);

    public List<OptionsEntity> listOption();

    public HashMap<String, String> mapOption();

    public OptionsEntity getOptionById(Integer id);

    public OptionsEntity getOptionByName(String name);

    public void removeOption(Integer id);

    public void updateOption(OptionsEntity option);
}
