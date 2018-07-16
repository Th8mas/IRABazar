package com.droow.irabazar.controllers.contract;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.controllers.customer.CustomerDetailController;
import com.droow.irabazar.controllers.product.ProductNewController;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.utils.PayoutContractContentGenerator;
import com.droow.irabazar.utils.Utils;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class ContractPayoutController extends BaseContractController implements Initializable, BackButtonDefined {

    @FXML
    private Label contractCodeLabel;
    @FXML
    private Label totalPayoutLabel;
    @FXML
    private Text contractText;
    
    @FXML
    private TableView<ProductsEntity> contractProductsTableView;

    @FXML
    private Button contractCustomerButton;
    @FXML
    private Button finishButton;
    @FXML
    private Button backButton;
    @FXML
    private Button printButton;

    @FXML
    private CheckBox removeFees;
    @FXML
    private CheckBox removePenalties;
    
    
    private List<ProductsEntity> productsLeft = new ArrayList<>();
    public boolean isTermination = false;

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
    private void onContractCustomerButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenCustomersDetail);
        CustomerDetailController controller = (CustomerDetailController)this.sc.getCurrentController();
        controller.setBackLinkScreen(Main.screenContractPayout);
        controller.setCurrentCustomer(this.currentContract.getCustomerId());
    }
    
    public void finish(){
    	onFinishButtonAction(null);
    }

    @FXML
    private void onFinishButtonAction(ActionEvent event) {
        if(!isTermination && !Utils.showConfirmationDialog(
                    "Potvrďte akci",
                    "Opravdu chcete vyplatit smlouvu?",
                    "Některé produkty ještě nebyly prodány, což povede na odstoupení od smlouvy.")) return;
        
        List<ProductsEntity> originalProducts = this.productService.listAllProducts().stream().filter(
    			product -> product.getContractId() == this.currentContract.getId()
    	).collect(Collectors.toList());
        
        boolean makeNewContract = false;
        
        if(isTermination) {
        	productsLeft.clear();
        }
        else {
	        for(ProductsEntity productToRenew : productsLeft){	
	        	if(productToRenew.getCount() != 0){		
	        		makeNewContract = true;
	        	}
	        }
        }
        
        optionsList.put("noStorageFee", "" + removeFees.isSelected());
        optionsList.put("noPenalty", "" + removePenalties.isSelected());
        optionsList.put("totalPayout", "" + Utils.getPriceString(currentContract.totalAmountToPayout()));
        
        currentContract.setProducts(productsLeft);
        Utils.generatePayoutContractContent(currentContract, originalProducts, optionsList, PayoutContractContentGenerator.TEMPLATE_NEW, isTermination);
         
        for(ProductsEntity originalProduct : originalProducts){
        	originalProduct.setStatus(originalProduct.getCount() == 0 ? ProductsEntity.Status.PAID : ProductsEntity.Status.RETURNED);
        	
        	if(isTermination){
        		originalProduct.setCount(0);
        	}
        	
        	this.productService.updateProduct(originalProduct);
        }
        
        this.contractText.setText(this.currentContract.getPayoutContent());
        this.currentContract.setStatus(isTermination ? ContractsEntity.Status.TERMINATED : ContractsEntity.Status.PAID);
        
        this.contractService.updateContract(currentContract);
        
        Utils.showInfoNotification("Smlouva byla úspěšně označena jako vyplacená!");
        
        this.finishButton.setVisible(false);
        
        if(Utils.showConfirmationDialog("Tisk smlouvy", "Chcete vytisknout smlouvu?", null)){
        	onPrintButtonAction(null);
        }
        
        if(makeNewContract){
        	this.sc.loadScreen(Main.screenContractNew);
        	
        	ContractNewController controller = (ContractNewController) this.sc.getCurrentController();
            controller.setContractToRenew(currentContract);
            controller.setBackLinkScreen(Main.screenContractPayout);
        }
    }

    @FXML
    private void onRenewButtonAction(ActionEvent event) {
        if(Utils.showConfirmationDialog(
                "Potvrďte akci",
                "Opravdu chcete obnovit smlouvu?",
                "")) {
            this.sc.loadScreen(Main.screenContractNew);
            ContractNewController controller = (ContractNewController)this.sc.getCurrentController();
            controller.setBackLinkScreen(Main.screenContractPayout);
            controller.setContractToRenew(this.currentContract);
        }
    }
    
    @FXML
    public void onPrintButtonAction(ActionEvent event) {
    	Utils.printString(this.currentContract.getPayoutContent());
    }

    @Override
    public void refreshData() {
        if(this.currentContract != null) {
            this.contractCodeLabel.setText(this.currentContract.getCode());

            boolean isPaid = currentContract.getStatus() == ContractsEntity.Status.PAID || currentContract.getStatus() == ContractsEntity.Status.TERMINATED;
            
            this.totalPayoutLabel.setText(Utils.getPriceString(this.currentContract.totalAmountToPayout()));
            this.contractCustomerButton.setText(this.currentContract.getCustomer().toString());

            List<ProductsEntity> prods = this.currentContract.getProducts().stream().filter(
                product -> product.getCount() != 0
            ).collect(Collectors.toList());
            
            this.productsLeft = prods;
            
            Utils.populateProductTableView(
            		this.contractProductsTableView, 
            		FXCollections.observableList(prods), 
            		this::goToProductDetail,
                    isPaid ? null : this::removeProduct, 
            		null,
            		false
            );
            
            if(isPaid){
            	this.contractProductsTableView.setDisable(true);
            	this.contractText.setText(this.currentContract.getPayoutContent());
            	this.finishButton.setVisible(false);

                this.removeFees.setVisible(false);
                this.removePenalties.setVisible(false);
            }
            else {
            	this.contractProductsTableView.setDisable(false);
            	this.contractText.setText(this.currentContract.getPayoutContent());
            	this.finishButton.setVisible(true);

                this.removeFees.setVisible(true);
                this.removePenalties.setVisible(true);
            }
            
            this.removeFees.setSelected(false);
            this.removePenalties.setSelected(false);
        }
    }

    /* CALLBACKS */

    /**
     * Callback function to return back to this screen
     * @return null
     */
    private Void returnBack() {
        this.sc.loadScreen(Main.screenContractPayout);
        return null;
    }
    
    private void goToProductDetail(
            ObservableValue<?> observableValue,
            ProductsEntity oldValue,
            ProductsEntity newValue
            ) {
        //Check whether item is selected and set value of selected item to Label
        if (contractProductsTableView.getSelectionModel().getSelectedItem() != null && newValue != null) {
            this.sc.loadScreen(Main.screenProductsNew);
            ProductNewController controller = ((ProductNewController)this.sc.getCurrentController());
            controller.setProfit(this.optionService.getOptionByName("profitInPercent").getIntValue());
            controller.setBackLinkScreen(Main.screenContractPayout);
            controller.setCurrentProduct(newValue);
            controller.addCallbackFunction(prod -> this.returnBack());
            controller.addCallbackFunction(prod -> this.refreshProductList(null));
            contractProductsTableView.getSelectionModel().clearSelection();
        }
    }
    
    private void removeProduct(
            ObservableValue<?> observableValue,
            ProductsEntity oldValue,
            ProductsEntity newValue
            ) {
        
        if (contractProductsTableView.getSelectionModel().getSelectedItem() != null && newValue != null) {
        	ObservableList<ProductsEntity> list = FXCollections.observableArrayList(this.contractProductsTableView.getItems());
        	list.remove(newValue);
        	refreshProductList(list);
        }
    }
    
    /**
     * Callback function to refresh products TableView
     * @return null
     */
    private Void refreshProductList() {
    	List<ProductsEntity> prods = this.productService.listAllProducts().stream().filter(
    			product -> product.getContractId() == this.currentContract.getId()
    	).filter(
    			product -> product.getCount() != 0
        ).collect(Collectors.toList());
    	
    	this.productsLeft = prods;
    	
    	ObservableList<ProductsEntity> list = FXCollections.observableArrayList(prods);
    	return refreshProductList(list);
    }
    
    private Void refreshProductList(ObservableList<ProductsEntity> list){
    	if(list == null){
    		list = FXCollections.observableArrayList(this.contractProductsTableView.getItems());
    	}
    	
    	this.productsLeft = list;
    	
    	Utils.populateProductTableView(
            this.contractProductsTableView,
            list,
            this::goToProductDetail,
            this::removeProduct,
            null,
            false
        );
        return null;
    }
    
}
