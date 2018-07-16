package com.droow.irabazar.controllers.product;

import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.utils.Utils;
import com.droow.irabazar.model.entity.CategoriesEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Angie
 */
@Component
public class ProductCategoriesController extends BaseProductController implements Initializable, BackButtonDefined {

    @FXML
    private TreeView<CategoriesEntity> productCategoriesListTable;

    @FXML
    private TextField categoryNameInput;

    @FXML
    private ComboBox<CategoriesEntity> parentCategoryInput;

    @FXML
    private Button backButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert productCategoriesListTable != null : "fx:id=\"productCategoriesListTable\" was not injected: check your FXML file 'product/categories.fxml'.";
        if(this.categoryService.getCategoryByName(rootCategoryName) == null) {
            CategoriesEntity category = new CategoriesEntity();
            category.setName(rootCategoryName);
            this.categoryService.addCategory(category);
        }
        if(this.backLinkScreen != null) {
            this.backButton.setVisible(false);
        }
        this.refreshData();
    }

    @FXML
    public void onBackButtonAction(ActionEvent event) {
        this.sc.loadScreen(this.backLinkScreen);
    }

    private void populateTable() {
        Utils.populateCategoriesTreeView(this.productCategoriesListTable, this.rootCategory,
            (observable, oldValue, newValue) -> {
                if(newValue != null) {
                    parentCategoryInput.setValue(newValue.getValue());
                }
            }
        );
    }

    @FXML
    private void onSaveButtonAction(ActionEvent event) {
        CategoriesEntity category = new CategoriesEntity();

        category.setName(this.categoryNameInput.getText())
                .setParent(this.parentCategoryInput.getValue());

        this.categoryService.addCategory(category);
        Utils.showInfoNotification("Nová kategorie úspěšně uložena!");

        this.rootCategory = this.categoryService.getCategoryByName(rootCategoryName);
        this.categoryNameInput.setText("");
        this.refreshCategoriesList();
        this.refreshData();
    }

    @Override
    public void refreshData() {
        this.rootCategory = this.categoryService.getCategoryByName(rootCategoryName);
        // parentCategoryInput.setDisable(true);
        this.parentCategoryInput.setItems(this.getCategoriesList());
        this.populateTable();
    }

}