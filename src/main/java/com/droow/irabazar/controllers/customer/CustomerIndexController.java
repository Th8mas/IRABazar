package com.droow.irabazar.controllers.customer;

import com.droow.irabazar.Main;
import com.droow.irabazar.model.entity.CustomersEntity;
import com.droow.irabazar.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by droow on 8.11.15.
 */
@Component
public class CustomerIndexController extends BaseCustomerController implements Initializable {

    @FXML
    private TableView<CustomersEntity> customersListTable;

    @FXML
    private TextField customerFilterInput;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert customersListTable != null : "fx:id=\"customersListTable\" was not injected: check your FXML file 'simple.fxml'.";
        this.refreshData();

    }

    private void populateTable() {
        Utils.populateCustomersTableView(
            this.customersListTable,
            this.customerList,
            (observableValue, oldValue, newValue) -> {
                if(newValue != null) {
                    this.sc.loadScreen(Main.screenCustomersDetail);
                    CustomerDetailController cont = (CustomerDetailController) this.sc.getCurrentController();
                    cont.setCurrentCustomer(newValue.getId());
                    cont.setBackLinkScreen(Main.screenCustomers);
                }
            },
            this.customerFilterInput.textProperty()
        );
    }

    @Override
    public void refreshData() {
        this.customerList = this.getCustomerList();
        populateTable();
    }

    public void clearSelection() {
        this.customersListTable.getSelectionModel().clearSelection();
    }

}
