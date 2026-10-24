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

/**
 * Prueba unitaria exclusiva para la Oportunidad ID 04 (CRIS-04):
 * Replace Magic Number with Symbolic Constant en DetalleOrdenDaoCsv
 * (índices de columnas 0, 1, 2, 3 y mínimo de 4 columnas en buscarPorIdOrden y actualizarRecepcion).
 */
public class TestDetalleOrdenConstantesCsv {

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
        if (respaldoDetalles != null) {
            CsvUtils.escribirArchivo(RUTA_DETALLES, respaldoDetalles);
        }
    }

    // CRIS-04: buscarPorIdOrden interpreta correctamente las columnas 0 (idOrden), 1 (idProducto), 2 (esperada) y 3 (recibida)
    @Test
    void testMapeoDeColumnasEnBuscarPorIdOrden() {
        List<DetalleOrden> detalles = dao.buscarPorIdOrden(2);
        assertNotNull(detalles);
        assertEquals(2, detalles.size(), "La orden 2 debe recuperar sus 2 detalles");

        DetalleOrden d1 = detalles.get(0);
        DetalleOrden d2 = detalles.get(1);

        // Columna 1: idProducto, Columna 2: cantidadEsperada, Columna 3: cantidadRecibida
        assertEquals("M002", d1.getProducto().getIdProducto());
        assertEquals(12, d1.getCantidadEsperada());
        assertEquals(12, d1.getCantidadRecibida());

        assertEquals("M001", d2.getProducto().getIdProducto());
        assertEquals(9, d2.getCantidadEsperada());
        assertEquals(8, d2.getCantidadRecibida());
    }

    // CRIS-04: buscarPorIdOrden filtra por la columna 0 (idOrden) y retorna vacío cuando no hay coincidencias
    @Test
    void testFiltradoPorColumnaIdOrdenInexistente() {
        List<DetalleOrden> detalles = dao.buscarPorIdOrden(99999);
        assertNotNull(detalles, "No debe retornar null");
        assertTrue(detalles.isEmpty(), "Debe retornar lista vacía si ninguna fila coincide en la columna idOrden");
    }

    // CRIS-04: guardar y actualizarRecepcion respetan las posiciones de cantidadEsperada (col 2) y cantidadRecibida (col 3)
    @Test
    void testPosicionesColumnasAlGuardarYActualizar() {
        Producto p = productoDao.buscarPorId("M001");
        assertNotNull(p, "El producto M001 debe existir");

        DetalleOrden nuevoDetalle = new DetalleOrden(p, 25, 5);
        assertTrue(dao.guardar(77, nuevoDetalle));

        // Verificar que la columna 2 sea 25 (esperada) y la columna 3 sea 5 (recibida)
        List<DetalleOrden> leidos = dao.buscarPorIdOrden(77);
        assertEquals(1, leidos.size());
        assertEquals("M001", leidos.get(0).getProducto().getIdProducto());
        assertEquals(25, leidos.get(0).getCantidadEsperada());
        assertEquals(5, leidos.get(0).getCantidadRecibida());

        // Al actualizar recepción, solo debe cambiar la columna 3 (cantidadRecibida) y conservar la columna 2 (cantidadEsperada)
        assertTrue(dao.actualizarRecepcion(77, "M001", 20));
        List<DetalleOrden> actualizados = dao.buscarPorIdOrden(77);
        assertEquals(25, actualizados.get(0).getCantidadEsperada(), "La columna de cantidadEsperada debe conservarse en 25");
        assertEquals(20, actualizados.get(0).getCantidadRecibida(), "La columna de cantidadRecibida debe actualizarse a 20");
    }
}
