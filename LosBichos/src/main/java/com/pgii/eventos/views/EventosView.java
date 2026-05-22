package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EventosView {
    private Stage stage;
    private VBox root;
    private EventoService eventoService;
    private CompraService compraService;
    private Persona usuarioActivo;

    // Filtros
    private ComboBox<String> cbCiudad;
    private ComboBox<CategoriaEvento> cbCategoria;
    private DatePicker dpFecha;
    private Button btnBuscar;
    private VBox eventosContainer;

    public EventosView(Stage stage) {
        this.stage = stage;
        this.usuarioActivo = GestorSesion.getInstance().getUsuarioActivo();
        inicializarServicios();
        crearUI();
        cargarEventos();
    }

    private void inicializarServicios() {
        EventoRepository eventoRepo = new EventoRepository();
        CompraRepository compraRepo = new CompraRepository();
        AsientoRepository asientoRepo = new AsientoRepository();

        this.eventoService = new EventoService(eventoRepo);
        this.compraService = new CompraService(compraRepo, asientoRepo);
    }

    private void crearUI() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // Título
        Label titulo = new Label("🎪 Eventos Disponibles");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Panel de filtros
        VBox filtrosBox = crearPanelFiltros();

        // Contenedor de eventos (scroll)
        eventosContainer = new VBox(15);
        eventosContainer.setPadding(new Insets(10));

        ScrollPane scrollEventos = new ScrollPane(eventosContainer);
        scrollEventos.setFitToWidth(true);
        scrollEventos.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollEventos.setPrefHeight(500);

        root.getChildren().addAll(titulo, filtrosBox, scrollEventos);
    }

    private VBox crearPanelFiltros() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-padding: 15;");

        Label lblFiltros = new Label("🔍 Filtros de búsqueda");
        lblFiltros.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox filtrosRow = new HBox(15);
        filtrosRow.setAlignment(Pos.CENTER_LEFT);

        // Ciudad
        VBox ciudadBox = new VBox(5);
        Label lblCiudad = new Label("Ciudad:");
        cbCiudad = new ComboBox<>();
        cbCiudad.getItems().addAll("Todas", "Armenia", "Pereira", "Manizales", "Cali", "Medellín", "Bogotá");
        cbCiudad.setValue("Todas");
        ciudadBox.getChildren().addAll(lblCiudad, cbCiudad);

        // Categoría
        VBox categoriaBox = new VBox(5);
        Label lblCategoria = new Label("Categoría:");
        cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll(CategoriaEvento.values());
        cbCategoria.setPromptText("Todas");
        categoriaBox.getChildren().addAll(lblCategoria, cbCategoria);

        // Fecha
        VBox fechaBox = new VBox(5);
        Label lblFecha = new Label("Desde fecha:");
        dpFecha = new DatePicker();
        fechaBox.getChildren().addAll(lblFecha, dpFecha);

        // Botón buscar
        btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 20;");
        btnBuscar.setOnAction(e -> cargarEventos());

        // Botón limpiar
        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666; -fx-background-radius: 20px; -fx-padding: 8 20;");
        btnLimpiar.setOnAction(e -> {
            cbCiudad.setValue("Todas");
            cbCategoria.setValue(null);
            dpFecha.setValue(null);
            cargarEventos();
        });

        HBox buttonsBox = new HBox(10, btnBuscar, btnLimpiar);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);

        filtrosRow.getChildren().addAll(ciudadBox, categoriaBox, fechaBox, buttonsBox);
        panel.getChildren().addAll(lblFiltros, filtrosRow);

        return panel;
    }

    private void cargarEventos() {
        eventosContainer.getChildren().clear();

        // Obtener eventos publicados
        List<Evento> eventos = eventoService.listarEventosPublicados();

        System.out.println("=== EVENTOS ENCONTRADOS ===");
        System.out.println("Total eventos publicados: " + eventos.size());

        // Depuración: mostrar eventos encontrados
        for (Evento e : eventos) {
            System.out.println("- " + e.getNombre() + " | Ciudad: " + e.getCiudad() + " | Estado: " + e.getEstado());
        }

        // Aplicar filtros
        String ciudad = cbCiudad.getValue();
        if (ciudad != null && !ciudad.equals("Todas")) {
            eventos = eventos.stream()
                    .filter(e -> e.getCiudad() != null && e.getCiudad().equalsIgnoreCase(ciudad))
                    .collect(Collectors.toList());
        }

        if (cbCategoria.getValue() != null) {
            CategoriaEvento categoria = cbCategoria.getValue();
            eventos = eventos.stream()
                    .filter(e -> e.getCategoria() == categoria)
                    .collect(Collectors.toList());
        }

        if (dpFecha.getValue() != null) {
            LocalDate fecha = dpFecha.getValue();
            eventos = eventos.stream()
                    .filter(e -> e.getFechaHora().toLocalDate().isAfter(fecha) || e.getFechaHora().toLocalDate().isEqual(fecha))
                    .collect(Collectors.toList());
        }

        if (eventos.isEmpty()) {
            Label noEventos = new Label("No hay eventos disponibles con estos filtros");
            noEventos.setStyle("-fx-text-fill: #888; -fx-padding: 30;");
            eventosContainer.getChildren().add(noEventos);
            return;
        }

        for (Evento evento : eventos) {
            eventosContainer.getChildren().add(crearTarjetaEvento(evento));
        }
    }

    private VBox crearTarjetaEvento(Evento evento) {
        VBox tarjeta = new VBox(10);
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Categoría badge
        Label badge = new Label(evento.getCategoria().toString());
        String badgeColor;
        switch (evento.getCategoria()) {
            case CONCIERTO: badgeColor = "#e94560"; break;
            case TEATRO: badgeColor = "#667eea"; break;
            case CONFERENCIA: badgeColor = "#43e97b"; break;
            default: badgeColor = "#888";
        }
        badge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white; -fx-background-radius: 15px; -fx-padding: 5 12; -fx-font-size: 11px;");

        // Título
        Label nombre = new Label(evento.getNombre());
        nombre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Descripción
        Text descripcion = new Text(evento.getDescripcion());
        descripcion.setWrappingWidth(500);
        descripcion.setStyle("-fx-fill: #666;");

        // Detalles en grid
        GridPane detalles = new GridPane();
        detalles.setHgap(20);
        detalles.setVgap(8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Label lblFecha = new Label("📅 " + evento.getFechaHora().format(formatter));
        Label lblCiudad = new Label("📍 " + evento.getCiudad());
        Label lblRecinto = new Label("🏟️ " + evento.getRecinto().getNombre());
        Label lblPrecios = new Label("💰 Desde $" + String.format("%,.0f", obtenerPrecioMinimo(evento)));

        lblFecha.setStyle("-fx-text-fill: #555;");
        lblCiudad.setStyle("-fx-text-fill: #555;");
        lblRecinto.setStyle("-fx-text-fill: #555;");
        lblPrecios.setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold;");

        detalles.add(lblFecha, 0, 0);
        detalles.add(lblCiudad, 1, 0);
        detalles.add(lblRecinto, 0, 1);
        detalles.add(lblPrecios, 1, 1);

        // Botones
        HBox botonesBox = new HBox(10);
        botonesBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnVerZonas = new Button("Ver zonas y precios");
        btnVerZonas.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 15;");
        btnVerZonas.setOnAction(e -> mostrarZonasEvento(evento));

        Button btnComprar = new Button("Comprar entrada");
        btnComprar.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 15; -fx-font-weight: bold;");
        btnComprar.setOnAction(e -> iniciarCompra(evento));

        botonesBox.getChildren().addAll(btnVerZonas, btnComprar);

        tarjeta.getChildren().addAll(badge, nombre, descripcion, detalles, new Separator(), botonesBox);

        return tarjeta;
    }

    private double obtenerPrecioMinimo(Evento evento) {
        return evento.getRecinto().getZonas().stream()
                .mapToDouble(Zona::getPrecioBase)
                .min()
                .orElse(0);
    }

    private void mostrarZonasEvento(Evento evento) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Zonas - " + evento.getNombre());
        dialog.setHeaderText(null);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        for (Zona zona : evento.getRecinto().getZonas()) {
            VBox zonaBox = new VBox(5);
            zonaBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10px; -fx-padding: 10;");

            Label nombreZona = new Label(zona.getNombre());
            nombreZona.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label precio = new Label("Precio: $" + String.format("%,.0f", zona.getPrecioBase()));
            precio.setStyle("-fx-text-fill: #e94560;");

            Label asientos = new Label("Asientos disponibles: " + zona.getAsientosDisponibles() + " / " + zona.getCapacidad());
            asientos.setStyle("-fx-text-fill: #666;");

            zonaBox.getChildren().addAll(nombreZona, precio, asientos);
            content.getChildren().add(zonaBox);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void iniciarCompra(Evento evento) {
        // Verificar si es administrador (no puede comprar)
        if (usuarioActivo instanceof Administrador) {
            mostrarAlerta("Acción no permitida", "Los administradores no pueden comprar entradas.\nInicie sesión como usuario normal.");
            return;
        }

        // Crear diálogo para seleccionar zona
        Dialog<Zona> dialog = new Dialog<>();
        dialog.setTitle("Comprar entrada - " + evento.getNombre());
        dialog.setHeaderText("Seleccione una zona");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(350);

        ComboBox<Zona> cbZona = new ComboBox<>();
        cbZona.getItems().addAll(evento.getRecinto().getZonas());
        cbZona.setPromptText("Seleccione zona");

        // Mostrar información de la zona seleccionada
        Label lblInfo = new Label();
        lblInfo.setStyle("-fx-text-fill: #666;");

        cbZona.setOnAction(e -> {
            Zona zona = cbZona.getValue();
            if (zona != null) {
                lblInfo.setText("Precio: $" + String.format("%,.0f", zona.getPrecioBase()) +
                        "\nAsientos disponibles: " + zona.getAsientosDisponibles());
            }
        });

        Button btnConfirmar = new Button("Confirmar compra");
        btnConfirmar.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 10; -fx-font-weight: bold;");

        content.getChildren().addAll(new Label("Zona:"), cbZona, lblInfo, btnConfirmar);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        btnConfirmar.setOnAction(ev -> {
            Zona zona = cbZona.getValue();
            if (zona != null) {
                realizarCompra(evento, zona);
                dialog.close();
            } else {
                mostrarAlerta("Error", "Debe seleccionar una zona");
            }
        });

        dialog.showAndWait();
    }

    private void realizarCompra(Evento evento, Zona zona) {
        // Buscar asiento disponible
        Asiento asiento = zona.getAsientos().stream()
                .filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE)
                .findFirst()
                .orElse(null);

        if (asiento == null) {
            mostrarAlerta("Sin disponibilidad", "No hay asientos disponibles en esta zona");
            return;
        }

        // Crear compra
        String idCompra = "C" + System.currentTimeMillis();
        Compra compra = compraService.crearCompra(idCompra, (Usuario) usuarioActivo, evento);

        // Crear entrada
        Entrada entrada = new Entrada("ENT" + System.currentTimeMillis(), evento, zona, asiento, zona.getPrecioBase());
        compraService.agregarEntrada(compra, entrada);

        // Marcar asiento como reservado
        asiento.setEstado(EstadoAsiento.RESERVADO);

        mostrarAlerta("Éxito", "✅ Entrada reservada con éxito!\n\n" +
                "Evento: " + evento.getNombre() + "\n" +
                "Zona: " + zona.getNombre() + "\n" +
                "Asiento: " + asiento.getFila() + asiento.getNumero() + "\n" +
                "Total: $" + String.format("%,.0f", entrada.getPrecio()) + "\n\n" +
                "Complete el pago en la sección 'Mis Compras'");

        // Actualizar lista de eventos
        cargarEventos();
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