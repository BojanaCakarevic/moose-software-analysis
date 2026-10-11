package com.ddd.bookverse.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ddd.bookverse.utils.NavHelper.*;

public class NavbarAdminController {
    private Object controler;

    @FXML
    private Button account_icon;

    @FXML
    private Button booklist_icon;

    @FXML
    private Button dashboard_icon;

    @FXML
    private Button accountlist_icon;

    @FXML
    private Button info_icon;

    /**
     * Initializes the NavbarAdminController.
     * Sets up mouse click event handlers for each navigation button.
     */
    @FXML
    private void initialize() {
        dashboard_icon.setOnMouseClicked(this::navigateToDashboard);
        booklist_icon.setOnMouseClicked(this::navigateToBookList);
        account_icon.setOnMouseClicked(this::navigateToMyAccount);
        accountlist_icon.setOnMouseClicked(this::navigateToAccountManagement);
        info_icon.setOnMouseClicked(this::navigateToInFoAdmin);
        setNavbarAdminController(this);
    }

    /**
     * Sets the controller for the current view, depending on the provided controller type.
     *
     * @param controller the controller object to be associated with the navigation bar
     */
    public void setController(Object controller) {
        switch (controller) {
            case DashboardAdminController dashboardAdminController
                    -> this.controler = dashboardAdminController;
            case BookManagementController bookManagementController
                    -> this.controler = bookManagementController;
            case AccountManagementController accountManagementController ->
                    this.controler = accountManagementController;
            case MyAccountController myAccountController -> this.controler = myAccountController;
            case InfoController infoController -> this.controler = infoController;
            case null, default ->
                    System.out.println("Controller type not recognized: ");
        }
    }

    /**
     * Triggers the loading indicator for the associated controller's view.
     * If the controller is of a known type, it calls the `setLoading` method to show loading state.
     */
    public void loading() {
        if (controler instanceof DashboardAdminController) {
            ((DashboardAdminController) controler).setLoading(true);
        } else if (controler instanceof BookManagementController) {
            ((BookManagementController) controler).setLoading(true);
        } else if (controler instanceof AccountManagementController) {
            ((AccountManagementController) controler).setLoading(true);
        } else if (controler instanceof MyAccountController) {
            ((MyAccountController) controler).setLoading(true);
        } else if (controler instanceof InfoController) {
            ((InfoController) controler).setLoading(true);
        }
    }

    /**
     * Navigates to the admin dashboard view.
     *
     * @param event the mouse click event triggering the navigation
     */
    private void navigateToDashboard(MouseEvent event) {
        loading();
        try {
            toAdminDashboard(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Navigates to the account management view.
     *
     * @param event the mouse click event triggering the navigation
     */
    private void navigateToAccountManagement(MouseEvent event) {
        loading();
        try {
            toAccountManagement(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Navigates to the book management view.
     *
     * @param event the mouse click event triggering the navigation
     */
    private void navigateToBookList(MouseEvent event) {
        loading();
        try {
            toBookManagement(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the admin's account view.
     *
     * @param event the mouse click event triggering the navigation
     */
    private void navigateToMyAccount(MouseEvent event) {
        loading();
        try {
            toAdminAccount(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the admin's information view.
     *
     * @param event the mouse click event triggering the navigation
     */
    private void navigateToInFoAdmin(MouseEvent event) {
        loading();
        try {
            toInFoAdmin(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
