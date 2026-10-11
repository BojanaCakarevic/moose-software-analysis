package com.ddd.bookverse.controller;

import com.ddd.bookverse.exception.BookNotFoundException;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.model.Borrow;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.service.BorrowService;
import com.ddd.bookverse.utils.QRCode;
import com.google.zxing.NotFoundException;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ddd.bookverse.api.GoogleBooksAPI.searchBookByISBN;
import static com.ddd.bookverse.constants.Constants.*;
import static com.ddd.bookverse.item.AnchorPane.*;
import static com.ddd.bookverse.utils.ImageUtil.downloadImage;
import static com.ddd.bookverse.utils.ImageUtil.loadImageFromResources;
import static com.ddd.bookverse.utils.NavHelper.toBookDetail;
import static com.ddd.bookverse.utils.ValidationUtil.*;

/**
 * The `BookManagementController` class is responsible for managing books within the application.
 * It implements the `UiController`, `DeleteController`,
 * and `SearchController` interfaces to handle
 * UI interactions, book deletion, and book search functionality.
 *
 * <p>This class integrates the management of books,
 * including displaying them, searching, and deleting them.
 * It provides the necessary functionality for adding,
 * removing, and searching books within the system, and
 * it updates the UI based on the results of these operations.</p>
 *
 * Implements:
 * - {@link UiController} for managing the user interface,
 * including the display and interaction with the book list.
 * - {@link DeleteController} for handling the deletion of books,
 * including user confirmation before deletion.
 * - {@link SearchController} for enabling the search functionality,
 * allowing users to search for books by different criteria.
 *
 * @see UiController
 * @see DeleteController
 * @see SearchController
 */
