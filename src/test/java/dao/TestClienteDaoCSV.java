package dao;

import gestionmats.dao.ClienteDaoCsv;
import gestionmats.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TestClienteDaoCSV {

    private ClienteDaoCsv dao;

    @BeforeEach
    void setUp() {
        dao = new ClienteDaoCsv();
    }

    // CRIS-01: listarTodos debe retornar clientes válidos y omitir cabecera
    @Test
    void testListarTodosNoVacioYOmitirCabecera() {
        List<Cliente> clientes = dao.listarTodos();
        assertNotNull(clientes, "La lista de clientes no debe ser nula");
        assertFalse(clientes.isEmpty(), "La lista de clientes no debe estar vacía");
        assertTrue(clientes.size() >= 4, "Debe contener al menos los clientes registrados en el CSV");

        for (Cliente c : clientes) {
            assertNotEquals("nombre", c.getNombre().toLowerCase(), "La cabecera no debe ser mapeada como cliente");
            assertNotNull(c.getNombre(), "El nombre no debe ser nulo");
        }
    }

    // CRIS-01: listarTodos mapea correctamente los 7 atributos de un cliente conocido
    @Test
    void testMapeoAtributosClienteRegistrado() {
        Cliente juan = dao.buscarPorId(1);
        assertNotNull(juan, "El cliente con ID 1 (Juan Perez) debe existir");
        assertEquals(1, juan.getIdCliente());
        assertEquals("Juan Perez", juan.getNombre());
        assertEquals("juan@mail.com", juan.getCorreoElectronico());
        assertEquals("555-1234", juan.getNumeroTelefonico());
        assertEquals("Calle 123", juan.getDireccion());
        assertEquals(150.0, juan.getPuntosLealtad(), 0.001);
        assertEquals("Herramientas", juan.getPreferencias());
    }

    // CRIS-01: buscarPorId retorna el cliente cuando existe y null cuando no
    @Test
    void testBuscarPorId() {
        Cliente c2 = dao.buscarPorId(2);
        assertNotNull(c2);
        assertEquals(2, c2.getIdCliente());
        assertEquals("Ana Lopez", c2.getNombre());

        Cliente noExiste = dao.buscarPorId(-9999);
        assertNull(noExiste, "Buscar un ID inexistente debe retornar null");
    }

    // CRIS-01: listarTodos asigna valores por defecto a campos vacíos (Cliente 0)
    @Test
    void testClienteConCamposVaciosAplicaValoresPorDefecto() {
        Cliente c0 = dao.buscarPorId(0);
        assertNotNull(c0, "El cliente genérico con ID 0 debe existir");
        assertEquals(0, c0.getIdCliente());
        assertEquals("Cliente Sin Registrar", c0.getNombre());
        // Columnas vacías mapeadas a cadenas vacías o 0.0 según getDato
        assertEquals("", c0.getCorreoElectronico());
        assertEquals("", c0.getNumeroTelefonico());
        assertEquals("", c0.getDireccion());
        assertEquals(0.0, c0.getPuntosLealtad(), 0.001);
        assertEquals("Venta Rápida", c0.getPreferencias());
    }

    // CRIS-01: obtenerUltimoId devuelve el mayor ID numérico de clientes
    @Test
    void testObtenerUltimoId() {
        int ultimoId = dao.obtenerUltimoId();
        assertTrue(ultimoId >= 3, "El último ID debe ser al menos 3");
    }
}
