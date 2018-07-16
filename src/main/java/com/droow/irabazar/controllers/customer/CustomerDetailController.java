package com.droow.irabazar.controllers.customer;

import com.droow.irabazar.Main;
import com.droow.irabazar.controllers.BackButtonDefined;
import com.droow.irabazar.controllers.contract.ContractDetailController;
import com.droow.irabazar.controllers.contract.ContractPayoutController;
import com.droow.irabazar.controllers.product.ProductDetailController;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.OrderItemsEntity;
import com.droow.irabazar.utils.Utils;
import com.droow.irabazar.model.entity.CustomersEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by droow on 8.11.15.
 */
@Component
public class CustomerDetailController extends BaseCustomerController implements Initializable, BackButtonDefined {

    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerToPayoutLabel;
    @FXML
    private Label codeLabel;

    @FXML
    private TextField icoInput;
    @FXML
    private TextField dicoInput;
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField phoneInput;
    @FXML
    private TextField addressInput;
    @FXML
    private TextField opInput;
    @FXML
    private TextField opAuthorityInput;

    @FXML
    private DatePicker bdayInput;

    @FXML
    private Button customerPayoutButton;
    @FXML
    private Button backButton;

    @FXML
    private TableView<ProductsEntity> customerProductsListTableView;
    @FXML
    private TableView<ContractsEntity> customerContractsListTableView;

    private List<Function<CustomersEntity, Void>> callbacks = new ArrayList<Function<CustomersEntity, Void>>();

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
    private void onSaveButtonAction(ActionEvent event) {
        CustomersEntity customer = null;
        if(this.currentCustomer != null) {
            customer = this.currentCustomer;
        } else {
            customer =  new CustomersEntity();
            customer.setCreated(new Timestamp(System.currentTimeMillis()));
        }

        customer.setIco(this.icoInput.getText())
                .setDico(this.dicoInput.getText())
                .setFirstname(this.firstNameInput.getText())
                .setLastname(this.lastNameInput.getText())
                .setEmail(this.emailInput.getText())
                .setPhone(this.phoneInput.getText())
                .setAddress(this.addressInput.getText())
                .setBirthdate(Utils.getDateFromLocalDate(this.bdayInput.getValue()))
                .setOpNumber(this.opInput.getText())
                .setCode(this.codeLabel.getText())
                .setOpAuthority(this.opAuthorityInput.getText());

        if(this.currentCustomer != null) {
            this.customerService.updateCustomer(customer);
            Utils.showInfoNotification("Zákazník úspěšně upraven!");
        } else {
            this.customerService.addCustomer(customer);
            Utils.showInfoNotification("Nový zákazník úspěšně uložen!");
        }

        this.currentCustomer = customer;
        this.runCallbacks(customer);
    }

    @FXML
    private void onResetButtonAction(ActionEvent event) {
        this.setCurrentCustomer(this.currentCustomer.getId());
    }

    @FXML
    private void onCustomerPayoutButtonAction(ActionEvent event) {
        List<ContractsEntity> contracts = this.currentCustomer.getContracts().stream().filter(
            cont ->
                cont.getStatus() != ContractsEntity.Status.TERMINATED
                && cont.getStatus() != ContractsEntity.Status.RENEWED
                && cont.getStatus() != ContractsEntity.Status.PAID
        ).collect(Collectors.toList());
        this.sc.loadScreen(Main.screenContractPayout);
        ContractPayoutController controller = (ContractPayoutController)this.sc.getCurrentController();
        controller.setBackLinkScreen(Main.screenCustomersDetail);
        controller.setCurrentContract(contracts.iterator().next().getId());
    }

    public void setCurrentCustomer(Integer id) {
        this.currentCustomer = this.getCustomerById(id);

        this.refreshData();
    }

