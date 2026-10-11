package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.*;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.service.BorrowService;
import com.ddd.bookverse.service.ReviewService;
import com.ddd.bookverse.service.UserService;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ddd.bookverse.item.AnchorPane.createAnChorPaneLoading;
import static com.ddd.bookverse.item.AnchorPane.createAnchorPaneUserBorrow;
import static com.ddd.bookverse.utils.NavHelper.toBookManagement;

public class DetailsBookAdminController {

    private Book book ;
    private final BookService bookService = new BookService();
    private final UserService userService = new UserService();
    private final ReviewService reviewService = new ReviewService();
    private final BorrowService borrowService = new BorrowService();
    private HashMap<Long, Borrow> changedBorrows = new HashMap<>();
    private List<Borrow> addedBorrows = new ArrayList<>();
    private DeleteConfirmationController deleteConfirmationController ;
    private javafx.scene.layout.AnchorPane anchorPaneDelete;
    private boolean isDeleteBook;
    private boolean isDeleteComment;
    private Review reviewDelete;

    @FXML
    private Label title_book;

    @FXML
    private Text description_book;

    @FXML
    private Text isbn_book;
    @FXML
    private Text pages_book;
    @FXML
    private Text publisher_book;
    @FXML
    private Text publication_date;
    @FXML
    private Text id_book;
    @FXML
    private Text author1;
    @FXML
    private Text author2;
    @FXML
    private Text author3;
    @FXML
    private Text category1;
    @FXML
    private Text category2;
    @FXML
    private Text category3;
    @FXML
    private ImageView img_book;
    @FXML
    private Button buttonoverview;
    @FXML
    private Button buttoncomment;
    @FXML
    private Button buttonhistory;

    @FXML
    private AnchorPane anchorpane_overview;
    @FXML
    private AnchorPane anchorpane_comment;
    @FXML
    private AnchorPane anchorpane_history;

    @FXML
    private VBox vbox_comment;
    @FXML
    private VBox vbox_history;

    @FXML
    private AnchorPane rating_bar;
    @FXML
    private Button button_back;
    @FXML
    private Button button_delete;

    @FXML
    private Text num_quantity;
    @FXML
    private Text num_borrowed;
    @FXML
    private Text num_overdued;

    @FXML
    private Button button_manage_borrows;
    @FXML
    private AnchorPane anchorpane_manage_borrows;
    @FXML
    private TextField textfield_username;
    @FXML
    private Text alert_text;
    @FXML
    private FontIcon button_search_user_borrow;
    @FXML
    private FlowPane user_borrow_search_results;
    @FXML
    private VBox user_borrows_table;
    @FXML
    private Button button_close_manage_borrows;
    @FXML
    private Button button_update_manage_borrows;
    @FXML
    private VBox user_not_found_alert;
    @FXML
    private Text username_text;
    @FXML
    private AnchorPane user_borrow_result_header;
    @FXML
    private Text result_username_text;
    @FXML
    private Button button_reset_borrow_changes;
    @FXML
    private Button button_add_user_borrow;
    @FXML
    private VBox borrow_error;
    @FXML
    private Text borrow_error_msg;
    @FXML
    private ImageView no_result_placeholder;
    @FXML
    private javafx.scene.layout.AnchorPane  mainAnchorPane;
    @FXML
    private javafx.scene.layout.AnchorPane loading;
    @FXML
    private FontIcon button_delete_search;
    @FXML
    private MenuButton button_option_search;



