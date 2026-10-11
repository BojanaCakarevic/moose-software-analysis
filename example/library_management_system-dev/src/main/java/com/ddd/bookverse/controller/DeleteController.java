package com.ddd.bookverse.controller;

/**
 * The `DeleteController` interface defines the contract for classes
 * that manage the delete confirmation process.
 * Implementing this interface enables a class to handle user interactions
 * related to deleting an item, such as showing
 * a confirmation dialog, processing the deletion request,
 * and managing the state of the delete confirmation UI.
 *
 * The methods in this interface allow for setting up and handling the delete
 * confirmation UI, including displaying the confirmation message, performing the delete action,
 * and handling cancellation of the operation.

 */
public interface DeleteController {

    /**
     * Exits or hides the delete confirmation UI.
     * This method is called when the user decides to cancel the deletion process
     * or when the confirmation UI should
     * be closed without performing any deletion.
     */
    void ExitDeleteConfirmation();

    /**
     * Sets up or initializes the delete confirmation controller.
     * This method is typically called to prepare the
     * confirmation UI before it is shown to the user, ensuring the
     * necessary configurations and listeners are set up.
     */
    void setDeleteConfirmationController();

    /**
     * Handles the delete confirmation action.
     * This method is invoked when the user confirms the deletion.
     * It should trigger the necessary actions to perform
     * the deletion of the item,
     * such as updating the database or notifying other components of the system.
     */
    void deleteConfirmation();
}
