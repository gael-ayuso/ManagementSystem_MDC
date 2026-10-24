package dao;

import gestionmats.dao.DetalleOrdenDaoCsv;
import gestionmats.model.DetalleOrden;
import gestionmats.utils.CsvUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba unitaria exclusiva para la Oportunidad ID 03 (CRIS-03):
 * Remove Control Flag en DetalleOrdenDaoCsv.actualizarRecepcion(int, String, int).
 */
public class TestDetalleOrdenDaoCsv {

    private DetalleOrdenDaoCsv dao;
    private List<String> respaldoDetalles;

    private static final String RUTA_DETALLES = "dataBase/DetallesOrden.csv";

    @BeforeEach
    void setUp() {
        dao = new DetalleOrdenDaoCsv();
        respaldoDetalles = CsvUtils.leerArchivo(RUTA_DETALLES);
    }

    @AfterEach
    void tearDown() {
        if (respaldoDetalles != null) {
            CsvUtils.escribirArchivo(RUTA_DETALLES, respaldoDetalles);
        }
    }

    // CRIS-03: actualizarRecepcion encuentra la fila coincidente, actualiza la cantidad, guarda y retorna true
    @Test
    void testActualizarRecepcionExitosoRetornaTrue() {
        boolean actualizado = dao.actualizarRecepcion(2, "M001", 9);
        assertTrue(actualizado, "actualizarRecepcion debe retornar true al encontrar la fila de la orden y producto");

        List<DetalleOrden> detalles = dao.buscarPorIdOrden(2);
        boolean encontrado = false;
        for (DetalleOrden d : detalles) {
            if ("M001".equals(d.getProducto().getIdProducto())) {
                assertEquals(9, d.getCantidadRecibida(), "La cantidad recibida de M001 debe actualizarse a 9");
                encontrado = true;
            } else if ("M002".equals(d.getProducto().getIdProducto())) {
                assertEquals(12, d.getCantidadRecibida(), "Los demás renglones de la orden no deben alterarse");
            }
        }
        assertTrue(encontrado, "El producto M001 debe seguir existiendo en la orden 2");
    }

    // CRIS-03: actualizarRecepcion con un idOrden inexistente retorna false y no modifica el archivo
    @Test
    void testActualizarRecepcionOrdenInexistenteRetornaFalse() {
        List<String> lineasAntes = CsvUtils.leerArchivo(RUTA_DETALLES);

        boolean resultado = dao.actualizarRecepcion(99999, "M001", 10);
        assertFalse(resultado, "Debe retornar false cuando el idOrden no existe en el archivo");

        List<String> lineasDespues = CsvUtils.leerArchivo(RUTA_DETALLES);
        assertEquals(lineasAntes, lineasDespues, "El contenido del archivo CSV no debe modificarse");
    }

    // CRIS-03: actualizarRecepcion con un idProducto inexistente dentro de una orden válida retorna false
    @Test
    void testActualizarRecepcionProductoInexistenteRetornaFalse() {
        List<String> lineasAntes = CsvUtils.leerArchivo(RUTA_DETALLES);

        boolean resultado = dao.actualizarRecepcion(2, "PROD_INEXISTENTE", 10);
        assertFalse(resultado, "Debe retornar false cuando el producto no pertenece a la orden");

        List<String> lineasDespues = CsvUtils.leerArchivo(RUTA_DETALLES);
        assertEquals(lineasAntes, lineasDespues, "El contenido del archivo CSV no debe modificarse");
    }
}
