package com.ddd.bookverse.controller;

import com.ddd.bookverse.model.*;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.item.AnchorPane;
import com.ddd.bookverse.service.BorrowService;
import com.ddd.bookverse.service.ReviewService;
import com.ddd.bookverse.service.UserService;
import com.ddd.bookverse.singleton.UserSession;
import com.ddd.bookverse.utils.QRCode;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.util.Duration;
import javafx.util.Pair;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import static com.ddd.bookverse.item.AnchorPane.BOOKLIST_UI;
import static com.ddd.bookverse.item.AnchorPane.createAnChorPaneLoading;
import static com.ddd.bookverse.service.BookService.stackBook;
import static com.ddd.bookverse.model.Borrow.*;
import static com.ddd.bookverse.utils.NavHelper.*;

/**
 * The `BookDetailController` class is responsible for
 * handling the details and interactions of a specific book
 * in the application. It extends the `BookListController`
 * to inherit general book list management functionality
 * and implements the `UiController` and `DeleteController`
 * interfaces to handle UI interactions and book deletion
 * operations.
 *
 * <p>This class is designed to manage the detailed view of a
 * selected book, allowing users to view detailed
 * information, perform actions such as deleting the book, and interact
 * with the user interface related to
 * book details.</p>
 *
 * Inherits from:
 * - {@link BookListController} for general book list management functionality.
 *
 * Implements:
 * - {@link UiController} for UI management and interactions specific to the book detail view.
 * - {@link DeleteController} for handling the deletion of the book.
 *
 * @see BookListController
 * @see UiController
 * @see DeleteController
 */
