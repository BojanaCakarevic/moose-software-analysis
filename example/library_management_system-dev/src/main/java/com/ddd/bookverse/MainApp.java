package com.ddd.bookverse;

import com.ddd.bookverse.config.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static com.ddd.bookverse.constants.Constants.*;
import static com.ddd.bookverse.utils.ViewUtil.loadImagesInBackground;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadImagesInBackground();
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Successfully connected to the database!");
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(LOGIN_URL)));
            primaryStage.setTitle(APP_NAME);
            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                throw new RuntimeException("CSS file not found");
            }
            primaryStage.setScene(scene);
            primaryStage.show();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth())/2);
            primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight())/2);
            primaryStage.setResizable(false);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
