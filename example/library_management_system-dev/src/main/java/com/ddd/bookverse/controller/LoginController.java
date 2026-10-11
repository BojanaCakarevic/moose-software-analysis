package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.User;
import com.ddd.bookverse.service.BorrowService;
import com.ddd.bookverse.service.UserService;
import com.ddd.bookverse.singleton.UserSession;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

import static com.ddd.bookverse.item.AnchorPane.createAnChorPaneLoading;
import static com.ddd.bookverse.utils.AnimationUtil.*;
import static com.ddd.bookverse.utils.NavHelper.*;
import static com.ddd.bookverse.utils.ViewUtil.getPasswordText;
import static com.ddd.bookverse.utils.ViewUtil.togglePasswordVisibility;

/**
 * The {@code LoginController} class implements the {@link UiController}
 * interface and is responsible for managing the user interface and logic related to
 * the user login process in the application.
 * It handles user input for login, validates the credentials, and
 * navigates to the appropriate screen based on the login success or failure.
 */
public class LoginController implements UiController {

    private final UserService userService = new UserService();
    private final BorrowService borrowService = new BorrowService();

    @FXML
    private TextField text_user_name;
    @FXML
    private PasswordField text_password;
    @FXML
    private Button button_login;
    @FXML
    private Text sign_up;
    @FXML
    private Text messlogin;
    @FXML
    private ImageView toggle_password;
    @FXML
    private AnchorPane anchorpane_login;
    @FXML
    private AnchorPane loading;
    @FXML
    private AnchorPane anchorpane_effect;
    @FXML
    private FontIcon button_delete_user_name;
    @FXML
    private FontIcon button_delete_password;



    public LoginController() {
    }

    @FXML
    public void initialize() {
        initUI();
        clickButton();
    }

    // Ui Controller
    /**
     * Initializes the user interface (UI) for the login screen.
     * This method sets up animations for the login pane
     * and rain effect, prepares a loading indicator, and hides it initially.
     */
    @Override
    public void initUI() {
        // Applies a rotate transition animation to the login pane
        setAnimationRotateTransition(anchorpane_login, -90, 0);
        // Creates a rain effect animation for the background
        createAnimationDropRain(anchorpane_effect);
        // Initializes the loading indicator
        loading.getChildren().add(createAnChorPaneLoading(0, 0));
        loading.setVisible(false); // Hides the loading indicator initially
    }

    /**
     * Handles button clicks and user interactions.
     * This method defines actions for login button, password visibility
     * toggle, sign-up button, and clearing the username and password fields.
     */
    @Override
    public void clickButton() {
        button_login.setOnAction(event -> {
            try {
                handleLogin(event); // Handles login logic
            } catch (IOException e) {
                throw new RuntimeException(e); // Handles IOException during login
            }
        });

        toggle_password.setOnMouseClicked(event ->
                // Toggles password visibility
                togglePasswordVisibility(text_password, toggle_password, button_delete_password)
        );

        sign_up.setOnMouseClicked(event -> {
            try {
                handleSignUpClick(event); // Handles the sign-up action
            } catch (IOException e) {
                throw new RuntimeException(e); // Handles IOException during sign-up
            }
        });

        button_delete_user_name.setOnMouseClicked(e -> {
            text_user_name.setText(""); // Clears the username field
        });

        button_delete_password.setOnMouseClicked(e -> {
            text_password.setText(""); // Clears the password field
        });

        setButtonDeleteText(); // Sets or updates the delete button text as needed
    }

    /**
     * Controls the visibility of the loading indicator. Since the method is empty,
     * it might be used to show or hide the loading indicator in future logic, or
     * it could be a placeholder for further functionality.
     *
     * @param isVisible A boolean value indicating
     *                  whether the loading indicator should be shown (true) or hidden (false).
     */
    @Override
    public void setLoading(boolean isVisible) {
        // No implementation provided in the current method
    }

    /**
     * Sets up the visibility of delete buttons for the username
     * and password fields based on their content.
     * When the fields are empty, the corresponding
     * delete buttons are hidden; otherwise, they are visible.
     */
    public void setButtonDeleteText() {
        text_user_name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (text_user_name.getText().equals("")) {
                // Hides the delete button if the username field is empty
                button_delete_user_name.setVisible(false);
            } else {
                // Shows the delete button if the username field has text
                button_delete_user_name.setVisible(true);
            }
        });

        text_password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (text_password.getText().equals("")) {
                // Hides the delete button if the password field is empty
                button_delete_password.setVisible(false);
            } else {
                // Shows the delete button if the password field has text
                button_delete_password.setVisible(true);
            }
        });
    }

    /**
     * Handles the login process. This method validates the user's credentials
     * (username and password) andredirects the user to their corresponding dashboard
     * based on their role (member or admin).
     * If the authentication fails, an error message is displayed.
     *
     * @param event The event triggered by clicking the login button.
     * @throws IOException If an input-output error occurs during the transition to the dashboard.
     */
    public void handleLogin(ActionEvent event) throws IOException {
        String username = text_user_name.getText();
        String password = getPasswordText(text_password);
        loading.setVisible(true); // Shows the loading indicator during authentication

        // Authenticates the user with the provided username and password
        if (userService.authenticate(username, password)) {
            messlogin.setText("Login successful!"); // Displays a success message
            User user = userService.findUserByUsername(username);
            Long userId = user.getId(); // Retrieves the user ID
            String userRole = user.getRole(); // Retrieves the user's role (member or admin)
            UserSession userSession = UserSession.getInstance();
            userSession.login(userId, username, userRole); // Logs the user into the session

            // Pauses for a short duration before transitioning to the appropriate dashboard
            PauseTransition pause = new PauseTransition(Duration.seconds(0.2));
            pause.setOnFinished(e -> {
                try {
                    // Redirects to the appropriate dashboard based on the user's role
                    if (userSession.getRole() == User.memberRole) {
                        toDashboard(event); // Redirects to the member dashboard
                    } else if (userSession.getRole() == User.adminRole) {
                        toAdminDashboard(event); // Redirects to the admin dashboard
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            pause.play();
        } else {
            loading.setVisible(false); // Hides the loading indicator if authentication fails
            messlogin.setText("Invalid username or password."); // Displays an error message
        }
    }

    /**
     * Handles the sign-up button click event.
     * It animates the login pane and then redirects to the sign-up page.
     *
     * @param event The event triggered by clicking the sign-up button.
     * @throws IOException If an input-output error occurs during
     * the transition to the sign-up page.
     */
    public void handleSignUpClick(MouseEvent event) throws IOException {
        // Applies a rotate transition animation to the login pane, then transitions to the sign-up page
        setAnimationRotateTransition(anchorpane_login, 0, 90).setOnFinished(eventfinsed -> {
            try {
                toSignUp(event); // Redirects to the sign-up page
            } catch (IOException e) {
                throw new RuntimeException(e); // Handles IOException during sign-up redirection
            }
        });
    }

}
