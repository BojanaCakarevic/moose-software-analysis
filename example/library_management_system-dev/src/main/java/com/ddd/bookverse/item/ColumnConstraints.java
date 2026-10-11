package com.ddd.bookverse.item;

import java.time.Duration;

public class ColumnConstraints extends javafx.scene.layout.ColumnConstraints{
    public ColumnConstraints() {}
    public ColumnConstraints(Double PrefWidth, Double MinWidth) {
        this.setMinWidth(MinWidth);
        this.setPrefWidth(PrefWidth);
    }

    public ColumnConstraints(Double PrefWidth) {
        this.setPrefWidth(PrefWidth);
    }
}
