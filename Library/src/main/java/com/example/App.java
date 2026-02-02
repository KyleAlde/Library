package com.example;

import java.io.IOException;
import java.sql.SQLException;

import com.example.utility.DatabaseConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
        
        // Create a loading screen for faster initial display
        StackPane loadingRoot = createLoadingScreen();
        scene = new Scene(loadingRoot, 600, 400);
        
        stage.setScene(scene);
        stage.show();
        
        // Load the actual content asynchronously
        loadMainContentAsync(stage);
    }
    
    private StackPane createLoadingScreen() {
        StackPane loadingPane = new StackPane();
        loadingPane.setStyle("-fx-background-color: #2c3e50;");
        
        // Create loading indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(60, 60);
        
        // Create loading text
        Text loadingText = new Text("Loading Library System...");
        loadingText.setFont(Font.font("Segoe UI", 24));
        loadingText.setFill(Color.WHITE);
        
        // Create layout
        VBox loadingBox = new VBox(20, progressIndicator, loadingText);
        loadingBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        loadingPane.getChildren().add(loadingBox);
        return loadingPane;
    }
    
    private void loadMainContentAsync(Stage stage) {
        javafx.application.Platform.runLater(() -> {
            try {
                // Load the main content
                Parent mainRoot = loadFXML("fxml/welcomePage/welcomePortal");
                
                // Replace loading screen with main content
                scene.setRoot(mainRoot);
                
                System.out.println("Application loaded successfully");
                
            } catch (Exception e) {
                System.err.println("Failed to load main content: " + e.getMessage());
                e.printStackTrace();
                
                // Show error on loading screen
                javafx.application.Platform.runLater(() -> {
                    StackPane currentRoot = (StackPane) scene.getRoot();
                    if (currentRoot.getChildren().size() > 1) {
                        Text errorText = (Text) ((VBox) currentRoot.getChildren().get(0)).getChildren().get(1);
                        errorText.setText("Error loading application. Please restart.");
                        errorText.setFill(Color.RED);
                    }
                });
            }
        });
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
