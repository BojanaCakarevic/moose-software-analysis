package com.ddd.bookverse.item;

import javafx.scene.paint.Color;

/**
 * A custom {@link javafx.scene.shape.Path} class that creates a star shape
 * with the ability to change its color based on selection status.
 * This class extends the {@link javafx.scene.shape.Path}
 * to provide a method to draw a star shape of a given size
 * and fill it with a chosen color if selected, or default white if not selected.
 */
public class Path extends javafx.scene.shape.Path {

    /**
     * Creates a star shape with the given size and color based on the selection status.
     * The size of the star is scaled based on the provided `size` parameter, and the fill color
     * will be yellow if the star is chosen, or white if not chosen.
     *
     * @param size The size of the star. The actual size is scaled down by a factor of 16.
     * @param isChosen A boolean indicating whether the star is selected.
     *                 If true, the star is filled with a yellow color.
     * @return A {@link Path} object representing a star shape.
     */
    public Path createStar(int size, boolean isChosen) {
        Path star = new Path();
        if (isChosen) {
            star.setFill(Color.web("#fcc641"));  // Set the fill color to yellow for chosen star
        } else {
            star.setFill(Color.WHITE);  // Set the fill color to white for non-chosen star
        }
        star.setStroke(Color.web("#fcc641"));  // Set the stroke color to yellow
        star.setStrokeWidth(1);  // Set stroke width

        // Scale the size by a factor of 16
        size /= 16;

        // Define the points of the star shape using a series of move and line commands
        star.getElements().addAll(
                new javafx.scene.shape.MoveTo(8 * size, 1 * size), // Top point
                new javafx.scene.shape.LineTo(11 * size, 7 * size), // Top-right edge
                new javafx.scene.shape.LineTo(16 * size, 7 * size), // Bottom-right edge
                new javafx.scene.shape.LineTo(12 * size, 10 * size), // Bottom edge
                new javafx.scene.shape.LineTo(13 * size, 15 * size), // Bottom point
                new javafx.scene.shape.LineTo(8 * size, 12 * size), // Bottom-left edge
                new javafx.scene.shape.LineTo(3 * size, 15 * size), // Bottom-left point
                new javafx.scene.shape.LineTo(4 * size, 10 * size), // Left edge
                new javafx.scene.shape.LineTo(0 * size, 7 * size), // Top-left edge
                new javafx.scene.shape.LineTo(6 * size, 7 * size), // Top edge
                new javafx.scene.shape.ClosePath() // Close the path to form the star
        );

        return star;
    }
}
