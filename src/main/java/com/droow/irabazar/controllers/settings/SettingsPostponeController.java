package com.droow.irabazar.controllers.settings;

import com.droow.irabazar.model.service.ContractService;
import com.droow.irabazar.controllers.BaseScreenController;
import com.droow.irabazar.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class SettingsPostponeController extends BaseScreenController implements Initializable {

    @FXML
    private TextField daysInput;

    @FXML
    private Button saveButton;

    @Autowired
    private ContractService contractService;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.refreshData();
        this.daysInput.textProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (!newValue.equals("0")) {
                    saveButton.setDisable(false);
                } else {
                    saveButton.setDisable(true);
                }
            }
        );

    }

    @FXML
    private void onSaveButtonAction(ActionEvent event) {
        Integer days = Integer.parseInt(this.daysInput.getText());
        if(days > 0 && Utils.showConfirmationDialog(
                "Potvrďte akci",
                "Opravdu chcete posunout termíny všech smluv?",
                "Termíny budou posunuty o " + days + " " + (days == 1 ? "den" : (days < 5 ? "dny" : "dní")) + "!"
        )) {
            contractService.postponeContracts(days);
            Utils.showInfoNotification("Termíny prodlouženy.");
        }
    }

    private void fillForm() {
        this.daysInput.setText("0");

        this.saveButton.setDisable(true);
    }

    @Override
    public void refreshData() {
        this.fillForm();
    }


}
