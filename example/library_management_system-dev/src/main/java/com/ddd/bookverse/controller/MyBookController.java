package com.ddd.bookverse.controller;

import com.ddd.bookverse.item.AnchorPane;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.service.BorrowService;
import com.ddd.bookverse.singleton.UserSession;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ddd.bookverse.controller.RightPanelController.setController;
import static com.ddd.bookverse.item.AnchorPane.*;
import static com.ddd.bookverse.model.Borrow.*;
import static com.ddd.bookverse.utils.NavHelper.*;


/**
 * Controller for managing the user's book list, including search and navigation.
 */
public class MyBookController implements UiController,SearchController {


    private final BookService bookService = new BookService();
    private final BorrowService borrowService = new BorrowService();
    private final UserSession userSession = UserSession.getInstance();
    private String bookStatus;

    // Khai báo ExecutorService
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final int  MAX_SUGGESTION = 20;

    public MyBookController() {

    }

    @FXML
    private VBox content_booklists;
    @FXML
    private Label booklist_home;
    @FXML
    private Button button_back;
    @FXML
    private TextField search_bar;
    @FXML
    private MenuButton button_option_search;
    @FXML
    private javafx.scene.layout.AnchorPane loading;
    @FXML
    private javafx.scene.layout.AnchorPane right_panel;
    @FXML
    private FontIcon button_search;
    @FXML
    private VBox vbox_result_search;
    @FXML
    private ScrollPane scroll_result_search;
    @FXML
    private FontIcon button_delete_search;



