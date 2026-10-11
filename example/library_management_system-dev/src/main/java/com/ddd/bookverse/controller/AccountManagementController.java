package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.Borrow;
import com.ddd.bookverse.model.Member;
import com.ddd.bookverse.model.User;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.service.BorrowService;
import com.ddd.bookverse.service.UserService;
import com.ddd.bookverse.utils.ViewUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ddd.bookverse.item.AnchorPane.*;
import static com.ddd.bookverse.model.Borrow.BORROWING;
import static com.ddd.bookverse.model.Borrow.OVERDUE;
import static com.ddd.bookverse.model.User.adminRole;
import static com.ddd.bookverse.model.User.memberRole;
import static com.ddd.bookverse.utils.AnimationUtil.setAnimationBlink;
import static com.ddd.bookverse.utils.ValidationUtil.*;
/**
 * The `AccountManagementController` class is responsible for managing
 * account-related operations in the application.
 * This includes handling the UI interactions for account management,
 * deleting accounts, and searching for specific accounts.
 * It implements them `UiController`, `DeleteController`,
 * and `SearchController` interfaces, providing methods for UI
 * setup, deletion confirmation, and searching functionalities.
 *
 * <p>This class is designed to work in conjunction
 * with the application's account management features, enabling users
 * to interact with accounts through the user
 * interface, initiate account deletions, and perform searches for specific
 * accounts based on criteria.</p>
 *
 * Implements:
 * - {@link UiController} for UI handling and interactions.
 * - {@link DeleteController} for account deletion functionality.
 * - {@link SearchController} for account search functionality.
 *
 * @see UiController
 * @see DeleteController
 * @see SearchController
 */
