package com.droow.irabazar.controllers;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.contract.ContractNewController;
import com.droow.irabazar.controllers.customer.CustomerDetailController;
import com.droow.irabazar.controllers.customer.CustomerIndexController;
import com.droow.irabazar.controllers.order.OrderNewController;
import com.droow.irabazar.controllers.product.ProductPayoutController;
import com.droow.irabazar.model.service.ContractService;
import com.droow.irabazar.model.service.ProductService;
import com.droow.irabazar.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by droow on 31.10.15.
 */
@Component
public class RootController extends BaseScreenController implements Initializable {

    @Autowired
    ContractService contractService;
    
    @Autowired
    ProductService productService;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        optionsList = this.optionService.mapOption();
        contractService.updateAllContracts();
        this.refreshData();
    }

    // =====================================================================
    // Products
    @FXML
    private void onMenuProductsAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenProducts);
        this.sc.getCurrentController().refreshData();
    }

    @FXML
    private void onMenuProductsCategoriesAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenProductsCategories);
    }

    @FXML
    private void onMenuProductsStockingAction(ActionEvent event) {
        Utils.printStocking(this.productService);
    }
    
    @FXML
    private void onMenuProductsPayout(ActionEvent event) {
    	String oldScreen = this.sc.getCurrentScreenId();
    	this.sc.loadScreen(Main.screenProductPayout);
    	
        ProductPayoutController controller = (ProductPayoutController)this.sc.getCurrentController();
        controller.refreshData();
        controller.setBackLinkScreen(oldScreen);
    }
    
    // ====================================================================
    // Goods
    @FXML
    private void onMenuProductsSaleAction(ActionEvent event) {
        String oldScreen = this.sc.getCurrentScreenId();
        this.sc.loadScreen(Main.screenOrderNew);
        OrderNewController controller = (OrderNewController)this.sc.getCurrentController();
        controller.refreshData();
        controller.setBackLinkScreen(oldScreen);
    }

    @FXML
    private void onMenuProductsReceiptAction(ActionEvent event) {
        String oldScreen = this.sc.getCurrentScreenId();
        this.sc.loadScreen(Main.screenContractNew);
        ContractNewController controller = (ContractNewController)this.sc.getCurrentController();
        controller.refreshData();
        controller.setBackLinkScreen(oldScreen);
    }

    // =====================================================================
    // Papers
    @FXML
    private void onMenuContractsAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenContracts);
        this.sc.getCurrentController().refreshData();
    }

    @FXML
    private void onMenuOrdersAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenOrders);
        this.sc.getCurrentController().refreshData();
    }

    // =====================================================================
    // Customers
    @FXML
    private void onMenuCustomersAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenCustomers);
        ((CustomerIndexController)this.sc.getCurrentController()).clearSelection();
    }

    @FXML
    private void onMenuCustomersNewAction(ActionEvent event) {
        String oldScreen = this.sc.getCurrentScreenId();
        this.sc.loadScreen(Main.screenCustomersDetail);
        CustomerDetailController controller = (CustomerDetailController)this.sc.getCurrentController();
        controller.setCurrentCustomer(0);
        controller.setBackLinkScreen(oldScreen);
    }

    // =====================================================================
    // Settings
    @FXML
    private void onMenuSettingsAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenSettings);
    }
    @FXML
    private void onMenuSettingsPostponeAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenSettingsPostpone);
    }

    // =====================================================================
    // Help
    @FXML
    private void onMenuHelpAboutAction(ActionEvent event) {
        // show dialog
        Utils.showInfoDialog(
                "O aplikaci",
                "Autor: Daniel Pršala\nEmail: droow@email.cz",
                "Verze programu: 0.6");
    }

    @Override
    public void refreshData() {
        this.checkOptions();
    }
}
