package com.pgii.eventos.views;

import javafx.scene.layout.VBox;

public class EventosView {
    private VBox root;

    public EventosView() {
        root = new VBox();
        root.setStyle("-fx-padding: 20;");
        // Aquí implementarás la lista de eventos y compra
    }

    public VBox getRoot() { return root; }
}