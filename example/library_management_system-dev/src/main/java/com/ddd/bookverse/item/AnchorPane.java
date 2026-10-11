package com.ddd.bookverse.item;
import com.ddd.bookverse.controller.*;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.model.Borrow;
import com.ddd.bookverse.model.Review;
import com.ddd.bookverse.model.User;
import com.ddd.bookverse.service.BookService;
import com.ddd.bookverse.service.BorrowService;
import com.ddd.bookverse.service.ReviewService;
import com.ddd.bookverse.singleton.UserSession;
import javafx.animation.*;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.ddd.bookverse.model.Borrow.BORROWING;

public class AnchorPane extends javafx.scene.layout.AnchorPane {
    private static BookService bookService = new BookService();
    private static final ReviewService reviewService = new ReviewService();
    private static final BorrowService borrowService = new BorrowService();
    private static UserSession userSession = UserSession.getInstance();
    private static BookListController bookListController = new BookListController();
    private static DetailsBookAdminController detailsBookAdminController = new DetailsBookAdminController();
    public static BookManagementController bookManagementController = new BookManagementController();
    public static AccountManagementController accountManagementController = new AccountManagementController();
    public static final String BOOKLIST_UI = "Booklist";
    public static final String DASHBOARD_UI = "Dashboard";
    public static final String BOOKMNG_UI = "Bookmanagement";

    /**
     * Creates an {@link AnchorPane} that displays a list of books in a grid format.
     * The method customizes the appearance and layout of the books based on various
     * parameters such as the title, the number of books,
     * whether to display star ratings, and the current UI context (e.g., borrowing status).
     *
     * @param listBook A list of books to be displayed in the grid.
     * @param title The title to be displayed at the top of the grid, e.g., "You May Also Like".
     * @param amountBook The number of books to be displayed in the grid.
     * @param isStar A flag indicating whether to display star ratings for the books.
     * @param currentUI The current user interface context (e.g., "BORROWING").
     * @param loading An {@link AnchorPane} that shows a loading indicator
     *                while the book details are being fetched.
     *
     * @return An {@link AnchorPane} containing the list of books arranged in a grid,
     * with titles, images, and additional information.
     */
    public static AnchorPane createAnchorPaneBook(List<Book> listBook, String title,
                                                  int amountBook , boolean isStar, String currentUI,
                                                  javafx.scene.layout.AnchorPane loading) {

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(141 * amountBook);

        Pane pane = new Pane();
        pane.setPrefWidth(141 * amountBook);
        pane.setPrefHeight(60);
        pane.setStyle("-fx-background-color: #5f43b2; -fx-background-radius: 8px 8px 0 0;");
        Text titleText = new Text(title,"#fff",24);
        titleText.setStyle("-fx-font-weight:bold; -fx-font-size:20;");
        pane.setLayoutY(25);
        titleText.setLayoutY(28);
        titleText.setLayoutX(14);
        if(titleText.getText().equals("You May Also Like")) {
            pane.setLayoutX(10);
            pane.setStyle("-fx-background-color:white;");
            titleText.setFill(Color.web("#5f43b2"));
            titleText.setLayoutY(10);
        }
        pane.getChildren().add(titleText);
        // Tạo GridPane
        GridPane gridPane = new GridPane(1.0 * 141 * amountBook,214.0);
        gridPane.setAlignment(javafx.geometry.Pos.CENTER);
        gridPane.setLayoutX(titleText.getText().equals("You May Also Like") ? 10 : 0);

        if (titleText.getText().isEmpty()) {
            gridPane.setLayoutY(15);
            gridPane.setStyle("-fx-background-color:white; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        }
        else {
            gridPane.setLayoutY(70);
            gridPane.setStyle("-fx-background-color:white; -fx-background-radius: 0 0 10px 10px; -fx-border-radius: 0 0 10px 10px;");
        }

        if (!titleText.getText().equals("You May Also Like")) {
            gridPane.setStyle(gridPane.getStyle() + " -fx-border-color: #e4e4e7;");
        }


        for (int i = 0; i < amountBook; i++) {
            ColumnConstraints column = new ColumnConstraints(100.0,10.0);
            column.setHalignment(javafx.geometry.HPos.CENTER);
            column.setHgrow(Priority.SOMETIMES);
            gridPane.getColumnConstraints().add(column);
        }
        RowConstraints row1 = new RowConstraints(154.8,162.2,10.0);
        row1.setValignment(VPos.CENTER);
        gridPane.getRowConstraints().add(row1);

        RowConstraints row2 = new RowConstraints(30.0,78.8,0.0);
        gridPane.getRowConstraints().add(row2);


        for (int i = 0; i < Math.min(amountBook,listBook.size()); i++) {
            VBox vBox = new VBox();
            vBox.setStyle("-fx-background-color:rgba(229, 231, 235, 0.7);");
            Button buttonRead = new Button("Read");
            FontIcon readIcon = new FontIcon("fas-book-reader");
            readIcon.setIconColor(Color.web("#fff"));
            buttonRead.setGraphic(readIcon);
            buttonRead.setGraphicTextGap(8);
            buttonRead.getStyleClass().add("btn");
            buttonRead.getStyleClass().add("btn-primary");
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().add(buttonRead);
            vBox.setVisible(false);

            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(listBook.get(i).getImage())));
            double imageViewWidth = 100.0;
            double imageViewHeight = 150.0;
            imageView.setFitHeight(imageViewHeight);
            imageView.setFitWidth(imageViewWidth);
            imageView.setPreserveRatio(false);
            imageView.setStyle("-fx-effect: dropshadow(gaussian, lightgray, 10, 0.3, 0, 0); -fx-border-color: lightgray; -fx-border-width: 2;");

            Rectangle clip = new Rectangle(imageViewWidth, imageViewHeight);
            imageView.setClip(clip);

            vBox.setMaxHeight(imageViewHeight * 1.2);
            vBox.setMaxWidth(imageViewWidth * 1.05);
            vBox.setLayoutX(imageView.getLayoutX());
            vBox.setLayoutY(imageView.getLayoutY());

            ScaleTransition scaleZoom = new ScaleTransition(Duration.seconds(0.2), imageView);
            ScaleTransition scaleZoomReverse = new ScaleTransition(Duration.seconds(0.2), imageView);
            int index = i;
            buttonRead.setOnMouseClicked(event -> {
                scaleZoom.setFromX(1.0);
                scaleZoom.setFromY(1.0);
                scaleZoom.setToX(0.9);
                scaleZoom.setToY(0.9);
                scaleZoom.play();
                loading.setVisible(true);
                try {
                    bookListController.loadDetailsBook(event, listBook.get(index), currentUI);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), vBox);
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), vBox);
            imageView.setOnMouseEntered(event -> {
                        scaleZoomReverse.setFromX(1.0);
                        scaleZoomReverse.setFromY(1.0);
                        scaleZoomReverse.setToX(1.1);
                        scaleZoomReverse.setToY(1.05);
                        scaleZoomReverse.play();
                vBox.setOpacity(0);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                if (fadeOut.getStatus() != Animation.Status.RUNNING) {
                    vBox.setVisible(true);
                }
                fadeIn.play();
            });

            vBox.setOnMouseExited(event -> {
                if (scaleZoomReverse.getStatus() == Animation.Status.RUNNING) {
                    scaleZoomReverse.stop();
                }
                    scaleZoom.setFromX(1.1);
                    scaleZoom.setFromY(1.05);
                    scaleZoom.setToX(1.0);
                    scaleZoom.setToY(1.0);
                    scaleZoom.play();
                    if(fadeIn.getStatus() == Animation.Status.RUNNING) {
                        fadeIn.stop();
                    }
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> vBox.setVisible(false));
                fadeOut.play();
            });

