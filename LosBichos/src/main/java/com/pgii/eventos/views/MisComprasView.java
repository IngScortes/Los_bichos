package com.pgii.eventos.views;

import javafx.scene.layout.VBox;

public class MisComprasView {
    private VBox root;

    public MisComprasView() {
        root = new VBox();
        root.setStyle("-fx-padding: 20;");
        // Aquí implementarás el historial de compras
    }

    public VBox getRoot() { return root; }
}