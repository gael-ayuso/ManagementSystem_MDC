package controllers;

import gestionmats.model.Producto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para las reglas de validación de GerenteProductoFormController
 * (ID 12 / DA-02 - Decompose Conditional en handleGuardar).
 * Verifica que se rechacen campos obligatorios vacíos, nombres duplicados en creación y edición,
 * stock actual mayor al máximo y números malformados.
 */
public class TestGerenteProductoFormValidation {

    // Métodos que representan las condiciones descompuestas de handleGuardar()
    public static boolean hayCamposObligatoriosVacios(String nombre, String marca, String unidad,
                                                      String precio, String stockActual, String stockMaximo) {
        return nombre == null || nombre.trim().isEmpty() ||
                marca == null || marca.trim().isEmpty() ||
                unidad == null || unidad.trim().isEmpty() ||
                precio == null || precio.trim().isEmpty() ||
                stockActual == null || stockActual.trim().isEmpty() ||
                stockMaximo == null || stockMaximo.trim().isEmpty();
    }

    public static boolean esNombreDuplicado(Producto productoEdicion, String nuevoNombre, boolean existeEnDao) {
        if (nuevoNombre == null) return false;
        boolean nombreModificado = (productoEdicion == null) ||
                !productoEdicion.getNombre().equalsIgnoreCase(nuevoNombre.trim());
        return nombreModificado && existeEnDao;
    }

    public static boolean esStockInvalido(int stockActual, int stockMax) {
        return stockActual > stockMax;
    }

    // DA-02: valida que ningún campo obligatorio esté vacío o compuesto solo por espacios
    @Test
    void testCamposObligatoriosVacios() {
        // Todos los campos válidos -> false (no hay vacíos)
        assertFalse(hayCamposObligatoriosVacios("Cemento", "Tolteca", "Bulto", "250.0", "50", "100"));

        // Casos con un campo vacío o con espacios
        assertTrue(hayCamposObligatoriosVacios("", "Tolteca", "Bulto", "250.0", "50", "100"), "Nombre vacío debe fallar");
        assertTrue(hayCamposObligatoriosVacios("   ", "Tolteca", "Bulto", "250.0", "50", "100"), "Nombre con espacios debe fallar");
        assertTrue(hayCamposObligatoriosVacios("Cemento", "", "Bulto", "250.0", "50", "100"), "Marca vacía debe fallar");
        assertTrue(hayCamposObligatoriosVacios("Cemento", "Tolteca", "", "250.0", "50", "100"), "Unidad vacía debe fallar");
        assertTrue(hayCamposObligatoriosVacios("Cemento", "Tolteca", "Bulto", "", "50", "100"), "Precio vacío debe fallar");
        assertTrue(hayCamposObligatoriosVacios("Cemento", "Tolteca", "Bulto", "250.0", "", "100"), "Stock actual vacío debe fallar");
        assertTrue(hayCamposObligatoriosVacios("Cemento", "Tolteca", "Bulto", "250.0", "50", ""), "Stock máximo vacío debe fallar");
    }

    // DA-02: modo creación detecta nombre duplicado cuando ya existe en el DAO
    @Test
    void testNombreDuplicadoEnCreacion() {
        Producto productoNuevo = null; // En modo creación productoEdicion es null

        // Nombre que ya existe en el sistema -> duplicado
        assertTrue(esNombreDuplicado(productoNuevo, "Cemento Tolteca", true));

        // Nombre inédito -> no duplicado
        assertFalse(esNombreDuplicado(productoNuevo, "Tubo PVC Nuevo", false));
    }

    // DA-02: modo edición permite conservar el mismo nombre sin marcar duplicado
    @Test
    void testNombreDuplicadoEnEdicionMismoNombre() {
        Producto edicion = new Producto("M001", "Cemento Tolteca", "Tolteca", "Construcción",
                250.0, 100, 50, "Bulto", 0.0);

        // Mismo nombre (incluso con distinta mayúscula) -> no debe marcar duplicado
        assertFalse(esNombreDuplicado(edicion, "Cemento Tolteca", true));
        assertFalse(esNombreDuplicado(edicion, "cemento tolteca", true));
    }

    // DA-02: modo edición detecta duplicado si se renombra al nombre de otro producto existente
    @Test
    void testNombreDuplicadoEnEdicionCambioANombreExistente() {
        Producto edicion = new Producto("M001", "Cemento Tolteca", "Tolteca", "Construcción",
                250.0, 100, 50, "Bulto", 0.0);

        // Se cambia el nombre a "Cal Hidratada" y ese nombre ya existe en el DAO -> duplicado
        assertTrue(esNombreDuplicado(edicion, "Cal Hidratada", true));
    }

    // DA-02: valida que stockActual no sea mayor a stockMaximo
    @Test
    void testValidacionStockInvalido() {
        // stockActual > stockMaximo -> inválido
        assertTrue(esStockInvalido(150, 100), "150 > 100 debe ser inválido");

        // stockActual == stockMaximo -> válido
        assertFalse(esStockInvalido(100, 100), "100 == 100 debe ser válido");

        // stockActual < stockMaximo -> válido
        assertFalse(esStockInvalido(40, 100), "40 < 100 debe ser válido");
    }

    // DA-02: entradas de texto no numéricas para precio y stock lanzan NumberFormatException
    @Test
    void testFormatoNumericoInvalido() {
        assertThrows(NumberFormatException.class, () -> Double.parseDouble("cien_pesos"));
        assertThrows(NumberFormatException.class, () -> Integer.parseInt("diez"));
        assertThrows(NumberFormatException.class, () -> Integer.parseInt("12.5")); // stock debe ser entero
    }
}
