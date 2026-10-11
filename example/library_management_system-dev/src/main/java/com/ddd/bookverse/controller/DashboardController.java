package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.service.UserService;
import com.ddd.bookverse.singleton.UserSession;
import com.ddd.bookverse.utils.ViewUtil;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.ddd.bookverse.constants.Constants.RIGHTPANEL_URL;
import static com.ddd.bookverse.controller.RightPanelController.setController;
import static com.ddd.bookverse.item.AnchorPane.*;
import static com.ddd.bookverse.model.Borrow.*;
import static com.ddd.bookverse.utils.NavHelper.formMyBookToBookList;

import static java.util.Collections.sort;

/**
 * The `DashboardController` class is responsible for managing the
 * dashboard interface of the application.
 * It implements the {@link UiController} interface to handle the user
 * interface interactions and display key
 * information for the user, such as statistics, charts, and other relevant data that
 * are part of the user's dashboard.
 *
 * <p>This class retrieves and displays important data for the user,
 * such as the number of books, user activity,
 * and other metrics relevant to the user’s experience within the application.
 * It is also responsible for updating
 * the dashboard UI to reflect the latest data.</p>
 *
 * Implements:
 * - {@link UiController} for managing the user interface of the dashboard,
 * handling data updates, and responding
 *   to user interactions with various dashboard components.
 *
 * @see UiController
 */
public class DashboardController implements UiController {
    private final BookService bookService = new BookService();
    private final UserService userService = new UserService();
    private final BookListController bookListController = new BookListController();
    private final UserSession userSession = UserSession.getInstance();
    private final int NUMBER_OF_BOOK_IN_ROW = 5;
    private final int MAX_LEN_TEXT = 20;

    @FXML
    private AnchorPane plan_to_read;
    @FXML
    private AnchorPane borrowed_books;
    @FXML
    private AnchorPane returned_books;
    @FXML
    private GridPane book_gridpane;
    @FXML
    private Text num_plan;
    @FXML
    private Text num_borrowing;
    @FXML
    private Text num_returned;
    @FXML
    private AnchorPane empty_continue_reading;
    @FXML
    private AnchorPane loading;
    @FXML
    private AnchorPane right_panel;
    @FXML
    private ImageView dashboard_gif;
    @FXML
    private ImageView readinggif;

    public DashboardController() {

    }

    @FXML
    public void initialize() {
        initUI();
        clickButton();
    }
    // Ui Controller
    /**
     * Initializes the user interface by setting up the count of books,
     * loading the list of books, hiding the loading screen, and setting
     * up the right panel and dashboard GIF. This method is typically
     * called when the UI is first loaded.
     */
    @Override
    public void initUI() {
        setCountBook();
        loadBooks();
        setLoading(false);
        loading.getChildren().add(createAnChorPaneLoading(0, 0));
        setRightPanel();
        setDashboardGif();
        readinggif.setImage(ViewUtil.bookGifReading);
    }

    /**
     * Sets up click event handlers for buttons to navigate to different
     * book lists: "Plan to borrow", "Borrowing", and "Returned".
     * Each button will trigger the `switchToMyBookList()` method with
     * the appropriate status and event.
     */
    @Override
    public void clickButton() {
        plan_to_read.setOnMouseClicked(event ->
                switchToMyBookList("Plan to borrow", event));
        borrowed_books.setOnMouseClicked(event -> switchToMyBookList(BORROWING, event));
        returned_books.setOnMouseClicked(event -> switchToMyBookList(RETURNED, event));
    }

    /**
     * Toggles the visibility of the loading screen.
     *
     * @param isVisible a boolean value indicating whether the loading
     *                  screen should be visible or hidden.
     */
    @Override
    public void setLoading(boolean isVisible) {
        loading.setVisible(isVisible);
    }



    /**
     * Sets the GIF image on the dashboard with rounded corners by applying a clip
     * with rounded arcs to the image.
     */
    private void setDashboardGif() {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(50);
        clip.setArcHeight(50);
        clip.setWidth(dashboard_gif.getFitWidth() - 10);
        clip.setHeight(dashboard_gif.getFitHeight());
        dashboard_gif.setClip(clip);
        dashboard_gif.setImage(ViewUtil.bookGifDashboard);
    }

