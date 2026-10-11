package com.ddd.bookverse.controller;
import com.ddd.bookverse.exception.BookNotFoundException;
import com.ddd.bookverse.item.AnchorPane;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.utils.NavHelper;
import com.ddd.bookverse.utils.QRCode;
import com.google.zxing.NotFoundException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.ddd.bookverse.constants.Constants.*;
import static com.ddd.bookverse.controller.RightPanelController.setController;
import static com.ddd.bookverse.item.AnchorPane.*;
import static com.ddd.bookverse.utils.AnimationUtil.setAnimationfadeTransition;
import static com.ddd.bookverse.utils.NavHelper.toBookDetail;

/**
 * The `BookListController` class is responsible for managing
 * and displaying the list of books in the application.
 * It implements the `UiController` and `SearchController`
 * interfaces to handle UI interactions and book search functionality.
 *
 * <p>This class is designed to provide the functionality for displaying a list of books,
 * allowing users to
 * interact with the list, perform searches, and update the UI based on the results.
 * It manages the list of books and integrates the search functionality to filter the displayed
 * books based on user input.</p>
 *
 * Implements:
 * - {@link UiController} for UI management, including rendering
 * the book list and handling interactions with the UI.
 * - {@link SearchController} for enabling the search functionality within the book list.
 *
 * @see UiController
 * @see SearchController
 */
public class BookListController implements UiController,SearchController {
    protected final BookService bookService = new BookService();
    private static Stack<Map.Entry<String, String>> historySearch = new Stack<>();
    private final int  MAX_SUGGESTION = 20;
    private final int NUMBER_OF_BOOK_IN_ROW = 5;
    private final int NUMBER_OF_COL_IN_CATEGORY = 3;
    private List<Book> list_book_current = new ArrayList<>();


    @FXML
    private VBox content_booklists;
    @FXML
    private MenuButton button_option_search;
    @FXML
    private TextField search_bar;
    @FXML
    private FontIcon button_search;
    @FXML
    private javafx.scene.layout.AnchorPane loading;
    @FXML
    private javafx.scene.layout.AnchorPane right_panel;
    @FXML
    private FontIcon font_back;
    @FXML
    private VBox vbox_result_search;
    @FXML
    private ScrollPane scroll_result_search;
    @FXML
    private FontIcon button_delete_search;

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

    private final FileChooser qrChooser = new FileChooser();


    public void initialize() {
        initUI();
        clickButton();
    }

    /**
     * Initializes the user interface by loading the content,
     * setting the loading state, configuring the right panel,
     * and setting up QR scan functionality.
     */
    @Override
    public void initUI() {
        loadContent();
        setLoading(false);
        setRightPanel();
        loading.getChildren().add(createAnChorPaneLoading(0,0));
        setUpQRScan();
    }

    /**
     * Sets up click event handlers for the buttons and actions in the UI.
     * It includes handling search button clicks, search option changes,
     * back button actions for navigating through search history,
     * and clearing search results.
     */
    @Override
    public void clickButton() {
        // Search button click event handler
        button_search.setOnMouseClicked(e -> {
            searchContent();
            font_back.setVisible(true);
            vbox_result_search.getChildren().clear();
            scroll_result_search.setVisible(false);
        });

        // Search option selection event handler
        for (MenuItem item : button_option_search.getItems()) {
            item.setOnAction(event -> button_option_search.setText(item.getText()));
        }

        // Back button click event handler for navigating through search history
        font_back.setOnMouseClicked(e -> {
            if (!historySearch.isEmpty()) {
                historySearch.pop();
            }
            if (!historySearch.isEmpty()) {
                Map.Entry<String, String> entry = historySearch.peek();
                search(entry.getKey(), entry.getValue(), false);
                search_bar.setText(entry.getKey());
                button_option_search.setText(entry.getValue());
            } else {
                loadContent();
                font_back.setVisible(false);
            }
        });

        // Trigger search when the user presses Enter in the search bar
        search_bar.setOnAction(event -> {
            searchContent();
            vbox_result_search.getChildren().clear();
            scroll_result_search.setVisible(false);
        });

        // Render search results
        renderResultSearch();

        // Set up delete search button click event
        setButtonDeleteText();
        button_delete_search.setOnMouseClicked(e -> { search_bar.setText(""); });
    }

