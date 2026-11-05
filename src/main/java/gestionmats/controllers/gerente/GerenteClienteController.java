package gestionmats.controllers.gerente;

import gestionmats.dao.ClienteDaoCsv;
import gestionmats.model.Cliente;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GerenteClienteController implements Initializable {

    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre, colTelefono, colEmail, colDireccion, colPreferencias;
    @FXML private TableColumn<Cliente, Double> colPuntos;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotal;
    @FXML private Button btnAgregar, btnEditar, btnEliminar;

    private static final int ID_CLIENTE_OCULTO = 0;

    private ClienteDaoCsv clienteDao;
    private ObservableList<Cliente> allItems;
    private FilteredList<Cliente> filteredItems;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clienteDao = new ClienteDaoCsv();
        setupStyles();
        setupTable();
        loadData();
        setupFilters();
    }

    private void setupStyles() {
        UIComponents.applyButtonAdd(btnAgregar);
        UIComponents.applyButtonEdit(btnEditar);
        UIComponents.applyButtonDelete(btnEliminar);
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("numeroTelefonico"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colPreferencias.setCellValueFactory(new PropertyValueFactory<>("preferencias"));

        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntosLealtad"));
        colPuntos.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.0f", item));
                    setStyle("-fx-text-fill: #F58220; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void loadData() {
        allItems = FXCollections.observableArrayList(clienteDao.listarTodos());

        filteredItems = new FilteredList<>(
                allItems,
                c -> c.getIdCliente() != 0
        );

        tableClientes.setItems(filteredItems);

        filteredItems.addListener(
                (javafx.collections.ListChangeListener<Cliente>) c ->
                        lblTotal.setText(
                                "Total: " + filteredItems.size() + " clientes"
                        )
        );

        lblTotal.setText(
                "Total: " + filteredItems.size() + " clientes"
        );
    }

    private void setupFilters() {
        txtSearch.textProperty().addListener((obs, old, newVal) -> {
            filteredItems.setPredicate(
                    c -> coincideFiltro(c, newVal)
            );
        });
    }

    public boolean coincideFiltro(
            Cliente c,
            String filtroTexto) {

        if (c.getIdCliente() == ID_CLIENTE_OCULTO) {
            return false;
        }

        if (filtroTexto == null || filtroTexto.trim().isEmpty()) {
            return true;
        }

        String filter = filtroTexto.toLowerCase().trim();

        boolean coincideNombre =
                c.getNombre() != null
                        && c.getNombre()
                        .toLowerCase()
                        .contains(filter);

        boolean coincideTelefono =
                c.getNumeroTelefonico() != null
                        && c.getNumeroTelefonico()
                        .contains(filter);

        return coincideNombre || coincideTelefono;
    }

    @FXML
    private void handleNuevoCliente() {
        abrirDialogoCliente(null);
    }

    @FXML
    private void handleEditarCliente() {
        Cliente sel = tableClientes.getSelectionModel().getSelectedItem();

        if (sel == null) {
            UIComponents.showNotification(
                    "Seleccione un cliente para editar",
                    "warn"
            );
            return;
        }

        abrirDialogoCliente(sel);
    }

    private void abrirDialogoCliente(Cliente clienteExistente) {
        Dialog<Cliente> dialog = new Dialog<>();
        boolean isEdicion = clienteExistente != null;

        dialog.setTitle(
                isEdicion ? "Editar Cliente" : "Nuevo Cliente"
        );

        dialog.setHeaderText(
                isEdicion
                        ? "Editando: " + clienteExistente.getNombre()
                        : "Registrar nuevo cliente"
        );

        ButtonType btnGuardar =
                new ButtonType(
                        "Guardar",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(btnGuardar, ButtonType.CANCEL);

        dialog.getDialogPane().setStyle(
                "-fx-background-color: #111318; -fx-base: #111318;"
        );

        GridPane grid = new GridPane();

        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField txtNombre =
                new TextField(
                        isEdicion
                                ? clienteExistente.getNombre()
                                : ""
                );

        TextField txtTelefono =
                new TextField(
                        isEdicion
                                ? clienteExistente.getNumeroTelefonico()
                                : ""
                );

        TextField txtEmail =
                new TextField(
                        isEdicion
                                ? clienteExistente.getCorreoElectronico()
                                : ""
                );

        TextField txtDireccion =
                new TextField(
                        isEdicion
                                ? clienteExistente.getDireccion()
                                : ""
                );

        TextField txtPreferencias =
                new TextField(
                        isEdicion
                                ? clienteExistente.getPreferencias()
                                : ""
                );

        txtNombre.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white;"
        );

        txtTelefono.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white;"
        );

        txtEmail.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white;"
        );

        txtDireccion.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white;"
        );

        txtPreferencias.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white;"
        );

        grid.addRow(
                0,
                new Label("Nombre:"),
                txtNombre
        );

        grid.addRow(
                1,
                new Label("Teléfono:"),
                txtTelefono
        );

        grid.addRow(
                2,
                new Label("Email:"),
                txtEmail
        );

        grid.addRow(
                3,
                new Label("Dirección:"),
                txtDireccion
        );

        grid.addRow(
                4,
                new Label("Preferencias:"),
                txtPreferencias
        );

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node btnOk =
                dialog.getDialogPane()
                        .lookupButton(btnGuardar);

        btnOk.setDisable(!isEdicion);

        txtNombre.textProperty().addListener(
                (obs, o, n) ->
                        btnOk.setDisable(
                                n.trim().isEmpty()
                                        || txtTelefono
                                        .getText()
                                        .trim()
                                        .isEmpty()
                        )
        );

        txtTelefono.textProperty().addListener(
                (obs, o, n) ->
                        btnOk.setDisable(
                                txtNombre
                                        .getText()
                                        .trim()
                                        .isEmpty()
                                        || n.trim().isEmpty()
                        )
        );

        dialog.setResultConverter(bt -> {
            if (bt != btnGuardar) {
                return null;
            }

            if (isEdicion) {
                clienteExistente.setNombre(
                        txtNombre.getText().trim()
                );

                clienteExistente.setNumeroTelefonico(
                        txtTelefono.getText().trim()
                );

                clienteExistente.setCorreoElectronico(
                        txtEmail.getText().trim()
                );

                clienteExistente.setDireccion(
                        txtDireccion.getText().trim()
                );

                clienteExistente.setPreferencias(
                        txtPreferencias.getText().trim()
                );

                return clienteExistente;
            } else {
                int nuevoId =
                        clienteDao.obtenerUltimoId() + 1;

                return new Cliente(
                        nuevoId,
                        txtNombre.getText().trim(),
                        txtEmail.getText().trim(),
                        txtTelefono.getText().trim(),
                        txtDireccion.getText().trim(),
                        0.0,
                        txtPreferencias.getText().trim()
                );
            }
        });

        dialog.showAndWait().ifPresent(c -> {
            if (isEdicion) {
                clienteDao.actualizar(c);
            } else {
                clienteDao.guardar(c);
            }

            loadData();

            UIComponents.showNotification(
                    isEdicion
                            ? "Cliente actualizado"
                            : "Cliente registrado",
                    "success"
            );
        });
    }

    @FXML
    private void handleEliminarCliente() {
        Cliente sel =
                tableClientes.getSelectionModel()
                        .getSelectedItem();

        if (sel == null) {
            UIComponents.showNotification(
                    "Seleccione un cliente para eliminar",
                    "warn"
            );
            return;
        }

        boolean confirm =
                UIComponents.showConfirmDialog(
                        "Eliminar cliente",
                        "¿Eliminar a "
                                + sel.getNombre()
                                + "?\nLas ventas históricas no se borrarán."
                );

        if (confirm) {
            clienteDao.eliminar(
                    sel.getIdCliente()
            );

            loadData();

            UIComponents.showNotification(
                    "Cliente eliminado",
                    "success"
            );
        }
    }
}