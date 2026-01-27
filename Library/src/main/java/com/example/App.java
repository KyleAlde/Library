package com.example;

import java.io.IOException;
import java.sql.SQLException;

import com.example.utility.DatabaseConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        stage.initStyle(StageStyle.DECORATED);
        scene = new Scene(loadFXML("fxml/memberPortal"), 1920, 1080);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.setFullScreen(!stage.isFullScreen());
                stage.initStyle(!stage.isFullScreen() ? StageStyle.DECORATED : StageStyle.UNDECORATED);
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        DatabaseConnection connection = new DatabaseConnection();
        try {
            connection.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            e.printStackTrace();
            return;
        }
        launch();
    }
}
