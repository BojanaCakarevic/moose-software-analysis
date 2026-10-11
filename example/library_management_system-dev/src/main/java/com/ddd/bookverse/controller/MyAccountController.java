package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.model.User;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.service.UserService;
import com.ddd.bookverse.singleton.UserSession;
import com.ddd.bookverse.utils.ViewUtil;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import static com.ddd.bookverse.item.AnchorPane.createAnChorPaneLoading;
import static com.ddd.bookverse.utils.NavHelper.*;
import static com.ddd.bookverse.utils.ValidationUtil.*;
import static com.ddd.bookverse.utils.ViewUtil.*;
import static java.util.Collections.sort;

public class MyAccountController implements UiController {

    private final UserService userService = new UserService();
    private final UserSession userSession = UserSession.getInstance();

    // UPDATE INFO
    @FXML
    private AnchorPane update_info_pane;
    @FXML
    private TextField update_fullname;
    @FXML
    private TextField update_username;
    @FXML
    private TextField update_email;
    @FXML
    private Button update_button;
    @FXML
    private Button update_info_back;
    @FXML
    private Text update_info_message;

    // CHANGE PASSWORD
    @FXML
    private AnchorPane change_password_pane;
    @FXML
    private PasswordField current_password;
    @FXML
    private PasswordField new_password;
    @FXML
    private PasswordField confirm_password;
    @FXML
    private Button change_button;
    @FXML
    private Button change_password_back;
    @FXML
    private Text change_password_message;
    @FXML
    private ImageView toggle_current_password;
    @FXML
    private ImageView toggle_new_password;
    @FXML
    private ImageView toggle_confirm_password;


    // INFO PANE
    @FXML
    private AnchorPane info_pane;
    @FXML
    private TextField info_fullname;
    @FXML
    private TextField info_username;
    @FXML
    private TextField info_email;
    @FXML
    private Button update_info;
    @FXML
    private Button change_password;

    @FXML
    private FontIcon button_delete_user_name;
    @FXML
    private FontIcon button_delete_full_name;
    @FXML
    private FontIcon button_delete_email;
    @FXML
    private FontIcon button_delete_password;
    @FXML
    private FontIcon button_delete_new_password;
    @FXML
    private FontIcon button_delete_confirm_new_password;

    @FXML
    private javafx.scene.layout.AnchorPane rating_bar;
    @FXML
    private javafx.scene.layout.AnchorPane loading;

    private FadeTransition fadeOut;

    public MyAccountController() {
    }


    @FXML
    public void initialize() {
        initUI();
        clickButton();
    }
    /**
     * Initializes the user interface. It sets the visibility of the info pane,
     * update info pane, and change password pane.
     * It also loads the current user information and sets the loading indicator.
     */
    @Override
    public void initUI() {
        info_pane.setVisible(true);  // Makes the user info pane visible
        update_info_pane.setVisible(false);  // Hides the update info pane
        change_password_pane.setVisible(false);  // Hides the change password pane
        loadUserInfo();  // Loads the current user information
        setLoading(false);  // Hides the loading indicator
        loading.getChildren().add(createAnChorPaneLoading(0, 0));
    }

    /**
     * Sets up the click actions for various buttons and toggles in the UI.
     * It handles updating the information, changing the password, and navigating between panes.
     */
    @Override
    public void clickButton() {
        update_info.setOnAction(event -> onUpdateInfo());
        change_password.setOnAction(event -> onChangePassword());
        update_button.setOnAction(event -> onUpdateButtonClick());
        change_button.setOnAction(event -> onChangeButtonClick());
        update_info_back.setOnAction(event -> onBackToInfoFromUpdate());
        change_password_back.setOnAction(event -> onBackToInfoFromChange());

        // Toggles password visibility for the respective fields
        toggle_current_password.setOnMouseClicked(
                event -> togglePasswordVisibility(
                        current_password, toggle_current_password,
                        button_delete_password));
        toggle_new_password.setOnMouseClicked(
                event -> togglePasswordVisibility(
                        new_password, toggle_new_password, button_delete_new_password));
        toggle_confirm_password.setOnMouseClicked(
                event -> togglePasswordVisibility(
                        confirm_password, toggle_confirm_password,
                        button_delete_confirm_new_password));

        // Clears the respective input fields when delete buttons are clicked
        button_delete_full_name.setOnMouseClicked(e -> { update_fullname.setText(""); });
        button_delete_user_name.setOnMouseClicked(e -> { update_username.setText(""); });
        button_delete_email.setOnMouseClicked(e -> { update_email.setText(""); });
        button_delete_password.setOnMouseClicked(e -> { current_password.setText(""); });
        button_delete_new_password.setOnMouseClicked(e -> { new_password.setText(""); });
        button_delete_confirm_new_password.setOnMouseClicked(e -> { confirm_password.setText(""); });

        setButtonDeleteText();  // Sets up delete buttons visibility based on text fields content
    }

