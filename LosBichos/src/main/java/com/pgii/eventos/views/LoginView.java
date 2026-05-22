package com.pgii.eventos.views;

import com.pgii.eventos.app.DataInitializer;
import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.GestorSesion;
import com.pgii.eventos.service.UsuarioService;
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
    private UsuarioService usuarioService;
    private AdministradorRepository adminRepository;
    private UsuarioRepository usuarioRepository;

    // Mapa de credenciales válidas (email + contraseña)
    private Map<String, String> credencialesValidas;

    public LoginView(Stage stage) {
        this.stage = stage;
        inicializarServicios();
        cargarDatosIniciales();
        asegurarAdminExistente();
        inicializarCredenciales();
        crearUI();
    }

    private void inicializarServicios() {
        this.usuarioRepository = new UsuarioRepository();
        this.usuarioService = new UsuarioService(usuarioRepository);
        this.adminRepository = new AdministradorRepository();
    }

    private void cargarDatosIniciales() {
        RecintoRepository recintoRepo = new RecintoRepository();
        ZonaRepository zonaRepo = new ZonaRepository();
        AsientoRepository asientoRepo = new AsientoRepository();
        EventoRepository eventoRepo = new EventoRepository();
        UsuarioRepository usuarioRepo = new UsuarioRepository();
        AdministradorRepository adminRepo = new AdministradorRepository();
        CompraRepository compraRepo = new CompraRepository();

        DataInitializer.inicializar(recintoRepo, zonaRepo, asientoRepo, eventoRepo,
                usuarioRepo, adminRepo, compraRepo);
    }

    private void asegurarAdminExistente() {
        // Verificar si ya existe un administrador
        if (adminRepository.findAll().isEmpty()) {
            Administrador admin = new Administrador("AD001", "Administrador", "admin@eventos.com", "600555666");
            adminRepository.save(admin);
            System.out.println("✅ Administrador creado manualmente");
        } else {
            System.out.println("✅ Administrador ya existe");
        }

        // Mostrar todos los admins para depuración
        System.out.println("=== ADMINISTRADORES EN REPOSITORIO ===");
        for (Administrador a : adminRepository.findAll()) {
            System.out.println("Admin: " + a.getEmail() + " - " + a.getNombreCompleto());
        }
    }

    private void inicializarCredenciales() {
        credencialesValidas = new HashMap<>();
        // Usuarios con sus contraseñas
        credencialesValidas.put("juan@mail.com", "123456");
        credencialesValidas.put("maria@mail.com", "maria123");
        credencialesValidas.put("carlos@mail.com", "carlos123");
        credencialesValidas.put("admin@eventos.com", "admin123");
    }

    private void crearUI() {
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Panel principal con efecto glassmorphism
        VBox mainPanel = new VBox(25);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setMaxWidth(450);
        mainPanel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                        "-fx-background-radius: 30px;" +
                        "-fx-padding: 40px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 10);"
        );

        // Logo o ícono
        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER);

        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-font-size: 60px;");

        Label titulo = new Label("Plataforma de Gestión");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label subtitulo = new Label("Proyecto Final");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

        logoBox.getChildren().addAll(iconLabel, titulo, subtitulo);

        // Separador
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");

        // Formulario
        VBox formBox = new VBox(15);

        Label lblEmail = new Label("Correo Electrónico");
        lblEmail.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("usuario@ejemplo.com");
        txtEmail.setText("admin@eventos.com");
        txtEmail.getStyleClass().add("text-field");

        Label lblPassword = new Label("Contraseña");
        lblPassword.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Ingrese su contraseña");
        txtPassword.setText("admin123");

        // Botón de login con animación
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

        // Efecto hover
        btnLogin.setOnMouseEntered(e -> {
            btnLogin.setScaleX(1.02);
            btnLogin.setScaleY(1.02);
        });
        btnLogin.setOnMouseExited(e -> {
            btnLogin.setScaleX(1);
            btnLogin.setScaleY(1);
        });

        // Acción del botón login - VERSIÓN CORREGIDA
        btnLogin.setOnAction(e -> {
            String email = txtEmail.getText();
            String password = txtPassword.getText();

            System.out.println("=== INTENTO DE LOGIN ===");
            System.out.println("Email: " + email);

            // Validar credenciales
            if (!validarCredenciales(email, password)) {
                mostrarAlerta("Error de Autenticación", "Correo o contraseña incorrectos\n\nCredenciales:\nadmin@eventos.com / admin123");
                return;
            }

            Persona usuario = null;

            // 1. Buscar en administradores
            System.out.println("Buscando en Administradores...");
            for (Administrador admin : adminRepository.findAll()) {
                System.out.println(" - Admin: " + admin.getEmail());
                if (admin.getEmail().equals(email)) {
                    usuario = admin;
                    System.out.println("✅ Admin encontrado: " + admin.getNombreCompleto());
                    break;
                }
            }

            // 2. Buscar en usuarios normales
            if (usuario == null) {
                System.out.println("Buscando en Usuarios normales...");
                for (Usuario u : usuarioService.listarTodos()) {
                    System.out.println(" - Usuario: " + u.getEmail());
                    if (u.getEmail().equals(email)) {
                        usuario = u;
                        System.out.println("✅ Usuario encontrado: " + u.getNombreCompleto());
                        break;
                    }
                }
            }

            if (usuario != null) {
                GestorSesion.getInstance().setUsuarioActivo(usuario);
                System.out.println("✅ Login exitoso: " + usuario.getNombreCompleto());
                System.out.println("Tipo: " + usuario.getClass().getSimpleName());
                System.out.println("¿Es admin? " + (usuario instanceof Administrador));
                mostrarDashboard();
            } else {
                System.out.println("❌ Usuario NO encontrado en ningún repositorio");
                mostrarAlerta("Error", "Usuario no encontrado en el sistema.\nContacte al administrador.");
            }
        });

        // Enlace de registro
        HBox registroBox = new HBox(5);
        registroBox.setAlignment(Pos.CENTER);
        Label lblRegistro = new Label("¿No tienes cuenta?");
        lblRegistro.setStyle("-fx-text-fill: #888;");
        Hyperlink linkRegistro = new Hyperlink("Regístrate aquí");
        linkRegistro.setStyle("-fx-text-fill: #667eea;");
        registroBox.getChildren().addAll(lblRegistro, linkRegistro);

        formBox.getChildren().addAll(lblEmail, txtEmail, lblPassword, txtPassword, btnLogin, registroBox);

        mainPanel.getChildren().addAll(logoBox, separator, formBox);

        // Animación de entrada
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

    private boolean validarCredenciales(String email, String password) {
        if (email == null || password == null) return false;
        String passValida = credencialesValidas.get(email);
        return passValida != null && passValida.equals(password);
    }

    private void mostrarDashboard() {
        try {
            System.out.println("=== CARGANDO DASHBOARD ===");
            DashboardView dashboard = new DashboardView(stage);
            System.out.println("DashboardView creado correctamente");

            Scene scene = new Scene(dashboard.getRoot(), 1300, 800);
            stage.setScene(scene);
            stage.setTitle("Plataforma de Gestión - Dashboard");
            System.out.println("Dashboard cargado exitosamente");

        } catch (Exception e) {
            System.out.println("ERROR al cargar Dashboard: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el Dashboard: " + e.getMessage());
        }
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