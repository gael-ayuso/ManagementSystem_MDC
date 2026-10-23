package model;

import gestionmats.model.DetalleOrden;
import gestionmats.model.OrdenCompra;
import gestionmats.model.Producto;
import gestionmats.model.Proveedor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TestOrdenCompra {

    // OPN-03: constructor inicializa la lista de detalles vacía y estado EMITIDA
    @Test
    void testInicializacionOrdenCompra() {
        OrdenCompra orden = new OrdenCompra();
        assertNotNull(orden.getDetalles(), "La lista de detalles no debe ser nula");
        assertTrue(orden.getDetalles().isEmpty(), "La lista inicial debe estar vacía");
        assertEquals("EMITIDA", orden.getEstado());
        assertNotNull(orden.getFechaEmision());
    }

    // OPN-03: addDetalleOrden agrega elementos correctamente y se reflejan en getDetalles
    @Test
    void testAddDetalleOrden() {
        OrdenCompra orden = new OrdenCompra(1, new Proveedor(1, "Proveedor A", "555-1111"));
        Producto p1 = new Producto("M001", "Cemento", "Tolteca", "Construcción", 250.0, 100, 50, "Bulto", 0.0);
        DetalleOrden det1 = new DetalleOrden(p1, 10, 0);

        orden.addDetalleOrden(det1);

        List<DetalleOrden> detalles = orden.getDetalles();
        assertEquals(1, detalles.size(), "Debe contener 1 detalle");
        assertEquals(det1, detalles.get(0));
        assertEquals("M001", detalles.get(0).getProducto().getIdProducto());
    }

    // OPN-03: addDetalleOrden ignora elementos nulos sin alterar la lista
    @Test
    void testAddDetalleOrdenNullIgnorado() {
        OrdenCompra orden = new OrdenCompra();
        orden.addDetalleOrden(null);
        assertEquals(0, orden.getDetalles().size(), "Agregar null no debe modificar el tamaño");
    }

    // OPN-03: múltiples llamadas a addDetalleOrden conservan el orden y cantidad de elementos
    @Test
    void testMultiplesDetallesOrden() {
        OrdenCompra orden = new OrdenCompra(2, new Proveedor(2, "Proveedor B", "555-2222"));
        Producto p1 = new Producto("M001", "Cemento", "Tolteca", "Construcción", 250.0, 100, 50, "Bulto", 0.0);
        Producto p2 = new Producto("M002", "Cal", "Tolteca", "Construcción", 85.0, 200, 100, "Bulto", 0.0);

        orden.addDetalleOrden(new DetalleOrden(p1, 10, 5));
        orden.addDetalleOrden(new DetalleOrden(p2, 20, 20));

        List<DetalleOrden> lista = orden.getDetalles();
        assertEquals(2, lista.size());
        assertEquals("M001", lista.get(0).getProducto().getIdProducto());
        assertEquals("M002", lista.get(1).getProducto().getIdProducto());
    }

    // OPN-03: constructor con parámetros asigna ID y Proveedor correctamente
    @Test
    void testConstructorParametrizado() {
        Proveedor prov = new Proveedor(5, "Acero Monterrey", "555-9876");
        OrdenCompra orden = new OrdenCompra(10, prov);

        assertEquals(10, orden.getIdOrden());
        assertEquals(prov, orden.getProveedor());
        assertEquals("EMITIDA", orden.getEstado());
        assertNotNull(orden.getDetalles());
    }
}
