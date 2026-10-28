package gestionmats.controllers.gerente;

import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.Producto;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GerenteProductoFormController implements Initializable {

    @FXML private TextField txtId, txtNombre, txtMarca, txtUnidad, txtPrecio, txtDescuento, txtStockActual, txtStockMaximo;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private Button btnGuardar, btnCancelar;

    private ProductoDaoCsv dao;
    private boolean guardadoExitoso = false;

    // Si esta variable es nula, estamos Creando. Si tiene datos, estamos Editando.
    private Producto productoEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new ProductoDaoCsv();

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Cemento y concreto", "Acero y metales", "Madera y derivados",
                "Impermeabilizantes", "Herramientas", "Tubería y plomería"
        ));
        cmbCategoria.getSelectionModel().selectFirst();

        // Por defecto, asumimos que es una creación
        txtId.setText(dao.generarSiguienteId());

        UIComponents.applyButtonAdd(btnGuardar);
        UIComponents.applyButtonGhost(btnCancelar);
    }

    /**
     * MÉTODO NUEVO: Lo llama el inventario cuando queremos editar un material.
     */
    public void cargarProductoParaEdicion(Producto p) {
        this.productoEdicion = p; // Guardamos la referencia

        txtId.setText(p.getIdProducto());
        txtNombre.setText(p.getNombre());
        txtMarca.setText(p.getMarca());
        cmbCategoria.setValue(p.getCategoria());
        txtUnidad.setText(p.getUnidadMedida());
        txtPrecio.setText(String.valueOf(p.getPrecioVenta()));
        txtDescuento.setText(String.valueOf(p.getDescuento()));
        txtStockActual.setText(String.valueOf(p.getStockActual()));
        txtStockMaximo.setText(String.valueOf(p.getStockMaximo()));

        // BLOQUEO DE SEGURIDAD (Tú lo pediste)
        txtStockActual.setDisable(true);
        btnGuardar.setText("Actualizar Material");
    }

    @FXML
    private void handleGuardar() {

        if (hayCamposObligatoriosVacios()) {
            UIComponents.showNotification(
                    "Complete todos los campos obligatorios.",
                    "warn"
            );
            return;
        }

        if (nombreProductoDuplicado()) {
            UIComponents.showNotification(
                    "Ya existe un material registrado con ese nombre.",
                    "error"
            );
            return;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            double descuento = txtDescuento.getText().trim().isEmpty()
                    ? 0.0
                    : Double.parseDouble(txtDescuento.getText().trim());

            int stockActual = Integer.parseInt(txtStockActual.getText().trim());
            int stockMax = Integer.parseInt(txtStockMaximo.getText().trim());

            if (stockEsInvalido(stockActual, stockMax)) {
                UIComponents.showNotification(
                        "El stock actual no puede ser mayor al stock máximo.",
                        "warn"
                );
                return;
            }

            Producto productoProcesado = new Producto(
                    txtId.getText(),
                    txtNombre.getText().trim(),
                    txtMarca.getText().trim(),
                    cmbCategoria.getValue(),
                    precio,
                    stockMax,
                    stockActual,
                    txtUnidad.getText().trim(),
                    descuento
            );

            boolean exito;

            if (productoEdicion == null) {
                exito = dao.guardar(productoProcesado);
            } else {
                exito = dao.actualizar(productoProcesado);
            }

            if (exito) {
                guardadoExitoso = true;
                cerrarVentana();
            } else {
                UIComponents.showNotification(
                        "Error al escribir en la base de datos.",
                        "error"
                );
            }

        } catch (NumberFormatException e) {
            UIComponents.showNotification(
                    "Revise que el precio y los stocks sean números válidos.",
                    "error"
            );
        }
    }

    // Métodos Decompose Conditional

    private boolean hayCamposObligatoriosVacios() {
        return hayCamposObligatoriosVacios(
                txtNombre.getText(),
                txtMarca.getText(),
                txtUnidad.getText(),
                txtPrecio.getText(),
                txtStockActual.getText(),
                txtStockMaximo.getText()
        );
    }

    public boolean hayCamposObligatoriosVacios(
            String nombre,
            String marca,
            String unidad,
            String precio,
            String stockActual,
            String stockMaximo) {

        return nombre == null || nombre.trim().isEmpty()
                || marca == null || marca.trim().isEmpty()
                || unidad == null || unidad.trim().isEmpty()
                || precio == null || precio.trim().isEmpty()
                || stockActual == null || stockActual.trim().isEmpty()
                || stockMaximo == null || stockMaximo.trim().isEmpty();
    }


    private boolean nombreProductoDuplicado() {
        boolean existeEnDao =
                dao.existeProductoPorNombreExacto(txtNombre.getText());

        return nombreProductoDuplicado(
                productoEdicion,
                txtNombre.getText(),
                existeEnDao
        );
    }

    public boolean nombreProductoDuplicado(
            Producto productoEdicion,
            String nuevoNombre,
            boolean existeEnDao) {

        if (nuevoNombre == null) {
            return false;
        }

        boolean nombreModificado =
                productoEdicion == null
                        || !productoEdicion.getNombre()
                        .equalsIgnoreCase(nuevoNombre.trim());

        return nombreModificado && existeEnDao;
    }


    public boolean stockEsInvalido(int stockActual, int stockMax) {
        return stockActual > stockMax;
    }


    @FXML private void handleCancelar() { cerrarVentana(); }
    private void cerrarVentana() { ((Stage) btnCancelar.getScene().getWindow()).close(); }
    public boolean isGuardadoExitoso() { return guardadoExitoso; }
}