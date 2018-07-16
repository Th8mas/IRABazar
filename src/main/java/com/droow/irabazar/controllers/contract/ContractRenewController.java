package com.droow.irabazar.controllers.contract;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.controllers.customer.CustomerDetailController;
import com.droow.irabazar.controllers.product.ProductNewController;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class ContractRenewController extends BaseContractController implements Initializable, BackButtonDefined {

    @FXML
    private Label contractCodeLabel;

    @FXML
    private TextField daysToKeepInput;

    @FXML
    private DatePicker fromDateInput;

    @FXML
    private TableView<ProductsEntity> productsListTable;

    @FXML
    private Button contractCustomerButton;
    @FXML
    private Button backButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(this.backLinkScreen != null) {
            this.backButton.setVisible(false);
        }
    }

    @FXML
    public void onBackButtonAction(ActionEvent event) {
        this.sc.loadScreen(this.backLinkScreen);
    }

    @FXML
    private void onAddProductButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenProductsNew);
        ProductNewController controller = (ProductNewController) this.sc.getCurrentController();
        controller.cleanData();
        controller.setBackLinkScreen(Main.screenContractRenew);
        controller.setProductCodeStep(this.productsListTable.getItems().size()+1);
        controller.addCallbackFunction(this::addProductToList);
        controller.addCallbackFunction(prod -> this.returnBack());
    }

    @FXML
    private void onCompleteContractButtonAction(ActionEvent event) {

        this.sc.loadScreen(Main.screenContractDetail);
        ContractDetailController controller = (ContractDetailController)this.sc.getCurrentController();
        controller.setCurrentContract(this.currentContract.getId());
        controller.setBackLinkScreen(Main.screenContractRenew);
    }

    @FXML
    private void onContractCustomerButtonAction(ActionEvent event){
        this.sc.loadScreen(Main.screenCustomersDetail);
        CustomerDetailController controller = (CustomerDetailController)this.sc.getCurrentController();
        controller.setBackLinkScreen(Main.screenContractRenew);
        controller.setCurrentCustomer(this.currentContract.getCustomerId());
    }

    /* CALLBACKS */

    /**
     * Callback function to return back to this screen
     * @return null
     */
    private Void returnBack() {
        this.sc.loadScreen(Main.screenContractRenew);
        return null;
    }

    /**
     * Callback function to add product to list and refresh TableView
     * @param productsEntity ProductsEntity to add
     * @return null
     */
    private Void addProductToList(ProductsEntity productsEntity) {
        ObservableList<ProductsEntity> list = FXCollections.observableArrayList(this.productsListTable.getItems());
        list.add(productsEntity);
        this.currentContract.addProduct(productsEntity);
        Utils.populateProductTableView(
                this.productsListTable,
                list,
                null,
                null,
                null,
                false
        );
        return null;
    }

    @Override
    public void refreshData() {
        this.contractCodeLabel.setText(this.currentContract.getCode());
        this.contractCustomerButton.setText(this.currentContract.getCustomer().toString());
        this.fromDateInput.setValue(Utils.getLocalDateFromDate(new Date()));
        this.daysToKeepInput.setText(optionsList.get("defaultDaysToKeep"));
        ObservableList<ProductsEntity> productList = FXCollections.observableArrayList(this.currentContract.getProducts());
        Utils.populateProductTableView(
                this.productsListTable,
                productList,
                null,
                null,
                null,
                false
        );
    }

}
