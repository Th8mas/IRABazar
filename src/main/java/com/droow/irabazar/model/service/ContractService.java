package com.droow.irabazar.model.service;

import com.droow.irabazar.model.entity.ContractsEntity;

import java.util.List;

public interface ContractService {
    public void addContract(ContractsEntity contract);

    public List<ContractsEntity> listContracts();

    public ContractsEntity getContractById(Integer id);

    public void removeContract(Integer id);

    public void updateContract(ContractsEntity contract);

    public void updateAllContracts();

    public void postponeContracts(Integer days);

    public void merge(ContractsEntity contract);
}
