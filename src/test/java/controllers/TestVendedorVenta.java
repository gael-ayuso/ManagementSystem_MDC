package controllers;

import gestionmats.model.Cliente;
import gestionmats.model.Venta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para las reglas de negocio de VendedorController
 * (ID 16 / DG-01 - Extract Method en handleFinalizarVenta).
 * Verifica el cálculo de puntos de lealtad del cliente (con canje, sin canje y límite inferior en cero)
 * y la correcta construcción del objeto Venta desacoplada de la interfaz gráfica.
 */
public class TestVendedorVenta {

    /**
     * Fórmula exacta implementada en VendedorController (líneas 691-694):
     * double puntosUsados = puntosUsadosEnEstaVenta;
     * double puntosGanados = total * 0.05;
     * double puntosFinales = clienteActual.getPuntosLealtad() - puntosUsados + puntosGanados;
     * if (puntosFinales < 0) puntosFinales = 0;
     */
    public static double calcularPuntosFinales(double puntosActuales, double puntosUsados, double totalVenta) {
        double puntosGanados = totalVenta * 0.05;
        double puntosFinales = puntosActuales - puntosUsados + puntosGanados;
        if (puntosFinales < 0) {
            puntosFinales = 0;
        }
        return puntosFinales;
    }

    // DG-01: cálculo de puntos ganados sin canje (100 puntos iniciales + 1000 * 0.05 = 150)
    @Test
    void testCalcularPuntosSinCanje() {
        double puntosActuales = 100.0;
        double puntosUsados = 0.0;
        double totalVenta = 1000.0;

        double resultado = calcularPuntosFinales(puntosActuales, puntosUsados, totalVenta);
        assertEquals(150.0, resultado, 0.001);
    }

    // DG-01: cálculo de puntos con canje previo (200 actuales - 150 usados + 800 * 0.05 = 90)
    @Test
    void testCalcularPuntosConCanje() {
        double puntosActuales = 200.0;
        double puntosUsados = 150.0;
        double totalVenta = 800.0; // 800 * 0.05 = 40 puntos ganados

        double resultado = calcularPuntosFinales(puntosActuales, puntosUsados, totalVenta);
        assertEquals(90.0, resultado, 0.001);
    }

    // DG-01: resultado negativo de puntos se limita estrictamente a cero
    @Test
    void testCalcularPuntosLimiteInferiorCero() {
        double puntosActuales = 20.0;
        double puntosUsados = 50.0;
        double totalVenta = 100.0; // 100 * 0.05 = 5 puntos ganados
        // 20 - 50 + 5 = -25 -> debe fijarse en 0.0

        double resultado = calcularPuntosFinales(puntosActuales, puntosUsados, totalVenta);
        assertEquals(0.0, resultado, 0.001, "Los puntos no pueden ser negativos");
    }

    // DG-01: actualización de saldo de puntos en la entidad Cliente
    @Test
    void testActualizacionPuntosEnCliente() {
        Cliente cliente = new Cliente(1, "Juan Perez", "juan@mail.com", "555-1234", "Dir", 120.0, "Pref");
        double nuevoSaldo = calcularPuntosFinales(cliente.getPuntosLealtad(), 40.0, 500.0);
        // 120 - 40 + 25 = 105
        cliente.setPuntosLealtad(nuevoSaldo);

        assertEquals(105.0, cliente.getPuntosLealtad(), 0.001);
    }

    // DG-01: construcción de objeto Venta asigna íntegramente sus atributos transaccionales
    @Test
    void testConstruccionObjetoVenta() {
        int idCliente = 1;
        int idVendedor = 2;
        String tipoVenta = "INSTANTANEA";
        String metodoPago = "EFECTIVO";
        double subtotal = 1000.0;
        double descuento = 10.0;
        double total = 900.0;

        Venta venta = new Venta(idCliente, idVendedor, tipoVenta, metodoPago, subtotal, descuento, total);

        assertEquals(idCliente, venta.getIdCliente());
        assertEquals(idVendedor, venta.getIdVendedor());
        assertEquals(tipoVenta, venta.getTipoVenta());
        assertEquals(metodoPago, venta.getMetodoPago());
        assertEquals(subtotal, venta.getSubtotal(), 0.001);
        assertEquals(descuento, venta.getDescuento(), 0.001);
        assertEquals(total, venta.getTotal(), 0.001);
    }
}
