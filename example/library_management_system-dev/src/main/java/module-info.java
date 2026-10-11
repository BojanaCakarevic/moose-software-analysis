module com.ddd.bookverse {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires com.google.api.client.auth;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.checkerframework.checker.qual;
    // add icon pack modules

    opens com.ddd.bookverse to javafx.fxml;
    opens com.ddd.bookverse.controller to javafx.fxml;
    opens com.ddd.bookverse.model to javafx.fxml;

    exports com.ddd.bookverse;
    exports com.ddd.bookverse.controller;
    exports com.ddd.bookverse.model;
    exports com.ddd.bookverse.utils;
}