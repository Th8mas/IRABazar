package com.droow.irabazar.model.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.droow.irabazar.model.entity.ProductsEntity.Status.NEW;

/**
 * Created by droow on 8.11.15.
 */
@NamedQueries(
    @NamedQuery(
        name = "findProductsAfterDate",
        query = "SELECT COUNT(p.id) FROM ProductsEntity p WHERE p.created >= :created"
    )
)
@Entity
@Table(name = "products")
public class ProductsEntity extends BaseEntity {
    private int id;
    private int contractId;
    private String code;
    private String name;
    private String description;
    // PRICES
    // Customer price
    private double price;
    private double totalPrice;
    // Sale price
    private double salePrice;
    private double totalSalePrice;
    private double currentSalePrice;
    // Storage fees
    private double storageFee;
    private double totalStorageFee;
    // Penalize
    private double penale;
    // Lower price
    private boolean hasLowerPrice;
    private double lowerPrice;
    private double totalLowerPrice;
    // Withdrawal fees
    private double withdrawalFee;
    //private double totalWithdrawalFee;
    private int repeatedSale;
    private String type;
    private int originalCount;
    private int count;
    private int parts;
    
    private Timestamp dateSold;
    
    private Status status;
    private Status status2;
    private ProductsEntity renewedFromProduct;
    private ProductsEntity renewedProduct;
    private ContractsEntity contract;

    private Set<CategoriesEntity> categories = new HashSet<>(0);
    private Set<OrderItemsEntity> orderItems = new HashSet<OrderItemsEntity>();

    public enum Status {
        NEW ("Nové"),
        RENEWED ("Obnoveno"),
        SOLD ("Prodáno"),
        PAID ("Vyplaceno"),
        RETURNED ("Vráceno"),
        PENALIZED ("Penalizováno");

        private final String name;

