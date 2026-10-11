package com.ddd.bookverse.controller;

import com.ddd.bookverse.service.ReviewService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * The `DeleteConfirmationController` class is responsible for managing
 * the delete confirmation UI in the application.
 * It provides functionality for displaying a confirmation dialog when
 * the user intends to delete an item, such as a book
 * or account. The user can either confirm or cancel the deletion based on their selection.
 *
 * <p>The class manages the visibility of the delete confirmation dialog,
 * updates the message displayed to the user,
 * and handles the logic for confirming or cancelling the deletion process.
 * It is typically used in conjunction with
 * operations that involve removing an item from the system.</p>
 *
 * Implements:
 * - {@link DeleteController} for managing the
 * delete operation logic and providing an interface for confirming or cancelling deletions.
 *
 * Example usage:
 * <pre>
 *     DeleteConfirmationController deleteController = new DeleteConfirmationController();
 *     deleteController.render("Are you sure you want to delete this item?");
 * </pre>
 *
 * @see DeleteController
 */
public class DeleteConfirmationController {
    private final ReviewService reviewService = new ReviewService();
    private final BooleanProperty status = new SimpleBooleanProperty(false);

    @FXML
    private Button button_delete;
    @FXML
    private Button button_cancel;
    @FXML
    private FontIcon button_exit;
    @FXML
    private AnchorPane delete_confirmation;
    @FXML
    private Text message;
    @FXML
    private Text error_message;

    public void initialize(){
        button_cancel.setOnMouseClicked(e->{
            exit();
        });
        button_exit.setOnMouseClicked(e->{
            exit();
        });
        button_delete.setOnMouseClicked(e->{
            setStatus(true);
        });
    }

    /**
     * Sets the status value.
     *
     * @param status the new status to set
     */
    public void setStatus(boolean status) {
        this.status.set(status);
    }

    /**
     * Returns the property of the status, which is a BooleanProperty.
     *
     * @return the status property
     */
    public BooleanProperty statusProperty() {
        return status;
    }

    /**
     * Gets the current value of the status.
     *
     * @return the current status value
     */
    public boolean getStatus() {
        return status.get();
    }

    /**
     * Renders a confirmation message and makes the delete confirmation visible.
     *
     * @param message the message to display in the confirmation
     */
    public void render(String message) {
        delete_confirmation.setVisible(true);
        this.message.setText(message);
    }

    /**
     * Displays an error message and makes the error message visible.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        error_message.setText(message);
        error_message.setVisible(true);
    }

    /**
     * Exits the current process by hiding the error message and the delete confirmation.
     * It also sets the status to false.
     */
    public void exit() {
        setStatus(false);
        error_message.setVisible(false);
        delete_confirmation.setVisible(false);
    }

}
