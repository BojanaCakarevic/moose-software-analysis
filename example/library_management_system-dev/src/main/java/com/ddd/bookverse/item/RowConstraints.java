package com.ddd.bookverse.item;

public class RowConstraints extends javafx.scene.layout.RowConstraints {
    public RowConstraints(){};
    public RowConstraints(Double PrefHeight, Double MaxHeight, Double MinHeight) {
        this.setMaxHeight(MaxHeight);
        this.setMinHeight(MinHeight);
        this.setPrefHeight(PrefHeight);
    }
    public RowConstraints(double PrefHeight) {
        this.setPrefHeight(PrefHeight);
    }
}
