package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MisComprasView {
    private Stage stage;
    private VBox root;
    private CompraRepository compraRepository;
    private CompraService compraService;
    private Persona usuarioActivo;
    private VBox comprasContainer;

    public MisComprasView(Stage stage) {
        this.stage = stage;
        this.usuarioActivo = GestorSesion.getInstance().getUsuarioActivo();
        inicializarServicios();
        crearUI();
        cargarCompras();
    }

    private void inicializarServicios() {
        CompraRepository compraRepo = new CompraRepository();
        AsientoRepository asientoRepo = new AsientoRepository();

        this.compraRepository = compraRepo;
        this.compraService = new CompraService(compraRepo, asientoRepo);
    }

    private void crearUI() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // Título
        Label titulo = new Label("🎟️ Mis Compras");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Panel de resumen
        HBox resumenBox = crearPanelResumen();

        // Contenedor de compras
        comprasContainer = new VBox(15);
        comprasContainer.setPadding(new Insets(10));

        ScrollPane scrollCompras = new ScrollPane(comprasContainer);
        scrollCompras.setFitToWidth(true);
        scrollCompras.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollCompras.setPrefHeight(500);

        // Botón refrescar
        Button btnRefrescar = new Button("🔄 Refrescar");
        btnRefrescar.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 20;");
        btnRefrescar.setOnAction(e -> cargarCompras());

        HBox buttonBox = new HBox(btnRefrescar);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(titulo, resumenBox, buttonBox, scrollCompras);
    }

    private HBox crearPanelResumen() {
        HBox panel = new HBox(20);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15;");
        panel.setAlignment(Pos.CENTER);

        List<Compra> misCompras = compraRepository.findAll().stream()
                .filter(c -> c.getUsuario().getId().equals(usuarioActivo.getId()))
                .toList();

        long totalCompras = misCompras.size();
        long comprasPagadas = misCompras.stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .count();
        double totalGastado = misCompras.stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal)
                .sum();

        VBox box1 = crearTarjetaResumen("📦 Total Compras", String.valueOf(totalCompras), "#667eea");
        VBox box2 = crearTarjetaResumen("✅ Pagadas", String.valueOf(comprasPagadas), "#43e97b");
        VBox box3 = crearTarjetaResumen("💰 Total Gastado", "$" + String.format("%,.0f", totalGastado), "#e94560");

        panel.getChildren().addAll(box1, box2, box3);
        return panel;
    }

    private VBox crearTarjetaResumen(String titulo, String valor, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    private void cargarCompras() {
        comprasContainer.getChildren().clear();

        List<Compra> misCompras = compraRepository.findAll().stream()
                .filter(c -> c.getUsuario().getId().equals(usuarioActivo.getId()))
                .toList();

        if (misCompras.isEmpty()) {
            Label noCompras = new Label("No tienes compras realizadas");
            noCompras.setStyle("-fx-text-fill: #888; -fx-padding: 30;");
            comprasContainer.getChildren().add(noCompras);
            return;
        }

        for (Compra compra : misCompras) {
            comprasContainer.getChildren().add(crearTarjetaCompra(compra));
        }
    }

    private VBox crearTarjetaCompra(Compra compra) {
        VBox tarjeta = new VBox(10);

        String borderColor;
        switch (compra.getEstado()) {
            case PAGADA: borderColor = "#43e97b"; break;
            case CANCELADA: borderColor = "#f5576c"; break;
            case CREADA: borderColor = "#ffa502"; break;
            default: borderColor = "#667eea";
        }

        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15; -fx-border-color: " + borderColor + "; -fx-border-width: 2px; -fx-border-radius: 15px;");

        // Cabecera
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblId = new Label("🔖 " + compra.getIdCompra());
        lblId.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblEstado = new Label(compra.getEstado().toString());
        String estadoStyle;
        switch (compra.getEstado()) {
            case PAGADA: estadoStyle = "-fx-background-color: #43e97b; -fx-text-fill: white; -fx-background-radius: 15px; -fx-padding: 4 12; -fx-font-size: 11px;"; break;
            case CANCELADA: estadoStyle = "-fx-background-color: #f5576c; -fx-text-fill: white; -fx-background-radius: 15px; -fx-padding: 4 12; -fx-font-size: 11px;"; break;
            default: estadoStyle = "-fx-background-color: #ffa502; -fx-text-fill: white; -fx-background-radius: 15px; -fx-padding: 4 12; -fx-font-size: 11px;";
        }
        lblEstado.setStyle(estadoStyle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblFecha = new Label(compra.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblFecha.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

        header.getChildren().addAll(lblId, lblEstado, spacer, lblFecha);

        // Evento
        Label lblEvento = new Label("🎪 " + compra.getEvento().getNombre());
        lblEvento.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Items comprados
        VBox itemsBox = new VBox(5);
        Label lblItems = new Label("Items comprados:");
        lblItems.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        itemsBox.getChildren().add(lblItems);

        for (ItemCompra item : compra.getItems()) {
            Text itemText = new Text("• " + item.getDescripcion() + " - $" + String.format("%,.0f", item.getPrecio()));
            itemText.setStyle("-fx-fill: #666;");
            itemsBox.getChildren().add(itemText);
        }

        // Total y botones
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Label lblTotal = new Label("Total: $" + String.format("%,.0f", compra.getTotal()));
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e94560;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        if (compra.getEstado() == EstadoCompra.CREADA) {
            Button btnPagar = new Button("💳 Pagar");
            btnPagar.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
            btnPagar.setOnAction(e -> procesarPago(compra));

            Button btnCancelar = new Button("❌ Cancelar");
            btnCancelar.setStyle("-fx-background-color: #f5576c; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
            btnCancelar.setOnAction(e -> cancelarCompra(compra));

            footer.getChildren().addAll(btnPagar, btnCancelar);
        } else if (compra.getEstado() == EstadoCompra.PAGADA) {
            Button btnVerEntrada = new Button("🎫 Ver entrada");
            btnVerEntrada.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
            btnVerEntrada.setOnAction(e -> verEntradas(compra));

            footer.getChildren().add(btnVerEntrada);
        }

        footer.getChildren().add(0, lblTotal);

        tarjeta.getChildren().addAll(header, lblEvento, itemsBox, new Separator(), footer);

        return tarjeta;
    }

    private void procesarPago(Compra compra) {
        Pago pago = new Pago("PAG" + System.currentTimeMillis(), "Efectivo", compra.getTotal(), java.time.LocalDateTime.now());
        pago.setEstado(EstadoPago.APROBADO);
        compra.setPago(pago);
        compra.setEstado(EstadoCompra.PAGADA);

        for (ItemCompra item : compra.getItems()) {
            if (item instanceof Entrada) {
                Entrada entrada = (Entrada) item;
                if (entrada.getAsiento() != null) {
                    entrada.getAsiento().setEstado(EstadoAsiento.VENDIDO);
                }
            }
        }

        compraRepository.save(compra);
        mostrarAlerta("Éxito", "Pago procesado correctamente");
        cargarCompras();
    }

    private void cancelarCompra(Compra compra) {
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Cancelar compra");
        confirmar.setHeaderText(null);
        confirmar.setContentText("¿Estás seguro de cancelar esta compra?");

        if (confirmar.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            compraService.cancelarCompra(compra);
            mostrarAlerta("Cancelada", "La compra ha sido cancelada");
            cargarCompras();
        }
    }

    private void verEntradas(Compra compra) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Entradas - " + compra.getEvento().getNombre());
        dialog.setHeaderText(null);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        for (ItemCompra item : compra.getItems()) {
            if (item instanceof Entrada) {
                Entrada entrada = (Entrada) item;
                VBox entradaBox = new VBox(5);
                entradaBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10px; -fx-padding: 10;");

                Label lblEvento = new Label(entrada.getEvento().getNombre());
                lblEvento.setStyle("-fx-font-weight: bold;");

                Label lblZona = new Label("Zona: " + entrada.getZona().getNombre());
                Label lblAsiento = entrada.getAsiento() != null ?
                        new Label("Asiento: " + entrada.getAsiento().getFila() + entrada.getAsiento().getNumero()) :
                        new Label("Entrada general");
                Label lblPrecio = new Label("Precio: $" + String.format("%,.0f", entrada.getPrecio()));

                entradaBox.getChildren().addAll(lblEvento, lblZona, lblAsiento, lblPrecio);
                content.getChildren().add(entradaBox);
            }
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
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