        private Status(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
    public enum Type { KOMISE, NASE }

    public ProductsEntity() {
        this.parts = 1;
        this.repeatedSale = 0;
        this.status = NEW;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public ProductsEntity setId(int id) {
        this.id = id;
        return this;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public ProductsEntity setCode(String code) {
        this.code = code;
        return this;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public ProductsEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() { return description; }

    public ProductsEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    @Basic
    @Column(name = "dateSold")
    public Timestamp getDateSold() {
        return dateSold;
    }
    
    public ProductsEntity setDateSold(Timestamp date) {
        this.dateSold = date;
    	return this;
    }

    public ProductsEntity setPrice(double price) {
        this.price = price;
        return this;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public ProductsEntity setType(String type) {
        this.type = type;
        return this;
    }

    @Basic
    @Column(name = "originalcount")
    public int getOriginalCount() {
        return originalCount;
    }

    public ProductsEntity setOriginalCount(int count) {
        this.originalCount = count;
        return this;
    }

    @Basic
    @Column(name = "count")
    public int getCount() {
        return count;
    }

    public ProductsEntity setCount(int count) {
        this.count = count;
        return this;
    }

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "status")
    public Status getStatus() {
        return status;
    }

    public ProductsEntity setStatus(Status status) {
        this.status = status;
        return this;
    }

    @Basic
    @Column(name = "contract_id", insertable = false, updatable = false)
    public int getContractId() {
        return contractId;
    }

    public ProductsEntity setContractId(int contractId) {
        this.contractId = contractId;
        return this;
    }

    @Basic
    @Column(name = "sale_price")
    public double getSalePrice() {
        return salePrice;
    }

    public ProductsEntity setSalePrice(double salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    @Basic
    @Column(name = "storage_fee")
    public double getStorageFee() {
        return storageFee;
    }

    public ProductsEntity setStorageFee(double storageFee) {
        this.storageFee = storageFee;
        return this;
    }

    @Basic
    @Column(name = "total_storage_fee")
    public double getTotalStorageFee() {
        return totalStorageFee;
    }

    public ProductsEntity setTotalStorageFee(double storageFee) {
        this.totalStorageFee = storageFee;
        return this;
    }

    @Basic
    @Column(name = "withdrawal_fee")
    public double getWithdrawalFee() {
        return withdrawalFee;
    }

    public ProductsEntity setWithdrawalFee(double withdrawalFee) {
        this.withdrawalFee = withdrawalFee;
        return this;
    }

    @Basic
    @Column(name = "is_lower_price")
    public boolean getHasLowerPrice() {
        return hasLowerPrice;
    }

    public ProductsEntity setHasLowerPrice(boolean is) {
        this.hasLowerPrice = is;
        return this;
    }

    @Basic
    @Column(name = "lower_price")
    public double getLowerPrice() {
        return lowerPrice;
    }

    public ProductsEntity setLowerPrice(double price) {
        this.lowerPrice = price;
        return this;
    }

    @Basic
    @Column(name = "total_price")
    public double getTotalPrice() {
        return totalPrice;
    }

    public ProductsEntity setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    @Basic
    @Column(name = "total_sale_price")
    public double getTotalSalePrice() {
        return totalSalePrice;
    }

    public ProductsEntity setTotalSalePrice(double totalSalePrice) {
        this.totalSalePrice = totalSalePrice;
        return this;
    }

    @Basic
    @Column(name = "total_lower_price")
    public double getTotalLowerPrice() {
        return totalLowerPrice;
    }

    public ProductsEntity setTotalLowerPrice(double totalLowerPrice) {
        this.totalLowerPrice = totalLowerPrice;
        return this;
    }

    @Basic
    @Column(name = "parts")
    public int getParts() {
        return parts;
    }

    public ProductsEntity setParts(int parts) {
        this.parts = parts;
        return this;
    }

    @Basic
    @Column(name = "repeated_sale")
    public int getRepeatedSale() {
        return repeatedSale;
    }

    public ProductsEntity setRepeatedSale(int repeatedSale) {
        this.repeatedSale = repeatedSale;
        return this;
    }

    @Basic
    @Column(name = "penale")
    public double getPenalty() {
        return penale;
    }

    public ProductsEntity setPenalty(double penale) {
        this.penale = penale;
        return this;
    }
    
    @Basic
    @Column(name = "price")
    public double getPrice() {
        return price;
    }

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false, updatable= false)
    public ContractsEntity getContract() {
        return contract;
    }

    public ProductsEntity setContract(ContractsEntity contractsByContractId) {
        this.contract = contractsByContractId;
        return this;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "product_categories", joinColumns = {
            @JoinColumn(name = "product_id", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "category_id", nullable = false, updatable = false) })
    public Set<CategoriesEntity> getCategories() {
        return this.categories;
    }

    public ProductsEntity setCategories(Set<CategoriesEntity> categories) {
        this.categories = categories;
        return this;
    }

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    public Set<OrderItemsEntity> getOrderItems() {
        return orderItems;
    }

    public ProductsEntity setOrderItems(Set<OrderItemsEntity> orderItems) {
        this.orderItems = orderItems;
        return this;
    }

    @OneToOne
    @JoinColumn(name="renewedFromProduct", updatable= false)
    public ProductsEntity getRenewedFromProduct() {
        return renewedFromProduct;
    }

    public ProductsEntity setRenewedFromProduct(ProductsEntity renewedFromProduct) {
        this.renewedFromProduct = renewedFromProduct;
        return this;
    }

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="renewedProduct", updatable= false)
    public ProductsEntity getRenewedProduct() {
        return renewedProduct;
    }

    public ProductsEntity setRenewedProduct(ProductsEntity renewedProduct) {
        this.renewedProduct = renewedProduct;
        return this;
    }

    @Transient
    public double getCurrentSalePrice() {
        return currentSalePrice;
    }

    public void setCurrentSalePrice(double currentSalePrice) {
        this.currentSalePrice = currentSalePrice;
    }

    @Override
    public String toString() {
        String code = this.getCode();
        return (!code.equals("") ? (code + " - ") : "") + this.getName();
    }

}
