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
        root.setStyle("-fx-background-color: #f5f7fb;");
        root.setTop(crearHeader());
        root.setLeft(crearMenu());
        root.setCenter(crearTabs());
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: white; -fx-padding: 12px 25px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);

        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER);
        Label logoIcon = new Label("⚡");
        logoIcon.setStyle("-fx-font-size: 24px;");
        Label logoTitulo = new Label("EventFlow");
        logoTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4f46e5;");
        logoBox.getChildren().addAll(logoIcon, logoTitulo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Persona usuario = GestorSesion.getInstance().getUsuarioActivo();

        HBox userBox = new HBox(12);
        userBox.setAlignment(Pos.CENTER);

        Circle avatar = new Circle(22);
        avatar.setFill(Color.web("#4f46e5"));
        Label avatarText = new Label(usuario.getNombreCompleto().substring(0, 1).toUpperCase());
        avatarText.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox userInfo = new VBox(2);
        Label userName = new Label(usuario.getNombreCompleto());
        userName.setStyle("-fx-font-weight: 600; -fx-text-fill: #1e293b; -fx-font-size: 13px;");
        Label userRole = new Label(GestorSesion.getInstance().isAdmin() ? "Administrador" : "Usuario");
        userRole.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userRole);

        Button btnLogout = new Button("Salir");
        btnLogout.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6px 18px; -fx-cursor: hand; -fx-font-weight: 600; -fx-font-size: 12px;");
        btnLogout.setOnAction(e -> volverLogin());

        userBox.getChildren().addAll(avatarPane, userInfo, btnLogout);
        headerContent.getChildren().addAll(logoBox, spacer, userBox);
        header.getChildren().add(headerContent);
        return header;
    }

    private VBox crearMenu() {
        VBox menu = new VBox(8);
        menu.setStyle("-fx-background-color: #1e293b; -fx-padding: 25px 16px; -fx-min-width: 240px;");

        Persona usuario = GestorSesion.getInstance().getUsuarioActivo();

        // Perfil en menú
        HBox perfilBox = new HBox(12);
        perfilBox.setAlignment(Pos.CENTER_LEFT);
        perfilBox.setPadding(new Insets(0, 0, 15, 0));

        Circle avatar = new Circle(28);
        avatar.setFill(Color.web("#4f46e5"));
        Label avatarText = new Label(usuario.getNombreCompleto().substring(0, 1).toUpperCase());
        avatarText.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        VBox perfilInfo = new VBox(2);
        Label nombre = new Label(usuario.getNombreCompleto());
        nombre.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600;");
        Label email = new Label(usuario.getEmail());
        email.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        perfilInfo.getChildren().addAll(nombre, email);

        perfilBox.getChildren().addAll(avatarPane, perfilInfo);
        menu.getChildren().add(perfilBox);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #334155;");
        menu.getChildren().add(sep);

        // Botones del menú
        Button btnDashboard = crearBotonMenu("📊 Dashboard", true);
        Button btnEventos = crearBotonMenu("🎪 Eventos", false);
        Button btnCompras = crearBotonMenu("🎟️ Mis Compras", false);

        btnDashboard.setOnAction(e -> tabPane.getSelectionModel().select(0));
        btnEventos.setOnAction(e -> tabPane.getSelectionModel().select(1));
        btnCompras.setOnAction(e -> tabPane.getSelectionModel().select(2));

        menu.getChildren().addAll(btnDashboard, btnEventos, btnCompras);

        if (GestorSesion.getInstance().isAdmin()) {
            Button btnMetricas = crearBotonMenu("📈 Métricas", false);
            btnMetricas.setOnAction(e -> tabPane.getSelectionModel().select(3));
            menu.getChildren().add(btnMetricas);
        }

        Button btnPerfil = crearBotonMenu("👤 Mi Perfil", false);
        btnPerfil.setOnAction(e -> mostrarPerfil());
        menu.getChildren().add(btnPerfil);

        if (GestorSesion.getInstance().isAdmin()) {
            Button btnAdmin = crearBotonMenu("⚙️ Administrador", false);
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
        btn.setStyle("-fx-background-color: " + (activo ? "#4f46e5" : "transparent") + ";" +
                "-fx-text-fill: #e2e8f0;" +
                "-fx-padding: 10px 16px;" +
                "-fx-background-radius: 10px;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " + (activo ? "bold" : "normal") + ";" +
                "-fx-cursor: hand;");

        if (!activo) {
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-padding: 10px 16px; -fx-background-radius: 10px; -fx-font-size: 13px; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e2e8f0; -fx-padding: 10px 16px; -fx-background-radius: 10px; -fx-font-size: 13px; -fx-cursor: hand;"));
        }
        return btn;
    }

    private TabPane crearTabs() {
        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent; -fx-padding: 20px;");

        Tab tabDashboard = new Tab("Resumen");
        tabDashboard.setContent(crearDashboardContent());
        tabDashboard.setClosable(false);

        Tab tabEventos = new Tab("Eventos");
        eventosView = new EventosView(stage);
        tabEventos.setContent(eventosView.getRoot());
        tabEventos.setClosable(false);

        Tab tabCompras = new Tab("Mis Compras");
        misComprasView = new MisComprasView(stage);
        tabCompras.setContent(misComprasView.getRoot());
        tabCompras.setClosable(false);

        tabPane.getTabs().addAll(tabDashboard, tabEventos, tabCompras);

        if (GestorSesion.getInstance().isAdmin()) {
            Tab tabMetricas = new Tab("Métricas");
            metricasView = new MetricasView(stage);
            tabMetricas.setContent(metricasView.getRoot());
            tabMetricas.setClosable(false);
            tabPane.getTabs().add(tabMetricas);

            Tab tabAdminStats = new Tab("Reportes");
            tabAdminStats.setContent(crearDashboardAdminContent());
            tabAdminStats.setClosable(false);
            tabPane.getTabs().add(tabAdminStats);
        }

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

        grid.add(crearCard("🎪", "Eventos", String.valueOf(eventos), "#4f46e5"), 0, 0);
        grid.add(crearCard("🛒", "Compras", String.valueOf(compras), "#8b5cf6"), 1, 0);
        grid.add(crearCard("💰", "Ingresos", "$" + String.format("%,.0f", ingresos), "#10b981"), 2, 0);
        grid.add(crearCard("👥", "Usuarios", String.valueOf(usuarios), "#f59e0b"), 3, 0);

        content.getChildren().add(grid);
        return content;
    }

    private VBox crearCard(String icono, String titulo, String valor, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20px; -fx-padding: 20px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        card.setPrefWidth(200);

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 28px;");
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
        headerBox.getChildren().addAll(lblIcono, lblTitulo);

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 20px; -fx-padding: 20px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 4);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 20px; -fx-padding: 20px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);"));

        card.getChildren().addAll(headerBox, lblValor);
        return card;
    }

    private VBox crearDashboardAdminContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        EventoRepository eventoRepo = EventoRepository.getInstance();
        CompraRepository compraRepo = CompraRepository.getInstance();
        UsuarioRepository usuarioRepo = UsuarioRepository.getInstance();

        long totalEventos = eventoRepo.findAll().size();
        long totalCompras = compraRepo.findAll().size();
        double ingresosTotales = compraRepo.findAll().stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).mapToDouble(Compra::getTotal).sum();
        long totalUsuarios = usuarioRepo.findAll().size();
        long ticketsVendidos = compraRepo.findAll().stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).mapToInt(c -> c.getItems().size()).sum();

        grid.add(crearCardAdmin("🎪", "Eventos", String.valueOf(totalEventos), "#4f46e5"), 0, 0);
        grid.add(crearCardAdmin("🛒", "Compras", String.valueOf(totalCompras), "#8b5cf6"), 1, 0);
        grid.add(crearCardAdmin("💰", "Ingresos", "$" + String.format("%,.0f", ingresosTotales), "#10b981"), 2, 0);
        grid.add(crearCardAdmin("👥", "Usuarios", String.valueOf(totalUsuarios), "#f59e0b"), 3, 0);
        grid.add(crearCardAdmin("🎫", "Tickets Vendidos", String.valueOf(ticketsVendidos), "#06b6d4"), 0, 1);

        content.getChildren().add(grid);
        return content;
    }

    private VBox crearCardAdmin(String icono, String titulo, String valor, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-padding: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");
        card.setPrefWidth(180);

        HBox headerBox = new HBox(8);
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 22px;");
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        headerBox.getChildren().addAll(lblIcono, lblTitulo);

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(headerBox, lblValor);
        return card;
    }

    private void mostrarPerfil() {
        PerfilView perfilView = new PerfilView(stage);
        Scene scene = new Scene(perfilView.getRoot(), 1300, 800);
        stage.setScene(scene);
        stage.setTitle("Mi Perfil");
        stage.setMaximized(true);
    }

    private void mostrarAdminView() {
        if (adminView == null) {
            adminView = new AdminView(stage);
        }
        Scene scene = new Scene(adminView.getRoot(), 1300, 800);
        stage.setScene(scene);
        stage.setTitle("Administrador");
        stage.setMaximized(true);
    }

    private void volverLogin() {
        GestorSesion.getInstance().cerrarSesion();
        LoginView loginView = new LoginView(stage);
        Scene scene = new Scene(loginView.getRoot(), 900, 600);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.setMaximized(true);
    }

    public BorderPane getRoot() { return root; }
}