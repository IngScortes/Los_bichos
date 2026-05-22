package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MetricasView {
    private Stage stage;
    private VBox root;
    private CompraRepository compraRepository;
    private EventoRepository eventoRepository;
    private UsuarioRepository usuarioRepository;

    public MetricasView(Stage stage) {
        this.stage = stage;
        inicializarServicios();
        crearUI();
    }

    private void inicializarServicios() {
        this.compraRepository = new CompraRepository();
        this.eventoRepository = new EventoRepository();
        this.usuarioRepository = new UsuarioRepository();
    }

    private void crearUI() {
        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // Título
        Label titulo = new Label("📊 Métricas y Estadísticas");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Scroll principal
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(10));

        // Fila 1: Tarjetas de KPIs
        HBox kpiRow = crearKPIs();

        // Fila 2: Gráficos principales
        HBox graficosRow = new HBox(20);
        graficosRow.setAlignment(Pos.CENTER);

        // Gráfico de barras - Ventas por mes
        VBox ventasMesBox = crearGraficoVentasPorMes();

        // Gráfico de torta - Ventas por categoría
        VBox categoriaBox = crearGraficoPorCategoria();

        graficosRow.getChildren().addAll(ventasMesBox, categoriaBox);

        // Fila 3: Tabla de eventos más vendidos
        VBox topEventosBox = crearTablaTopEventos();

        // Fila 4: Estadísticas adicionales
        VBox estadisticasBox = crearEstadisticasAdicionales();

        content.getChildren().addAll(kpiRow, graficosRow, topEventosBox, estadisticasBox);
        scrollPane.setContent(content);

        root.getChildren().addAll(titulo, scrollPane);
    }

    private HBox crearKPIs() {
        HBox panel = new HBox(20);
        panel.setAlignment(Pos.CENTER);

        List<Compra> compras = compraRepository.findAll();
        List<Compra> comprasPagadas = compras.stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .collect(Collectors.toList());

        double ingresosTotales = comprasPagadas.stream()
                .mapToDouble(Compra::getTotal)
                .sum();

        long totalEventos = eventoRepository.findAll().size();
        long totalUsuarios = usuarioRepository.findAll().size();
        long ticketsVendidos = comprasPagadas.stream()
                .mapToLong(c -> c.getItems().size())
                .sum();

        NumberFormat formato = NumberFormat.getInstance(Locale.US);

        VBox card1 = crearCardKPI("💰 Ingresos Totales", "$" + formato.format(ingresosTotales), "#43e97b");
        VBox card2 = crearCardKPI("🎫 Tickets Vendidos", String.valueOf(ticketsVendidos), "#667eea");
        VBox card3 = crearCardKPI("🎪 Eventos", String.valueOf(totalEventos), "#f093fb");
        VBox card4 = crearCardKPI("👥 Usuarios", String.valueOf(totalUsuarios), "#4facfe");

        panel.getChildren().addAll(card1, card2, card3, card4);
        return panel;
    }

    private VBox crearCardKPI(String titulo, String valor, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(200);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    private VBox crearGraficoVentasPorMes() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20;");
        panel.setPrefWidth(450);

        Label titulo = new Label("📈 Ventas por Mes");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Datos de ventas por mes
        Map<String, Double> ventasPorMes = new LinkedHashMap<>();

        // Inicializar últimos 6 meses
        LocalDateTime ahora = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime mes = ahora.minusMonths(i);
            String nombreMes = mes.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            ventasPorMes.put(nombreMes, 0.0);
        }

        // Acumular ventas
        for (Compra c : compraRepository.findAll()) {
            if (c.getEstado() == EstadoCompra.PAGADA) {
                String mesCompra = c.getFechaCreacion().format(DateTimeFormatter.ofPattern("MMM yyyy"));
                if (ventasPorMes.containsKey(mesCompra)) {
                    ventasPorMes.put(mesCompra, ventasPorMes.get(mesCompra) + c.getTotal());
                }
            }
        }

        // Crear gráfico de barras
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ingresos ($)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas");

        for (Map.Entry<String, Double> entry : ventasPorMes.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);

        panel.getChildren().addAll(titulo, barChart);
        return panel;
    }

    private VBox crearGraficoPorCategoria() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20;");
        panel.setPrefWidth(400);

        Label titulo = new Label("🥧 Ventas por Categoría");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Datos por categoría
        Map<CategoriaEvento, Integer> ventasPorCategoria = new HashMap<>();
        for (Compra c : compraRepository.findAll()) {
            if (c.getEstado() == EstadoCompra.PAGADA) {
                CategoriaEvento categoria = c.getEvento().getCategoria();
                ventasPorCategoria.put(categoria, ventasPorCategoria.getOrDefault(categoria, 0) + c.getItems().size());
            }
        }

        PieChart pieChart = new PieChart();
        pieChart.setPrefHeight(300);

        for (Map.Entry<CategoriaEvento, Integer> entry : ventasPorCategoria.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey().toString(), entry.getValue());
            pieChart.getData().add(data);
        }

        if (pieChart.getData().isEmpty()) {
            pieChart.getData().add(new PieChart.Data("Sin datos", 1));
        }

        panel.getChildren().addAll(titulo, pieChart);
        return panel;
    }

    private VBox crearTablaTopEventos() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20;");

        Label titulo = new Label("🏆 Top Eventos más Vendidos");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Datos de ventas por evento
        Map<String, Integer> ventasPorEvento = new HashMap<>();
        Map<String, Double> ingresosPorEvento = new HashMap<>();

        for (Compra c : compraRepository.findAll()) {
            if (c.getEstado() == EstadoCompra.PAGADA) {
                String nombreEvento = c.getEvento().getNombre();
                int tickets = c.getItems().size();
                double total = c.getTotal();

                ventasPorEvento.put(nombreEvento, ventasPorEvento.getOrDefault(nombreEvento, 0) + tickets);
                ingresosPorEvento.put(nombreEvento, ingresosPorEvento.getOrDefault(nombreEvento, 0.0) + total);
            }
        }

        // Ordenar por tickets vendidos
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(ventasPorEvento.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Tabla
        TableView<Map<String, Object>> tabla = new TableView<>();
        tabla.setPrefHeight(250);

        TableColumn<Map<String, Object>, String> colEvento = new TableColumn<>("Evento");
        colEvento.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty((String) cell.getValue().get("evento")));
        colEvento.setPrefWidth(250);

        TableColumn<Map<String, Object>, Integer> colTickets = new TableColumn<>("Tickets Vendidos");
        colTickets.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty((Integer) cell.getValue().get("tickets")).asObject());
        colTickets.setPrefWidth(150);

        TableColumn<Map<String, Object>, String> colIngresos = new TableColumn<>("Ingresos");
        colIngresos.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty((String) cell.getValue().get("ingresos")));
        colIngresos.setPrefWidth(150);

        tabla.getColumns().addAll(colEvento, colTickets, colIngresos);

        NumberFormat formato = NumberFormat.getInstance(Locale.US);

        for (Map.Entry<String, Integer> entry : sorted) {
            Map<String, Object> row = new HashMap<>();
            row.put("evento", entry.getKey());
            row.put("tickets", entry.getValue());
            row.put("ingresos", "$" + formato.format(ingresosPorEvento.get(entry.getKey())));
            tabla.getItems().add(row);
        }

        if (tabla.getItems().isEmpty()) {
            Map<String, Object> row = new HashMap<>();
            row.put("evento", "No hay datos");
            row.put("tickets", 0);
            row.put("ingresos", "$0");
            tabla.getItems().add(row);
        }

        panel.getChildren().addAll(titulo, tabla);
        return panel;
    }

    private VBox crearEstadisticasAdicionales() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 20;");

        Label titulo = new Label("📋 Estadísticas Adicionales");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(15);

        List<Compra> compras = compraRepository.findAll();
        List<Compra> comprasPagadas = compras.stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .collect(Collectors.toList());

        double ticketPromedio = comprasPagadas.stream()
                .mapToDouble(c -> c.getTotal() / c.getItems().size())
                .average()
                .orElse(0);

        long comprasCanceladas = compras.stream()
                .filter(c -> c.getEstado() == EstadoCompra.CANCELADA)
                .count();

        long comprasPendientes = compras.stream()
                .filter(c -> c.getEstado() == EstadoCompra.CREADA)
                .count();

        double porcentajeCancelacion = compras.isEmpty() ? 0 : (comprasCanceladas * 100.0 / compras.size());

        NumberFormat formato = NumberFormat.getInstance(Locale.US);

        grid.add(new Label("🎫 Ticket Promedio:"), 0, 0);
        grid.add(new Label("$" + formato.format(ticketPromedio)), 1, 0);

        grid.add(new Label("❌ Compras Canceladas:"), 0, 1);
        grid.add(new Label(String.valueOf(comprasCanceladas)), 1, 1);

        grid.add(new Label("⏳ Compras Pendientes:"), 0, 2);
        grid.add(new Label(String.valueOf(comprasPendientes)), 1, 2);

        grid.add(new Label("📊 Tasa de Cancelación:"), 0, 3);
        grid.add(new Label(String.format("%.1f%%", porcentajeCancelacion)), 1, 3);

        panel.getChildren().addAll(titulo, grid);
        return panel;
    }

    public VBox getRoot() {
        return root;
    }
}