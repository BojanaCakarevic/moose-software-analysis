package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.Borrow;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.service.BorrowService;
import com.ddd.bookverse.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ddd.bookverse.item.AnchorPane.createAnChorPaneLoading;
import static com.ddd.bookverse.item.AnchorPane.createAnchorPaneListActivities;
import static com.ddd.bookverse.utils.NavHelper.*;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * The `DashboardAdminController` class is responsible for managing
 * the admin dashboard of the application.
 * It implements the {@link UiController} interface to handle
 * the user interface interactions specific to the
 * admin dashboard, including displaying key statistics, charts,
 * and managing the view for administrative tasks.
 *
 * <p>This class interacts with various services to retrieve
 * and display information such as the total number
 * of books, users, activities, and other admin-specific data.
 * It provides an overview of the system’s status
 * and is responsible for updating the dashboard UI.</p>
 *
 * Implements:
 * - {@link UiController} for managing the user interface of t
 * he admin dashboard, including displaying statistics,
 *   charts, and handling user interactions with the dashboard components.
 *
 * @see UiController
 */
public class DashboardAdminController implements UiController {
    private final BorrowService borrowService = new BorrowService();
    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();

    @FXML
    private VBox vbox_list_activities;
    @FXML
    private Text sumActivities;
    @FXML
    private Text sumBooks;
    @FXML
    private Text sumUsers;
    @FXML
    private PieChart pieChart;
    @FXML
    private AnchorPane loading;


    public void initialize() {
        initUI();
        clickButton();
    }

    // Ui Controller
    /**
     * Initializes the UI components by setting the sum of activities, books, and users,
     * and configuring the pie chart and activity list. Also manages the visibility of
     * the loading screen during the initialization.
     */
    @Override
    public void initUI() {
        sumActivities.setText(borrowService.getSum().toString());
        sumBooks.setText(bookService.getSum().toString());
        sumUsers.setText(userService.getSum().toString());
        setPieChart();
        setListActivities();
        setLoading(false);
        loading.getChildren().add(createAnChorPaneLoading(0, 0));
    }

    /**
     * A placeholder method for button click events. Currently empty and not implemented.
     * This method can be used to define the behavior of button clicks in the future.
     */
    @Override
    public void clickButton() {
    }

    /**
     * Sets the visibility of the loading screen. If isVisible is true, the loading
     * screen will be shown; otherwise, it will be hidden.
     *
     * @param isVisible A boolean value indicating whether the loading screen should be visible.
     */
    @Override
    public void setLoading(boolean isVisible) {
        loading.setVisible(isVisible);
    }



    /**
     * Sets the list of activities (borrows) to be displayed in the UI.
     * It retrieves the latest 20 borrow records and adds them to the list.
     * Each borrow activity is displayed in a separate AnchorPane, created by the method
     * `createAnchorPaneListActivities`. If the borrow record lacks valid user or book data,
     * it is skipped.
     */
    public void setListActivities() {
        vbox_list_activities.getChildren().clear();
        vbox_list_activities.setSpacing(10);
        List<Borrow> listborrow = borrowService.getLatestNBorrows(20);
        for (Borrow borrow : listborrow) {
            if (borrow.getUser() == null || borrow.getBook() == null) continue;
            vbox_list_activities.getChildren().add(createAnchorPaneListActivities(borrow));
        }
    }

    /**
     * Sets the data for the pie chart by fetching the book categories and their corresponding
     * counts. It iterates over the map returned by `bookService.getDataCategory()` and adds
     * each category and its count as a slice of the pie chart.
     */
    public void setPieChart() {
        Map<String, Integer> map = bookService.getDataCategory();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(),entry.getValue()));
        }
    }


}
