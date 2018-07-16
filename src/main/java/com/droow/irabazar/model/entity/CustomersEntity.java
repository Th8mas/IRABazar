package com.droow.irabazar.model.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * Created by droow on 8.11.15.
 */
@NamedQueries({
    @NamedQuery(
        name = "findCustomersAfterDate",
        query = "SELECT COUNT(o.id) FROM CustomersEntity o WHERE o.created >= :created"
    )
})
@Entity
@Table(name = "customers")
public class CustomersEntity extends BaseEntity {
    private int id;
    private String code;
    private String email;
    private String firstname;
    private String lastname;
    private String phone;
    private String address;
    private String ico;
    private String dico;
    private Date birthdate;
    private String opNumber;
    private String opAuthority;
    private Collection<ContractsEntity> contracts;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public CustomersEntity setId(int id) {
        this.id = id;
        return this;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public CustomersEntity setCode(String code) {
        this.code = code;
        return this;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public CustomersEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    @Basic
    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public CustomersEntity setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    @Basic
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public CustomersEntity setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    @Basic
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public CustomersEntity setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Basic
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public CustomersEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    @Basic
    @Column(name = "ico")
    public String getIco() {
        return ico;
    }

    public CustomersEntity setIco(String ico) {
        this.ico = ico;
        return this;
    }

    @Basic
    @Column(name = "dico")
    public String getDico() {
        return dico;
    }

    public CustomersEntity setDico(String dico) {
        this.dico = dico;
        return this;
    }

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    public Collection<ContractsEntity> getContracts() {
        return contracts;
    }

    public CustomersEntity setContracts(Collection<ContractsEntity> contracts) {
        this.contracts = contracts;
        return this;
    }

    @Basic
    @Column(name = "birthdate")
    public Date getBirthdate() {
        return birthdate;
    }

    @Transient
    public String getFormatedBirthdate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
        return dateFormat.format(this.birthdate.getTime());
    }

    public CustomersEntity setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    @Basic
    @Column(name = "op")
    public String getOpNumber() {
        return opNumber;
    }

    public CustomersEntity setOpNumber(String opNumber) {
        this.opNumber = opNumber;
        return this;
    }

    @Basic
    @Column(name = "opauthority")
    public String getOpAuthority() {
        return opAuthority;
    }

    public CustomersEntity setOpAuthority(String opAuthority) {
        this.opAuthority = opAuthority;
        return this;
    }

    @Override
    public String toString() {
        return this.code + " - " + this.firstname + " " + this.lastname;
    }
}
