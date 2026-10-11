package com.ddd.bookverse.utils;

import com.ddd.bookverse.controller.RightPanelController;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Utility class for handling view-related operations.
 */

public class ViewUtil {

    public static void loadImagesInBackground() {
        Task<Void> imageLoadingTask = new Task<>() {
            @Override
            protected Void call() {
                // Tải ảnh trong nền
                Image bookgifdasbord = new Image(ViewUtil.class.getResource("/img/dashboard.gif").toExternalForm());
                Image bookgifreadind = new Image(ViewUtil.class.getResource("/img/readinggirl.gif").toExternalForm());

                javafx.application.Platform.runLater(() -> {
                    ViewUtil.bookGifDashboard = bookgifdasbord;
                    ViewUtil.bookGifReading = bookgifreadind;
                });
                return null;
            }
        };

        // Chạy task trên luồng nền
        Thread thread = new Thread(imageLoadingTask);
        thread.setDaemon(true); // Đảm bảo luồng kết thúc khi ứng dụng đóng
        thread.start();
    }

    // Biến lưu ảnh
    public static Image bookGifDashboard;
    public static Image bookGifReading;

    /**
     * Toggles the visibility of the password in a PasswordField.
     * If the PasswordField is visible, it replaces it with a TextField to show the password.
     * If the PasswordField is hidden, it replaces the TextField with the PasswordField.
     *
     * @param passwordField The PasswordField to toggle.
     * @param imgView The ImageView to update with the appropriate icon.
     * @param fontIcon The FontIcon to update with the appropriate icon.
     */
    public static void togglePasswordVisibility(PasswordField passwordField, ImageView imgView, FontIcon fontIcon) {
        if (passwordField.isVisible()) {
            imgView.setImage(new Image(ViewUtil.class.getResource("/img/openpassword.png").toExternalForm()));
            // Create a new TextField to replace the PasswordField
            TextField textField = new TextField(passwordField.getText());
            textField.setLayoutX(passwordField.getLayoutX());
            textField.setLayoutY(passwordField.getLayoutY());
            textField.setPrefWidth(passwordField.getPrefWidth());
            textField.setPrefHeight(passwordField.getPrefHeight());
            textField.setStyle("-fx-border-color: #5f43b2;-fx-background-color:transparent;-fx-font-size:16;-fx-border-radius:10");
            passwordField.setVisible(false);
            AnchorPane parent = (AnchorPane) passwordField.getParent();
            parent.getChildren().add(textField);
            parent.getChildren().remove(fontIcon);
            fontIcon.setOnMouseClicked(e -> { textField.setText(""); passwordField.setText(""); });
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (textField.getText().equals("")) {
                    fontIcon.setVisible(false);
                } else {
                    fontIcon.setVisible(true);
                }
            });
            parent.getChildren().add(fontIcon);
        } else {
            imgView.setImage(new Image(ViewUtil.class.getResource("/img/closepassword.png").toExternalForm()));
            AnchorPane parent = (AnchorPane) passwordField.getParent();
            TextField textField = null;
            // Find the TextField in the parent node
            for (javafx.scene.Node node : parent.getChildren()) {
                if (node instanceof TextField) {
                    textField = (TextField) node;
                }
            }
            if (textField != null) {
                passwordField.setText(textField.getText());
                passwordField.setVisible(true);
                parent.getChildren().remove(textField); // Remove TextField
                passwordField.requestFocus();
            }
        }
    }

    /**
     * Retrieves the text from a PasswordField, even if it is currently hidden.
     *
     * @param passwordField The PasswordField to retrieve the text from.
     * @return The text from the PasswordField or the associated TextField.
     */
    public static String getPasswordText(PasswordField passwordField) {
        if (!passwordField.isVisible()) {
            AnchorPane parent = (AnchorPane) passwordField.getParent();
            TextField textField = null;

            // Find the TextField in the parent node
            for (javafx.scene.Node node : parent.getChildren()) {
                if (node instanceof TextField) {
                    textField = (TextField) node;
                }
            }
            if (textField != null) {
                return textField.getText();
            } else {
                throw new RuntimeException("Cannot find password textField");
            }
        } else {
            return passwordField.getText();
        }
    }

    /**
     * Closes the password visibility if the PasswordField is currently hidden.
     *
     * @param passwordField The PasswordField to close.
     * @param imgView The ImageView to update with the appropriate icon.
     */
    public static void closePasswordIfVisible(PasswordField passwordField, ImageView imgView) {
        if (!passwordField.isVisible()) {
            imgView.setImage(new Image(ViewUtil.class.getResource("/img/closepassword.png").toExternalForm()));

            AnchorPane parent = (AnchorPane) passwordField.getParent();
            TextField textField = null;

            // Find the TextField in the parent node
            for (javafx.scene.Node node : parent.getChildren()) {
                if (node instanceof TextField) {
                    textField = (TextField) node;
                }
            }

            if (textField != null) {
                passwordField.setText(textField.getText());
                passwordField.setVisible(true);
                parent.getChildren().remove(textField); // Remove TextField
                passwordField.requestFocus();
            }
        }
    }
}