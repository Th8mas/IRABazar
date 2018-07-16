package com.droow.irabazar.utils;

import com.droow.irabazar.model.entity.*;
import com.droow.irabazar.model.service.OptionService;
import com.droow.irabazar.model.service.ProductService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static com.droow.irabazar.controllers.product.BaseProductController.rootCategoryName;

/**
 * Global static utils
 */
public class Utils {

	private static final Logger Log = LoggerFactory.getLogger(Utils.class);
	
    /**
     * Appends currency postfix to price and returns it as string
     * @param price
     * @return price with currency string
     */
    public static String getPriceString(Double price) {
    	return Utils.round(price, 1) + " Kč";
    }

    /**
     * Gets the controller instance from FXML file path
     * @param fxmlPath String
     * @return BaseScreenController|null
     */
    public static Class getControllerClass(String fxmlPath) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            URL location = Utils.class.getResource(fxmlPath);
            if(location == null) {
                location = Utils.class.getClassLoader().getResource(fxmlPath);
            }
            Document document = builder.parse(location.openStream());
            NamedNodeMap attributes = document.getDocumentElement().getAttributes();
            String fxControllerClassName=null;
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                if(item.getNodeName().equals(FXMLLoader.FX_NAMESPACE_PREFIX+":"+FXMLLoader.CONTROLLER_KEYWORD)){
                    fxControllerClassName = item.getNodeValue();
                }
            }
            if(fxControllerClassName!=null)
                return ClassLoader.getSystemClassLoader().loadClass(fxControllerClassName);
        } catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException ex) {
            Log.error("Cannot get controller class", ex);
        }
        return null;
    }

    /**
     * Populates TreeView with CategoriesEntity
     * @param table TreeView
     * @param rootCategory CategoriesEntity
     * @param listener ChangeListener
     */
    public static void populateCategoriesTreeView(
            TreeView<CategoriesEntity> table,
            CategoriesEntity rootCategory,
            ChangeListener<TreeItem<CategoriesEntity>> listener
            //ComboBox<CategoriesEntity> selectInput
    ) {
        TreeItem<CategoriesEntity> rootItem = new TreeItem<CategoriesEntity> (rootCategory);

        fillChildren(rootItem);
        table.setRoot(rootItem);
        if(listener != null) {
            table.getSelectionModel().selectedItemProperty().addListener(listener);
        }
    }

    /**
     * Recursively fills children of TreeItem item
     * @param rootItem TreeItem
     */
    private static void fillChildren(TreeItem<CategoriesEntity> rootItem) {
        rootItem.setExpanded(true);
        if(rootItem.getValue() == null) return;
        for (CategoriesEntity category : rootItem.getValue().getChilds()) {
            TreeItem<CategoriesEntity> item = new TreeItem<CategoriesEntity> (category);
            if(category.getChilds().size() > 0 && item != null) {
                fillChildren(item);
            }
            rootItem.getChildren().add(item);
        }
    }

    /**
     * Populates TableView with ContractsEntity
     * @param table TableView
     * @param list ObservableList
     * @param listener ChangeListener
     */
    public static void populateContractsTableView(
            TableView<ContractsEntity> table,
            ObservableList<ContractsEntity> list,
            ChangeListener<ContractsEntity> listener,
            StringProperty property
    ) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<ContractsEntity> filteredData = new FilteredList<>(list, p -> true);
        // 2. Set the filter Predicate whenever the filter changes.
        if(property != null) {
            property.addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(contract -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Filter by Code
                    return contract.getCode().toLowerCase().contains(lowerCaseFilter);
                });
            });
        }
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<ContractsEntity> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        table.getSelectionModel().clearSelection();
        //table.getItems().clear();
        table.setItems(sortedData);

        TableColumn<ContractsEntity, String> codeCol = new TableColumn<ContractsEntity, String>("Kód smlouvy");
        codeCol.setCellValueFactory(
            contract -> new SimpleStringProperty(contract.getValue().getCode())
        );
        TableColumn<ContractsEntity, String> customerCol = new TableColumn<>("Zákazník");
        customerCol.setCellValueFactory(
            contract -> new SimpleStringProperty(contract.getValue().getCustomer().toString())
        );
        TableColumn<ContractsEntity, String> dateFromCol = new TableColumn<>("Od");
        dateFromCol.setCellValueFactory(
            contract -> getStringPropertyFromTimestamp(contract.getValue().getDateFrom())
        );
        TableColumn<ContractsEntity, String> dateToCol = new TableColumn<>("Do");
        dateToCol.setCellValueFactory(
            contract -> getStringPropertyFromTimestamp(contract.getValue().getDateTo())
        );
        TableColumn<ContractsEntity, String> countCol = new TableColumn<>("Počet zboží");
        countCol.setCellValueFactory(
            contract -> new SimpleStringProperty(contract.getValue().getProducts().size() + "")
        );
        TableColumn<ContractsEntity, String> statusCol = new TableColumn<>("Stav");
        statusCol.setCellValueFactory(
            contract -> new SimpleStringProperty(contract.getValue().getStatus().toString())
        );

        table.getColumns().setAll(
                codeCol,
                customerCol,
                dateFromCol,
                dateToCol,
                countCol,
                statusCol
        );

        if(listener != null)
            table.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    /**
     * Populates TableView with ProductsEntity
     * @param table TableView
     * @param list ObservableList
     * @param listener ChangeListener
     */
    public static void populateProductTableView(
            TableView<ProductsEntity> table,
            ObservableList<ProductsEntity> list,
            ChangeListener<ProductsEntity> listener,
            ChangeListener<ProductsEntity> removeListener,
            StringProperty property,
            boolean showAllItems
    ) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data but the renewed items (we don't want the items to appear twice in the table)).
        FilteredList<ProductsEntity> filteredData = new FilteredList<>(list, product -> shouldShowProduct(product, showAllItems));
        // 2. Set the filter Predicate whenever the filter changes.
        if(property != null) {
            property.addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(product -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Filter by Code, Name, Description
                    return product.getName().toLowerCase().contains(lowerCaseFilter)
                        || product.getCode().toLowerCase().contains(lowerCaseFilter)
                        || product.getDescription().toLowerCase().contains(lowerCaseFilter);
                });
            });
        }
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<ProductsEntity> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        table.getSelectionModel().clearSelection();
        //table.getItems().clear();
        table.setItems(sortedData);

        // Product code
        TableColumn<ProductsEntity, String> productIdCol = new TableColumn<>("Kód");
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        // Product name
        TableColumn<ProductsEntity, String> nameCol = new TableColumn<>("Název");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Product description
        TableColumn<ProductsEntity, String> descCol = new TableColumn<>("Popis");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        // Product original count
        TableColumn<ProductsEntity, Integer> originalCountCol = new TableColumn<>("Kusů přijato");
        originalCountCol.setCellValueFactory(new PropertyValueFactory<>("originalCount"));
        // Product count
        TableColumn<ProductsEntity, Integer> countCol = new TableColumn<>("Kusů zbývá");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        // Product price
        /*
        TableColumn<ProductsEntity, String> priceCol = new TableColumn<>("Průměrná cena za kus");
        priceCol.setCellValueFactory(
            product -> new SimpleStringProperty(getPriceString(product.getValue().getSalePrice()))
        );
        */
        // Total price
        TableColumn<ProductsEntity, Double> totalPriceCol = new TableColumn<>("Celková cena");
        totalPriceCol.setCellValueFactory(
            //product -> new SimpleStringProperty(getPriceString(product.getValue().getTotalSalePrice()))
        	new PropertyValueFactory<>("totalSalePrice")
        );
        // Product categories
        TableColumn<ProductsEntity, String> categoryCol = new TableColumn<>("Kategorie");
        categoryCol.setCellValueFactory(
            product -> {
                if(product.getValue().getCategories().size() > 0) {
                    CategoriesEntity cat = product.getValue().getCategories().iterator().next();
                    if(cat != null) {
                        String catString = cat.getName();
                        while (cat.getParent() != null && cat.getParent().getName() != rootCategoryName) {
                            cat = cat.getParent();
                            catString = cat.getName() + " / " + catString;
                        }
                        return new SimpleStringProperty(catString);
                    }
                }
                return new SimpleStringProperty("Žádná");
            }
        );
        // Product type
        TableColumn<ProductsEntity, String> typeCol = new TableColumn<>("Typ");
        typeCol.setCellValueFactory(
            product -> new SimpleStringProperty(product.getValue().getType().equals("K") ? "Komise" : "Naše")
        );
        
        // Owner name
        TableColumn<ProductsEntity, String> ownerCol = new TableColumn<>("Zákazník");
        ownerCol.setCellValueFactory(
            product -> new SimpleStringProperty(getCustomerName(product.getValue()))
        );
        
        // Delivery date 
        TableColumn<ProductsEntity, String> createdCol = new TableColumn<>("Datum přijetí");
        createdCol.setCellValueFactory(
            product -> {
            	Timestamp created = product.getValue().getCreated();
            	return new SimpleStringProperty(created == null ? "" : new SimpleDateFormat("dd.MM.yyyy HH:mm").format(created));
            }
        );

        // Last sell date
        TableColumn<ProductsEntity, String> lastSellCol = new TableColumn<>("Poslední prodej");
        lastSellCol.setCellValueFactory(
            product -> {
            	Timestamp dateSold = product.getValue().getDateSold();
            	return new SimpleStringProperty(dateSold == null ? "" : new SimpleDateFormat("dd.MM.yyyy HH:mm").format(dateSold));
            }
        );
        
        // Renewed product
        TableColumn<ProductsEntity, String> renewedProductCol = new TableColumn<>("Původní zboží");
        renewedProductCol.setCellValueFactory(
            product -> {
            	ProductsEntity renewedProduct = product.getValue().getRenewedFromProduct();
            	return new SimpleStringProperty(renewedProduct == null ? "" : renewedProduct.getCode());
            }
        );

        TableColumn<ProductsEntity, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(
            product -> {
            	return new SimpleStringProperty(product.getValue().getStatus().name());
            }
        );
        
        table.getColumns().setAll(
            productIdCol,
            nameCol,
            descCol,
            categoryCol,
            originalCountCol,
            countCol,
            //priceCol,
            totalPriceCol,
            typeCol,
            ownerCol,
            createdCol,
            lastSellCol,
            renewedProductCol,
            statusCol
        );

        if(removeListener == null) {
        	if(listener != null){
        		table.getSelectionModel().selectedItemProperty().addListener(listener);
        	}
        }
        else {
	        table.setRowFactory(new Callback<TableView<ProductsEntity>, TableRow<ProductsEntity>>() {
	    		@Override
	    		public TableRow<ProductsEntity> call(TableView<ProductsEntity> tableView) {
	    		    final TableRow<ProductsEntity> row = new TableRow<>();
	    		    final ContextMenu rowMenu = new ContextMenu();
	    		    MenuItem editItem = new MenuItem("Detaily");
	    		    editItem.setOnAction(new EventHandler<ActionEvent>() {
	        		    @Override
	        		    public void handle(ActionEvent event) {
	        		    	if(listener != null) {
	        		    		listener.changed(null, row.getItem(), row.getItem());
	        		        }
	        		    }
	        		});
	    		    MenuItem removeItem = new MenuItem("Odstranit");
	    		    removeItem.setOnAction(new EventHandler<ActionEvent>() {
	        		    @Override
	        		    public void handle(ActionEvent event) {
	        		    	if(showConfirmationDialog("Odstranění zboží z příjmu", "Opravdu chcete odstranit přijaté zboží ze seznamu?", null)){
	        		    		removeListener.changed(null, row.getItem(), row.getItem());
	        		    	}
	        		    }
	        		});
	        		rowMenu.getItems().addAll(editItem, removeItem);
	
	        		// only display context menu for non-null items:
	        		row.contextMenuProperty().bind(
	        		  Bindings.when(Bindings.isNotNull(row.itemProperty()))
	        		  .then(rowMenu)
	        		  .otherwise((ContextMenu)null));
	        		return row;
	    		}
	        });
        }
    }
    
    private static boolean shouldShowProduct(ProductsEntity product, boolean showAll){
    	 if(showAll) return true;
    	 
    	 return (product.getStatus() != ProductsEntity.Status.RETURNED &&
    			 product.getStatus() != ProductsEntity.Status.SOLD &&
    			 product.getStatus() != ProductsEntity.Status.PAID);
    }

    public static String getCustomerName(ProductsEntity product) {
    	try {
    		CustomersEntity customer = product.getContract().getCustomer();
    		return customer.getFirstname() + " " + customer.getLastname();
    	} catch (Exception e){
    		return "";
    	}
    }
    
    /**
     * Populates TableView with OrderItemsEntity
     * @param table TableView
     * @param list ObservableList
     */
    public static void populateOrderItemsTableView(
        TableView<OrderItemsEntity> table,
        boolean orderCompleted,
        ObservableList<OrderItemsEntity> list,
        ChangeListener<OrderItemsEntity> listener,
        ChangeListener<OrderItemsEntity> removeListener
    ) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<OrderItemsEntity> filteredData = new FilteredList<>(list, p -> true);
        // 2. Set the filter Predicate whenever the filter changes.
        
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<OrderItemsEntity> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        table.getSelectionModel().clearSelection();
        //table.getItems().clear();
        table.setItems(sortedData);

        // Product
        TableColumn<OrderItemsEntity, String> codeCol = new TableColumn<OrderItemsEntity, String>("Kód zboží");
        codeCol.setCellValueFactory(
                orderItem -> new SimpleStringProperty(orderItem.getValue().getProduct().getCode())
        );
        TableColumn<OrderItemsEntity, String> nameCol = new TableColumn<OrderItemsEntity, String>("Název zboží");
        nameCol.setCellValueFactory(
                orderItem -> new SimpleStringProperty(orderItem.getValue().getProduct().getName())
        );
        // Count
        TableColumn<OrderItemsEntity, Integer> countCol = new TableColumn<>("Kusů");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        
        // Product price
        /*
        TableColumn<OrderItemsEntity, String> priceCol = new TableColumn<>("Prodejní cena za kus");
        priceCol.setCellValueFactory(
                oi -> new SimpleStringProperty(getPriceString(oi.getValue().getPrice()))
        );
        */
        // Product total price
        TableColumn<OrderItemsEntity, String> totalPriceCol = new TableColumn<>("Prodejní cena celkem");
        totalPriceCol.setCellValueFactory(
                oi -> new SimpleStringProperty(getPriceString(oi.getValue().getTotalPrice()))
        );
        
        // Product info
        TableColumn<OrderItemsEntity, String> infoCol = new TableColumn<>("Info");
        infoCol.setCellValueFactory(
                oi -> new SimpleStringProperty(getSoldOutText(oi.getValue(), orderCompleted))
        );

        table.getColumns().setAll(
                codeCol,
                nameCol,
                countCol,
                //priceCol,
                totalPriceCol,
                infoCol
        );
        
        if(removeListener == null) {
        	if(listener != null){
        		table.getSelectionModel().selectedItemProperty().addListener(listener);
        	}
        }
        else {
	        table.setRowFactory(new Callback<TableView<OrderItemsEntity>, TableRow<OrderItemsEntity>>() {
	    		@Override
	    		public TableRow<OrderItemsEntity> call(TableView<OrderItemsEntity> tableView) {
	    			
	    		    final TableRow<OrderItemsEntity> row = new TableRow<>();
	    		    final ContextMenu rowMenu = new ContextMenu();
	    		    
	    		    MenuItem editItem = new MenuItem("Detaily");
	    		    editItem.setOnAction(new EventHandler<ActionEvent>() {
	        		    @Override
	        		    public void handle(ActionEvent event) {
	        		    	if(listener != null) {
	        		    		listener.changed(null, row.getItem(), row.getItem());
	        		        }
	        		    }
	        		});
	    		    MenuItem removeItem = new MenuItem("Odstranit");
	    		    removeItem.setOnAction(new EventHandler<ActionEvent>() {
	        		    @Override
	        		    public void handle(ActionEvent event) {
	        		    	if(showConfirmationDialog("Odstranění zboží z objednávky", "Opravdu chcete odstranit zboží z objednávky?", null)){
	        		    		removeListener.changed(null, row.getItem(), row.getItem());
	        		    	}
	        		    }
	        		});
	        		rowMenu.getItems().addAll(editItem, removeItem);
	
	        		// only display context menu for non-null items:
	        		row.contextMenuProperty().bind(
        				Bindings.when(Bindings.isNotNull(row.itemProperty()))
        				.then(rowMenu)
        				.otherwise((ContextMenu)null));
	        		return row;
	    		}
	        });
        }
    }
    
    private static String getSoldOutText(OrderItemsEntity orderItem, boolean orderCompleted){
    	if((orderCompleted && orderItem.getProduct().getCount() == 0) ||
    	   (!orderCompleted && orderItem.getProduct().getCount() == orderItem.getCount()))
    	{
    		return "Vyprodáno";
    	}
    	return "";
    }

    /**
     * Populates TableView with OrdersEntity
     * @param table TableView
     * @param list ObservableList
     * @param listener ChangeListener
     */
    public static void populateOrdersTableView(
            TableView<OrdersEntity> table,
            ObservableList<OrdersEntity> list,
            ChangeListener<OrdersEntity> listener
    ) {
        table.getItems().clear();
        table.setItems(list);

        TableColumn<OrdersEntity, String> productIdCol = new TableColumn<>("Kód");
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        TableColumn<OrdersEntity, String> priceCol = new TableColumn<>("Cena");
        priceCol.setCellValueFactory(
                order -> new SimpleStringProperty(getPriceString(order.getValue().getTotalPrice()))
        );
        TableColumn<OrdersEntity, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(
                order -> new SimpleStringProperty(order.getValue().getStatus().toString())
        );
        TableColumn<OrdersEntity, String> dateCol = new TableColumn<>("Datum");
        dateCol.setCellValueFactory(
                order -> getStringPropertyFromTimestamp(order.getValue().getCreated())
        );
        

        table.getColumns().setAll(
                productIdCol,
                dateCol,
                priceCol,
                statusCol
        );

        if(listener != null)
            table.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    /**
     * Populates TableView with CustomersEntity
     * @param table TableView to fill
     * @param list ObservableList of customers
     * @param listener ChangeListener
     * @param property StringProperty
     */
    public static void populateCustomersTableView(
            TableView<CustomersEntity> table,
            ObservableList<CustomersEntity> list,
            ChangeListener<CustomersEntity> listener,
            StringProperty property
    ) {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<CustomersEntity> filteredData = new FilteredList<>(list, p -> true);
        // 2. Set the filter Predicate whenever the filter changes.
        if(property != null) {
            property.addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(customer -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Filter by ICO, Code, Name
                    return customer.getIco().toLowerCase().contains(lowerCaseFilter)
                        || customer.getCode().toLowerCase().contains(lowerCaseFilter)
                        || customer.getFirstname().toLowerCase().contains(lowerCaseFilter)
                        || customer.getLastname().toLowerCase().contains(lowerCaseFilter);
                });
            });
        }
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<CustomersEntity> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.

        table.getItems().clear();
        table.setItems(sortedData);
        //table.setItems(list);

        //TableColumn<CustomersEntity, Integer> customerIdCol = new TableColumn<CustomersEntity, Integer>("ID");
        //customerIdCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, Integer>("id"));
        TableColumn<CustomersEntity, String> icoCol = new TableColumn<CustomersEntity, String>("IČO");
        icoCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("ico"));
        TableColumn<CustomersEntity, String> customerCodeCol = new TableColumn<CustomersEntity, String>("Kód");
        customerCodeCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("code"));
        TableColumn<CustomersEntity, String> firstNameCol = new TableColumn<CustomersEntity, String>("Jméno");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("firstname"));
        TableColumn<CustomersEntity, String> lastNameCol = new TableColumn<CustomersEntity, String>("Příjmení");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("lastname"));
        TableColumn<CustomersEntity, String> emailCol = new TableColumn<CustomersEntity, String>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("email"));
        TableColumn<CustomersEntity, String> phoneCol = new TableColumn<CustomersEntity, String>("Telefon");
        phoneCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("phone"));
        TableColumn<CustomersEntity, String> addressCol = new TableColumn<CustomersEntity, String>("Adresa");
        addressCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("address"));
        /*TableColumn<CustomersEntity, String> contractFromCol = new TableColumn<CustomersEntity, String>("Smlouva od");
        contractFromCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("contract_from"));
        TableColumn<CustomersEntity, String> contractToCol = new TableColumn<CustomersEntity, String>("Smlouva do");
        contractToCol.setCellValueFactory(new PropertyValueFactory<CustomersEntity, String>("contract_to"));*/

        table.getColumns().setAll(
                //customerIdCol,
                customerCodeCol,
                icoCol,
                firstNameCol,
                lastNameCol,
                emailCol,
                phoneCol,
                addressCol
                //contractFromCol,
                //contractToCol
        );

        if(listener != null) table.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public static void populateSalesTableView(TableView<OrderItemsEntity> table, ObservableList<OrderItemsEntity> orderItems) {
 
    	// 3. Wrap the FilteredList in a SortedList.
        SortedList<OrderItemsEntity> sortedData = new SortedList<>(orderItems);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());
    	
        table.setItems(sortedData);
        
        TableColumn<OrderItemsEntity, String> dateCol = new TableColumn<>("Datum");
        dateCol.setPrefWidth(100);
        dateCol.setCellValueFactory(orderItem -> getStringPropertyFromTimestamp(orderItem.getValue().getOrder().getCreated()));
        
        TableColumn<OrderItemsEntity, String> countCol = new TableColumn<>("Počet kusů");
        countCol.setPrefWidth(100);
        countCol.setCellValueFactory(orderItem -> new SimpleStringProperty(String.valueOf(orderItem.getValue().getCount())));
        
        TableColumn<OrderItemsEntity, String> priceCol = new TableColumn<>("Cena");
        priceCol.setPrefWidth(100);
        priceCol.setCellValueFactory(orderItem -> new SimpleStringProperty(getPriceString(orderItem.getValue().getTotalPrice())));
        
        table.getColumns().setAll(
        		dateCol,
        		countCol,
        		priceCol
        );
    }
    
    public static String getFormattedPrice(double price) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);
        return df.format(Math.round(price));
    }

    public static double getStorageFeeFromSalePrice(double price, int minStorageFee, int storageFeeInPercent) {
        double storageFee = price / 100 * storageFeeInPercent;
        return minStorageFee > storageFee ? minStorageFee : storageFee;
    }

    public static double getWithdrawalFeeFromSalePrice(double salePrice, int withdrawalFeeInPercent) {
        return salePrice / 100 * withdrawalFeeInPercent;
    }

    public static void printString(String content) {
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        if(service != null) {
            String defaultPrinter = service.getName();
            AttributeSet attributes = service.getAttributes();
            if(attributes != null) {
                Attribute piaj = attributes.get(PrinterIsAcceptingJobs.class);
                //PrinterState printerState = (PrinterState)service.getSupportedAttributeValues(PrinterState.class, null, null);
                System.out.println("Default printer: " + defaultPrinter);
                if(piaj == null) {
                    Utils.showWarningDialog("Nedostupná tiskárna", "Nepodařilo se spojit s tiskárnou.", "Ujistěte se že je tiskárna zpojená a zapnutá,");
                    return;
                }
            } else {
                Utils.showWarningDialog("Nedostupná tiskárna", "Nepodařilo se spojit s tiskárnou.", "Ujistěte se že je tiskárna zpojená a zapnutá,");
                return;
            }

            Media[] res = (Media[]) service.getSupportedAttributeValues(Media.class, null, null);
            MediaSizeName msn = null;
            for (Media media : res) {
                if (media instanceof MediaSizeName) {
                    msn = (MediaSizeName) media;
                    MediaSize ms = MediaSize.getMediaSizeForName(msn);
                    if(ms != null) {
                        float width = ms.getX(MediaSize.MM);
                        float height = ms.getY(MediaSize.MM);
                        if(width == 209.903F && height == 304.8F) {
                            break;
                        }
                        //System.out.println(media + ": width = " + width + "; height = " + height);
                    }
                }
            }

            try {
            	content = stripAccents(content);
                InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
                
                DocFlavor[] flavors = service.getSupportedDocFlavors() ;
                for (int i = 0; i < flavors.length; i++) {
                     System.out.println("\t" +flavors[i]);
                }
                //DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                //DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                DocFlavor flavor = new DocFlavor("text/plain; charset=utf-8", InputStream.class.getName());
                DocAttributeSet das = new HashDocAttributeSet();
                //das.add(PrintQuality.HIGH);
                das.add(msn);

                Doc doc = new SimpleDoc(is, flavor, das);
                DocPrintJob job = service.createPrintJob();
                /*job.addPrintJobListener(new PrintJobAdapter() {
                    public void printDataTransferCompleted(PrintJobEvent e) {
                        System.out.println("Data transfer completed!");
                    }

                    public void printJobNoMoreEvents(PrintJobEvent e) {
                        System.out.println("No more events!");
                    }

                    public void printJobRequiresAttention(PrintJobEvent e) {
                        System.out.println("Requires Attention!");
                    }

                    public void printJobFailed(PrintJobEvent e) {
                        System.out.println("Print Job Failed!");
                    }

                    public void printJobCompleted(PrintJobEvent e) {
                        System.out.println("Print Job Completed!");
                    }

                    public void printJobCanceled(PrintJobEvent e) {
                        System.out.println("Print Job Cancelled!");
                    }
                });*/

                PrintRequestAttributeSet atts = new HashPrintRequestAttributeSet();
                atts.add(msn);
                atts.add(Finishings.NONE);

                job.print(doc, atts);
                is.close();
            } catch (IOException | PrintException e) {
                // Try normal print if UTF8 failed
                try {
                    InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
                    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                    //DocFlavor flavor = new DocFlavor("text/plain; charset=utf-8", InputStream.class.getName());
                    DocAttributeSet das = new HashDocAttributeSet();
                    das.add(msn);

                    Doc doc = new SimpleDoc(is, flavor, das);
                    DocPrintJob job = service.createPrintJob();

                    PrintRequestAttributeSet atts = new HashPrintRequestAttributeSet();
                    atts.add(msn);
                    atts.add(Finishings.NONE);

                    job.print(doc, atts);
                    is.close();
                } catch (IOException | PrintException ex) {
                    Utils.showExceptionDialog(ex);
                }
            }
        } else {
            Utils.showWarningDialog("Nedostupná tiskárna", "Nepodařilo se spojit s tiskárnou.", "Ujistěte se že je tiskárna zapojená a zapnutá.");
        }
    }

    private enum NotificationType {
        Error, Warn, Info
    }

    /**
     * Converts Date to LocalDate
     * @param date the Date object
     * @return LocalDate object
     */
    public static LocalDate getLocalDateFromDate(java.util.Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Converts LocalDate to Date
     * @param localDate the LocalDate object
     * @return Date
     */
    public static java.util.Date getDateFromLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Shows exception dialog
     * @param ex exception to show
     */
    public static void showExceptionDialog(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText("Nastala neočekávaná chyba");
        alert.setContentText(ex.toString());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Popis chyby:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        GridPane expContent = new GridPane();
        //expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        //textArea.setMaxWidth(Double.MAX_VALUE);
        //textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.getDialogPane().expandedProperty().addListener((l) -> {
            Platform.runLater(() -> {
                alert.getDialogPane().requestLayout();
                Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
                stage.sizeToScene();
            });
        });

        alert.showAndWait();
    }

    /**
     * Shows information dialog
     * @param title the dialog title
     * @param headerText dialog header text
     * @param contentText dialog content text
     */
    public static void showInfoDialog(String title, String headerText, String contentText) {
        Utils.showDialog(Alert.AlertType.INFORMATION, title, headerText, contentText);
    }

    /**
     * Shows warning dialog
     * @param title the dialog title
     * @param headerText dialog header text
     * @param contentText dialog content text
     */
    public static void showWarningDialog(String title, String headerText, String contentText) {
        Utils.showDialog(Alert.AlertType.WARNING, title, headerText, contentText);
    }

    /**
     * Shows error dialog
     * @param title the dialog title
     * @param headerText dialog header text
     * @param contentText dialog content text
     */
    public static void showErrorDialog(String title, String headerText, String contentText) {
        Utils.showDialog(Alert.AlertType.ERROR, title, headerText, contentText);
    }

    /**
     * Shows dialog by type
     * @param type the dialog type
     * @param title the dialog title
     * @param headerText dialog header text
     * @param contentText dialog content text
     */
    public static void showDialog(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.setWidth(500);

        alert.showAndWait();
    }


    /**
     * Shows confirmation dialog
     * @param title the dialog title
     * @param headerText dialog header text
     * @param contentText dialog content text
     * @return returns true if ok is selected
     */
    public static boolean showConfirmationDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        ButtonType buttonTypeOk = new ButtonType("Pokračovat", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Zrušit akci", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == buttonTypeOk;
    }

    /**
     * Shows informational notification
     * @param text notification text
     */
    public static void showInfoNotification(String text) {
        Utils.showNotification(NotificationType.Info, "OK", text);
    }

    /**
     * Shows warning notification
     * @param text notification text
     */
    public static void showWarnNotification(String text) {
        Utils.showNotification(NotificationType.Warn, "Pozor!", text);
    }

    /**
     * Shows error notification
     * @param text notification text
     */
    public static void showErrorNotification(String text) {
        Utils.showNotification(NotificationType.Error, "Chyba!", text);
    }

    /**
     * Shows notificatiton popup by type
     * @param type type of notification
     * @param title notification title
     * @param text notification text
     */
    private static void showNotification(NotificationType type, String title, String text) {
        Notifications notif = Notifications.create().text(text);
        switch (type) {
            case Warn:
                notif.showWarning();
                break;
            case Error:
                notif.showError();
                break;
            default:
                notif.showInformation();
                break;
        }
    }

    /**
     * Format timestamp to date and create SimpleStringProperty
     * @param timestamp The timestamp to be converted
     * @return SimpleStringProperty containing formated date
     */
    private static SimpleStringProperty getStringPropertyFromTimestamp(Timestamp timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return new SimpleStringProperty(dateFormat.format(timestamp));
    }

    // TODO: Move to generator?
    public static boolean generateContractContent(ContractsEntity contract, HashMap<String,String> options, String type) {
        ContractContentGenerator generator = new ContractContentGenerator(contract, options);
        switch(type) {
            case ContractContentGenerator.TEMPLATE_NEW:
            	generator.generateNewContractTemplate(false);
            	break;
            default:
                generator.generateNewContractTemplate(true);
                break;
        }
        return false;
    }
    
    public static boolean generatePayoutContractContent(ContractsEntity contract, Collection<ProductsEntity> originalProducts, HashMap<String,String> options, String type, boolean isTermination) {
        PayoutContractContentGenerator generator = new PayoutContractContentGenerator(contract, originalProducts, options, isTermination);
        switch(type) {
            case PayoutContractContentGenerator.TEMPLATE_NEW:
            default:
                generator.generateNewContractTemplate();
                break;
        }
        return false;
    }
    
    public static int getDaysFromNow(Timestamp date){
    	return (int) ((date.getTime() - new java.util.Date().getTime()) / (24 * 3600 * 1000));
    }
    
    /**
     * Adds storage fee and penalty to product 
     * @param product
     * @param count
     * @param optionService
     */
    public static void setStorageFee(ProductsEntity product, int count, HashMap<String, String> options){
    	double storageFeePct = Double.valueOf(options.get("storageFeeInPercent"));
    	double unsoldDailyFeePct = Double.valueOf(options.get("notSoldDailyFeeInPercent"));
    	double minimumFee = Double.valueOf(options.get("storageMinimumFeeInCrowns"));
    	
    	if(product.getCount() > 0){
        	double totalStorageFee = product.getTotalSalePrice() / 100 * storageFeePct;
        	totalStorageFee = totalStorageFee < minimumFee ? minimumFee : totalStorageFee;
        	
    		product.setTotalStorageFee(Math.round(totalStorageFee));
    		
    		int daysBeforePickup = Utils.getDaysFromNow(product.getContract().getDateToPickup());
        	if(daysBeforePickup >= 0) {
        		product.setPenalty(0);
        		return;
        	}
        	
        	double totalPenalty = product.getTotalSalePrice() / 100 * unsoldDailyFeePct * (-daysBeforePickup);
        	product.setPenalty(Math.round(totalPenalty));
    	}
    	else {
    		product.setTotalStorageFee(0);
    		product.setPenalty(0);
    	}
    }
    
    public static void setRenewPrice(ProductsEntity product, OptionService optionService){
    	double discountPct = optionService.getOptionByName("renewDiscountInPercent").getDoubleValue();
    	double profitPct = optionService.getOptionByName("profitInPercent").getDoubleValue();
    	
    	double totalDiscount = product.getTotalSalePrice() / 100 * discountPct;
    	totalDiscount = totalDiscount < 10 ? 10 : totalDiscount;
    	double totalSalePrice = product.getTotalSalePrice() - totalDiscount;
    	double totalPrice = totalSalePrice / 100 * (100 - profitPct);
    	
    	product.setPrice((int) (totalPrice / product.getCount()));
    	product.setTotalPrice((int) totalPrice);
    	product.setSalePrice((int) (totalSalePrice / product.getCount()));
    	product.setTotalSalePrice((int) totalSalePrice);
    }
    
    public static double getSalePrice(double price, int profit){
    	double salePrice = price * 100 / (100 - profit);
    	return getFinalSalePrice(salePrice);
    }

    /**
     * Just round helper
     * @param i The number to round
     * @param v Decimal places to round
     * @return The rounded number
     */
    public static double round(double i, int v){
        return Math.round(i/v) * v;
    }

    public static double getFinalSalePrice(double salePrice) {
        if(salePrice > 1000) {
            return (double)Utils.round(salePrice, 10);
        }
        if(salePrice > 300) {
            return (double)Utils.round(salePrice, 5);
        }
        return (double)Utils.round(salePrice, 1);
    }
    
    /**
     * Add specified amount of days to a date
     * @param d The date to add days to
     * @param days Number of days to add
     * @return Modified date
     */
    public static java.util.Date addDays(java.util.Date d, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static boolean isBankHoliday(java.util.Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        if((Calendar.SATURDAY == c.get(c.DAY_OF_WEEK)) || (Calendar.SUNDAY == c.get(c.DAY_OF_WEEK))) {
            return true;
        }
        return false;
    }

    /**
     * Returns first working day after supplied timestamp
     * @param start
     * @return First working day timestamp
     */
    public static Timestamp getFirstWorkingDay(Timestamp start) {
        java.util.Date myDate = new java.util.Date(start.getTime());
        while (isBankHoliday(myDate)) {
            myDate = addDays(myDate, 1);
        }
        return new Timestamp(myDate.getTime());
    }
    
    /**
     * Replaces accents with ordinary chars
     * @param s
     * @return
     */
    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }
    
    public static String formatDate(java.util.Date date, String format){
    	SimpleDateFormat sf = new SimpleDateFormat(format);
    	return sf.format(date);
    }

    public static String formatDate(Date date, String format){
    	SimpleDateFormat sf = new SimpleDateFormat(format);
    	return sf.format(date);
    }
    
    public static double checkDoubleNumber(String number) throws Exception{
    	try {
    		return Double.valueOf(number.replace(",", "."));
    	} catch(Exception e){
    		throw new Exception("Neplatný formát čísla - '" + number + "'");
    	}
    }
    
    public static int checkIntNumber(String number) throws Exception{
    	try {
    		return Integer.valueOf(number);
    	} catch(Exception e){
    		throw new Exception("Neplatný formát čísla - '" + number + "'");
    	}
    }
    
    public static String wrapText(String text, int maxChars, int newLineOffset){
		String finalString = "";
		String[] words = text.split(" ");
		int length = 0;
		
		for(int a=0; a<words.length; a++){
			length += words[a].length() + 1;
			
			if(length <= maxChars){
				finalString += words[a] + " ";
			}
			else {
				finalString += "\n";
				
				for(int i=0; i<newLineOffset; i++){
					finalString += " ";
				}
				
				finalString += words[a] + " ";
				length = words[a].length() + 1;
			}
		}
		
		return finalString;
	}
    
    public static void printStocking(ProductService productService){
    	if(Utils.showConfirmationDialog("Tisk inventury", "Chcete vytisknout inventurní seznam zboží?", null)){
        	String content = StockingContentGenerator.getContent(productService);
        	printString(content);
        }
    }
    
}
