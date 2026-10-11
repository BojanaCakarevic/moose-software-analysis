package com.ddd.bookverse.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

import static com.ddd.bookverse.utils.NavHelper.*;
import static com.ddd.bookverse.controller.BookListController.setEmptyHistorySearch;
import static com.ddd.bookverse.controller.BookDetailController.setEmptyStackBook;

/**
 * The NavbarController class manages the navigation bar of the application.
 * It provides navigation functionality between different sections of the application,
 * including Dashboard, Book List, Account, and Information pages.
 */
public class NavbarController {
    private Object controller;

    @FXML
    private Button account_icon;

    @FXML
    private Button booklist_icon;

    @FXML
    private Button dashboard_icon;

    @FXML
    private Button info_icon;

    /**
     * Sets the current controller for the view and resets any specific state variables.
     *
     * @param controller the controller object associated with the current view.
     */
    public void setController(Object controller) {
        setEmptyHistorySearch();
        if (!(controller instanceof BookDetailController)) {
            setEmptyStackBook();
        }
        switch (controller) {
            case DashboardController dashboardController
                    -> this.controller = dashboardController;
            case BookDetailController bookDetailController
                    -> this.controller = bookDetailController;
            case BookListController bookListController
                    -> this.controller = bookListController;
            case MyAccountController myAccountController
                    -> this.controller = myAccountController;
            case InfoController infoController -> this.controller = infoController;
            case null, default ->
                    System.out.println("Controller type not recognized: " );
        }
    }

    /**
     * Sets the loading state for the associated controller.
     * The specific controller's `setLoading` method is called to indicate loading status.
     */
    public void loading() {
        if (controller instanceof DashboardController) {
            ((DashboardController) controller).setLoading(true);
        } else if (controller instanceof MyAccountController) {
            ((MyAccountController) controller).setLoading(true);
        } else if (controller instanceof BookDetailController) {
            ((BookDetailController) controller).setLoading(true);
        } else if (controller instanceof BookListController) {
            ((BookListController) controller).setLoading(true);
        } else if (controller instanceof InfoController) {
            ((InfoController) controller).setLoading(true);
        }
    }

    /**
     * Initializes the navigation bar by setting mouse click event handlers for each button.
     */
    @FXML
    private void initialize() {
        dashboard_icon.setOnMouseClicked(this::navigateToDashboard);
        booklist_icon.setOnMouseClicked(this::navigateToBookList);
        account_icon.setOnMouseClicked(this::navigateToMyAccount);
        info_icon.setOnMouseClicked(this::navigateToInFo);
        setNavbarController(this);
    }

    /**
     * Navigates to the dashboard view.
     *
     * @param event the mouse click event triggering the navigation.
     */
    private void navigateToDashboard(MouseEvent event) {
        loading();
        try {
            toDashboard(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the book list view.
     *
     * @param event the mouse click event triggering the navigation.
     */
    private void navigateToBookList(MouseEvent event) {
        loading();
        try {
            toBookList(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the user's account view.
     *
     * @param event the mouse click event triggering the navigation.
     */
    private void navigateToMyAccount(MouseEvent event) {
        loading();
        try {
            toMyAccount(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the information view.
     *
     * @param event the mouse click event triggering the navigation.
     */
    private void navigateToInFo(MouseEvent event) {
        loading();
        try {
            toInFo(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