    /**
     * Sets the visibility of the loading spinner.
     *
     * @param isVisible A boolean indicating whether the loading spinner should be visible.
     */
    @Override
    public void setLoading(boolean isVisible) {
        loading.setVisible(isVisible);
    }


    // Search Controller
    /**
     * Returns the type of search based on the current selection in the search options.
     * The type can be "Category", "Title", "Author", or "Publisher", corresponding
     * to the relevant field in the `Book` class.
     *
     * @return The search type as a String.
     */
    @Override
    public String getSearchType() {
        String type_search = new String();
        if(button_option_search.getText().equals("Category")) type_search = Book.CATEGORY;
        else if (button_option_search.getText().equals("Title")) type_search = Book.TITLE;
        else if (button_option_search.getText().equals("Author")) type_search = Book.AUTHOR;
        else if (button_option_search.getText().equals("Publisher")) type_search = Book.PUBLISHER;
        else type_search = Book.CATEGORY;
        return type_search;
    }

    /**
     * Initiates the search process based on the text entered in the search bar
     * and the selected search type. The search results will be processed and
     * displayed asynchronously.
     */
    @Override
    public void searchContent() {
        String type_search = getSearchType();
        search(search_bar.getText(), type_search, true);
    }

    /**
     * Performs a search with the specified query and search type.
     * If `isAddHistory` is true, the search query and type are added to the search history.
     * This method performs the search asynchronously and displays the results.
     *
     * @param query The search query entered by the user.
     * @param type The type of search (e.g., "Category", "Title", "Author", "Publisher").
     * @param isAddHistory If true, adds the search query and type to the search history.
     */
    @Override
    public void search(String query, String type, boolean isAddHistory) {
        if(isAddHistory) historySearch.add(
                new AbstractMap.SimpleEntry<>(query, button_option_search.getText()));
        font_back.setVisible(true);
        vbox_result_search.getChildren().clear();
        scroll_result_search.setVisible(false);
        content_booklists.getChildren().clear();
        setLoading(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Book> listBook = new ArrayList<>();
                if(type == Book.AUTHOR) {
                    listBook = bookService.getBookByAuthor(query);
                } else if (type == Book.TITLE) {
                    listBook = bookService.getBookByTitle(query);
                } else if (type == Book.CATEGORY) {
                    listBook = bookService.getBookByCategory(query);
                } else if (type == Book.PUBLISHER) {
                    listBook = bookService.getBookByPublisher(query);
                }
                list_book_current = listBook;
                for(int i = 0; i < listBook.size(); i+=5) {
                    int index = i;
                    int size = listBook.size();
                    List<Book> listshortbook = listBook.subList(
                            index, Math.min(size, index + 5));
                    javafx.application.Platform.runLater(() -> {
                        content_booklists.getChildren().add(
                                AnchorPane.createAnchorPaneBook(
                                        listshortbook, "",
                                        5, false, BOOKLIST_UI, loading));
                    });
                    Thread.sleep(100);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            setLoading(false);
        });
        task.setOnFailed(event -> {
            setLoading(false);
        });

        new Thread(task).start();
    }

