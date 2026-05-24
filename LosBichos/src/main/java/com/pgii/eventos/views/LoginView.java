package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.GestorSesion;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class LoginView {
    private Stage stage;
    private VBox root;
    private Map<String, String> credenciales;

    private UsuarioRepository usuarioRepository;
    private AdministradorRepository adminRepository;
    private EventoRepository eventoRepository;
    private CompraRepository compraRepository;
    private AsientoRepository asientoRepository;
    private RecintoRepository recintoRepository;
    private ZonaRepository zonaRepository;

    public LoginView(Stage stage) {
        this.stage = stage;

        // ========== INICIALIZAR REPOSITORIOS ==========
        this.usuarioRepository = UsuarioRepository.getInstance();
        this.adminRepository = AdministradorRepository.getInstance();
        this.eventoRepository = EventoRepository.getInstance();
        this.compraRepository = CompraRepository.getInstance();
        this.asientoRepository = AsientoRepository.getInstance();
        this.recintoRepository = RecintoRepository.getInstance();
        this.zonaRepository = ZonaRepository.getInstance();

        inicializarDatos();
        inicializarCredenciales();
        crearUI();
    }

    private void inicializarDatos() {
        UsuarioRepository usuarioRepo = UsuarioRepository.getInstance();
        AdministradorRepository adminRepo = AdministradorRepository.getInstance();
        EventoRepository eventoRepo = EventoRepository.getInstance();
        RecintoRepository recintoRepo = RecintoRepository.getInstance();
        ZonaRepository zonaRepo = ZonaRepository.getInstance();
        AsientoRepository asientoRepo = AsientoRepository.getInstance();
        CompraRepository compraRepo = CompraRepository.getInstance();

        if (adminRepo.findAll().isEmpty()) {
            adminRepo.save(new Administrador("AD001", "Administrador", "admin@eventos.com", "600555666"));
        }

        if (usuarioRepo.findAll().isEmpty()) {
            usuarioRepo.save(new Usuario("U001", "Juan Pérez", "juan@mail.com", "300111222"));
            usuarioRepo.save(new Usuario("U002", "María Gómez", "maria@mail.com", "310333444"));
        }

        Recinto estadio = null;
        if (recintoRepo.findAll().isEmpty()) {
            estadio = new Recinto("R001", "Estadio Centenario", "Calle 30 #20-10", "Armenia");
            Recinto teatro = new Recinto("R002", "Teatro Azul", "Carrera 14 #12-45", "Armenia");
            recintoRepo.save(estadio);
            recintoRepo.save(teatro);
        } else {
            estadio = recintoRepo.findById("R001");
        }

        if (zonaRepo.findAll().isEmpty() && estadio != null) {
            Zona vip = new Zona("Z001", "VIP", 80, 280000, estadio);
            Zona general = new Zona("Z002", "General", 500, 60000, estadio);

            for (int i = 1; i <= 80; i++) {
                Asiento a = new Asiento("VIP_" + i, "V", i, vip);
                vip.agregarAsiento(a);
                asientoRepo.save(a);
            }
            for (int i = 1; i <= 500; i++) {
                Asiento a = new Asiento("GEN_" + i, "G", i, general);
                general.agregarAsiento(a);
                asientoRepo.save(a);
            }

            estadio.agregarZona(vip);
            estadio.agregarZona(general);
            recintoRepo.save(estadio);

            zonaRepo.save(vip);
            zonaRepo.save(general);
        }

        if (eventoRepo.findAll().isEmpty() && estadio != null) {
            Evento evento = new Evento("E001", "Feid Concierto", CategoriaEvento.CONCIERTO,
                    "El artista del momento en concierto", "Armenia",
                    LocalDateTime.of(2025, 7, 20, 20, 0), estadio);
            evento.setEstado(EstadoEvento.PUBLICADO);
            eventoRepo.save(evento);
        }
    }

    private void inicializarCredenciales() {
        credenciales = new HashMap<>();
        // Ya no se usa para validar, solo como respaldo
        credenciales.put("juan@mail.com", "123456");
        credenciales.put("maria@mail.com", "maria123");
        credenciales.put("carlos@mail.com", "carlos123");
        credenciales.put("admin@eventos.com", "admin123");
    }

    private void crearUI() {
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");

        VBox panel = new VBox(25);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(420);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 32px; -fx-padding: 48px 40px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 25, 0, 0, 8);");

        // Logo
        VBox logoBox = new VBox(8);
        logoBox.setAlignment(Pos.CENTER);
        Label iconLabel = new Label("⚡");
        iconLabel.setStyle("-fx-font-size: 52px;");
        Label titulo = new Label("EventFlow");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label subtitulo = new Label("Accede a tu cuenta");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        logoBox.getChildren().addAll(iconLabel, titulo, subtitulo);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e2e8f0;");

        // Formulario
        VBox formBox = new VBox(16);

        Label lblEmail = new Label("Correo electrónico");
        lblEmail.setStyle("-fx-font-weight: 600; -fx-text-fill: #334155; -fx-font-size: 13px;");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("usuario@ejemplo.com");
        txtEmail.setText("juan@mail.com");
        txtEmail.setStyle("-fx-background-radius: 12px; -fx-padding: 12px; -fx-font-size: 14px;");

        Label lblPassword = new Label("Contraseña");
        lblPassword.setStyle("-fx-font-weight: 600; -fx-text-fill: #334155; -fx-font-size: 13px;");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Ingrese su contraseña");
        txtPassword.setText("123456");
        txtPassword.setStyle("-fx-background-radius: 12px; -fx-padding: 12px; -fx-font-size: 14px;");

        Button btnLogin = new Button("Iniciar Sesión");
        btnLogin.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12px; -fx-background-radius: 30px; -fx-cursor: hand;");
        btnLogin.setMaxWidth(Double.MAX_VALUE);


        // ========== LOGIN CORREGIDO - LEE DEL REPOSITORIO ==========
        btnLogin.setOnAction(e -> {
            String email = txtEmail.getText();
            String password = txtPassword.getText();

            // Buscar usuario en repositorio
            Persona usuario = null;

            for (Administrador a : adminRepository.findAll()) {
                if (a.getEmail().equals(email)) {
                    usuario = a;
                    break;
                }
            }

            if (usuario == null) {
                for (Usuario u : usuarioRepository.findAll()) {
                    if (u.getEmail().equals(email)) {
                        usuario = u;
                        break;
                    }
                }
            }

            // Validar contraseña
            boolean passwordValida = false;
            if (usuario instanceof Administrador) {
                passwordValida = password.equals("admin123");
            } else if (usuario != null) {
                passwordValida = password.equals("123456");
            }

            if (usuario != null && passwordValida) {
                GestorSesion.getInstance().setUsuarioActivo(usuario);
                System.out.println("✅ Login exitoso: " + usuario.getNombreCompleto());
                mostrarDashboard();
            } else {
                mostrarAlerta("Error", "Credenciales incorrectas\n\nUsuarios disponibles:\n- juan@mail.com / 123456\n- admin@eventos.com / admin123");
            }
        });
        // ============================================================

        // Enlace de registro
        HBox registroBox = new HBox(5);
        registroBox.setAlignment(Pos.CENTER);
        Label lblRegistro = new Label("¿No tienes cuenta?");
        lblRegistro.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        Hyperlink linkRegistro = new Hyperlink("Regístrate aquí");
        linkRegistro.setStyle("-fx-text-fill: #6366f1; -fx-font-size: 12px; -fx-underline: true;");
        registroBox.getChildren().addAll(lblRegistro, linkRegistro);

        formBox.getChildren().addAll(lblEmail, txtEmail, lblPassword, txtPassword, btnLogin, registroBox);

        panel.getChildren().addAll(logoBox, separator, formBox);
        root.getChildren().add(panel);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.8), panel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void mostrarDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        Scene scene = new Scene(dashboardView.getRoot(), 1300, 800);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {}
        stage.setScene(scene);
        stage.setTitle("Plataforma de Gestión - Dashboard");

        // ========== MANTENER MAXIMIZADO ==========
        stage.setMaximized(true);
        // =========================================
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void asegurarAdminExistente() {
        AdministradorRepository adminRepo = AdministradorRepository.getInstance();

        if (adminRepo.findAll().isEmpty()) {
            Administrador admin = new Administrador("AD001", "Administrador", "admin@eventos.com", "600555666");
            adminRepo.save(admin);
            System.out.println("✅ Administrador creado manualmente");
        } else {
            System.out.println("✅ Administrador ya existe");
            for (Administrador a : adminRepo.findAll()) {
                System.out.println("Admin en repositorio: " + a.getEmail());
            }
        }
    }

    public VBox getRoot() { return root; }
}