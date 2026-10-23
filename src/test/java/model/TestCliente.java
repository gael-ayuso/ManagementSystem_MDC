package model;

import gestionmats.model.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCliente {

    // OPN-05: aplicarPuntosComoDescuento calcula el 10% del saldo y reinicia los puntos a cero
    @Test
    void testAplicarPuntosComoDescuento() {
        Cliente cliente = new Cliente(1, "Carlos Santana", "carlos@mail.com", "555-1234",
                "Calle 1", 150.0, "Herramientas");

        double descuento = cliente.aplicarPuntosComoDescuento();

        // 150.0 puntos * $0.10 por punto = $15.00 de descuento
        assertEquals(15.0, descuento, 0.001, "El descuento debe ser exactamente puntos * 0.10");
        assertEquals(0.0, cliente.getPuntosLealtad(), 0.001, "Los puntos deben quedar en 0 tras consumirse");
    }

    // OPN-05: aplicarPuntosComoDescuento con saldo cero retorna 0.0 y conserva saldo en cero
    @Test
    void testAplicarPuntosConSaldoCero() {
        Cliente cliente = new Cliente(2, "Ana Martinez", "ana@mail.com", "555-5678",
                "Calle 2", 0.0, "Pintura");

        double descuento = cliente.aplicarPuntosComoDescuento();

        assertEquals(0.0, descuento, 0.001);
        assertEquals(0.0, cliente.getPuntosLealtad(), 0.001);
    }

    // OPN-05: acumularPuntos suma el 1% del monto de venta al saldo de puntos
    @Test
    void testAcumularPuntosVentaPositiva() {
        Cliente cliente = new Cliente(3, "Luis Lopez", "luis@mail.com", "555-9999",
                "Calle 3", 50.0, "Plomería");

        cliente.acumularPuntos(2000.0); // 2000 * 0.01 = 20.0 puntos ganados

        assertEquals(70.0, cliente.getPuntosLealtad(), 0.001, "50.0 + 20.0 debe ser 70.0");
    }

    // OPN-05: acumularPuntos con monto cero o negativo no altera el saldo de puntos
    @Test
    void testAcumularPuntosMontoCeroONegativo() {
        Cliente cliente = new Cliente(4, "Maria Torres", "maria@mail.com", "555-4444",
                "Calle 4", 100.0, "Materiales");

        cliente.acumularPuntos(0.0);
        assertEquals(100.0, cliente.getPuntosLealtad(), 0.001);

        cliente.acumularPuntos(-500.0);
        assertEquals(100.0, cliente.getPuntosLealtad(), 0.001);
    }
}