            gridPane.add(imageView, i, 0);
            gridPane.add(vBox, i, 0);
        }

        for (int i = 0; i < Math.min(amountBook,listBook.size()); i++) {
            Label bookText = new Label(listBook.get(i).getTitle(), "#333333" ,
                    "-fx-pref-width: 90px; -fx-pref-height: 45px;" +
                            " -fx-wrap-text: true;-fx-font-size:13" );
            gridPane.add(bookText, i, 1);
        }

        if (!currentUI.equals(BORROWING)) {
            RowConstraints row = new RowConstraints(20.0, 78.8, 0.0);
            gridPane.getRowConstraints().add(row);

            for (int i = 0; i < Math.min(amountBook, listBook.size()); i++) {
                Label bookText = new Label(String.join(",", listBook.get(i).getAuthors()),
                        "#666666", "-fx-pref-width: 90px;" +
                        " -fx-pref-height: 45px; -fx-wrap-text: true;-fx-font-size:12");
                gridPane.add(bookText, i, 2);
            }
        }

        if (isStar) {
            RowConstraints row4 = new RowConstraints(30.0, 61.6, 10.0);
            gridPane.getRowConstraints().add(row4);
            for (int i = 0; i < Math.min(amountBook, listBook.size()); i++) {
                AnchorPane anchorpanerating = new AnchorPane();
                for (int j = 0; j < 5; j++) {
                    Path star = new Path();
                    star = star.createStar(16, j < listBook.get(i).getAverageRating() ? true : false);
                    star.setLayoutY(10);
                    star.setLayoutX(20 + 20 * j);
                    anchorpanerating.getChildren().add(star);
                }
                gridPane.add(anchorpanerating, i, 3); // Thêm vào hàng 2, cột i
            }
        }

        if (currentUI.equals(BORROWING)) {
            RowConstraints row = new RowConstraints(16.4,61.6,10.0);
            gridPane.getRowConstraints().add(row);
            for (int i = 0; i < Math.min(amountBook,listBook.size()); i++) {
                Book book = listBook.get(i);
                String status = borrowService.
                        getLatestBorrowOfABookFromUser(userSession.getUserId(), book.getId()).
                        getStatus();
                if ("Overdue".equals(status)) {
                    Label overdueLabel = new Label("Overdue");
                    overdueLabel.setStyle("-fx-text-fill: #e74c3c; " +
                            "-fx-font-size: 12px; -fx-font-weight: bold;");
                    gridPane.add(overdueLabel, i, 2);
                    javafx.scene.layout.GridPane.setHalignment(overdueLabel, HPos.CENTER);
                    javafx.scene.layout.GridPane.setValignment(overdueLabel, VPos.CENTER);
                } else {
                    double completionPercentage = bookService.getCompletionPercentage(book.getId(),
                            userSession.getUserId());
                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setPrefWidth(120.0);
                    progressBar.setPrefHeight(10.0);
                    progressBar.setProgress(completionPercentage);
                    progressBar.setStyle("-fx-accent: #5f43b2;");
                    gridPane.add(progressBar, i, 2);
                }
            }
        }

        anchorPane.getChildren().add(pane);
        anchorPane.getChildren().add(gridPane);
        return anchorPane;
    }

    /**
     * Creates an {@link AnchorPane} that displays a review for a book.
     * The review includes the user's profile image, name, star rating, and content description.
     *
     * @param review The {@link Review} object containing the review details.
     * @param image The profile image of the user who posted the review.
     * @param username The username of the user who posted the review.
     *
     * @return An {@link AnchorPane} containing the review details, including the user's profile
     * image, name, star rating, and review content.
     */
    public static AnchorPane createAnchorPaneReview(Review review, Image image, String username) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(141);
        anchorPane.setPrefWidth(578);
        anchorPane.setStyle("-fx-border-color: #dddddd; -fx-border-radius: 10; " +
                "-fx-background-radius: 10; -fx-background-color: #ffffff");

        ImageView adminImageView = new ImageView(image);
        adminImageView.setFitHeight(50);
        adminImageView.setFitWidth(50);
        adminImageView.setLayoutX(10);
        anchorPane.getChildren().add(adminImageView);

        Text nameText = new Text(username);
        nameText.setFill(Color.web("#666666"));
        nameText.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameText.setLayoutX(70);
        nameText.setLayoutY(20);
        anchorPane.getChildren().add(nameText);


        for (int i = 0;i < review.getRating(); i++) {
            Path star = new Path();
            star = star.createStar(16,true);
            star.setLayoutY(27);
            star.setLayoutX(69 + 20 * i);
            anchorPane.getChildren().add(star);
        }

        Text descriptionText = new Text(review.getContent());
        descriptionText.setFill(Color.web("#333333"));
        descriptionText.setFont(Font.font(16));
        descriptionText.setLayoutX(14);
        descriptionText.setLayoutY(69);
        descriptionText.setWrappingWidth(526.4);
        anchorPane.getChildren().add(descriptionText);

        return anchorPane;
    }


    /**
     * Creates an {@link AnchorPane} that displays a user account in a grid layout.
     * The account includes the user's ID, username, full name, email, and buttons for
     * viewing details, updating, and deleting the account. The layout also handles user
     * role-based behavior, disabling buttons for admin users.
     *
     * @param user The {@link User} object representing the account information to be displayed.
     *
     * @return An {@link AnchorPane} containing the user account details, with associated buttons
     *         for actions such as viewing details, updating, and deleting the account.
     *         Admin users will have their buttons disabled.
     */
    public static AnchorPane createAnchorPaneAccount(User user) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("table-row");

        GridPane gridPane = new GridPane();
        gridPane.setPrefHeight(35.0);
        gridPane.setPrefWidth(978.0);

        // Column Constraints
        for (int i = 0; i < 5; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
            switch (i) {
                case 0: column.setPrefWidth(56); break;
                case 1: column.setPrefWidth(163); break;
                case 2: column.setPrefWidth(202); break;
                case 3: column.setPrefWidth(232); break;
                case 4: column.setPrefWidth(326); break;
            }
            gridPane.getColumnConstraints().add(column);
        }

        // Text elements
        Text textId = new Text(user.getId().toString());
        gridPane.add(textId, 0, 0);

        Text textUsername = new Text(user.getUsername());
        gridPane.add(textUsername, 1, 0);

        Text textName = new Text(user.getFullName().length() < 15
                ? user.getFullName() : user.getFullName().substring(0, 15) + "...");
        gridPane.add(textName, 2, 0);

        Text textEmail = new Text(user.getEmail().length() < 30
                ? user.getEmail() : user.getEmail().substring(0, 30) + "...");
        gridPane.add(textEmail, 3, 0);

        // Buttons in AnchorPane
        FlowPane buttonContainer = new FlowPane();
        buttonContainer.setHgap(15);
        gridPane.add(buttonContainer, 4, 0);

        // Set up icon
        FontIcon trashIcon = new FontIcon("fas-trash-alt");
        FontIcon editIcon = new FontIcon("fas-edit");
        FontIcon detailIcon = new FontIcon("fas-info");
        trashIcon.setIconColor(Color.web("#dc3545"));
        editIcon.setIconColor(Color.web("#28a745"));
        detailIcon.setIconColor(Color.web("#5f43b2"));

        // Detail Button
        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().add("btn");
        detailButton.getStyleClass().add("btn-default");
        detailButton.getStyleClass().add("btn-details-inner");
        detailButton.setGraphic(detailIcon);
        buttonContainer.getChildren().add(detailButton);

        // Update Button
        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("btn");
        updateButton.getStyleClass().add("btn-default");
        updateButton.getStyleClass().add("btn-update-inner");
        updateButton.setGraphic(editIcon);
        buttonContainer.getChildren().add(updateButton);

        // Delete Button
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("btn");
        deleteButton.getStyleClass().add("btn-default");
        deleteButton.getStyleClass().add("btn-danger-inner");
        deleteButton.setGraphic(trashIcon);
        buttonContainer.getChildren().add(deleteButton);

        // Disable buttons if the user is an admin
        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            detailButton.setDisable(true);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        }

        // Button actions
        detailButton.setOnAction(e -> accountManagementController.clickDetails(user));
        updateButton.setOnAction(e -> accountManagementController.showFormUpdate(user));
        deleteButton.setOnAction(e -> accountManagementController.deleteAccount(user));

        anchorPane.getChildren().add(gridPane);
        return anchorPane;
    }

    /**
     * Creates an {@link AnchorPane} that displays the borrowing history of
     * a user in a grid layout.
     * The history includes the book ID, title, borrow date, return date,
     * and the current borrow status.
     * The status is displayed with different styles based on the status of the
     * borrowing process, such as
     * "Borrowing", "Returned", "Overdue", and "Paid".
     *
     * @param borrow The {@link Borrow} object containing the details of
     *              a borrowing record to be displayed.
     *
     * @return An {@link AnchorPane} containing the borrowing history details,
     * displayed in a grid layout,
     *         with a status box indicating the current status of the borrowing.
     */
    public static AnchorPane createAnchorPaneHistoryUser(Borrow borrow) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(971.0, 42.0);
        anchorPane.getStyleClass().add("table-row");

        // Tạo GridPane
        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(14.0);
        gridPane.setPrefSize(969.0, 38.0);

        // Cấu hình cột
        ColumnConstraints column1 = new ColumnConstraints(180.2);
        column1.setHalignment(javafx.geometry.HPos.CENTER);

        ColumnConstraints column2 = new ColumnConstraints(240.2);
        column2.setHalignment(javafx.geometry.HPos.CENTER);

        ColumnConstraints column3 = new ColumnConstraints(186.6);
        column3.setHalignment(javafx.geometry.HPos.CENTER);

        ColumnConstraints column4 = new ColumnConstraints(232.2);
        column4.setHalignment(javafx.geometry.HPos.CENTER);

        ColumnConstraints column5 = new ColumnConstraints(195.4);
        column5.setHalignment(javafx.geometry.HPos.CENTER);

        gridPane.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

        // Cấu hình hàng
        RowConstraints row = new RowConstraints(30.0);
        row.setValignment(javafx.geometry.VPos.CENTER);
        gridPane.getRowConstraints().add(row);

        // Thêm các thành phần con vào GridPane
        Text text1 = new Text(borrow.getBook().getId());

        Text text2 = new Text(borrow.getBook().getTitle().length() < 30
                ? borrow.getBook().getTitle() : borrow.getBook().getTitle().substring(0,30) + "...");
        GridPane.setColumnIndex(text2, 1);

        Text text3 = new Text(borrow.getBorrowDate().toString());
        GridPane.setColumnIndex(text3, 2);

        Text text4 = new Text(borrow.getReturnDate().toString());
        GridPane.setColumnIndex(text4, 3);

        Text borrowStatus = new Text(borrow.getStatus());
        VBox statusBox = new VBox();
        statusBox.setAlignment(javafx.geometry.Pos.CENTER);

        switch (borrow.getStatus()) {
            case "Borrowing":
                statusBox.getStyleClass().add("status-borrow-box");
//                statusText.getStyleClass().add("status-borrow-text");
                break;

            case "Returned":
                statusBox.getStyleClass().add("status-return-box");
//                statusText.getStyleClass().add("status-return-text");
                break;

            case "Overdue":
                statusBox.getStyleClass().add("status-overdue-box");
//                statusText.getStyleClass().add("status-overdue-text");
                break;

            case "Paid":
                statusBox.getStyleClass().add("status-paid-box");
//                statusText.getStyleClass().add("status-paid-text");
                break;

            default:
                statusBox.getStyleClass().add("status-default-box");
                break;
        }

        statusBox.getChildren().add(borrowStatus);
        GridPane.setColumnIndex(statusBox, 4);

        gridPane.getChildren().addAll(text1, text2, text3, text4, statusBox);

        anchorPane.getChildren().add(gridPane);
        return anchorPane;
    }

    /**
     * Creates an {@link AnchorPane} that displays the management options
     * for a book in a table row format.
     * This includes the book's ID, title, author,
     * and three action buttons: Detail, Update, and Delete.
     * Each button has an associated icon and is styled
     * with different colors for visual differentiation.
     * The Detail button also triggers loading of detailed book information when clicked.
     *
     * @param book The {@link Book} object containing the details of the book to be managed.
     * @param loading An {@link AnchorPane} that will be shown when the Detail button
     *               is clicked, indicating that details are being loaded.
     *
     * @return An {@link AnchorPane} containing the book details in a table row,
     * with buttons for viewing, updating, and deleting the book.
     */
    public static AnchorPane createAnchorPaneManagementBook(
            Book book,javafx.scene.layout.AnchorPane loading) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("table-row");

        GridPane gridPane = new GridPane();
        gridPane.setPrefHeight(35.0);

        // Column Constraints
        for (int i = 0; i < 4; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
            switch (i) {
                case 0: column.setPrefWidth(140); break;
                case 1: column.setPrefWidth(345); break;
                case 2: column.setPrefWidth(215); break;
                case 3: column.setPrefWidth(280); break;
            }
            gridPane.getColumnConstraints().add(column);
        }

        // Add Text
        Text idText = new Text(book.getId());
        gridPane.add(idText, 0, 0);

        Text titlebook = new Text(book.getTitle());
        titlebook.setWrappingWidth(300);
        titlebook.setStyle("-fx-font-weight:bold");
        gridPane.add(titlebook, 1, 0);

        Text authorText = new Text(book.getAuthors()[0]);
        gridPane.add(authorText, 2, 0);

        // Button Container
        FlowPane buttonContainer = new FlowPane();
        buttonContainer.setHgap(7);
        gridPane.add(buttonContainer, 3, 0);

        // Set up icon
        FontIcon trashIcon = new FontIcon("fas-trash-alt");
        FontIcon editIcon = new FontIcon("fas-edit");
        FontIcon detailIcon = new FontIcon("fas-info");
        trashIcon.setIconColor(Color.web("#dc3545"));
        editIcon.setIconColor(Color.web("#28a745"));
        detailIcon.setIconColor(Color.web("#5f43b2"));

        // Detail Button
        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().add("btn");
        detailButton.getStyleClass().add("btn-default");
        detailButton.getStyleClass().add("btn-details-inner");
        detailButton.setGraphic(detailIcon);
        buttonContainer.getChildren().add(detailButton);

        // Update Button
        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("btn");
        updateButton.getStyleClass().add("btn-default");
        updateButton.getStyleClass().add("btn-update-inner");
        updateButton.setGraphic(editIcon);
        buttonContainer.getChildren().add(updateButton);

        // Delete Button
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("btn");
        deleteButton.getStyleClass().add("btn-default");
        deleteButton.getStyleClass().add("btn-danger-inner");
        deleteButton.setGraphic(trashIcon);
        buttonContainer.getChildren().add(deleteButton);

        deleteButton.setOnAction(e -> {bookManagementController.deleteBook(book);});
        updateButton.setOnAction(e -> bookManagementController.showFormUpdate(book));
        detailButton.setOnAction(e ->{
            loading.setVisible(true);
            try {
                bookManagementController.loadDetailsBook(book ,e);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });

        anchorPane.getChildren().add(gridPane);
        return anchorPane;
    }

    /**
     * Creates an {@link AnchorPane} that represents a user borrow record in a table format.
     * This pane allows the user to view and modify the borrow date, return date,
     * and status of a borrowed book.
     * Changes made to the borrow record are tracked and can be applied or canceled later.
     *
     * @param borrow The {@link Borrow} object containing the initial borrow details,
     *               including the borrow ID, borrow date, return date, and status.
     * @param newlyAddedBorrow A boolean flag indicating whether the borrow record is newly added.
     *                         If true, the row will be highlighted as a new addition.
     * @param button_apply_changes The {@link Button} used to apply any changes made to the borrow records.
     * @param changedBorrows A {@link HashMap} that stores borrow records that have been modified,
     *                      with the borrow ID as the key and the updated {@link Borrow} as the value.
     * @param addedBorrows A {@link List} that stores newly added borrow records.
     *
     * @return An {@link AnchorPane} containing a {@link GridPane}
     * with editable fields for borrow date, return date, and status.
     *         The pane allows the user to modify these fields,
     *         and changes are tracked for later application.
     */
    public static AnchorPane createAnchorPaneUserBorrow(Borrow borrow,
                                                        boolean newlyAddedBorrow, Button button_apply_changes,
                                                        HashMap<Long, Borrow> changedBorrows,
                                                        List<Borrow> addedBorrows) {
        AnchorPane anchorPane = new AnchorPane();
        GridPane gridPane = new GridPane();

        RowConstraints row = new RowConstraints();
        row.setPrefHeight(50);
        gridPane.getRowConstraints().add(row);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
            column.setHalignment(javafx.geometry.HPos.CENTER);
            switch (i) {
                case 0: column.setPrefWidth(50); break;
                case 1: column.setPrefWidth(175); break;
                case 2: column.setPrefWidth(175); break;
                case 3: column.setPrefWidth(180); break;
            }
            gridPane.getColumnConstraints().add(column);
        }

        Text idText = new Text(borrow.getId() != null ? borrow.getId().toString() : "???");
        gridPane.add(idText, 0, 0);

        DatePicker borrowDate = new DatePicker(borrow.getBorrowDate());
        borrowDate.setPrefWidth(160);
        borrowDate.setPrefHeight(35);
        gridPane.add(borrowDate, 1, 0);

        DatePicker returnDate = new DatePicker(borrow.getReturnDate());
        returnDate.setPrefWidth(160);
        returnDate.setPrefHeight(35);
        gridPane.add(returnDate, 2, 0);

        ChoiceBox<String> statusChoiceBox = new ChoiceBox<>();
        statusChoiceBox.setPrefWidth(160);
        statusChoiceBox.setPrefHeight(35);
        statusChoiceBox.setValue(borrow.getStatus());

        String[] statusList = new String[0];

        switch (borrow.getStatus()) {
            case "Borrowing":
                statusList = new String[]{"Borrowing", "Returned"};
                break;
            case "Returned":
                statusList = new String[]{"Returned"};
                break;
            case "Overdue":
                statusList = new String[]{"Overdue", "Paid"};
                break;
            case "Paid":
                statusList = new String[]{"Paid"};
                break;
        }

        if (borrow.getStatus().equals("Returned") || borrow.getStatus().equals("Paid")) {
            statusChoiceBox.setDisable(true);
        }
        int i = 0;
        for (String status : statusList) {
            if (status.equals(borrow.getStatus())) {
                break;
            }
            i++;
        }
        statusChoiceBox.getItems().addAll(Arrays.copyOfRange(statusList, i, statusList.length));
        gridPane.add(statusChoiceBox, 3, 0);


        int addedBorrowIndex = addedBorrows.size();

        if (newlyAddedBorrow) {
            anchorPane.getStyleClass().add("added-row");
            updateApplyChangesButton(button_apply_changes);

            Borrow addedBorrow = new Borrow(borrow);
            addedBorrow.setBorrowDate(borrowDate.getValue());
            addedBorrow.setReturnDate(returnDate.getValue());
            addedBorrow.setStatus(statusChoiceBox.getValue());
            addedBorrows.add(addedBorrow);
        }

        statusChoiceBox.setOnAction(e -> {
            if (newlyAddedBorrow) {
                addedBorrows.get(addedBorrowIndex).setStatus(statusChoiceBox.getValue());
            } else {
                if (changedBorrows.containsKey(borrow.getId())) {
                    changedBorrows.get(borrow.getId()).setStatus(statusChoiceBox.getValue());
                } else {
                    anchorPane.getStyleClass().add("changed-row");
                    updateApplyChangesButton(button_apply_changes);

                    Borrow changedBorrow = new Borrow(borrow);
                    changedBorrow.setStatus(statusChoiceBox.getValue());
                    changedBorrows.put(borrow.getId(), changedBorrow);
                }
            }
        });

        borrowDate.setOnAction(e -> {
            if (newlyAddedBorrow) {
                addedBorrows.get(addedBorrowIndex).setBorrowDate(borrowDate.getValue());
            } else {
                if (changedBorrows.containsKey(borrow.getId())) {
                    changedBorrows.get(borrow.getId()).setBorrowDate(borrowDate.getValue());
                } else {
                    anchorPane.getStyleClass().add("changed-row");
                    updateApplyChangesButton(button_apply_changes);

                    Borrow changedBorrow = new Borrow(borrow);
                    changedBorrow.setBorrowDate(borrowDate.getValue());
                    changedBorrows.put(borrow.getId(), changedBorrow);
                }
            }
        });

        returnDate.setOnAction(e -> {
            if (newlyAddedBorrow) {
                addedBorrows.get(addedBorrowIndex).setReturnDate(returnDate.getValue());
            } else {
                if (changedBorrows.containsKey(borrow.getId())) {
                    changedBorrows.get(borrow.getId()).setReturnDate(returnDate.getValue());
                } else {
                    anchorPane.getStyleClass().add("changed-row");
                    updateApplyChangesButton(button_apply_changes);

                    Borrow changedBorrow = new Borrow(borrow);
                    changedBorrow.setReturnDate(returnDate.getValue());
                    changedBorrows.put(borrow.getId(), changedBorrow);
                }
            }
        });

        anchorPane.getChildren().add(gridPane);
        return anchorPane;
    }


    /**
     * Updates the state of the "Apply changes" button by enabling it
     * and incrementing the count of changes.
     * The count reflects the number of changes that need to be applied.
     * The button text is updated to show
     * the new count in the format "Apply changes (X)", where X is the updated count of changes.
     *
     * @param button_apply_changes The {@link Button}
     *                             that represents the "Apply changes" action. This button
     *                             is enabled and its text is updated to reflect the number
     *                             of pending changes.
     */
    public static void updateApplyChangesButton (Button button_apply_changes) {
        button_apply_changes.setDisable(false);
        int currentCount = Integer
                .parseInt(button_apply_changes.getText().replaceAll("\\D+", ""));
        button_apply_changes.setText("Apply changes (" + (currentCount + 1) + ")");
    }

    /**
     * Creates an {@link AnchorPane} displaying a rating system with 5 stars.
     * The rating is visually represented
     * by stars, where the filled stars correspond to the current rating.
     * If the provided controller and editable
     * flag are valid, the rating can be modified by interacting with the stars.
     *
     * @param rating The current rating value (1-5).
     *               This determines how many stars will be filled.
     * @param bookDetailController The {@link BookDetailController} to handle the rating interactions.
     *                            If null,
     *                             the rating is displayed without interactivity.
     * @param isEditable A boolean flag indicating whether the rating stars
     *                   should be interactive (editable) or not.
     *                   If true, users can modify the rating by clicking on the stars.
     * @param size The size (width and height) of each star in the rating.
     * @return An {@link AnchorPane} containing the rating system,
     * where each star corresponds to a part of the rating.
     */
    public static AnchorPane createAnchorPaneRating(
            int rating ,BookDetailController bookDetailController,
            boolean isEditable, int size) {
        AnchorPane anchorPane = new AnchorPane();
        for (int i = 0; i < 5; i++) {
            Path star = new Path();
            star = star.createStar(size, i < rating ? true : false);
            star.setLayoutY(10);
            star.setLayoutX(10 + size * i);
            anchorPane.getChildren().add(star);
            int index = i;
            if(bookDetailController != null && isEditable) {
                star.setOnMousePressed(
                        e -> bookDetailController.setRating(index + 1, isEditable));
                star.setOnMouseEntered(
                        e -> bookDetailController.changeRatingBar(index + 1));
            }
        }
        return anchorPane;
    }

    /**
     * Creates an {@link AnchorPane} displaying information about a borrow transaction.
     * The display includes details such as the user's ID, username, borrow date, return date,
     * and the current status of the borrow.
     * This method creates a grid layout to organize and display these details in a tabular form.
     *
     * @param borrow The {@link Borrow} object that contains the details to be displayed.
     *              It includes the user information, borrow dates, and the borrow status.
     * @return An {@link AnchorPane} containing a {@link GridPane} that displays
     * the borrow details in a structured layout.
     */
    public static AnchorPane createAnchorPaneBorrow(Borrow borrow) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(517, 59);

        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(14);
        gridPane.setLayoutY(11);
        gridPane.setPrefSize(584, 37);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(javafx.geometry.HPos.CENTER);
        column1.setPrefWidth(46.5);
        column1.setMaxWidth(112.2);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(javafx.geometry.HPos.CENTER);
        column2.setPrefWidth(145.5);
        column2.setMaxWidth(174.6);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setHalignment(javafx.geometry.HPos.CENTER);
        column3.setPrefWidth(124);
        column3.setMaxWidth(124);

        ColumnConstraints column4 = new ColumnConstraints();
        column4.setHalignment(javafx.geometry.HPos.CENTER);
        column4.setPrefWidth(124);
        column4.setMaxWidth(124);

        ColumnConstraints column5 = new ColumnConstraints();
        column5.setHalignment(javafx.geometry.HPos.CENTER);
        column5.setPrefWidth(109);
        column5.setMaxWidth(109);

        gridPane.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

        RowConstraints row = new RowConstraints();
        row.setPrefHeight(30);
        gridPane.getRowConstraints().add(row);

        Text text1 = new Text(borrow.getUser().getId().toString());
        text1.setFont(new Font(16));

        Text text2 = new Text(borrow.getUser().getUsername());
        text2.setFont(new Font(16));
        GridPane.setColumnIndex(text2, 1);

        Text text3 = new Text(borrow.getBorrowDate().toString());
        text3.setFont(new Font(16));
        GridPane.setColumnIndex(text3, 2);

        Text text4 = new Text(borrow.getReturnDate().toString());
        text4.setFont(new Font(16));
        GridPane.setColumnIndex(text4, 3);

        Text text5 = new Text(borrow.getStatus());
        text5.setFont(new Font(16));
        GridPane.setColumnIndex(text5, 4);

        gridPane.getChildren().addAll(text1, text2, text3, text4, text5);

        anchorPane.getChildren().add(gridPane);
        return anchorPane;
    }

    /**
     * Creates an {@link AnchorPane} that displays a row of borrow activity information. The row includes
     * details about the book's title, the user's username,
     * the borrow date (or return date if the status is "Returned"),
     * and the status of the borrow activity (e.g., "Borrowing", "Returned", "Overdue", "Paid").
     * This method uses a {@link GridPane} to arrange these details in a structured layout.
     *
     * @param borrow The {@link Borrow} object containing the details of the borrowing activity.
     *              This includes the book information, user information, borrow dates,
     *              and the current status of the borrow.
     * @return An {@link AnchorPane} containing a {@link GridPane}
     * that displays the borrow activity details
     *         in a row format, with each detail positioned in a separate column.
     */
    public static AnchorPane createAnchorPaneListActivities(Borrow borrow) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(33);
        anchorPane.setPrefWidth(531);
        anchorPane.getStyleClass().add("activity-row");

        // Tạo GridPane
        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(18);
        gridPane.setLayoutY(1);
        gridPane.setPrefHeight(35);
        gridPane.setPrefWidth(524);

        // Cấu hình cột
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(205.4);
        col1.setMaxWidth(238.8);
        col1.setMinWidth(10);
        gridPane.getColumnConstraints().add(col1);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(119);
        col2.setMaxWidth(171.8);
        col2.setMinWidth(10);
        gridPane.getColumnConstraints().add(col2);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPrefWidth(104.6);
        col3.setMaxWidth(131.4);
        col3.setMinWidth(10);
        gridPane.getColumnConstraints().add(col3);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPrefWidth(95.6);
        col4.setMaxWidth(129.2);
        col4.setMinWidth(10);
        gridPane.getColumnConstraints().add(col4);

        // Cấu hình hàng
        RowConstraints row1 = new RowConstraints();
        row1.setMinHeight(10);
        row1.setPrefHeight(30);
        gridPane.getRowConstraints().add(row1);

        // Thêm các Text vào GridPane
        String borrowStatus = borrow.getStatus();

        Text titleText = new Text(borrow.getBook().getTitle().length() < 20
                ? borrow.getBook().getTitle(): borrow.getBook().getTitle().substring(0,20) + "...");
        titleText.setFill(Color.web("#333333"));
        titleText.setStyle("-fx-font-weight:bold;");

        Text authorText = new Text(borrow.getUser().getUsername().length() < 10
                ? borrow.getUser().getUsername() : borrow.getUser().getUsername().substring(0,10));
        authorText.setFill(Color.web("#333333"));

        Text dateText = new Text(borrowStatus.equals("Borrowing")
                ? borrow.getBorrowDate().toString() : borrow.getReturnDate().toString());
        dateText.setFill(Color.web("#333333"));

        VBox statusBox = new VBox();
        statusBox.setAlignment(javafx.geometry.Pos.CENTER);

        Text statusText = new Text(borrow.getStatus());
        statusText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        switch (borrowStatus) {
            case "Borrowing":
                statusBox.getStyleClass().add("status-borrow-box");
                statusText.getStyleClass().add("status-borrow-text");
                break;

            case "Returned":
                statusBox.getStyleClass().add("status-return-box");
                statusText.getStyleClass().add("status-return-text");
                break;

            case "Overdue":
                statusBox.getStyleClass().add("status-overdue-box");
                statusText.getStyleClass().add("status-overdue-text");
                break;

            case "Paid":
                statusBox.getStyleClass().add("status-paid-box");
                statusText.getStyleClass().add("status-paid-text");
                break;

            default:
                statusBox.getStyleClass().add("status-default-box");
                statusText.getStyleClass().add("status-default-text");
                break;
        }

        statusBox.getChildren().add(statusText);

        gridPane.add(titleText, 0, 0);
        gridPane.add(authorText, 1, 0);
        gridPane.add(dateText, 2, 0);
        gridPane.add(statusBox, 3, 0);

        anchorPane.getChildren().add(gridPane);

        return anchorPane;
    }

    /**
     * Creates an {@link AnchorPane} with a loading animation consisting of multiple small circles that
     * scale in and out repeatedly to create a dynamic loading effect. A label with the text "Loading"
     * is also displayed at the center of the animation.
     *
     * The animation consists of a series of circles arranged in a circular pattern around a central point.
     * Each circle scales between two sizes, creating the effect of loading. The animation is infinite,
     * with each circle animating sequentially in a loop.
     *
     * @param pos_x The x-coordinate for the center of the loading animation.
     * @param pos_y The y-coordinate for the center of the loading animation.
     * @return An {@link AnchorPane} containing the loading animation and a label with the text "Loading".
     */
    public static AnchorPane createAnChorPaneLoading(double pos_x, double pos_y) {
        AnchorPane root = new AnchorPane();
        Label label = new Label("Loading");
        label.setLayoutX(-22);
        label.setLayoutY(-8);
        label.setTextFill(Color.web("#8370BC"));
        root.getChildren().add(label);
        double centerX = pos_x;
        double centerY = pos_y;
        double radius = 30;
        int numberOfCircles = 40;
        List<Circle> smallCircle = new ArrayList<>();
        List<ScaleTransition> listeffect = new ArrayList<>();
        for (int i = 0; i < numberOfCircles; i++) {
            double angle = 2 * Math.PI * i / numberOfCircles;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            Circle circle = new Circle(x, y, 2, Color.web("#5f43b2"));
            smallCircle.add(circle);
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1),circle);
            scaleTransition.setFromX(1);
            scaleTransition.setFromY(1);
            scaleTransition.setToX(0.5);
            scaleTransition.setToY(0.5);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
            listeffect.add(scaleTransition);
        }
        for(Circle circle : smallCircle) root.getChildren().add(circle);

        Timeline timeline = new Timeline();

        for (int i = 0; i < listeffect.size(); i++) {
            ScaleTransition effect = listeffect.get(i);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i/7), event -> effect.play());
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.play();
        return root;
    }
}
