package com.droow.irabazar.model.service;

import com.droow.irabazar.model.dao.ContractDAO;
import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class ContractServiceImpl implements ContractService {
    @Autowired
    private ContractDAO contractDAO;

    @Override
    public void addContract(ContractsEntity contract) {
        contractDAO.addContract(contract);
    }

    @Override
    public void merge(ContractsEntity contract) {
        contractDAO.merge(contract);
    }

    @Override
    public List<ContractsEntity> listContracts() {
        return contractDAO.listContract();
    }

    @Override
    public ContractsEntity getContractById(Integer id) { return contractDAO.getContractById(id); }

    @Override
    public void removeContract(Integer id) {
        contractDAO.removeContract(id);
    }

    @Override
    public void updateContract(ContractsEntity contract) {
        contractDAO.updateContract(contract);
    }

    @Override
    public void updateAllContracts() {
        List<ContractsEntity> contracts = this.listContracts();
        for(ContractsEntity contract : contracts) {
            if(contract.updateStatus()) {
                this.contractDAO.updateContract(contract);
            }
        }
    }

    @Override
    public void postponeContracts(Integer days) {
        List<ContractsEntity.Status> statuses = new ArrayList<>(4);
        statuses.add(ContractsEntity.Status.NEW);
        statuses.add(ContractsEntity.Status.WAITING_FOR_PAYOUT);
        statuses.add(ContractsEntity.Status.NOT_SOLD);
        //statuses.add(ContractsEntity.Status.EXPIRED);
        List<ContractsEntity> contracts = this.contractDAO.listContractByStatus(statuses, false);
        for(ContractsEntity contract : contracts) {
            Date to = contract.getDateTo();
            Date toPickup = contract.getDateToPickup();

            Calendar cal = Calendar.getInstance();
            cal.setTime(to);
            cal.add(Calendar.DATE, days);
            contract.setDateTo(new Timestamp(cal.getTime().getTime()));

            Timestamp pickupDate = Utils.getFirstWorkingDay(
                new Timestamp(toPickup.getTime())
            );
            contract.setDateToPickup(pickupDate);

            this.updateContract(contract);
        }
    }
}