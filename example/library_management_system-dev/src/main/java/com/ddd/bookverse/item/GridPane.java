package com.ddd.bookverse.item;

import javafx.geometry.Pos;

public class GridPane extends javafx.scene.layout.GridPane {
    GridPane() {}

    GridPane(Double PrefWidth, Double PrefHeight) {
        this.setPrefHeight(PrefHeight);
        this.setPrefWidth(PrefWidth);
    }
}
