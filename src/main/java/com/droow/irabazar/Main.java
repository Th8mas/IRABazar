package com.droow.irabazar;

import com.droow.irabazar.config.AppContextConfig;
import com.droow.irabazar.controllers.ScreensController;
import com.droow.irabazar.utils.Utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import javafx.application.Application;
import javafx.stage.Stage;
import org.hibernate.exception.GenericJDBCException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {
	private static final Logger Log = LoggerFactory.getLogger("Main");

    public static String applicationName = "Bazar";
    public static String applicationVersion = "1.4.3";

    public static String screenRoot = "/fxml/root.fxml";
    // Product screens
    public static String screenProducts = "/fxml/products/index.fxml";
    public static String screenProductsNew = "/fxml/products/new.fxml";
    public static String screenProductsDetail = "/fxml/products/show.fxml";
    public static String screenProductsCategories = "/fxml/products/categories.fxml";
    public static String screenProductPayout = "/fxml/products/payout.fxml";
    // Contract screens
    public static String screenContracts = "/fxml/contracts/index.fxml";
    public static String screenContractNew = "/fxml/contracts/new.fxml";
    public static String screenContractDetail = "/fxml/contracts/show.fxml";
    public static String screenContractPayout = "/fxml/contracts/payout.fxml";
    public static String screenContractRenew = "/fxml/contracts/renew.fxml";
    // Order screens
    public static String screenOrders = "/fxml/orders/index.fxml";
    public static String screenOrderNew = "/fxml/orders/new.fxml";
    public static String screenOrderDetail = "/fxml/orders/show.fxml";
    
    // Customer screens
    public static String screenCustomers = "/fxml/customers/index.fxml";
    public static String screenCustomersDetail = "/fxml/customers/show.fxml";
    // Settings screens
    public static String screenSettings = "/fxml/settings/index.fxml";
    public static String screenSettingsPostpone = "/fxml/settings/postpone.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        try {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppContextConfig.class);
            ScreensController bean = context.getBean(ScreensController.class);
            bean.init(stage);

            bean.loadScreen(screenRoot);
            bean.loadScreen(screenProducts);
        } catch (Exception e) {
            if(e.getCause() instanceof GenericJDBCException) {
                GenericJDBCException parent = (GenericJDBCException)e.getCause();
                if ( parent.getCause() instanceof PSQLException) {
                    Utils.showErrorDialog(
                            "Chyba",
                            parent.getLocalizedMessage(),
                            parent.getCause().getLocalizedMessage()
                    );
                }
            } else {
                Utils.showExceptionDialog(e);
            }
        }
    }

    /**
     * Lets get this party started!
     * @param args launch args
     */
    public static void main(String[] args) {
    	try { initLogEngine(); } catch(Exception e){}
    	Log.info("Starting application...");
        launch(args);
    }

	//------------------------------------------------------------------------

	private static void initLogEngine() throws JoranException {
	    // print internal state
		// assume SLF4J is bound to logback in the current environment
	    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	    
	    JoranConfigurator configurator = new JoranConfigurator();
	    configurator.setContext(context);
      
	    // Call context.reset() to clear any previous configuration, e.g. default 
	    // configuration. For multi-step configuration, omit calling context.reset().
	    context.reset(); 
      
	    configurator.doConfigure(System.getProperty("user.dir") + "/log_config.xml");
	    
	    //StatusPrinter.printInCaseOfErrorsOrWarnings(context);	
	}	
}
