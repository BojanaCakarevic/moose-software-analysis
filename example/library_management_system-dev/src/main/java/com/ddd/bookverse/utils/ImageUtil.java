package com.ddd.bookverse.utils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.ddd.bookverse.constants.Constants.URL_NO_IMG;
import static javafx.application.Application.launch;

/**
 * Utility class for handling image-related operations.
 */
public class ImageUtil extends Application {

    /**
     * Downloads an image from the specified URL and returns it as a byte array.
     *
     * @param imageUrl The URL of the image to download.
     * @return A byte array containing the image data, or null if the download fails.
     */
    public static byte[] downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            loadImageFromResources(URL_NO_IMG);
            return null;
        }
    }

    /**
     * Loads an image from the specified resource path and returns it as a byte array.
     *
     * @param resourcePath The path to the resource.
     * @return A byte array containing the image data, or null if the resource is not found.
     */
    public static byte[] loadImageFromResources(String resourcePath) {
        try (InputStream inputStream = ImageUtil.class.getResourceAsStream(resourcePath);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            if (inputStream == null) {
                System.err.println("Resource not found: " + resourcePath);
                return null;
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Displays an image from the specified URL in a new window.
     *
     * @param imageUrl The URL of the image to display.
     */
    public void displayImage(String imageUrl) {
        byte[] imageBytes = downloadImage(imageUrl);
        if (imageBytes != null) {
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(200);
            imageView.setFitHeight(300);
            imageView.setPreserveRatio(true);

            StackPane root = new StackPane();
            root.getChildren().add(imageView);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 300, 400));
            stage.setTitle("Image Display");
            stage.show();
        } else {
            System.out.println("Failed to download image.");
        }
    }

    /**
     * The main entry point for JavaFX applications.
     *
     * @param primaryStage The primary stage for this application.
     */
    public void start(Stage primaryStage) {
        // Example image URL
        Long start = System.currentTimeMillis();
        String imageUrl = "http://books.google.com/books/content?id=AiH4zgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api";
        displayImage(imageUrl);
        Long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * Converts a BufferedImage to a WritableImage.
     *
     * @param bufferedImage The BufferedImage to convert.
     * @return The converted WritableImage.
     */
    public static WritableImage convertBufferedImageToWritableImage(BufferedImage bufferedImage) {
        // Create a WritableImage with the same width and height as the BufferedImage
        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // Loop through all the pixels in the BufferedImage
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                // Get the ARGB value of the pixel (which includes alpha, red, green, and blue)
                int argb = bufferedImage.getRGB(x, y);

                // Write the ARGB pixel into the WritableImage
                pixelWriter.setArgb(x, y, argb);
            }
        }

        return writableImage;  // Return the WritableImage (JavaFX Image)
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}