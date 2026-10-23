package model;

import gestionmats.model.EstadoProducto;
import gestionmats.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestProducto {

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("M001", "Varilla Corrugada", "Sicartsa", "Construcción",
                180.0, 100, 50, "Pieza", 0.0);
    }

    // OPN-04: calcularStockMinimo devuelve el 25% entero del stockMaximo
    @Test
    void testCalcularStockMinimo() {
        producto.setStockMaximo(100);
        assertEquals(25, producto.calcularStockMinimo(), "100 * 0.25 debe ser 25");

        producto.setStockMaximo(40);
        assertEquals(10, producto.calcularStockMinimo(), "40 * 0.25 debe ser 10");

        producto.setStockMaximo(37);
        assertEquals(9, producto.calcularStockMinimo(), "37 * 0.25 debe truncar a 9");

        producto.setStockMaximo(0);
        assertEquals(0, producto.calcularStockMinimo(), "0 * 0.25 debe ser 0");
    }

    // OPN-04: getEstado evalúa LLENO cuando stockActual es igual a stockMaximo
    @Test
    void testEstadoLleno() {
        producto.setStockMaximo(100);
        producto.setStockActual(100);
        assertEquals(EstadoProducto.LLENO, producto.getEstado());
    }

    // OPN-04: getEstado evalúa OK cuando stockActual es estrictamente mayor al stock mínimo
    @Test
    void testEstadoOk() {
        producto.setStockMaximo(100); // stockMinimo = 25
        producto.setStockActual(50);
        assertEquals(EstadoProducto.OK, producto.getEstado());

        producto.setStockActual(26); // Justo por encima de 25
        assertEquals(EstadoProducto.OK, producto.getEstado());

        producto.setStockActual(99); // Justo debajo de lleno
        assertEquals(EstadoProducto.OK, producto.getEstado());
    }

    // OPN-04: getEstado evalúa BAJO cuando stockActual está entre el nivel crítico (10%) y el mínimo (25%)
    @Test
    void testEstadoBajo() {
        producto.setStockMaximo(100); // stockMinimo = 25, nivelCritico = 10
        producto.setStockActual(25); // En el límite del mínimo
        assertEquals(EstadoProducto.BAJO, producto.getEstado());

        producto.setStockActual(15); // En medio
        assertEquals(EstadoProducto.BAJO, producto.getEstado());

        producto.setStockActual(11); // Justo por encima del nivel crítico
        assertEquals(EstadoProducto.BAJO, producto.getEstado());
    }

    // OPN-04: getEstado evalúa CRITICO cuando stockActual es menor o igual al nivel crítico (10%)
    @Test
    void testEstadoCritico() {
        producto.setStockMaximo(100); // nivelCritico = 10
        producto.setStockActual(10); // En el límite crítico
        assertEquals(EstadoProducto.CRITICO, producto.getEstado());

        producto.setStockActual(5);
        assertEquals(EstadoProducto.CRITICO, producto.getEstado());

        producto.setStockActual(0); // Agotado
        assertEquals(EstadoProducto.CRITICO, producto.getEstado());
    }
}