    public void initialize() {
        buttonoverview.setOnAction(e -> {
            anchorpane_overview.setVisible(true);
            anchorpane_history.setVisible(false);
            anchorpane_comment.setVisible(false);
        });
        buttonhistory.setOnAction( e -> {
            anchorpane_overview.setVisible(false);
            anchorpane_history.setVisible(true);
            anchorpane_comment.setVisible(false);
        });
        buttoncomment.setOnAction( e -> {
            anchorpane_overview.setVisible(false);
            anchorpane_history.setVisible(false);
            anchorpane_comment.setVisible(true);
        });

        Button[] bookTabs = new Button[]{buttonoverview, buttoncomment, buttonhistory};
        for (Button tab : bookTabs) {
            tab.setOnMouseClicked(e -> {
                for (Button bookTab : bookTabs) {
                    bookTab.getStyleClass().remove("btn-primary");
                    if (!bookTab.getStyleClass().contains("book-tab-unactive")) {
                        bookTab.getStyleClass().add("book-tab-unactive");
                    }
                }
                if (!tab.getStyleClass().contains("btn-primary")) {
                    tab.getStyleClass().add("btn-primary");
                }
                tab.getStyleClass().remove("book-tab-unactive");
            });
        }

        // Manage borrows modal
        changedBorrows = new HashMap<>();
        addedBorrows = new ArrayList<>();

        anchorpane_overview.setVisible(true);
        anchorpane_comment.setVisible(false);
        anchorpane_history.setVisible(false);

        anchorpane_manage_borrows.setVisible(false);
        user_borrow_result_header.setVisible(false);
        user_borrow_search_results.setVisible(false);
        user_not_found_alert.setVisible(false);
        borrow_error.setVisible(false);
        button_close_manage_borrows.setOnMouseClicked(e -> anchorpane_manage_borrows.setVisible(false));
        button_manage_borrows.setOnMouseClicked(e -> anchorpane_manage_borrows.setVisible(true));

        button_search_user_borrow.setOnMouseClicked(e -> {
            String searchOption = button_option_search.getText();
            String query = textfield_username.getText();
            User user = null;

            switch (searchOption) {
                case "Username" -> user = userService.findUserByUsername(query);
                case "User ID" -> {
                    long userId;

                    try {
                        userId = Long.parseLong(query);
                    } catch (NumberFormatException typeError) {
                        userId = -1;
                    }

                    user = userService.findUserById(userId);
                }
                case "Email" -> user = userService.findUserByEmail(query);
            }

            if (!(user instanceof Member)) {
                user_borrow_result_header.setVisible(false);
                user_borrow_search_results.setVisible(false);
                user_not_found_alert.setVisible(true);

                switch (searchOption) {
                    case "Username" -> alert_text.setText("Cannot find member with username: ");
                    case "User ID" -> alert_text.setText("Cannot find member with user ID: ");
                    case "Email" -> alert_text.setText("Cannot find member with email: ");
                }

                username_text.setText(query);
                no_result_placeholder.setVisible(true);
                return;
            }

            loadUserBorrows(user);
        });

        button_reset_borrow_changes.setOnMouseClicked(e -> {
            loadUserBorrows(userService.findUserByUsername(textfield_username.getText()));
        });

        button_add_user_borrow.setOnMouseClicked(e -> {
            Borrow newBorrow = new Borrow();
            newBorrow.setBook(book);
            newBorrow.setUser(userService.findUserByUsername(textfield_username.getText()));
            newBorrow.setBorrowDate(LocalDate.from(LocalDateTime.now()));
            newBorrow.setReturnDate(LocalDate.from(LocalDateTime.now().plusDays(14)));
            newBorrow.setStatus("Borrowing");
            newBorrow.setPagesRead(0);
            user_borrows_table.getChildren().addFirst(createAnchorPaneUserBorrow(
                newBorrow,
                true,
                button_update_manage_borrows,
                changedBorrows,
                addedBorrows
            ));
        });

        button_update_manage_borrows.setOnMouseClicked(e -> {
            if (!validateUserBorrows(changedBorrows, addedBorrows)) {
                borrow_error.setVisible(true);
                borrow_error_msg.setText("Previous borrow must be returned before new borrow");
                return;
            }
            for (Borrow borrow : changedBorrows.values()) {
                if (borrow.getStatus().equals("Returned")) {
                    try {
                        borrowService.returnBook(borrow);
                    } catch (Exception exception) {
                        borrow_error.setVisible(true);
                        borrow_error_msg.setText(exception.getMessage());
                        return;
                    }
                } else if (borrow.getStatus().equals("Paid")) {
                    try {
                        borrowService.paidFine(borrow);
                    } catch (Exception exception) {
                        borrow_error.setVisible(true);
                        borrow_error_msg.setText(exception.getMessage());
                        return;
                    }
                }
            }
            for (Borrow borrow : addedBorrows) {
                if (borrow.getStatus().equals("Borrowing")) {
                    try {
                        borrowService.borrowBook(borrow);
                    } catch (Exception exception) {
                        borrow_error.setVisible(true);
                        borrow_error_msg.setText(exception.getMessage());
                        return;
                    }
                } else if (borrow.getStatus().equals("Returned")) {
                    try {
                        borrowService.returnBook(borrow);
                    } catch (Exception exception) {
                        borrow_error.setVisible(true);
                        borrow_error_msg.setText(exception.getMessage());
                        return;
                    }
                }
            }
            loadUserBorrows(userService.findUserByUsername(textfield_username.getText()));
            updateBookStats();
        });

        for (MenuItem item : button_option_search.getItems()) {
            item.setOnAction(event -> button_option_search.setText(item.getText()));
        }

        button_back.setOnMouseClicked(e -> {
            setLoading(true);
            try {
                backHome(e);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });

        button_delete.setOnAction(e -> {
            deleteConfirmationController.render("Are you sure to delete this book?");
            isDeleteBook = true;
            isDeleteComment = false;
            if(!mainAnchorPane.getChildren().contains(anchorPaneDelete)) mainAnchorPane.getChildren().add(anchorPaneDelete);
        });
        setUp();
        setDeleteConfirmationController();
        deleteConfirmationController.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                deleteConfirmationBook();
                deleteConfirmationComment();
            }
        });
        setLoading(false);
        loading.getChildren().add(createAnChorPaneLoading(0,0));
        button_delete_search.setOnMouseClicked(e->{textfield_username.setText("");});
        setButtonDeleteText();
    }

    /**
     * Adds a listener to the username text field to control the visibility of the delete button.
     * If the text field is empty, the delete button is hidden; otherwise, it is displayed.
     */
    public void setButtonDeleteText() {
        textfield_username.textProperty().addListener((observable, oldValue, newValue) -> {
            if (textfield_username.getText().equals("")) {
                button_delete_search.setVisible(false);
            } else {
                button_delete_search.setVisible(true);
            }
        });
    }

    /**
     * Sets the visibility of the loading indicator.
     *
     * @param isVisible a boolean indicating whether the loading indicator should be visible.
     *                  {@code true} to show the loading indicator, {@code false} to hide it.
     */
    public void setLoading(boolean isVisible) {
        loading.setVisible(isVisible);
    }

    /**
     * Resets and exits the delete confirmation dialog.
     * This method ensures the deletion flags are reset to {@code false},
     * the delete confirmation controller
     * is set to inactive, and the confirmation dialog is closed.
     */
    public void ExitDeleteConfirmation() {
        isDeleteBook = false;
        isDeleteComment = false;
        deleteConfirmationController.setStatus(false);
        deleteConfirmationController.exit();
    }


    /**
     * Initializes and sets the delete confirmation controller
     * by loading the associated FXML file.
     * This method loads the FXML for the delete confirmation dialog
     * and retrieves its controller instance.
     * If an error occurs during loading, the stack trace is printed.
     */
    public void setDeleteConfirmationController() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/deleteConfirmation.fxml"));
            Parent root = loader.load();
            anchorPaneDelete = (javafx.scene.layout.AnchorPane) root;
            deleteConfirmationController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the delete confirmation process for a book.
     * If the delete confirmation dialog status is active
     * and the deletion flag is set, this method deletes
     * the book via the book service, loads the book management interface, and updates the scene.
     * After the process, it resets the confirmation dialog state.
     */
    public void deleteConfirmationBook() {
        if (deleteConfirmationController.getStatus() && isDeleteBook) {
            bookService.deleteBook(book.getId());
            try {
                FXMLLoader loaderDetail = new FXMLLoader(
                        getClass().getResource("/fxml/bookmanagement.fxml"));
                Parent root = loaderDetail.load();
                BookManagementController bookManagementController = loaderDetail.getController();
                Stage stage = (Stage) button_back.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
            ExitDeleteConfirmation();
        }
    }

    /**
     * Validates the borrow records of a user.
     * This method checks whether the added borrow records are valid by ensuring that:
     * - The return date of one borrow record does not overlap with the borrow date of the next.
     * - No record has a status of "Borrowing."
     *
     * @param changedBorrows a {@code HashMap} of modified borrow records identified by their IDs.
     * @param addedBorrows a {@code List} of new borrow records to be validated.
     * @return {@code true} if all borrow records are valid, {@code false} otherwise.
     */
    public boolean validateUserBorrows(HashMap<Long, Borrow> changedBorrows,
                                       List<Borrow> addedBorrows) {
        for (int i = 0; i < addedBorrows.size() - 1; i++) {
            if (addedBorrows.get(i).getReturnDate()
                    .isAfter(addedBorrows.get(i + 1).getBorrowDate())) {
                return false;
            }
            if (addedBorrows.get(i).getStatus().equals("Borrowing")) {
                return false;
            }
        }
        return true;
    }


    /**
     * Loads the borrow records of a user for a specific book.
     * This method initializes the list of modified and added borrow records,
     * resets UI placeholders and alerts,
     * and displays the borrow details of the user for the given book in the borrow table.
     *
     * @param user the {@code User} whose borrow records are to be loaded.
     */
    public void loadUserBorrows(User user) {
        changedBorrows = new HashMap<>();
        addedBorrows = new ArrayList<>();

        no_result_placeholder.setVisible(false);
        user_not_found_alert.setVisible(false);
        borrow_error.setVisible(false);
        user_borrow_result_header.setVisible(true);
        user_borrow_search_results.setVisible(true);
        result_username_text.setText(user.getUsername());
        user_borrows_table.getChildren().clear();

        button_update_manage_borrows.setText("Apply changes (0)");
        button_update_manage_borrows.setDisable(true);

        List<Borrow> borrows = borrowService
                .getAllBorrowsOfABookFromUser(book.getId(), user.getId());
        for (Borrow borrow : borrows) {
            user_borrows_table.getChildren().add(createAnchorPaneUserBorrow(
                    borrow,
                    false,
                    button_update_manage_borrows,
                    changedBorrows,
                    addedBorrows
            ));
        }
    }

    /**
     * Navigates back to the book management interface.
     * This method attempts to return to the main book management view.
     * If an error occurs during navigation, the stack trace is printed.
     *
     * @param event the event that triggers the navigation action.
     * @throws IOException if there is an issue loading the book management interface.
     */
    public void backHome(Event event) throws IOException {
        try {
            toBookManagement(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the current book in the controller.
     * This method assigns the given {@code Book} to the controller and calls {@code setUp()}
     * to initialize the related UI components and state based on the selected book.
     *
     * @param book the {@code Book} to be set as the current book.
     */
    public void setBook(Book book) {
        this.book = book;
        setUp();
    }


    /**
     * Initializes and sets up the UI and data for the selected book.
     * This method updates book details such as title, description, ISBN,
     * page count, publisher, publication date, authors, and categories.
     * It also loads the book's image, comments, borrowing history, and statistics.
     */
    public void setUp() {
        if (book == null) {
            return;
        }
        borrowService.updateOverdueBorrowsByBookId(book.getId());
        title_book.setText(book.getTitle());
        description_book.setText(book.getDescription());
        isbn_book.setText(book.getIsbn());
        pages_book.setText(book.getPageCount().toString());
        publisher_book.setText(book.getPublisher());
        publication_date.setText(book.getPublishDate());
        id_book.setText(book.getId());
        setAuthorsBook(book.getAuthors());
        setCategoriesBook(book.getCategories());
        img_book.setImage(new Image(new ByteArrayInputStream(book.getImage())));
        setComment();
        setHistory();
        updateBookStats();
    }

    /**
     * Updates the statistical information for the selected book.
     * This includes:
     * - The quantity of the book currently in stock.
     * - The number of copies currently being borrowed.
     * - The number of overdue borrowings.
     */
    public void updateBookStats() {
        num_quantity.setText(bookService.getBook(book.getId()).getQuantityInStock() + "");
        num_borrowed.setText(borrowService
                .getNumBorrowsOfBookWithStatus(book.getId(), "Borrowing") + "");
        num_overdued.setText(borrowService
                .getNumBorrowsOfBookWithStatus(book.getId(), "Overdue") + "");
    }

    /**
     * Loads and displays all comments (reviews) for the selected book.
     * This method retrieves the reviews from the database, creates UI components to display them,
     * and adds a delete icon for administrators to remove comments.
     * It also displays the average rating for the book using a rating bar.
     */
    public void setComment() {
        vbox_comment.getChildren().clear();
        vbox_comment.setPadding(new Insets(10));
        vbox_comment.setSpacing(10);
        List<Review> listReview = reviewService.getReviewsByBookId(book.getId());
        Image image = new Image(getClass().getResourceAsStream("/img/admin.png"));
        for (Review review : listReview) {
            User user = userService.findUserById(review.getUserId());
            com.ddd.bookverse.item.AnchorPane reviewPane = com.ddd.bookverse.item.AnchorPane.createAnchorPaneReview(review, image, user.getUsername());

            FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TIMES);
            deleteIcon.setIconSize(16);
            deleteIcon.setIconColor(Color.web("#666666"));
            deleteIcon.setLayoutX(reviewPane.getPrefWidth() - 45);
            deleteIcon.setLayoutY(25);
            deleteIcon.getStyleClass().add("btn");

            deleteIcon.setOnMouseEntered(event -> deleteIcon.setIconColor(Color.web("#5f43b2")));
            deleteIcon.setOnMouseExited(event -> deleteIcon.setIconColor(Color.web("#666666")));
            deleteIcon.setOnMouseClicked(event -> {
                reviewDelete = review;
                handleDeleteComment();
            });

            reviewPane.getChildren().add(deleteIcon);
            vbox_comment.getChildren().add(reviewPane);
        }
        rating_bar.getChildren().add(com.ddd.bookverse.item.AnchorPane.createAnchorPaneRating((int) book.getAverageRating(), null, false, 20));
    }

    /**
     * Handles the deletion of a comment by displaying a confirmation dialog.
     * This method sets the appropriate flags for deleting a comment,
     * prompts the user with a confirmation message, and ensures that the
     * delete confirmation dialog is shown on the UI.
     */
    public void handleDeleteComment() {
        isDeleteComment = true;
        isDeleteBook = false;
        deleteConfirmationController.render("Are you sure to delete this comment?");
        if (!mainAnchorPane.getChildren().contains(anchorPaneDelete))
            mainAnchorPane.getChildren().add(anchorPaneDelete);
    }

    /**
     * Confirms and deletes the comment if the user agrees.
     * This method removes the rating associated with the comment,
     * deletes the review from the review service,
     * updates the book in the book service, and then refreshes the comments section.
     * After deletion, it exits the confirmation dialog.
     */
    public void deleteConfirmationComment() {
        if (deleteConfirmationController.getStatus() && isDeleteComment) {
            if (book != null && reviewDelete != null) {
                book.removeRating(reviewDelete.getRating());
                reviewService.deleteReview(reviewDelete.getId());
                bookService.updateBook(book);
                setComment();
            }
            ExitDeleteConfirmation();  // Exits the confirmation dialog
        }
    }

    /**
     * Sets the authors for the book in the UI.
     * Based on the number of authors, it populates the appropriate author fields
     * and hides any unused author fields.
     *
     * @param listAuthors An array of authors to be displayed for the book.
     */
    public void setAuthorsBook(String[] listAuthors) {
        if (listAuthors.length == 1) {
            author1.setText(listAuthors[0]);
            author2.getParent().setVisible(false);
            author3.getParent().setVisible(false);
        } else if (listAuthors.length == 2) {
            author1.setText(listAuthors[0]);
            author2.setText(listAuthors[1]);
            author3.getParent().setVisible(false);
        } else if (listAuthors.length == 3) {
            author1.setText(listAuthors[0]);
            author2.setText(listAuthors[1]);
            author3.setText(listAuthors[2]);
        }
    }


    /**
     * Sets the categories for the book in the UI.
     * Based on the number of categories, it populates the appropriate category fields
     * and hides any unused category fields.
     *
     * @param listCategories An array of categories to be displayed for the book.
     */
    public void setCategoriesBook(String[] listCategories) {
        if (listCategories.length == 1) {
            category1.setText(listCategories[0]);
            category2.getParent().setVisible(false);
            category3.getParent().setVisible(false);
        } else if (listCategories.length == 2) {
            category1.setText(listCategories[0]);
            category2.setText(listCategories[1]);
            category3.getParent().setVisible(false);
        } else if (listCategories.length == 3) {
            category1.setText(listCategories[0]);
            category2.setText(listCategories[1]);
            category3.setText(listCategories[2]);
        }
    }

    /**
     * Sets the borrowing history for the book in the UI.
     * This method populates the history section
     * by displaying all borrow records associated with the book.
     * Each borrow record is shown with alternating row colors for better readability.
     *
     * @see Borrow
     */
    public void setHistory() {
        vbox_history.getChildren().clear();
        vbox_history.setSpacing(0);
        List<Borrow> listBorrow = borrowService.getBorrowByBookId(book.getId());
        int index = 0;
        for (Borrow borrow : listBorrow) {
            if (borrow == null || borrow.getUser() == null) continue;

            AnchorPane borrowPane = com.ddd.bookverse.item.AnchorPane.createAnchorPaneBorrow(borrow);

            // Alternates background color for each row in the history
            if (index % 2 == 0) {
                borrowPane.setStyle("-fx-background-color: #e5e5e5; " +
                        "-fx-border-color: #d1d1d1; -fx-border-width: 0 0 1px 0;");
            } else {
                borrowPane.setStyle("-fx-background-color: #ffffff;" +
                        " -fx-border-color: #d3d3d3; -fx-border-width: 0 0 1px 0;");
            }

            vbox_history.getChildren().add(borrowPane);
            index++;
        }
    }

    /**
     * Sets the rating bar for the book in the UI.
     * This method adds a rating bar based on the provided rating value.
     *
     * @param Rating The rating value to be displayed in the rating bar.
     */
    public void setRatingBar(int Rating) {
        rating_bar.getChildren().add(com.ddd.bookverse.item.AnchorPane
                .createAnchorPaneRating(Rating, null, true, 20));
    }




}
