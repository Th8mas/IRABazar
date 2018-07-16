/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.droow.irabazar.controllers;

import com.droow.irabazar.model.entity.OptionsEntity;
import com.droow.irabazar.model.service.OptionService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 *
 * @author Mehmet Sunkur <mehmetsunkur@gmail.com>
 */

public abstract class BaseScreenController implements Initializable, BeanNameAware{

    @Autowired
    protected ScreensController sc;

    @Autowired
    protected OptionService optionService;

    protected String screenId;

    protected Parent root;

    protected String backLinkScreen;

    public static HashMap<String, String> optionsList = new HashMap<>();

    @FXML
    protected Label screenName;

    protected static HashMap<String, String> defaultOptions;
    static
    {
        defaultOptions = new HashMap<String, String>();

        defaultOptions.put("storageMinimumFeeInCrowns", "10"); // Minimalni poplatek za uložení
        defaultOptions.put("storageFeeInPercent", "3"); // Poplatek za uložení
        defaultOptions.put("defaultDaysToKeep", "30"); // Smlouva na počet dní
        defaultOptions.put("missingContractFeeInCrowns", "50"); // Ztráta smlouvy
        defaultOptions.put("notSoldDailyFeeInPercent", "5"); // Neprodáno - po uplynutí lhůty penále za uskladnění, stejné jako poplatek za uložení?
        defaultOptions.put("notSoldDaysToPayFee", "7");
        defaultOptions.put("lowerPriceInPercent", "10"); // Procenta snížené ceny
        defaultOptions.put("lowerPriceDaysLimit", "10"); // Lhůta pro vyzvednutí - TODO: Better name
        defaultOptions.put("profitInPercent", "26"); // Provize z prodeje
        defaultOptions.put("withdrawalFeeInPercent", "15"); // Poplatek při odstoupení
        // TODO: minimalni cena

        defaultOptions.put("comName", "IRA Josef Sobota");
        defaultOptions.put("comIco", "47710233");
        defaultOptions.put("comDico", "neplátce DPH");
        defaultOptions.put("comAddress", "337 01 Rokycany Jiráskova ul. 214/1");
        defaultOptions.put("comPhone", "371 724 536");
        defaultOptions.put("comEmail", "");
        defaultOptions.put("comWebsite", "www.ira-bazar.cz");

    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }
    @Override
    public void setBeanName(String name) {
        this.screenId = name;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    /**
     * Checks default options and creates them if not exists
     */
    public void checkOptions() {
        // TODO: Refactor
        BaseScreenController.optionsList = this.optionService.mapOption();
        defaultOptions.entrySet().stream().filter(option -> !BaseScreenController.optionsList.containsKey(option.getKey())).forEach(option -> {
            OptionsEntity opt = new OptionsEntity();
            opt.setName(option.getKey());
            opt.setValue(option.getValue());
            this.optionService.addOption(opt);
        });
        BaseScreenController.optionsList = this.optionService.mapOption();
    }

    public String getBackLinkScreen() {
        return backLinkScreen;
    }

    public void setBackLinkScreen(String backLinkScreen) {
        this.backLinkScreen = backLinkScreen;
    }

    public abstract void refreshData();

    //public abstract void returnBack();

    public void screenLoadCallback() {
    }
}
