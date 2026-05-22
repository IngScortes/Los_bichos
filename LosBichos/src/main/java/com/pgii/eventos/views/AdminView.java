package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminView {
    private Stage stage;
    private VBox root;

    // Servicios y repositorios
    private EventoService eventoService;
    private RecintoRepository recintoRepository;
    private EventoRepository eventoRepository;
    private UsuarioRepository usuarioRepository;
    private CompraRepository compraRepository;

    // Componentes UI
    private TabPane tabPane;
    private TableView<Evento> tablaEventos;
    private TableView<Usuario> tablaUsuarios;
    private TextArea txtReporte;

    public AdminView(Stage stage) {
        this.stage = stage;
        inicializarServicios();
        crearRecintosSiNoExisten();
        crearUI();
        cargarEventos();
        cargarUsuarios();
    }

    private void inicializarServicios() {
        this.eventoRepository = new EventoRepository();
        this.recintoRepository = new RecintoRepository();
        this.usuarioRepository = new UsuarioRepository();
        this.compraRepository = new CompraRepository();
        this.eventoService = new EventoService(eventoRepository);
    }

    private void crearRecintosSiNoExisten() {
        if (recintoRepository.findAll().isEmpty()) {
            System.out.println("=== CREANDO RECINTOS DE PRUEBA ===");

            Recinto estadio = new Recinto("R001", "Estadio Centenario", "Calle 30 #20-10", "Armenia");
            Recinto teatro = new Recinto("R002", "Teatro Azul", "Carrera 14 #12-45", "Armenia");
            Recinto auditorio = new Recinto("R003", "Auditorio Uniquindío", "Calle 23 #15-30", "Armenia");
            Recinto plaza = new Recinto("R004", "Plaza de Toros", "Calle 5 #10-20", "Manizales");
            Recinto centro = new Recinto("R005", "Centro de Convenciones", "Carrera 8 #15-60", "Pereira");

            recintoRepository.save(estadio);
            recintoRepository.save(teatro);
            recintoRepository.save(auditorio);
            recintoRepository.save(plaza);
            recintoRepository.save(centro);

            System.out.println("✅ " + recintoRepository.findAll().size() + " recintos creados");
        } else {
            System.out.println("✅ Recintos ya existentes: " + recintoRepository.findAll().size());
        }
    }

    private void crearUI() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // Header con botón de regreso
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

        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        // Pestaña: Gestión de Eventos
        Tab tabEventos = new Tab("📅 Gestión de Eventos");
        tabEventos.setContent(crearPanelEventos());
        tabEventos.setClosable(false);

        // Pestaña: Crear Evento
        Tab tabCrearEvento = new Tab("➕ Crear Evento");
        tabCrearEvento.setContent(crearPanelCrearEvento());
        tabCrearEvento.setClosable(false);

        // Pestaña: Gestión de Usuarios
        Tab tabUsuarios = new Tab("👥 Usuarios");
        tabUsuarios.setContent(crearPanelUsuarios());
        tabUsuarios.setClosable(false);

        // Pestaña: Reportes
        Tab tabReportes = new Tab("📊 Reportes");
        tabReportes.setContent(crearPanelReportes());
        tabReportes.setClosable(false);

        // Pestaña: Recintos
        Tab tabRecintos = new Tab("🏟️ Recintos");
        tabRecintos.setContent(crearPanelRecintos());
        tabRecintos.setClosable(false);

        tabPane.getTabs().addAll(tabEventos, tabCrearEvento, tabUsuarios, tabReportes, tabRecintos);

        root.getChildren().addAll(header, tabPane);
    }

    private VBox crearPanelEventos() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button btnRefrescar = new Button("🔄 Refrescar");
        btnRefrescar.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnRefrescar.setOnAction(e -> cargarEventos());

        Button btnPublicar = new Button("📢 Publicar");
        btnPublicar.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnPublicar.setOnAction(e -> cambiarEstadoEvento(EstadoEvento.PUBLICADO));

        Button btnPausar = new Button("⏸️ Pausar");
        btnPausar.setStyle("-fx-background-color: #ffa502; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnPausar.setOnAction(e -> cambiarEstadoEvento(EstadoEvento.PAUSADO));

        Button btnCancelar = new Button("❌ Cancelar");
        btnCancelar.setStyle("-fx-background-color: #f5576c; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnCancelar.setOnAction(e -> cambiarEstadoEvento(EstadoEvento.CANCELADO));

        Button btnEliminar = new Button("🗑️ Eliminar");
        btnEliminar.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnEliminar.setOnAction(e -> eliminarEvento());

        toolbar.getChildren().addAll(btnRefrescar, btnPublicar, btnPausar, btnCancelar, btnEliminar);

        // Tabla de eventos
        tablaEventos = new TableView<>();
        tablaEventos.setPrefHeight(400);

        TableColumn<Evento, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getIdEvento()));
        colId.setPrefWidth(80);

        TableColumn<Evento, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
        colNombre.setPrefWidth(200);

        TableColumn<Evento, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategoria().toString()));
        colCategoria.setPrefWidth(100);

        TableColumn<Evento, String> colCiudad = new TableColumn<>("Ciudad");
        colCiudad.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCiudad()));
        colCiudad.setPrefWidth(100);

        TableColumn<Evento, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colFecha.setPrefWidth(150);

        TableColumn<Evento, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEstado().toString()));
        colEstado.setPrefWidth(100);

        tablaEventos.getColumns().addAll(colId, colNombre, colCategoria, colCiudad, colFecha, colEstado);

        panel.getChildren().addAll(toolbar, tablaEventos);
        return panel;
    }

    private VBox crearPanelCrearEvento() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");

        Label subtitulo = new Label("Crear Nuevo Evento");
        subtitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(10));

        // Campos del formulario
        TextField txtId = new TextField();
        txtId.setPromptText("Ej: E010");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del evento");

        ComboBox<CategoriaEvento> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll(CategoriaEvento.values());
        cbCategoria.setPromptText("Seleccionar");

        TextField txtCiudad = new TextField();
        txtCiudad.setPromptText("Ciudad");

        DatePicker dpFecha = new DatePicker(LocalDate.now());
        dpFecha.setPromptText("dd/MM/yyyy");

        Spinner<Integer> spHora = new Spinner<>(0, 23, 20);
        Spinner<Integer> spMinuto = new Spinner<>(0, 59, 0);

        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripción del evento");

        // Recinto ComboBox
        ComboBox<Recinto> cbRecinto = new ComboBox<>();
        List<Recinto> recintos = recintoRepository.findAll();
        System.out.println("Recintos disponibles para crear evento: " + recintos.size());
        cbRecinto.getItems().addAll(recintos);
        cbRecinto.setPromptText("Seleccionar recinto");

        // Mostrar nombre y ciudad en el ComboBox
        cbRecinto.setCellFactory(lv -> new ListCell<Recinto>() {
            @Override
            protected void updateItem(Recinto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " (" + item.getCiudad() + ")");
            }
        });
        cbRecinto.setButtonCell(new ListCell<Recinto>() {
            @Override
            protected void updateItem(Recinto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " (" + item.getCiudad() + ")");
            }
        });

        form.add(new Label("ID:"), 0, 0);
        form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1);
        form.add(txtNombre, 1, 1);
        form.add(new Label("Categoría:"), 0, 2);
        form.add(cbCategoria, 1, 2);
        form.add(new Label("Ciudad:"), 0, 3);
        form.add(txtCiudad, 1, 3);
        form.add(new Label("Fecha:"), 0, 4);
        form.add(dpFecha, 1, 4);
        form.add(new Label("Hora:"), 2, 4);
        form.add(new HBox(5, spHora, new Label(":"), spMinuto), 3, 4);
        form.add(new Label("Descripción:"), 0, 5);
        form.add(txtDescripcion, 1, 5, 3, 1);
        form.add(new Label("Recinto:"), 0, 6);
        form.add(cbRecinto, 1, 6);

        Button btnCrear = new Button("✨ Crear Evento");
        btnCrear.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25px; -fx-padding: 10 30;");

        Label lblMensaje = new Label();
        lblMensaje.setStyle("-fx-text-fill: #e94560;");

        btnCrear.setOnAction(e -> {
            if (validarCampos(txtId, txtNombre, cbCategoria, txtCiudad, dpFecha, cbRecinto)) {
                LocalDateTime fechaHora = LocalDateTime.of(dpFecha.getValue(), LocalTime.of(spHora.getValue(), spMinuto.getValue()));

                Evento nuevoEvento = new Evento(
                        txtId.getText(),
                        txtNombre.getText(),
                        cbCategoria.getValue(),
                        txtDescripcion.getText(),
                        txtCiudad.getText(),
                        fechaHora,
                        cbRecinto.getValue()
                );
                nuevoEvento.setEstado(EstadoEvento.PUBLICADO);

                eventoRepository.save(nuevoEvento);
                lblMensaje.setText("✅ Evento creado exitosamente!");
                lblMensaje.setStyle("-fx-text-fill: #43e97b;");
                limpiarFormulario(txtId, txtNombre, cbCategoria, txtCiudad, dpFecha, txtDescripcion, cbRecinto);
                cargarEventos();
            } else {
                lblMensaje.setText("❌ Complete todos los campos obligatorios");
            }

        });

        HBox buttonBox = new HBox(btnCrear);
        buttonBox.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(subtitulo, form, buttonBox, lblMensaje);
        return panel;
    }

    private VBox crearPanelUsuarios() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Button btnRefrescar = new Button("🔄 Refrescar");
        btnRefrescar.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnRefrescar.setOnAction(e -> cargarUsuarios());

        tablaUsuarios = new TableView<>();
        tablaUsuarios.setPrefHeight(400);

        TableColumn<Usuario, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        colId.setPrefWidth(80);

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombreCompleto()));
        colNombre.setPrefWidth(200);

        TableColumn<Usuario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
        colEmail.setPrefWidth(200);

        TableColumn<Usuario, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTelefono()));
        colTelefono.setPrefWidth(120);

        tablaUsuarios.getColumns().addAll(colId, colNombre, colEmail, colTelefono);

        panel.getChildren().addAll(btnRefrescar, tablaUsuarios);
        return panel;
    }

    private VBox crearPanelReportes() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label subtitulo = new Label("Reporte de Ventas");
        subtitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button btnGenerar = new Button("📄 Generar Reporte");
        btnGenerar.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 20;");
        btnGenerar.setOnAction(e -> generarReporte());

        txtReporte = new TextArea();
        txtReporte.setEditable(false);
        txtReporte.setPrefHeight(400);
        txtReporte.setStyle("-fx-font-family: monospace;");

        panel.getChildren().addAll(subtitulo, btnGenerar, txtReporte);
        return panel;
    }

    private VBox crearPanelRecintos() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label subtitulo = new Label("🏟️ Gestión de Recintos");
        subtitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Botón para refrescar
        Button btnRefrescar = new Button("🔄 Refrescar");
        btnRefrescar.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");

        // Tabla de recintos
        TableView<Recinto> tablaRecintos = new TableView<>();
        tablaRecintos.setPrefHeight(400);

        TableColumn<Recinto, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getIdRecinto()));
        colId.setPrefWidth(80);

        TableColumn<Recinto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
        colNombre.setPrefWidth(250);

        TableColumn<Recinto, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDireccion()));
        colDireccion.setPrefWidth(250);

        TableColumn<Recinto, String> colCiudad = new TableColumn<>("Ciudad");
        colCiudad.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCiudad()));
        colCiudad.setPrefWidth(100);

        tablaRecintos.getColumns().addAll(colId, colNombre, colDireccion, colCiudad);

        // Cargar datos
        tablaRecintos.getItems().addAll(recintoRepository.findAll());

        btnRefrescar.setOnAction(e -> {
            tablaRecintos.getItems().clear();
            tablaRecintos.getItems().addAll(recintoRepository.findAll());
        });

        panel.getChildren().addAll(subtitulo, btnRefrescar, tablaRecintos);
        return panel;
    }

    private void cargarEventos() {
        if (tablaEventos != null) {
            tablaEventos.getItems().clear();
            List<Evento> eventos = eventoRepository.findAll();
            System.out.println("Cargando " + eventos.size() + " eventos");
            tablaEventos.getItems().addAll(eventos);
        }
    }

    private void cargarUsuarios() {
        if (tablaUsuarios != null) {
            tablaUsuarios.getItems().clear();
            tablaUsuarios.getItems().addAll(usuarioRepository.findAll());
        }
    }

    private void cambiarEstadoEvento(EstadoEvento nuevoEstado) {
        Evento selected = tablaEventos.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setEstado(nuevoEstado);
            eventoRepository.save(selected);
            cargarEventos();
            mostrarAlerta("Éxito", "Estado cambiado a " + nuevoEstado);
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
                eventoRepository.deleteById(selected.getIdEvento());
                cargarEventos();
                mostrarAlerta("Éxito", "Evento eliminado");
            }
        } else {
            mostrarAlerta("Error", "Seleccione un evento");
        }
    }

    private void generarReporte() {
        List<Compra> compras = compraRepository.findAll();
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE VENTAS ===\n\n");
        reporte.append(String.format("%-12s %-25s %-12s %-10s\n", "ID Compra", "Evento", "Total", "Estado"));
        reporte.append("------------------------------------------------------------\n");

        for (Compra c : compras) {
            String nombreEvento = c.getEvento().getNombre();
            if (nombreEvento.length() > 22) nombreEvento = nombreEvento.substring(0, 19) + "...";
            reporte.append(String.format("%-12s %-25s $%-11.0f %-10s\n",
                    c.getIdCompra(),
                    nombreEvento,
                    c.getTotal(),
                    c.getEstado()));
        }

        double totalIngresos = compras.stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal)
                .sum();

        reporte.append("\n============================================================\n");
        reporte.append("TOTAL INGRESOS: $" + String.format("%,.0f", totalIngresos) + "\n");
        reporte.append("TOTAL COMPRAS: " + compras.size() + "\n");
        reporte.append("COMPRAS PAGADAS: " + compras.stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).count() + "\n");

        txtReporte.setText(reporte.toString());
    }

    private boolean validarCampos(TextField id, TextField nombre, ComboBox<?> categoria, TextField ciudad, DatePicker fecha, ComboBox<?> recinto) {
        return !id.getText().isEmpty() && !nombre.getText().isEmpty() &&
                categoria.getValue() != null && !ciudad.getText().isEmpty() &&
                fecha.getValue() != null && recinto.getValue() != null;
    }

    private void limpiarFormulario(TextField id, TextField nombre, ComboBox<CategoriaEvento> categoria,
                                   TextField ciudad, DatePicker fecha, TextField descripcion, ComboBox<Recinto> recinto) {
        id.clear();
        nombre.clear();
        categoria.setValue(null);
        ciudad.clear();
        fecha.setValue(LocalDate.now());
        descripcion.clear();
        recinto.setValue(null);
    }

    private void volverDashboard() {
        DashboardView dashboard = new DashboardView(stage);
        Scene scene = new Scene(dashboard.getRoot(), 1300, 800);
        stage.setScene(scene);
        stage.setTitle("Plataforma de Gestión - Dashboard");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}