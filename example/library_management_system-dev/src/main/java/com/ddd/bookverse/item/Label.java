package com.ddd.bookverse.item;

import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Label extends javafx.scene.control.Label{
    Label(String text, String textcolor, String stytle) {
        this.setText(text);
        this.setTextFill(Color.web(textcolor));
        this.setTextAlignment(TextAlignment.CENTER);
        this.setStyle(stytle);
    }
    Label(String text) {
        this.setText(text);
    }

}
