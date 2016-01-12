package com.cak.toy.jfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by dacki on 02.01.16.
 */
public class MainController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private ListView<Label> navigation;
    @FXML
    private BorderPane rootpanel;

    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing Main view");

        // Prefill list
        ObservableList<Label> navigationItems = FXCollections.observableArrayList(
                new Label("Overview"),
                new Label("Create Transaction"),
                new Label("Transaction History"));
        navigation.setItems(navigationItems);
        navigation.getSelectionModel().select(0);

        // Add listener
        navigation.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> gotoPlace(newValue)
        );
    }

    private void gotoPlace(Number index) {
        Map<Number, String> placeToFileMapping = new HashMap<>();
        placeToFileMapping.put(0, "overview");
        placeToFileMapping.put(1, "transaction");
        placeToFileMapping.put(2, "transactionhistory");

        StringBuilder fxmlFile = new StringBuilder("/fxml/");
        fxmlFile.append(placeToFileMapping.get(index));
        fxmlFile.append(".fxml");

        log.debug("Loading FXML for main view from: {}", fxmlFile);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Class.class.getResource("/fxml/"));
        Parent rootNode = null;
        try {
            log.info("Changing place to {}.", fxmlFile.toString());
            rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile.toString()));
            rootpanel.setCenter(rootNode);
        } catch (IOException e) {
            log.error("", e);
        }

    }
}
