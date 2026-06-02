package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MetricasView {
    private Stage stage;
    private VBox root;
    private EventoRepository eventoRepo;
    private CompraRepository compraRepo;
    private UsuarioRepository usuarioRepo;
    private AsientoRepository asientoRepo;

    public MetricasView(Stage stage) {
        this.stage = stage;
        this.eventoRepo = EventoRepository.getInstance();
        this.compraRepo = CompraRepository.getInstance();
        this.usuarioRepo = UsuarioRepository.getInstance();
        this.asientoRepo = AsientoRepository.getInstance();
        crearUI();
        cargarMetricas();
    }

    private void crearUI() {
        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        Label titulo = new Label("📊 Métricas y Estadísticas");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        root.getChildren().add(titulo);
    }

    private void cargarMetricas() {
        root.getChildren().removeIf(n -> n != root.getChildren().get(0));

        // ========== 1. KPIs principales ==========
        GridPane kpiGrid = new GridPane();
        kpiGrid.setHgap(20);
        kpiGrid.setVgap(20);

        long totalEventos = eventoRepo.findAll().size();
        long totalEventosPublicados = eventoRepo.findAll().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO).count();
        long totalCompras = compraRepo.findAll().size();
        long totalComprasPagadas = compraRepo.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA).count();
        double ingresosTotales = compraRepo.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal).sum();
        long totalUsuarios = usuarioRepo.findAll().size();
        long ticketsVendidos = compraRepo.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToInt(c -> c.getItems().size()).sum();

        // Asientos totales y ocupados
        long totalAsientos = asientoRepo.findAll().size();
        long asientosOcupados = asientoRepo.findAll().stream()
                .filter(a -> a.getEstado() == EstadoAsiento.VENDIDO || a.getEstado() == EstadoAsiento.RESERVADO)
                .count();
        double porcentajeOcupacion = totalAsientos > 0 ? (asientosOcupados * 100.0 / totalAsientos) : 0;

        kpiGrid.add(crearCard("🎪 Total Eventos", String.valueOf(totalEventos), "#667eea"), 0, 0);
        kpiGrid.add(crearCard("📢 Eventos Publicados", String.valueOf(totalEventosPublicados), "#43e97b"), 1, 0);
        kpiGrid.add(crearCard("🛒 Total Compras", String.valueOf(totalCompras), "#f093fb"), 2, 0);
        kpiGrid.add(crearCard("✅ Compras Pagadas", String.valueOf(totalComprasPagadas), "#4facfe"), 3, 0);
        kpiGrid.add(crearCard("💰 Ingresos Totales", "$" + String.format("%,.0f", ingresosTotales), "#e94560"), 0, 1);
        kpiGrid.add(crearCard("👥 Usuarios", String.valueOf(totalUsuarios), "#f39c12"), 1, 1);
        kpiGrid.add(crearCard("🎫 Tickets Vendidos", String.valueOf(ticketsVendidos), "#1abc9c"), 2, 1);
        kpiGrid.add(crearCard("🪑 Ocupación", String.format("%.1f%%", porcentajeOcupacion), "#9b59b6"), 3, 1);

        // ========== 2. Gráfico de ventas por evento (Barras) ==========
        Map<String, Integer> ventasEvento = new HashMap<>();
        for (Compra c : compraRepo.findAll()) {
            if (c.getEstado() == EstadoCompra.PAGADA) {
                String nombre = c.getEvento().getNombre();
                ventasEvento.put(nombre, ventasEvento.getOrDefault(nombre, 0) + c.getItems().size());
            }
        }

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Tickets vendidos");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("🎟️ Tickets vendidos por evento");
        barChart.setPrefHeight(350);
        barChart.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15px;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas");
        for (Map.Entry<String, Integer> e : ventasEvento.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        barChart.getData().add(series);

        // ========== 3. Gráfico de ventas por categoría (Torta) ==========
        Map<CategoriaEvento, Integer> ventasCategoria = new HashMap<>();
        for (Compra c : compraRepo.findAll()) {
            if (c.getEstado() == EstadoCompra.PAGADA) {
                CategoriaEvento cat = c.getEvento().getCategoria();
                ventasCategoria.put(cat, ventasCategoria.getOrDefault(cat, 0) + c.getItems().size());
            }
        }

        PieChart pieChart = new PieChart();
        pieChart.setTitle("📊 Ventas por categoría");
        pieChart.setPrefHeight(350);
        pieChart.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15px;");
        for (Map.Entry<CategoriaEvento, Integer> e : ventasCategoria.entrySet()) {
            PieChart.Data data = new PieChart.Data(e.getKey().toString(), e.getValue());
            pieChart.getData().add(data);
        }

        if (pieChart.getData().isEmpty()) {
            pieChart.getData().add(new PieChart.Data("Sin datos", 1));
        }

        // ========== 4. Gráfico de ingresos por mes (Líneas) ==========
        Map<String, Double> ingresosPorMes = new LinkedHashMap<>();
        LocalDateTime ahora = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime mes = ahora.minusMonths(i);
            String nombreMes = mes.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            ingresosPorMes.put(nombreMes, 0.0);
        }

        for (Compra c : compraRepo.findAll()) {
            if (c.getEstado() == EstadoCompra.PAGADA) {
                String mesCompra = c.getFechaCreacion().format(DateTimeFormatter.ofPattern("MMM yyyy"));
                if (ingresosPorMes.containsKey(mesCompra)) {
                    ingresosPorMes.put(mesCompra, ingresosPorMes.get(mesCompra) + c.getTotal());
                }
            }
        }

        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        yAxisLine.setLabel("Ingresos ($)");
        LineChart<String, Number> lineChart = new LineChart<>(xAxisLine, yAxisLine);
        lineChart.setTitle("📈 Ingresos por mes (últimos 6 meses)");
        lineChart.setPrefHeight(350);
        lineChart.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15px;");

        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Ingresos");
        for (Map.Entry<String, Double> entry : ingresosPorMes.entrySet()) {
            lineSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        lineChart.getData().add(lineSeries);

        // ========== 5. Tabla de eventos más vendidos ==========
        VBox topEventosBox = new VBox(10);
        topEventosBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15px;");
        Label lblTopEventos = new Label("🏆 Top 5 Eventos más vendidos");
        lblTopEventos.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TableView<Map<String, Object>> tablaTop = new TableView<>();
        tablaTop.setPrefHeight(200);

        TableColumn<Map<String, Object>, String> colEvento = new TableColumn<>("Evento");
        colEvento.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty((String) c.getValue().get("evento")));
        colEvento.setPrefWidth(250);

        TableColumn<Map<String, Object>, Integer> colTickets = new TableColumn<>("Tickets Vendidos");
        colTickets.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty((Integer) c.getValue().get("tickets")).asObject());
        colTickets.setPrefWidth(150);

        TableColumn<Map<String, Object>, String> colIngresos = new TableColumn<>("Ingresos");
        colIngresos.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty((String) c.getValue().get("ingresos")));
        colIngresos.setPrefWidth(150);

        tablaTop.getColumns().addAll(colEvento, colTickets, colIngresos);

        // Ordenar eventos por tickets vendidos
        List<Map.Entry<String, Integer>> sortedEventos = new ArrayList<>(ventasEvento.entrySet());
        sortedEventos.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (int i = 0; i < Math.min(5, sortedEventos.size()); i++) {
            Map.Entry<String, Integer> entry = sortedEventos.get(i);
            Map<String, Object> row = new HashMap<>();
            row.put("evento", entry.getKey());
            row.put("tickets", entry.getValue());

            double ingresos = compraRepo.findAll().stream()
                    .filter(c -> c.getEstado() == EstadoCompra.PAGADA && c.getEvento().getNombre().equals(entry.getKey()))
                    .mapToDouble(Compra::getTotal).sum();
            row.put("ingresos", "$" + String.format("%,.0f", ingresos));
            tablaTop.getItems().add(row);
        }

        topEventosBox.getChildren().addAll(lblTopEventos, tablaTop);

        // ========== 6. Estadísticas adicionales ==========
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15px;");
        Label lblStats = new Label("📋 Estadísticas Adicionales");
        lblStats.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(12);

        double ticketPromedio = compraRepo.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(c -> c.getTotal() / c.getItems().size())
                .average().orElse(0);

        long comprasCanceladas = compraRepo.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.CANCELADA).count();
        long comprasPendientes = compraRepo.findAll().stream()
                .filter(c -> c.getEstado() == EstadoCompra.CREADA).count();
        double porcentajeCancelacion = totalCompras > 0 ? (comprasCanceladas * 100.0 / totalCompras) : 0;

        statsGrid.add(new Label("🎫 Ticket Promedio:"), 0, 0);
        statsGrid.add(new Label("$" + String.format("%,.0f", ticketPromedio)), 1, 0);
        ((Label)statsGrid.getChildren().get(1)).setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold;");

        statsGrid.add(new Label("❌ Compras Canceladas:"), 0, 1);
        statsGrid.add(new Label(String.valueOf(comprasCanceladas)), 1, 1);

        statsGrid.add(new Label("⏳ Compras Pendientes:"), 0, 2);
        statsGrid.add(new Label(String.valueOf(comprasPendientes)), 1, 2);

        statsGrid.add(new Label("📊 Tasa de Cancelación:"), 0, 3);
        statsGrid.add(new Label(String.format("%.1f%%", porcentajeCancelacion)), 1, 3);

        statsBox.getChildren().addAll(lblStats, statsGrid);

        // Organizar en filas
        HBox chartsRow1 = new HBox(20);
        chartsRow1.setAlignment(Pos.CENTER);
        chartsRow1.getChildren().addAll(barChart, pieChart);

        HBox chartsRow2 = new HBox(20);
        chartsRow2.setAlignment(Pos.CENTER);
        chartsRow2.getChildren().addAll(lineChart, topEventosBox);

        root.getChildren().addAll(kpiGrid, chartsRow1, chartsRow2, statsBox);
    }

    private VBox crearCard(String titulo, String valor, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15px; -fx-min-width: 160px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    public VBox getRoot() { return root; }
}