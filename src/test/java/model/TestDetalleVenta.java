package model;

import gestionmats.model.DetalleVenta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestDetalleVenta {

    // DG-05: creación sin descuento calcula subtotal = total = precio * cantidad
    @Test
    void testCalculoSubtotalYTotalSinDescuento() {
        DetalleVenta item = new DetalleVenta(1, "M001", "Cemento Tolteca", 2, 100.0, 0.0);

        assertEquals(2, item.getCantidad());
        assertEquals(100.0, item.getPrecioUnitario(), 0.001);
        assertEquals(0.0, item.getDescuentoAplicado(), 0.001);
        assertEquals(200.0, item.getSubtotal(), 0.001);
        assertEquals(200.0, item.getTotal(), 0.001);
    }

    // DG-05: creación con 10% de descuento aplica total = subtotal * 0.90
    @Test
    void testCalculoSubtotalYTotalConDescuento() {
        DetalleVenta item = new DetalleVenta(1, "M001", "Cemento Tolteca", 2, 100.0, 10.0);

        assertEquals(2, item.getCantidad());
        assertEquals(200.0, item.getSubtotal(), 0.001);
        assertEquals(180.0, item.getTotal(), 0.001, "200.0 * (1 - 10/100) debe ser 180.0");
    }

    // DG-05: incrementar cantidad sin descuento produce montos coherentes (precio 100, cant 2 + 3 = 5, total 500)
    @Test
    void testRecalculoAgregarCantidadSinDescuento() {
        DetalleVenta item = new DetalleVenta(1, "M001", "Cemento Tolteca", 2, 100.0, 0.0);

        item.agregarCantidad(3);

        assertEquals(5, item.getCantidad());
        assertEquals(500.0, item.getSubtotal(), 0.001);
        assertEquals(500.0, item.getTotal(), 0.001);

        // Coincide exactamente con crear un DetalleVenta nuevo con cantidad 5
        DetalleVenta nuevoEsperado = new DetalleVenta(1, "M001", "Cemento Tolteca", 5, 100.0, 0.0);
        assertEquals(nuevoEsperado.getSubtotal(), item.getSubtotal(), 0.001);
        assertEquals(nuevoEsperado.getTotal(), item.getTotal(), 0.001);
    }

    // DG-05: incrementar cantidad con 10% de descuento produce montos coherentes (cant 2 + 3 = 5, total 450)
    @Test
    void testRecalculoAgregarCantidadConDescuento() {
        DetalleVenta item = new DetalleVenta(1, "M001", "Cemento Tolteca", 2, 100.0, 10.0);

        item.agregarCantidad(3);

        assertEquals(5, item.getCantidad());
        assertEquals(500.0, item.getSubtotal(), 0.001);
        assertEquals(450.0, item.getTotal(), 0.001, "500.0 * 0.90 debe ser 450.0");

        DetalleVenta nuevoEsperado = new DetalleVenta(1, "M001", "Cemento Tolteca", 5, 100.0, 10.0);
        assertEquals(nuevoEsperado.getSubtotal(), item.getSubtotal(), 0.001);
        assertEquals(nuevoEsperado.getTotal(), item.getTotal(), 0.001);
    }
}
