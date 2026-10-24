package dao;

import gestionmats.dao.ClienteDaoCsv;
import gestionmats.model.Cliente;
import gestionmats.utils.CsvUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba unitaria exclusiva para la Oportunidad ID 01 (CRIS-01):
 * Extract Method en ClienteDaoCsv.listarTodos() (extracción de mapearDeCsv).
 */
public class TestClienteDaoCSV {

    private static final String RUTA_CLIENTES = "dataBase/clientes.csv";

    private ClienteDaoCsv dao;
    private List<String> respaldoClientes;

    @BeforeEach
    void setUp() {
        dao = new ClienteDaoCsv();
        respaldoClientes = CsvUtils.leerArchivo(RUTA_CLIENTES);
    }

    @AfterEach
    void tearDown() {
        if (respaldoClientes != null) {
            CsvUtils.escribirArchivo(RUTA_CLIENTES, respaldoClientes);
        }
    }

    // CRIS-01: listarTodos debe retornar clientes válidos y omitir la cabecera del CSV
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

    // CRIS-01: listarTodos mapea correctamente los 7 atributos de una línea CSV completa
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

    // CRIS-01: listarTodos asigna valores por defecto cuando una línea tiene campos vacíos
    @Test
    void testClienteConCamposVaciosAplicaValoresPorDefecto() {
        Cliente c0 = dao.buscarPorId(0);
        assertNotNull(c0, "El cliente genérico con ID 0 debe existir");
        assertEquals(0, c0.getIdCliente());
        assertEquals("Cliente Sin Registrar", c0.getNombre());
        assertEquals("", c0.getCorreoElectronico());
        assertEquals("", c0.getNumeroTelefonico());
        assertEquals("", c0.getDireccion());
        assertEquals(0.0, c0.getPuntosLealtad(), 0.001);
        assertEquals("Venta Rápida", c0.getPreferencias());
    }

    // CRIS-01: listarTodos descarta líneas corruptas (ID o puntos no numéricos) y conserva los clientes válidos
    @Test
    void testLineaCorruptaEsIgnoradaSinDetenerLectura() {
        int totalValidosAntes = dao.listarTodos().size();

        List<String> lineasConCorrupta = new ArrayList<>(respaldoClientes);
        lineasConCorrupta.add("ID_INVALIDO,Cliente Malformado,correo@mail.com,555-0000,Calle X,PUNTOS_INVALIDOS,Pref");
        CsvUtils.escribirArchivo(RUTA_CLIENTES, lineasConCorrupta);

        List<Cliente> clientesDespues = dao.listarTodos();
        assertEquals(totalValidosAntes, clientesDespues.size(),
                "La línea corrupta debe ser ignorada y retornar exactamente los mismos clientes válidos");
    }
}
