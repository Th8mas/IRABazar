package com.droow.irabazar.model.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by droow on 8.11.15.
 */
@NamedQueries({
    @NamedQuery(
        name = "findContractsByStatus",
        query = "SELECT c FROM ContractsEntity c WHERE c.status IN :status AND c.paid = :paid"
    )
})
@Entity
@Table(name = "contracts")
public class ContractsEntity extends BaseEntity {
    private int id;
    private int customerId;
    private String code;
    private Double totalPrice;
    private Timestamp dateFrom;
    private Timestamp dateTo;
    private Timestamp dateToPickup;
    private Collection<ProductsEntity> products = new HashSet<ProductsEntity>();
    private CustomersEntity customer;
    private Status status;
    private String content;
    private String payoutContent;
    private Integer profit;
    private Integer daysToKeep;
    private Boolean paid;
    private ContractsEntity renewedContract;
    private ContractsEntity renewedFromContract;

    public enum Status {
        // draft -> NEW
        CONCEPT ("Koncept"),
        // nová
        // - po doběhnutí termínu k nasazení snížené ceny -> NOT_SOLD
        // - při prodání před termínem -> WAITING_FOR_PAYOUT
        // - když se staví hned ten den ještě před lhůtou-> RENEWED ?
        NEW ("Nová"),
        // neprodané po termínu (běží 10dní lhůta)
        // - po 10 dnech -> EXPIRED
        // - v průběu 10 dní vyzvednuto -> RENEWED | TERMINATED
        NOT_SOLD ("Neprodáno"),
        // neprodané po termínu a lhůtě 10 dní (penalizace)
        // - po penalizaci -> TERMINATED | RENEWED
        EXPIRED ("Penalizováno"),
        // obnovená - konečný stav
        RENEWED ("Obnovena"),
        // prodané ale nevyplacené
        // - po vyzvednutí -> PAID
        // - při nevyzvednutí do 10 dní penalizace -> EXPIRED
        // - možnost přidat dodatek místo normální smlouvy -> RENEWED ?
        WAITING_FOR_PAYOUT ("Čeká na vyplacení"),
        // vyplacená - konečný stav
        PAID ("Vyplacená"),
        // ukončená/odstoupení - konečný stav
        TERMINATED ("Ukončená");

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

    public ContractsEntity() {
        this.status = Status.CONCEPT;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public ContractsEntity setId(int id) {
        this.id = id;
        return this;
    }

    @Basic
    @Column(name = "totalprice")
    public Double getTotalPrice() {
        return totalPrice;
    }

    public ContractsEntity setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    @Basic
    @Column(name = "daystokeep")
    public Integer getDaysToKeep() {
        return daysToKeep;
    }

    public ContractsEntity setDaysToKeep(Integer daysToKeep) {
        this.daysToKeep = daysToKeep;
        return this;
    }

    @Basic
    @Column(name = "datefrom")
    public Timestamp getDateFrom() {
        return dateFrom;
    }

    public ContractsEntity setDateFrom(Timestamp date) {
        this.dateFrom = date;
        return this;
    }

    @Basic
    @Column(name = "dateto")
    public Timestamp getDateTo() {
        return dateTo;
    }

    public ContractsEntity setDateTo(Timestamp dateTo) {
        this.dateTo = dateTo;
        return this;
    }

    @Basic
    @Column(name = "datetopickup")
    public Timestamp getDateToPickup() {
        return dateToPickup;
    }

    public ContractsEntity setDateToPickup(Timestamp dateToPickup) {
        this.dateToPickup = dateToPickup;
        return this;
    }

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "contract", fetch = FetchType.EAGER)
    public Collection<ProductsEntity> getProducts() {
        return products;
    }

    public ContractsEntity setProducts(Collection<ProductsEntity> products) {
        this.products = products;
        return this;
    }

    public ContractsEntity addProduct(ProductsEntity prod) {
        this.products.add(prod);
        return this;
    }

    @Basic
    @Column(name = "customer_id", insertable = false, updatable = false)
    public int getCustomerId() {
        return customerId;
    }

    public ContractsEntity setCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    public CustomersEntity getCustomer() {
        return customer;
    }

    public ContractsEntity setCustomer(CustomersEntity customersByCustomerId) {
        this.customer = customersByCustomerId;
        return this;
    }

    @Basic
    @Column(name = "status")
    public Status getStatus() {
        return status;
    }

