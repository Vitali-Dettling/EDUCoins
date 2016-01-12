package com.cak.toy.jfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        log.info("Starting EDUCoin Wallet UI");

        String fxmlFile = "/fxml/main.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Class.class.getResource("/fxml/"));
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode, 800, 600);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("EDU.Coin.Wallet");
        stage.setScene(scene);
        stage.show();
    }
}
