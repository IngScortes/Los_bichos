package com.pgii.eventos.views;

import com.pgii.eventos.model.Usuario;
import com.pgii.eventos.service.GestorSesion;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class LoginView {
    private Stage stage;
    private VBox root;

    // Mapa de credenciales y usuarios simulados
    private Map<String, String> credencialesValidas;
    private Map<String, Usuario> usuariosPorEmail;

    public LoginView(Stage stage) {
        this.stage = stage;
        inicializarCredencialesYUsuarios();
        crearUI();
    }

    private void inicializarCredencialesYUsuarios() {
        credencialesValidas = new HashMap<>();
        usuariosPorEmail = new HashMap<>();

        // Usuarios simulados con sus contraseñas
        Usuario juan = new Usuario("U001", "Juan Pérez", "juan@mail.com", "300111222");
        Usuario maria = new Usuario("U002", "María Gómez", "maria@mail.com", "310333444");
        Usuario carlos = new Usuario("U003", "Carlos López", "carlos@mail.com", "320555666");
        Usuario admin = new Usuario("AD001", "Administrador", "admin@eventos.com", "600555666");

        credencialesValidas.put("juan@mail.com", "123456");
        credencialesValidas.put("maria@mail.com", "maria123");
        credencialesValidas.put("carlos@mail.com", "carlos123");
        credencialesValidas.put("admin@eventos.com", "admin123");

        usuariosPorEmail.put("juan@mail.com", juan);
        usuariosPorEmail.put("maria@mail.com", maria);
        usuariosPorEmail.put("carlos@mail.com", carlos);
        usuariosPorEmail.put("admin@eventos.com", admin);
    }

    private void crearUI() {
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        VBox mainPanel = new VBox(25);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setMaxWidth(450);
        mainPanel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                        "-fx-background-radius: 30px;" +
                        "-fx-padding: 40px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 10);"
        );

        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER);

        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-font-size: 60px;");

        Label titulo = new Label("Plataforma de Gestión");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label subtitulo = new Label("Proyecto Final");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

        logoBox.getChildren().addAll(iconLabel, titulo, subtitulo);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");

        VBox formBox = new VBox(15);

        Label lblEmail = new Label("Correo Electrónico");
        lblEmail.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("usuario@ejemplo.com");
        txtEmail.setText("juan@mail.com");

        Label lblPassword = new Label("Contraseña");
        lblPassword.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Ingrese su contraseña");
        txtPassword.setText("123456");

        Button btnLogin = new Button("Iniciar Sesión");
        btnLogin.setStyle(
                "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12px;" +
                        "-fx-background-radius: 25px;" +
                        "-fx-cursor: hand;"
        );

        btnLogin.setOnMouseEntered(e -> {
            btnLogin.setScaleX(1.02);
            btnLogin.setScaleY(1.02);
        });
        btnLogin.setOnMouseExited(e -> {
            btnLogin.setScaleX(1);
            btnLogin.setScaleY(1);
        });

        btnLogin.setOnAction(e -> {
            String email = txtEmail.getText();
            String password = txtPassword.getText();

            String passValida = credencialesValidas.get(email);
            if (passValida != null && passValida.equals(password)) {
                Usuario usuario = usuariosPorEmail.get(email);
                if (usuario != null) {
                    GestorSesion.getInstance().setUsuarioActivo(usuario);
                    System.out.println("✅ Login exitoso: " + usuario.getNombreCompleto());
                    mostrarDashboard();
                } else {
                    mostrarAlerta("Error", "Usuario no encontrado");
                }
            } else {
                mostrarAlerta("Error de Autenticación", "Correo o contraseña incorrectos\n\nCredenciales de prueba:\njuan@mail.com / 123456");
            }
        });

        HBox registroBox = new HBox(5);
        registroBox.setAlignment(Pos.CENTER);
        Label lblRegistro = new Label("¿No tienes cuenta?");
        lblRegistro.setStyle("-fx-text-fill: #888;");
        Hyperlink linkRegistro = new Hyperlink("Regístrate aquí");
        linkRegistro.setStyle("-fx-text-fill: #667eea;");
        registroBox.getChildren().addAll(lblRegistro, linkRegistro);

        formBox.getChildren().addAll(lblEmail, txtEmail, lblPassword, txtPassword, btnLogin, registroBox);

        mainPanel.getChildren().addAll(logoBox, separator, formBox);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), mainPanel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.5), mainPanel);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        scaleIn.play();

        root.getChildren().add(mainPanel);
    }

    private void mostrarDashboard() {
        DashboardView dashboard = new DashboardView(stage);
        Scene scene = new Scene(dashboard.getRoot(), 1300, 800);
        stage.setScene(scene);
        stage.setTitle("Plataforma de Gestión - Dashboard");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getDialogPane().setStyle("-fx-background-radius: 15px;");
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}