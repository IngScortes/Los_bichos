package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.patterns.behavioral.observer.Observador;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardView implements Observador {
    private Stage stage;
    private BorderPane root;
    private EventoService eventoService;
    private CompraService compraService;
    private CompraRepository compraRepository;
    private UsuarioService usuarioService;

    // Componentes UI
    private Label lblTotalUsuarios, lblTotalCompras, lblIngresosTotales, lblEventosActivos;
    private ListView<String> listViewNotificaciones;
    private PieChart pieChartVentas;
    private BarChart<String, Number> barChartEventos;

    public DashboardView(Stage stage) {
        this.stage = stage;
        inicializarServicios();
        crearUI();
        cargarMetricas();
        registrarObservador();
        iniciarAnimaciones();
    }

    private void inicializarServicios() {
        EventoRepository eventoRepo = new EventoRepository();
        CompraRepository compraRepo = new CompraRepository();
        UsuarioRepository usuarioRepo = new UsuarioRepository();
        AsientoRepository asientoRepo = new AsientoRepository();

        this.eventoService = new EventoService(eventoRepo);
        this.compraRepository = compraRepo;
        this.compraService = new CompraService(compraRepo, asientoRepo);
        this.usuarioService = new UsuarioService(usuarioRepo);
    }

    private void registrarObservador() {
        for (Evento e : eventoService.listarEventosPublicados()) {
            e.agregarObservador(this);
        }
        for (Compra c : compraRepository.findAll()) {
            c.agregarObservador(this);
        }
    }

    private void crearUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f2f5;");

        root.setTop(crearHeader());
        root.setLeft(crearMenuLateral());
        root.setCenter(crearPanelCentral());
    }

    private Node crearHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Logo
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER);
        Label logoIcon = new Label("📋");
        logoIcon.setStyle("-fx-font-size: 28px;");
        Label logoTitulo = new Label("Plataforma de Gestión");
        logoTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        logoBox.getChildren().addAll(logoIcon, logoTitulo);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Info usuario
        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER);

        Circle avatarCircle = new Circle(20);
        avatarCircle.setFill(Color.web("#667eea"));
        String nombreUsuario = GestorSesion.getInstance().getUsuarioActivo().getNombreCompleto();
        String inicial = nombreUsuario.substring(0, 1);
        Label avatarLabel = new Label(inicial);
        avatarLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        StackPane avatar = new StackPane(avatarCircle, avatarLabel);

        VBox userInfo = new VBox(2);
        Label userName = new Label(nombreUsuario);
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        Label userEmail = new Label(GestorSesion.getInstance().getUsuarioActivo().getEmail());
        userEmail.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        userInfo.getChildren().addAll(userName, userEmail);

        Button btnLogout = new Button("Salir");
        btnLogout.setStyle("-fx-background-color: #f5576c; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 20;");
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle("-fx-background-color: #e8455a; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 20;"));
        btnLogout.setOnMouseExited(e -> btnLogout.setStyle("-fx-background-color: #f5576c; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 20;"));
        btnLogout.setOnAction(e -> volverLogin());

        userBox.getChildren().addAll(avatar, userInfo, btnLogout);
        header.getChildren().addAll(logoBox, spacer, userBox);

        return header;
    }

    private Node crearMenuLateral() {
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(20, 15, 20, 15));
        menu.setStyle("-fx-background-color: white; -fx-min-width: 220;");

        String[] menuItems = {"📊 Dashboard", "🎪 Eventos", "🎟️ Mis Compras", "📈 Métricas", "⚙️ Configuración"};

        for (int i = 0; i < menuItems.length; i++) {
            Button btn = crearBotonMenu(menuItems[i], i == 0);
            menu.getChildren().add(btn);
        }

        if (GestorSesion.getInstance().isAdmin()) {
            Button btnAdmin = crearBotonMenu("👑 Administrador", false);
            menu.getChildren().add(btnAdmin);
        }

        return menu;
    }

    private Button crearBotonMenu(String texto, boolean activo) {
        Button btn = new Button(texto);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: " + (activo ? "linear-gradient(to right, #667eea, #764ba2);" : "transparent;") +
                        "-fx-text-fill: " + (activo ? "white;" : "#555;") +
                        "-fx-padding: 12 15;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e -> {
            if (!activo) {
                btn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #667eea; -fx-padding: 12 15; -fx-background-radius: 10px; -fx-font-size: 13px; -fx-cursor: hand;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!activo) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555; -fx-padding: 12 15; -fx-background-radius: 10px; -fx-font-size: 13px; -fx-cursor: hand;");
            }
        });

        return btn;
    }

    private Node crearPanelCentral() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab tabDashboard = new Tab("Dashboard");
        tabDashboard.setContent(crearDashboardContent());
        tabDashboard.setClosable(false);

        Tab tabEventos = new Tab("Eventos");
        tabEventos.setContent(crearEventosContent());
        tabEventos.setClosable(false);

        Tab tabCompras = new Tab("Mis Compras");
        tabCompras.setContent(crearComprasContent());
        tabCompras.setClosable(false);

        Tab tabMetricas = new Tab("Métricas");
        tabMetricas.setContent(crearMetricasContent());
        tabMetricas.setClosable(false);

        tabPane.getTabs().addAll(tabDashboard, tabEventos, tabCompras, tabMetricas);

        return tabPane;
    }

    private Node crearDashboardContent() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Tarjetas de métricas
        GridPane cardsGrid = new GridPane();
        cardsGrid.setHgap(20);
        cardsGrid.setVgap(20);

        lblTotalUsuarios = crearTarjetaMetrica("👥 Usuarios", "0", "#667eea");
        lblTotalCompras = crearTarjetaMetrica("🛒 Compras", "0", "#f093fb");
        lblIngresosTotales = crearTarjetaMetrica("💰 Ingresos", "$0", "#4facfe");
        lblEventosActivos = crearTarjetaMetrica("🎪 Eventos", "0", "#43e97b");

        cardsGrid.add(lblTotalUsuarios, 0, 0);
        cardsGrid.add(lblTotalCompras, 1, 0);
        cardsGrid.add(lblIngresosTotales, 2, 0);
        cardsGrid.add(lblEventosActivos, 3, 0);

        // Gráfico de barras
        VBox chartBox = new VBox(10);
        chartBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20;");
        Label chartTitle = new Label("Ventas por Evento");
        chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        barChartEventos = new BarChart<>(xAxis, yAxis);
        barChartEventos.setLegendVisible(false);
        barChartEventos.setPrefHeight(300);
        barChartEventos.setStyle("-fx-background-color: transparent;");

        chartBox.getChildren().addAll(chartTitle, barChartEventos);

        // Panel de notificaciones
        VBox notifBox = new VBox(10);
        notifBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20;");
        Label notifTitle = new Label("📢 Notificaciones en Tiempo Real");
        notifTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        listViewNotificaciones = new ListView<>();
        listViewNotificaciones.setPrefHeight(150);
        listViewNotificaciones.setStyle("-fx-background-radius: 10px;");

        notifBox.getChildren().addAll(notifTitle, listViewNotificaciones);

        content.getChildren().addAll(cardsGrid, chartBox, notifBox);
        scrollPane.setContent(content);

        return scrollPane;
    }

    private Label crearTarjetaMetrica(String titulo, String valor, String color) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(200);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(lblTitulo, lblValor);

        return lblValor;
    }

    private Node crearEventosContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label label = new Label("🎪 Lista de Eventos");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label sublabel = new Label("Próximamente: Visualización y compra de entradas");
        sublabel.setStyle("-fx-text-fill: #888;");

        content.getChildren().addAll(label, sublabel);
        return content;
    }

    private Node crearComprasContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label label = new Label("🎟️ Mis Compras");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label sublabel = new Label("Próximamente: Historial de compras y cancelaciones");
        sublabel.setStyle("-fx-text-fill: #888;");

        content.getChildren().addAll(label, sublabel);
        return content;
    }

    private Node crearMetricasContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        pieChartVentas = new PieChart();
        pieChartVentas.setTitle("Ventas por Categoría");
        pieChartVentas.setPrefHeight(400);
        pieChartVentas.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20;");

        content.getChildren().add(pieChartVentas);
        return content;
    }

    private void cargarMetricas() {
        NumberFormat formato = NumberFormat.getInstance(Locale.US);

        long totalUsuarios = usuarioService.listarTodos().size();
        lblTotalUsuarios.setText(String.valueOf(totalUsuarios));

        long totalCompras = compraRepository.findAll().size();
        lblTotalCompras.setText(String.valueOf(totalCompras));

        double ingresos = compraRepository.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal)
                .sum();
        lblIngresosTotales.setText("$" + formato.format(ingresos));

        long eventosActivos = eventoService.listarEventosPublicados().size();
        lblEventosActivos.setText(String.valueOf(eventosActivos));

        cargarGraficoBarras();
        cargarGraficoTorta();
    }

    private void cargarGraficoBarras() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Entradas Vendidas");

        Map<String, Integer> ventasPorEvento = new HashMap<>();
        for (Compra c : compraRepository.findAll()) {
            if (c.getEstado() == EstadoCompra.PAGADA) {
                String nombreEvento = c.getEvento().getNombre();
                ventasPorEvento.put(nombreEvento, ventasPorEvento.getOrDefault(nombreEvento, 0) + c.getItems().size());
            }
        }

        barChartEventos.getData().clear();
        for (Map.Entry<String, Integer> entry : ventasPorEvento.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChartEventos.getData().add(series);
    }

    private void cargarGraficoTorta() {
        Map<CategoriaEvento, Integer> ventasPorCategoria = new HashMap<>();
        for (Compra c : compraRepository.findAll()) {
            if (c.getEstado() == EstadoCompra.PAGADA) {
                CategoriaEvento categoria = c.getEvento().getCategoria();
                ventasPorCategoria.put(categoria, ventasPorCategoria.getOrDefault(categoria, 0) + c.getItems().size());
            }
        }

        pieChartVentas.getData().clear();
        for (Map.Entry<CategoriaEvento, Integer> entry : ventasPorCategoria.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey().toString(), entry.getValue());
            pieChartVentas.getData().add(data);
        }
    }

    private void iniciarAnimaciones() {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    @Override
    public void notificar(String evento, String mensaje, Object fuente) {
        Platform.runLater(() -> {
            String notificacion = "[" + evento + "] " + mensaje;
            listViewNotificaciones.getItems().add(0, notificacion);
            if (listViewNotificaciones.getItems().size() > 50) {
                listViewNotificaciones.getItems().remove(50);
            }

            if (evento.equals("ESTADO_COMPRA") || evento.equals("ESTADO_EVENTO")) {
                cargarMetricas();
            }
        });
    }

    private void volverLogin() {
        GestorSesion.getInstance().cerrarSesion();
        LoginView loginView = new LoginView(stage);
        Scene scene = new Scene(loginView.getRoot(), 900, 600);
        stage.setScene(scene);
        stage.setTitle("Plataforma de Gestión - Login");
    }

    public BorderPane getRoot() {
        return root;
    }
}