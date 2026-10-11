package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.singleton.RandomBookSession;
import com.ddd.bookverse.utils.NavHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static com.ddd.bookverse.item.AnchorPane.createAnchorPaneRating;


/**
 * Controller for the right panel of the application, which includes:
 * - A list of popular books based on borrow frequency.
 * - A randomly selected book with additional details.
 */
public class RightPanelController {

    @FXML
    private TableView<Pair<Book, Integer>> popular_books;
    @FXML
    private TableColumn<Pair<Book, Integer>, HBox> book_col; // Change to HBox for horizontal layout
    @FXML
    private TableColumn<Pair<Book, Integer>, Integer> borrow_col;
    @FXML
    private ImageView random_image;
    @FXML
    private Text random_title;
    @FXML
    private Text random_authors;
    @FXML
    private Button random_read;
    @FXML
    private javafx.scene.layout.AnchorPane rating_bar_random;

    private String currentUI = null;

    private static final RandomBookSession randomBookSession = RandomBookSession.getInstance();
    private static final BookService bookService = new BookService();
    private static final  BookListController bookListController = new BookListController();
    private static Object controller;

    private ObservableList<Pair<Book, Integer>> bookList = FXCollections.observableArrayList();

    /**
     * Initializes the right panel with default data and sets up the table columns.
     */
    @FXML
    public void initialize() {
        popular_books.setStyle("-fx-table-cell-border-color: transparent;");
        book_col.setCellValueFactory(pair -> {
            Book book = pair.getValue().getKey();
            HBox hbox = new HBox(10);

            ImageView imageView = new ImageView();
            byte[] imageBytes = book.getImage();
            if (imageBytes != null && imageBytes.length > 0) {
                InputStream inputStream = new ByteArrayInputStream(imageBytes);
                Image image = new Image(inputStream);
                imageView.setImage(image);
            }
            imageView.setFitWidth(50);
            imageView.setFitHeight(75);
            imageView.setPreserveRatio(true);

            VBox vbox = new VBox(5);
            vbox.setStyle("-fx-alignment: center-left;");

            Text titleText = new Text(truncateText(book.getTitle(), 12));
            titleText.getStyleClass().add("book-title");

            Text authorText = new Text(truncateText(book.getAuthors()[0], 12));
            authorText.getStyleClass().add("book-author");

            vbox.getChildren().addAll(titleText, authorText);

            hbox.getChildren().addAll(imageView, vbox);

            return new javafx.beans.property.SimpleObjectProperty<>(hbox);
        });

        borrow_col.setCellValueFactory(pair -> new javafx.beans.property.SimpleObjectProperty<>(pair.getValue().getValue()));
        borrow_col.setCellFactory(column -> new TableCell<Pair<Book, Integer>, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(item.toString());
                    setAlignment(Pos.CENTER);
                    text.setStyle("-fx-text-alignment: center;-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #798188;");
                    setGraphic(text);
                }
            }
        });
        loadTopBorrowedBooks(5);
        popular_books.setItems(bookList);
        displayRandomBook();
        popular_books.setRowFactory(tv -> {
            TableRow<Pair<Book, Integer>> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                renderLoading();
                try {
                    if (!row.isEmpty()) {
                        Pair<Book, Integer> rowData = row.getItem();
                        Book book = rowData.getKey();
                        bookListController.loadDetailsBook(event, book, currentUI);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            return row;
        });

    }

    /**
     * Truncates text to the specified length, appending "..." if necessary.
     *
     * @param text      the text to truncate.
     * @param maxLength the maximum allowed length.
     * @return the truncated text.
     */
    private String truncateText(String text, int maxLength) {
        if (text != null && text.length() > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }


    /**
     * Sets the controller for rendering loading state based on the current UI.
     *
     * @param other_controller the active controller instance.
     */
    public static void setController(Object other_controller) {
        if(other_controller instanceof BookListController) {
            controller = (BookListController) other_controller;
        } else if(other_controller instanceof  DashboardController) {
            controller = (DashboardController) other_controller;
        } else if(other_controller instanceof  MyBookController) {
            controller = (MyBookController) other_controller;
        }
    }

    /**
     * Triggers a loading animation in the associated controller.
     */
    public static void renderLoading() {
        if(controller instanceof BookListController) {
            ((BookListController) controller).setLoading(true);
        } else if(controller instanceof  DashboardController) {
            ((DashboardController) controller).setLoading(true);
        } else if(controller instanceof  MyBookController) {
            ((MyBookController) controller).setLoading(true);
        }
    }

    // Method to load the top N borrowed books
    /**
     * Loads the top N borrowed books into the table.
     *
     * @param n the number of top borrowed books to display.
     */
    private void loadTopBorrowedBooks(int n) {
        List<Pair<Book, Integer>> topBooks = bookService.getTopBorrowedBooks(n);
        bookList.clear();  // Clear any existing data
        bookList.addAll(topBooks);  // Add new top borrowed books to the list
    }

    /**
     * Displays a randomly selected book in the right panel.
     */
    public void displayRandomBook() {
        Book randomBook = randomBookSession.getRandomBook();
        if (randomBook != null) {
            int maxLength = 15;
            String title = randomBook.getTitle();
            if (title.length() > maxLength) {
                title = title.substring(0, maxLength) + "...";
            }
            random_title.setText(title);

            String[] authors = randomBook.getAuthors();
            String author = authors[0];
            if (author.length() > maxLength) {
                author = author.substring(0, maxLength) + "...";
            }
            random_authors.setText(author);
            rating_bar_random.getChildren().add(createAnchorPaneRating((int) randomBook.getAverageRating(),null, false,16));

            byte[] imageBytes = randomBook.getImage();
            if (imageBytes != null && imageBytes.length > 0) {
                try {
                    InputStream inputStream = new ByteArrayInputStream(imageBytes);
                    Image image = new Image(inputStream);
                    random_image.setImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                random_image.setImage(null);
            }
        }
        random_read.setOnMouseClicked(event -> {
            renderLoading();
            try {
                bookListController.loadDetailsBook(event, randomBook, currentUI);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Gets the current UI context.
     *
     * @return the current UI context as a string.
     */
    public String getCurrentUI() {
        return currentUI;
    }


    /**
     * Sets the current UI context.
     *
     * @param currentUI the current UI context as a string.
     */
    public void setCurrentUI(String currentUI) {
        this.currentUI = currentUI;
    }
}
