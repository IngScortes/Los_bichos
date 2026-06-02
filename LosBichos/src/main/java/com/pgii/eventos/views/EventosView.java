package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.patterns.structural.decorator.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventosView {
    private Stage stage;
    private VBox root;
    private EventoRepository eventoRepo;
    private CompraRepository compraRepo;
    private AsientoRepository asientoRepo;
    private CompraService compraService;
    private Persona usuario;
    private VBox container;

    private ComboBox<String> cbCiudad;
    private ComboBox<CategoriaEvento> cbCategoria;
    private TextField txtBuscarNombre;
    private ComboBox<String> cbOrdenar;
    private Slider sliderPrecio;
    private Label lblRangoPrecio;
    private DatePicker dpFechaEspecifica;

    public EventosView(Stage stage) {
        this.stage = stage;
        this.usuario = GestorSesion.getInstance().getUsuarioActivo();
        this.eventoRepo = EventoRepository.getInstance();
        this.compraRepo = CompraRepository.getInstance();
        this.asientoRepo = AsientoRepository.getInstance();
        this.compraService = new CompraService(compraRepo, asientoRepo);
        crearUI();
        cargarEventos();
    }

    private void crearUI() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setFillWidth(true);
        root.setStyle("-fx-background-color: #f1f5f9;");

        Label titulo = new Label("🎪 Eventos Disponibles");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        VBox filtrosBox = new VBox(10);
        filtrosBox.setStyle("-fx-background-color: white; -fx-background-radius: 20px; -fx-padding: 15px;");

        Label lblFiltros = new Label("🔍 Filtros");
        lblFiltros.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");

        HBox fila1 = new HBox(15);
        fila1.setAlignment(Pos.CENTER_LEFT);
        Label lblBuscar = new Label("🔎");
        txtBuscarNombre = new TextField();
        txtBuscarNombre.setPromptText("Buscar evento...");
        txtBuscarNombre.setPrefWidth(200);
        txtBuscarNombre.textProperty().addListener((obs, oldVal, newVal) -> cargarEventos());
        Label lblCiudad = new Label("📍");
        cbCiudad = new ComboBox<>();
        cbCiudad.getItems().addAll("Todas", "Armenia", "Pereira", "Manizales", "Cali", "Medellín", "Bogotá");
        cbCiudad.setValue("Todas");
        cbCiudad.setOnAction(e -> cargarEventos());
        cbCiudad.setPrefWidth(120);
        fila1.getChildren().addAll(lblBuscar, txtBuscarNombre, lblCiudad, cbCiudad);

        HBox fila2 = new HBox(15);
        fila2.setAlignment(Pos.CENTER_LEFT);
        Label lblCategoria = new Label("📂");
        cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll(CategoriaEvento.values());
        cbCategoria.setPromptText("Categoría");
        cbCategoria.setOnAction(e -> cargarEventos());
        cbCategoria.setPrefWidth(130);
        Label lblFecha = new Label("📅");
        dpFechaEspecifica = new DatePicker();
        dpFechaEspecifica.setPromptText("Fecha");
        dpFechaEspecifica.setPrefWidth(120);
        dpFechaEspecifica.setOnAction(e -> cargarEventos());
        fila2.getChildren().addAll(lblCategoria, cbCategoria, lblFecha, dpFechaEspecifica);

        HBox fila3 = new HBox(15);
        fila3.setAlignment(Pos.CENTER_LEFT);
        Label lblOrdenar = new Label("📊");
        cbOrdenar = new ComboBox<>();
        cbOrdenar.getItems().addAll("Fecha (más cercano)", "Fecha (más lejano)", "Precio (menor)", "Precio (mayor)");
        cbOrdenar.setValue("Fecha (más cercano)");
        cbOrdenar.setOnAction(e -> cargarEventos());
        cbOrdenar.setPrefWidth(160);
        Label lblPrecio = new Label("💰");
        sliderPrecio = new Slider(0, 500000, 500000);
        sliderPrecio.setPrefWidth(180);
        lblRangoPrecio = new Label("Hasta $500k");
        lblRangoPrecio.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        sliderPrecio.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblRangoPrecio.setText("Hasta $" + String.format("%,.0f", newVal.doubleValue()) + "k");
            cargarEventos();
        });
        Button btnLimpiar = new Button("🗑️ Limpiar");
        btnLimpiar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 5 15; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: 600;");
        btnLimpiar.setOnAction(e -> {
            txtBuscarNombre.clear();
            cbCiudad.setValue("Todas");
            cbCategoria.setValue(null);
            dpFechaEspecifica.setValue(null);
            cbOrdenar.setValue("Fecha (más cercano)");
            sliderPrecio.setValue(500000);
            cargarEventos();
        });
        HBox precioBox = new HBox(5);
        precioBox.setAlignment(Pos.CENTER_LEFT);
        precioBox.getChildren().addAll(lblPrecio, sliderPrecio, lblRangoPrecio);
        fila3.getChildren().addAll(lblOrdenar, cbOrdenar, precioBox, btnLimpiar);

        filtrosBox.getChildren().addAll(lblFiltros, fila1, fila2, fila3);

        container = new VBox(15);
        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setPrefHeight(600);

        root.getChildren().addAll(titulo, filtrosBox, scroll);
    }

    private void cargarEventos() {
        container.getChildren().clear();

        List<Evento> eventos = eventoRepo.findAll().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO)
                .collect(Collectors.toList());

        if (txtBuscarNombre != null && !txtBuscarNombre.getText().isEmpty()) {
            String busqueda = txtBuscarNombre.getText().toLowerCase();
            eventos = eventos.stream().filter(e -> e.getNombre().toLowerCase().contains(busqueda)).collect(Collectors.toList());
        }
        if (cbCiudad.getValue() != null && !cbCiudad.getValue().equals("Todas")) {
            eventos = eventos.stream().filter(e -> e.getCiudad().equalsIgnoreCase(cbCiudad.getValue())).collect(Collectors.toList());
        }
        if (cbCategoria.getValue() != null) {
            eventos = eventos.stream().filter(e -> e.getCategoria() == cbCategoria.getValue()).collect(Collectors.toList());
        }
        if (dpFechaEspecifica.getValue() != null) {
            LocalDate fecha = dpFechaEspecifica.getValue();
            eventos = eventos.stream().filter(e -> e.getFechaHora().toLocalDate().equals(fecha)).collect(Collectors.toList());
        }

        double precioMax = sliderPrecio.getValue();
        eventos = eventos.stream().filter(e -> e.getPrecioMinimo() <= precioMax).collect(Collectors.toList());

        if (cbOrdenar != null) {
            String orden = cbOrdenar.getValue();
            if (orden.equals("Fecha (más cercano)")) eventos.sort(Comparator.comparing(Evento::getFechaHora));
            else if (orden.equals("Fecha (más lejano)")) eventos.sort((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()));
            else if (orden.equals("Precio (menor)")) eventos.sort(Comparator.comparingDouble(Evento::getPrecioMinimo));
            else if (orden.equals("Precio (mayor)")) eventos.sort((a, b) -> Double.compare(b.getPrecioMinimo(), a.getPrecioMinimo()));
        }

        if (eventos.isEmpty()) {
            Label noEventos = new Label("📭 No hay eventos disponibles con estos filtros");
            noEventos.setStyle("-fx-text-fill: #64748b; -fx-padding: 40px; -fx-font-size: 14px;");
            container.getChildren().add(noEventos);
            return;
        }

        for (Evento e : eventos) {
            container.getChildren().add(crearTarjetaEvento(e));
        }
    }

    private VBox crearTarjetaEvento(Evento evento) {
        VBox tarjeta = new VBox(12);
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 24px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 4);");

        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 24px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 25, 0, 0, 8);"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 24px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 4);"));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label(evento.getCategoria().toString());
        String badgeColor;
        switch (evento.getCategoria()) {
            case CONCIERTO: badgeColor = "#ef4444"; break;
            case TEATRO: badgeColor = "#6366f1"; break;
            case CONFERENCIA: badgeColor = "#10b981"; break;
            default: badgeColor = "#64748b";
        }
        badge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 4 15px; -fx-font-size: 12px; -fx-font-weight: 600;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label fechaLabel = new Label(evento.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        fechaLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        header.getChildren().addAll(badge, spacer, fechaLabel);

        Label nombre = new Label(evento.getNombre());
        nombre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Text descripcion = new Text(evento.getDescripcion());
        descripcion.setWrappingWidth(500);
        descripcion.setStyle("-fx-fill: #64748b; -fx-font-size: 13px;");

        GridPane detalles = new GridPane();
        detalles.setHgap(20);
        detalles.setVgap(8);
        detalles.setStyle("-fx-padding: 10 0;");
        detalles.add(new Label("🕐 " + evento.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm"))), 0, 0);
        detalles.add(new Label("📍 " + evento.getCiudad()), 1, 0);
        detalles.add(new Label("🏟️ " + evento.getRecinto().getNombre()), 0, 1);

        Label lblPrecioMin = new Label("💰 Desde $" + String.format("%,.0f", evento.getPrecioMinimo()));
        lblPrecioMin.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        detalles.add(lblPrecioMin, 1, 1);

        VBox zonasBox = new VBox(8);
        Label lblZonas = new Label("🎪 Zonas disponibles:");
        lblZonas.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        zonasBox.getChildren().add(lblZonas);

        GridPane tablaZonas = new GridPane();
        tablaZonas.setHgap(15);
        tablaZonas.setVgap(8);
        tablaZonas.add(new Label("Zona"), 0, 0);
        tablaZonas.add(new Label("Precio"), 1, 0);
        tablaZonas.add(new Label("Disponibles"), 2, 0);
        for (javafx.scene.Node node : tablaZonas.getChildren()) {
            if (node instanceof Label) ((Label) node).setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        }

        int row = 1;
        for (Zona zona : evento.getZonas()) {
            tablaZonas.add(new Label(zona.getNombre()), 0, row);
            Label precioZona = new Label("$" + String.format("%,.0f", zona.getPrecioBase()));
            precioZona.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 600;");
            tablaZonas.add(precioZona, 1, row);
            long disponibles = zona.getAsientos().stream().filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE).count();
            tablaZonas.add(new Label(disponibles + " / " + zona.getCapacidad()), 2, row);
            row++;
        }
        zonasBox.getChildren().add(tablaZonas);

        int totalAsientos = evento.getZonas().stream().mapToInt(Zona::getCapacidad).sum();
        int disponiblesTotal = evento.getZonas().stream()
                .flatMap(z -> z.getAsientos().stream())
                .mapToInt(a -> a.getEstado() == EstadoAsiento.DISPONIBLE ? 1 : 0).sum();
        int ocupados = totalAsientos - disponiblesTotal;
        double porcentajeOcupacion = totalAsientos > 0 ? (ocupados * 100.0 / totalAsientos) : 0;

        VBox resumenAsientos = new VBox(5);
        Label lblResumen = new Label("📊 Resumen:");
        lblResumen.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        ProgressBar barraOcupacion = new ProgressBar(porcentajeOcupacion / 100);
        barraOcupacion.setPrefWidth(200);
        barraOcupacion.setStyle("-fx-accent: #ef4444;");
        Label lblDisponibles = new Label("🪑 Asientos disponibles: " + disponiblesTotal + " / " + totalAsientos);
        lblDisponibles.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        resumenAsientos.getChildren().addAll(lblResumen, barraOcupacion, lblDisponibles);

        Button btnComprar = new Button("💰 Comprar entrada");
        btnComprar.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #34d399); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 30px; -fx-padding: 10 25px; -fx-cursor: hand; -fx-font-size: 13px;");
        btnComprar.setMaxWidth(250);
        btnComprar.setOnAction(e -> mostrarSelectorZonas(evento));

        HBox buttonBox = new HBox(btnComprar);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        tarjeta.getChildren().addAll(header, nombre, descripcion, new Separator(), detalles, zonasBox, resumenAsientos, buttonBox);
        return tarjeta;
    }

    private void mostrarSelectorZonas(Evento evento) {
        if (usuario instanceof Administrador) {
            registrarIncidencia(
                    "Intento de compra por administrador",
                    "El administrador " + usuario.getEmail() + " intentó comprar entradas para el evento " + evento.getNombre(),
                    Incidencia.Tipo.SEGURIDAD,
                    Incidencia.Prioridad.ALTA,
                    evento
            );
            mostrarAlerta("Acción no permitida", "Los administradores no pueden comprar entradas.");
            return;
        }

        List<Zona> zonas = evento.getZonas();
        if (zonas.isEmpty()) {
            mostrarAlerta("Error", "Este evento no tiene zonas disponibles.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("🎟️ Comprar entrada - " + evento.getNombre());
        dialog.setHeaderText("Seleccione zona y servicios adicionales");
        dialog.setResizable(true);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);

        VBox infoEvento = new VBox(5);
        infoEvento.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 15px; -fx-padding: 15px;");
        infoEvento.getChildren().addAll(
                new Label("📌 " + evento.getNombre()),
                new Label("📅 " + evento.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                new Label("📍 " + evento.getCiudad() + " - " + evento.getRecinto().getNombre())
        );
        infoEvento.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        ((Label)infoEvento.getChildren().get(1)).setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        ((Label)infoEvento.getChildren().get(2)).setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        VBox selectorBox = new VBox(8);
        Label lblSeleccion = new Label("🎪 Seleccione una zona:");
        lblSeleccion.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");

        ComboBox<Zona> cbZona = new ComboBox<>();
        cbZona.getItems().addAll(zonas);
        cbZona.setPromptText("Elija una zona");
        cbZona.setCellFactory(lv -> new ListCell<Zona>() {
            @Override
            protected void updateItem(Zona item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    long disponibles = item.getAsientos().stream().filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE).count();
                    setText(item.getNombre() + " - $" + String.format("%,.0f", item.getPrecioBase()) + " (" + disponibles + " disponibles)");
                }
            }
        });
        cbZona.setButtonCell(new ListCell<Zona>() {
            @Override
            protected void updateItem(Zona item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("Elija una zona");
                else setText(item.getNombre() + " - $" + String.format("%,.0f", item.getPrecioBase()));
            }
        });
        selectorBox.getChildren().addAll(lblSeleccion, cbZona);

        VBox serviciosBox = new VBox(8);
        Label lblServicios = new Label("✨ Servicios adicionales:");
        lblServicios.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");

        CheckBox chkVIP = new CheckBox("⭐ Acceso VIP - $50,000");
        CheckBox chkSeguro = new CheckBox("🛡️ Seguro de cancelación - $15,000");
        CheckBox chkMerchandising = new CheckBox("🎁 Kit de merchandising - $25,000");
        CheckBox chkParqueadero = new CheckBox("🅿️ Parqueadero preferencial - $10,000");

        chkVIP.setStyle("-fx-cursor: hand;");
        chkSeguro.setStyle("-fx-cursor: hand;");
        chkMerchandising.setStyle("-fx-cursor: hand;");
        chkParqueadero.setStyle("-fx-cursor: hand;");

        serviciosBox.getChildren().addAll(lblServicios, chkVIP, chkSeguro, chkMerchandising, chkParqueadero);

        VBox resumenBox = new VBox(8);
        resumenBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 10px; -fx-padding: 12px;");
        Label lblResumen = new Label("💰 Resumen:");
        lblResumen.setStyle("-fx-font-weight: bold;");
        Label lblPrecioBase = new Label("Precio base: $0");
        Label lblServiciosTotal = new Label("Servicios: $0");
        Label lblTotal = new Label("Total: $0");
        lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981; -fx-font-size: 16px;");

        resumenBox.getChildren().addAll(lblResumen, lblPrecioBase, lblServiciosTotal, lblTotal);

        Runnable actualizarResumen = () -> {
            Zona zona = cbZona.getValue();
            if (zona != null) {
                double precioBase = zona.getPrecioBase();
                double adicionales = 0;
                if (chkVIP.isSelected()) adicionales += 50000;
                if (chkSeguro.isSelected()) adicionales += 15000;
                if (chkMerchandising.isSelected()) adicionales += 25000;
                if (chkParqueadero.isSelected()) adicionales += 10000;

                lblPrecioBase.setText("Precio base: $" + String.format("%,.0f", precioBase));
                lblServiciosTotal.setText("Servicios: $" + String.format("%,.0f", adicionales));
                lblTotal.setText("Total: $" + String.format("%,.0f", precioBase + adicionales));
            }
        };

        cbZona.setOnAction(e -> actualizarResumen.run());
        chkVIP.setOnAction(e -> actualizarResumen.run());
        chkSeguro.setOnAction(e -> actualizarResumen.run());
        chkMerchandising.setOnAction(e -> actualizarResumen.run());
        chkParqueadero.setOnAction(e -> actualizarResumen.run());

        Button btnConfirmar = new Button("✅ Confirmar compra");
        btnConfirmar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 30px; -fx-padding: 12 20; -fx-cursor: hand;");
        btnConfirmar.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().addAll(infoEvento, selectorBox, serviciosBox, resumenBox, btnConfirmar);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        btnConfirmar.setOnAction(ev -> {
            Zona zona = cbZona.getValue();
            if (zona != null) {
                Asiento asiento = zona.getAsientos().stream()
                        .filter(a -> a.getEstado() == EstadoAsiento.DISPONIBLE)
                        .findFirst().orElse(null);

                if (asiento == null) {
                    registrarIncidencia(
                            "Asiento no disponible",
                            "Usuario " + usuario.getEmail() + " intentó comprar en zona " + zona.getNombre() +
                                    " del evento " + evento.getNombre() + " pero no hay asientos disponibles.",
                            Incidencia.Tipo.LOGISTICA,
                            Incidencia.Prioridad.MEDIA,
                            evento
                    );
                    mostrarAlerta("Sin disponibilidad", "No hay asientos disponibles en esta zona.");
                    return;
                }

                ItemCompra entradaBase = new Entrada("ENT" + System.currentTimeMillis(), evento, zona, asiento, zona.getPrecioBase());
                ItemCompra entradaFinal = entradaBase;

                if (chkVIP.isSelected()) entradaFinal = new VIPDecorator(entradaFinal);
                if (chkSeguro.isSelected()) entradaFinal = new SeguroDecorator(entradaFinal);
                if (chkMerchandising.isSelected()) entradaFinal = new MerchandisingDecorator(entradaFinal);
                if (chkParqueadero.isSelected()) entradaFinal = new ParqueaderoDecorator(entradaFinal);

                realizarCompraConServicios(evento, zona, asiento, entradaFinal);
                dialog.close();
            } else {
                mostrarAlerta("Error", "Debe seleccionar una zona");
            }
        });

        dialog.showAndWait();
    }

    private void realizarCompraConServicios(Evento evento, Zona zona, Asiento asiento, ItemCompra entradaFinal) {
        // Verificar si el asiento sigue disponible (por si acaso)
        if (asiento.getEstado() != EstadoAsiento.DISPONIBLE) {
            registrarIncidencia(
                    "Asiento ya ocupado",
                    "Usuario " + usuario.getEmail() + " intentó comprar asiento " + asiento.getIdAsiento() +
                            " del evento " + evento.getNombre() + " pero ya estaba ocupado.",
                    Incidencia.Tipo.LOGISTICA,
                    Incidencia.Prioridad.MEDIA,
                    evento
            );
            mostrarAlerta("Sin disponibilidad", "El asiento ya no está disponible.");
            cargarEventos();
            return;
        }

        String idCompra = "C" + System.currentTimeMillis();
        Compra compra = new Compra(idCompra, (Usuario) usuario, evento);
        compra.agregarItem(entradaFinal);
        compraRepo.save(compra);

        asiento.setEstado(EstadoAsiento.RESERVADO);
        asientoRepo.save(asiento);

        String serviciosAdicionales = "";
        if (entradaFinal instanceof VIPDecorator) serviciosAdicionales += "\n✓ Acceso VIP";
        if (entradaFinal instanceof SeguroDecorator) serviciosAdicionales += "\n✓ Seguro de cancelación";
        if (entradaFinal instanceof MerchandisingDecorator) serviciosAdicionales += "\n✓ Kit de merchandising";
        if (entradaFinal instanceof ParqueaderoDecorator) serviciosAdicionales += "\n✓ Parqueadero preferencial";

        mostrarAlerta("✅ Compra exitosa",
                "🎉 ¡Entrada reservada con servicios adicionales!\n\n" +
                        "📌 " + evento.getNombre() + "\n" +
                        "🎪 Zona: " + zona.getNombre() + "\n" +
                        "🪑 Asiento: " + asiento.getFila() + asiento.getNumero() + "\n" +
                        "💰 Total: $" + String.format("%,.0f", entradaFinal.getPrecio()) + "\n" +
                        "✨ Servicios:" + (serviciosAdicionales.isEmpty() ? " Ninguno" : serviciosAdicionales) + "\n\n" +
                        "💡 Complete el pago en 'Mis Compras'");

        cargarEventos();
    }

    private void registrarIncidencia(String titulo, String descripcion,
                                     Incidencia.Tipo tipo, Incidencia.Prioridad prioridad,
                                     Evento evento) {
        Incidencia incidencia = new Incidencia(
                "INC_" + System.currentTimeMillis(),
                titulo,
                descripcion,
                tipo,
                prioridad,
                evento,
                GestorSesion.getInstance().getUsuarioActivo().getEmail()
        );
        IncidenciaRepository.getInstance().save(incidencia);
        System.out.println("⚠️ Incidencia registrada: " + titulo);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public VBox getRoot() { return root; }
}