/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.droow.irabazar.controllers;

import com.droow.irabazar.Main;
import com.droow.irabazar.scope.ScreenScope;
import com.droow.irabazar.scope.ScreenScoped;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.droow.irabazar.utils.Utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mehmet Sunkur <mehmetsunkur@gmail.com>
 */
@Service
public class ScreensController implements ApplicationContextAware {
	
	private static final Logger Log = LoggerFactory.getLogger(ScreensController.class);
	
    @Autowired
    ScreenScope screenScope;

    private ApplicationContext applicationContext;
    private Stage stage;
    private String currentScreenId;
    private final Map<String, BaseScreenController> screens = Collections.synchronizedMap(new HashMap<String, BaseScreenController>());
    
    public void init(Stage stage) {
        this.stage = stage;
        Group root = new Group();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        this.stage.setScene(new Scene(root));

        this.stage.setTitle(Main.applicationName + " v" + Main.applicationVersion);
        this.stage.setX(bounds.getMinX());
        this.stage.setY(bounds.getMinY());
        this.stage.setWidth(bounds.getWidth());
        this.stage.setHeight(bounds.getHeight());
        this.stage.setFullScreen(true);
        this.stage.setMaximized(true);
        this.stage.getIcons().add(new Image("/assets/icon.png"));
    }

    public void loadScreen(String fxml) {
        BaseScreenController oldScreenController = this.getCurrentController();
        try {

            Class controllerClass = Utils.getControllerClass(fxml);
            final BaseScreenController fxmlController = (BaseScreenController) applicationContext.getBean(controllerClass);
            
            if (this.screens.get(fxmlController.getScreenId()) == null) {
            	URL url = getClass().getResource(fxml);
                FXMLLoader loader = new FXMLLoader(url);
                loader.setControllerFactory(new Callback<Class<?>, Object>() {
                    @Override
                    public Object call(Class<?> aClass) {
                        return fxmlController;
                    }
                });
                Parent root = loader.load();
                fxmlController.setRoot(root);
                this.screens.put(fxmlController.getScreenId(), fxmlController);
            }

            this.currentScreenId = fxmlController.getScreenId();
            swapScreen(getCurrentController().getRoot());
            getCurrentController().screenLoadCallback();
            if (oldScreenController != null) {
                if (oldScreenController.getClass().isAnnotationPresent(ScreenScoped.class)) {
                    this.screens.remove(oldScreenController.getScreenId());
                    this.screenScope.remove(oldScreenController.getScreenId());
                }
            }

        } catch (IOException e) {
            Log.error("ScreensController exception", e);
            Utils.showExceptionDialog(e);
        }
    }

    private boolean swapScreen(final Parent root) {
        final Group rootGroup = getScreenRoot();
        final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        if (!isScreenEmpty()) {
            final DoubleProperty opacity = ((BorderPane)rootGroup.getChildren().get(0)).getCenter().opacityProperty();

            Timeline fade = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                    new KeyFrame(new Duration(250), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent t) {
                            BorderPane pane = (BorderPane) rootGroup.getChildren().get(0);
                            pane.setCenter(root);

                            ((AnchorPane)root).setPrefWidth(bounds.getWidth());
                            ((AnchorPane)root).setPrefHeight(bounds.getHeight());

                            //rootGroup.getChildren().add(0, root);
                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                    new KeyFrame(new Duration(350), new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                        }
                    }, new KeyValue(opacity, 0.0)));
            fade.play();
            return true;
        } else {
            rootGroup.getChildren().add(0, root);

            final DoubleProperty opacity = root.opacityProperty();
            opacity.set(0.0);
            Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                    new KeyFrame(new Duration(350), new KeyValue(opacity, 1.0)));
            fadeIn.play();
        }

        if (!this.stage.isShowing()) {
            this.stage.show();
        }
        return true;
    }

    private Group getScreenRoot() {
        return (Group) this.stage.getScene().getRoot();
    }

    private boolean isScreenEmpty() {
        return getScreenRoot().getChildren().isEmpty();
    }

    public BaseScreenController getCurrentController() {
        return screens.get(getCurrentScreenId());
    }

    public String getCurrentScreenId() {
        return currentScreenId;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
