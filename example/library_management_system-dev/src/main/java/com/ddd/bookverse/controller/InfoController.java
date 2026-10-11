package com.ddd.bookverse.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static com.ddd.bookverse.item.AnchorPane.createAnChorPaneLoading;
import static com.ddd.bookverse.utils.NavHelper.toSignUp;

/**
 * The {@code InfoController} class implements the
 * {@link UiController} interface and is responsible
 * for managing the UI logic related to displaying information within the application.
 * This class handles interactions with the user interface
 * and updates the UI based on the application's state.
 */
public class InfoController implements UiController {
    @FXML private Pane pane_donate;
    @FXML private Button button_donate;
    @FXML private Button button_close;
    @FXML private AnchorPane loading;

    public void initialize() {
        initUI();
        clickButton();
    }
    /**
     * Initializes the UI by setting the initial visibility of the
     * donation pane to false and ensuring that the loading indicator is hidden.
     * It also adds a loading pane for future use.
     */
    @Override
    public void initUI() {
        pane_donate.setVisible(false);
        setLoading(false);
        loading.getChildren().add(createAnChorPaneLoading(0,0));
    }

    /**
     * Sets up the button click event handlers.
     * When the user clicks the donate button, the donation pane
     * becomes visible. When the user clicks the close button, the donation pane is hidden.
     */
    @Override
    public void clickButton() {
        button_donate.setOnMouseClicked(e->{pane_donate.setVisible(true);});
        button_close.setOnMouseClicked(e->{pane_donate.setVisible(false);});
    }

    /**
     * Controls the visibility of the loading indicator. When the isVisible parameter is true,
     * the loading pane is displayed. Otherwise, the loading pane is hidden.
     *
     * @param isVisible A boolean indicating whether the loading indicator should be shown or hidden.
     */
    @Override
    public void setLoading(boolean isVisible) {
        if(loading != null) loading.setVisible(isVisible);
    }

}
