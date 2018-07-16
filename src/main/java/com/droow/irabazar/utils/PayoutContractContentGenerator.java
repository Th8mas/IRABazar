package com.droow.irabazar.utils;

import com.droow.irabazar.model.entity.ContractsEntity;
import com.droow.irabazar.model.entity.CustomersEntity;
import com.droow.irabazar.model.entity.ProductsEntity;
import com.droow.irabazar.model.entity.ProductsEntity.Status;

import org.stringtemplate.v4.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by droow on 16.12.16.
 */
public class PayoutContractContentGenerator {
	
    private String content = "";
    private ContractsEntity contract;
    private HashMap<String, String> options;
    private Collection<ProductsEntity> originalProducts;
    private boolean isTermination;

    public static final String TEMPLATE_NEW = "new";

    public PayoutContractContentGenerator(ContractsEntity contract, Collection<ProductsEntity> originalProducts, HashMap<String, String> options, boolean isTermination) {
        this.contract = contract;
        this.options = options;
        this.originalProducts = originalProducts;
        this.isTermination = isTermination;
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

    public boolean generateNewContractTemplate() {
        try {
            CustomersEntity customer = contract.getCustomer();
            SimpleDateFormat dt1 = new SimpleDateFormat("dd.MM.YYYY");
            String toDate = dt1.format(contract.getDateTo());
            String fromDate = dt1.format(contract.getDateFrom());

            STGroup g = new STGroupDir("assets/templates");
            ST template = g.getInstanceOf("payout");

            Collections.sort((List<ProductsEntity>)contract.getProducts(),
                (ProductsEntity p1, ProductsEntity p2) -> p1.getCode().compareTo(p2.getCode())
            );

            // Company
            template.add("company_name", options.get("comName"));
            template.add("company_ico", options.get("comIco"));
            template.add("company_dico", options.get("comDico"));
            template.add("company_address", options.get("comAddress"));
            template.add("company_phone", options.get("comPhone"));
            template.add("company_website", options.get("comWebsite"));
            // Customer
            template.add("customer_code", customer.getCode());
            template.add("customer_name", customer.getLastname()+" "+customer.getFirstname());
            template.add("customer_birthdate", customer.getFormatedBirthdate());
            template.add("customer_op_number", customer.getOpNumber());
            template.add("customer_address", customer.getAddress());
            // Contract
            template.add("payout_contract_code", contract.getCode());
            template.add("income_contract_code", "");
            template.add("completion_contract_code", "");
            template.add("payout_sum", options.get("totalPayout"));
            
            double totalFee = getIncomeSum();
            
            template.add("income_sum", totalFee);
            template.add("total_fee", String.format("%.2f", totalFee));
            template.add("current_date", Utils.formatDate(new Date(), "dd.MM.yyyy"));
            // Products
            template.add("returned_products_list", getProductList());

            contract.setPayoutContent(template.render());

            return true;
        } catch (Exception e) {
            Utils.showExceptionDialog(e);
        }

        return false;
    }
    
    private double getIncomeSum(){
    	double sum = 0;
    	
    	for(ProductsEntity product : originalProducts){
    		if(product.getCount() > 0){
    			Utils.setStorageFee(product, product.getCount(), this.options);
    			
        		sum += options.get("noStorageFee").equals("true") ? 0 :product.getTotalStorageFee();
        		sum += options.get("noPenalty").equals("true") ? 0 : (isTermination ? 50 : product.getPenalty());
    		}
    	}
    	
    	return sum;
    }
    /*
    private double getTotalFee(){
    	double totalFee = 0;
    	
    	for (ProductsEntity product : unsoldProducts) {
        	if(product.getStatus() != Status.RETURNED) continue;
        	
        	totalFee += product.getTotalStorageFee();
    	}
    	
    	return totalFee;
    }
    */
    private String getProductList() {
        String content = "";
        for (ProductsEntity product : originalProducts) {
        	if(product.getCount() == 0) continue;		//sold
        	
        	String description = "";
        	if(product.getDescription().length() <= 36){
        		description = product.getDescription();
        	}
        	else {
        		description = String.format("%s%n%-64s", Utils.wrapText(product.getDescription(), 48, 28), "");
        	}
        	
            content += String.format("%-12s%-16s%-36s%4d%10s%n",
                    product.getCode(),
                    product.getName(),
                    description,
                    product.getCount(),
                    contract.getCode()
            );
            content += String.format("%20s%11s%11s%20.2f%n",
                    Utils.formatDate(contract.getDateFrom(), "dd.MM.yyyy"),
                    Utils.formatDate(contract.getDateTo(), "dd.MM.yyyy"),
                    Utils.formatDate(contract.getDateToPickup(), "dd.MM.yyyy"),
                    options.get("noStorageFee").equals("true") ? 0 : product.getTotalStorageFee()
            );
            
            int daysAfterPickup = Utils.getDaysFromNow(contract.getDateToPickup());
            daysAfterPickup = daysAfterPickup < 0 ? -daysAfterPickup : 0;
            
            content += String.format("  s penálem %3d dní po termínu vyřízení prodeje %28.2fKč%n",
            		daysAfterPickup,
            		options.get("noPenalty").equals("true") ? 0 : product.getPenalty()
            );
            content += String.format("  (vzniklé penále: %.2f Kč)%n",
            		product.getPenalty()
            );
        }
        return content.trim();
    }
    
}
