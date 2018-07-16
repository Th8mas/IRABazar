package com.droow.irabazar.controllers.contract;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.product.ProductDetailController;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class ContractIndexController extends BaseContractController implements Initializable {

    @FXML
    private TableView<ContractsEntity> contractsListTableView;

    @FXML
    private TextField contractFilterInput;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.refreshData();
    }

    private void populateTable() {
        Utils.populateContractsTableView(
            this.contractsListTableView,
            this.contractsList,
            (observableValue, oldValue, newValue) -> {
                //Check whether item is selected and set value of selected item to Label
                if (contractsListTableView.getSelectionModel().getSelectedItem() != null) {
                    this.sc.loadScreen(Main.screenContractDetail);
                    ContractDetailController controller = (ContractDetailController)this.sc.getCurrentController();
                    controller.setBackLinkScreen(Main.screenContracts);
                    controller.setCurrentContract((newValue).getId());
                }
            },
            this.contractFilterInput.textProperty()
        );
    }

    @Override
    public void refreshData() {
        this.contractsList = this.getContractsList();
        this.populateTable();
    }
}
