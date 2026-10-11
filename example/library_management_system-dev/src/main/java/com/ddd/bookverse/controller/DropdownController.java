package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.User;
import com.ddd.bookverse.singleton.UserSession;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

import static com.ddd.bookverse.utils.NavHelper.*;

public class DropdownController {

    @FXML
    private Text nameText;

    @FXML
    private ImageView toggleImageView;

    @FXML
    private VBox optionsPane;

    @FXML
    private TextField accountField;

    @FXML
    private TextField logoutField;

    @FXML
    private AnchorPane layout_dropdown;

    private final UserSession userSession = UserSession.getInstance();

    private RotateTransition rotateTransition;


    @FXML
    public void initialize() {
//        layout_dropdown.setTranslateX(900);
        layout_dropdown.setPrefHeight(50);
        nameText.setText(userSession.getUsername());
        nameText.setFill(Color.web("#333333"));

        toggleImageView.setCursor(Cursor.HAND);
        accountField.setCursor(Cursor.HAND);
        logoutField.setCursor(Cursor.HAND);

        // Load and set the image for the ImageView
        Image toggleImage = new Image(getClass()
                .getResource("/img/right.png").toExternalForm());
        toggleImageView.setImage(toggleImage);

        // Setup rotation animation
        rotateTransition = new RotateTransition(Duration.seconds(0.2), toggleImageView);
        rotateTransition.setCycleCount(1);

        // Setup hover styles
        setupHoverStyles();

        toggleImageView.setOnMouseClicked(this::toggleOptionsPane);

        accountField.setOnMouseClicked(this::handleAccountClick);

        logoutField.setOnMouseClicked(this::handleLogoutClick);
    }

    /**
     * Toggles the visibility of the options pane when the user clicks on the
     * corresponding UI element.
     * If the options pane is not visible, it will be shown with a smooth animation,
     * rotating an associated
     * element by 90 degrees. If the options pane is already visible,
     * it will be hidden with a reverse rotation.
     * The height of the layout dropdown will also change depending on the
     * visibility of the options pane.
     *
     * @param event The mouse event that triggered the method.
     */
    @FXML
    private void toggleOptionsPane(MouseEvent event) {
        if (!optionsPane.isVisible()) {
            rotateTransition.setByAngle(90);
            rotateTransition.play();
            layout_dropdown.setPrefHeight(274);
            optionsPane.setVisible(true);
        } else {
            rotateTransition.setByAngle(-90);
            rotateTransition.play();
            optionsPane.setVisible(false);
            layout_dropdown.setPrefHeight(50);
        }
    }

    /**
     * Sets up hover styles for the account and logout fields.
     * When the user hovers over either the account or logout field,
     * their background color and text color
     * change to give a visual feedback. When the user stops hovering,
     * the styles revert to their original state.
     */
    private void setupHoverStyles() {
        accountField.setOnMouseEntered(
                e -> accountField.setStyle("-fx-background-color:#5f43b2;" +
                        " -fx-text-fill: white;"));
        accountField.setOnMouseExited(
                e -> accountField.setStyle("-fx-background-color: rgba(0,0,0,0);" +
                        "-fx-text-fill: #333333;"));

        logoutField.setOnMouseEntered(
                e -> logoutField.setStyle("-fx-background-color:#5f43b2;" +
                        " -fx-text-fill: white;"));
        logoutField.setOnMouseExited(
                e -> logoutField.setStyle("-fx-background-color: rgba(0,0,0,0);" +
                        "-fx-text-fill: #333333;"));
    }

    /**
     * Handles the click event on the account field. Based on the user's role,
     * this method directs the user to their appropriate account page
     * (either the member account or the admin account page).
     *
     * @param event The mouse event that triggered the method.
     * @throws IOException if an error occurs while navigating to the appropriate account page.
     */
    @FXML
    private void handleAccountClick(MouseEvent event) {
        try {
            if (userSession.getRole().equals(User.memberRole)) {
                toMyAccount(event);
            } else if (userSession.getRole().equals(User.adminRole)) {
                toAdminAccount(event);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the click event on the logout field. This method logs out the user and redirects them
     * to the login page.
     *
     * @param event The mouse event that triggered the method.
     * @throws IOException if an error occurs while redirecting the user to the login page.
     */
    @FXML
    private void handleLogoutClick(MouseEvent event) {
        try {
            toLogIn(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
