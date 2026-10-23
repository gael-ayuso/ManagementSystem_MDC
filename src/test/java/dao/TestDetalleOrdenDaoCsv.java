package dao;

import gestionmats.dao.DetalleOrdenDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.DetalleOrden;
import gestionmats.model.Producto;
import gestionmats.utils.CsvUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TestDetalleOrdenDaoCsv {

    private DetalleOrdenDaoCsv dao;
    private ProductoDaoCsv productoDao;
    private List<String> respaldoDetalles;

    private static final String RUTA_DETALLES = "dataBase/DetallesOrden.csv";

    @BeforeEach
    void setUp() {
        dao = new DetalleOrdenDaoCsv();
        productoDao = new ProductoDaoCsv();
        respaldoDetalles = CsvUtils.leerArchivo(RUTA_DETALLES);
    }

    @AfterEach
    void tearDown() {
        // Restaurar estado original del archivo para no alterar los datos de prueba
        if (respaldoDetalles != null) {
            CsvUtils.escribirArchivo(RUTA_DETALLES, respaldoDetalles);
        }
    }

    // CRIS-04: buscarPorIdOrden recupera todos los detalles asociados a una orden
    @Test
    void testBuscarPorIdOrdenExistente() {
        List<DetalleOrden> detalles = dao.buscarPorIdOrden(2);
        assertNotNull(detalles);
        assertEquals(2, detalles.size(), "La orden 2 debe tener 2 detalles");

        DetalleOrden d1 = detalles.get(0);
        DetalleOrden d2 = detalles.get(1);

        assertEquals("M002", d1.getProducto().getIdProducto());
        assertEquals(12, d1.getCantidadEsperada());
        assertEquals(12, d1.getCantidadRecibida());

        assertEquals("M001", d2.getProducto().getIdProducto());
        assertEquals(9, d2.getCantidadEsperada());
        assertEquals(8, d2.getCantidadRecibida());
    }

    // CRIS-04: buscarPorIdOrden con orden inexistente retorna lista vacía
    @Test
    void testBuscarPorIdOrdenInexistente() {
        List<DetalleOrden> detalles = dao.buscarPorIdOrden(99999);
        assertNotNull(detalles, "No debe retornar null");
        assertTrue(detalles.isEmpty(), "Debe retornar una lista vacía");
    }

    // CRIS-03 & CRIS-04: actualizarRecepcion actualiza cantidad y retorna true
    @Test
    void testActualizarRecepcionExitoso() {
        // En orden 2, producto M001 tiene inicialmente cantidadRecibida = 8
        boolean actualizado = dao.actualizarRecepcion(2, "M001", 9);
        assertTrue(actualizado, "actualizarRecepcion debe retornar true al encontrar la fila");

        // Verificar que el cambio se refleje en disco
        List<DetalleOrden> detalles = dao.buscarPorIdOrden(2);
        boolean encontrado = false;
        for (DetalleOrden d : detalles) {
            if ("M001".equals(d.getProducto().getIdProducto())) {
                assertEquals(9, d.getCantidadRecibida(), "La cantidad recibida debe haberse actualizado a 9");
                encontrado = true;
            } else if ("M002".equals(d.getProducto().getIdProducto())) {
                // Verificar que las demás filas no se alteren
                assertEquals(12, d.getCantidadRecibida(), "M002 no debe ser modificado");
            }
        }
        assertTrue(encontrado, "El producto M001 debe existir en la orden 2");
    }

    // CRIS-03: actualizarRecepcion con orden o producto inexistente retorna false y no modifica archivo
    @Test
    void testActualizarRecepcionInexistenteRetornaFalse() {
        int totalLineasAntes = CsvUtils.leerArchivo(RUTA_DETALLES).size();

        // Orden inexistente
        boolean resultadoOrdenFalsa = dao.actualizarRecepcion(99999, "M001", 10);
        assertFalse(resultadoOrdenFalsa, "Debe retornar false si la orden no existe");

        // Producto inexistente en orden válida
        boolean resultadoProdFalso = dao.actualizarRecepcion(2, "PROD_INEXISTENTE", 10);
        assertFalse(resultadoProdFalso, "Debe retornar false si el producto no existe en la orden");

        int totalLineasDespues = CsvUtils.leerArchivo(RUTA_DETALLES).size();
        assertEquals(totalLineasAntes, totalLineasDespues, "El archivo no debe haber cambiado");
    }

    // CRIS-04: guardar agrega un nuevo renglón de detalle para una orden
    @Test
    void testGuardarDetalle() {
        Producto p = productoDao.buscarPorId("M001");
        assertNotNull(p, "El producto M001 debe existir");

        DetalleOrden nuevoDetalle = new DetalleOrden(p, 15, 0);
        boolean exito = dao.guardar(99, nuevoDetalle);
        assertTrue(exito, "guardar() debe retornar true");

        List<DetalleOrden> detalles = dao.buscarPorIdOrden(99);
        assertEquals(1, detalles.size());
        assertEquals("M001", detalles.get(0).getProducto().getIdProducto());
        assertEquals(15, detalles.get(0).getCantidadEsperada());
        assertEquals(0, detalles.get(0).getCantidadRecibida());
    }
}
