package com.droow.irabazar.controllers.order;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.product.BaseProductController;
import com.droow.irabazar.controllers.product.ProductDetailController;
import com.droow.irabazar.model.entity.OrdersEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Angie
 */
@Component
public class OrderIndexController extends BaseOrderController implements Initializable {

    @FXML
    private TableView<OrdersEntity> ordersListTableView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.refreshData();
    }

    private void populateTable() {
    	Utils.populateOrdersTableView(this.ordersListTableView, this.ordersList,
            (observableValue, oldValue, newValue) -> {
                //Check whether item is selected and set value of selected item to Label
                if (ordersListTableView.getSelectionModel().getSelectedItem() != null) {
                    this.sc.loadScreen(Main.screenOrderDetail);
                    OrderDetailController controller = (OrderDetailController)this.sc.getCurrentController();
                    controller.setBackLinkScreen(Main.screenOrders);
                    controller.setCurrentOrder(newValue.getId());
                }
            }
        );
    }

    @Override
    public void refreshData() {
        this.ordersList = this.getOrdersList();
        this.populateTable();
    }

}