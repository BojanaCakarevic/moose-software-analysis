package com.ddd.bookverse.controller;

/**
 * Interface defining the standard behavior for controllers managing user interface (UI) components.
 * This interface provides methods for initializing the UI, handling button click actions,
 * and managing loading state visibility.
 */
public interface UiController {

    /**
     * Initializes the user interface components.
     * This method is used to set up the layout, styles, animations, or any other UI-related elements.
     */
    void initUI();

    /**
     * Configures button click actions within the user interface.
     * This method binds event handlers to buttons and other interactive elements.
     */
    void clickButton();

    /**
     * Sets the visibility of a loading indicator in the UI.
     *
     * @param isVisible a boolean indicating whether the loading indicator should be visible.
     *                  {@code true} to make the loading indicator visible, {@code false} to hide it.
     */
    void setLoading(boolean isVisible);
}
