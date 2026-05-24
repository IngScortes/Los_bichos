package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class DashboardView {
    private Stage stage;
    private BorderPane root;
    private TabPane tabPane;
    private EventosView eventosView;
    private MisComprasView misComprasView;
    private MetricasView metricasView;
    private AdminView adminView;

    public DashboardView(Stage stage) {
        this.stage = stage;
        crearUI();
    }

    private void crearUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f1f5f9;");
        root.setTop(crearHeader());
        root.setLeft(crearMenu());
        root.setCenter(crearTabs());
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: white; -fx-padding: 15px 25px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("📋 Plataforma de Gestión");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Persona usuario = GestorSesion.getInstance().getUsuarioActivo();
        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER);

        Circle avatar = new Circle(20);
        avatar.setFill(Color.web("#6366f1"));
        Label avatarText = new Label(usuario.getNombreCompleto().substring(0, 1).toUpperCase());
        avatarText.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userInfo = new VBox(2);
        Label userName = new Label(usuario.getNombreCompleto());
        userName.setStyle("-fx-font-weight: 600; -fx-text-fill: #1e293b; -fx-font-size: 13px;");
        Label userEmail = new Label(usuario.getEmail());
        userEmail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userEmail);

        Button btnLogout = new Button("Salir");
        btnLogout.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 25px; -fx-padding: 6 18; -fx-cursor: hand; -fx-font-weight: 600;");
        btnLogout.setOnAction(e -> volverLogin());

        userBox.getChildren().addAll(avatarPane, userInfo, btnLogout);
        headerContent.getChildren().addAll(titulo, spacer, userBox);
        header.getChildren().add(headerContent);
        return header;
    }

    private VBox crearMenu() {
        VBox menu = new VBox(8);
        menu.setStyle("-fx-background-color: linear-gradient(to bottom, #1e1b4b, #312e81); -fx-padding: 25px 15px; -fx-min-width: 240px;");

        Persona usuario = GestorSesion.getInstance().getUsuarioActivo();
        Circle avatar = new Circle(35);
        avatar.setFill(Color.web("#8b5cf6"));
        Label avatarText = new Label(usuario.getNombreCompleto().substring(0, 1).toUpperCase());
        avatarText.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        StackPane avatarPane = new StackPane(avatar, avatarText);
        avatarPane.setAlignment(Pos.CENTER);

        Label nombre = new Label(usuario.getNombreCompleto());
        nombre.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label email = new Label(usuario.getEmail());
        email.setStyle("-fx-text-fill: #a5b4fc; -fx-font-size: 11px;");

        menu.getChildren().addAll(avatarPane, nombre, email, new Separator());

        Button btnDashboard = crearBotonMenu("📊 Dashboard", true);
        Button btnEventos = crearBotonMenu("🎪 Eventos", false);

        btnDashboard.setOnAction(e -> tabPane.getSelectionModel().select(0));
        btnEventos.setOnAction(e -> tabPane.getSelectionModel().select(1));

        menu.getChildren().addAll(btnDashboard, btnEventos);

        // ========== MIS COMPRAS SOLO PARA USUARIOS NORMALES ==========
        if (!GestorSesion.getInstance().isAdmin()) {
            Button btnCompras = crearBotonMenu("🎟️ Mis Compras", false);
            btnCompras.setOnAction(e -> tabPane.getSelectionModel().select(2));
            menu.getChildren().add(btnCompras);
        }
        // ==============================================================

        // ========== MÉTRICAS SOLO PARA ADMIN ==========
        if (GestorSesion.getInstance().isAdmin()) {
            Button btnMetricas = crearBotonMenu("📈 Métricas", false);
            btnMetricas.setOnAction(e -> tabPane.getSelectionModel().select(3));
            menu.getChildren().add(btnMetricas);
        }
        // ============================================

        // ========== PERFIL PARA TODOS ==========
        Button btnPerfil = crearBotonMenu("👤 Mi Perfil", false);
        btnPerfil.setOnAction(e -> mostrarPerfil());
        menu.getChildren().add(btnPerfil);
        // =======================================

        if (GestorSesion.getInstance().isAdmin()) {
            Button btnAdmin = crearBotonMenu("👑 Administrador", false);
            btnAdmin.setOnAction(e -> mostrarAdminView());
            menu.getChildren().add(btnAdmin);
        }

        menu.getChildren().add(new Separator());
        Button btnLogout = crearBotonMenu("🚪 Cerrar Sesión", false);
        btnLogout.setOnAction(e -> volverLogin());
        menu.getChildren().add(btnLogout);

        return menu;
    }

    private Button crearBotonMenu(String texto, boolean activo) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
                "-fx-background-color: " + (activo ? "#8b5cf6" : "transparent") + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10px 15px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-weight: 600;" +
                        "-fx-font-size: 13px;"
        );
        if (!activo) {
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #4c1d95; -fx-text-fill: white; -fx-padding: 10px 15px; -fx-background-radius: 12px; -fx-cursor: hand; -fx-font-weight: 600;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 10px 15px; -fx-background-radius: 12px; -fx-cursor: hand; -fx-font-weight: 600;"));
        }
        return btn;
    }

    private TabPane crearTabs() {
        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent; -fx-padding: 20px;");

        Tab tabDashboard = new Tab("📊 Dashboard");
        tabDashboard.setContent(crearDashboardContent());
        tabDashboard.setClosable(false);

        Tab tabEventos = new Tab("🎪 Eventos");
        eventosView = new EventosView(stage);
        tabEventos.setContent(eventosView.getRoot());
        tabEventos.setClosable(false);

        tabPane.getTabs().addAll(tabDashboard, tabEventos);

        // ========== MIS COMPRAS SOLO PARA USUARIOS NORMALES ==========
        if (!GestorSesion.getInstance().isAdmin()) {
            Tab tabCompras = new Tab("🎟️ Mis Compras");
            misComprasView = new MisComprasView(stage);
            tabCompras.setContent(misComprasView.getRoot());
            tabCompras.setClosable(false);
            tabPane.getTabs().add(tabCompras);
        }
        // ==============================================================

        // ========== MÉTRICAS SOLO PARA ADMIN ==========
        if (GestorSesion.getInstance().isAdmin()) {
            Tab tabMetricas = new Tab("📈 Métricas");
            metricasView = new MetricasView(stage);
            tabMetricas.setContent(metricasView.getRoot());
            tabMetricas.setClosable(false);
            tabPane.getTabs().add(tabMetricas);

            Tab tabAdminStats = new Tab("📊 Estadísticas Admin");
            tabAdminStats.setContent(crearDashboardAdminContent());
            tabAdminStats.setClosable(false);
            tabPane.getTabs().add(tabAdminStats);
        }
        // ============================================

        return tabPane;
    }

    private VBox crearDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        EventoRepository eventoRepo = EventoRepository.getInstance();
        CompraRepository compraRepo = CompraRepository.getInstance();
        UsuarioRepository usuarioRepo = UsuarioRepository.getInstance();

        long eventos = eventoRepo.findAll().stream().filter(e -> e.getEstado() == EstadoEvento.PUBLICADO).count();
        long compras = compraRepo.findAll().size();
        double ingresos = compraRepo.findAll().stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).mapToDouble(Compra::getTotal).sum();
        long usuarios = usuarioRepo.findAll().size();

        grid.add(crearCard("🎪 Eventos", String.valueOf(eventos), "#6366f1"), 0, 0);
        grid.add(crearCard("🛒 Compras", String.valueOf(compras), "#8b5cf6"), 1, 0);
        grid.add(crearCard("💰 Ingresos", "$" + String.format("%,.0f", ingresos), "#10b981"), 2, 0);
        grid.add(crearCard("👥 Usuarios", String.valueOf(usuarios), "#f59e0b"), 3, 0);

        content.getChildren().add(grid);
        return content;
    }

    private VBox crearCard(String titulo, String valor, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20px; -fx-padding: 20px; -fx-min-width: 180px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    private VBox crearDashboardAdminContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        EventoRepository eventoRepo = EventoRepository.getInstance();
        CompraRepository compraRepo = CompraRepository.getInstance();
        UsuarioRepository usuarioRepo = UsuarioRepository.getInstance();

        long totalEventos = eventoRepo.findAll().size();
        long totalEventosPublicados = eventoRepo.findAll().stream().filter(e -> e.getEstado() == EstadoEvento.PUBLICADO).count();
        long totalCompras = compraRepo.findAll().size();
        long totalComprasPagadas = compraRepo.findAll().stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).count();
        double ingresosTotales = compraRepo.findAll().stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).mapToDouble(Compra::getTotal).sum();
        long totalUsuarios = usuarioRepo.findAll().size();
        long ticketsVendidos = compraRepo.findAll().stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).mapToInt(c -> c.getItems().size()).sum();

        String eventoMasVendido = "";
        int maxVentas = 0;
        for (Evento e : eventoRepo.findAll()) {
            int ventas = compraRepo.findAll().stream()
                    .filter(c -> c.getEvento().getIdEvento().equals(e.getIdEvento()))
                    .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                    .mapToInt(c -> c.getItems().size()).sum();
            if (ventas > maxVentas) {
                maxVentas = ventas;
                eventoMasVendido = e.getNombre();
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        grid.add(crearCardAdmin("🎪 Total Eventos", String.valueOf(totalEventos), "#6366f1"), 0, 0);
        grid.add(crearCardAdmin("📢 Publicados", String.valueOf(totalEventosPublicados), "#10b981"), 1, 0);
        grid.add(crearCardAdmin("🛒 Compras", String.valueOf(totalCompras), "#8b5cf6"), 2, 0);
        grid.add(crearCardAdmin("✅ Pagadas", String.valueOf(totalComprasPagadas), "#3b82f6"), 3, 0);
        grid.add(crearCardAdmin("💰 Ingresos", "$" + String.format("%,.0f", ingresosTotales), "#ef4444"), 0, 1);
        grid.add(crearCardAdmin("👥 Usuarios", String.valueOf(totalUsuarios), "#f59e0b"), 1, 1);
        grid.add(crearCardAdmin("🎫 Tickets", String.valueOf(ticketsVendidos), "#06b6d4"), 2, 1);
        grid.add(crearCardAdmin("🏆 Más Vendido", eventoMasVendido + " (" + maxVentas + ")", "#8b5cf6"), 3, 1);

        content.getChildren().add(grid);
        return content;
    }

    private VBox crearCardAdmin(String titulo, String valor, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20px; -fx-padding: 15px; -fx-min-width: 160px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    private void mostrarAdminView() {
        if (adminView == null) {
            adminView = new AdminView(stage);
        }
        Scene scene = new Scene(adminView.getRoot(), 1300, 800);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {}
        stage.setScene(scene);
    }

    private void volverLogin() {
        GestorSesion.getInstance().cerrarSesion();
        LoginView loginView = new LoginView(stage);
        Scene scene = new Scene(loginView.getRoot(), 900, 600);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {}
        stage.setScene(scene);
    }


    private void mostrarPerfil() {
        PerfilView perfilView = new PerfilView(stage);
        Scene scene = new Scene(perfilView.getRoot(), 1300, 800);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {}
        stage.setScene(scene);
        stage.setTitle("Mi Perfil");
    }

    public BorderPane getRoot() { return root; }
}