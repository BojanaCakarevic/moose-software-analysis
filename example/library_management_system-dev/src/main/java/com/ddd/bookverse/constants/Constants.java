package com.ddd.bookverse.constants;

public class Constants {
    // DATABASE
    public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/library_db";
    public static final String DATABASE_USER = "root";
    public static final String DATABASE_PASSWORD = "88888888";

    //API
    public static final String API_KEY = "AIzaSyDFe1Qi2YHV04Zb29qjzJjUmTZXXTpjy9E";
    public static final String GOOG_BOOK_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    public static final String URL_NO_IMG = "/img/no_image.jpg";

    // APP
    public static final String APP_NAME = "Bookverse";
    public static final int APP_HEIGHT = 800;
    public static final int APP_WIDTH = 1200;
    public static final String PRIMARY_COLOR = "#5f43b2";


    //URL
    public static final String LOGIN_URL = "/fxml/login.fxml";
    public static final String SIGNUP_URL = "/fxml/signup.fxml";
    public static final String RIGHTPANEL_URL = "/fxml/rightpanel.fxml";

    public static final String INFO_URL = "/fxml/info.fxml";
    public static final String DASHBOARD_URL =  "/fxml/dashboard.fxml";
    public static final String BOOK_LIST_URL = "/fxml/booklist.fxml";
    public static final String BOOK_DETAILS_URL = "/fxml/details.fxml";
    public static final String MY_BOOK_URL = "/fxml/mybook.fxml";
    public static final String MY_ACCOUNT_URL = "/fxml/myaccount.fxml";

    public static final String DASHBOARD_ADMIN_URL =  "/fxml/dashboardadmin.fxml";
    public static final String BOOK_MANAGEMENT_URL = "/fxml/bookmanagement.fxml";
    public static final String BOOK_DETAILS_ADMIN_URL = "/fxml/bookdetailsadmin.fxml";
    public static final String INFO_URL_ADMIN = "/fxml/infoadmin.fxml";
    public static final String ACCOUNT_MANAGEMENT_URL = "/fxml/accountmanagement.fxml";
    public static final String MY_ACCOUNT_ADMIN_URL = "/fxml/myaccountadmin.fxml";

    // EXCEPTION
    public static final String EXCEPTION_CALL_API = "API call failed.";
    public static final String EXCEPTION_BOOK_NOT_FOUND = "Book not found.";
    public static final String EXCEPTION_BORROW_NOT_FOUND = "Borrow not found.";
    public static final String EXCEPTION_ILLEGALPAGESREAD
            = "Pages read must be greater than 0 and less than or equal to the total number of pages";
    public static final String EXCEPTION_OVER_DUE_BOOK = "Book is overdue.";
    public static final String EXCEPTION_REVIEW_NOT_FOUND = "Review not found.";
    public static final String EXCEPTION_USER_NOT_FOUND = "User not found.";
    public static final String EXCEPTION_BORROW_STATUS = "Borrow status is invalid.";
    public static final String EXCEPTION_DATABASE_OPERATION  = "Database operation failed.";

}
