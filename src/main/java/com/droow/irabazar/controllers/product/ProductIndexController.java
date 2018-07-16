package com.droow.irabazar.controllers.product;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.contract.ContractNewController;
import com.droow.irabazar.controllers.order.OrderNewController;
import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * FXML Controller class
 *
 * @author Angie
 */
@Component
public class ProductIndexController extends BaseProductController implements Initializable {

    //private boolean shoppingMode = false;
    @FXML
    private TableView<ProductsEntity> productsListTableView;
    //@FXML
    //private SplitPane productSplitPanel;
    @FXML
    private TextField productsFilterInput;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert productsListTableView != null : "fx:id=\"customersListTable\" was not injected: check your FXML file 'simple.fxml'.";
        //this.productSplitPanel.setDividerPosition(0, 1);
        this.refreshData();
    }

    private void populateTable() {
        Utils.populateProductTableView(
            this.productsListTableView,
            this.productList,
            (observableValue, oldValue, newValue) -> {
                //Check whether item is selected and set value of selected item to Label
                if (productsListTableView.getSelectionModel().getSelectedItem() != null) {
                    this.sc.loadScreen(Main.screenProductsDetail);
                    ProductDetailController controller = (ProductDetailController)this.sc.getCurrentController();
                    controller.setCurrentProduct(newValue.getId());
                    controller.addCallbackFunction(prod -> this.refresh());
                    controller.setBackLinkScreen(Main.screenProducts);
                }
            },
            null,
            this.productsFilterInput.textProperty(),
            false
        );
    }

    @FXML
    public void onShoppingButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenOrderNew);
        OrderNewController controller = (OrderNewController)this.sc.getCurrentController();
        controller.refreshData();
        controller.setBackLinkScreen(Main.screenProducts);
    }

    @FXML
    public void onDepositButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenContractNew);
        ContractNewController controller = (ContractNewController)this.sc.getCurrentController();
        controller.refreshData();
        controller.setBackLinkScreen(Main.screenProducts);
    }

    private Void refresh(){
    	refreshData();
    	return null;
    }
    
    @Override
    public void refreshData() {
        this.productList = this.getAllProductsList();
        this.populateTable();
    }
    

}