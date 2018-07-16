package com.droow.irabazar.controllers.contract;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.controllers.customer.CustomerDetailController;
import com.droow.irabazar.controllers.product.ProductDetailController;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.utils.ContractContentGenerator;
import com.droow.irabazar.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class ContractDetailController extends BaseContractController implements Initializable, BackButtonDefined {

    @FXML
    private Label contractNumberLabel;
    @FXML
    private Label contractStatusLabel;
    @FXML
    private Label contractToLabel;
    @FXML
    private Label contractFromLabel;
    @FXML
    private Label contractPickupDateLabel;
    @FXML
    private Label contractProfitLabel;
    @FXML
    private Text contractText;

    @FXML
    private Button endContractButton;
    @FXML
    private Button renewContractButton;
    @FXML
    private Button terminateContractButton;
    @FXML
    private Button contractCustomerButton;
    @FXML
    private Button backButton;

    @FXML
    private TableView<ProductsEntity> contractProductsTableView;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //this.refreshData();
        this.endContractButton.setVisible(false);
        this.renewContractButton.setVisible(false);
        this.terminateContractButton.setVisible(false);

        if(this.backLinkScreen != null) {
            this.backButton.setVisible(false);
        }
    }

    @Override
    public void setCurrentContract(Integer id) {
        super.setCurrentContract(id);

        if((this.currentContract.getStatus().equals(ContractsEntity.Status.NEW) || 
        	this.currentContract.getStatus().equals(ContractsEntity.Status.RENEWED)) &&
        	this.currentContract.getDateTo().after(new Date())
        ){
        	this.terminateContractButton.setVisible(true);
        	this.endContractButton.setVisible(false);
        }
        else {
        	this.terminateContractButton.setVisible(false);
        	this.endContractButton.setVisible(true);
        }

        this.refreshData();
    }

    @FXML
    private void onEndContractButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenContractPayout);
        ContractPayoutController controller = (ContractPayoutController)this.sc.getCurrentController();
        controller.isTermination = false;
        controller.setBackLinkScreen(Main.screenContractDetail);
        controller.setCurrentContract(currentContract.getId());
    }

    @FXML
    private void onRenewContractButtonAction(ActionEvent event) {
        // TODO: if nothing sold, go to new, if something sold go to payout
        if(Utils.showConfirmationDialog(
                "Potvrďte akci",
                "Opravdu chcete obnovit smlouvu?",
                "")) {
            this.sc.loadScreen(Main.screenContractNew);
            ContractNewController controller = (ContractNewController)this.sc.getCurrentController();
            controller.setContractToRenew(this.currentContract);
            controller.setBackLinkScreen(Main.screenContractDetail);
        }
    }

    @FXML
    private void onTerminateContractButtonAction(ActionEvent event) {
        if(Utils.showConfirmationDialog(
                "Potvrďte akci",
                "Opravdu chcete odstoupit od smlouvy?",
                "")) {
            // TODO: if something sold, go to payout, if nothing sold go to terminate
        	//if(somethingSold(this.currentContract)){
	        	this.sc.loadScreen(Main.screenContractPayout);
	            ContractPayoutController controller = (ContractPayoutController)this.sc.getCurrentController();
	            controller.isTermination = true;
	            controller.setCurrentContract(this.currentContract.getId());
	            controller.setBackLinkScreen(Main.screenContractDetail);
	            controller.finish();
        	/*
        	}
        	else {
        		this.sc.loadScreen(Main.s)this.sc.getCurrentController();
	            controller.setCurrentContract(this.currentContract.getId());
	            controller.setBackLinkScreen(Main.screenContractDetail);
        	}
        	*/
        }
    }
    
    private boolean somethingSold(ContractsEntity contract){
    	for(ProductsEntity product : contract.getProducts()){
    		if(product.getStatus() == ProductsEntity.Status.SOLD || product.getStatus() == ProductsEntity.Status.PAID){
    			return true;
    		}
    	}
    	return false;
    }

    @FXML
    public void onBackButtonAction(ActionEvent event) {
        this.sc.loadScreen(this.backLinkScreen);
    }

    @FXML
    private void onPrintButtonAction(ActionEvent event) {
        Utils.printString(this.currentContract.getContent());
    }

    @FXML
    private void onPrintCardsButtonAction(ActionEvent event) {
        Utils.printString(ContractContentGenerator.getProductCards(this.currentContract.getProducts()));
    }

    @FXML
    private void onContractCustomerButtonAction(ActionEvent event){
        this.sc.loadScreen(Main.screenCustomersDetail);
        CustomerDetailController controller = (CustomerDetailController)this.sc.getCurrentController();
        controller.setCurrentCustomer(this.currentContract.getCustomerId());
        controller.setBackLinkScreen(Main.screenContractDetail);
    }

    @Override
    public void refreshData() {
        // TMP
        //Utils.generateContractContent(this.currentContract, optionsList, ContractContentGenerator.TEMPLATE_NEW);

        //this.currentContract.updateStatus();
        //this.contractService.updateContract(this.currentContract);

        SimpleDateFormat dt1 = new SimpleDateFormat("dd. MM. YYYY");
        this.contractToLabel.setText(dt1.format(this.currentContract.getDateTo()));
        this.contractFromLabel.setText(dt1.format(this.currentContract.getDateFrom()));
        this.contractPickupDateLabel.setText(dt1.format(this.currentContract.getDateToPickup()));

        String profit = this.currentContract.getProfit() != null ? this.currentContract.getProfit().toString() : "0";
        this.contractProfitLabel.setText(profit);
        this.contractText.setText(this.currentContract.getContent());
        this.contractNumberLabel.setText(this.currentContract.getCode());
        this.contractCustomerButton.setText(this.currentContract.getCustomer().toString());
        this.contractStatusLabel.setText(this.currentContract.getStatus().toString());

        ObservableList<ProductsEntity> productList = FXCollections.observableArrayList(this.currentContract.getProducts());
        Utils.populateProductTableView(
            this.contractProductsTableView,
            productList,
            (observableValue, oldValue, newValue) -> {
                //Check whether item is selected and set value of selected item to Label
                if (this.contractProductsTableView.getSelectionModel().getSelectedItem() != null) {
                    this.sc.loadScreen(Main.screenProductsDetail);
                    ProductDetailController controller = (ProductDetailController)this.sc.getCurrentController();
                    controller.setCurrentProduct(newValue.getId());
                    controller.addCallbackFunction(prod -> this.refresh());
                    controller.setBackLinkScreen(Main.screenContractDetail);
                }
            },
            null,
            /*
            (observableValue, oldValue, newValue) -> {
                //Check whether item is selected and set value of selected item to Label
                if (this.contractProductsTableView.getSelectionModel().getSelectedItem() != null) {
                	ObservableList<ProductsEntity> list = FXCollections.observableArrayList(this.contractProductsTableView.getItems());
                	list.remove(newValue);
                }
            },
            */
            null,
            true
        );
    }

    private Void refresh(){
    	refreshData();
    	return null;
    }

}
