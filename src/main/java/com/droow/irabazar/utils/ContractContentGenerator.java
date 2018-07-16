package com.droow.irabazar.utils;

import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.CustomersEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import org.stringtemplate.v4.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by droow on 16.12.16.
 */
public class ContractContentGenerator {
    private String content = "";
    private ContractsEntity contract;
    private HashMap<String, String> options;

    public static final String TEMPLATE_NEW = "new";
    public static final String TEMPLATE_RENEW = "renew";

    public ContractContentGenerator(ContractsEntity contract, HashMap<String, String> options) {
        this.contract = contract;
        this.options = options;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addContent(String content) {
        this.content += content;
    }

    public void addLine() {
        this.content += this.getLine();
    }

    private String getLine() {
        // width = 80 = 2 padding + 70 content + 2 padding
        return "  ---------------------------------------------------------------------------\n";
    }

    public static String getLine(int len) {
        return String.format("%0"+len+"d", 0).replace("0", "-");
    }

    public boolean generateNewContractTemplate(boolean repeatedSale) {
        try {
            CustomersEntity customer = contract.getCustomer();
            SimpleDateFormat dt1 = new SimpleDateFormat("dd.MM.YYYY");
            String toDate = dt1.format(contract.getDateTo());
            String fromDate = dt1.format(contract.getDateFrom());
            String pickupDate = dt1.format(contract.getDateToPickup());

            STGroup g = new STGroupDir("assets/templates");
            ST template = g.getInstanceOf("new");

            Collections.sort((List<ProductsEntity>)contract.getProducts(),
                (ProductsEntity p1, ProductsEntity p2) -> p1.getCode().compareTo(p2.getCode())
            );

            template.add("repeated_sale", repeatedSale ? "OPAKOVANÝ PRODEJ" : "");
            
            // Company
            template.add("company_name", options.get("comName"));
            template.add("company_ico", options.get("comIco"));
            template.add("company_dico", options.get("comDico"));
            template.add("company_address", options.get("comAddress"));
            template.add("company_phone", options.get("comPhone"));
            template.add("company_website", options.get("comWebsite"));
            // Contract
            template.add("contract_code", contract.getCode());
            template.add("contract_from_date", fromDate);
            template.add("contract_to_date", toDate);
            template.add("contract_pickup_date", pickupDate);
            
            template.add("contract_missing_fee", options.get("missingContractFeeInCrowns"));
            template.add("contract_days", contract.getDaysToKeep());
            template.add("contract_profit", contract.getProfit());
            // Products
            template.add("product_list", getProductList(contract.getProducts(), repeatedSale));
            template.add("product_list_small", getProductListSmall(contract.getProducts()));
            template.add("product_cards", getProductCards(contract.getProducts()));
            // Customer
            template.add("customer_code", customer.getCode());
            template.add("customer_name", customer.getLastname()+" "+customer.getFirstname());
            template.add("customer_birthdate", customer.getFormatedBirthdate());
            template.add("customer_op_number", customer.getOpNumber());
            template.add("customer_address", customer.getAddress());
            // Other
            template.add("global_lower_price_days", options.get("lowerPriceDaysLimit"));
            template.add("global_storage_fee_in_percent", options.get("storageFeeInPercent"));
            template.add("global_storage_fee_minimum", options.get("storageMinimumFeeInCrowns"));
            template.add("global_not_sold_fee_in_percent", options.get("notSoldDailyFeeInPercent"));
            template.add("global_not_sold_days_to_pay", options.get("notSoldDaysToPayFee"));
            template.add("global_withdrawal_fee", options.get("withdrawalFeeInPercent"));

            contract.setContent(template.render());

            return true;
        } catch (Exception e) {
            Utils.showExceptionDialog(e);
        }

        return false;
    }

    public static String getProductList(Collection<ProductsEntity> products, boolean repeatedSale) {
        // TODO: Show total or single as now?
        String content = "";
        for (ProductsEntity product : products) {
        	String description = "";
        	if(product.getDescription().length() <= 36){
        		description = product.getDescription();
        	}
        	else {
        		description = String.format("%s%n%-64s", Utils.wrapText(product.getDescription(), 48, 28), "");
        	}
        	
            content += String.format("%-12s%-16s%-36s%-8s%s%n",
                    product.getCode(),
                    product.getName(),
                    description,
                    product.getOriginalCount(),
                    "0"
            );
            content += String.format("%17s%11s%10s%12s%10s%7s%8s%n",
                    Utils.getFormattedPrice(product.getTotalPrice()),
                    Utils.getFormattedPrice(product.getTotalSalePrice()),
                    "0",
                    Utils.getFormattedPrice(product.getLowerPrice()),
                    Utils.getFormattedPrice(product.getLowerPrice()),
                    Utils.getFormattedPrice(product.getTotalStorageFee()),
                    Utils.getFormattedPrice(product.getWithdrawalFee())
            );
            
            if(repeatedSale){
            	content += String.format(
                    "(pův. zboží: %s)",
                    product.getRenewedFromProduct().getCode()
                );
            }
            
            /*
            if(product.getRepeatedSale() > 0) {
                ProductsEntity p = product;
                while(true) {
                    if(p.getRenewedFromProduct() == null) break;
                    p = p.getRenewedFromProduct();
                }
                content += String.format(
                    "( %d.op.pr.z: %s, pův.č.: %s, vzn.pen.: %d Kč, zapl.: %d Kč )",
                    product.getRepeatedSale(),
                    product.getRenewedFromProduct().getCode(),
                    p.getCode(),
                    0, // TODO: Penalizace
                    0
                );
            }
            */
        }
        return content.trim();
    }

    public static String getProductListSmall(Collection<ProductsEntity> products) {
        String content = "";
        for (ProductsEntity product : products) {
            content += String.format("%-12s%-26s%-8s%-8s",
                    product.getCode(),
                    product.getName(),
                    product.getSalePrice(),
                    product.getLowerPrice()
                    );
            content += "\n";
        }
        return content.trim();
    }

    public static String getProductCards(Collection<ProductsEntity> products) {
        String content = "";
        Collections.sort((List<ProductsEntity>)products,
            (ProductsEntity p1, ProductsEntity p2) -> p1.getCode().compareTo(p2.getCode())
        );
        for (ProductsEntity product : products) {
            content += getProductCard(product);
            content += "\n\n";
        }
        return content.trim();
    }

    public static String getProductCard(ProductsEntity product) {
        String content = "";
        SimpleDateFormat dt1 = new SimpleDateFormat("dd.MM.YYYY");
        SimpleDateFormat dt2 = new SimpleDateFormat("dd.MM.YYYY EEEE");
        ContractsEntity contract = product.getContract();
        CustomersEntity customer = contract.getCustomer();
        content += String.format("%s%-6s%s%n",
                getLine(33),
                " ",
                getLine(33));
        content += String.format("| EVID.LÍSTEK ZBOŽÍ Č.: %s |%-6s| EVID.LÍSTEK ZBOŽÍ Č.: %s |%n",
                product.getCode(), " ", product.getCode());
        content += String.format("%s%-6s%s%n",
                getLine(33),
                " ",
                getLine(33));
        content += String.format("  Název: %-23s%-8sNázev: %s%n",
                product.getName(), " ", product.getName());
        
        String description = Utils.wrapText(product.getDescription(), 24, 9);
        String[] descLines = description.split("\n");
        
        content += String.format("  Popis: %-23s%-8sPopis: %s%n", descLines[0], " ", descLines[0]);
        
        for(int a=1; a<descLines.length; a++){
        	content += String.format("%-32s%-6s%s%n", descLines[a], " ", descLines[a]);
        }
        
        content += String.format("  Kusů: %-23s%-8s Kusů: %s%n",
                product.getCount(), " ", product.getCount());
        content += String.format("  Částí:    %-20s%-8sČástí:    %s%n",
                product.getParts(), " ", product.getParts());
        content += String.format("  Prodejna: R%27sProdejna: R%n", " ");
        content += String.format("  %38sZákazník: %s %s%n", "", customer.getCode(), customer.getLastname());
        content += "\n";
        content += String.format("  Přijetí: %-29sPřijetí: %s%n",
                dt1.format(contract.getDateFrom()), dt1.format(contract.getDateFrom()));
        content += String.format("  Ukončení: %-28sUkončení: %s%n",
                dt1.format(contract.getDateTo()), dt1.format(contract.getDateTo()));
        content += String.format("  Vyřízení: %-28sVyřízení: %s%n",
                dt2.format(contract.getDateToPickup()), dt2.format(contract.getDateToPickup()));

        content += String.format("%s%-6s%s%n",
                getLine(33),
                " ",
                getLine(33));
        content += String.format("| PRODEJNÍ CENA: %14s |%-6s| CENA ZÁKAZNÍKA: %13s |%n",
                Utils.getPriceString(product.getTotalSalePrice()), // TOTAL OR SINGLE?
                " ",
                Utils.getPriceString(product.getTotalPrice()) // TOTAL OR SINGLE?
                );
        content += String.format("%s%-6s%s%n",
                getLine(33),
                " ",
                getLine(33));

        return content;
    }
}