public class BookManagementController implements
        UiController, DeleteController,SearchController{

    private final BookService bookService = new BookService();
    private final BorrowService borrowService = new BorrowService();

    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String CATEGORY  = "category";
    private static final String PUBLISHER = "publisher";
    private static final String ID = "id";
    private final int  MAX_SUGGESTION = 20;
    private DeleteConfirmationController deleteConfirmationController ;
    private javafx.scene.layout.AnchorPane anchorPaneDelete;
    private Book bookDelete;
    private int pages;
    private int current_page;
    private final int MAX_BOOKS_IN_PAGE = 20;
    private List<VBox> listpageBook = new ArrayList<>();
    private List<Book> list_book_current = new ArrayList<>();

    @FXML
    private javafx.scene.layout.AnchorPane  mainAnchorPane;
    @FXML
    private VBox vbox_list_book;
    @FXML
    private Button button_add_book;
    @FXML
    private AnchorPane anchorpane_add_book;
    @FXML
    private Button button_cancel;
    @FXML
    private TextField title_book;
    @FXML
    private TextField authors_book;
    @FXML
    private TextField categories_book;
    @FXML
    private TextField publishdate_book;
    @FXML
    private TextField publisher_book;
    @FXML
    private TextField isbn_book;
    @FXML
    private TextField pages_book;
    @FXML
    private TextField id_book;
    @FXML
    private TextField quantity_in_stock;
    @FXML
    private TextField link_image_book;
    @FXML
    private TextArea description_book;
    @FXML
    private Button button_add_book_form;
    @FXML
    private AnchorPane anchorpane_add_api;
    @FXML
    private Button button_add_book_hand;
    @FXML
    private Button button_add_api;
    @FXML
    private Button button_update_book_form;

    @FXML
    private TextField search_bar;
    @FXML
    private FontIcon button_search;

    @FXML
    private MenuButton button_option_search;

    @FXML
    private TextField textfield_isbn_add_book;
    @FXML
    private FontIcon button_search_api;
    @FXML
    private ImageView img_book_api;
    @FXML
    private Text title_book_api;
    @FXML
    private Text authors_book_api;
    @FXML
    private Text categories_book_api;
    @FXML
    private Button button_add_form_book_api;
    @FXML
    private Button button_cancel1;
    @FXML
    private AnchorPane anchorpane_detail_book_api;
    @FXML
    private Text text_search_api_fail;
    @FXML
    private Text edit_book_label;
    @FXML
    private Text add_image_label;

    @FXML
    private Text message_add_book;

    @FXML
    private Text message_add_book_api;


    @FXML
    private AnchorPane loading;

    @FXML
    private VBox vbox_result_search;
    @FXML
    private  ScrollPane scroll_result_search;
    @FXML
    private FontIcon button_delete_search;
    @FXML
    private FontIcon button_delete_add_title;
    @FXML
    private FontIcon button_delete_add_authors;
    @FXML
    private FontIcon button_delete_add_categories;
    @FXML
    private FontIcon button_delete_add_publishdate;
    @FXML
    private FontIcon button_delete_add_publisher;
    @FXML
    private FontIcon button_delete_add_isbn;
    @FXML
    private FontIcon button_delete_add_id;
    @FXML
    private FontIcon button_delete_add_quantity;
    @FXML
    private FontIcon button_delete_add_img;
    @FXML
    private FontIcon button_delete_add_description;
    @FXML
    private FontIcon button_delete_add_bookapi;
    @FXML
    private FontIcon button_delete_add_pages;
    @FXML
    private Button button_qr_scan;
    @FXML
    private Button button_upload_qr;
    @FXML
    private ImageView qr_image;
    @FXML
    private Text qr_error_message;
    @FXML
    private GridPane qr_book_info;
    @FXML
    private ImageView qr_book_image;
    @FXML
    private Text qr_book_title;
    @FXML
    private Button button_qr_book_details;
    @FXML
    private Button button_close_qr_modal;
    @FXML
    private javafx.scene.layout.AnchorPane qr_scan_modal;
    @FXML
    private HBox hBox_Pages;
    @FXML
    private TextField text_pages;
    @FXML
    private MenuButton button_option_sort;
    @FXML
    private Text total_pages;


    private final FileChooser qrChooser = new FileChooser();

    public void initialize() {
        initUI();
        clickButton();
    }

    /**
     * Initializes the user interface (UI) by loading the books, setting up the QR code scanner,
     * and configuring the text formatter for the pages input field.
     * It also hides the "add book" form initially and prepares the UI for interaction.
     */
    @Override
    public void initUI() {
        loadBook();
        anchorpane_add_book.setVisible(false);
        setLoading(false);
        loading.getChildren().add(createAnChorPaneLoading(0, 0));
        bookManagementController = this;
        setUpQRScan();
        text_pages.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
    }

    /**
     * Configures the actions for various buttons in the UI, such as adding a book,
     * canceling the form, submitting the form, switching between adding books by hand or API,
     * searching for books, and handling pagination.
     */
    @Override
    public void clickButton() {
        button_add_book.setOnAction(e -> {
            add_image_label.setText("Add Image");
            button_update_book_form.setVisible(false);
            button_add_book_form.setVisible(true);
            anchorpane_add_book.setVisible(true);
            button_add_book_hand.setVisible(true);
            button_add_api.setVisible(true);
            edit_book_label.setVisible(false);
        });

        button_cancel.setOnAction(e -> closeAddBookModal());
        button_cancel1.setOnAction(e -> closeAddBookModal());
        button_add_book_form.setOnAction(e -> addBook());

        button_add_book_hand.setOnAction(e -> {
            anchorpane_add_api.setVisible(false);
            if (!button_add_book_hand.getStyleClass().contains("btn-primary")) {
                button_add_book_hand.getStyleClass().add("btn-primary");
            }
            if (!button_add_api.getStyleClass().contains("modal-tab-unactive")) {
                button_add_api.getStyleClass().add("modal-tab-unactive");
            }
            button_add_book_hand.getStyleClass().remove("modal-tab-unactive");
            button_add_api.getStyleClass().remove("btn-primary");
        });

        button_add_api.setOnAction(e -> {
            anchorpane_add_api.setVisible(true);
            button_add_form_book_api.setDisable(true);
            if (!button_add_api.getStyleClass().contains("btn-primary")) {
                button_add_api.getStyleClass().add("btn-primary");
            }
            if (!button_add_book_hand.getStyleClass().contains("modal-tab-unactive")) {
                button_add_book_hand.getStyleClass().add("modal-tab-unactive");
            }
            button_add_api.getStyleClass().remove("modal-tab-unactive");
            button_add_book_hand.getStyleClass().remove("btn-primary");
        });

        button_search.setOnMouseClicked(e -> searchContent());

        for (MenuItem item : button_option_search.getItems()) {
            item.setOnAction(event -> button_option_search.setText(item.getText()));
        }

        for (MenuItem item : button_option_sort.getItems()) {
            item.setOnAction(event -> {
                button_option_sort.setText(item.getText());
                sort(item.getText());
            });
        }

        button_search_api.setOnMouseClicked(e -> searchBookByAPI());
        renderResultSearch();
        setDeleteConfirmationController();

        deleteConfirmationController
                .statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                deleteConfirmation();
            }
        });

        buttonDeleteAll();
        setButtonDeleteText();

        text_pages.setOnAction(e -> {
            Integer p = Integer.parseInt(text_pages.getText());
            if (p > pages) changePages(pages);
            else if (p < 1) changePages(1);
            else changePages(p);
        });
    }

    /**
     * Sets the visibility of the loading spinner, showing or hiding it based on the
     * provided parameter.
     *
     * @param isVisible A boolean value indicating whether to show or hide the
     *                 loading spinner.
     */
    @Override
    public void setLoading(boolean isVisible) {
        loading.setVisible(isVisible);
    }


    /**
     * Sets up the delete actions for various input fields. When the delete button is clicked,
     * the corresponding text fields will be cleared.
     */
    public void buttonDeleteAll() {
        button_delete_search.setOnMouseClicked(e -> { search_bar.setText(""); });
        button_delete_add_title.setOnMouseClicked(e -> { title_book.setText(""); });
        button_delete_add_authors.setOnMouseClicked(e -> { authors_book.setText(""); });
        button_delete_add_publishdate.setOnMouseClicked(e -> { publishdate_book.setText(""); });
        button_delete_add_isbn.setOnMouseClicked(e -> { isbn_book.setText(""); });
        button_delete_add_description.setOnMouseClicked(e -> { description_book.setText(""); });
        button_delete_add_img.setOnMouseClicked(e -> { link_image_book.setText(""); });
        button_delete_add_quantity.setOnMouseClicked(e -> { quantity_in_stock.setText(""); });
        button_delete_add_id.setOnMouseClicked(e -> { id_book.setText(""); });
        button_delete_add_publisher.setOnMouseClicked(e -> { publisher_book.setText(""); });
        button_delete_add_categories.setOnMouseClicked(e -> { categories_book.setText(""); });
        button_delete_add_pages.setOnMouseClicked(e -> { pages_book.setText(""); });
        button_delete_add_bookapi.setOnMouseClicked(e -> { textfield_isbn_add_book.setText(""); });
    }

    /**
     * Sets up the visibility of the delete buttons based on the text fields' content.
     * If a text field is empty, the corresponding delete button will be hidden.
     * If the text field contains text, the delete button will be visible.
     */
    public void setButtonDeleteText() {
        search_bar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (search_bar.getText().equals("")) {
                button_delete_search.setVisible(false);
            } else {
                button_delete_search.setVisible(true);
            }
        });

        title_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (title_book.getText().equals("")) {
                button_delete_add_title.setVisible(false);
            } else {
                button_delete_add_title.setVisible(true);
            }
        });

        authors_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (authors_book.getText().equals("")) {
                button_delete_add_authors.setVisible(false);
            } else {
                button_delete_add_authors.setVisible(true);
            }
        });

        publishdate_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (publishdate_book.getText().equals("")) {
                button_delete_add_publishdate.setVisible(false);
            } else {
                button_delete_add_publishdate.setVisible(true);
            }
        });

        publisher_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (publisher_book.getText().equals("")) {
                button_delete_add_publisher.setVisible(false);
            } else {
                button_delete_add_publisher.setVisible(true);
            }
        });

        isbn_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isbn_book.getText().equals("")) {
                button_delete_add_isbn.setVisible(false);
            } else {
                button_delete_add_isbn.setVisible(true);
            }
        });

        id_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (id_book.getText().equals("")) {
                button_delete_add_id.setVisible(false);
            } else {
                button_delete_add_id.setVisible(true);
            }
        });

        pages_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (pages_book.getText().equals("")) {
                button_delete_add_pages.setVisible(false);
            } else {
                button_delete_add_pages.setVisible(true);
            }
        });

        quantity_in_stock.textProperty().addListener((observable, oldValue, newValue) -> {
            if (quantity_in_stock.getText().equals("")) {
                button_delete_add_quantity.setVisible(false);
            } else {
                button_delete_add_quantity.setVisible(true);
            }
        });

        link_image_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (link_image_book.getText().equals("")) {
                button_delete_add_img.setVisible(false);
            } else {
                button_delete_add_img.setVisible(true);
            }
        });

        description_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (description_book.getText().equals("")) {
                button_delete_add_description.setVisible(false);
            } else {
                button_delete_add_description.setVisible(true);
            }
        });

        categories_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (categories_book.getText().equals("")) {
                button_delete_add_categories.setVisible(false);
            } else {
                button_delete_add_categories.setVisible(true);
            }
        });

        textfield_isbn_add_book.textProperty().addListener((observable, oldValue, newValue) -> {
            if (textfield_isbn_add_book.getText().equals("")) {
                button_delete_add_bookapi.setVisible(false);
            } else {
                button_delete_add_bookapi.setVisible(true);
            }
        });
    }

    /**
     * Exits the delete confirmation modal and resets the delete status.
     */
    @Override
    public void ExitDeleteConfirmation() {
        deleteConfirmationController.setStatus(false);
        deleteConfirmationController.exit();
    }

    /**
     * Sets the delete confirmation controller by loading the corresponding FXML file
     * and initializing the controller for delete confirmation.
     */
    @Override
    public void setDeleteConfirmationController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/fxml/deleteConfirmation.fxml"));
            Parent root = loader.load();
            anchorPaneDelete = (javafx.scene.layout.AnchorPane) root;
            deleteConfirmationController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Confirms the deletion of the book. If the book is not currently borrowed or overdue,
     * it will be deleted. Otherwise, an error message will be shown.
     */
    @Override
    public void deleteConfirmation() {
        setLoading(true);
        if (deleteConfirmationController.getStatus()) {
            int numBorrowing = borrowService
                    .getNumBorrowsOfBookWithStatus(bookDelete.getId(), Borrow.BORROWING);
            int numOverdue = borrowService
                    .getNumBorrowsOfBookWithStatus(bookDelete.getId(), Borrow.OVERDUE);
            if (numOverdue + numBorrowing == 0) {
                bookService.deleteBook(bookDelete.getId());
                loadBook();
                ExitDeleteConfirmation();
            } else {
                deleteConfirmationController
                        .showError("Unable to delete the book because it is currently on loan");
            }
        }
    }



    /**
     * Returns the type of search based on the selected search option
     * (Category, Title, Author, Publisher).
     *
     * @return The type of search as a string (CATEGORY, TITLE, AUTHOR, PUBLISHER).
     */
    @Override
    public String getSearchType() {
        String type_search = new String();
        if(button_option_search.getText().equals("Category")) type_search = CATEGORY;
        else if (button_option_search.getText().equals("Title")) type_search = TITLE;
        else if (button_option_search.getText().equals("Author")) type_search = AUTHOR;
        else if (button_option_search.getText().equals("Publisher")) type_search = PUBLISHER;
        else type_search = CATEGORY;
        return type_search;
    }

    /**
     * Performs the search operation using the current search
     * type and the text in the search bar.
     * This method retrieves the search results based on the selected search type
     * (Category, Title, Author, Publisher).
     */
    @Override
    public void searchContent() {
        String type_search = getSearchType();
        search(search_bar.getText(), type_search, false);
    }

    /**
     * Performs the search operation based on the given query and search type,
     * and displays the results. This method also handles pagination and displays books
     * in a paginated manner with a maximum of {@link #MAX_BOOKS_IN_PAGE} books per page.
     *
     * @param query The search query entered by the user.
     * @param type The search type (CATEGORY, TITLE, AUTHOR, PUBLISHER).
     * @param isAddHistory Indicates if the search should be added to history.
     */
    @Override
    public void search(String query, String type, boolean isAddHistory) {
        vbox_result_search.getChildren().clear();
        listpageBook.clear();
        scroll_result_search.setVisible(false);
        vbox_list_book.getChildren().clear();
        setLoading(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Book> listBook = new ArrayList<>();
                if (type == AUTHOR) {
                    listBook = bookService.getBookByAuthor(query);
                } else if (type == TITLE) {
                    listBook = bookService.getBookByTitle(query);
                } else if (type == CATEGORY) {
                    listBook = bookService.getBookByCategory(query);
                } else if (type == PUBLISHER) {
                    listBook = bookService.getBookByPublisher(query);
                }
                list_book_current = listBook;
                pages = (int) Math.ceil(1.0 * listBook.size() / MAX_BOOKS_IN_PAGE);
                total_pages.setText("/" + pages);
                text_pages.setText("1");
                current_page = 1;
                List<Book> currentBooks = new ArrayList<>();
                AtomicInteger index = new AtomicInteger(0);
                for (Book book : listBook) {
                    int currentIndex = index.getAndIncrement();
                    int sizeList = listBook.size();
                    javafx.application.Platform.runLater(() -> {
                        currentBooks.add(book);
                        if (currentBooks.size() >= MAX_BOOKS_IN_PAGE
                                || currentIndex == sizeList - 1) {
                            VBox vBox = new VBox();
                            for (Book b : currentBooks) {
                                vBox.getChildren()
                                        .add(createAnchorPaneManagementBook(b, loading));
                            }
                            if (listpageBook.isEmpty()) vbox_list_book.getChildren().add(vBox);
                            listpageBook.add(vBox);
                            currentBooks.clear();
                        }
                    });
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            createHboxPages(pages, 1);
            setLoading(false);
        });
        task.setOnFailed(event -> {
            setLoading(false);
        });
        new Thread(task).start();
    }

    /**
     * Searches for the books based on the current search query in the search bar,
     * and retrieves the books matching the selected search type. It also provides
     * auto-suggestions based on the search query for categories, authors, titles, and publishers.
     */
    @Override
    public void searchResult() {
        String type_search = getSearchType();
        List<Book> listBook = new ArrayList<>();
        if (type_search == AUTHOR) {
            listBook = bookService.getBookByAuthor(search_bar.getText());
        } else if (type_search == TITLE) {
            listBook = bookService.getBookByTitle(search_bar.getText());
        } else if (type_search == PUBLISHER) {
            listBook = bookService.getBookByPublisher(search_bar.getText());
        } else {
            List<String> listcategory = bookService.getDistinctCategories(search_bar.getText());
            for (String category : listcategory) {
                TextField textField = new TextField();
                textField.setFont(Font.font(14));
                textField.setStyle("-fx-background-color: " +
                        "transparent; -fx-cursor: hand;-fx-text-fill:#333333");
                textField.setEditable(false);
                textField.setOnMouseEntered(e -> {
                    textField.setStyle("-fx-background-color:#5f43b2 ;" +
                            "-fx-text-fill:white;-fx-cursor: hand;");
                });
                textField.setOnMouseExited(e -> {
                    textField.setStyle("-fx-background-color: transparent ;" +
                            "-fx-text-fill:#333333;-fx-cursor: hand;");
                });
                textField.setOnMouseClicked(e -> {
                    search_bar.setText(textField.getText());
                    searchContent();
                });
                vbox_result_search.getChildren().add(textField);
                scroll_result_search.setVisible(true);
                textField.setText(category);
            }
        }
        int limit = 0;
        for (Book book : listBook) {
            TextField textField = new TextField();
            textField.setFont(Font.font(14));
            switch (type_search) {
                case AUTHOR:
                    textField.setText(book.getAuthors()[0]);
                    break;
                case TITLE:
                    textField.setText(book.getTitle());
                    break;
                case PUBLISHER:
                    textField.setText(book.getPublisher());
                    break;
            }
            textField.setStyle("-fx-background-color: transparent; -fx-cursor:" +
                    " hand;-fx-text-fill:#333333");
            textField.setEditable(false);
            textField.setOnMouseEntered(e -> {
                textField.setStyle("-fx-background-color:#5f43b2 ;" +
                        "-fx-text-fill:white;-fx-cursor: hand;");
            });
            textField.setOnMouseExited(e -> {
                textField.setStyle("-fx-background-color: transparent ;" +
                        "-fx-text-fill:#333333;-fx-cursor: hand;");
            });
            textField.setOnMouseClicked(e -> {
                search_bar.setText(textField.getText());
                searchContent();
            });
            vbox_result_search.getChildren().add(textField);
            scroll_result_search.setVisible(true);
            if (limit++ > MAX_SUGGESTION) break;
        }
    }

    /**
     * Sets up a listener on the search bar to trigger search results whenever the text in
     * the search bar changes. It also manages the visibility of the search results box.
     */
    @Override
    public void renderResultSearch() {
        vbox_result_search.getChildren().clear();
        scroll_result_search.setVisible(false);
        StringProperty textProperty = search_bar.textProperty();
        textProperty.addListener((observable, oldValue, newValue) -> {
            vbox_result_search.getChildren().clear();
            scroll_result_search.setVisible(false);
            if (search_bar.getText().length() > 0) searchResult();
        });
    }



    /**
     * Sorts the list of books based on the specified type (Title, Author, or ID).
     * The method sorts the books currently being displayed on the current page and
     * updates the book list displayed in the user interface.
     *
     * @param type The type of sorting criteria. It can be one of the following:
     *             - "Title" to sort by book titles.
     *             - "Author" to sort by the first author.
     *             - "ID" to sort by book ID.
     */
    public void sort(String type) {
        String type_search = new String();
        if (type.equals("Title")) type_search = TITLE;
        else if (type.equals("Author")) type_search = AUTHOR;
        else type_search = ID;

        // Get the list of books for the current page
        List<Book> listBook = new ArrayList<>();
        listBook = list_book_current.subList(
                (current_page - 1) * MAX_BOOKS_IN_PAGE,
                Math.min(current_page * MAX_BOOKS_IN_PAGE, list_book_current.size())
        );

        // Sort the list based on the specified type
        switch (type_search) {
            case TITLE:
                listBook.sort((book1, book2)
                        -> CharSequence.compare(book1.getTitle(), book2.getTitle()));
                break;
            case AUTHOR:
                listBook.sort((book1, book2)
                        -> CharSequence.compare(book1.getAuthors()[0], book2.getAuthors()[0]));
                break;
            default:
                listBook.sort((book1, book2)
                        -> CharSequence.compare(book1.getId(), book2.getId()));
                break;
        }

        // Clear the existing displayed books and update with the sorted list
        vbox_list_book.getChildren().clear();
        for (Book book : listBook) {
            javafx.application.Platform.runLater(() -> {
                vbox_list_book.getChildren().add(createAnchorPaneManagementBook(book, loading));
            });
        }
    }


    /**
     * Loads the list of books from the database and displays them in the user interface.
     * This method fetches all the books from the book service, calculates the total number
     * of pages based on the number of books, and adds them to the `vbox_list_book` in pages.
     * The loading indicator is shown during the operation.
     */
    public void loadBook() {
//        vbox_list_book.setSpacing(5);
        vbox_list_book.getChildren().clear();
        listpageBook.clear();
        setLoading(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Book> listBook = new ArrayList<>();
                listBook = bookService.getAllBooks();
                list_book_current = listBook;
                pages = (int) Math.ceil(1.0 * listBook.size() / MAX_BOOKS_IN_PAGE);
                total_pages.setText("/" + pages); current_page = 1;
                List<Book> currentBooks = new ArrayList<>();
                AtomicInteger index = new AtomicInteger(0);
                for (Book book : listBook) {
                    int currentIndex = index.getAndIncrement();
                    int sizeList = listBook.size();
                    javafx.application.Platform.runLater(() -> {
                        currentBooks.add(book);
                        if (currentBooks.size() >= MAX_BOOKS_IN_PAGE
                                || currentIndex == sizeList - 1) {
                            VBox vBox = new VBox();
                            for (Book b : currentBooks) {
                                vBox.getChildren().add(createAnchorPaneManagementBook(b, loading));
                            }
                            if (listpageBook.isEmpty()) vbox_list_book.getChildren().add(vBox);
                            listpageBook.add(vBox);
                            currentBooks.clear();
                        }
                    });
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            setLoading(false);
            createHboxPages(pages,1);
        });
        task.setOnFailed(event -> {
            setLoading(false);
        });

        new Thread(task).start();
    }

    /**
     * Clears the input fields in the "Add Book" form.
     * This method resets all the text fields related to book details to empty strings,
     * ensuring the form is cleared and ready for new data input.
     */
    public void clearBook() {
        textfield_isbn_add_book.setText("");
        id_book.setText("");
        publisher_book.setText("");
        isbn_book.setText("");
        pages_book.setText("");
        quantity_in_stock.setText("");
        title_book.setText("");
        publishdate_book.setText("");
        authors_book.setText("");
        categories_book.setText("");
        description_book.setText("");
        link_image_book.setText("");
        message_add_book.setText("");
        message_add_book_api.setText("");
    }


    /**
     * Validates the input values, sets the properties of a new book, and checks for
     * any validation errors.
     *
     * @param book The book object to be created.
     * @return true if the book data is valid and set successfully, false otherwise.
     */
    public boolean setAddBook(Book book) {
        String id = id_book.getText();
        String title = title_book.getText();
        String publisher = publisher_book.getText();
        String isbn = isbn_book.getText();
        String publishDate = publishdate_book.getText();
        String description = description_book.getText();
        String pages = pages_book.getText();
        String quantity = quantity_in_stock.getText();
        String imageLink = link_image_book.getText();
        String authors = authors_book.getText();
        String categories = categories_book.getText();

        if (title.isEmpty() || id.isEmpty() || isbn.isEmpty()
                || description.isEmpty() || categories.isEmpty()
                || quantity.isEmpty() || pages.isEmpty()) {
            showMessage(message_add_book,"Please fill in all required fields.");
            return false;
        }

        if (!isValidISBN(isbn)) {
            showMessage(message_add_book, "Invalid ISBN format.");
            return false;
        }

        try {
            int pageCount = Integer.parseInt(pages);
            if (pageCount < 0) {
                showMessage(message_add_book,
                        "Page count must be a non-negative number.");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage(message_add_book, "Page count must be a valid number.");
            return false;
        }


        try {
            int stockQuantity = Integer.parseInt(quantity);
            if (stockQuantity < 0) {
                showMessage(message_add_book,
                        "Quantity in stock must be a non-negative number.");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage(message_add_book, "Quantity in stock must be a valid number.");
            return false;
        }

        if (!isValidISBN(isbn)) {
            showMessage(message_add_book, "Invalid ISBN format.");
            return false;
        }

        if (bookService.isIsbnTaken(isbn)) {
            showMessage(message_add_book, "ISBN is already taken by another book.");
            return false;
        }

        if (bookService.isIdTaken(isbn)) {
            showMessage(message_add_book, "ID is already taken by another book.");
            return false;
        }

        if (!publishDate.isEmpty() && !isValidDate(publishDate)) {
            showMessage(message_add_book, "Invalid publish date");
            return false;
        }


        if (!imageLink.isEmpty() && !isValidImageURL(imageLink)) {
            System.out.println();
            showMessage(message_add_book, "Invalid image URL");
            return false;
        }

        book.setId(id);
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setDescription(description);
        book.setPageCount(Integer.parseInt(pages));
        book.setQuantityInStock(Integer.parseInt(quantity));
        book.setPublishDate(publishDate.isEmpty() ? null : publishDate);

        book.setAuthors(authors.isEmpty() ? new String[0] : authors.split(","));
        book.setCategories(categories.isEmpty() ? new String[0] : categories.split(","));
        book.setPublisher(publisher.isEmpty() ? "unknown" : publisher);

        if (imageLink.isEmpty()) {
            book.setImage(loadImageFromResources(URL_NO_IMG));
        } else {
            book.setImage(downloadImage(imageLink));
        }

        return true;
    }

    /**
     * Validates the input values, updates the properties of an existing book,
     * and checks for any validation errors.
     *
     * @param book The book object to be updated.
     * @return true if the book data is valid and updated successfully, false otherwise.
     */
    public boolean setUpdateBook(Book book) {
        String currId = book.getId();
        String currIsbn = book.getIsbn();

        String id = id_book.getText();
        String title = title_book.getText();
        String publisher = publisher_book.getText();
        String isbn = isbn_book.getText();
        String publishDate = publishdate_book.getText();
        String description = description_book.getText();
        String pages = pages_book.getText();
        String quantity = quantity_in_stock.getText();
        String imageLink = link_image_book.getText();
        String authors = authors_book.getText();
        String categories = categories_book.getText();

        if (title.isEmpty() || id.isEmpty() || isbn.isEmpty()
                || description.isEmpty() || categories.isEmpty()
                || quantity.isEmpty() || pages.isEmpty()) {
            showMessage(message_add_book,"Please fill in all required fields.");
            return false;
        }

        if (!isValidISBN(isbn)) {
            showMessage(message_add_book, "Invalid ISBN format.");
            return false;
        }

        try {
            int pageCount = Integer.parseInt(pages);
            if (pageCount < 0) {
                showMessage(message_add_book, "Page count must be a non-negative number.");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage(message_add_book, "Page count must be a valid number.");
            return false;
        }


        try {
            int stockQuantity = Integer.parseInt(quantity);
            if (stockQuantity < 0) {
                showMessage(message_add_book,
                        "Quantity in stock must be a non-negative number.");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage(message_add_book, "Quantity in stock must be a valid number.");
            return false;
        }

        if (!isValidISBN(isbn)) {
            showMessage(message_add_book, "Invalid ISBN format.");
            return false;
        }

        if (!currIsbn.equals(isbn) && bookService.isIsbnTaken(isbn)) {
            showMessage(message_add_book, "ISBN is already taken by another book.");
            return false;
        }

        if (!currId.equals(id) && bookService.isIdTaken(isbn)) {
            showMessage(message_add_book, "ID is already taken by another book.");
            return false;
        }

        if (!publishDate.isEmpty() && !isValidDate(publishDate)) {
            showMessage(message_add_book, "Invalid publish date");
            return false;
        }


        if (!imageLink.isEmpty() && !isValidImageURL(imageLink)) {
            System.out.println();
            showMessage(message_add_book, "Invalid image URL");
            return false;
        }

        book.setId(id);
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setDescription(description);
        book.setPageCount(Integer.parseInt(pages));
        book.setQuantityInStock(Integer.parseInt(quantity));
        book.setPublishDate(publishDate.isEmpty() ? null : publishDate);

        book.setAuthors(authors.isEmpty() ? new String[0] : authors.split(","));
        book.setCategories(categories.isEmpty() ? new String[0] : categories.split(","));
        book.setPublisher(publisher.isEmpty() ? "unknown" : publisher);

        if (!imageLink.isEmpty()) {
            book.setImage(downloadImage(imageLink));
        }

        return true;
    }

    /**
     * Displays a message in the provided message text field.
     *
     * @param message The text field where the message will be displayed.
     * @param msg The message to be shown in the text field.
     */
    public void showMessage(Text message, String msg) {
        message.setText(msg);
        message.setVisible(true);
    }

    /**
     * Sets the input fields in the "Edit Book" form with the details of the given book.
     *
     * @param book The book whose details will be populated in the form.
     */
    public void setInputBook(Book book) {
        id_book.setText(book.getId());
        publisher_book.setText(book.getPublisher());
        isbn_book.setText(book.getIsbn());
        pages_book.setText(book.getPageCount().toString());
        quantity_in_stock.setText(book.getQuantityInStock().toString());
        title_book.setText(book.getTitle());
        description_book.setText(book.getDescription());

        publishdate_book.setText(book.getPublishDate() == null ? "" : book.getPublishDate());
        authors_book.setText(String.join(",",book.getAuthors()));
        categories_book.setText(String.join(",",book.getCategories()));
        link_image_book.setText("");
    }

    /**
     * Closes the "Add Book" modal window and clears the form.
     * This method hides the modal window and resets the form fields to their default state.
     */
    public void closeAddBookModal() {
        anchorpane_add_book.setVisible(false);
        anchorpane_detail_book_api.setVisible(false);
        text_search_api_fail.setVisible(false);
        clearBook();
    }

    /**
     * Adds a new book to the system after validating the input data.
     * This method creates a new `Book` object, validates its data using `setAddBook(Book book)`,
     * and then adds the book to the book service. After adding the book, the form is cleared,
     * the "Add Book" modal is closed, and the book list is reloaded.
     */
    public void addBook() {
        Book book = new Book();
        if (setAddBook(book)) {
            bookService.addBook(book);
            clearBook();
            anchorpane_add_book.setVisible(false);
            loadBook();
        }
    }

    /**
     * Initiates the process of deleting a book from the system.
     * This method sets the `bookDelete` object to the specified book, displays a confirmation dialog
     * asking the user if they are sure about deleting the book, and shows the confirmation pane.
     *
     * @param book The book to be deleted.
     */
    public void deleteBook(Book book) {
        this.bookDelete = book;
        deleteConfirmationController.render("Are you sure to delete this book?");
        if (!mainAnchorPane.getChildren().contains(anchorPaneDelete)) {
            mainAnchorPane.getChildren().add(anchorPaneDelete);
        }
    }

    /**
     * Updates the details of an existing book in the system.
     * This method validates the updated book information using `setUpdateBook(Book book)`,
     * and if the validation is successful, it updates the book in the book service.
     * After updating, the "Add Book" modal is closed, and the book list is reloaded.
     *
     * @param book The book with updated details to be saved.
     */
    public void updateBook(Book book) {
        if (setUpdateBook(book)) {
            bookService.updateBook(book);
            anchorpane_add_book.setVisible(false);
            loadBook();
        }
    }


    /**
     * Displays the form to update an existing book's details.
     * This method populates the form with the current book's data, changes the form title
     * and button visibility to indicate the update mode, and sets up the update action.
     *
     * @param book The book whose details are to be updated.
     */
    public void showFormUpdate(Book book) {
        setInputBook(book);
        add_image_label.setText("New Image");
        anchorpane_add_book.setVisible(true);
        edit_book_label.setVisible(true);
        button_update_book_form.setVisible(true);
        button_add_book_form.setVisible(false);
        button_add_book_hand.setVisible(false);
        button_add_api.setVisible(false);
        button_update_book_form.setOnAction(e -> updateBook(book));
    }

    /**
     * Loads the detailed view of a book and redirects to the book details page.
     * This method uses the provided `book` to navigate to the book's detail page for the admin UI.
     *
     * @param book The book whose details are to be displayed.
     * @param event The event that triggered the book detail loading.
     * @throws IOException If an error occurs while trying to load the details.
     */
    public void loadDetailsBook(Book book, Event event) throws IOException {
        try {
            toBookDetail(event, book, BOOK_DETAILS_ADMIN_URL, BOOKMNG_UI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches for a book by its ISBN using an external API.
     * If the book is found, its details (including image, title, authors, and categories)
     * are displayed in the UI. If not, a failure message is shown.
     */
    public void searchBookByAPI() {
        Book bookAPI = searchBookByISBN(textfield_isbn_add_book.getText());
        if (bookAPI == null) {
            anchorpane_detail_book_api.setVisible(false);
            text_search_api_fail.setVisible(true);
            button_add_form_book_api.setDisable(true);
        } else {
            img_book_api.setImage(new Image(new ByteArrayInputStream(bookAPI.getImage())));
            title_book_api.setText(bookAPI.getTitle());
            authors_book_api.setText(String.join(",", bookAPI.getAuthors()));
            categories_book_api.setText(String.join(",", bookAPI.getCategories()));
            anchorpane_detail_book_api.setVisible(true);
            text_search_api_fail.setVisible(false);
            button_add_form_book_api.setDisable(false);
            button_add_form_book_api.setOnAction(e -> addBookByAPI(bookAPI));
        }
    }

    /**
     * Adds a book to the system using data obtained from an external API.
     * This method checks if the ISBN is already taken by another book, and if not,
     * it adds the book to the system and updates the UI.
     *
     * @param bookAPI The book data obtained from the external API to be added to the system.
     */
    public void addBookByAPI(Book bookAPI) {
        String isbn = bookAPI.getIsbn();
        if (bookService.isIsbnTaken(isbn)) {
            showMessage(message_add_book_api, "ISBN is already taken by another book.");
            return;
        }
        bookService.addBook(bookAPI);
        anchorpane_add_book.setVisible(false);
        anchorpane_detail_book_api.setVisible(false);
        text_search_api_fail.setVisible(false);
        message_add_book_api.setVisible(false);
        message_add_book_api.setText("");
        textfield_isbn_add_book.setText("");
        loadBook();
    }


    /**
     * Sets up the QR code scanning functionality by configuring the QR image file chooser
     * and setting up event handlers for buttons related to QR scanning.
     * This method allows the user to upload a QR image or initiate a QR scan modal.
     */
    public void setUpQRScan() {
        qrChooser.setTitle("Select a QR image");
        qrChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        button_upload_qr.setOnMouseClicked(e -> {
            File selectedFile = qrChooser.showOpenDialog(null);
            if (selectedFile != null) {
                handleQRFile(selectedFile);
            } else {
                showQRError("Invalid file format");
            }
        });
        button_qr_scan.setOnMouseClicked(e -> qr_scan_modal.setVisible(true));
        button_close_qr_modal.setOnMouseClicked(e -> qr_scan_modal.setVisible(false));
    }

    /**
     * Handles the selected QR code image file. It attempts to read the QR code from the
     * selected file and displays the QR image in the UI. The QR code is processed in a separate
     * background task to avoid blocking the main UI thread.
     *
     * @param selectedFile The image file that contains the QR code to be processed.
     */
    private void handleQRFile(File selectedFile) {
        Image qrImage = new Image(selectedFile.toURI().toString());
        qr_image.setImage(qrImage);

        String originalText = button_upload_qr.getText();
        qr_book_info.setVisible(false);
        button_upload_qr.setText("Reading QR code...");
        button_upload_qr.setDisable(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws IOException, NotFoundException,
                    BookNotFoundException, InterruptedException {
                Thread.sleep(200);
                return QRCode.readQR(selectedFile.getAbsolutePath());
            }
        };

        task.setOnSucceeded(event -> handleQRSuccess(task.getValue(), originalText));
        task.setOnFailed(event -> handleQRFailure(originalText));

        new Thread(task).start();
    }

    /**
     * Handles the successful reading of the QR code. If the QR content corresponds to a valid book,
     * it updates the book details in the UI. Otherwise, it shows an error message.
     *
     * @param qrContent The content extracted from the QR code.
     * @param originalText The original text of the upload button, used to reset the button's state.
     */
    private void handleQRSuccess(String qrContent, String originalText) {
        button_upload_qr.setText(originalText);
        button_upload_qr.setDisable(false);
        Book book = bookService.getBook(qrContent);
        if (book == null) {
            showQRError("Book not found");
        } else {
            qr_error_message.setVisible(false);
            updateQRBookInfo(book);
        }
    }

    /**
     * Handles the failure of reading the QR code. It resets the button state and displays an
     * error message.
     *
     * @param originalText The original text of the upload button, used to reset the
     *                    button's state.
     */
    private void handleQRFailure(String originalText) {
        button_upload_qr.setText(originalText);
        button_upload_qr.setDisable(false);
        showQRError("Invalid QR code");
    }

    /**
     * Displays an error message related to QR code reading. The message is shown in the UI and
     * the book info is hidden.
     *
     * @param message The error message to be displayed.
     */
    private void showQRError(String message) {
        qr_error_message.setText(message);
        qr_error_message.setVisible(true);
        qr_book_info.setVisible(false);
    }

    /**
     * Updates the UI with the information of a book retrieved from a QR code.
     * The book's title and image are displayed, and a button is set up to view more details
     * about the book.
     *
     * @param book The book to display information about.
     */
    public void updateQRBookInfo(Book book) {
        qr_book_title.setText(book.getTitle());
        qr_book_image.setImage(new Image(new ByteArrayInputStream(book.getImage())));
        qr_book_info.setVisible(true);
        qr_error_message.setVisible(false);
        button_qr_book_details.setOnMouseClicked(event -> {
            setLoading(true);
            try {
                toBookDetail(event, book, BOOK_DETAILS_ADMIN_URL, BOOKMNG_UI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Creates the pagination navigation bar. It generates buttons for the pages based on the
     * total number of pages and the current page. The navigation buttons ("<" and ">")
     * are also created.
     *
     * @param pages The total number of pages.
     * @param start The current page number.
     */
    public void createHboxPages(int pages, int start) {
        hBox_Pages.getChildren().clear();
        hBox_Pages.getChildren().
                add(createNavButton("<", start > 1 ? start - 1 : start, start == 1));
        if (pages <= 5) {
            for (int i = 0; i < pages; i++) {
                int pageIndex = i + 1;
                hBox_Pages.getChildren().add(createPageButton(pageIndex, start));
            }
        } else {
            if (start + 2 > pages) {
                hBox_Pages.getChildren().add(createEllipsisButton());
                for (int i = 3; i >= 0; i--) {
                    int pageIndex = pages - i;
                    hBox_Pages.getChildren().add(createPageButton(pageIndex, start));
                }
            } else if (start - 2 < 0) {
                for (int i = 0; i < 4; i++) {
                    int pageIndex = i + 1;
                    hBox_Pages.getChildren().add(createPageButton(pageIndex, start));
                }
                hBox_Pages.getChildren().add(createEllipsisButton());
            } else {
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
        hBox_Pages.getChildren().add(createNavButton(
                ">", start < pages ? start + 1 : start, start == pages));
    }

    /**
     * Creates a button for a specific page in the pagination navigation bar.
     * The button is styled to indicate whether it is the current page or not.
     *
     * @param pageNumber The number of the page represented by the button.
     * @param currentPage The current page number to compare with.
     * @return The created page button.
     */
    private Button createPageButton(int pageNumber, int currentPage) {
        Button button = new Button(String.valueOf(pageNumber));
        button.getStyleClass().add("pagination-button");
        if (pageNumber == currentPage) {
            button.getStyleClass().add("current-page-button");
        }
        button.setOnMouseClicked(e -> changePages(pageNumber));
        return button;
    }

    /**
     * Creates a navigation button ("<" or ">") for the pagination bar.
     * The button will navigate to the previous or next page based on the target page.
     *
     * @param text The text displayed on the button (either "<" or ">").
     * @param targetPage The page number to navigate to when the button is clicked.
     * @param isdefault A boolean value indicating whether this button is the default
     *                  navigation button.
     * @return The created navigation button.
     */
    private Button createNavButton(String text, int targetPage, boolean isdefault) {
        Button button = new Button(text);
        if(isdefault) button.getStyleClass().addAll("pagination-button","default-button");
        else button.getStyleClass().addAll("pagination-button", "nav-button");
        button.setOnMouseClicked(e -> changePages(targetPage));
        return button;
    }

    /**
     * Creates an ellipsis button ("...") to indicate more pages exist but are not displayed.
     * The button is disabled and non-clickable.
     *
     * @return The created ellipsis button.
     */
    private Button createEllipsisButton() {
        Button button = new Button("...");
        button.getStyleClass().addAll("pagination-button", "ellipsis-button");
        button.setDisable(true);
        return button;
    }

    /**
     * Changes the current page and updates the displayed list of books. It also updates
     * the pagination
     * navigation bar to reflect the new current page.
     *
     * @param start The page number to navigate to.
     */
    public void changePages(int start) {
        current_page = start;
        text_pages.setText(String.valueOf(start));
        vbox_list_book.getChildren().clear();
        vbox_list_book.getChildren().add(listpageBook.get(start - 1));
        createHboxPages(pages, start);
    }


}