package com.droow.irabazar.controllers.order;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.controllers.product.ProductNewController;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.model.entity.OrdersEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.model.service.ContractService;
import com.droow.irabazar.utils.AutoCompleteComboBoxListener;
import com.droow.irabazar.utils.Utils;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class OrderNewController extends BaseOrderController implements Initializable, BackButtonDefined {

    @FXML
    private ComboBox<ProductsEntity> productInput;

    @FXML
    private TextField productCountInput;
    
    @FXML
    private TextField productPriceInput;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private TableView<OrderItemsEntity> orderItemsListTable;

    @Autowired
    private ContractService contractService;

    @FXML
    private Button backButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.refreshData();
        this.currentOrder = new OrdersEntity();
        if(this.backLinkScreen != null) {
            this.backButton.setVisible(false);
        }
    }

    @FXML
    public void onBackButtonAction(ActionEvent event) {
        this.sc.loadScreen(this.backLinkScreen);
        
        this.currentOrder.getOrderItems().clear();
        ObservableList<OrderItemsEntity> list = FXCollections.observableArrayList(this.currentOrder.getOrderItems());
        Utils.populateOrderItemsTableView(
    		this.orderItemsListTable, 
    		false,
    		list,
    		this::goToProductDetail, 
    		this::removeProduct
        );
        refreshData();
    }

    @FXML
    private void onCompleteOrderButtonAction(ActionEvent event) {
        if (!Utils.showConfirmationDialog("Potvrďte akci", "Opravdu chcete vytvořit novou objednávku?", "")) return;

        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        
        Collection<OrderItemsEntity> ois = this.currentOrder.getOrderItems();
        this.currentOrder.setCreated(timeNow);
        this.currentOrder.setCode(this.orderService.getOrderCode());
        this.orderService.addOrder(this.currentOrder);
        Double totalPrice = 0.0d;
        
        for (OrderItemsEntity oi : ois) {
            this.orderService.addOrderItem(oi);
            
            ProductsEntity product = oi.getProduct();
            product.setDateSold(timeNow);
            product.setCount(product.getCount() - oi.getCount());
            
            double newPrice = product.getTotalSalePrice() - oi.getTotalPrice();
            
            product.setTotalSalePrice(newPrice > 0 ? newPrice : 0);
            
            if(product.getCount() == 0){
            	product.setStatus(ProductsEntity.Status.SOLD);
            }
            
            this.productService.updateProduct(product);
            
            if (product.getCount() == 0) {
                ContractsEntity contract = product.getContract();
                contract.updateStatus();
                this.contractService.updateContract(contract);
            }
            
            totalPrice += oi.getTotalPrice();
        }
        
        this.currentOrder.setTotalPrice(totalPrice);
        this.orderService.updateOrder(this.currentOrder);

        Utils.showInfoNotification("Nová objednávka úspěšně uložena!");

        this.sc.loadScreen(Main.screenOrderDetail);
        OrderDetailController controller = (OrderDetailController) this.sc.getCurrentController();
        controller.setBackLinkScreen(Main.screenOrderNew);
        controller.setCurrentOrder(this.currentOrder.getId());
        
        //clear products list after completing the order
        this.currentOrder = new OrdersEntity();
        Utils.populateOrderItemsTableView(
    		this.orderItemsListTable, 
    		false,
    		FXCollections.observableArrayList(this.currentOrder.getOrderItems()), 
    		this::goToProductDetail, 
    		this::removeProduct
        );
    }

    @FXML
    private void onAddProductButtonAction(ActionEvent event) {
        //ProductsEntity prod = this.productInput.getSelectionModel().getSelectedItem();
        this.addProductToList(currentProduct, Integer.parseInt(this.productCountInput.getText()));
        Double totalPrice = 0.0d;
        Collection<OrderItemsEntity> ois = this.currentOrder.getOrderItems();
        for(OrderItemsEntity oi : ois) {
            if(oi.getProduct() == currentProduct && oi.getCount() == currentProduct.getCount()) {
                this.productInput.getItems().remove(currentProduct);
            }
            totalPrice += oi.getTotalPrice();
        }

        this.productCountInput.setText("");
        this.productPriceInput.setText("");
        this.totalPriceLabel.setText(""+totalPrice);
        
        SelectionModel sm = this.productInput.getSelectionModel();
        if(sm != null) {
            sm.clearSelection();
        }
    }

    /* CALLBACKS */

    /**
     * Callback function to add product to list and refresh TableView
     * @param productsEntity ProductsEntity to add to order
     * @param count Count of products to add to order
     * @return null
     */
    private Void addProductToList(ProductsEntity productsEntity, Integer count) {
        this.currentOrder.addProduct(productsEntity, count);
        ObservableList<OrderItemsEntity> list = FXCollections.observableArrayList(this.currentOrder.getOrderItems());
        Utils.populateOrderItemsTableView(this.orderItemsListTable, false, list, this::goToProductDetail, this::removeProduct);
        return null;
    }
    
    private void goToProductDetail(
            ObservableValue<?> observableValue,
            OrderItemsEntity oldValue,
            OrderItemsEntity newValue
            ) {
        //Check whether item is selected and set value of selected item to Label
        if (orderItemsListTable.getSelectionModel().getSelectedItem() != null && newValue != null) {
            this.sc.loadScreen(Main.screenProductsNew);
            
            ProductNewController controller = ((ProductNewController)this.sc.getCurrentController());
            controller.setBackLinkScreen(Main.screenOrderNew);
            controller.setCurrentProduct(newValue.getProduct().getId());
            controller.addCallbackFunction(prod -> this.returnBack());
            controller.addCallbackFunction(prod -> this.refreshProductList());
            orderItemsListTable.getSelectionModel().clearSelection();
        }
    }
    
    private void removeProduct(
            ObservableValue<?> observableValue,
            OrderItemsEntity oldValue,
            OrderItemsEntity newValue
            ) {
        //Check whether item is selected and set value of selected item to Label
        if (orderItemsListTable.getSelectionModel().getSelectedItem() != null && newValue != null) {
        	//this.productList.remove(newValue);
        	ObservableList<OrderItemsEntity> list = FXCollections.observableArrayList(this.orderItemsListTable.getItems());
        	list.remove(newValue);
        	this.currentOrder.removeProduct(newValue.getProduct());
        	refreshProductList(list);
        }
    }

    /* CALLBACKS */

    /**
     * Callback function to return back to this screen
     * @return null
     */
    private Void returnBack() {
        this.sc.loadScreen(Main.screenOrderNew);
        return null;
    }
    
    /**
     * Callback function to refresh products TableView
     * @return null
     */
    private Void refreshProductList() {
    	return refreshProductList(null);
    }
    
    private Void refreshProductList(ObservableList<OrderItemsEntity> list){
    	if(list == null){
    		list = FXCollections.observableArrayList(this.orderItemsListTable.getItems());
    	}
    	
    	Utils.populateOrderItemsTableView(this.orderItemsListTable, false, list, this::goToProductDetail, this::removeProduct);
        
        return null;
    }

    @Override
    public void refreshData() {
    	List<ProductsEntity> prods = this.getAllProductsList().stream().filter(
            product -> (product.getStatus() != ProductsEntity.Status.RETURNED && product.getStatus() != ProductsEntity.Status.SOLD && product.getStatus() != ProductsEntity.Status.PAID && product.getCount() > 0)
        ).collect(Collectors.toList());
    	
        this.productInput.setItems(FXCollections.observableArrayList(prods));
        
        new AutoCompleteComboBoxListener<>(this.productInput);
        
        this.productInput.getSelectionModel().selectedItemProperty().addListener(
                (ov, t, t1) -> {
                    ProductsEntity prod = (ProductsEntity) t1;
                    currentProduct = prod;
                    productCountInput.setText("1");
                    if(prod != null){
                    	productPriceInput.setText("" + Utils.getFinalSalePrice(prod.getSalePrice()));
                    }
                    else {
                    	productPriceInput.setText("");
                    }
                }
        );
        this.productCountInput.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    Integer count = Integer.parseInt(newValue);
                    int alreadyAddedCount = getAlreadyAddedCount(currentProduct);
                    
                    if (count + alreadyAddedCount > currentProduct.getCount()) {
                        count = currentProduct.getCount() - alreadyAddedCount;
                        productCountInput.setText(count.toString());
                    }
                    
                    productPriceInput.setText((currentProduct.getSalePrice()*count)+"");
                }
        );
        this.productPriceInput.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.equals("")){
                    	if(this.currentProduct == null) return;
                    	
                    	try {
                    		newValue = "" + this.currentProduct.getSalePrice() * Integer.valueOf(productCountInput.getText());
                    	} catch(Exception e){
                    		return;
                    	}
                    }
                    try {
                        Double price = Utils.checkDoubleNumber(newValue);
                        currentProduct.setCurrentSalePrice(price);
                        
                    } catch (Exception e) {
                        ((StringProperty)observable).setValue(oldValue);
                        Utils.showErrorDialog("Error", e.getMessage(), null);
                    }
                }
        );
    }
    
    private int getAlreadyAddedCount(ProductsEntity currentProduct){
    	for(OrderItemsEntity orderedItem : this.currentOrder.getOrderItems()){
    		if(orderedItem.getProduct().equals(currentProduct)){
    			return orderedItem.getCount();
    		}
    	}
    	
    	return 0;
    }

}