    /**
     * Performs the search operation and handles the search result display.
     * It processes the search based on the selected type and query.
     * For category searches, it displays distinct categories as clickable options.
     * For other types, it displays the search results with a maximum limit of suggestions.
     */
    @Override
    public void searchResult() {
        String type_search = getSearchType();
        List<Book> listBook = new ArrayList<>();

        if(type_search == Book.AUTHOR) {
            listBook = bookService.getBookByAuthor(search_bar.getText());
        } else if (type_search == Book.TITLE) {
            listBook = bookService.getBookByTitle(search_bar.getText());
        } else if (type_search == Book.PUBLISHER) {
            listBook = bookService.getBookByPublisher(search_bar.getText());
        } else {
            List<String> listcategory = new ArrayList<>();
            listcategory = bookService.getDistinctCategories(search_bar.getText());
            for (String category : listcategory) {
                TextField textField = new TextField();
                textField.setFont(Font.font(14));
                textField.setStyle("-fx-background-color: transparent; " +
                        "-fx-cursor: hand;-fx-text-fill:#333333");
                textField.setEditable(false);
                textField.setOnMouseEntered(e ->
                { textField.setStyle("-fx-background-color:#5f43b2 ;" +
                        "-fx-text-fill:white;-fx-cursor: hand;"); });
                textField.setOnMouseExited(e ->
                { textField.setStyle("-fx-background-color: transparent ;" +
                        "-fx-text-fill:#333333;-fx-cursor: hand;"); });
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
        for(Book book : listBook) {
            TextField textField = new TextField();
            textField.setFont(Font.font(14));
            switch (type_search) {
                case Book.AUTHOR: textField.setText(book.getAuthors()[0]); break;
                case Book.TITLE: textField.setText(book.getTitle()); break;
                case Book.PUBLISHER: textField.setText(book.getPublisher()); break;
            }
            textField.setStyle("-fx-background-color: transparent;" +
                    " -fx-cursor: hand;-fx-text-fill:#333333");
            textField.setEditable(false);
            textField.setOnMouseEntered(e -> {
                textField.setStyle("-fx-background-color:#5f43b2 ;" +
                        "-fx-text-fill:white;-fx-cursor: hand;"); });
            textField.setOnMouseExited(e -> {
                textField.setStyle("-fx-background-color: transparent ;" +
                        "-fx-text-fill:#333333;-fx-cursor: hand;"); });
            textField.setOnMouseClicked(e -> {
                search_bar.setText(textField.getText());
                searchContent();
            });
            vbox_result_search.getChildren().add(textField);
            scroll_result_search.setVisible(true);
            if(limit++ > MAX_SUGGESTION) break;
        }
    }

    /**
     * Renders the search results dynamically as the user types in the search bar.
     * It clears the previous results and fetches new suggestions based on the text entered.
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


    // Book List interface processing
    /**
     * Sets the visibility of the delete button in the search bar based on whether
     * the search bar is empty or not. If the search bar is empty, the delete
     * button will be hidden; otherwise, it will be visible.
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
     * Clears the search history.
     * This static method clears the entire search history stored in the `historySearch` list.
     */
    public static void setEmptyHistorySearch() {
        historySearch.clear();
    }

    /**
     * Initializes and sets up the right panel of the UI.
     * If the right panel is not already populated, it loads the panel by
     * utilizing the `FXMLLoader` to load a predefined UI file and set the
     * controller for the panel.
     */
    public void setRightPanel() {
        try {
            if (right_panel.getChildren().isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(RIGHTPANEL_URL));
                setController(this);
                javafx.scene.layout.AnchorPane rightPanel = loader.load();
                RightPanelController rightPanelController = loader.getController();
                rightPanelController.setCurrentUI(BOOKLIST_UI);
                right_panel.getChildren().add(rightPanel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the main content for the book list, categorized by book categories.
     * It clears any existing content and retrieves category data from the book
     * service. For each category, a background task is created to fetch and
     * display the books, which are displayed in paginated AnchorPane sections.
     */
    public void loadContent() {
        content_booklists.getChildren().clear();
        List<String> categories = new ArrayList<>();
        HashMap<String, Integer> dataCategory
                = (HashMap<String, Integer>) bookService.getDataCategory();
        for (HashMap.Entry<String, Integer> entry : dataCategory.entrySet()) {
            String category = entry.getKey();
            categories.add(category);
        }
        for (String category : categories) {
            Task<List<AnchorPane>> task = new Task<>() {
                @Override
                protected List<AnchorPane> call() throws Exception {
                    List<Book> listBook = bookService.getBookByCategory(category);
                    Thread.sleep(100);
                    List<AnchorPane> anchorpaneList = new ArrayList<>();
                    for (int k = 0; k < NUMBER_OF_COL_IN_CATEGORY; k++) {
                        if(NUMBER_OF_BOOK_IN_ROW * k >= listBook.size()) break;
                        anchorpaneList.add(createAnchorPaneBook(
                                listBook.subList(k * NUMBER_OF_BOOK_IN_ROW,
                                        Math.min((k + 1) * NUMBER_OF_BOOK_IN_ROW,
                                                listBook.size())),
                                k == 0 ? category : "",
                                NUMBER_OF_BOOK_IN_ROW, false, BOOKLIST_UI, loading));
                    }
                    return anchorpaneList;
                }
            };
            task.setOnSucceeded(e -> {
                List<AnchorPane> root = task.getValue();
                Platform.runLater(() -> {
                    for(AnchorPane anchorPane : root) {
                        setAnimationfadeTransition(anchorPane);
                        content_booklists.getChildren().add(anchorPane);
                    }
                });
            });
            new Thread(task).start();
        }
    }

    /**
     * Loads the details of a specific book when a user clicks on it.
     * The method navigates to a detailed view of the book and passes the current
     * UI context for proper navigation management.
     *
     * @param event The event that triggered the navigation to book details.
     * @param book The book object whose details need to be loaded.
     * @param currentUI The identifier of the current UI from where the navigation is happening.
     * @throws IOException If an error occurs while loading the book details page.
     */
    public void loadDetailsBook(Event event, Book book, String currentUI) throws IOException {
        try {
            toBookDetail(event, book, BOOK_DETAILS_URL, currentUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the QR code scanning functionality by configuring file chooser options
     * for selecting a QR image and adding event listeners for the upload and scan buttons.
     * The method sets up behavior for uploading a QR code image and opening a modal for scanning.
     */
    public void setUpQRScan() {
        qrChooser.setTitle("Select a QR image");
        qrChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.png", "*.jpg", "*.jpeg"));

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
     * Handles the selected QR code file, displaying the image and initiating the
     * QR code reading process.
     *
     * @param selectedFile The QR code image file selected by the user.
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
            protected String call() throws IOException,
                    NotFoundException, BookNotFoundException, InterruptedException {
                Thread.sleep(200);
                return QRCode.readQR(selectedFile.getAbsolutePath());
            }
        };

        task.setOnSucceeded(event -> handleQRSuccess(task.getValue(), originalText));
        task.setOnFailed(event -> handleQRFailure(originalText));

        new Thread(task).start();
    }

    /**
     * Handles the successful scanning of a QR code. Retrieves book information
     * based on the scanned QR content and updates the UI with the book details.
     *
     * @param qrContent The content retrieved from the QR code.
     * @param originalText The original text of the upload button before the scanning process.
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
     * Handles the failure of the QR code scanning process. Resets the upload button
     * and displays an error message.
     *
     * @param originalText The original text of the upload button before the scanning process.
     */
    private void handleQRFailure(String originalText) {
        button_upload_qr.setText(originalText);
        button_upload_qr.setDisable(false);
        showQRError("Invalid QR code");
    }

    /**
     * Displays an error message related to the QR scanning process.
     *
     * @param message The error message to be displayed.
     */
    private void showQRError(String message) {
        qr_error_message.setText(message);
        qr_error_message.setVisible(true);
        qr_book_info.setVisible(false);
    }

    /**
     * Updates the UI with the book information retrieved from a successful QR code scan.
     * Displays the book title, image, and enables a button for viewing more details about the book.
     *
     * @param book The book object containing the information to be displayed.
     */
    public void updateQRBookInfo(Book book) {
        qr_book_title.setText(book.getTitle());
        qr_book_image.setImage(new Image(new ByteArrayInputStream(book.getImage())));
        qr_book_info.setVisible(true);
        qr_error_message.setVisible(false);
        button_qr_book_details.setOnMouseClicked(e -> {
            setLoading(true);
            try {
                toBookDetail(e, book, BOOK_DETAILS_URL, BOOKLIST_UI);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

}
