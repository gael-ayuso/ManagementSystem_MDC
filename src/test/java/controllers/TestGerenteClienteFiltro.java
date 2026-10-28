package controllers;

import gestionmats.controllers.gerente.GerenteClienteController;
import gestionmats.model.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el filtrado de clientes en GerenteClienteController
 * (ID 13 / DA-03 - Replace Magic Number with Symbolic Constant en setupFilters).
 * Verifica que el cliente con ID 0 (cliente genérico de mostrador) permanezca siempre oculto
 * y que los demás clientes se filtren correctamente por nombre o teléfono.
 */
public class TestGerenteClienteFiltro {

    // Instancia real del controlador para probar el método coincideFiltro()
    private final GerenteClienteController controller =
            new GerenteClienteController();

    // DA-03: el cliente con ID 0 (mostrador) nunca debe mostrarse, incluso sin búsqueda activa
    @Test
    void testClienteIdCeroOcultadoSinFiltro() {
        Cliente clienteCero = new Cliente(
                0,
                "Cliente Sin Registrar",
                "",
                "",
                "",
                0.0,
                "Venta Rápida"
        );

        assertFalse(
                controller.coincideFiltro(clienteCero, ""),
                "El cliente con ID 0 debe retornar false sin filtro"
        );

        assertFalse(
                controller.coincideFiltro(clienteCero, null),
                "El cliente con ID 0 debe retornar false con filtro null"
        );
    }

    // DA-03: el cliente con ID 0 nunca debe mostrarse aunque la búsqueda coincida con su nombre o teléfono
    @Test
    void testClienteIdCeroOcultadoConBusquedaCoincidente() {
        Cliente clienteCero = new Cliente(
                0,
                "Cliente Sin Registrar",
                "",
                "000",
                "",
                0.0,
                "Venta Rápida"
        );

        assertFalse(
                controller.coincideFiltro(clienteCero, "cliente"),
                "No debe mostrarse aunque busque 'cliente'"
        );

        assertFalse(
                controller.coincideFiltro(clienteCero, "sin registrar"),
                "No debe mostrarse aunque busque 'sin registrar'"
        );

        assertFalse(
                controller.coincideFiltro(clienteCero, "0"),
                "No debe mostrarse aunque busque '0'"
        );
    }

    // DA-03: clientes con ID distinto de 0 son visibles cuando no hay filtro de búsqueda
    @Test
    void testClientesValidosVisiblesSinFiltro() {
        Cliente juan = new Cliente(
                1,
                "Juan Perez",
                "juan@mail.com",
                "555-1234",
                "Calle 1",
                150.0,
                "Herramientas"
        );

        Cliente ana = new Cliente(
                2,
                "Ana Lopez",
                "ana@mail.com",
                "555-5678",
                "Av. 2",
                100.0,
                "Materiales"
        );

        assertTrue(controller.coincideFiltro(juan, ""));
        assertTrue(controller.coincideFiltro(ana, ""));
        assertTrue(controller.coincideFiltro(juan, "   "));
    }

    // DA-03: filtrado por nombre coincide de forma insensible a mayúsculas
    @Test
    void testFiltroPorNombre() {
        Cliente juan = new Cliente(
                1,
                "Juan Perez",
                "juan@mail.com",
                "555-1234",
                "Calle 1",
                150.0,
                "Herramientas"
        );

        Cliente ana = new Cliente(
                2,
                "Ana Lopez",
                "ana@mail.com",
                "555-5678",
                "Av. 2",
                100.0,
                "Materiales"
        );

        assertTrue(controller.coincideFiltro(juan, "juan"));
        assertTrue(controller.coincideFiltro(juan, "PEREZ"));
        assertFalse(controller.coincideFiltro(ana, "juan"));

        assertTrue(controller.coincideFiltro(ana, "lopez"));
        assertFalse(controller.coincideFiltro(juan, "lopez"));
    }

    // DA-03: filtrado por número telefónico coincide correctamente
    @Test
    void testFiltroPorTelefono() {
        Cliente juan = new Cliente(
                1,
                "Juan Perez",
                "juan@mail.com",
                "555-1234",
                "Calle 1",
                150.0,
                "Herramientas"
        );

        Cliente ana = new Cliente(
                2,
                "Ana Lopez",
                "ana@mail.com",
                "555-5678",
                "Av. 2",
                100.0,
                "Materiales"
        );

        assertTrue(controller.coincideFiltro(juan, "555-1234"));
        assertTrue(controller.coincideFiltro(juan, "1234"));
        assertFalse(controller.coincideFiltro(ana, "1234"));

        assertTrue(controller.coincideFiltro(ana, "5678"));
    }

    // DA-03: búsqueda que no coincide con ningún cliente retorna false
    @Test
    void testFiltroSinCoincidencias() {
        Cliente juan = new Cliente(
                1,
                "Juan Perez",
                "juan@mail.com",
                "555-1234",
                "Calle 1",
                150.0,
                "Herramientas"
        );

        assertFalse(
                controller.coincideFiltro(
                        juan,
                        "texto_que_no_existe_xyz"
                )
        );
    }
}