public class BookDetailController extends BookListController
        implements UiController, DeleteController  {
    private final ReviewService reviewService = new ReviewService();
    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();
    private final BorrowService borrowService = new BorrowService();
    private final UserSession userSession = UserSession.getInstance();
    private DeleteConfirmationController deleteConfirmationController ;
    private javafx.scene.layout.AnchorPane anchorPaneDelete;
    Image captchaImage = null;
    String captchaText = null;
    private Borrow latestBorrow;
    private int rating = 0;
    private Review currentReview = null;
    private String prevUI = null;


    private Book book;
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
    private javafx.scene.layout.AnchorPane listbookrelative;
    @FXML
    private ImageView img_book;

    @FXML
    private Button buttonoverview;
    @FXML
    private Button  buttoncomment;
    @FXML
    private javafx.scene.layout.AnchorPane anchorpane_comment;
    @FXML
    private javafx.scene.layout.AnchorPane anchorpane_overview;

    @FXML
    private VBox vbox_comment;

    @FXML
    private Button button_plantoborrow;
    @FXML
    private Button button_borrow;
    @FXML
    private Button button_overdue;

    @FXML
    private javafx.scene.layout.AnchorPane borrow_modal;
    @FXML
    private javafx.scene.control.DatePicker return_date_picker;
    @FXML
    private javafx.scene.control.Button cancel_borrow_modal;
    @FXML
    private javafx.scene.control.Button accept_borrow_modal;
    @FXML
    private Text borrow_message;

    @FXML
    private javafx.scene.control.Button button_share;
    @FXML
    private javafx.scene.layout.AnchorPane qr_code_tooltip;
    @FXML
    private javafx.scene.image.ImageView qr_code_img;

    @FXML
    private javafx.scene.control.TextField captcha_confirm;
    @FXML
    private javafx.scene.image.ImageView captcha_image;
    @FXML
    private javafx.scene.control.Button button_reload_captcha;

    @FXML
    private javafx.scene.control.Button button_return;
    @FXML
    private javafx.scene.layout.AnchorPane reading_progress;
    @FXML
    private javafx.scene.control.TextField pages_read_input;
    @FXML
    private javafx.scene.text.Text pages_read;
    @FXML
    private javafx.scene.text.Text total_pages;
    @FXML
    private javafx.scene.control.Button button_edit_progress;
    @FXML
    private javafx.scene.control.Button button_accept_progress;
    @FXML
    private javafx.scene.control.Button button_cancel_progress;

    @FXML
    private javafx.scene.layout.AnchorPane rating_comment_bar;

    @FXML
    private javafx.scene.layout.AnchorPane return_modal;
    @FXML
    private javafx.scene.control.Button cancel_return_modal;
    @FXML
    private javafx.scene.control.Button accept_return_modal;
    @FXML
    private javafx.scene.text.TextFlow return_modal_message;

    @FXML
    private javafx.scene.layout.AnchorPane overdue_modal;
    @FXML
    private javafx.scene.control.Button close_overdue_modal;
    @FXML
    private javafx.scene.text.TextFlow overdue_modal_message;


    @FXML
    private javafx.scene.text.TextFlow quantity_label;
    @FXML
    private javafx.scene.text.Text quantityText;

    @FXML
    private Button button_back;

    @FXML
    private TextArea comment_box;

    @FXML
    private FontIcon post_comment;

    @FXML
    private Text message_comment_bar;

    @FXML
    private FontIcon edit_comment;

    @FXML
    private FontIcon cancel_edit;

    @FXML
    private FontIcon delete_comment;

    @FXML
    private javafx.scene.layout.AnchorPane rating_bar;

    @FXML
    private javafx.scene.layout.AnchorPane loading;

    @FXML
    private javafx.scene.layout.AnchorPane comment_frame;

    @FXML
    private Line line_comment;
    @FXML
    private javafx.scene.layout.AnchorPane  mainAnchorPane;

    public static final int MAX_BORROWS = 8;
    private FadeTransition fadeOut;


    public void initialize() {
        initUI();
        clickButton();
    }

    /**
     * Initializes the user interface by setting up the UI components and ensuring
     * the loading state is hidden initially.
     * This method is called to set up the UI when the view is first loaded.
     */
    @Override
    public void initUI() {
        setUp();  // Calls the setup method to initialize the UI components.
        setLoading(false);  // Ensures that the loading indicator is hidden initially.
        // Adds a loading anchor pane to the loading container.
        loading.getChildren().add(createAnChorPaneLoading(0, 0));
    }

    /**
     * Sets up the event handlers for various buttons and actions within the UI.
     * - Handles comment box clicks by invoking the handleCommentEffect method.
     * - Sets the action for the back button by invoking handleBackButton.
     * - Configures tab switching behavior for "overview" and "comment" tabs,
     *   ensuring that the correct tab is highlighted and rendered.
     * - Initializes a delete confirmation listener to handle user deletion requests.
     */
    @Override
    public void clickButton() {
        // Set up comment box click event
        comment_box.setOnMouseClicked(event -> handleCommentEffect());

        // Set up back button click event
        button_back.setOnMouseClicked(this::handleBackButton);

        // Handle switching between tabs: Overview and Comment
        Button[] bookTabs = new Button[]{buttonoverview, buttoncomment};
        for (Button tab : bookTabs) {
            tab.setOnMouseClicked(e -> {
                if (tab.equals(buttonoverview)) renderOverview();  // Render Overview tab
                else renderComment();  // Render Comment tab

                // Remove "btn-primary" class and add "book-tab-unactive" class to all tabs
                for (Button bookTab : bookTabs) {
                    bookTab.getStyleClass().remove("btn-primary");
                    if (!bookTab.getStyleClass().contains("book-tab-unactive")) {
                        bookTab.getStyleClass().add("book-tab-unactive");
                    }
                }

                // Add "btn-primary" class to the clicked tab to highlight it
                if (!tab.getStyleClass().contains("btn-primary")) {
                    tab.getStyleClass().add("btn-primary");
                }
                tab.getStyleClass().remove("book-tab-unactive");
            });
        }

        // Set up delete confirmation listener
        setDeleteConfirmationController();
        deleteConfirmationController.statusProperty().addListener(
                (observable, oldValue, newValue) -> {
            if (newValue) {
                deleteConfirmation();  // Handle deletion if confirmed
            }
        });
    }

    /**
     * Sets the visibility of the loading indicator.
     *
     * @param isVisible A boolean indicating whether the loading
     *                  indicator should be visible.
     *                  If true, the loading indicator is shown;
     *                  otherwise, it is hidden.
     */
    @Override
    public void setLoading(boolean isVisible) {
        loading.setVisible(isVisible);  // Sets the visibility of the loading component.
    }

    /**
     * Exits the delete confirmation process by setting the confirmation
     * status to false
     * and invoking the `exit` method of the `deleteConfirmationController`.
     * This method is called when the user decides to cancel or exit the delete
     * confirmation dialog.
     */
    @Override
    public void ExitDeleteConfirmation() {
        deleteConfirmationController.setStatus(false);  // Set confirmation status to
        // false (canceled)
        deleteConfirmationController.exit();  // Exit the delete confirmation dialog
    }

    /**
     * Initializes the delete confirmation controller by loading the corresponding FXML
     * file
     * and retrieving the controller instance.
     * This method is called to set up the delete confirmation dialog in the UI.
     */
    @Override
    public void setDeleteConfirmationController() {
        try {
            // Load the FXML for the delete confirmation dialog
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource("/fxml/deleteConfirmation.fxml"));
            Parent root = loader.load();

            // Set the anchor pane for the delete confirmation dialog
            anchorPaneDelete = (javafx.scene.layout.AnchorPane) root;

            // Retrieve the delete confirmation controller
            deleteConfirmationController = loader.getController();
        } catch (IOException e) {
            // Print the stack trace if an error occurs while loading the FXML
            e.printStackTrace();
        }
    }

    /**
     * Performs the deletion of the current review and rating if the delete confirmation
     * dialog has been confirmed by the user.
     * - Removes the rating from the book.
     * - Deletes the current review from the review service.
     * - Updates the book details.
     * - Resets the current review and rating to their initial states.
     * - Calls the `ExitDeleteConfirmation` method to close the confirmation dialog.
     */
    @Override
    public void deleteConfirmation() {
        if (deleteConfirmationController.getStatus()) {// If the user has confirmed the deletion
            // Remove the current review's rating from the book
            book.removeRating(currentReview.getRating());

            // Delete the review from the review service
            reviewService.deleteReview(currentReview.getId());

            // Reset the rating and current review
            rating = 0;
            currentReview = null;

            // Update the book with the new rating information
            bookService.updateBook(book);

            // Remove any comment effect and set the new comment state
            removeCommentEffect();
            setComment();

            // Exit the delete confirmation dialog
            ExitDeleteConfirmation();
        }
    }

    /**
     * Sets up the hover effects and click event handlers for the comment buttons in the UI.
     * - Adds hover effects to the post, edit, delete, and cancel buttons.
     * - Associates each button with its corresponding click event handler.
     * This method is called to initialize the comment-related buttons, providing the user
     * with interactive functionality for posting, editing, deleting, and canceling comments.
     */
    private void setCommentButton() {
        // Set hover effects for each comment-related button
        setupHoverEffect(post_comment, "#666666", "#5f43b2");
        setupHoverEffect(edit_comment, "#666666", "#5f43b2");
        setupHoverEffect(delete_comment, "#666666", "#5f43b2");
        setupHoverEffect(cancel_edit, "#666666", "#5f43b2");

        // Set click event handlers for each button
        post_comment.setOnMouseClicked(event -> handlePostComment());
        edit_comment.setOnMouseClicked(event -> handleEditComment());
        delete_comment.setOnMouseClicked(event -> handleDeleteComment());
        cancel_edit.setOnMouseClicked(event -> handleCancelEdit());
    }

    /**
     * Sets up hover effects for a given FontIcon.
     * This method changes the icon color when the mouse enters and exits the icon area.
     * The icon's color is set to a specified default color and changes to
     * a hover color when the mouse hovers over it.
     *
     * @param icon The FontIcon to which the hover effect will be applied.
     * @param defaultColor The default color of the icon.
     * @param hoverColor The color of the icon when the mouse hovers over it.
     */
    private void setupHoverEffect(FontIcon icon, String defaultColor, String hoverColor) {
        icon.setIconColor(Color.web(defaultColor));
        icon.setOnMouseEntered(event -> icon.setIconColor(Color.web(hoverColor)));
        icon.setOnMouseExited(event -> icon.setIconColor(Color.web(defaultColor)));
    }

    /**
     * Handles the effect of selecting the comment frame.
     * This method checks if the comment frame is already selected, and if not, it adds
     * the "selected" style class
     * to the comment frame and its corresponding line.
     */
    private void handleCommentEffect() {
        boolean isSelected = comment_frame.getStyleClass().contains("selected");

        if (!isSelected) {
            comment_frame.getStyleClass().add("selected");
            line_comment.getStyleClass().add("selected");
        }
    }

    /**
     * Removes the "selected" effect from the comment frame.
     * This method checks if the comment frame is selected and, if so,
     * removes the "selected" style class
     * from both the comment frame and its corresponding line.
     */
    private void removeCommentEffect() {
        boolean isSelected = comment_frame.getStyleClass().contains("selected");

        if (isSelected) {
            comment_frame.getStyleClass().remove("selected");
            line_comment.getStyleClass().remove("selected");
        }
    }

    /**
     * Renders the comment section by making the comment panel visible and
     * the overview panel hidden.
     * This method is used to switch to the comment view, hiding the overview
     * and showing the comment section.
     */
    public void renderComment() {
        anchorpane_overview.setVisible(false);
        anchorpane_comment.setVisible(true);
        removeCommentEffect();
    }

    /**
     * Renders the overview section by making the overview panel visible and
     * the comment panel hidden.
     * This method is used to switch to the overview view, hiding the comment
     * section and showing the overview panel.
     */
    public void renderOverview() {
        anchorpane_comment.setVisible(false);
        anchorpane_overview.setVisible(true);
    }


    /**
     * Sets the current book and adds it to the stack of books.
     * This method assigns the given book to the current `book` variable and
     * adds it to the `stackBook`.
     * It then calls the `setUp()` method to initialize the UI or other
     * components as needed.
     *
     * @param book The book to set as the current book.
     */
    public void setBook(Book book) {
        this.book = book;
        stackBook.add(book);
        setUp();
    }

    /**
     * Clears the stack of books.
     * This static method clears the `stackBook`, effectively removing all books
     * from the stack.
     */
    public static void setEmptyStackBook() {
        stackBook.clear();
    }

    /**
     * Handles the back button click event.
     * This method pops the top book from the `stackBook` if it's not empty.
     * If the stack is not empty after the pop, it sets
     * the current book to the book on top of the stack and calls the `setUp()`
     * method to update the UI.
     * If the stack is empty, it shows the loading screen and navigates to the
     * previous UI based on the `prevUI` value.
     *
     * @param event The MouseEvent triggered by clicking the back button.
     * @throws RuntimeException If an unexpected error occurs during navigation.
     */
    private void handleBackButton(MouseEvent event) {
        if(!stackBook.empty()) stackBook.pop();
        if(stackBook.size() > 0) {
            this.book = stackBook.peek();
            setUp();
        } else {
            setLoading(true);
            try {
                switch (prevUI) {
                    case AnchorPane.BOOKLIST_UI:
                        toBookList(event);
                        break;
                    case AnchorPane.DASHBOARD_UI:
                        toDashboard(event);
                        break;
                    case BORROWING:
                    case PLAN:
                    case RETURNED:
                        formMyBookToBookList(prevUI, event);
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected value: " + prevUI);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Initializes the UI and updates the state of the book.
     *
     * This method sets up the book's details in the UI components,
     * such as the title, description,
     * ISBN, publisher, publication date, authors, categories, and image.
     * It also sets up the comment section
     * and the book quantity information. Additionally, it updates the borrow state,
     * handles modals for borrowing,
     * returning, and overdue status, and sets up the action handlers
     * for various buttons like borrow, return,
     * overdue, and progress buttons.
     *
     * The book's latest borrowing state is retrieved and the UI is adjusted accordingly:
     * - If the book is currently borrowed, the return state is activated.
     * - If the book is overdue, the overdue state is activated.
     * - If the book is available to borrow, the borrow state is activated.
     * - If the book is planned to be borrowed, the "Plan to borrow" state is activated.
     */
    public void setUp() {
        if (book == null) {
            return;
        }

        borrowService.updateOverdueBorrowsByUserId(userSession.getUserId());
        anchorpane_overview.setVisible(true);
        anchorpane_comment.setVisible(false);
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

        // Book quantity
        quantity_label.getChildren().clear();

        FontIcon icon = new FontIcon(FontAwesomeRegular.CLONE);
        icon.setIconColor(Color.web("#2e2e2e"));
        icon.setIconSize(16);
        quantity_label.getChildren().add(icon);

        quantityText = new Text();
        quantityText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-fill: #2e2e2e");
        quantity_label.getChildren().add(quantityText);

        Text copiesText = new Text();
        copiesText.setText(" copies available");
        copiesText.setStyle("-fx-font-size: 16px; -fx-fill: #2e2e2e");
        quantity_label.getChildren().add(copiesText);
        updateBookQuantity();

        borrow_modal.setVisible(false);
        return_modal.setVisible(false);
        overdue_modal.setVisible(false);

        borrow_message.setVisible(false);
        button_reload_captcha.setOnAction(e -> resetCaptcha());

        button_borrow.setOnAction(e -> clickButtonBorrow());
        cancel_borrow_modal.setOnAction(e -> borrow_modal.setVisible(false));
        accept_borrow_modal.setOnAction(e -> handleAcceptBorrow());

        button_return.setOnAction(e -> clickButtonReturn());
        cancel_return_modal.setOnAction(e -> return_modal.setVisible(false));
        accept_return_modal.setOnAction(e -> handleAcceptReturn());

        button_overdue.setOnAction(e -> clickButtonOverdue());
        close_overdue_modal.setOnAction(e -> overdue_modal.setVisible(false));

        button_plantoborrow.setOnAction(e -> clickButtonPlanToBorrow());

        updateLatestBorrow();
        if (latestBorrow != null && latestBorrow.getStatus().equals("Borrowing")) {
            changeToReturnState();
        } else if (latestBorrow != null && latestBorrow.getStatus().equals("Overdue")) {
            changeToOverdueState();
        } else {
            changeToBorrowState();
        }

        if (latestBorrow != null && latestBorrow.getStatus().equals("Plan to borrow")) {
            changeToPlanToBorrow();
        } else {
            changeToNotPlanToBorrow();
        }

        button_edit_progress.setOnAction(e -> changeToUpdateProgressState());
        button_accept_progress.setOnAction(e -> handleAcceptProgress());
        button_cancel_progress.setOnAction(e -> handleCancelProgress());

        // Share button
        qr_code_tooltip.setVisible(false);

        button_share.setOnAction(e -> {
            if (qr_code_tooltip.isVisible()) {
                qr_code_tooltip.setVisible(false);
                return;
            } else {
                qr_code_tooltip.setVisible(true);
                QRCode QR = new QRCode();
                Image qrCode = QR.createQR(book.getId(), 150, 150);
                qr_code_img.setImage(qrCode);
            }
        });

        setCommentButton();
    }

    /**
     * Updates the book's state by retrieving the most recent version of the book from the service.
     * This method calls the `bookService` to fetch the latest details of the book from the database
     * and updates the `book` variable with the fetched information.
     */
    private void updateBookState() {
        this.book = bookService.getBook(book.getId());
    }

    /**
     * Updates the latest borrow record of the book for the current user.
     * This method calls the `borrowService` to retrieve the most recent borrowing
     * record for the current user
     * and the specified book. The record is stored in the `latestBorrow` variable.
     */
    public void updateLatestBorrow() {
        latestBorrow = borrowService
                .getLatestBorrowOfABookFromUser(userSession.getUserId(), book.getId());
    }


    /**
     * Sets the authors of the book in the UI.
     * If there is only one author, it sets the first author's name
     * and hides the other two author fields.
     * If there are two authors, it sets the names of the first two authors
     * and hides the third field.
     * If there are three authors, it sets the names of all three authors.
     *
     * @param listAuthors An array containing the authors' names.
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
     * Sets the categories of the book in the UI.
     * If there is only one category, it sets the first category and hides
     * the other two category fields.
     * If there are two categories, it sets the names of the first two categories
     * and hides the third field.
     * If there are three categories, it sets the names of all three categories.
     *
     * @param listCategories An array containing the categories of the book.
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
     * Sets the relative books section for the current book in the UI.
     * This method retrieves a list of books that belong to the same
     * category as the current book,
     * then randomly selects up to 7 books
     * from that list and displays them in the "You May Also Like" section.
     *
     * It first clears any existing books in the list, retrieves
     * the books from the same category,
     * and uses a random selection process to add books to the UI.
     * The selected books are displayed as a list
     * with a title "You May Also Like".
     */
    public void setRelativeBook() {
        listbookrelative.getChildren().clear();
        List<Book> listBook = new ArrayList<>();
        String query = book.getCategories()[0];
        listBook = bookService.getBookByCategory(book.getCategories()[0]);
        List<Book> listBookRandom = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            if (listBook.size() <= 1) break;
            int randId = random.nextInt(0, listBook.size() - 1);
            listBookRandom.add(listBook.get(randId));
            listBook.remove(randId);
        }
        listbookrelative.getChildren().add(AnchorPane
                .createAnchorPaneBook(listBookRandom,
                        "You May Also Like", 7, true,
                        BOOKLIST_UI, loading));
    }

    /**
     * Sets up the comment section for the current book.
     * This method prepares the UI to display the comments section, including setting up the
     * visibility of buttons (post, edit, delete), displaying existing reviews for the book,
     * and handling the case where the user has already commented.
     * It also allows the user to edit or delete their comment, and adds
     * the current rating to the rating bar.
     *
     * - Displays existing reviews for the book.
     * - Sets visibility of comment-related buttons based on user interaction.
     * - If the user has already commented, pre-fills the comment box with their
     * review and disables editing.
     * - Allows the user to edit or delete their comment.
     * - Displays the average rating of the book in the rating bar.
     */
    public void setComment() {
        post_comment.setVisible(true);
        edit_comment.setVisible(false);
        cancel_edit.setVisible(false);
        delete_comment.setVisible(false);
        message_comment_bar.setText("");
        message_comment_bar.setVisible(false);
        comment_box.setText("");
        comment_box.setEditable(true);

        vbox_comment.getChildren().clear();
        vbox_comment.setPadding(new Insets(10));
        vbox_comment.setSpacing(10);
        List<Review> listReview = reviewService.getReviewsByBookId(book.getId());

        Image image = new Image(getClass().getResourceAsStream("/img/admin.png"));
        boolean hasCommented = false;
        for (Review review : listReview) {
            User user = userService.findUserById(review.getUserId());
            vbox_comment.getChildren()
                    .add(AnchorPane.createAnchorPaneReview(review, image, user.getUsername()));
            if (Objects.equals(review.getUserId(), userSession.getUserId())) {
                comment_box.setText(review.getContent());
                comment_box.setEditable(false);
                rating = review.getRating();
                rating_comment_bar.getChildren().add(AnchorPane
                        .createAnchorPaneRating(rating,
                                this, false, 20));
                hasCommented = true;
                currentReview = review;
                message_comment_bar.setText("You've already commented. Wanna edit?");
                message_comment_bar.setVisible(true);

                post_comment.setVisible(false);
                edit_comment.setVisible(true);
                delete_comment.setVisible(true);
            }
        }
        if (!hasCommented) {
            rating_comment_bar.getChildren().add(AnchorPane
                    .createAnchorPaneRating(
                            rating, this, true, 20));
            rating_comment_bar.setOnMouseExited(e -> {
                rating_comment_bar.getChildren()
                        .add(AnchorPane.createAnchorPaneRating(
                                rating, this, true, 20));
            });
        }

        rating_bar.getChildren().add(com.ddd.bookverse.item.AnchorPane.
                createAnchorPaneRating((int) Math.round(book.getAverageRating()),
                        null, false, 20));
    }

    /**
     * Handles the deletion of a user's comment.
     * If the user has posted a comment for the current book, this method
     * displays a confirmation prompt asking
     * whether the user is sure they want to delete their comment. If confirmed,
     * the comment will be deleted.
     */
    public void handleDeleteComment() {
        if (currentReview != null) {
            deleteConfirmationController.render("Are you sure to delete your comment?");
            if (!mainAnchorPane.getChildren().
                    contains(anchorPaneDelete)) mainAnchorPane.
                    getChildren().add(anchorPaneDelete);
        }
    }


    /**
     * Handles the event when the user edits their comment.
     * This method allows the user to edit their comment by making the comment box editable again,
     * displaying the "post" and "cancel edit" buttons, and hiding the "edit" and "delete" buttons.
     * It also updates the rating bar to allow the user to modify their rating.
     */
    public void handleEditComment() {
        cancel_edit.setVisible(true);
        post_comment.setVisible(true);
        edit_comment.setVisible(false);
        delete_comment.setVisible(false);

        comment_box.setEditable(true);
        message_comment_bar.setVisible(false);
        rating_comment_bar.getChildren().add(AnchorPane.
                createAnchorPaneRating(rating, this, true, 20));
        rating_comment_bar.setOnMouseExited(e -> {
            rating_comment_bar.getChildren().add(AnchorPane
                    .createAnchorPaneRating(rating, this, true, 20));
        });
    }

    /**
     * Handles the event when the user cancels editing their comment.
     * This method restores the UI to the previous state, including updating the rating bar
     * and displaying the original comment state. It also removes any
     * temporary changes made during editing.
     */
    public void handleCancelEdit() {
        if (currentReview != null) {
            rating = currentReview.getRating();
        }
        rating_comment_bar.getChildren().add(AnchorPane.
                createAnchorPaneRating(rating, this, false, 20));
        rating_comment_bar.setOnMouseExited(null);
        setComment();
        removeCommentEffect();
    }

    /**
     * Handles the event when the user posts or updates their comment.
     * This method validates the rating and comment content, ensuring that the rating is given
     * and the comment is at least 50 characters long. If valid, it either creates a new review
     * or updates an existing one. It also updates the book's rating information and triggers
     * the necessary UI changes.
     *
     * If the user is updating an existing comment:
     * - Updates the comment content and rating.
     * - Adjusts the book's rating sum accordingly.
     *
     * If the user is posting a new comment:
     * - Creates a new review.
     * - Increases the book's rating count and adjusts the rating sum.
     */
    public void handlePostComment() {
        if (rating < 1) {
            message_comment_bar.setText("Please rate before posting comment");
            message_comment_bar.setVisible(true);
            return;
        }
        String content = comment_box.getText();
        if (content.length() < 50) {
            message_comment_bar.setText("Comment need at least 50 characters");
            message_comment_bar.setVisible(true);
            return;
        }

        if (currentReview != null) {
            int oldRating = currentReview.getRating();
            reviewService.updateReview(currentReview.getId(), content, rating);
            message_comment_bar.setText("Your comment has been updated.");

            book.setRatingSum(book.getRatingSum() + rating - oldRating);
            rating_comment_bar.getChildren().add(AnchorPane.
                    createAnchorPaneRating(
                            rating, this, false, 20));

            rating_comment_bar.setOnMouseExited(null);
            bookService.updateBook(book);
            setComment();
        } else {
            Review newReview = new Review(book.getId(), userSession.getUserId(), content, rating);
            reviewService.addReview(newReview);
            message_comment_bar.setText("Thank you for your comment.");

            book.setRatingCount(book.getRatingCount() + 1);
            book.setRatingSum(book.getRatingSum() + rating);

            rating_comment_bar.setOnMouseExited(null);
            bookService.updateBook(book);
            setComment();
        }

        removeCommentEffect();
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(e -> setComment());
        delay.play();
    }


    /**
     * Resets the CAPTCHA by generating a new CAPTCHA image and text.
     * The new CAPTCHA image is displayed in the UI, and the text is printed to the console.
     * This method ensures that the CAPTCHA
     * is refreshed each time the user needs to verify their action.
     */
    public void resetCaptcha() {
        Pair<Image, String> captcha = com.ddd.bookverse.utils.CaptchaUtil.getRandomCaptcha();
        captchaImage = captcha.getKey();
        captchaText = captcha.getValue();
        captcha_image.setImage(captchaImage);
        System.out.println(captchaText);
    }

    /**
     * Handles the event when the user clicks the "Borrow" button.
     * This method checks whether the user has already reached
     * the maximum allowed number of borrowed books.
     * If the user has reached the maximum limit, a message is displayed to inform the user.
     * If the limit is not reached, a borrow modal is displayed,
     * and a new CAPTCHA is generated for validation.
     */
    public void clickButtonBorrow() {
        Long userId = userSession.getUserId();
        int booksBorrowedCount = bookService.getNumBooksByUserIdAndStatus(userId,
                "Borrowing");
        if (booksBorrowedCount >= MAX_BORROWS) {
            if (fadeOut != null && fadeOut.getStatus() == Animation.Status.RUNNING) {
                fadeOut.stop();
            }
            borrow_message.setText("You cannot borrow more than " + MAX_BORROWS + " books.");
            borrow_message.setVisible(true);

            fadeOut = new FadeTransition(Duration.seconds(5), borrow_message);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(fadeEvent -> {
                borrow_message.setVisible(false);
            });
            fadeOut.play();
            return;
        }
        borrow_modal.setVisible(true);
        resetCaptcha();
    }

    /**
     * Validates the borrow request form.
     * This method checks if the return date is valid (between today and one month from now)
     * and if the CAPTCHA input matches the generated CAPTCHA text.
     * The form will be highlighted with a red border if there are validation errors.
     *
     * @return true if the borrow request is valid, false otherwise.
     */
    private Boolean validateBorrow() {
        Boolean valid = true;

        LocalDate returnDate = return_date_picker.getValue();
        String userCaptcha = captcha_confirm.getText();

        if (returnDate == null
                || returnDate.isBefore(LocalDate.now())
                || returnDate.isAfter(LocalDate.now().plusMonths(1))
        ) {
            return_date_picker.setStyle("-fx-border-color: red; -fx-border-width: 2px");
            valid = false;
        } else {
            return_date_picker.setStyle("-fx-border-color: transparent");
        }

        if (!userCaptcha.equals(captchaText)) {
            valid = false;
            captcha_confirm.setStyle("-fx-border-color: red; -fx-border-width: 2px");
        } else {
            captcha_confirm.setStyle("-fx-border-color: transparent");
        }

        if (!valid) {
            resetCaptcha();
        }

        return valid;
    }

    /**
     * Handles the event when the user accepts the borrow request.
     * This method validates the form using the `validateBorrow()` method.
     * If the form is valid, a new borrow record is created or updated,
     * and the book's stock quantity is decreased.
     * The borrow modal is closed, and the book's borrowing state is updated.
     *
     * The method also transitions the book to the "return" state if applicable
     * and resets the borrow plan status.
     */
    public void handleAcceptBorrow() {
        if (!validateBorrow()) {
            return;
        }

        Borrow newBorrow = new Borrow();
        newBorrow.setUser(userService.findUserById(userSession.getUserId()));
        newBorrow.setBook(book);
        newBorrow.setBorrowDate(LocalDate.now());
        newBorrow.setReturnDate(return_date_picker.getValue());
        newBorrow.setStatus("Borrowing");
        newBorrow.setPagesRead(0);

        if (latestBorrow != null && latestBorrow.getStatus().equals("Plan to borrow")) {
            borrowService.updateBorrow(latestBorrow.getId(), newBorrow);
        } else {
            borrowService.addBorrow(newBorrow);
        }
        borrow_modal.setVisible(false);

        bookService.updateBookQuantity(book.getId(), book.getQuantityInStock() - 1);
        updateBookState();
        updateBookQuantity();

        updateLatestBorrow();
        changeToReturnState();
        changeToNotPlanToBorrow();
    }


    /**
     * Changes the book's state to "Borrow" mode.
     * This method hides the "Return" button, shows the "Borrow" and "Plan to Borrow" buttons,
     * and hides the reading progress indicator, allowing the user to borrow the book.
     */
    public void changeToBorrowState() {
        button_borrow.setVisible(true);
        button_return.setVisible(false);
        button_plantoborrow.setVisible(true);
        reading_progress.setVisible(false);
    }

    /**
     * Handles the event when the "Overdue" button is clicked.
     * This method displays an overdue modal containing information about the overdue book,
     * including a warning icon, the book's title, the due date, and a message about
     * returning the book.
     * The modal will prompt the user to return the book to avoid additional penalties.
     */
    private void clickButtonOverdue() {
        overdue_modal.setVisible(true);
        overdue_modal_message.getChildren().clear();

        FontIcon warningIcon = new FontIcon("fas-exclamation-circle");
        warningIcon.setIconSize(24);
        warningIcon.setFill(Paint.valueOf("#e74c3c"));
        String truncatedTitle = book.getTitle().length() > 35
                ? book.getTitle().substring(0, 32) + "..."
                : book.getTitle();
        Text bookTitle = new Text(truncatedTitle);
        bookTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        bookTitle.setFill(Color.web("#000000"));
        HBox titleBox = new HBox(10, warningIcon, bookTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Text dueDate = new Text("Due date: " + latestBorrow.getReturnDate());
        dueDate.setStyle("-fx-font-size: 12px; -fx-font-weight: normal;");
        dueDate.setFill(Color.web("#555555"));

        Text message = new Text(
                "Please return this book as soon as possible to avoid additional penalties. "
                        + "Contact the library for more information."
        );

        message.setWrappingWidth(300);
        message.setFill(Color.web("#555555"));
        message.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");

        overdue_modal_message.getChildren().addAll(
                titleBox,
                new Text("\n"),
                dueDate,
                new Text("\n\n"),
                message
        );
        overdue_modal_message.setTextAlignment(TextAlignment.LEFT);
    }

    /**
     * Changes the book's state to "Overdue" mode.
     * This method displays an "Overdue" button showing
     * the number of days the book is overdue,
     * hides the "Borrow" and "Plan to Borrow" buttons,
     * and shows a message indicating that the book is overdue.
     * Additionally, it shows the reading progress indicator
     * with a locked state and updates the progress display.
     */
    private void changeToOverdueState() {
        long daysOverdue = ChronoUnit.DAYS.between(latestBorrow.getReturnDate(), LocalDate.now());
        button_overdue.setText("Overdue (" + daysOverdue + " days late)");
        button_overdue.setVisible(true);

        button_return.setVisible(false);
        button_borrow.setVisible(false);
        button_plantoborrow.setVisible(false);

        borrow_message.setVisible(true);
        borrow_message.setText("This book is overdue! Progress tracking is locked.");

        reading_progress.setVisible(true);
        total_pages.setText("/ " + book.getPageCount());
        changeToOverdueProgressState();
    }


    /**
     * Changes the book's state to "Return" mode.
     * This method displays the "Return" button with the number of days left to return the book,
     * hides the "Borrow" and "Plan to Borrow" buttons, and hides the borrow message.
     * It also shows the reading progress indicator and updates the total pages.
     * Additionally, it changes the reading progress state to "normal".
     */
    public void changeToReturnState() {
        long daysEarlier = ChronoUnit.DAYS.between(LocalDate.now(), latestBorrow.getReturnDate());

        button_return.setText("Return (in " + daysEarlier + " days)");
        button_return.setVisible(true);

        button_borrow.setVisible(false);
        borrow_message.setVisible(false);
        button_plantoborrow.setVisible(false);

        reading_progress.setVisible(true);
        total_pages.setText("/ " + book.getPageCount());
        changeToNormalProgressState();
    }

    /**
     * Handles the event when the "Return" button is clicked.
     * This method displays a modal that asks the user for confirmation to return the book
     * despite returning it "X" days earlier than the expected return date.
     * It shows a warning with the number of days the book is being returned earlier.
     */
    public void clickButtonReturn() {
        return_modal.setVisible(true);
        return_modal_message.getChildren().clear();

        long daysEarlier = ChronoUnit.DAYS.between(LocalDate.now(), latestBorrow.getReturnDate());
        Text text = new Text("Are you sure you want to return this book despite being ");
        text.setFont(Font.font(16));
        Text days = new Text(daysEarlier + " days earlier?");
        days.setStyle("-fx-font-weight: bold");
        days.setFont(Font.font(16));
        return_modal_message.getChildren().add(text);
        return_modal_message.getChildren().add(days);
    }

    /**
     * Handles the confirmation of returning the book.
     * This method updates the borrow status to "Returned", hides the return modal,
     * and updates the book's stock quantity.
     * It also updates the book's state and quantity and
     * prepares the system for the next possible borrow.
     */
    public void handleAcceptReturn() {
        borrowService.updateBorrowStatus(latestBorrow.getId(), "Returned");
        return_modal.setVisible(false);

        bookService.updateBookQuantity(book.getId(), book.getQuantityInStock() + 1);
        updateBookState();
        updateBookQuantity();

        updateLatestBorrow();
        changeToBorrowState();
    }


    /**
     * Handles the event when the "Plan to Borrow" button is clicked.
     * This method checks if the current book is already planned to be borrowed.
     * If so, it deletes the plan and changes the button state to "Plan to borrow".
     * Otherwise, it adds a new borrow entry with status "Plan to borrow"
     * and changes the button state to "Planned to borrow".
     */
    private void clickButtonPlanToBorrow() {
        updateLatestBorrow();

        if (latestBorrow != null && latestBorrow.getStatus().equals("Plan to borrow")) {
            borrowService.deleteBorrow(latestBorrow.getId());
            changeToNotPlanToBorrow();
        } else {
            borrowService.addBorrow(
                    userSession.getUserId(),
                    book.getId(),
                    java.time.LocalDate.now().toString(),
                    java.time.LocalDate.now().plusMonths(1).toString(),
                    "Plan to borrow",
                    0
            );
            changeToPlanToBorrow();
        }

        updateLatestBorrow();
    }

    /**
     * Changes the state of the "Plan to Borrow" button to indicate that
     * the book is planned to be borrowed.
     * This method modifies the button's style, text, and icon to
     * show a planned borrowing state.
     */
    public void changeToPlanToBorrow() {
        button_plantoborrow.getStyleClass().remove("btn-default");
        button_plantoborrow.getStyleClass().add("btn-planned-to-borrow");
        button_plantoborrow.setText("Planned to borrow");
        FontIcon icon = new FontIcon(FontAwesomeSolid.CHECK);
        icon.setIconColor(Color.web("#5f43b2"));
        icon.setIconSize(16);
        button_plantoborrow.setGraphic(icon);
        button_plantoborrow.setGraphicTextGap(8);
    }

    /**
     * Changes the state of the "Plan to Borrow" button back to its default state.
     * This method resets the button's style, text, and removes the icon.
     */
    public void changeToNotPlanToBorrow () {
        button_plantoborrow.getStyleClass().remove("btn-planned-to-borrow");
        button_plantoborrow.getStyleClass().add("btn-default");
        button_plantoborrow.setText("Plan to borrow");
        button_plantoborrow.setGraphic(null);
    }

    /**
     * Changes the reading progress state to the normal mode where users
     * can view their progress.
     * This method displays the current pages read and allows editing
     * by showing the edit button.
     */
    public void changeToNormalProgressState() {
        pages_read.setText(latestBorrow.getPagesRead() + "");
        pages_read.setVisible(true);
        pages_read_input.setVisible(false);
        button_edit_progress.setVisible(true);
        button_accept_progress.setVisible(false);
        button_cancel_progress.setVisible(false);
    }

    /**
     * Changes the reading progress state to the overdue mode where users
     * can view their progress
     * but cannot edit it. This method hides the input field for pages read
     * and disables editing buttons.
     */
    public void changeToOverdueProgressState() {
        pages_read.setText(latestBorrow.getPagesRead() + "");
        pages_read.setVisible(true);

        pages_read_input.setVisible(false);

        button_edit_progress.setVisible(false);
        button_accept_progress.setVisible(false);
        button_cancel_progress.setVisible(false);
    }

    /**
     * Changes the reading progress state to the update mode where users
     * can edit the number of pages read.
     * This method shows the input field for pages read and enables the
     * accept and cancel buttons for editing.
     */
    public void changeToUpdateProgressState() {
        pages_read_input.setText(latestBorrow.getPagesRead() + "");
        pages_read_input.setVisible(true);
        pages_read.setVisible(false);
        button_edit_progress.setVisible(false);
        button_accept_progress.setVisible(true);
        button_cancel_progress.setVisible(true);
    }


    /**
     * Handles the event when the user accepts the updated reading progress.
     * This method updates the pages read in the borrow record and then
     * switches the reading progress state back to normal.
     */
    public void handleAcceptProgress() {
        int pagesRead = Integer.parseInt(pages_read_input.getText());
        pagesRead = Math.min(pagesRead, book.getPageCount());
        System.out.println(pagesRead);
        borrowService.updatePagesRead(latestBorrow.getId(), pagesRead);
        updateLatestBorrow();
        changeToNormalProgressState();
    }

    /**
     * Handles the event when the user cancels the reading progress update.
     * This method switches the reading progress state back to normal without saving changes.
     */
    public void handleCancelProgress() {
        changeToNormalProgressState();
    }

    /**
     * Changes the rating bar to display a specified rating.
     * This method adds a rating component to the rating comment bar.
     *
     * @param rating The rating to be displayed.
     */
    public void changeRatingBar(int rating) {
        rating_comment_bar.getChildren().add(AnchorPane.createAnchorPaneRating(
                rating,this, true, 20));
    }

    /**
     * Sets the rating for the book and updates the rating comment bar.
     * This method adds a rating component to the rating comment bar and
     * updates the internal rating variable.
     *
     * @param rating The rating to be set.
     * @param isEditable Flag indicating whether the rating is editable.
     */
    public void setRating(int rating, boolean isEditable) {
        rating_comment_bar.getChildren().add(AnchorPane.createAnchorPaneRating(
                rating,this, isEditable, 20));
        this.rating = rating;
    }

    /**
     * Updates the book quantity display and enables or disables the borrow buttons
     * based on the availability of the book in stock.
     * If the book is out of stock, the borrow buttons are disabled.
     */
    private void updateBookQuantity() {
        quantityText.setText("  " + book.getQuantityInStock());

        if (book.getQuantityInStock() == 0) {
            button_borrow.setDisable(true);
            button_plantoborrow.setDisable(true);
        } else {
            button_borrow.setDisable(false);
            button_plantoborrow.setDisable(false);
        }
    }

    /**
     * Gets the previous UI screen or state that the user was on.
     *
     * @return The string representing the previous UI screen or state.
     */
    public String getPrevUI() {
        return prevUI;
    }

    /**
     * Sets the previous UI screen or state that the user was on.
     * This method updates the `prevUI` variable and updates the relative book state.
     *
     * @param prevUI The string representing the previous UI screen or state.
     */
    public void setPrevUI(String prevUI) {
        this.prevUI = prevUI;
        setRelativeBook();
    }

}
