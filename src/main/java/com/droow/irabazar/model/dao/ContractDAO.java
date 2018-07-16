package com.droow.irabazar.model.dao;

import com.droow.irabazar.model.entity.ContractsEntity;

import java.util.Collection;
import java.util.List;


/**
 * Created by droow on 8.11.15.
 */
public interface ContractDAO {
    public void addContract(ContractsEntity contract);
    public List<ContractsEntity> listContract();
    public List<ContractsEntity> listContractByStatus(List<ContractsEntity.Status> status, boolean paid);
    public ContractsEntity getContractById(Integer id);
    public void removeContract(Integer id);
    public void updateContract(ContractsEntity contract);
    public void merge(ContractsEntity contract);
}