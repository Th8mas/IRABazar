package com.droow.irabazar.controllers.product;

import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.utils.AutoCompleteComboBoxListener;
import com.droow.irabazar.utils.Utils;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class ProductPayoutController extends BaseProductController implements Initializable, BackButtonDefined {

    @FXML
    private ComboBox<ProductsEntity> productInput;

    @FXML
    private Label priceLabel;
    
    @FXML
    private Button backButton;
    
    private ProductsEntity selectedProduct;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.refreshData();
        if(this.backLinkScreen != null) {
            this.backButton.setVisible(true);
        }
        else {
        	this.backButton.setVisible(false);
        }
    }

    @FXML
    public void onBackButtonAction(ActionEvent event) {
        this.sc.loadScreen(this.backLinkScreen);
    }

    @FXML
    public void onProductPayout(ActionEvent event) {
        if (!Utils.showConfirmationDialog("Potvrďte akci", "Chcete vyplatit vybrané zboží v částce " + priceLabel.getText() + "?", "")) return;
        
        if(selectedProduct == null){
        	Utils.showErrorDialog("Chyba", "Nebyla vybrána žádná položka", "");
        	return;
        }
        
        selectedProduct.setStatus(ProductsEntity.Status.PAID);
        
        this.productService.updateProduct(selectedProduct);
        
        refreshData();
    }
    
    /* CALLBACKS */

    @Override
    public void refreshData() {
    	
    	List<ProductsEntity> prods = this.getAllProductsList().stream().filter(
            product -> (product.getStatus() == ProductsEntity.Status.SOLD && product.getCount() == 0)
        ).collect(Collectors.toList());
    	
        this.productInput.setItems(FXCollections.observableArrayList(prods));
        new AutoCompleteComboBoxListener<>(this.productInput);
        
        this.productInput.getSelectionModel().selectedItemProperty().addListener(
                (ov, t, t1) -> {
                    ProductsEntity prod = (ProductsEntity) t1;
                    selectedProduct = t1;
                    priceLabel.setText(prod.getTotalPrice() + "Kč");
                }
        );
        
        priceLabel.setText("");
    }

}
