package com.ddd.bookverse.utils;

import com.ddd.bookverse.controller.*;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.singleton.UserSession;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import static com.ddd.bookverse.constants.Constants.*;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class for handling navigation within the application.
 */
public class NavHelper {
    private static NavbarController navbarController = new NavbarController();
    private static NavbarAdminController navbarAdminController = new NavbarAdminController();

    /**
     * Sets the NavbarController instance.
     *
     * @param navbarController The NavbarController instance to set.
     */
    public static void setNavbarController(NavbarController navbarController) {
        NavHelper.navbarController = navbarController;
    }

    /**
     * Sets the NavbarAdminController instance.
     *
     * @param navbarAdminController The NavbarAdminController instance to set.
     */
    public static void setNavbarAdminController(NavbarAdminController navbarAdminController) {
        NavHelper.navbarAdminController = navbarAdminController;
    }

    /**
     * Navigates to the book list view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toBookList(Event event) throws IOException {
        navigateTo(BOOK_LIST_URL, event);
    }

    /**
     * Navigates to the My Account view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toMyAccount(Event event) throws IOException {
        navigateTo(MY_ACCOUNT_URL, event);
    }

    /**
     * Navigates to the Info view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toInFo(Event event) throws IOException {
        navigateTo(INFO_URL, event);
    }

    /**
     * Navigates to the Admin Info view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toInFoAdmin(Event event) throws IOException {
        navigateTo(INFO_URL_ADMIN, event);
    }

    /**
     * Logs out the user and navigates to the login view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toLogIn(Event event) throws IOException {
        UserSession userSession = UserSession.getInstance();
        userSession.logout();
        navigateTo(LOGIN_URL, event);
    }

    /**
     * Logs out the user and navigates to the sign-up view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toSignUp(Event event) throws IOException {
        UserSession userSession = UserSession.getInstance();
        userSession.logout();
        navigateTo(SIGNUP_URL, event);
    }

    /**
     * Navigates to the dashboard view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toDashboard(Event event) throws IOException {
        navigateTo(DASHBOARD_URL, event);
    }

    /**
     * Navigates to the book management view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toBookManagement(Event event) throws IOException {
        navigateTo(BOOK_MANAGEMENT_URL, event);
    }

    /**
     * Navigates to the account management view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toAccountManagement(Event event) throws IOException {
        navigateTo(ACCOUNT_MANAGEMENT_URL, event);
    }

    /**
     * Navigates to the admin account view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toAdminAccount(Event event) throws IOException {
        navigateTo(MY_ACCOUNT_ADMIN_URL, event);
    }

    /**
     * Navigates to the admin dashboard view.
     *
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void toAdminDashboard(Event event) throws IOException {
        navigateTo(DASHBOARD_ADMIN_URL, event);
    }

    /**
     * Navigates to the book detail view.
     *
     * @param event The event that triggered the navigation.
     * @param book The book to display details for.
     * @param path The path to the FXML file.
     * @param prevUI The previous UI.
     * @throws IOException If an I/O error occurs.
     */
    public static void toBookDetail(Event event, Book book, String path, String prevUI) throws IOException {
        Task<Parent> task = new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                Parent root = loader.load();
                if (path.equals(BOOK_DETAILS_ADMIN_URL)) {
                    DetailsBookAdminController detailsBookAdminController = loader.getController();
                    detailsBookAdminController.setBook(book);
                } else {
                    BookDetailController bookDetailController = loader.getController();
                    navbarController.setController(bookDetailController);
                    bookDetailController.setBook(book);
                    bookDetailController.setPrevUI(prevUI);
                }
                return root;
            }
        };
        task.setOnSucceeded(e -> {
            Parent root = task.getValue();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            URL cssUrl = NavHelper.class.getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                throw new RuntimeException("Không tìm thấy file CSS");
            }
            stage.setScene(scene);
            stage.show();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
            stage.setResizable(false);
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });
        new Thread(task).start();
    }

    /**
     * Navigates from My Book view to the book list view with the specified book status.
     *
     * @param bookStatus The status of the book.
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    public static void formMyBookToBookList(String bookStatus, Event event) throws IOException {
        Task<Parent> task = new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(MY_BOOK_URL));
                Parent root = loader.load();
                MyBookController myBookController = loader.getController();
                myBookController.setBookStatus(bookStatus);
                return root;
            }
        };
        task.setOnSucceeded(e -> {
            Parent root = task.getValue();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            URL cssUrl = NavHelper.class.getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                throw new RuntimeException("Không tìm thấy file CSS");
            }
            stage.setScene(scene);
            stage.show();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
            stage.setResizable(false);
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });
        new Thread(task).start();
    }

    /**
     * Navigates to the specified FXML path.
     *
     * @param fxmlPath The path to the FXML file.
     * @param event The event that triggered the navigation.
     * @throws IOException If an I/O error occurs.
     */
    private static void navigateTo(String fxmlPath, Event event) throws IOException {
        Task<Parent> task = new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Object Controller = loader.getController();
                if (Controller != null) {
                    if (Controller instanceof DashboardController) {
                        navbarController.setController((DashboardController) Controller);
                    } else if (Controller instanceof BookListController) {
                        navbarController.setController((BookListController) Controller);
                    } else if (Controller instanceof MyAccountController) {
                        navbarController.setController((MyAccountController) Controller);
                        navbarAdminController.setController((MyAccountController) Controller);
                    } else if (Controller instanceof InfoController) {
                        navbarController.setController((InfoController) Controller);
                        navbarAdminController.setController((InfoController) Controller);
                    } else if (Controller instanceof BookManagementController) {
                        navbarAdminController.setController((BookManagementController) Controller);
                    } else if (Controller instanceof AccountManagementController) {
                        navbarAdminController.setController((AccountManagementController) Controller);
                    } else if (Controller instanceof DashboardAdminController) {
                        navbarAdminController.setController((DashboardAdminController) Controller);
                    }
                }
                return root;
            }
        };

        task.setOnSucceeded(e -> {
            Parent root = task.getValue();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            URL cssUrl = NavHelper.class.getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                throw new RuntimeException("Không tìm thấy file CSS");
            }
            stage.setScene(scene);
            stage.show();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
            stage.setResizable(false);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });
        new Thread(task).start();
    }
}