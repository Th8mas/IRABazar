package com.droow.irabazar.model.dao;

/**
 * Created by droow on 8.11.15.
 */

import com.droow.irabazar.model.entity.ContractsEntity;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public class ContractDAOImpl extends AbstractDAO< ContractsEntity > implements ContractDAO {

    ContractDAOImpl() {
        this.setClazz(ContractsEntity.class);
    }

    public List<ContractsEntity> listContract() {
        List<ContractsEntity> list = entityManager.createQuery(
            "SELECT c FROM ContractsEntity c", ContractsEntity.class
        ).getResultList();
        return list;
    }

    public List<ContractsEntity> listContractByStatus(List<ContractsEntity.Status> status, boolean paid) {
        List<ContractsEntity> list = entityManager.createNamedQuery(
            "findContractsByStatus", ContractsEntity.class
        )
        .setParameter("status", status)
        .setParameter("paid", paid).getResultList();

        return list;
    }

    public void addContract(ContractsEntity contract) {
        this.create(contract);
    }

    public ContractsEntity getContractById(Integer id) {
        return this.findOne(id);
    }

    public void removeContract(Integer id) {
        this.deleteById(id);
    }

    public void updateContract(ContractsEntity contract) {
        this.update(contract);
    }
}
