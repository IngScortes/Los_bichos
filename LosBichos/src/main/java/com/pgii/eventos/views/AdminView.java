package com.pgii.eventos.views;

import javafx.scene.layout.VBox;

public class AdminView {
    private VBox root;

    public AdminView() {
        root = new VBox();
        root.setStyle("-fx-padding: 20;");
        // Aquí implementarás gestión de eventos, usuarios, etc.
    }

    public VBox getRoot() { return root; }
}