    /**
     * Toggles the visibility of the loading indicator.
     *
     * @param isVisible Whether the loading indicator should be visible.
     */
    @Override
    public void setLoading(boolean isVisible) {
        if (loading != null) loading.setVisible(isVisible);
    }

    /**
     * Sets the visibility of the delete buttons based
     * on the content of the respective text fields.
     * Each text field will toggle its delete button visibility when the content changes.
     */
    public void setButtonDeleteText() {
        // Listens to changes in each text field and shows/hides delete buttons accordingly
        update_username.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_user_name.setVisible(!update_username.getText().isEmpty());
        });
        update_fullname.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_full_name.setVisible(!update_fullname.getText().isEmpty());
        });
        update_email.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_email.setVisible(!update_email.getText().isEmpty());
        });

        current_password.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_password.setVisible(!current_password.getText().isEmpty());
        });
        new_password.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_new_password.setVisible(!new_password.getText().isEmpty());
        });

        confirm_password.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_confirm_new_password.setVisible(!confirm_password.getText().isEmpty());
        });
    }

    /**
     * Loads the current user's information into the UI.
     * If the user is logged in, it will populate
     * the info and update text fields with their data.
     * Otherwise, it will show a message indicating that no user is logged in.
     */
    private void loadUserInfo() {
        User currentUser = userService.findUserById(userSession.getUserId());

        if (currentUser != null) {
            info_fullname.setText(currentUser.getFullName());  // Displays the user's full name
            info_username.setText(currentUser.getUsername());  // Displays the user's username
            info_email.setText(currentUser.getEmail());  // Displays the user's email

            update_fullname.setText(currentUser.getFullName());
            update_username.setText(currentUser.getUsername());
            update_email.setText(currentUser.getEmail());
        } else {
            showMessage(update_info_message, "No user is logged in!");
        }
    }

    /**
     * Handles the action to navigate to the update info pane.
     */
    @FXML
    public void onUpdateInfo() {
        info_pane.setVisible(false);  // Hides the info pane
        update_info_pane.setVisible(true);  // Shows the update info pane
    }

    /**
     * Handles the action to navigate to the change password pane.
     */
    @FXML
    public void onChangePassword() {
        info_pane.setVisible(false);  // Hides the info pane
        change_password_pane.setVisible(true);  // Shows the change password pane
    }

    /**
     * Handles the action to navigate back from the update info pane to the info pane.
     */
    @FXML
    public void onBackToInfoFromUpdate() {
        update_info_message.setText("");  // Clears any messages in the update info section
        update_info_pane.setVisible(false);  // Hides the update info pane
        info_pane.setVisible(true);  // Shows the info pane
    }

    /**
     * Handles the action to navigate back from the change password pane to the info pane.
     * It also clears the password fields and resets the message in the change password pane.
     */
    @FXML
    public void onBackToInfoFromChange() {
        closePasswordIfVisible(current_password, toggle_current_password);
        closePasswordIfVisible(new_password, toggle_new_password);
        closePasswordIfVisible(confirm_password, toggle_confirm_password);

        current_password.setText("");  // Clears the current password field
        new_password.setText("");  // Clears the new password field
        confirm_password.setText("");  // Clears the confirm password field
        change_password_message.setText("");  // Clears any messages in the change password section
        change_password_pane.setVisible(false);  // Hides the change password pane
        info_pane.setVisible(true);  // Shows the info pane
    }

    /**
     * Handles the action to update the user's information.
     * It validates the input fields and updates the user data if valid.
     */
    @FXML
    public void onUpdateButtonClick() {
        // Validates and updates user information
    }

    /**
     * Handles the action to change the user's password.
     * It validates the password fields and updates the password if valid.
     */
    @FXML
    public void onChangeButtonClick() {
        // Validates and changes the password
    }

    /**
     * Displays a message on the UI, with a fade-out effect after 5 seconds.
     *
     * @param text The text element to display the message.
     * @param message The message to be displayed.
     */
    public void showMessage(Text text, String message) {
        // Displays a message and fades out after a delay
    }
}
