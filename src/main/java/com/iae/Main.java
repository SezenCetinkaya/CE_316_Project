package com.iae;

import com.iae.db.ConfigurationDAO;
import com.iae.db.DatabaseHelper;
import com.iae.gui.MainController;
import com.iae.gui.UiAnimations;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 720;

    @Override
    public void start(Stage primaryStage) throws IOException {
        DatabaseHelper dbHelper = new DatabaseHelper();
        dbHelper.initialiseSchema();
        new ConfigurationDAO().seedDefaultsIfEmpty();

        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/com/iae/gui/main-view.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(
                Main.class.getResource("/com/iae/gui/styles.css").toExternalForm());

        primaryStage.setTitle("IAE — Integrated Assignment Environment");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
        UiAnimations.fadeSceneIn(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
