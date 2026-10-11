package com.ddd.bookverse.item;

public class TextField extends javafx.scene.control.TextField{
    public TextField() {

    }
    public TextField(String text, double LayoutX, double LayoutY, double PrefWidth, double PrefHeight, String style){
        this.setVisible(true);
        this.setLayoutX(LayoutX);
        this.setLayoutY(LayoutY);
        this.setPrefSize(PrefWidth,PrefHeight);
        this.setStyle("-fx-border-color:transparent;-fx-background-color:transparent;-fx-font-size:16");
    }

}
