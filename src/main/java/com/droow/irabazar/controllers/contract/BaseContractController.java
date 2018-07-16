package com.droow.irabazar.controllers.contract;

import com.droow.irabazar.controllers.product.BaseProductController;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.CustomersEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.model.service.ContractService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by droow on 24.8.16.
 */
public abstract class BaseContractController extends BaseProductController {
    protected ContractsEntity currentContract;

    protected ObservableList<ContractsEntity> contractsList = FXCollections.observableArrayList();

    @Autowired
    protected ContractService contractService;

    protected ContractsEntity getContractById(Integer id) {
        return contractService.getContractById(id);
    }

    public ObservableList<ContractsEntity> getContractsList() {
        if (!contractsList.isEmpty()) contractsList.clear();
        contractsList = FXCollections.observableList(contractService.listContracts());
        return contractsList;
    }

    public void setCurrentContract(Integer id) {
        this.currentContract = this.getContractById(id);
        this.refreshData();
    }
}
