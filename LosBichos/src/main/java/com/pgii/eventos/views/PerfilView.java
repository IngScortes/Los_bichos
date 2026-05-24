package com.pgii.eventos.views;

import com.pgii.eventos.model.Persona;
import com.pgii.eventos.model.Usuario;
import com.pgii.eventos.repository.UsuarioRepository;
import com.pgii.eventos.service.GestorSesion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PerfilView {
    private Stage stage;
    private VBox root;
    private Persona usuarioActivo;
    private UsuarioRepository usuarioRepo;

    private TextField txtNombre;
    private TextField txtEmail;
    private TextField txtTelefono;
    private ListView<String> listMetodosPago;

    public PerfilView(Stage stage) {
        this.stage = stage;
        this.usuarioActivo = GestorSesion.getInstance().getUsuarioActivo();
        this.usuarioRepo = UsuarioRepository.getInstance();
        crearUI();
        cargarDatos();
    }

    private void crearUI() {
        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f1f5f9;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Button btnVolver = new Button("← Volver al Dashboard");
        btnVolver.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 8 15; -fx-cursor: hand;");
        btnVolver.setOnAction(e -> volverDashboard());

        Label titulo = new Label("👤 Mi Perfil");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(btnVolver, spacer, titulo);

        HBox contenido = new HBox(20);
        contenido.setPadding(new Insets(10));

        // Panel información personal
        VBox panelInfo = new VBox(15);
        panelInfo.setStyle("-fx-background-color: white; -fx-background-radius: 20px; -fx-padding: 25px;");
        panelInfo.setPrefWidth(400);

        Label lblInfo = new Label("📋 Información Personal");
        lblInfo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // Avatar
        StackPane avatarCircle = new StackPane();
        avatarCircle.setStyle("-fx-background-color: #6366f1; -fx-background-radius: 50; -fx-min-width: 80; -fx-min-height: 80;");
        String inicial = usuarioActivo.getNombreCompleto().substring(0, 1);
        Label avatarLabel = new Label(inicial);
        avatarLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");
        avatarCircle.getChildren().add(avatarLabel);

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);

        Label lblNombre = new Label("Nombre completo:");
        lblNombre.setStyle("-fx-font-weight: bold;");
        txtNombre = new TextField();
        txtNombre.setPrefWidth(250);

        Label lblEmail = new Label("Correo electrónico:");
        lblEmail.setStyle("-fx-font-weight: bold;");
        txtEmail = new TextField();

        Label lblTelefono = new Label("Teléfono:");
        lblTelefono.setStyle("-fx-font-weight: bold;");
        txtTelefono = new TextField();

        form.add(lblNombre, 0, 0);
        form.add(txtNombre, 1, 0);
        form.add(lblEmail, 0, 1);
        form.add(txtEmail, 1, 1);
        form.add(lblTelefono, 0, 2);
        form.add(txtTelefono, 1, 2);

        Button btnGuardar = new Button("💾 Guardar cambios");
        btnGuardar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25px; -fx-padding: 10 20;");
        btnGuardar.setOnAction(e -> guardarCambios());

        panelInfo.getChildren().addAll(lblInfo, avatarCircle, form, btnGuardar);

        VBox panelPago = new VBox(15);
        panelPago.setStyle("-fx-background-color: white; -fx-background-radius: 20px; -fx-padding: 25px;");
        panelPago.setPrefWidth(400);

        Label lblPago = new Label("💳 Métodos de Pago");
        lblPago.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        listMetodosPago = new ListView<>();
        listMetodosPago.setPrefHeight(150);

        HBox pagoButtons = new HBox(10);
        Button btnAgregar = new Button("➕ Agregar");
        btnAgregar.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnAgregar.setOnAction(e -> agregarMetodoPago());

        Button btnEliminar = new Button("🗑️ Eliminar");
        btnEliminar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 6 15;");
        btnEliminar.setOnAction(e -> eliminarMetodoPago());

        pagoButtons.getChildren().addAll(btnAgregar, btnEliminar);

        // Solo mostrar métodos de pago si es Usuario (no Administrador)
        if (usuarioActivo instanceof Usuario) {
            panelPago.getChildren().addAll(lblPago, listMetodosPago, pagoButtons);
        } else {
            Label lblAdminMsg = new Label("Los administradores no tienen métodos de pago asociados.");
            lblAdminMsg.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic;");
            panelPago.getChildren().addAll(lblPago, lblAdminMsg);
        }

        contenido.getChildren().addAll(panelInfo, panelPago);
        root.getChildren().addAll(header, contenido);
    }

    private void cargarDatos() {
        txtNombre.setText(usuarioActivo.getNombreCompleto());
        txtEmail.setText(usuarioActivo.getEmail());
        txtTelefono.setText(usuarioActivo.getTelefono());

        if (usuarioActivo instanceof Usuario) {
            Usuario usuario = (Usuario) usuarioActivo;
            listMetodosPago.getItems().clear();
            listMetodosPago.getItems().addAll(usuario.getMetodosPago());
        }
    }

    private void guardarCambios() {
        String emailAnterior = usuarioActivo.getEmail();
        String emailNuevo = txtEmail.getText();

        usuarioActivo.setNombreCompleto(txtNombre.getText());
        usuarioActivo.setEmail(emailNuevo);
        usuarioActivo.setTelefono(txtTelefono.getText());

        if (usuarioActivo instanceof Usuario) {
            usuarioRepo.save((Usuario) usuarioActivo);
        }

        // Si el email cambió, actualizar LoginView (esto es más complejo)
        // Solución simple: mostrar mensaje para cerrar sesión

        if (!emailAnterior.equals(emailNuevo)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Email actualizado");
            alert.setHeaderText(null);
            alert.setContentText("Tu correo ha sido actualizado a: " + emailNuevo +
                    "\n\nPor favor, cierra sesión y vuelve a iniciar con tu nuevo correo.");
            alert.showAndWait();
        } else {
            mostrarAlerta("Éxito", "Información actualizada");
        }

        cargarDatos();
    }

    private void agregarMetodoPago() {
        if (!(usuarioActivo instanceof Usuario)) {
            mostrarAlerta("Error", "Los administradores no pueden agregar métodos de pago");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar método de pago");
        dialog.setHeaderText("Nuevo método de pago");
        dialog.setContentText("Ej: Tarjeta de crédito ****1234, PSE, Efectivo");

        dialog.showAndWait().ifPresent(metodo -> {
            if (!metodo.trim().isEmpty()) {
                Usuario usuario = (Usuario) usuarioActivo;
                usuario.agregarMetodoPago(metodo);
                usuarioRepo.save(usuario);
                cargarDatos();
                mostrarAlerta("Éxito", "Método de pago agregado");
            }
        });
    }

    private void eliminarMetodoPago() {
        if (!(usuarioActivo instanceof Usuario)) {
            mostrarAlerta("Error", "Los administradores no tienen métodos de pago");
            return;
        }

        String selected = listMetodosPago.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Usuario usuario = (Usuario) usuarioActivo;
            usuario.eliminarMetodoPago(selected);
            usuarioRepo.save(usuario);
            cargarDatos();
            mostrarAlerta("Éxito", "Método de pago eliminado");
        } else {
            mostrarAlerta("Error", "Seleccione un método de pago");
        }
    }

    private void volverDashboard() {
        DashboardView dashboard = new DashboardView(stage);
        Scene scene = new Scene(dashboard.getRoot(), 1300, 800);
        stage.setScene(scene);
        stage.setTitle("Dashboard");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public VBox getRoot() { return root; }
}