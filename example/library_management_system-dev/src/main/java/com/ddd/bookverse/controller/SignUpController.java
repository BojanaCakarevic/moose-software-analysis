package com.ddd.bookverse.controller;

import com.ddd.bookverse.service.UserService;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

import static com.ddd.bookverse.utils.AnimationUtil.*;
import static com.ddd.bookverse.utils.NavHelper.*;
import static com.ddd.bookverse.utils.ValidationUtil.*;
import static com.ddd.bookverse.utils.ViewUtil.getPasswordText;
import static com.ddd.bookverse.utils.ViewUtil.togglePasswordVisibility;

/**
 * Controller for the Sign-Up screen in the Bookverse application.
 * This class handles user interactions during the sign-up process, including
 * validating user inputs, displaying messages, and navigating to other screens.
 */
public class SignUpController implements UiController {

    private final UserService userService = new UserService();

    @FXML
    private TextField text_user_name;

    @FXML
    private TextField text_email;

    @FXML
    private TextField text_full_name;

    @FXML
    private PasswordField text_password;

    @FXML
    private PasswordField text_confirm_password;

    @FXML
    private Button button_signup;

    @FXML
    private ImageView toggle_password;

    @FXML
    private ImageView toggle_confirm_password;

    @FXML
    private Text message_signup;

    @FXML
    private AnchorPane anchorpane_signup;

    @FXML
    private FontIcon button_delete_user_name;

    @FXML
    private FontIcon button_delete_full_name;

    @FXML
    private FontIcon button_delete_email;

    @FXML
    private FontIcon button_delete_password;

    @FXML
    private FontIcon button_delete_confirm_password;

    @FXML
    private AnchorPane anchorpane_effect;

    private FadeTransition fadeOut;

    /**
     * Default constructor for the SignUpController.
     * This is required for the FXML loader to instantiate the controller.
     */
    public SignUpController() {
    }

    /**
     * Initializes the UI components and their event handlers.
     * This method is automatically called after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        initUI();
        clickButton();
    }

    /**
     * {@inheritDoc}
     * Sets up UI animations for the sign-up screen.
     */
    @Override
    public void initUI() {
        setAnimationRotateTransition(anchorpane_signup, -90, 0);
        createAnimationDropRain(anchorpane_effect);
    }

    /**
     * {@inheritDoc}
     * Configures button click event handlers for the sign-up screen.
     */
    @Override
    public void clickButton() {
        button_signup.setOnAction(event -> handleSignUp(event));
        toggle_password.setOnMouseClicked(event ->
                togglePasswordVisibility(text_password, toggle_password, button_delete_password));
        toggle_confirm_password.setOnMouseClicked(event ->
                togglePasswordVisibility(text_confirm_password,
                        toggle_confirm_password, button_delete_confirm_password));
        button_delete_full_name.setOnMouseClicked(e -> text_full_name.setText(""));
        button_delete_user_name.setOnMouseClicked(e -> text_user_name.setText(""));
        button_delete_email.setOnMouseClicked(e -> text_email.setText(""));
        button_delete_password.setOnMouseClicked(e -> text_password.setText(""));
        button_delete_confirm_password.setOnMouseClicked(e -> text_confirm_password.setText(""));
        setButtonDeleteText();
    }

    /**
     * {@inheritDoc}
     * Currently unused in this controller but required by the UiController interface.
     */
    @Override
    public void setLoading(boolean isVisible) {
        // Not implemented
    }

    /**
     * Sets visibility for the delete buttons next to each input field based
     * on whether the field is empty.
     */
    public void setButtonDeleteText() {
        text_user_name.textProperty().addListener((observable, oldValue, newValue) ->
                button_delete_user_name.setVisible(!text_user_name.getText().isEmpty()));
        text_full_name.textProperty().addListener((observable, oldValue, newValue) ->
                button_delete_full_name.setVisible(!text_full_name.getText().isEmpty()));
        text_email.textProperty().addListener((observable, oldValue, newValue) ->
                button_delete_email.setVisible(!text_email.getText().isEmpty()));
        text_password.textProperty().addListener((observable, oldValue, newValue) ->
                button_delete_password.setVisible(!text_password.getText().isEmpty()));
        text_confirm_password.textProperty().addListener((observable, oldValue, newValue) ->
                button_delete_confirm_password.setVisible(!text_confirm_password.getText().isEmpty()));
    }

    /**
     * Handles the sign-up process when the "Sign Up" button is clicked.
     *
     * @param event the ActionEvent triggered by clicking the button.
     */
    public void handleSignUp(ActionEvent event) {
        String username = text_user_name.getText();
        String email = text_email.getText();
        String fullName = text_full_name.getText();
        String password = getPasswordText(text_password);
        String confirmPassword = getPasswordText(text_confirm_password);

        if (username.isEmpty() || email.isEmpty()
                || fullName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        if (!isValidPassword(password)) {
            showMessage("Password must be at least 8 characters," +
                    " contain at least one digit and one letter.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.");
            return;
        }

        if (!isValidEmail(email)) {
            showMessage("Invalid email format!");
            return;
        }

        if (!isValidUsername(username)) {
            showMessage("Invalid username format! Username must be at least 3 " +
                    "characters long and contain no special characters.");
            return;
        }

        if (userService.isUsernameTaken(username)) {
            Platform.runLater(() -> showMessage("Username already taken by another user!"));
            return;
        }

        if (userService.isEmailTaken(email)) {
            Platform.runLater(() -> showMessage("Email already taken by another user!"));
            return;
        }

        userService.addMember(username, password, email, fullName);
        Platform.runLater(() -> {
            showMessage("Sign up successful! Back to login...");
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(pauseEvent -> {
                try {
                    handleLoginClick(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            pause.play();
        });
    }

    /**
     * Displays a message on the sign-up screen with a fade-out effect.
     *
     * @param message the message to display.
     */
    private void showMessage(String message) {
        if (fadeOut != null && fadeOut.getStatus() == Animation.Status.RUNNING) {
            fadeOut.stop();
        }
        message_signup.setText(message);
        message_signup.setVisible(true);

        fadeOut = new FadeTransition(Duration.seconds(5), message_signup);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(fadeEvent -> message_signup.setVisible(false));
        fadeOut.play();
    }

    /**
     * Navigates to the login screen with a rotating animation effect.
     *
     * @param event the event triggering the navigation.
     * @throws IOException if an error occurs during navigation.
     */
    @FXML
    public void handleLoginClick(Event event) throws IOException {
        setAnimationRotateTransition(
                anchorpane_signup, 0, 90).setOnFinished(rotateEvent -> {
            try {
                toLogIn(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
