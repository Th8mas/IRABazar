package com.droow.irabazar.controllers.product;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.controllers.contract.ContractDetailController;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.utils.Utils;
import com.droow.irabazar.model.entity.CategoriesEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;

/**
 * Created by droow on 8.11.15.
 */
@Component
public class ProductDetailController extends BaseProductController implements Initializable, BackButtonDefined {

    @FXML
    private Label productNameLabel;
    @FXML
    private Label repeatedSaleLabel;
    @FXML
    private Label codeLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label countLabel;
    @FXML
    private Label originalCountLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label partsLabel;
    @FXML
    private Label profitLabel;
    @FXML
    private Label salePriceLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label typeLabel;

    @FXML
    private Button contractButton;
    @FXML
    private Button backButton;

    @FXML
    private VBox contentVBox;

    @FXML
    private GridPane baseGridPane;

    @FXML
    private ScrollPane sp;

    @FXML
    private TableView<OrderItemsEntity> salesTableView;
    
    private static int tmpC = 0;

    private List<Function<ProductsEntity, Void>> callbacks = new ArrayList<Function<ProductsEntity, Void>>();
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.refreshData();
        if(this.backLinkScreen != null) {
            this.backButton.setVisible(false);
        }
    }

    @FXML
    public void onBackButtonAction(ActionEvent event) {
        this.sc.loadScreen(this.backLinkScreen);
    }

    /* CALLBACKS */
    private Void runCallbacks(ProductsEntity prod) {
        for ( Function<ProductsEntity, Void> c : this.callbacks) {
            c.apply(prod);
        }
        this.callbacks.clear();
        return null;
    }

    public void addCallbackFunction(Function<ProductsEntity, Void> func) {
        this.callbacks.add(func);
    }

    // Setters
    public ProductDetailController setCurrentProduct(Integer id) {
        this.currentProduct = this.getProductById(id);
        
        this.refreshData();
        return this;
    }

    private void fillForm() {
        if(this.currentProduct != null) {
            this.productNameLabel.setText(this.currentProduct.getName());
            this.repeatedSaleLabel.setText(this.currentProduct.getRepeatedSale() == 0 ? "Ne" : this.currentProduct.getRepeatedSale() + ".");
            this.codeLabel.setText(this.currentProduct.getCode());
            this.nameLabel.setText(this.currentProduct.getName());
            this.descriptionLabel.setText(this.currentProduct.getDescription());
            this.countLabel.setText(this.currentProduct.getCount() + "");
            this.originalCountLabel.setText(", původně: " + this.currentProduct.getOriginalCount());
            this.priceLabel.setText(this.currentProduct.getTotalPrice() + "");
            this.partsLabel.setText(this.currentProduct.getParts() + "");
            this.salePriceLabel.setText(
                Utils.getPriceString(
                    Utils.getFinalSalePrice(
                        this.currentProduct.getTotalSalePrice()
                    )
                )
            );
            this.contractButton.setText(this.currentProduct.getContract().toString());
            Set<CategoriesEntity> ce = this.currentProduct.getCategories();
            if(ce.size() > 0) {
                CategoriesEntity cat = ce.iterator().next();
                this.categoryLabel.setText(cat.getName());
            }
            this.typeLabel.setText(this.currentProduct.getType().equals("K") ? "Komise" : "Naše");
            this.contentVBox.getChildren().removeAll();
            ProductsEntity p = this.currentProduct;
            while(true) {
                if(p.getRenewedFromProduct() == null) break;
                p = p.getRenewedFromProduct();
                GridPane gp = new GridPane();
                gp.setPrefWidth(1000);
                //gp.setPrefHeight(800);
                Label codeLabel = new Label(p.getCode());
                codeLabel.setFont(new Font(32));
                Label nameLabel = new Label("Opakovaný prodej: " + p.getRepeatedSale() + ".");
                nameLabel.setFont(new Font(20));
                Separator separator = new Separator();
                GridPane.setConstraints(nameLabel, 0, 1);
                gp.getChildren().addAll(codeLabel, nameLabel);
                this.contentVBox.getChildren().addAll(gp, separator);
            }
        }
    }

    @FXML
    public void onEditButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenProductsNew);
        ProductNewController controller = (ProductNewController)this.sc.getCurrentController();
        controller.addCallbackFunction(this::refresh);
        controller.addCallbackFunction(this::runCallbacks);
        controller.setProfit(this.currentProduct.getContract().getProfit());
        controller.setCurrentProduct(this.currentProduct.getId());
        controller.setBackLinkScreen(Main.screenProductsDetail);
    }
    
    private Void refresh(ProductsEntity product) {
    	this.currentProduct = product;
        refreshData();
        return null;
    }

    @FXML
    private void onContractButtonAction(ActionEvent event) {
        this.sc.loadScreen(Main.screenContractDetail);
        ContractDetailController controller = (ContractDetailController)this.sc.getCurrentController();
        controller.setBackLinkScreen(Main.screenProductsDetail);
        controller.setCurrentContract(this.currentProduct.getContract().getId());
    }

    @Override
    public void refreshData() {
        this.rootCategory = this.categoryService.getCategoryByName(rootCategoryName);
        this.fillForm();
        
        Utils.populateSalesTableView(this.salesTableView, this.getProductOrders());

    }

    public void cleanData() {
        this.currentProduct = null;
        this.refreshData();
    }
    
}
