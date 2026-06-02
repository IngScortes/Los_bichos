package com.pgii.eventos.app;

import com.pgii.eventos.views.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);

        AnchorPane root = new AnchorPane();
        root.getChildren().add(loginView.getRoot());
        AnchorPane.setTopAnchor(loginView.getRoot(), 0.0);
        AnchorPane.setBottomAnchor(loginView.getRoot(), 0.0);
        AnchorPane.setLeftAnchor(loginView.getRoot(), 0.0);
        AnchorPane.setRightAnchor(loginView.getRoot(), 0.0);

        Scene scene = new Scene(root, 900, 600);

        // ========== FORZAR CARGA DEL CSS ==========
        String css = getClass().getResource("/styles.css").toExternalForm();
        if (css != null) {
            scene.getStylesheets().add(css);
            System.out.println("✅ CSS cargado: " + css);
        } else {
            System.out.println("❌ CSS NO encontrado en /styles.css");
        }
        // ==========================================

        primaryStage.setTitle("Plataforma de Gestión");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}