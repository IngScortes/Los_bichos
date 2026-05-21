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

        // Opcional: cargar CSS (comenta si no tienes el archivo)
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, continuando sin estilos");
        }

        primaryStage.setTitle("Los Bichos - Plataforma de Eventos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}