    private void populateTable() {
        if(this.currentCustomer != null) {
            List<ContractsEntity> contracts = this.currentCustomer.getContracts().stream().filter(
                cont ->
                    cont.getStatus() != ContractsEntity.Status.TERMINATED
                    && cont.getStatus() != ContractsEntity.Status.RENEWED
                    && cont.getStatus() != ContractsEntity.Status.PAID
            ).collect(Collectors.toList());
            List<ProductsEntity> products = new ArrayList<ProductsEntity>();
            for (ContractsEntity contract : contracts) {
                products.addAll(contract.getProducts());
            }
            Utils.populateProductTableView(
                    this.customerProductsListTableView,
                    FXCollections.observableList(products),
                    (observableValue, oldValue, newValue) -> {
                        //Check whether item is selected and set value of selected item to Label
                        if (customerProductsListTableView.getSelectionModel().getSelectedItem() != null) {
                            this.sc.loadScreen(Main.screenProductsDetail);
                            ProductDetailController controller = (ProductDetailController) this.sc.getCurrentController();
                            controller.setBackLinkScreen(Main.screenCustomersDetail);
                            controller.setCurrentProduct(newValue.getId());
                        }
                    },
                    null,
                    null,
                    true
            );

            Utils.populateContractsTableView(
                this.customerContractsListTableView,
                FXCollections.observableList(contracts),
                (observableValue, oldValue, newValue) -> {
                    //Check whether item is selected and set value of selected item to Label
                    if (this.customerContractsListTableView.getSelectionModel().getSelectedItem() != null) {
                        this.sc.loadScreen(Main.screenContractDetail);
                        ContractDetailController controller = (ContractDetailController) this.sc.getCurrentController();
                        controller.setBackLinkScreen(Main.screenCustomersDetail);
                        controller.setCurrentContract(newValue.getId());
                    }
                },
                null
            );
        } else {
            Utils.populateProductTableView(
                this.customerProductsListTableView,
                FXCollections.observableList(new ArrayList<ProductsEntity>()), null, null, null, false);
            Utils.populateContractsTableView(
                this.customerContractsListTableView,
                FXCollections.observableList(new ArrayList<ContractsEntity>()), null, null);
        }
    }

    private void fillForm() {
        if(this.currentCustomer != null) {
            this.customerNameLabel.setText(this.currentCustomer.getFirstname() + " " + this.currentCustomer.getLastname());
            this.codeLabel.setText(this.currentCustomer.getCode());

            this.icoInput.setText(this.currentCustomer.getIco());
            this.dicoInput.setText(this.currentCustomer.getDico());
            this.firstNameInput.setText(this.currentCustomer.getFirstname());
            this.lastNameInput.setText(this.currentCustomer.getLastname());
            this.emailInput.setText(this.currentCustomer.getEmail());
            this.phoneInput.setText(this.currentCustomer.getPhone());
            this.addressInput.setText(this.currentCustomer.getAddress());
            this.bdayInput.setValue(Utils.getLocalDateFromDate(this.currentCustomer.getBirthdate()));
            this.opInput.setText(this.currentCustomer.getOpNumber());
            this.opAuthorityInput.setText(this.currentCustomer.getOpAuthority());

            /*
            if (!this.currentCustomer.getProductsesById().isEmpty()) {
                this.populateTable();
            }
            */
        } else {
            this.codeLabel.setText(this.customerService.getCustomerCode());
            this.customerNameLabel.setText(" nový");

            this.icoInput.setText("");
            this.dicoInput.setText("");
            this.firstNameInput.setText("");
            this.lastNameInput.setText("");
            this.emailInput.setText("");
            this.phoneInput.setText("");
            this.addressInput.setText("");
            this.bdayInput.setValue(null);
            this.opInput.setText("");
            this.opAuthorityInput.setText("");
        }
    }

    private void runCallbacks(CustomersEntity customer) {
        for ( Function<CustomersEntity, Void> c : this.callbacks) {
            c.apply(customer);
        }
        this.callbacks.clear();
    }

    public void addCallbackFunction(Function<CustomersEntity, Void> func) {
        this.callbacks.add(func);
    }

    @Override
    public void refreshData() {
        this.fillForm();
        this.populateTable();
        if(this.currentCustomer != null && this.currentCustomer.getContracts().size() > 0) {
            List<ContractsEntity> notPaidContracts = this.currentCustomer.getContracts().stream().filter(
                contractsEntity ->
                    contractsEntity.getStatus() == ContractsEntity.Status.NEW
                    || contractsEntity.getStatus() == ContractsEntity.Status.WAITING_FOR_PAYOUT
            ).collect(Collectors.toList());
            Double totalPayout = 0.0d;
            for (ContractsEntity contract : notPaidContracts) {
                totalPayout += contract.totalAmountToPayout();
            }
            //contract.getProducts().stream().filter(product -> product.getCount() != 0).collect(Collectors.toList())
            this.customerToPayoutLabel.setText(Utils.getPriceString(totalPayout));
        } else {
            this.customerToPayoutLabel.setText("");
            this.customerPayoutButton.setVisible(false);
        }
    }

    public void cleanData() {
        this.currentCustomer = null;
        this.refreshData();
    }

}
