package com.ddd.bookverse.item;

import javafx.scene.paint.Color;

public class Text extends javafx.scene.text.Text {
    public Text(String text, String color, int font) {
        this.setText(text);
        this.setFill(Color.web(color));
        this.setFont(javafx.scene.text.Font.font(18));
    }

    public Text(String text) {
        this.setText(text);
    }

}
