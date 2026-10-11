package com.ddd.bookverse.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for validating various inputs such as email, username, password, ISBN, image URL, and date.
 */
public class ValidationUtil {
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    private static final String ISBN_REGEX = "^.{11,}$";
    private static final String IMAGE_URL_REGEX = "^(http(s)?://)?([\\w.-]+)\\.([a-z]{2,6})(/[\\w.-]*)*\\.(jpg|jpeg|png|gif|bmp|webp)$";

    /**
     * Validates if the given email is in a valid format.
     *
     * @param email The email to validate.
     * @return true if the email is valid, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * Validates if the given password is in a valid format.
     *
     * @param password The password to validate.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return Pattern.matches(PASSWORD_REGEX, password);
    }

    /**
     * Validates if the given username is in a valid format.
     *
     * @param username The username to validate.
     * @return true if the username is valid, false otherwise.
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return Pattern.matches(USERNAME_REGEX, username);
    }

    /**
     * Validates if the given ISBN is in a valid format.
     *
     * @param isbn The ISBN to validate.
     * @return true if the ISBN is valid, false otherwise.
     */
    public static boolean isValidISBN(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            return false;
        }
        return Pattern.matches(ISBN_REGEX, isbn);
    }

    /**
     * Validates if the given image URL is in a valid format.
     *
     * @param url The image URL to validate.
     * @return true if the image URL is valid, false otherwise.
     */
    public static boolean isValidImageURL(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return Pattern.matches(IMAGE_URL_REGEX, url);
    }

    /**
     * Validates if the given date is in a valid format.
     *
     * @param date The date to validate.
     * @return true if the date is valid, false otherwise.
     */
    public static boolean isValidDate(String date) {
        try {
            if (date.matches("^\\d{4}$")) {
                int year = Integer.parseInt(date);
                return year >= 1 && year <= 9999;
            } else if (date.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
                LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
                return true;
            } else {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Main method for testing the validation methods.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        System.out.println(isValidDate("2023-11-28")); // true
        System.out.println(isValidDate("2023"));       // true
        System.out.println(isValidDate("2023-02-30")); // false (invalid day)
        System.out.println(isValidDate("abcd"));       // false
        System.out.println(isValidDate("2023-13-01")); // false (invalid month)
    }
}