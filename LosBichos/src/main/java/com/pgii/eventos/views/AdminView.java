package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.patterns.structural.adapter.ApachePOICSVAdapter;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.ReporteService;
import com.pgii.eventos.patterns.structural.adapter.PDFBoxAdapter;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.net.URI;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class AdminView {
    private Stage stage;
    private VBox root;
    private Scene escenaAnterior;

    private EventoRepository eventoRepo;
    private RecintoRepository recintoRepo;
    private CompraRepository compraRepo;
    private UsuarioRepository usuarioRepo;

    private TableView<Evento> tablaEventos;
    private TableView<Zona> tablaZonas;
    private List<Zona> zonasTemporales;

    public AdminView(Stage stage) {
        this.stage = stage;
        this.eventoRepo = EventoRepository.getInstance();
        this.recintoRepo = RecintoRepository.getInstance();
        this.compraRepo = CompraRepository.getInstance();
        this.usuarioRepo = UsuarioRepository.getInstance();
        this.zonasTemporales = new ArrayList<>();
        crearUI();
        cargarEventos();
    }

    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void crearUI() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Button btnVolver = new Button("← Volver al Dashboard");
        btnVolver.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 15;");
        btnVolver.setOnAction(e -> volverDashboard());

        Label titulo = new Label("👑 Panel de Administración");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(btnVolver, spacer, titulo);

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab tabEventos = new Tab("📅 Gestión de Eventos");
        tabEventos.setContent(crearPanelEventos());
        tabEventos.setClosable(false);

        Tab tabCrear = new Tab("➕ Crear Evento");
        tabCrear.setContent(crearPanelCrearEvento());
        tabCrear.setClosable(false);

        // ========== PESTAÑA DE REPORTES ==========
        Tab tabReportes = new Tab("📊 Reportes");
        tabReportes.setContent(crearPanelReportes());
        tabReportes.setClosable(false);
        // =======================================

        tabPane.getTabs().addAll(tabEventos, tabCrear, tabReportes);

        root.getChildren().addAll(header, tabPane);
    }

    private VBox crearPanelEventos() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        HBox toolbar = new HBox(10);

        Button btnRefrescar = new Button("🔄 Refrescar");
        btnRefrescar.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnRefrescar.setOnAction(e -> cargarEventos());

        Button btnPublicar = new Button("📢 Publicar");
        btnPublicar.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnPublicar.setOnAction(e -> cambiarEstado(EstadoEvento.PUBLICADO));

        Button btnPausar = new Button("⏸️ Pausar");
        btnPausar.setStyle("-fx-background-color: #ffa502; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnPausar.setOnAction(e -> cambiarEstado(EstadoEvento.PAUSADO));

        Button btnCancelar = new Button("❌ Cancelar");
        btnCancelar.setStyle("-fx-background-color: #f5576c; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnCancelar.setOnAction(e -> cambiarEstado(EstadoEvento.CANCELADO));

        Button btnEliminar = new Button("🗑️ Eliminar");
        btnEliminar.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnEliminar.setOnAction(e -> eliminarEvento());

        toolbar.getChildren().addAll(btnRefrescar, btnPublicar, btnPausar, btnCancelar, btnEliminar);

        tablaEventos = new TableView<>();
        tablaEventos.setPrefHeight(400);

        TableColumn<Evento, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getIdEvento()));
        colId.setPrefWidth(80);

        TableColumn<Evento, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colNombre.setPrefWidth(250);

        TableColumn<Evento, String> colCiudad = new TableColumn<>("Ciudad");
        colCiudad.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCiudad()));

        TableColumn<Evento, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colFecha.setPrefWidth(150);

        TableColumn<Evento, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado().toString()));

        tablaEventos.getColumns().addAll(colId, colNombre, colCiudad, colFecha, colEstado);

        panel.getChildren().addAll(toolbar, tablaEventos);
        return panel;
    }

    private VBox crearPanelCrearEvento() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(20));

        Label subtitulo = new Label("✨ Crear Nuevo Evento");
        subtitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Datos básicos
        TitledPane datosBasicos = new TitledPane();
        datosBasicos.setText("📋 Datos Básicos del Evento");
        datosBasicos.setCollapsible(true);
        datosBasicos.setExpanded(true);

        GridPane formBasico = new GridPane();
        formBasico.setHgap(15);
        formBasico.setVgap(12);
        formBasico.setPadding(new Insets(15));

        TextField txtId = new TextField();
        txtId.setPromptText("Ej: E010");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del evento");

        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción del evento");
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setWrapText(true);

        ComboBox<Recinto> cbRecinto = new ComboBox<>();
        cbRecinto.getItems().addAll(recintoRepo.findAll());
        cbRecinto.setPromptText("Seleccionar recinto");

        cbRecinto.setCellFactory(lv -> new ListCell<Recinto>() {
            @Override
            protected void updateItem(Recinto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " (" + item.getCiudad() + ")");
            }
        });

        TextField txtCiudad = new TextField();
        txtCiudad.setPromptText("Ciudad");

        ComboBox<CategoriaEvento> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll(CategoriaEvento.values());
        cbCategoria.setPromptText("Seleccionar categoría");

        DatePicker dpFecha = new DatePicker(LocalDate.now());
        Spinner<Integer> spHora = new Spinner<>(0, 23, 20);
        Spinner<Integer> spMinuto = new Spinner<>(0, 59, 0);

        formBasico.add(new Label("ID:*"), 0, 0);
        formBasico.add(txtId, 1, 0);
        formBasico.add(new Label("Nombre:*"), 0, 1);
        formBasico.add(txtNombre, 1, 1);
        formBasico.add(new Label("Descripción:"), 0, 2);
        formBasico.add(txtDescripcion, 1, 2);
        formBasico.add(new Label("Recinto:*"), 0, 3);
        formBasico.add(cbRecinto, 1, 3);
        formBasico.add(new Label("Ciudad:*"), 0, 4);
        formBasico.add(txtCiudad, 1, 4);
        formBasico.add(new Label("Categoría:*"), 0, 5);
        formBasico.add(cbCategoria, 1, 5);
        formBasico.add(new Label("Fecha:*"), 0, 6);
        formBasico.add(dpFecha, 1, 6);
        formBasico.add(new Label("Hora:*"), 0, 7);
        formBasico.add(new HBox(10, spHora, new Label(":"), spMinuto), 1, 7);

        datosBasicos.setContent(formBasico);

        // Gestión de zonas
        TitledPane gestionZonas = new TitledPane();
        gestionZonas.setText("🎪 Zonas del Evento");
        gestionZonas.setCollapsible(true);
        gestionZonas.setExpanded(true);

        VBox zonasContent = new VBox(15);
        zonasContent.setPadding(new Insets(15));

        GridPane formZona = new GridPane();
        formZona.setHgap(10);
        formZona.setVgap(10);
        formZona.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 10px;");

        TextField txtZonaNombre = new TextField();
        txtZonaNombre.setPromptText("Nombre (Ej: VIP, General)");

        Spinner<Integer> spCapacidad = new Spinner<>(1, 1000, 100);
        spCapacidad.setEditable(true);

        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Precio (Ej: 150000)");

        Button btnAgregarZona = new Button("➕ Agregar Zona");
        btnAgregarZona.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 15;");

        formZona.add(new Label("Nombre:"), 0, 0);
        formZona.add(txtZonaNombre, 1, 0);
        formZona.add(new Label("Capacidad:"), 2, 0);
        formZona.add(spCapacidad, 3, 0);
        formZona.add(new Label("Precio:"), 4, 0);
        formZona.add(txtPrecio, 5, 0);
        formZona.add(btnAgregarZona, 6, 0);

        tablaZonas = new TableView<>();
        tablaZonas.setPrefHeight(200);

        TableColumn<Zona, String> colZonaNombre = new TableColumn<>("Zona");
        colZonaNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colZonaNombre.setPrefWidth(150);

        TableColumn<Zona, Integer> colCapacidad = new TableColumn<>("Capacidad");
        colCapacidad.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCapacidad()).asObject());
        colCapacidad.setPrefWidth(100);

        TableColumn<Zona, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPrecioBase()).asObject());
        colPrecio.setPrefWidth(120);

        TableColumn<Zona, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(80);
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("🗑️");
            {
                btnEliminar.setStyle("-fx-background-color: #f5576c; -fx-text-fill: white; -fx-background-radius: 15px; -fx-cursor: hand;");
                btnEliminar.setOnAction(e -> {
                    Zona zona = getTableView().getItems().get(getIndex());
                    zonasTemporales.remove(zona);
                    tablaZonas.getItems().remove(zona);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });

        tablaZonas.getColumns().addAll(colZonaNombre, colCapacidad, colPrecio, colAcciones);

        btnAgregarZona.setOnAction(e -> {
            String nombre = txtZonaNombre.getText().trim();
            int capacidad = spCapacidad.getValue();
            double precio;
            try {
                precio = Double.parseDouble(txtPrecio.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Ingrese un precio válido");
                return;
            }
            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "Ingrese un nombre para la zona");
                return;
            }
            Zona nuevaZona = new Zona("TEMP_" + System.currentTimeMillis() + "_" + nombre, nombre, capacidad, precio, null);
            zonasTemporales.add(nuevaZona);
            tablaZonas.getItems().add(nuevaZona);
            txtZonaNombre.clear();
            txtPrecio.clear();
            spCapacidad.getValueFactory().setValue(100);
        });

        zonasContent.getChildren().addAll(formZona, tablaZonas);
        gestionZonas.setContent(zonasContent);

        Button btnCrearEvento = new Button("✨ CREAR EVENTO COMPLETO");
        btnCrearEvento.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 12 25; -fx-background-radius: 30px; -fx-cursor: hand;");
        btnCrearEvento.setMaxWidth(Double.MAX_VALUE);

        Label lblMensaje = new Label();
        lblMensaje.setStyle("-fx-text-fill: #e94560; -fx-padding: 10;");

        btnCrearEvento.setOnAction(e -> {
            if (validarCamposBasicos(txtId, txtNombre, cbRecinto, txtCiudad, cbCategoria, dpFecha)) {
                if (zonasTemporales.isEmpty()) {
                    mostrarAlerta("Error", "Debe agregar al menos una zona al evento");
                    return;
                }
                crearEventoCompleto(txtId, txtNombre, txtDescripcion, cbRecinto, txtCiudad,
                        cbCategoria, dpFecha, spHora, spMinuto);
                lblMensaje.setText("✅ Evento creado exitosamente con " + zonasTemporales.size() + " zonas!");
                lblMensaje.setStyle("-fx-text-fill: #43e97b; -fx-padding: 10;");
                zonasTemporales.clear();
                tablaZonas.getItems().clear();
                limpiarFormularioBasico(txtId, txtNombre, txtDescripcion, cbRecinto, txtCiudad, cbCategoria, dpFecha);
                cargarEventos();
            } else {
                lblMensaje.setText("❌ Complete todos los campos obligatorios (*)");
                lblMensaje.setStyle("-fx-text-fill: #e94560; -fx-padding: 10;");
            }
        });

        contenido.getChildren().addAll(subtitulo, datosBasicos, gestionZonas, btnCrearEvento, lblMensaje);
        scrollPane.setContent(contenido);
        panel.getChildren().add(scrollPane);

        return panel;
    }

    private void crearEventoCompleto(TextField txtId, TextField txtNombre, TextArea txtDescripcion,
                                     ComboBox<Recinto> cbRecinto, TextField txtCiudad,
                                     ComboBox<CategoriaEvento> cbCategoria, DatePicker dpFecha,
                                     Spinner<Integer> spHora, Spinner<Integer> spMinuto) {

        LocalDateTime fechaHora = LocalDateTime.of(dpFecha.getValue(),
                LocalTime.of(spHora.getValue(), spMinuto.getValue()));

        Recinto recinto = cbRecinto.getValue();

        Evento nuevoEvento = new Evento(txtId.getText(), txtNombre.getText(), cbCategoria.getValue(),
                txtDescripcion.getText(), txtCiudad.getText(), fechaHora, recinto);
        nuevoEvento.setEstado(EstadoEvento.BORRADOR);

        for (Zona zonaTemp : zonasTemporales) {
            String idZona = "Z_" + nuevoEvento.getIdEvento() + "_" + zonaTemp.getNombre();
            Zona nuevaZona = new Zona(idZona, zonaTemp.getNombre(),
                    zonaTemp.getCapacidad(), zonaTemp.getPrecioBase(), recinto);

            for (int i = 1; i <= zonaTemp.getCapacidad(); i++) {
                String fila = zonaTemp.getNombre().substring(0, 1);
                String idAsiento = idZona + "_" + i;
                Asiento asiento = new Asiento(idAsiento, fila, i, nuevaZona);
                nuevaZona.agregarAsiento(asiento);
                AsientoRepository.getInstance().save(asiento);
            }

            nuevoEvento.agregarZona(nuevaZona);
            recinto.agregarZona(nuevaZona);
            ZonaRepository.getInstance().save(nuevaZona);
        }

        eventoRepo.save(nuevoEvento);
        recintoRepo.save(recinto);
    }

    // ========== PESTAÑA DE REPORTES ==========
    private VBox crearPanelReportes() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 20px;");

        Label titulo = new Label("📊 Exportar Reportes");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label descripcion = new Label("Genere reportes en formato Excel o PDF de las diferentes métricas del sistema.");
        descripcion.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        // ========== REPORTES EXCEL ==========
        Label lblExcel = new Label("📄 Reportes Excel");
        lblExcel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-padding: 10 0 0 0;");

        Button btnVentas = new Button("📊 Exportar Reporte de Ventas (Excel)");
        btnVentas.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 25px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: 600;");
        btnVentas.setMaxWidth(Double.MAX_VALUE);
        btnVentas.setOnAction(e -> exportarReporteVentas());

        Button btnCompradores = new Button("👥 Exportar Compradores por Evento (Excel)");
        btnCompradores.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 25px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: 600;");
        btnCompradores.setMaxWidth(Double.MAX_VALUE);
        btnCompradores.setOnAction(e -> exportarCompradoresPorEvento());

        Button btnEstadisticas = new Button("📈 Exportar Estadísticas Generales (Excel)");
        btnEstadisticas.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 25px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: 600;");
        btnEstadisticas.setMaxWidth(Double.MAX_VALUE);
        btnEstadisticas.setOnAction(e -> exportarEstadisticas());

        // ========== REPORTES PDF ==========
        Label lblPDF = new Label("📑 Reportes PDF");
        lblPDF.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-padding: 15 0 0 0;");

        Button btnVentasPDF = new Button("📊 Exportar Reporte de Ventas (PDF)");
        btnVentasPDF.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 25px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: 600;");
        btnVentasPDF.setMaxWidth(Double.MAX_VALUE);
        btnVentasPDF.setOnAction(e -> exportarReporteVentasPDF());

        Button btnEstadisticasPDF = new Button("📈 Exportar Estadísticas Generales (PDF)");
        btnEstadisticasPDF.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 25px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: 600;");
        btnEstadisticasPDF.setMaxWidth(Double.MAX_VALUE);
        btnEstadisticasPDF.setOnAction(e -> exportarEstadisticasPDF());

        panel.getChildren().addAll(titulo, descripcion, lblExcel, btnVentas, btnCompradores, btnEstadisticas, lblPDF, btnVentasPDF, btnEstadisticasPDF);
        return panel;
    }

    private void exportarReporteVentas() {
        try {
            ReporteService reporteService = new ReporteService(compraRepo, eventoRepo, usuarioRepo);
            ApachePOICSVAdapter adapter = new ApachePOICSVAdapter();
            String nombreArchivo = "reporte_ventas_" + System.currentTimeMillis() + ".xlsx";
            reporteService.exportarReporteVentas(adapter, nombreArchivo);
            mostrarAlerta("Éxito", "Reporte generado: " + nombreArchivo);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    private void exportarCompradoresPorEvento() {
        Dialog<Evento> dialog = new Dialog<>();
        dialog.setTitle("Seleccionar Evento");
        dialog.setHeaderText("Elija el evento para exportar compradores");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<Evento> cbEvento = new ComboBox<>();
        cbEvento.getItems().addAll(eventoRepo.findAll());
        cbEvento.setPromptText("Seleccione un evento");
        cbEvento.setCellFactory(lv -> new ListCell<Evento>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        Button btnExportar = new Button("Exportar");
        btnExportar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 15;");

        content.getChildren().addAll(new Label("Evento:"), cbEvento, btnExportar);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        btnExportar.setOnAction(e -> {
            Evento evento = cbEvento.getValue();
            if (evento != null) {
                try {
                    ReporteService reporteService = new ReporteService(compraRepo, eventoRepo, usuarioRepo);
                    ApachePOICSVAdapter adapter = new ApachePOICSVAdapter();
                    String nombreArchivo = "compradores_" + evento.getIdEvento() + "_" + System.currentTimeMillis() + ".xlsx";
                    reporteService.exportarCompradoresPorEvento(evento.getIdEvento(), adapter, nombreArchivo);
                    mostrarAlerta("Éxito", "Reporte generado: " + nombreArchivo);
                    dialog.close();
                } catch (Exception ex) {
                    mostrarAlerta("Error", ex.getMessage());
                }
            } else {
                mostrarAlerta("Error", "Seleccione un evento");
            }
        });

        dialog.showAndWait();
    }

    private void exportarEstadisticas() {
        try {
            ReporteService reporteService = new ReporteService(compraRepo, eventoRepo, usuarioRepo);
            ApachePOICSVAdapter adapter = new ApachePOICSVAdapter();
            String nombreArchivo = "estadisticas_" + System.currentTimeMillis() + ".xlsx";
            reporteService.exportarEstadisticas(adapter, nombreArchivo);
            mostrarAlerta("Éxito", "Estadísticas exportadas: " + nombreArchivo);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }
    // ==========================================

    private boolean validarCamposBasicos(TextField id, TextField nombre, ComboBox<Recinto> recinto,
                                         TextField ciudad, ComboBox<CategoriaEvento> categoria, DatePicker fecha) {
        return !id.getText().isEmpty() && !nombre.getText().isEmpty() &&
                recinto.getValue() != null && !ciudad.getText().isEmpty() &&
                categoria.getValue() != null && fecha.getValue() != null;
    }

    private void limpiarFormularioBasico(TextField id, TextField nombre, TextArea descripcion,
                                         ComboBox<Recinto> recinto, TextField ciudad,
                                         ComboBox<CategoriaEvento> categoria, DatePicker fecha) {
        id.clear();
        nombre.clear();
        descripcion.clear();
        recinto.setValue(null);
        ciudad.clear();
        categoria.setValue(null);
        fecha.setValue(LocalDate.now());
    }

    private void cargarEventos() {
        if (tablaEventos != null) {
            tablaEventos.getItems().clear();
            tablaEventos.getItems().addAll(eventoRepo.findAll());
        }
    }

    private void cambiarEstado(EstadoEvento estado) {
        Evento selected = tablaEventos.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setEstado(estado);
            eventoRepo.save(selected);
            cargarEventos();
            mostrarAlerta("Éxito", "Estado cambiado a " + estado);
        } else {
            mostrarAlerta("Error", "Seleccione un evento");
        }
    }

    private void eliminarEvento() {
        Evento selected = tablaEventos.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar");
            confirm.setContentText("¿Eliminar evento " + selected.getNombre() + "?");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                eventoRepo.deleteById(selected.getIdEvento());
                cargarEventos();
                mostrarAlerta("Éxito", "Evento eliminado");
            }
        }
    }

    private void volverDashboard() {
        if (escenaAnterior != null) {
            stage.setScene(escenaAnterior);
        } else {
            DashboardView dashboard = new DashboardView(stage);
            Scene scene = new Scene(dashboard.getRoot(), 1300, 800);
            stage.setScene(scene);
        }
        stage.setTitle("Dashboard");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void exportarReporteVentasPDF() {
        try {
            ReporteService reporteService = new ReporteService(compraRepo, eventoRepo, usuarioRepo);
            PDFBoxAdapter pdfAdapter = new PDFBoxAdapter();
            String nombreArchivo = "reporte_ventas_" + System.currentTimeMillis() + ".pdf";
            reporteService.exportarReporteVentasPDF(pdfAdapter, nombreArchivo);
            mostrarAlerta("Éxito", "PDF generado: " + nombreArchivo);
            abrirArchivo(nombreArchivo);  // ← Agregar esta línea
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }
    private void exportarEstadisticasPDF() {
        try {
            ReporteService reporteService = new ReporteService(compraRepo, eventoRepo, usuarioRepo);
            PDFBoxAdapter pdfAdapter = new PDFBoxAdapter();
            String nombreArchivo = "estadisticas_" + System.currentTimeMillis() + ".pdf";
            reporteService.exportarEstadisticasPDF(pdfAdapter, nombreArchivo);
            mostrarAlerta("Éxito", "PDF generado: " + nombreArchivo);
            abrirArchivo(nombreArchivo);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirArchivo(String ruta) {
        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                String rutaAbsoluta = archivo.getAbsolutePath();

                if (ruta.endsWith(".pdf")) {
                    // Para PDF: abrir en navegador
                    String uri = "file:///" + rutaAbsoluta.replace("\\", "/").replace(" ", "%20");
                    Desktop.getDesktop().browse(new URI(uri));
                    System.out.println("📂 PDF abierto en navegador: " + uri);
                } else {
                    // Para Excel y otros: abrir con programa predeterminado
                    Desktop.getDesktop().open(archivo);
                    System.out.println("📂 Archivo abierto: " + rutaAbsoluta);
                }
            } else {
                System.out.println("❌ Archivo no encontrado: " + ruta);
            }
        } catch (Exception e) {
            System.out.println("❌ Error al abrir: " + e.getMessage());
            mostrarAlerta("Archivo generado", "El archivo se guardó en:\n" + new File(ruta).getAbsolutePath());
        }
    }
    public VBox getRoot() { return root; }
}