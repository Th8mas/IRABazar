package com.droow.irabazar.model.dao;

/**
 * Created by droow on 8.11.15.
 */

import com.droow.irabazar.model.entity.OptionsEntity;
import com.droow.irabazar.model.service.OptionService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class OptionDAOImpl extends AbstractDAO < OptionsEntity > implements OptionDAO {

    OptionDAOImpl() {
        this.setClazz(OptionsEntity.class);
    }

    public void addOption(OptionsEntity option) {
        this.create(option);
    }

    public List<OptionsEntity> listOption() {
        List<OptionsEntity> list = entityManager.createQuery(
                "SELECT o FROM OptionsEntity o",
                OptionsEntity.class
        ).getResultList();
        return list;
    }

    public OptionsEntity getOptionById(Integer id) {
        return this.findOne(id);
    }

    public OptionsEntity getOptionByName(String name) {
        Query q = entityManager.createNamedQuery("findOptionByName", OptionsEntity.class)
                .setParameter("name", name);
        return (OptionsEntity) q.getSingleResult();
    }

    public void removeOption(Integer id) {
        this.deleteById(id);
    }

    public void updateOption(OptionsEntity option) {
        this.update(option);
    }
}
