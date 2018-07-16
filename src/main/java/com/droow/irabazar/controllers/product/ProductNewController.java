package com.droow.irabazar.controllers.product;

import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.model.entity.CategoriesEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.utils.AutoCompleteComboBoxListener;
import com.droow.irabazar.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;

/**
 * Created by droow on 8.11.15.
 */
@Component
public class ProductNewController extends BaseProductController implements Initializable, BackButtonDefined {
    @FXML
    private TextField codeInput;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField countInput;
    @FXML
    private TextField partsInput;
    @FXML
    private TextField priceInput;
    @FXML
    private TextField originalCountInput;
    
    @FXML
    private Label originalCountLabel;
    @FXML
    private Label profitLabel;
    @FXML
    private Label salePriceLabel;
    @FXML
    private Label productNameLabel;

    @FXML
    private TextArea descriptionInput;

    @FXML
    private ComboBox<CategoriesEntity> categoryInput;

    @FXML
    private RadioButton typeInputKomise;
    @FXML
    private RadioButton typeInputNase;

    @FXML
    private TreeView<CategoriesEntity> productCategoriesTreeView;

    @FXML
    private Button backButton;

    private List<Function<ProductsEntity, Void>> callbacks = new ArrayList<Function<ProductsEntity, Void>>();

    private Integer productCodeStep = 0;

    private Integer profit;

    private boolean temporaryProduct = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(this.productCodeStep == 0) {
            this.productCodeStep = 1;
        }

        this.refreshData();

        this.priceInput.textProperty().addListener((observable, oldValue, newValue) -> {
        	try {
	            double salePrice = getSalePrice(Double.parseDouble(newValue));
	            this.salePriceLabel.setText(
	                Utils.getPriceString(salePrice)
	            );
        	} catch (Exception e){}
        });

        this.categoryInput.getSelectionModel().selectNext();
  
        if(this.backLinkScreen != null) {
            this.backButton.setVisible(false);
        }
        
        this.rootCategory = this.categoryService.getCategoryByName(rootCategoryName);
        this.categoryInput.setItems(this.getCategoriesList());
        
        new AutoCompleteComboBoxListener<>(this.categoryInput);
        
