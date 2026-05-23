package com.pgii.eventos.app;

import com.pgii.eventos.views.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);
        Scene scene = new Scene(loginView.getRoot(), 900, 600);

        // ========== AGREGAR CSS AQUÍ ==========
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            System.out.println("✅ CSS cargado correctamente");
        } catch (Exception e) {
            System.out.println("❌ CSS no encontrado: " + e.getMessage());
        }
        // ======================================

        primaryStage.setTitle("Plataforma de Gestión");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}