    public ContractsEntity setStatus(Status status) {
        this.status = status;
        return this;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public ContractsEntity setCode(String code) {
        this.code = code;
        return this;
    }

    @Basic
    @Type(type="text")
    @Column(name = "content")
    public String getContent() {
        return content;
    }
    
    @Basic
    @Type(type="text")
    @Column(name = "payoutContent")
    public String getPayoutContent() {
        return payoutContent;
    }

    @Basic
    @Column(name = "profit")
    public Integer getProfit() {
        return profit;
    }

    public ContractsEntity setProfit(Integer profit) {
        this.profit = profit;
        return this;
    }

    @Basic
    @Column(name = "paid")
    public Boolean getPaid() {
        return paid;
    }

    public ContractsEntity setPaid(Boolean paid) {
        this.paid = paid;
        return this;
    }

    public ContractsEntity setContent(String content) {
        this.content = content;
        return this;
    }
    
    public ContractsEntity setPayoutContent(String content) {
        this.payoutContent = content;
        return this;
    }

    public boolean updateStatus() {
        switch(status) {
            case NEW:
                boolean shouldBePaidOut = true;

                if(this.getProducts().size() == 0) {
                    // error?
                    shouldBePaidOut = false;
                } else {
                    for (ProductsEntity prod : this.getProducts()) {
                        if (prod.getCount() > 0) {
                            shouldBePaidOut = false;
                            break;
                        }
                    }
                }
                if(Calendar.getInstance().getTime().after(this.dateToPickup)) {
                    // if not picked up in 10 days after end, penalize
                    this.setStatus(Status.EXPIRED);
                    return true;
                } else if (shouldBePaidOut) {
                    this.setStatus(Status.WAITING_FOR_PAYOUT);
                    return true;
                } else if(Calendar.getInstance().getTime().after(this.dateTo)) {
                    this.setStatus(Status.NOT_SOLD);
                    return true;
                }
                break;
            case NOT_SOLD:
                if(Calendar.getInstance().getTime().after(this.dateToPickup)) {
                    this.setStatus(Status.EXPIRED);
                    return true;
                }
                break;
            case WAITING_FOR_PAYOUT:
                if(Calendar.getInstance().getTime().after(this.dateToPickup)) {
                    this.setStatus(Status.EXPIRED);
                    return true;
                }
                // Change to paid is manual
                break;
            case EXPIRED:
                // Change to paid or renewed is manual
                break;
            // Other statuses: TERMINATED, PAID, RENEWED
            default:
                // Nothing to do with this status anymore
                break;
        }
        return false;
    }

    public double totalAmountToPayout() {
        double totalPayout = 0.0D;
        Collection<ProductsEntity> contractProducts = getProducts();
        
        for (ProductsEntity prod : contractProducts) {
        	if(prod.getStatus() == ProductsEntity.Status.PAID) continue;
        	
            Collection<OrderItemsEntity> ois = prod.getOrderItems();
            for (OrderItemsEntity oi : ois) {
                totalPayout += oi.getTotalPrice();
            }
            
        }
        return totalPayout * 0.7;
    }

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="renewedContract", updatable= false)
    public ContractsEntity getRenewedContract() {
        return renewedContract;
    }

    public ContractsEntity setRenewedContract(ContractsEntity renewedContract) {
        this.renewedContract = renewedContract;
        return this;
    }

    @OneToOne
    @JoinColumn(name="renewedFromContract", updatable= false)
    public ContractsEntity getRenewedFromContract() {
        return renewedFromContract;
    }

    public ContractsEntity setRenewedFromContract(ContractsEntity renewedFromContract) {
        this.renewedFromContract = renewedFromContract;
        return this;
    }

    // DODATEK
        /*
        content += "9) <cena zak.>. případně <sníž.c.z.>v čl.1): částka k vyplacení zákazníkovi.\n";
        content += "10)<np> poplatek při neprodání věci ve výši >údaj N< 0 % sjednané prodejní\n";
        content += "   ceny, nejméně částka >údaj O<: 10 Kč.\n";
        content += "11)Smluvní pokuta ve výši >údaj P< 5.0 promile sjednané prodejní ceny věcí\n";
        content += "   za každý den prodletní\n";

        content += "14)Tento dodatek slouží jako potvrzení o pževzetí věcí k obstarání jejich\n";
        content += "   prodeje.\n";
        content += "15)Zkráceným uvedením konkretizovaných údajů v tomto dodatku není dotčen\n";
        content += "   text jednotlivých bodů základní smlouvy ani bodů této smlouvy v dodatku\n";
        content += "   neuvedených.\n";
        content += "18)Dodatek je vyhotoven ve dvou vyhotoveních, z nichž každá ze smluvních\n";
        content += "   stran obdrží po jednom.\n\n";*/

    @Override
    public String toString() {
        return code;
    }

	/**
	 * @param newValue
	 */
	public void removeProduct(ProductsEntity newValue) {
		this.products.remove(newValue);
	}
}
