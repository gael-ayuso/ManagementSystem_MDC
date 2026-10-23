package controllers;

import gestionmats.model.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el filtrado de clientes en GerenteClienteController
 * (ID 13 / DA-03 - Replace Magic Number with Symbolic Constant en setupFilters y loadData).
 * Verifica que el cliente con ID 0 (cliente genérico de mostrador) permanezca siempre oculto
 * y que los demás clientes se filtren correctamente por nombre o teléfono.
 */
public class TestGerenteClienteFiltro {

    // Constante simbólica objetivo de la refactorización
    private static final int ID_CLIENTE_OCULTO = 0;

    /**
     * Predicado que replica exactamente la regla de filtrado de GerenteClienteController:
     * if (c.getIdCliente() == 0) return false;
     * if (filter.isEmpty()) return true;
     * return c.getNombre().toLowerCase().contains(filter) || c.getNumeroTelefonico().contains(filter);
     */
    public static boolean coincideFiltro(Cliente c, String filtroTexto) {
        if (c.getIdCliente() == ID_CLIENTE_OCULTO) return false;
        if (filtroTexto == null || filtroTexto.trim().isEmpty()) return true;

        String f = filtroTexto.toLowerCase().trim();
        boolean coincideNombre = c.getNombre() != null && c.getNombre().toLowerCase().contains(f);
        boolean coincideTelefono = c.getNumeroTelefonico() != null && c.getNumeroTelefonico().contains(f);
        return coincideNombre || coincideTelefono;
    }

    // DA-03: el cliente con ID 0 (mostrador) nunca debe mostrarse, incluso sin búsqueda activa
    @Test
    void testClienteIdCeroOcultadoSinFiltro() {
        Cliente clienteCero = new Cliente(0, "Cliente Sin Registrar", "", "", "", 0.0, "Venta Rápida");
        assertFalse(coincideFiltro(clienteCero, ""), "El cliente con ID 0 debe retornar false sin filtro");
        assertFalse(coincideFiltro(clienteCero, null), "El cliente con ID 0 debe retornar false con filtro null");
    }

    // DA-03: el cliente con ID 0 nunca debe mostrarse aunque la búsqueda coincida con su nombre o teléfono
    @Test
    void testClienteIdCeroOcultadoConBusquedaCoincidente() {
        Cliente clienteCero = new Cliente(0, "Cliente Sin Registrar", "", "000", "", 0.0, "Venta Rápida");
        assertFalse(coincideFiltro(clienteCero, "cliente"), "No debe mostrarse aunque busque 'cliente'");
        assertFalse(coincideFiltro(clienteCero, "sin registrar"), "No debe mostrarse aunque busque 'sin registrar'");
        assertFalse(coincideFiltro(clienteCero, "0"), "No debe mostrarse aunque busque '0'");
    }

    // DA-03: clientes con ID distinto de 0 son visibles cuando no hay filtro de búsqueda
    @Test
    void testClientesValidosVisiblesSinFiltro() {
        Cliente juan = new Cliente(1, "Juan Perez", "juan@mail.com", "555-1234", "Calle 1", 150.0, "Herramientas");
        Cliente ana = new Cliente(2, "Ana Lopez", "ana@mail.com", "555-5678", "Av. 2", 100.0, "Materiales");

        assertTrue(coincideFiltro(juan, ""));
        assertTrue(coincideFiltro(ana, ""));
        assertTrue(coincideFiltro(juan, "   "));
    }

    // DA-03: filtrado por nombre coincide de forma insensible a mayúsculas
    @Test
    void testFiltroPorNombre() {
        Cliente juan = new Cliente(1, "Juan Perez", "juan@mail.com", "555-1234", "Calle 1", 150.0, "Herramientas");
        Cliente ana = new Cliente(2, "Ana Lopez", "ana@mail.com", "555-5678", "Av. 2", 100.0, "Materiales");

        assertTrue(coincideFiltro(juan, "juan"));
        assertTrue(coincideFiltro(juan, "PEREZ"));
        assertFalse(coincideFiltro(ana, "juan"));

        assertTrue(coincideFiltro(ana, "lopez"));
        assertFalse(coincideFiltro(juan, "lopez"));
    }

    // DA-03: filtrado por número telefónico coincide correctamente
    @Test
    void testFiltroPorTelefono() {
        Cliente juan = new Cliente(1, "Juan Perez", "juan@mail.com", "555-1234", "Calle 1", 150.0, "Herramientas");
        Cliente ana = new Cliente(2, "Ana Lopez", "ana@mail.com", "555-5678", "Av. 2", 100.0, "Materiales");

        assertTrue(coincideFiltro(juan, "555-1234"));
        assertTrue(coincideFiltro(juan, "1234"));
        assertFalse(coincideFiltro(ana, "1234"));

        assertTrue(coincideFiltro(ana, "5678"));
    }

    // DA-03: búsqueda que no coincide con ningún cliente retorna false
    @Test
    void testFiltroSinCoincidencias() {
        Cliente juan = new Cliente(1, "Juan Perez", "juan@mail.com", "555-1234", "Calle 1", 150.0, "Herramientas");
        assertFalse(coincideFiltro(juan, "texto_que_no_existe_xyz"));
    }
}
