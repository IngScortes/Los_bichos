package com.pgii.eventos.views;

import com.pgii.eventos.model.*;
import com.pgii.eventos.repository.*;
import com.pgii.eventos.service.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MisComprasView {
    private Stage stage;
    private VBox root;
    private CompraRepository compraRepo;
    private AsientoRepository asientoRepo;
    private Persona usuario;
    private VBox container;

    public MisComprasView(Stage stage) {
        this.stage = stage;
        this.usuario = GestorSesion.getInstance().getUsuarioActivo();
        this.compraRepo = CompraRepository.getInstance();
        this.asientoRepo = AsientoRepository.getInstance();
        crearUI();
        cargarCompras();
    }

    private void crearUI() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        Label titulo = new Label("🎟️ Mis Compras");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox resumenBox = crearPanelResumen();

        container = new VBox(15);
        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setPrefHeight(500);

        Button btnRefrescar = new Button("🔄 Refrescar");
        btnRefrescar.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 20;");
        btnRefrescar.setOnAction(e -> cargarCompras());

        HBox buttonBox = new HBox(btnRefrescar);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(titulo, resumenBox, buttonBox, scroll);
    }

    private HBox crearPanelResumen() {
        List<Compra> misCompras = compraRepo.findAll().stream()
                .filter(c -> c.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());

        long totalCompras = misCompras.size();
        double totalGastado = misCompras.stream()
                .filter(c -> c.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal).sum();

        HBox panel = new HBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15px;");

        VBox box1 = crearCardResumen("📦 Total Compras", String.valueOf(totalCompras), "#667eea");
        VBox box2 = crearCardResumen("💰 Total Gastado", "$" + String.format("%,.0f", totalGastado), "#e94560");

        panel.getChildren().addAll(box1, box2);
        return panel;
    }

    private VBox crearCardResumen(String titulo, String valor, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    private void cargarCompras() {
        container.getChildren().clear();

        List<Compra> misCompras = compraRepo.findAll().stream()
                .filter(c -> c.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());

        if (misCompras.isEmpty()) {
            Label noCompras = new Label("No tienes compras realizadas");
            noCompras.setStyle("-fx-text-fill: #888; -fx-padding: 40px;");
            container.getChildren().add(noCompras);
            return;
        }

        for (Compra c : misCompras) {
            container.getChildren().add(crearTarjetaCompra(c));
        }

        HBox nuevoResumen = crearPanelResumen();
        root.getChildren().set(1, nuevoResumen);
    }

    private VBox crearTarjetaCompra(Compra compra) {
        VBox tarjeta = new VBox(10);

        String borderColor;
        String estadoTexto;
        switch (compra.getEstado()) {
            case PAGADA: borderColor = "#43e97b"; estadoTexto = "PAGADA"; break;
            case CANCELADA: borderColor = "#f5576c"; estadoTexto = "CANCELADA"; break;
            default: borderColor = "#ffa502"; estadoTexto = "PENDIENTE DE PAGO";
        }
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-padding: 15px; -fx-border-color: " + borderColor + "; -fx-border-width: 2px;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblId = new Label("🔖 " + compra.getIdCompra());
        lblId.setStyle("-fx-font-weight: bold;");
        Label lblEstado = new Label(estadoTexto);
        lblEstado.setStyle("-fx-background-color: " + borderColor + "; -fx-text-fill: white; -fx-background-radius: 15px; -fx-padding: 4 12px; -fx-font-size: 11px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label lblFecha = new Label(compra.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblFecha.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
        header.getChildren().addAll(lblId, lblEstado, spacer, lblFecha);

        Label lblEvento = new Label("🎪 " + compra.getEvento().getNombre());
        lblEvento.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox itemsBox = new VBox(5);
        Label lblItems = new Label("Items comprados:");
        lblItems.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        itemsBox.getChildren().add(lblItems);
        for (ItemCompra item : compra.getItems()) {
            if (item instanceof Entrada) {
                Entrada e = (Entrada) item;
                itemsBox.getChildren().add(new Label("• " + e.getDescripcion() + " - $" + String.format("%,.0f", e.getPrecio())));
            }
        }

        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);
        Label lblTotal = new Label("Total: $" + String.format("%,.0f", compra.getTotal()));
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e94560;");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        if (compra.getEstado() == EstadoCompra.CREADA) {
            Button btnPagar = new Button("💳 Pagar ahora");
            btnPagar.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 8 20; -fx-cursor: hand;");
            btnPagar.setOnAction(e -> mostrarDialogoPago(compra));
            Button btnCancelar = new Button("❌ Cancelar compra");
            btnCancelar.setStyle("-fx-background-color: #f5576c; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 15; -fx-cursor: hand;");
            btnCancelar.setOnAction(e -> cancelarCompra(compra));
            footer.getChildren().addAll(btnCancelar, btnPagar);
        } else if (compra.getEstado() == EstadoCompra.PAGADA) {
            Button btnVerEntrada = new Button("🎫 Ver entrada");
            btnVerEntrada.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 20; -fx-cursor: hand;");
            btnVerEntrada.setOnAction(e -> verEntradas(compra));
            footer.getChildren().add(btnVerEntrada);

            // ========== FUNCIONALIDAD 3: Botón para generar PDF ==========
            Button btnPDF = new Button("📄 Descargar PDF");
            btnPDF.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 15; -fx-cursor: hand;");
            btnPDF.setOnAction(e -> generarPDFEntrada(compra));
            footer.getChildren().add(btnPDF);
        }

        footer.getChildren().add(0, lblTotal);
        tarjeta.getChildren().addAll(header, lblEvento, itemsBox, new Separator(), footer);
        return tarjeta;
    }

    private void mostrarDialogoPago(Compra compra) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("💳 Procesar pago");
        dialog.setHeaderText("Seleccione un método de pago");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(350);

        VBox infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 10px; -fx-padding: 12px;");
        infoBox.getChildren().addAll(
                new Label("📌 " + compra.getEvento().getNombre()),
                new Label("💰 Total a pagar: $" + String.format("%,.0f", compra.getTotal()))
        );
        infoBox.getChildren().get(0).setStyle("-fx-font-weight: bold;");
        ((Label)infoBox.getChildren().get(1)).setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold; -fx-font-size: 14px;");

        VBox metodosBox = new VBox(8);
        metodosBox.getChildren().add(new Label("Método de pago:"));
        ((Label)metodosBox.getChildren().get(0)).setStyle("-fx-font-weight: bold;");
        ToggleGroup grupoPago = new ToggleGroup();
        RadioButton rbTarjeta = new RadioButton("💳 Tarjeta de crédito/débito");
        rbTarjeta.setToggleGroup(grupoPago);
        rbTarjeta.setSelected(true);
        RadioButton rbPSE = new RadioButton("🏦 PSE (Transferencia bancaria)");
        rbPSE.setToggleGroup(grupoPago);
        RadioButton rbEfectivo = new RadioButton("💰 Efectivo (Pago en taquilla)");
        rbEfectivo.setToggleGroup(grupoPago);
        metodosBox.getChildren().addAll(rbTarjeta, rbPSE, rbEfectivo);

        Button btnConfirmar = new Button("✅ Confirmar pago");
        btnConfirmar.setStyle("-fx-background-color: #43e97b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25px; -fx-padding: 10 20; -fx-cursor: hand;");
        btnConfirmar.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().addAll(infoBox, metodosBox, btnConfirmar);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        btnConfirmar.setOnAction(ev -> {
            String metodo = rbTarjeta.isSelected() ? "Tarjeta de crédito" : (rbPSE.isSelected() ? "PSE" : "Efectivo");
            procesarPago(compra, metodo);
            dialog.close();
        });

        dialog.showAndWait();
    }

    private void procesarPago(Compra compra, String metodo) {
        Pago pago = new Pago("PAG" + System.currentTimeMillis(), metodo, compra.getTotal(), LocalDateTime.now());
        pago.setEstado(EstadoPago.APROBADO);
        compra.setPago(pago);
        compra.setEstado(EstadoCompra.PAGADA);

        for (ItemCompra item : compra.getItems()) {
            if (item instanceof Entrada) {
                Entrada entrada = (Entrada) item;
                if (entrada.getAsiento() != null) {
                    entrada.getAsiento().setEstado(EstadoAsiento.VENDIDO);
                    asientoRepo.save(entrada.getAsiento());
                }
            }
        }
        compraRepo.save(compra);
        mostrarAlerta("✅ Pago exitoso", "¡Pago procesado correctamente!\n\nMétodo: " + metodo + "\nTotal: $" + String.format("%,.0f", compra.getTotal()));
        cargarCompras();
    }

    private void cancelarCompra(Compra compra) {
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Cancelar compra");
        confirmar.setContentText("¿Estás seguro de cancelar esta compra?");
        if (confirmar.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            compra.setEstado(EstadoCompra.CANCELADA);
            for (ItemCompra item : compra.getItems()) {
                if (item instanceof Entrada) {
                    Entrada entrada = (Entrada) item;
                    if (entrada.getAsiento() != null) {
                        entrada.getAsiento().setEstado(EstadoAsiento.DISPONIBLE);
                        asientoRepo.save(entrada.getAsiento());
                    }
                }
            }
            compraRepo.save(compra);
            mostrarAlerta("Compra cancelada", "La compra ha sido cancelada exitosamente.");
            cargarCompras();
        }
    }

    private void verEntradas(Compra compra) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("🎫 Mis Entradas - " + compra.getEvento().getNombre());
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(450);

        for (ItemCompra item : compra.getItems()) {
            if (item instanceof Entrada) {
                Entrada e = (Entrada) item;
                VBox entradaBox = new VBox(5);
                entradaBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10px; -fx-padding: 15px; -fx-border-color: #e0e0e0; -fx-border-radius: 10px;");
                entradaBox.getChildren().addAll(
                        new Label("🎪 " + e.getEvento().getNombre()),
                        new Label("📅 " + e.getEvento().getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                        new Label("🎪 Zona: " + e.getZona().getNombre()),
                        e.getAsiento() != null ? new Label("🪑 Asiento: " + e.getAsiento().getFila() + e.getAsiento().getNumero()) : new Label("🎟️ Entrada general"),
                        new Label("💰 Precio: $" + String.format("%,.0f", e.getPrecio()))
                );
                entradaBox.getChildren().get(0).setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                content.getChildren().add(entradaBox);
            }
        }
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // ========== FUNCIONALIDAD 3: Generar PDF de entrada ==========
    private void generarPDFEntrada(Compra compra) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);
            cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
            cs.beginText();
            cs.newLineAtOffset(50, 750);
            cs.showText("🎫 ENTRADA DE EVENTO");
            cs.endText();

            cs.setFont(PDType1Font.HELVETICA, 12);
            int y = 700;

            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Evento: " + compra.getEvento().getNombre());
            cs.endText();
            y -= 30;

            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Fecha: " + compra.getEvento().getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            cs.endText();
            y -= 30;

            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Ciudad: " + compra.getEvento().getCiudad());
            cs.endText();
            y -= 30;

            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Recinto: " + compra.getEvento().getRecinto().getNombre());
            cs.endText();
            y -= 30;

            for (ItemCompra item : compra.getItems()) {
                if (item instanceof Entrada) {
                    Entrada e = (Entrada) item;
                    cs.beginText();
                    cs.newLineAtOffset(50, y);
                    cs.showText("Zona: " + e.getZona().getNombre());
                    cs.endText();
                    y -= 25;

                    if (e.getAsiento() != null) {
                        cs.beginText();
                        cs.newLineAtOffset(50, y);
                        cs.showText("Asiento: " + e.getAsiento().getFila() + e.getAsiento().getNumero());
                        cs.endText();
                        y -= 25;
                    }
                }
            }

            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Total: $" + String.format("%,.0f", compra.getTotal()));
            cs.endText();

            cs.close();

            String fileName = "entrada_" + compra.getIdCompra() + ".pdf";
            document.save(fileName);
            document.close();

            mostrarAlerta("PDF Generado", "Se ha generado el archivo: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo generar el PDF: " + e.getMessage());
        }
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