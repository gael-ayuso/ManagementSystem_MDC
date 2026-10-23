package model;

import gestionmats.model.DetalleOrden;
import gestionmats.model.Producto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestDetalleOrden {

    // OPN-02: calcularSubtotal con producto válido multiplica precioVenta por cantidadEsperada
    @Test
    void testCalcularSubtotalConProductoValido() {
        Producto p = new Producto("M001", "Cemento", "Tolteca", "Construcción", 250.0, 100, 50, "Bulto", 0.0);
        DetalleOrden detalle = new DetalleOrden(p, 4, 0);

        double esperado = 250.0 * 4; // 1000.0
        assertEquals(esperado, detalle.calcularSubtotal(), 0.001);
    }

    // OPN-02: calcularSubtotal con producto nulo retorna 0.0 sin lanzar NullPointerException
    @Test
    void testCalcularSubtotalConProductoNull() {
        DetalleOrden detalle = new DetalleOrden(null, 5, 0);
        assertEquals(0.0, detalle.calcularSubtotal(), 0.001);
    }

    // OPN-02: calcularSubtotal con cantidad esperada cero retorna 0.0
    @Test
    void testCalcularSubtotalConCantidadCero() {
        Producto p = new Producto("M002", "Cal hidratada", "Tolteca", "Construcción", 85.0, 200, 100, "Bulto", 0.0);
        DetalleOrden detalle = new DetalleOrden(p, 0, 0);
        assertEquals(0.0, detalle.calcularSubtotal(), 0.001);
    }

    // OPN-02: constructores, getters y setters preservan y actualizan atributos
    @Test
    void testConstructoresYGettersSetters() {
        DetalleOrden detalle = new DetalleOrden();
        assertNull(detalle.getProducto());
        assertEquals(0, detalle.getCantidadEsperada());
        assertEquals(0, detalle.getCantidadRecibida());

        Producto p = new Producto("M003", "Arena", "Cantera", "Material", 300.0, 50, 20, "m3", 0.0);
        detalle.setProducto(p);
        detalle.setCantidadEsperada(10);
        detalle.setCantidadRecibida(8);

        assertEquals(p, detalle.getProducto());
        assertEquals(10, detalle.getCantidadEsperada());
        assertEquals(8, detalle.getCantidadRecibida());
        assertEquals(3000.0, detalle.calcularSubtotal(), 0.001);
    }
}
