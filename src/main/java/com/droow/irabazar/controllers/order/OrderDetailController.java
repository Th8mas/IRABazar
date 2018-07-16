package com.droow.irabazar.controllers.order;

import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Created by droow on 9.3.16.
 */
@Component
public class OrderDetailController extends BaseOrderController implements Initializable, BackButtonDefined {

    @FXML
    private Label orderNumberLabel;
    @FXML
    private Label orderCodeLabel;
    @FXML
    private Label orderTotalPriceLabel;
    @FXML
    private Label orderCreatedLabel;

    @FXML
    private TableView<OrderItemsEntity> orderItemsTableView;

    @FXML
    private Button backButton;

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

    public void setCurrentOrder(Integer id) {
        this.currentOrder = this.getOrderById(id);

        this.refreshData();
    }

    @Override
    public void refreshData() {
        SimpleDateFormat dt1 = new SimpleDateFormat("dd. MM. YYYY");
        this.orderCreatedLabel.setText(dt1.format(this.currentOrder.getCreated()));

        this.orderNumberLabel.setText(this.currentOrder.getCode());
        this.orderTotalPriceLabel.setText(Utils.getPriceString(this.currentOrder.getTotalPrice()));

        ObservableList<OrderItemsEntity> orderItemsList = FXCollections.observableArrayList(this.currentOrder.getOrderItems());
        Utils.populateOrderItemsTableView(this.orderItemsTableView, true, orderItemsList, null, null);
    }

}