public class AccountManagementController implements UiController,
        DeleteController,SearchController {
    private UserService userService = new UserService();
    private BookService bookService = new BookService();

    private BorrowService borrowService = new BorrowService();
    private static final String USERNAME = "username";
    private static final String FULLNAME= "fullname";
    private static final String EMAIL  = "email";
    private static final String USER_ID = "id";
    private static final String ALL = "all";
    private final int  MAX_SUGGESTION = 10;
    private User userDelete;
    private DeleteConfirmationController deleteConfirmationController ;
    private AnchorPane anchorPaneDelete;
    private FadeTransition historyLoadingAnim = null;
    private int pages;
    private int current_page;
    private final int MAX_BOOKS_IN_PAGE = 20;
    private List<VBox> listpageUser = new ArrayList<>();
    private List<User> list_user_current = new ArrayList<>();

    @FXML
    private AnchorPane  mainAnchorPane;
    @FXML
    private VBox vbox_list_member;
    @FXML
    private AnchorPane anchorpane_detail;
    @FXML
    private Button back_detail;
    @FXML
    private Text full_name;
    @FXML
    private Text user_name;
    @FXML
    private Text email_user;
    @FXML
    private Text id_user;
    @FXML
    private VBox vbox_list_history;
    @FXML
    private AnchorPane anchorpane_addaccount;
    @FXML
    private Button button_add;
    @FXML
    private Button button_cancel;
    @FXML
    private Button button_add_form;
    @FXML
    private TextField textfield_fullname;
    @FXML
    private TextField textfield_username;
    @FXML
    private TextField textfield_email;
    @FXML
    private TextField textfield_password;
    @FXML
    private TextField textfield_confirm_password;
    @FXML
    private PasswordField passwordfield_password;
    @FXML
    private PasswordField passwordfield_confirm_password;
    @FXML
    private Text announcement;
    @FXML
    private Button button_update_form;

    @FXML
    private TextField search_bar;
    @FXML
    private FontIcon button_search;

    @FXML
    private MenuButton button_option_search;
    @FXML
    private AnchorPane loading;
    @FXML
    private VBox vbox_result_search;
    @FXML
    private ScrollPane scroll_result_search;

    @FXML
    ImageView toggle_password;
    @FXML
    ImageView toggle_confirm_password;

    @FXML
    private Text account_modal_label;

    @FXML
    private Text account_num_borrowing;
    @FXML
    private Text account_num_overdue;
    @FXML
    private FontIcon button_delete_search;
    @FXML
    private FontIcon button_delete_fullname;
    @FXML
    private FontIcon button_delete_username;
    @FXML
    private FontIcon button_delete_email;
    @FXML
    private FontIcon button_delete_password;
    @FXML
    private FontIcon button_delete_confirm_password;
    @FXML
    private AnchorPane history_loading;
    @FXML
    private HBox hBox_Pages;
    @FXML
    private TextField text_pages;
    @FXML
    private MenuButton button_option_sort;
    @FXML
    private Text total_pages;
    @FXML
    private MenuButton button_role;



    public void initialize() {
        initUI();
        clickButton();
    }

    // UiController

    /**
     * Initializes the user interface components for the account management screen.
     * This method sets up animations, loads user data, and prepares various UI elements.
     */
    @Override
    public void initUI() {
        // Disable the loading indicator initially
        setLoading(false);

        // Set up a blinking animation for the history loading element
        historyLoadingAnim = setAnimationBlink(history_loading);

        // Load user data based on the role text on the button
        loadUser(button_role.getText());

        // Add a loading animation pane to the UI
        loading.getChildren().add(createAnChorPaneLoading(0, 0));

        // Hide the account addition panel
        anchorpane_addaccount.setVisible(false);

        // Render the search results
        renderResultSearch();

        // Set up the delete confirmation controller for handling delete actions
        setDeleteConfirmationController();

        // Assign the current instance of the account management controller
        accountManagementController = this;
    }

    /**
     * Configures the actions for various buttons and UI elements on the account management screen.
     * This method binds event handlers to buttons, toggle switches, and menu items, enabling
     * interactive functionality such as adding accounts, toggling password visibility,
     * searching, sorting, and managing pagination.
     */
    @Override
    public void clickButton() {
        // Back button: Returns to the account management screen
        back_detail.setOnAction(e -> backAccountManagement());

        // Add button: Displays the form for adding a new account
        button_add.setOnAction(e -> {
            button_update_form.setVisible(false);
            button_add_form.setVisible(true);
            anchorpane_addaccount.setVisible(true);
        });

        // Cancel button: Clears all fields in the add account form and hides it
        button_cancel.setOnAction(e -> {
            passwordfield_password.setText("");
            passwordfield_confirm_password.setText("");
            textfield_email.setText("");
            textfield_fullname.setText("");
            textfield_username.setText("");
            anchorpane_addaccount.setVisible(false);
        });

        // Add form button: Calls the method to add a new account
        button_add_form.setOnAction(e -> addAccount());

        // Search button: Triggers the searchContent method when clicked
        button_search.setOnMouseClicked(e -> searchContent());

        // Toggle password visibility for the main password field
        toggle_password.setOnMouseClicked(e -> {
            if (passwordfield_password.isVisible()) {
                toggle_password.setImage(new Image(ViewUtil.class.
                        getResource("/img/openpassword.png")
                        .toExternalForm()));
                passwordfield_password.setVisible(false);
                textfield_password.setText(passwordfield_password.getText());
                textfield_password.setVisible(true);
                if (!textfield_password.getText().isEmpty())
                    button_delete_password.setVisible(true);
            } else {
                toggle_password.setImage(new Image(ViewUtil.class
                        .getResource("/img/closepassword.png")
                        .toExternalForm()));
                textfield_password.setVisible(false);
                passwordfield_password.setText(textfield_password.getText());
                passwordfield_password.setVisible(true);
            }
        });

        // Toggle password visibility for the confirmation password field
        toggle_confirm_password.setOnMouseClicked(e -> {
            if (passwordfield_confirm_password.isVisible()) {
                toggle_confirm_password.setImage(new Image(ViewUtil
                        .class.getResource("/img/openpassword.png")
                        .toExternalForm()));
                passwordfield_confirm_password.setVisible(false);
                textfield_confirm_password.setText(passwordfield_confirm_password.getText());
                textfield_confirm_password.setVisible(true);
                if (!textfield_confirm_password.getText().isEmpty())
                    button_delete_confirm_password.setVisible(true);
            } else {
                toggle_confirm_password.setImage(new Image(ViewUtil
                        .class.getResource("/img/closepassword.png")
                        .toExternalForm()));
                textfield_confirm_password.setVisible(false);
                passwordfield_confirm_password.setText(textfield_confirm_password.getText());
                passwordfield_confirm_password.setVisible(true);
            }
        });

        // Listener for delete confirmation status
        deleteConfirmationController.statusProperty()
                .addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                deleteConfirmation();
            }
        });

        // Set up additional delete and reset actions
        buttonDeleteAll();
        setButtonDeleteText();

        // Configure menu items for search options
        for (MenuItem item : button_option_search.getItems()) {
            item.setOnAction(event -> button_option_search.setText(item.getText()));
        }

        // Configure menu items for sort options
        for (MenuItem item : button_option_sort.getItems()) {
            item.setOnAction(event -> {
                button_option_sort.setText(item.getText());
                sort(item.getText());
            });
        }

        // Configure menu items for role selection
        for (MenuItem item : button_role.getItems()) {
            item.setOnAction(event -> {
                button_role.setText(item.getText());
                searchContent();
            });
        }

        // Handle pagination input
        text_pages.setOnAction(e -> {
            Integer p = Integer.parseInt(text_pages.getText());
            if (p > pages) changePages(pages);
            else if (p < 1) changePages(1);
            else changePages(p);
        });
    }

    /**
     * Toggles the visibility of the loading indicator.
     *
     * @param isVisible a boolean value indicating whether the loading indicator
     *                  should be visible (true) or hidden (false).
     */
    @Override
    public void setLoading(boolean isVisible) {
        loading.setVisible(isVisible);
    }

    // DeleteController
    /**
     * Exits the delete confirmation dialog.
     * Sets the status of the delete confirmation to false and closes the dialog.
     */
    @Override
    public void ExitDeleteConfirmation() {
        deleteConfirmationController.setStatus(false);
        deleteConfirmationController.exit();
    }

    /**
     * Initializes and sets up the delete confirmation controller.
     * This method loads the FXML file for the delete confirmation dialog and
     * assigns the corresponding controller instance.
     */
    @Override
    public void setDeleteConfirmationController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/fxml/deleteConfirmation.fxml"));
            Parent root = loader.load();
            anchorPaneDelete = (AnchorPane) root;
            deleteConfirmationController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace(); // Log the error if the FXML file cannot be loaded
        }
    }

    /**
     * Handles the delete confirmation logic.
     * Checks if the user can be deleted by verifying their borrowing and overdue statuses.
     * - If the user has no borrowing or overdue items, they are deleted, and the UI is updated.
     * - Otherwise, an error message is displayed in the delete confirmation dialog.
     */
    @Override
    public void deleteConfirmation() {
        if (deleteConfirmationController.getStatus()) {
            // Fetch the number of borrowing and overdue items for the user
            int numBorrowing = borrowService
                    .getNumBorrowsOfUserWithStatus(userDelete.getId(), BORROWING);
            int numOverdue = borrowService
                    .getNumBorrowsOfUserWithStatus(userDelete.getId(), OVERDUE);

            // Check if the user can be safely deleted
            if (numOverdue + numBorrowing == 0) {
                // Delete the user and reload the user list
                userService.deleteUser(userDelete.getId());
                loadUser(button_role.getText());
                ExitDeleteConfirmation();
            } else {
                // Show an error message if the user cannot be deleted
                deleteConfirmationController.showError(
                        "Unable to delete this user as they currently have books on loan"
                );
            }
        }
    }

    /**
     * Configures delete buttons for clearing input fields.
     * This method assigns event handlers to delete buttons associated with
     * various input fields, such as search bar, full name, username, email,
     * password, and confirm password fields.
     */
    public void buttonDeleteAll() {
        // Clear the search bar when the delete button is clicked
        button_delete_search.setOnMouseClicked(e -> search_bar.setText(""));

        // Clear the full name field when the delete button is clicked
        button_delete_fullname.setOnMouseClicked(e -> textfield_fullname.setText(""));

        // Clear the username field when the delete button is clicked
        button_delete_username.setOnMouseClicked(e -> textfield_username.setText(""));

        // Clear the email field when the delete button is clicked
        button_delete_email.setOnMouseClicked(e -> textfield_email.setText(""));

        // Clear the password fields when the delete button is clicked
        button_delete_password.setOnMouseClicked(e -> {
            passwordfield_password.setText("");
            textfield_password.setText("");
        });

        // Clear the confirm password fields when the delete button is clicked
        button_delete_confirm_password.setOnMouseClicked(e -> {
            passwordfield_confirm_password.setText("");
            textfield_confirm_password.setText("");
        });
    }

    /**
     * Dynamically shows or hides delete buttons based on the input fields' content.
     * This method adds listeners to input fields to toggle the visibility of
     * corresponding delete buttons. If an input field is empty, its delete button
     * is hidden; otherwise, the button is shown.
     */
    public void setButtonDeleteText() {
        // Listener for the search bar
        search_bar.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_search.setVisible(!search_bar.getText().isEmpty());
        });

        // Listener for the full name field
        textfield_fullname.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_fullname.setVisible(!textfield_fullname.getText().isEmpty());
        });

        // Listener for the username field
        textfield_username.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_username.setVisible(!textfield_username.getText().isEmpty());
        });

        // Listener for the email field
        textfield_email.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_email.setVisible(!textfield_email.getText().isEmpty());
        });

        // Listener for the password field
        passwordfield_password.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_password.setVisible(!passwordfield_password.getText().isEmpty());
            textfield_password.setText(passwordfield_password.getText());
        });

        // Listener for the confirm password field
        passwordfield_confirm_password.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            button_delete_confirm_password
                    .setVisible(!passwordfield_confirm_password.getText().isEmpty());
            textfield_confirm_password.setText(passwordfield_confirm_password.getText());
        });

        // Listener for the plain text password field
        textfield_password.textProperty().addListener((observable, oldValue, newValue) -> {
            button_delete_password.setVisible(!textfield_password.getText().isEmpty());
            passwordfield_password.setText(textfield_password.getText());
        });

        // Listener for the plain text confirm password field
        textfield_confirm_password.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            button_delete_confirm_password
                    .setVisible(!textfield_confirm_password.getText().isEmpty());
            passwordfield_confirm_password
                    .setText(textfield_confirm_password.getText());
        });
    }

    // SearchController
    /**
     * Determines the type of search based on the selected option in the search menu.
     *
     * @return A string representing the search type. It can be one of the following:
     *         USERNAME, FULLNAME, EMAIL, USER_ID, or ALL.
     */
    @Override
    public String getSearchType() {
        String type_search;
        if (button_option_search.getText().equals("Username")) {
            type_search = USERNAME;
        } else if (button_option_search.getText().equals("Fullname")) {
            type_search = FULLNAME;
        } else if (button_option_search.getText().equals("Email")) {
            type_search = EMAIL;
        } else if (button_option_search.getText().equals("User ID")) {
            type_search = USER_ID;
        } else {
            type_search = ALL;
        }
        return type_search;
    }

    /**
     * Initiates a search based on the user's input and selected search type.
     * Calls {@link #search(String, String, boolean)} with the current search query
     * and search type.
     */
    @Override
    public void searchContent() {
        String type_search = getSearchType();
        search(search_bar.getText(), type_search, false);
    }

    /**
     * Performs a search query and updates the UI with the results.
     *
     * @param query         The search query entered by the user.
     * @param type          The type of search to perform (e.g., USERNAME, FULLNAME).
     * @param isAddHistory  A boolean indicating whether the query should be added to history.
     */
    @Override
    public void search(String query, String type, boolean isAddHistory) {
        scroll_result_search.setVisible(false);
        setLoading(true);
        listpageUser.clear();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<User> listUser = new ArrayList<>();

                // Perform search based on type
                switch (type) {
                    case USERNAME -> listUser
                            = userService.findByUsernameAndRole(query, button_role.getText());
                    case FULLNAME -> listUser
                            = userService.findByFullnameAndRole(query, button_role.getText());
                    case EMAIL -> listUser
                            = userService.findByEmailAndRole(query, button_role.getText());
                    case USER_ID -> {
                        long userId;
                        try {
                            userId = Long.parseLong(query);
                        } catch (NumberFormatException e) {
                            userId = -1;
                        }
                        listUser = userService
                                .findUserByIDAndRole(userId, button_role.getText());
                    }
                }

                // Process and display results
                list_user_current = listUser;
                pages = (int) Math.ceil(1.0 * listUser.size() / MAX_BOOKS_IN_PAGE);
                total_pages.setText("/" + pages);
                text_pages.setText("1");
                current_page = 1;
                List<User> currentUsers = new ArrayList<>();
                AtomicInteger index = new AtomicInteger(0);
                for (User user : listUser) {
                    int currentIndex = index.getAndIncrement();
                    int sizeList = listUser.size();
                    Platform.runLater(() -> {
                        currentUsers.add(user);
                        if (currentUsers.size() >= MAX_BOOKS_IN_PAGE
                                || currentIndex == sizeList - 1) {
                            VBox vBox = new VBox();
                            for (User b : currentUsers) {
                                vBox.getChildren().add(createAnchorPaneAccount(b));
                            }
                            if (listpageUser.isEmpty()) {
                                vbox_list_member.getChildren().add(vBox);
                            }
                            listpageUser.add(vBox);
                            currentUsers.clear();
                        }
                    });
                }
                return null;
            }
        };

        // Handle task success and failure
        task.setOnSucceeded(event -> {
            createHboxPages(pages, 1);
            setLoading(false);
        });
        task.setOnFailed(event -> {
            setLoading(false);
            Throwable exception = task.getException();
            exception.printStackTrace();
        });

        vbox_list_member.getChildren().clear();
        new Thread(task).start();
    }

    /**
     * Displays search suggestions based on the current input in the search bar.
     * Populates a vertical box with suggestions and allows the user to select one
     * to autofill the search bar.
     */
    @Override
    public void searchResult() {
        String type_search = getSearchType();
        List<User> listUser = new ArrayList<>();

        // Perform search to fetch suggestions
        switch (type_search) {
            case USERNAME -> listUser = userService
                    .findByUsernameAndRole(search_bar.getText(), button_role.getText());
            case FULLNAME -> listUser = userService
                    .findByFullnameAndRole(search_bar.getText(), button_role.getText());
            case EMAIL -> listUser = userService
                    .findByEmailAndRole(search_bar.getText(), button_role.getText());
            case USER_ID -> {
                long userId;
                try {
                    userId = Long.parseLong(search_bar.getText());
                } catch (NumberFormatException e) {
                    userId = -1;
                }
                listUser = userService.findUserByIDAndRole(userId, button_role.getText());
            }
        }

        // Display suggestions
        int limit = 0;
        for (User user : listUser) {
            TextField textField = new TextField();
            textField.setFont(Font.font(14));
            switch (type_search) {
                case USERNAME -> textField.setText(user.getUsername());
                case FULLNAME -> textField.setText(user.getFullName());
                case EMAIL -> textField.setText(user.getEmail());
                case USER_ID -> textField.setText(String.valueOf(user.getId()));
                default -> textField.setText(user.getFullName());
            }
            textField.setStyle("-fx-background-color: transparent; "
                            + "-fx-cursor: hand; -fx-text-fill:#333333");
            textField.setEditable(false);
            textField.setOnMouseEntered(e ->
                    textField.setStyle("-fx-background-color:#5f43b2; "
                            + "-fx-text-fill:white; -fx-cursor: hand;"));
            textField.setOnMouseExited(e ->
                    textField.setStyle("-fx-background-color: transparent; "
                            + "-fx-text-fill:#333333; -fx-cursor: hand;"));
            textField.setOnMouseClicked(e -> {
                search_bar.setText(textField.getText());
                searchContent();
            });
            vbox_result_search.getChildren().add(textField);
            scroll_result_search.setVisible(true);
            if (limit++ > MAX_SUGGESTION) {
                break;
            }
        }
    }

    /**
     * Initializes the dynamic search suggestion functionality.
     * Adds a listener to the search bar to trigger suggestions when the input changes.
     */
    @Override
    public void renderResultSearch() {
        vbox_result_search.getChildren().clear();
        scroll_result_search.setVisible(false);
        StringProperty textProperty = search_bar.textProperty();
        textProperty.addListener((observable, oldValue, newValue) -> {
            vbox_result_search.getChildren().clear();
            scroll_result_search.setVisible(false);
            if (search_bar.getText().length() > 0) {
                searchResult();
            }
        });
    }

    // account management interface processing
    /**
     * Adds a new account to the system.
     * Validates the input fields and ensures the username and email are unique.
     * Displays error messages if validation fails.
     */
    public void addAccount() {
        String username = textfield_username.getText();
        String email = textfield_email.getText();
        String fullName = textfield_fullname.getText();
        String password = passwordfield_password.getText();
        String confirmPassword = passwordfield_confirm_password.getText();

        // Validate fields
        if (username.isEmpty() || email.isEmpty()
                || fullName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        if (!isValidPassword(password)) {
            showMessage("Password must be at least 8 characters, " +
                    "contain at least one digit and one letter.");
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
            showMessage("Invalid username format! Username must be at least 3 characters long "
                    + "and contain no special characters.");
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

        // Add account based on role
        String role = button_role.getText();
        if (role.equals(adminRole)) {
            userService.addAdmin(username, password, email, fullName);
        } else if (role.equals(memberRole)) {
            userService.addMember(username, password, email, fullName);
        }

        // Reset form and reload users
        resetForm();
        loadUser(role);
    }

    /**
     * Displays a message on the announcement label.
     *
     * @param message The message to display.
     */
    private void showMessage(String message) {
        announcement.setText(message);
        announcement.setVisible(true);
    }

    /**
     * Updates an existing user account in the system.
     * Allows changing username, email, full name, and optionally password.
     * Validates the input fields and ensures uniqueness of username and email.
     *
     * @param user The user to update.
     */
    public void updateAccount(User user) {
        String currUsername = user.getUsername();
        String currEmail = user.getEmail();

        String username = textfield_username.getText();
        String email = textfield_email.getText();
        String fullName = textfield_fullname.getText();
        String password = passwordfield_password.getText();
        String confirmPassword = passwordfield_confirm_password.getText();
        boolean changePassword = (!password.isEmpty() || !confirmPassword.isEmpty());

        // Validate fields
        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty()) {
            showMessage("Please fill in all info fields.");
            return;
        }

        if (changePassword) {
            if (!isValidPassword(password)) {
                showMessage("Password must be at least 8 characters, contain at least one digit and one letter.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showMessage("Passwords do not match.");
                return;
            }
        }

        if (!currEmail.equals(email) && !isValidEmail(email)) {
            showMessage("Invalid email format!");
            return;
        }

        if (!currUsername.equals(username) && !isValidUsername(username)) {
            showMessage("Invalid username format! Username must be at least 3 characters long and contain no special characters.");
            return;
        }

        if (!currUsername.equals(username) && userService.isUsernameTaken(username)) {
            Platform.runLater(() -> showMessage("Username already taken by another user!"));
            return;
        }

        if (!currEmail.equals(email) && userService.isEmailTaken(email)) {
            Platform.runLater(() -> showMessage("Email already taken by another user!"));
            return;
        }

        // Update account
        Member member = new Member();
        member.setEmail(textfield_email.getText());
        member.setFullName(textfield_fullname.getText());
        member.setUsername(textfield_username.getText());
        member.setId(user.getId());
        if (changePassword) {
            member.setPassword(passwordfield_password.getText());
        }
        userService.updateUser(member);

        // Reset form and reload users
        resetForm();
        loadUser(button_role.getText());
    }

    /**
     * Resets the input form and hides the account creation modal.
     */
    private void resetForm() {
        anchorpane_addaccount.setVisible(false);
        account_modal_label.setText("Create New Account");
        announcement.setText("");
        announcement.setVisible(false);
        passwordfield_password.setText("");
        passwordfield_confirm_password.setText("");
        textfield_email.setText("");
        textfield_fullname.setText("");
        textfield_username.setText("");
        textfield_password.setText("");
        textfield_confirm_password.setText("");
    }

    /**
     * Initiates the deletion process for a specified user account.
     * Displays a confirmation dialog for the deletion.
     *
     * @param user The {@link User} object representing the account to be deleted.
     */
    public void deleteAccount(User user) {
        this.userDelete = user;
        deleteConfirmationController.render("Are you sure to delete this account?");
        if (!mainAnchorPane.getChildren().contains(anchorPaneDelete)) {
            mainAnchorPane.getChildren().add(anchorPaneDelete);
        }
    }

    /**
     * Returns to the account management view by hiding the account detail pane
     * and making the member list visible.
     */
    public void backAccountManagement() {
        vbox_list_member.setVisible(true);
        anchorpane_detail.setVisible(false);
    }

    /**
     * Loads a list of users based on the specified role and displays them in the UI.
     * This method clears the current user list, resets pagination, and retrieves user
     * data asynchronously.
     *
     * @param role The role of the users to load (e.g., "Admin", "Member").
     */
    public void loadUser(String role) {
        vbox_list_member.setSpacing(5);
        vbox_list_member.getChildren().clear();
        listpageUser.clear();
        setLoading(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<User> listUser = userService.findAllUsersByRole(role);
                pages = (int) Math.ceil(1.0 * listUser.size() / MAX_BOOKS_IN_PAGE);
                total_pages.setText("/" + pages);
                text_pages.setText("1");
                current_page = 1;
                list_user_current = listUser;

                List<User> currentUsers = new ArrayList<>();
                AtomicInteger index = new AtomicInteger(0);

                for (User user : listUser) {
                    int currentIndex = index.getAndIncrement();
                    int sizeList = listUser.size();

                    Platform.runLater(() -> {
                        currentUsers.add(user);

                        // Group users into pages and display them in the VBox
                        if (currentUsers.size() >= MAX_BOOKS_IN_PAGE || currentIndex
                                == sizeList - 1) {
                            VBox vBox = new VBox();
                            for (User u : currentUsers) {
                                vBox.getChildren().add(createAnchorPaneAccount(u));
                            }
                            if (listpageUser.isEmpty()) {
                                vbox_list_member.getChildren().add(vBox);
                            }
                            listpageUser.add(vBox);
                            currentUsers.clear();
                        }
                    });
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            setLoading(false);
            createHboxPages(pages, 1); // Create pagination controls
        });

        task.setOnFailed(event -> {
            setLoading(false);
        });

        new Thread(task).start(); // Execute the task in a new thread
    }

    /**
     * Handles the event when a user is clicked to view detailed information.
     * This method updates the UI to display the user's details and borrowing history.
     *
     * @param user The user whose details are to be displayed.
     */
    public void clickDetails(User user) {
        vbox_list_member.setVisible(false); // Hide the list of members
        anchorpane_detail.setVisible(true); // Show the detail view
        anchorpane_addaccount.setVisible(false); // Hide the add account form

        // Update the borrowing records to reflect overdue statuses
        borrowService.updateOverdueBorrowsByUserId(user.getId());

        // Display detailed user information and borrowing history
        setDetailUser(user);
        setHistoryUser(user);
    }

    /**
     * Populates the UI with the detailed information of the specified user.
     * This includes personal details, borrowing stats, and overdue stats.
     *
     * @param user The user whose details are to be displayed.
     */
    public void setDetailUser(User user) {
        full_name.setText(user.getFullName()); // Display the full name
        user_name.setText(user.getUsername()); // Display the username
        email_user.setText(user.getEmail()); // Display the email
        id_user.setText(String.valueOf(user.getId())); // Display the user ID

        // Display the number of books currently borrowed by the user
        account_num_borrowing.setText(String.valueOf(
                bookService.getBooksByUserAndStatus(user.getId(), "Borrowing").size()
        ));

        // Display the number of overdue books for the user
        account_num_overdue.setText(String.valueOf(
                bookService.getBooksByUserAndStatus(user.getId(), "Overdue").size()
        ));
    }

    /**
     * Loads and displays the borrowing history for the specified user.
     * This method retrieves the borrow history from the backend service
     * and displays it in the UI.
     *
     * @param user The user whose borrowing history is to be displayed.
     */
    public void setHistoryUser(User user) {
        vbox_list_history.setSpacing(10); // Set spacing between history items
        vbox_list_history.getChildren().clear(); // Clear previous history items
        history_loading.setVisible(true); // Show loading indicator
        historyLoadingAnim.play(); // Play loading animation

        // Asynchronously load the borrowing history
        Task<List> task = new Task<List>() {
            @Override
            protected List call() throws Exception {
                List<Borrow> listBorrow = new ArrayList<>();
                listBorrow = borrowService.getBorrowByUserId(user.getId()); // Fetch borrow history
                Thread.sleep(250); // Simulate a short delay for loading
                return listBorrow;
            }
        };

        // When the history is loaded, update the UI
        task.setOnSucceeded(e -> {
            List<Borrow> listBorrow = task.getValue();
            for (Borrow borrow : listBorrow) {
                // Add each borrow record to the UI
                vbox_list_history.getChildren().add(createAnchorPaneHistoryUser(borrow));
            }
            history_loading.setVisible(false); // Hide loading indicator
            historyLoadingAnim.stop(); // Stop loading animation
        });

        new Thread(task).start(); // Start the task in a new thread
    }

    /**
     * Prepares and displays the form for updating a user's account details.
     * This method fills the form fields with the current user's information
     * and provides an option to update the account.
     *
     * @param user The user whose account details are to be updated.
     */
    public void showFormUpdate(User user) {
        anchorpane_addaccount.setVisible(true); // Show the account update form
        account_modal_label.setText("Update Account"); // Set the modal label to "Update Account"
        passwordfield_password.setText(""); // Clear password fields
        passwordfield_confirm_password.setText(""); // Clear confirm password field

        // Fill the form fields with the current user's details
        textfield_email.setText(user.getEmail());
        textfield_fullname.setText(user.getFullName());
        textfield_username.setText(user.getUsername());

        // Hide the "Add" button and show the "Update" button
        button_add_form.setVisible(false);
        button_update_form.setVisible(true);

        // Handle the update action
        button_update_form.setOnAction(e -> updateAccount(user));
    }

    /**
     * Creates and displays the pagination controls for navigating between pages.
     * This method dynamically generates buttons for page navigation based on the total
     * number of pages and the current starting page.
     *
     * @param pages The total number of pages.
     * @param start The starting page number for the current page range.
     */
    public void createHboxPages(int pages, int start) {
        hBox_Pages.getChildren().clear(); // Clear previous pagination controls
        hBox_Pages.getChildren().add(createNavButton("<", start > 1 ? start - 1
                : start, start == 1)); // Add "previous" button

        if (pages <= 5) {
            // For 5 or fewer pages, display all page buttons
            for (int i = 0; i < pages; i++) {
                int pageIndex = i + 1;
                hBox_Pages.getChildren().add(createPageButton(pageIndex, start));
            }
        } else {
            // For more than 5 pages, use ellipsis to condense page range
            if (start + 3 > pages) {
                hBox_Pages.getChildren().add(createEllipsisButton());
                for (int i = 3; i >= 0; i--) {
                    int pageIndex = pages - i;
                    hBox_Pages.getChildren().add(createPageButton(pageIndex, start));
                }
            } else if (start - 3 < 0) {
                for (int i = 0; i < 4; i++) {
                    int pageIndex = i + 1;
                    hBox_Pages.getChildren().add(createPageButton(pageIndex, start));
                }
                hBox_Pages.getChildren().add(createEllipsisButton());
            } else {
                // Display a range of pages with ellipsis in the middle if needed
                Button button1 = createPageButton(start, start);
                Button button2 = createPageButton(start + 1, start);
                Button button3 = createEllipsisButton();
                Button button4 = createPageButton(start < pages / 2 ? pages - 1 : 1, start);
                Button button5 = createPageButton(start < pages / 2 ? pages : 2, start);

                if (start < pages / 2) {
                    hBox_Pages.getChildren().addAll(button1, button2, button3, button4, button5);
                } else {
                    hBox_Pages.getChildren().addAll(button4, button5, button3, button1, button2);
                }
            }
        }
        hBox_Pages.getChildren().add(createNavButton(">", start < pages ? start + 1
                : start, start == pages)); // Add "next" button
    }

    /**
     * Creates a button for a specific page in the pagination controls.
     *
     * @param pageNumber The number of the page for which the button is created.
     * @param currentPage The current page number, used to highlight the active page.
     * @return A button representing the page number.
     */
    private Button createPageButton(int pageNumber, int currentPage) {
        // Create button with page number text
        Button button = new Button(String.valueOf(pageNumber));
        // Add style for pagination button
        button.getStyleClass().add("pagination-button");
        if (pageNumber == currentPage) {
            // Highlight current page button
            button.getStyleClass().add("current-page-button");
        }
        button.setOnMouseClicked(e -> changePages(pageNumber));
        return button;
    }

    /**
     * Creates a navigation button for moving between pages (previous or next).
     *
     * @param text The text to display on the button (either "<" for previous or ">" for next).
     * @param targetPage The page to navigate to when the button is clicked.
     * @param isdefault A flag indicating whether this is a default button (used for styling).
     * @return A button that navigates to the target page when clicked.
     */
    private Button createNavButton(String text, int targetPage, boolean isdefault) {
        Button button = new Button(text); // Create navigation button with provided text
        if (isdefault) {
            button.getStyleClass().addAll("pagination-button", "default-button");
        } else {
            button.getStyleClass().addAll("pagination-button", "nav-button");
        }
        button.setOnMouseClicked(e -> changePages(targetPage));
        return button;
    }

    /**
     * Creates a button representing an ellipsis ("...") for condensed page ranges.
     * This button is used when there are too many pages to display all at once,
     * indicating that there are more pages outside the current visible range.
     *
     * @return A button representing the ellipsis.
     */
    private Button createEllipsisButton() {
        Button button = new Button("..."); // Create an ellipsis button
        button.getStyleClass().addAll("pagination-button", "ellipsis-button");
        button.setDisable(true); // Disable the button as it's just a visual placeholder
        return button;
    }

    /**
     * Changes the current page to the specified page and updates the displayed content.
     * This method also updates the pagination controls to reflect the current page.
     *
     * @param start The page number to navigate to.
     */
    public void changePages(int start) {
        current_page = start; // Update current page
        text_pages.setText(String.valueOf(start)); // Update displayed page number
        vbox_list_member.getChildren().clear(); // Clear the current list of members
        vbox_list_member.getChildren().add(listpageUser.get(start - 1)); // Add the content for the selected page
        createHboxPages(pages, start); // Update pagination controls to reflect the new page
    }

    /**
     * Sorts the current list of users based on the specified type and updates the display.
     * The list of users is sorted by one of the following criteria: Username, Fullname,
     * Email, or User ID. The sorting is applied to the users currently displayed on the
     * active page, ensuring that the sorting only affects the users visible on the current.
     *
     * @param type The type of sorting criterion. It can be one of the following:
     *             - "Username" to sort by the username.
     *             - "Fullname" to sort by the full name.
     *             - "Email" to sort by the email address.
     *             - "User ID" to sort by the user ID (default if none of the above).
     */
    public void sort(String type) {
        String type_search = new String();

        // Determine the sorting criterion based on the input type
        if (type.equals("Username")) type_search = USERNAME;
        else if (type.equals("Fullname")) type_search = FULLNAME;
        else if (type.equals("Email")) type_search = EMAIL;
        else type_search = USER_ID;

        // Get the list of users for the current page
        List<User> listUser = new ArrayList<>();
        listUser = list_user_current.subList((current_page - 1) * MAX_BOOKS_IN_PAGE,
                Math.min(current_page * MAX_BOOKS_IN_PAGE, list_user_current.size()));

        // Perform the sorting based on the chosen criterion
        switch (type_search) {
            case USERNAME:
                listUser.sort((user1, user2) -> CharSequence.compare(user1.getUsername(),
                        user2.getUsername()));
                break;
            case FULLNAME:
                listUser.sort((user1, user2) -> CharSequence.compare(user1.getFullName(),
                        user2.getFullName()));
                break;
            case EMAIL:
                listUser.sort((user1, user2) -> CharSequence.compare(user1.getEmail(),
                        user2.getEmail()));
                break;
            default:
                listUser.sort(Comparator.comparingLong(User::getId));
                break;
        }

        // Update the display with the sorted list of users
        vbox_list_member.getChildren().clear();
        for (User user : listUser) {
            Platform.runLater(() -> {
                vbox_list_member.getChildren().add(createAnchorPaneAccount(user));
            });
        }
    }

}


