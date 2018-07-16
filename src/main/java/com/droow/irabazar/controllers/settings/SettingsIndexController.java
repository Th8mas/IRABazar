package com.droow.irabazar.controllers.settings;

import com.droow.irabazar.controllers.BaseScreenController;
import com.droow.irabazar.utils.Utils;
import com.droow.irabazar.model.entity.OptionsEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class SettingsIndexController extends BaseScreenController implements Initializable {

    @FXML
    private TextField storageMinimumFeeInCrownsInput;
    @FXML
    private TextField storageFeeInPercentInput;
    @FXML
    private TextField defaultDaysToKeepInput;
    @FXML
    private TextField missingContractFeeInCrownsInput;
    @FXML
    private TextField notSoldDailyFeeInPercentInput;
    @FXML
    private TextField notSoldDaysToPayFeeInput;
    @FXML
    private TextField lowerPriceDaysLimitInput;
    @FXML
    private TextField profitInPercentInput;
    @FXML
    private TextField withdrawalFeeInPercentInput;
    @FXML
    private TextField renewDiscountInPercentInput;
    @FXML
    private Button saveButton;
    
    enum ValueType {
    	NUMBER, BOOLEAN
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.refreshData();
        /*
        storageMinimumFeeInCrownsInput.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(false);
        });
        */
    }

    @FXML
    private void onSaveButtonAction(ActionEvent event) {
        try {
	        saveOption("storageMinimumFeeInCrowns", this.storageMinimumFeeInCrownsInput.getText());
	        saveOption("storageFeeInPercent", this.storageFeeInPercentInput.getText());
	        saveOption("defaultDaysToKeep", this.defaultDaysToKeepInput.getText());
	        saveOption("missingContractFeeInCrowns", this.missingContractFeeInCrownsInput.getText());
	        saveOption("notSoldDailyFeeInPercent", this.notSoldDailyFeeInPercentInput.getText());
	        saveOption("notSoldDaysToPayFee", this.notSoldDaysToPayFeeInput.getText());
	        saveOption("lowerPriceDaysLimit", this.lowerPriceDaysLimitInput.getText());
	        saveOption("profitInPercent", this.profitInPercentInput.getText());
	        saveOption("withdrawalFeeInPercent", this.withdrawalFeeInPercentInput.getText());
	        saveOption("renewDiscountInPercent", this.renewDiscountInPercentInput.getText());
	
	        Utils.showInfoNotification("Nastavení uloženo!");
	        optionsList = this.optionService.mapOption();
	        //this.saveButton.setDisable(true); 
		} catch (Exception e){
			Utils.showErrorDialog("Chyba", e.getMessage(), null);
		}
    }
    
    private void saveOption(String optionName, String value) throws Exception{
    	String oldValue = optionsList.get(optionName);
    	if(oldValue == null){
    		OptionsEntity option = new OptionsEntity();
    		option.setName(optionName);
    		option.setDoubleValue(value);
    		this.optionService.addOption(option);
    	}
    	else {
    		OptionsEntity option = this.optionService.getOptionByName(optionName);
    		option.setDoubleValue(value, oldValue);
    		this.optionService.updateOption(option);
    	}
    }

    private void fillForm() {
        this.storageMinimumFeeInCrownsInput.setText(optionsList.get("storageMinimumFeeInCrowns"));
        this.storageFeeInPercentInput.setText(optionsList.get("storageFeeInPercent"));
        this.defaultDaysToKeepInput.setText(optionsList.get("defaultDaysToKeep"));
        this.missingContractFeeInCrownsInput.setText(optionsList.get("missingContractFeeInCrowns"));
        this.notSoldDailyFeeInPercentInput.setText(optionsList.get("notSoldDailyFeeInPercent"));
        this.notSoldDaysToPayFeeInput.setText(optionsList.get("notSoldDaysToPayFee"));
        this.lowerPriceDaysLimitInput.setText(optionsList.get("lowerPriceDaysLimit"));
        this.profitInPercentInput.setText(optionsList.get("profitInPercent"));
        this.withdrawalFeeInPercentInput.setText(optionsList.get("withdrawalFeeInPercent"));
        this.renewDiscountInPercentInput.setText(optionsList.get("renewDiscountInPercent"));

        this.saveButton.setDisable(false);
    }

    @Override
    public void refreshData() {
        this.fillForm();
    }


}
