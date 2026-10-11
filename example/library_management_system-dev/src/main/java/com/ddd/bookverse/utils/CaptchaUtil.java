package com.ddd.bookverse.utils;

import javafx.scene.image.Image;
import javafx.util.Pair;

import java.io.File;
import java.util.Objects;

/**
 * Utility class for handling captcha-related operations.
 */
public class CaptchaUtil {

    /**
     * Retrieves a random captcha image and its corresponding text from the specified directory.
     *
     * @return A Pair containing the captcha Image and its corresponding text.
     */
    public static Pair<Image, String> getRandomCaptcha() {
        String path = "src/main/resources/captcha";
        File dir = new File(path);
        File captcha = null;
        Image captchaImage = null;
        String captchaText = null;

        if (dir.exists()) {
            int dirSize = dir.listFiles().length;
            int randomIndex = (int) (Math.random() * dirSize);
            captcha = Objects.requireNonNull(dir.listFiles())[randomIndex];
            captchaImage = new Image(captcha.toURI().toString());
            captchaText = captcha.getName().replace(".jpg", "");
        } else {
            System.out.println("Directory not found");
        }
        return new Pair<>(captchaImage, captchaText);
    }
}