    public void initialize() {
        initUI();
        clickButton();
    }
    // Ui Controller
    /**
     * Initializes the UI components and sets up event handlers.
     */
    @Override
    public void initUI() {
        setLoading(false);
        renderResultSearch();
    }
    /**
     * Initializes the UI components and sets up event handlers.
     */
    @Override
    public void clickButton() {
        button_back.setOnMouseClicked(event -> {
            try {
                backToDashboard(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        button_search.setOnMouseClicked(e -> {searchContent();});
        search_bar.setOnAction(event -> searchContent());
        button_option_search.getItems().forEach(item -> {
            item.setOnAction(event -> {
                button_option_search.setText(item.getText());
            });
        });
        loading.getChildren().add(createAnChorPaneLoading(0,0));
        button_delete_search.setOnMouseClicked(e->{search_bar.setText("");});
        setButtonDeleteText();
    }

    /**
     * Toggles the visibility of the loading indicator.
     *
     * @param isVisible true to show the loading indicator, false to hide it
     */
    @Override
    public void setLoading(boolean isVisible) {
        loading.setVisible(isVisible);
    }

    // Search Controller
    /**
     * Retrieves the current search type based on the selected option in the menu.
     *
     * @return the search type as a string (e.g., "Category", "Title", "Author")
     */
    @Override
    public String getSearchType() {
        String type_search = new String();
        if(button_option_search.getText().equals("Category")) type_search = Book.CATEGORY;
        else if (button_option_search.getText().equals("Title")) type_search = Book.TITLE;
        else if (button_option_search.getText().equals("Author")) type_search = Book.AUTHOR;
        else if (button_option_search.getText().equals("Publisher")) type_search = Book.PUBLISHER;
        else type_search = Book.TITLE;
        return type_search;
    }

    /**
     * Performs the search based on the input in the search bar.
     */
    @Override
    public void searchContent() {
        search(search_bar.getText(), getSearchType());

    }


    /**
     * Executes a search with the provided query and search type.
     *
     * @param query the search query
     * @param type  the search type (e.g., Category, Title, Author, Publisher)
     */
    public void search(String query , String type) {
        vbox_result_search.getChildren().clear();
        scroll_result_search.setVisible(false);
        executorService.submit(() -> {
            List<Book> bookList = bookService
                    .getBooksByUserStatusAndBookAttr(userSession.getUserId(), bookStatus, type, query);
            Platform.runLater(() -> {
                content_booklists.getChildren().clear();
                for (int i = 0; i < bookList.size(); i += 5) {
                    List<Book> subList = bookList.subList(i, Math.min(bookList.size(), i + 5));
                    content_booklists.getChildren()
                            .add(AnchorPane.createAnchorPaneBook(subList, "",
                                    Math.min(bookList.size() - i, 5), false, bookStatus,loading));
                }
            });
        });
    }

    /**
     * Renders search results as the user types in the search bar.
     */
    @Override
    public void renderResultSearch() {
        vbox_result_search.getChildren().clear();
        scroll_result_search.setVisible(false);
        StringProperty textProperty = search_bar.textProperty();
        textProperty.addListener((observable, oldValue, newValue) -> {
            vbox_result_search.getChildren().clear();
            scroll_result_search.setVisible(false);
            if(search_bar.getText().length() > 0) searchResult();
        });
    }
    @Override
    public void search(String query, String type, boolean isAddHistory) {
    }

    /**
     * Displays search suggestions based on user input in the search bar.
     */
    @Override
    public void searchResult() {
        String query = search_bar.getText();
        String type_search = getSearchType();
        List<Book> bookList = bookService
                .getBooksByUserStatusAndBookAttr(userSession.getUserId(),
                        bookStatus, type_search, query);
        int limit = 0;
        for(Book book:bookList) {
            TextField textField = new TextField();
            textField.setFont(Font.font(14));
            switch (type_search) {
                case Book.AUTHOR: textField.setText (book.getAuthors()[0]); break;
                case Book.TITLE:  textField.setText (book.getTitle());break;
                case Book.PUBLISHER: textField.setText (book.getPublisher());break;
            }
            textField.setStyle("-fx-background-color: transparent; " +
                    "-fx-cursor: hand;-fx-text-fill:#333333");
            textField.setEditable(false);
            textField.setOnMouseEntered(
                    e->{textField.setStyle("-fx-background-color:#5f43b2 ;" +
                            "-fx-text-fill:white;-fx-cursor: hand;");});
            textField.setOnMouseExited(
                    e->{textField.setStyle("-fx-background-color: transparent ;" +
                            "-fx-text-fill:#333333;-fx-cursor: hand;");});
            textField.setOnMouseClicked(e->{
                search_bar.setText(textField.getText());
                searchContent();
            });
            scroll_result_search.setVisible(true);
            vbox_result_search.getChildren().add(textField);
            if(limit++ > MAX_SUGGESTION) break;
        }
    }

    /**
     * Sets the right panel based on the current book status.
     */
    public void setRightPanel() {
        try {
            if (right_panel.getChildren().isEmpty()) {
                setController(this);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rightpanel.fxml"));
                javafx.scene.layout.AnchorPane rightPanel = loader.load();
                RightPanelController rightPanelController = loader.getController();
                rightPanelController.setCurrentUI(bookStatus);
                right_panel.getChildren().add(rightPanel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the visibility of the delete search button based on search input.
     */
    public void setButtonDeleteText() {
        search_bar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (search_bar.getText().equals("")) {
                button_delete_search.setVisible(false);
            } else {
                button_delete_search.setVisible(true);
            }
        });
    }
    /**
     * Sets the current book status and loads relevant content based on the status.
     * This method also handles updating overdue borrows if the status is "BORROWING".
     *
     * @param bookStatus the status of the books (e.g., PLAN, BORROWING, RETURNED)
     */
    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
        // Update overdue borrows if the status is "BORROWING"
        if (Objects.equals(this.bookStatus, BORROWING)) {
            borrowService.updateOverdueBorrowsByUserId(userSession.getUserId());
        }
        setBooklist_home(); // Update the home label text based on the status
        loadContent(); // Load the relevant content for the selected status
        setRightPanel(); // Update the right panel based on the status
    }

    /**
     * Sets the label text for the book list home based on the current book status.
     * The label text reflects the type of books being displayed (e.g., "Borrowed Books", "Plan to Borrow").
     */
    private void setBooklist_home() {
        String labelText = "My Book"; // Default label text
        switch (bookStatus) {
            case PLAN:
                labelText = "Plan to Borrow"; // Text for books planned to borrow
                break;
            case BORROWING:
                labelText = "Borrowed Books"; // Text for currently borrowed books
                break;
            case RETURNED:
                labelText = "Returned Books"; // Text for books that have been returned
                break;
        }
        booklist_home.setText(labelText); // Set the label text
    }

    /**
     * Navigates the user back to the dashboard screen.
     * This method is triggered when the back button is clicked.
     *
     * @param event the mouse event triggering the navigation
     * @throws IOException if an error occurs during the dashboard navigation
     */
    private void backToDashboard(MouseEvent event) throws IOException {
        try {
            toDashboard(event); // Perform navigation to the dashboard
        } catch (IOException e) {
            throw new RuntimeException(e); // Rethrow the exception if navigation fails
        }
    }

    /**
     * Loads and displays content (books) based on the current book status.
     * The content is displayed in batches of 5 books per page. Additionally, if the status is "BORROWING",
     * it includes overdue books, and if the status is "RETURNED", it includes paid books.
     */
    public void loadContent() {
        content_booklists.getChildren().clear(); // Clear the previous book list
        executorService.submit(() -> {
            // Fetch books based on the user ID and the current status
            List<Book> bookList = bookService
                    .getBooksByUserAndStatus(userSession.getUserId(), bookStatus);
            // If the status is "BORROWING", add overdue books
            if (Objects.equals(bookStatus, BORROWING)) {
                List<Book> overdueBooks = bookService
                        .getBooksByUserAndStatus(userSession.getUserId(), OVERDUE);
                bookList.addAll(overdueBooks);
            }
            // If the status is "RETURNED", add paid books
            else if (Objects.equals(bookStatus, RETURNED)) {
                List<Book> paidBooks = bookService
                        .getBooksByUserAndStatus(userSession.getUserId(), PAID);
                bookList.addAll(paidBooks);
            }

            // If there are books to display, update the UI with them
            if (bookList != null) {
                Platform.runLater(() -> {
                    // Display the books in batches of 5
                    for (int i = 0; i < bookList.size(); i += 5) {
                        List<Book> subList = bookList.subList(i, Math.min(bookList.size(), i + 5));
                        content_booklists.getChildren()
                                .add(AnchorPane.createAnchorPaneBook(subList, "",
                                        5, false, bookStatus, loading));
                    }
                });
            }
        });
    }


}