        Utils.populateCategoriesTreeView(this.productCategoriesTreeView, this.rootCategory, (new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<CategoriesEntity> selectedItem = (TreeItem<CategoriesEntity>) newValue;
                if(selectedItem != null) {
                    categoryInput.setValue(selectedItem.getValue());
                }
            }

        }));
    }

    @FXML
    public void onBackButtonAction(ActionEvent event) {
        this.sc.loadScreen(this.backLinkScreen);
    }

    @FXML
    private void onSaveButtonAction(ActionEvent event) {
        boolean newProduct = true;
    	ProductsEntity product = null;
        
        if(this.currentProduct != null) {
            product = this.currentProduct;
            newProduct = false;
        } else {
            product = new ProductsEntity();
            product.setCreated(new Timestamp(System.currentTimeMillis()));
        }
        
        Integer count = 0;
        try {
        	count = Utils.checkIntNumber(this.countInput.getText());
        } catch (Exception e){
        	Utils.showErrorDialog("Chyba", "Údaj 'počet' - " + e.getMessage(), null);
        	return;
        }
        String type = this.typeInputKomise.isSelected() ? "K" : "N";

        Double price = 0D;
        try {
        	price = Utils.checkDoubleNumber(this.priceInput.getText());
        } catch (Exception e){
        	Utils.showErrorDialog("Chyba", "Údaj 'cena' - " + e.getMessage(), null);
        	return;
        }
        
        Double salePrice = getSalePrice(price);

        Double storageFee = Utils.getStorageFeeFromSalePrice(
                salePrice,
                Integer.parseInt(optionsList.get("storageMinimumFeeInCrowns")),
                Integer.parseInt(optionsList.get("storageFeeInPercent")));
        Double withdrawalFee = Utils.getWithdrawalFeeFromSalePrice(
                salePrice,
                Integer.parseInt(optionsList.get("withdrawalFeeInPercent"))
        );
        
        Integer curCount = count;
        count = count > 0 ? count : 1;
        
        Integer originalCount = newProduct ? curCount : Integer.valueOf(originalCountInput.getText());
        
        if(!newProduct){
        	int delta = product.getOriginalCount() - originalCount;
        	
        	salePrice -= delta * product.getSalePrice();
        	price -= delta * product.getPrice();
        }
        		
        product.setCode(this.codeInput.getText())
                .setName(this.nameInput.getText())
                .setDescription(this.descriptionInput.getText())
                .setTotalPrice(price)
                .setTotalSalePrice(salePrice)
                .setTotalStorageFee(storageFee)
                .setPrice((int) Math.ceil(price/count))
                .setSalePrice((int) Math.ceil(salePrice/count))
                .setStorageFee(storageFee/count)
                .setWithdrawalFee(withdrawalFee/count)
                .setCount(curCount)
                .setOriginalCount(originalCount)
                .setParts(Integer.parseInt(this.partsInput.getText()))
                .setType(type);

        // Set product category ( TODO: allow multiple categories )
        Set<CategoriesEntity> ce = new HashSet<CategoriesEntity>();
        
        CategoriesEntity category = this.categoryInput.getValue();
        if(category == null){
        	ce.add(rootCategory);
        }
        else {
        	ce.add(category);
        }
        product.setCategories(ce);
        
        if(this.currentProduct != null && !this.temporaryProduct) {
            this.productService.updateProduct(product);
            Utils.showInfoNotification("Produkt úspěšně upraven!");
        } else {
            //product.setOriginalCount(Integer.parseInt(this.countInput.getText()));
        }
        this.currentProduct = null;
        this.temporaryProduct = false;
        this.runCallbacks(product);
    }

    private double getSalePrice(double originalPrice){
    	Double salePrice = 100 * originalPrice / (100 - this.profit);
        return Utils.getFinalSalePrice(salePrice);
    }
    
    @FXML
    private void onResetButtonAction(ActionEvent event) {
        this.setCurrentProduct(this.currentProduct.getId());
    }
    
    // Setters
    public ProductNewController setCurrentProduct(Integer id) {
        this.currentProduct = this.getProductById(id);
        this.temporaryProduct = false;
        
        if(this.profit == null){
        	this.profit = (int)((this.currentProduct.getSalePrice() - this.currentProduct.getPrice()) / this.currentProduct.getSalePrice() * 100);
        }
        
        this.refreshData();
        return this;
    }

    public ProductNewController setCurrentProduct(ProductsEntity product) {
        this.currentProduct = product;
        this.temporaryProduct = true;
        
        if(this.profit == null){
        	this.profit = (int)((this.currentProduct.getSalePrice() - this.currentProduct.getPrice()) / this.currentProduct.getSalePrice() * 100);
        }
        
        this.refreshData();
        return this;
    }
    
    public ProductNewController setProductCodeStep(Integer step) {
        this.productCodeStep = step;
        this.refreshData();
        return this;
    }

    public ProductNewController setProfit(Integer profit) {
        this.profit = profit;
        return this;
    }

    // Callbacks
    private void runCallbacks(ProductsEntity prod) {
        for ( Function<ProductsEntity, Void> c : this.callbacks) {
            c.apply(prod);
        }
        this.callbacks.clear();
    }

    public void addCallbackFunction(Function<ProductsEntity, Void> func) {
        this.callbacks.add(func);
    }

    private void fillForm() {
        if(this.currentProduct != null) {
            this.productNameLabel.setText(this.currentProduct.getName());

            this.codeInput.setText(this.currentProduct.getCode());
            this.nameInput.setText(this.currentProduct.getName());
            this.descriptionInput.setText(this.currentProduct.getDescription());
            this.countInput.setText(this.currentProduct.getCount() + "");
            this.originalCountInput.setText(String.valueOf(this.currentProduct.getOriginalCount()));
            this.priceInput.setText(this.currentProduct.getTotalPrice() + "");
            this.salePriceLabel.setText(
                Utils.getPriceString(
                    Utils.getFinalSalePrice(
                        this.currentProduct.getTotalSalePrice()
                    )
                )
            );
            
            Set<CategoriesEntity> ce = this.currentProduct.getCategories();
            if(ce.size() > 0) {
                CategoriesEntity cat = ce.iterator().next();
                this.categoryInput.setValue(cat);
            }
            if(this.currentProduct.getType().equals("K")) {
                this.typeInputKomise.setSelected(true);
                this.typeInputNase.setSelected(false);
            } else {
                this.typeInputKomise.setSelected(false);
                this.typeInputNase.setSelected(true);
            }
        } else {
            this.codeInput.setText(this.productService.getProductCode(this.productCodeStep));
            this.nameInput.setText("");
            this.descriptionInput.setText("");
            this.countInput.setText("1");
            this.partsInput.setText("1");
            this.priceInput.setText("");
            this.categoryInput.setValue(null);
            this.typeInputKomise.setSelected(true);
            this.typeInputNase.setSelected(false);
            this.salePriceLabel.setText(Utils.getPriceString(Utils.getFinalSalePrice(0)));
            
            if(this.profit != null) {
                this.profitLabel.setText(this.profit.toString());
            }
        }
    }

    @Override
    public void refreshData() {
        this.typeInputKomise.setSelected(true);
        this.typeInputNase.setSelected(false);
        this.fillForm();
        
        if(this.currentProduct != null){
        	originalCountLabel.setVisible(true);
        	originalCountInput.setVisible(true);
        }
        else {
        	originalCountLabel.setVisible(false);
        	originalCountInput.setVisible(false);
        }
    }

    public void cleanData() {
        this.currentProduct = null;
        this.refreshData();
    }

}
