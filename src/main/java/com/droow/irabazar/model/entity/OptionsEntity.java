package com.droow.irabazar.model.entity;

import javax.persistence.*;

import com.droow.irabazar.utils.Utils;

import java.io.Serializable;

/**
 * Created by droow on 8.11.15.
 */
@NamedQueries({
    @NamedQuery(
        name = "findOptionByName",
        query = "SELECT o FROM OptionsEntity o where o.name = :name"
    )
})
@Entity
@Table(name = "options")
public class OptionsEntity implements Serializable {
    private int id;
    private String name;
    private String value;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "id", unique = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "value", unique = false, nullable = false)
    public String getValue() {
        return value;
    }

    public void setIntValue(String value) throws Exception {
    	try {
			this.value = "" + Utils.checkIntNumber(value);
    	} catch (Exception e){
    		throw new Exception("Hodnota '" + name + "' - " + e.getMessage());
    	}
    }
    
    public void setDoubleValue(String value) throws Exception {
    	try {
			this.value = "" + Utils.checkDoubleNumber(value);
    	} catch (Exception e){
    		throw new Exception("Hodnota '" + name + "' - " + e.getMessage());
    	}
    }
    
    public void setIntValue(String value, String defaultValue) throws Exception {
    	try {
			this.value = "" + Utils.checkIntNumber(value);
    	} catch (Exception e){
    		this.value = defaultValue;
    		throw new Exception("Zadána neplatná hodnota, změna nebude mít efekt. Zadávejte pouze celočíselné hodnoty");
    	}
    }
    
    public void setDoubleValue(String value, String defaultValue) throws Exception {
    	try {
			this.value = "" + Utils.checkDoubleNumber(value);
    	} catch (Exception e){
    		this.value = defaultValue;
    		throw new Exception("Zadána neplatná hodnota, změna nebude mít efekt. Zadávejte pouze číselné hodnoty");
    	}
    }
    
    public void setValue(String value){
    	this.value = value;
    }

    @Transient
    public Integer getIntValue() {
        return Integer.parseInt(this.value);
    }

    @Transient
    public Double getDoubleValue() {
        return Double.valueOf(this.value);
    }
    
    public void setIntValue(Integer value) {
        this.value = value.toString();
    }

}
