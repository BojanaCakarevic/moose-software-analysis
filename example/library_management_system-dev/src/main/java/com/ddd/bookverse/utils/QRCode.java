package com.ddd.bookverse.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for creating and reading QR codes.
 */
public class QRCode {

    /**
     * Creates a QR code image from the given data and returns it as a JavaFX Image.
     *
     * @param data The data to encode in the QR code.
     * @param width The width of the QR code.
     * @param height The height of the QR code.
     * @return The generated QR code as a JavaFX Image, or null if an error occurs.
     */
    public static Image createQR(String data, int width, int height) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
            Path path = FileSystems.getDefault().getPath("src/main/resources/qr-codes/qrcode.png");
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);
            return new Image(Files.newInputStream(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads a QR code from the specified file path and returns the decoded text.
     *
     * @param path The file path of the QR code image.
     * @return The decoded text from the QR code.
     * @throws IOException If an I/O error occurs.
     * @throws NotFoundException If the QR code is not found or cannot be decoded.
     */
    public static String readQR(String path) throws IOException, NotFoundException {
        File qrFile = new File(path);
        BufferedImage bufferedImage = ImageIO.read(qrFile);

        LuminanceSource src = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(src));

        Result result = new MultiFormatReader().decode(binaryBitmap);
        return result.getText();
    }
}