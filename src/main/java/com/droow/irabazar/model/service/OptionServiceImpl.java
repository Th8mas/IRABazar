package com.droow.irabazar.model.service;

import com.droow.irabazar.model.dao.OptionDAO;
import com.droow.irabazar.model.entity.OptionsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
public class OptionServiceImpl implements OptionService {
    @Autowired
    private OptionDAO optionDAO;

    @Override
    public void addOption(OptionsEntity option) {
        optionDAO.addOption(option);
    }

    @Override
    @Transactional
    public List<OptionsEntity> listOption() {
        return optionDAO.listOption();
    }

    @Override
    public HashMap<String, String> mapOption() {
        List<OptionsEntity> optionsList = this.listOption();
        HashMap<String, String> optionsMap = new HashMap<>();

        for (OptionsEntity option : optionsList) {
            optionsMap.put(option.getName(), option.getValue());
        }
        return optionsMap;
    }


    @Override
    public OptionsEntity getOptionById(Integer id) { return optionDAO.getOptionById(id); }

    @Override
    public OptionsEntity getOptionByName(String name) { return optionDAO.getOptionByName(name); }

    @Override
    public void removeOption(Integer id) {
        optionDAO.removeOption(id);
    }

    @Override
    public void updateOption(OptionsEntity option) {
        optionDAO.updateOption(option);
    }
}