    /**
     * Loads the right panel of the UI by setting the controller and loading
     * the corresponding FXML. If the panel has not been loaded before, it
     * initializes and adds the right panel to the parent container.
     */
    public void setRightPanel() {
        try {
            if (right_panel.getChildren().isEmpty()) {
                setController(this);
                FXMLLoader loader = new FXMLLoader(getClass().getResource(RIGHTPANEL_URL));
                AnchorPane rightPanel = loader.load();
                RightPanelController rightPanelController = loader.getController();
                rightPanelController.setCurrentUI(DASHBOARD_UI);
                right_panel.getChildren().add(rightPanel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the count of books for different statuses ("Plan to borrow", "Borrowing",
     * "Returned", and "Paid") and updates the UI components with the corresponding values.
     */
    public void setCountBook() {
        num_plan.setText(String.valueOf(bookService.
                getNumBooksByUserIdAndStatus(userSession.getUserId(),
                        "Plan to borrow")));

        int borrowingCount = bookService
                .getNumBooksByUserIdAndStatus(userSession.getUserId(), BORROWING);
        int overdueCount = bookService
                .getNumBooksByUserIdAndStatus(userSession.getUserId(), OVERDUE);
        num_borrowing.setText(String.valueOf(borrowingCount + overdueCount));

        int returnedCount = bookService
                .getNumBooksByUserIdAndStatus(userSession.getUserId(), RETURNED);
        int paidCount = bookService
                .getNumBooksByUserIdAndStatus(userSession.getUserId(), PAID);
        num_returned.setText(String.valueOf(returnedCount + paidCount));
    }

    /**
     * Switches to the book list with the specified status, setting the loading indicator
     * to true while the new list is being loaded.
     *
     * @param bookStatus the status of the books to be displayed (e.g., "Plan to borrow")
     * @param event the event that triggered this action
     */
    private void switchToMyBookList(String bookStatus, Event event) {
        setLoading(true);
        try {
            formMyBookToBookList(bookStatus, event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a list of books for the current user and displays them in a grid format.
     * If there are no books to display, a message is shown. Each book is displayed
     * with its image, title, and a progress bar indicating the reading progress.
     * This method runs in a background thread to avoid blocking the UI thread.
     */
    public void loadBooks() {
        book_gridpane.getChildren().clear();
        empty_continue_reading.setVisible(false);

        Task<List<Book>> loadBooksTask = new Task<>() {
            @Override
            protected List<Book> call() {
                return bookService.getReadingBooksByUser(userSession.getUserId());
            }
        };

        loadBooksTask.setOnSucceeded(event -> {
            List<Book> books = loadBooksTask.getValue();

            if (books == null || books.isEmpty()) {
                book_gridpane.setVisible(false);
                empty_continue_reading.setVisible(true);
            } else {
                book_gridpane.setVisible(true);
                for (int i = 0; i < Math.min(books.size(), NUMBER_OF_BOOK_IN_ROW); i++) {
                    Book book = books.get(i);
                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(150);
                    imageView.setFitWidth(100);
                    imageView.getStyleClass().add("btn");

                    imageView.setOnMouseClicked(clickEvent -> {
                        try {
                            bookListController.loadDetailsBook(clickEvent, book, DASHBOARD_UI);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    byte[] imageBytes = book.getImage();
                    if (imageBytes != null && imageBytes.length > 0) {
                        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                            Image image = new Image(inputStream);
                            imageView.setImage(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        imageView.setImage(null);
                    }
                    Text titleText = new Text(book.getTitle().length() < MAX_LEN_TEXT ?
                            book.getTitle() : book.getTitle().substring(0, MAX_LEN_TEXT) + "...");
                    titleText.setStyle("-fx-fill: #666666; -fx-font-size: 14px;");

                    double completionPercentage
                            = bookService.getCompletionPercentage(book.getId(),
                            userSession.getUserId());
                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setPrefWidth(120.0);
                    progressBar.setPrefHeight(10.0);
                    progressBar.setProgress(completionPercentage);
                    progressBar.setStyle("-fx-accent: #5f43b2;");

                    book_gridpane.add(imageView, i, 0);
                    book_gridpane.add(titleText, i, 1);
                    book_gridpane.add(progressBar, i, 2);
                }

                for (int i = books.size(); i < 5; i++) {
                    book_gridpane.add(new Text(""), i, 0);
                    book_gridpane.add(new Text(""), i, 1);
                }
            }
        });

        loadBooksTask.setOnFailed(event -> {
            loading.setVisible(false);
            empty_continue_reading.setVisible(true);
            Throwable exception = loadBooksTask.getException();
            if (exception != null) {
                exception.printStackTrace();
            }
        });

        new Thread(loadBooksTask).start();
    }



}
