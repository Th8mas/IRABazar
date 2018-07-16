package com.droow.irabazar.controllers.contract;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.controllers.customer.CustomerDetailController;
import com.droow.irabazar.controllers.product.ProductDetailController;
import com.droow.irabazar.controllers.product.ProductNewController;
import com.droow.irabazar.model.entity.CategoriesEntity;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.CustomersEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.model.service.CategoryServiceImpl;
import com.droow.irabazar.utils.AutoCompleteComboBoxListener;
import com.droow.irabazar.utils.ContractContentGenerator;
import com.droow.irabazar.utils.Utils;
import com.sun.xml.internal.ws.api.message.Packet;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class ContractNewController extends BaseContractController implements Initializable, BackButtonDefined {

    @FXML
    private ComboBox<CustomersEntity> customerInput;

    @FXML
    private TextField daysToKeepInput;
    @FXML
    private TextField profitInput;

    @FXML
    private DatePicker fromDateInput;

    @FXML
    private Button addCustomerButton;
    @FXML
    private Button backButton;

    @FXML
    private TableView<ProductsEntity> productsListTable;

    private CustomersEntity currentCustomer;

    private ContractsEntity contractToRenew = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.profitInput.textProperty().addListener((observable, oldValue, newValue) -> {
            Integer val = Integer.parseInt(newValue);
            if(val >= 0) {
                currentContract.setProfit(val);
                for (ProductsEntity prod : currentContract.getProducts()) {
                    double salePrice = Utils.getSalePrice(prod.getTotalPrice(), val);
                    prod.setSalePrice(salePrice / prod.getCount());
                    prod.setTotalSalePrice(salePrice);
                }
                ObservableList<ProductsEntity> list = FXCollections.observableArrayList(currentContract.getProducts());
                Utils.populateProductTableView(
                    productsListTable,
                    list,
                    null,
                    null,
                    null,
                    false
                );
            }
        });
        if(this.backLinkScreen != null) {
            this.backButton.setVisible(false);
        }
        this.refreshData();
    }

    @FXML
    public void onBackButtonAction(ActionEvent event) {
        this.sc.loadScreen(this.backLinkScreen);
    }

    @FXML
    private void onAddCustomerButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenCustomersDetail);
        CustomerDetailController controller = (CustomerDetailController) this.sc.getCurrentController();
        controller.setBackLinkScreen(Main.screenContractNew);
        controller.cleanData();
        controller.addCallbackFunction(this::customerCreated);
        controller.addCallbackFunction(customer -> this.returnBack());
    }

    @FXML
    private void onAddProductButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenProductsNew);
        ProductNewController controller = (ProductNewController) this.sc.getCurrentController();
        controller.setProfit(currentContract.getProfit());
        controller.setBackLinkScreen(Main.screenContractNew);
        controller.cleanData();
        controller.setProductCodeStep(this.productsListTable.getItems().size()+1);
        controller.addCallbackFunction(this::addProductToList);
        controller.addCallbackFunction(prod -> this.returnBack());
    }

    @FXML
    private void onCompleteContractButtonAction(ActionEvent event) {
        if (!Utils.showConfirmationDialog(
                "Potvrďte akci",
                "Opravdu chcete dokončit příjem?",
                "Po dokončení již nebude možné přidat do smlouvy další zboží.")) return;
        // generate contract
        Double totalPrice = 0.0D;

        for( ProductsEntity prod : this.currentContract.getProducts()) {
            totalPrice += prod.getPrice() * prod.getOriginalCount();
        }
        
        Integer daysToKeep = Integer.parseInt(this.daysToKeepInput.getText());
        Timestamp accepted = Timestamp.valueOf(this.fromDateInput.getValue().atStartOfDay());
        Long result = (long) (24 * 60 * 60 * 1000);
        Timestamp acceptedTo = new Timestamp(accepted.getTime() + result*daysToKeep);
        Timestamp pickupDate = Utils.getFirstWorkingDay(
            new Timestamp(
                acceptedTo.getTime() + result*Integer.parseInt(optionsList.get("lowerPriceDaysLimit"))
            )
        );

        this.currentContract
            .setDateFrom(accepted)
            .setDateTo(acceptedTo)
            .setDaysToKeep(daysToKeep)
            .setDateToPickup(pickupDate)
            .setTotalPrice(totalPrice)
            .setStatus(this.contractToRenew != null ? ContractsEntity.Status.RENEWED : ContractsEntity.Status.NEW)
            .setCode(this.currentContract.getProducts().iterator().next().getCode());

        this.contractService.addContract(this.currentContract);

        for( ProductsEntity prod : this.currentContract.getProducts()) {
            prod.setContract(this.currentContract);

            if(contractToRenew != null) {
                ProductsEntity renewedFrom = prod.getRenewedFromProduct();
                renewedFrom
                    .setStatus(ProductsEntity.Status.RETURNED)
                    .setRenewedProduct(prod);
                this.productService.updateProduct(renewedFrom);
            } else {
                this.productService.merge(prod);
            }
        }

        Utils.generateContractContent(this.currentContract, optionsList, this.contractToRenew != null ? ContractContentGenerator.TEMPLATE_RENEW : ContractContentGenerator.TEMPLATE_NEW);

        if(this.contractToRenew != null){
            currentContract.setRenewedFromContract(contractToRenew);
            
            this.contractToRenew
                .setRenewedContract(currentContract)
                .setStatus(ContractsEntity.Status.TERMINATED);
            this.contractService.updateContract(this.contractToRenew);
            this.contractService.updateContract(this.currentContract);
            
            Utils.showInfoNotification("Smlouva úspěšně obnovena!");
        } else {
            this.contractService.updateContract(this.currentContract);
            
            Utils.showInfoNotification("Nová smlouva úspěšně uložena!");
        }

        this.sc.loadScreen(Main.screenContractDetail);
        
        ContractDetailController controller = (ContractDetailController) this.sc.getCurrentController();
        controller.setCurrentContract(this.currentContract.getId());
        //controller.setBackLinkScreen(Main.screenContractNew);
    }

    public ObservableList<CustomersEntity> getCustomerList() {
        return FXCollections.observableList(customerService.listCustomer());
    }

    public void setContractToRenew(ContractsEntity contract) {
        this.contractToRenew = contract;
        this.currentContract = new ContractsEntity();
        this.currentContract.setCustomer(contract.getCustomer());

        this.customerInput.setValue(contract.getCustomer());
        this.customerInput.setDisable(true);
        this.addCustomerButton.setVisible(false);

        int it = 1;
        for(ProductsEntity prod : contract.getProducts().stream().filter(
                product -> product.getCount() != 0
        ).collect(Collectors.toList())) {
            ProductsEntity newProd = new ProductsEntity();
            newProd.setOriginalCount(prod.getCount())
                    .setCount(prod.getCount())
                    .setName(prod.getName())
                    .setCode(productService.getProductCode(it++))
                    .setDescription(prod.getDescription())
                    .setType(prod.getType())
                    // prices
                    .setPrice(prod.getPrice())
                    .setSalePrice(prod.getSalePrice())
                    .setLowerPrice(prod.getLowerPrice())
                    // total prices
                    .setTotalPrice(prod.getTotalPrice())
                    .setTotalSalePrice(prod.getTotalSalePrice())
                    .setTotalLowerPrice(prod.getLowerPrice()*prod.getCount())
                    .setTotalStorageFee(prod.getStorageFee()*prod.getCount())
                    // fees
                    .setWithdrawalFee(prod.getWithdrawalFee())
                    .setStorageFee(prod.getStorageFee())
                    // associations
                    .setCategories(prod.getCategories())
                    // renew related
                    .setRepeatedSale(prod.getRepeatedSale()+1)
                    .setRenewedFromProduct(prod)
                    .setStatus(ProductsEntity.Status.RENEWED);

            Utils.setRenewPrice(newProd, optionService);
            
            this.currentContract.addProduct(newProd);
        }
        Utils.populateProductTableView(
        	this.productsListTable, 
        	FXCollections.observableList(new ArrayList(currentContract.getProducts())), 
        	null, 
        	null, 
        	null, 
        	false
        );
    }

    private void goToProductDetail(
            ObservableValue<?> observableValue,
            ProductsEntity oldValue,
            ProductsEntity newValue
            ) {
        //Check whether item is selected and set value of selected item to Label
        if (productsListTable.getSelectionModel().getSelectedItem() != null && newValue != null) {
            this.sc.loadScreen(Main.screenProductsNew);
            ProductNewController controller = ((ProductNewController)this.sc.getCurrentController());
            controller.setBackLinkScreen(Main.screenContractNew);
            controller.setCurrentProduct(newValue);
            controller.addCallbackFunction(prod -> this.returnBack());
            controller.addCallbackFunction(prod -> this.refreshProductList());
            productsListTable.getSelectionModel().clearSelection();
        }
    }
    
    private void removeProduct(
            ObservableValue<?> observableValue,
            ProductsEntity oldValue,
            ProductsEntity newValue
            ) {
        //Check whether item is selected and set value of selected item to Label
        if (productsListTable.getSelectionModel().getSelectedItem() != null && newValue != null) {
        	//this.productList.remove(newValue);
        	ObservableList<ProductsEntity> list = FXCollections.observableArrayList(this.productsListTable.getItems());
        	list.remove(newValue);
        	this.currentContract.removeProduct(newValue);
        	refreshProductList(list);
        }
    }

    /* CALLBACKS */

    /**
     * Callback function to return back to this screen
     * @return null
     */
    private Void returnBack() {
        this.sc.loadScreen(Main.screenContractNew);
        return null;
    }
    
    /**
     * Callback function to refresh products TableView
     * @return null
     */
    private Void refreshProductList() {
    	return refreshProductList(null);
    }
    
    private Void refreshProductList(ObservableList<ProductsEntity> list){
    	if(list == null){
    		list = FXCollections.observableArrayList(this.productsListTable.getItems());
    	}
    	
    	Utils.populateProductTableView(
            this.productsListTable,
            list,
            this::goToProductDetail,
            this::removeProduct,
            null,
            false
        );
        return null;
    }

    /**
     * Callback function to set customer data
     * @param customer CustomersEntity to add
     * @return null
     */
    private Void customerCreated(CustomersEntity customer) {
        this.customerInput.setItems(this.getCustomerList());
        this.customerInput.setValue(customer);
        return null;
    }

    /**
     * Add product to list and update TableView
     * @param productsEntity ProductsEntity
     * @return null
     */
    private Void addProductToList(ProductsEntity productsEntity) {
        ObservableList<ProductsEntity> list = FXCollections.observableArrayList(this.productsListTable.getItems());
        for(ProductsEntity prod : list) {
            if(prod.getCode().equals(productsEntity.getCode())) return null;
        }
        productsEntity.setContract(this.currentContract);
        
        list.add(productsEntity);
        this.currentContract.addProduct(productsEntity);
        Utils.populateProductTableView(
            this.productsListTable,
            list,
            this::goToProductDetail,
            this::removeProduct,
            null,
            false
        );
        return null;
    }

    @Override
    public void refreshData() {
        this.customerInput.setItems(this.getCustomerList());
        this.customerInput.setValue(null);
        this.daysToKeepInput.setText(optionsList.get("defaultDaysToKeep"));
        this.fromDateInput.setValue(Utils.getLocalDateFromDate(new Date()));
        this.currentContract = new ContractsEntity();
        this.productList = FXCollections.observableArrayList();
        Utils.populateProductTableView(
            this.productsListTable,
            this.productList,
            null,
            null,
            null,
            false
        );

        if(this.contractToRenew == null) {
            new AutoCompleteComboBoxListener<>(this.customerInput);
            this.customerInput.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldValue, newValue) -> {
                    if (newValue != null) {
                        currentContract.setCustomer(newValue);
                        currentCustomer = newValue;
                    }
                }
            );
        }
        this.currentContract.setProfit(Integer.parseInt(optionsList.get("profitInPercent")));
        this.profitInput.setText(optionsList.get("profitInPercent"));
    }

}
