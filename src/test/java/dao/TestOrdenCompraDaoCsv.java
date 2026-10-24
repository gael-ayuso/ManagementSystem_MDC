package dao;

import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.dao.ProveedorDaoCsv;
import gestionmats.model.DetalleOrden;
import gestionmats.model.OrdenCompra;
import gestionmats.model.Producto;
import gestionmats.model.Proveedor;
import gestionmats.utils.CsvUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba unitaria exclusiva para la Oportunidad ID 02 (CRIS-02):
 * Move Method en OrdenCompraDaoCsv (delegación de cargarDetalles y guardarDetalles a DetalleOrdenDaoCsv).
 */
public class TestOrdenCompraDaoCsv {

    private OrdenCompraDaoCsv ordenDao;
    private ProveedorDaoCsv proveedorDao;
    private List<String> respaldoOrdenes;
    private List<String> respaldoDetalles;

    private static final String RUTA_ORDENES = "dataBase/OrdenesCompra.csv";
    private static final String RUTA_DETALLES = "dataBase/DetallesOrden.csv";

    @BeforeEach
    void setUp() {
        ordenDao = new OrdenCompraDaoCsv();
        proveedorDao = new ProveedorDaoCsv();
        respaldoOrdenes = CsvUtils.leerArchivo(RUTA_ORDENES);
        respaldoDetalles = CsvUtils.leerArchivo(RUTA_DETALLES);
    }

    @AfterEach
    void tearDown() {
        if (respaldoOrdenes != null) {
            CsvUtils.escribirArchivo(RUTA_ORDENES, respaldoOrdenes);
        }
        if (respaldoDetalles != null) {
            CsvUtils.escribirArchivo(RUTA_DETALLES, respaldoDetalles);
        }
    }

    // CRIS-02: listarTodos recupera órdenes y carga sus listas de DetalleOrden asociadas
    @Test
    void testListarTodosYCargarDetalles() {
        List<OrdenCompra> ordenes = ordenDao.listarTodos();
        assertNotNull(ordenes, "La lista de órdenes no debe ser nula");
        assertFalse(ordenes.isEmpty(), "Debe haber órdenes registradas");

        OrdenCompra orden2 = null;
        for (OrdenCompra o : ordenes) {
            if (o.getIdOrden() == 2) {
                orden2 = o;
                break;
            }
        }

        assertNotNull(orden2, "La orden con ID 2 debe existir en el CSV");
        assertEquals("INCOMPLETA", orden2.getEstado());
        assertNotNull(orden2.getProveedor());
        assertEquals(2, orden2.getProveedor().getIdProveedor());

        List<DetalleOrden> detalles = orden2.getDetalles();
        assertNotNull(detalles, "La lista de detalles no debe ser nula");
        assertEquals(2, detalles.size(), "La orden 2 debe tener exactamente 2 detalles cargados");

        boolean tieneM002 = false;
        boolean tieneM001 = false;
        for (DetalleOrden d : detalles) {
            assertNotNull(d.getProducto());
            if ("M002".equals(d.getProducto().getIdProducto())) {
                tieneM002 = true;
                assertEquals(12, d.getCantidadEsperada());
                assertEquals(12, d.getCantidadRecibida());
            } else if ("M001".equals(d.getProducto().getIdProducto())) {
                tieneM001 = true;
                assertEquals(9, d.getCantidadEsperada());
                assertEquals(8, d.getCantidadRecibida());
            }
        }
        assertTrue(tieneM002, "Debe contener el producto M002");
        assertTrue(tieneM001, "Debe contener el producto M001");
    }

    // CRIS-02: buscarPorId recupera la orden con sus detalles correspondientes
    @Test
    void testBuscarPorIdCargaDetallesDeLaOrden() {
        OrdenCompra orden = ordenDao.buscarPorId(1);
        assertNotNull(orden, "La orden 1 debe existir");
        assertEquals(1, orden.getIdOrden());
        assertEquals("SURTIDA", orden.getEstado());

        assertNotNull(orden.getDetalles());
        assertEquals(1, orden.getDetalles().size(), "La orden 1 debe tener 1 detalle");
        assertEquals("M008", orden.getDetalles().get(0).getProducto().getIdProducto());
        assertEquals(89, orden.getDetalles().get(0).getCantidadEsperada());
        assertEquals(0, orden.getDetalles().get(0).getCantidadRecibida());

        OrdenCompra noExiste = ordenDao.buscarPorId(99999);
        assertNull(noExiste, "Buscar una orden inexistente debe retornar null");
    }

    // CRIS-02: guardar persiste cabecera en OrdenesCompra.csv y detalles en DetallesOrden.csv
    @Test
    void testGuardarOrdenYDetalles() {
        int nuevoId = ordenDao.generarSiguienteId();
        Proveedor proveedor = proveedorDao.buscarPorId(1);
        assertNotNull(proveedor, "El proveedor con ID 1 debe existir");

        OrdenCompra nuevaOrden = new OrdenCompra(nuevoId, proveedor);
        nuevaOrden.setEstado("PENDIENTE");

        Producto p = new Producto("M001", "Cemento", "Tolteca", "Construcción", 250.0, 100, 50, "Bulto", 0.0);
        nuevaOrden.addDetalleOrden(new DetalleOrden(p, 20, 0));

        boolean exito = ordenDao.guardar(nuevaOrden);
        assertTrue(exito, "guardar() debe retornar true");

        OrdenCompra recuperada = ordenDao.buscarPorId(nuevoId);
        assertNotNull(recuperada, "La orden guardada debe recuperarse con buscarPorId");
        assertEquals(nuevoId, recuperada.getIdOrden());
        assertEquals("PENDIENTE", recuperada.getEstado());
        assertEquals(1, recuperada.getProveedor().getIdProveedor());

        assertNotNull(recuperada.getDetalles());
        assertEquals(1, recuperada.getDetalles().size(), "Debe haber guardado y cargado el detalle");
        assertEquals("M001", recuperada.getDetalles().get(0).getProducto().getIdProducto());
        assertEquals(20, recuperada.getDetalles().get(0).getCantidadEsperada());
        assertEquals(0, recuperada.getDetalles().get(0).getCantidadRecibida());
    }
}
