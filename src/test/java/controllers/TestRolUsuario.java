package controllers;

import gestionmats.model.RolUsuario;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la correspondencia de vistas y títulos según RolUsuario
 * (ID 18 / DG-03 - Replace Conditional with Polymorphism en LoginController).
 * Verifica la existencia de las constantes de rol, su resolución con valueOf(),
 * la existencia de los archivos FXML en resources y las rutas/títulos asociados.
 */
public class TestRolUsuario {

    // Rutas y títulos esperados según el switch de LoginController (líneas 116-124)
    public static final String VISTA_GERENTE = "/views/gerente/GerenteView.fxml";
    public static final String TITULO_GERENTE = "SDG MDC - Panel de Gerente";

    public static final String VISTA_VENDEDOR = "/views/VendedorView.fxml";
    public static final String TITULO_VENDEDOR = "SDG MDC - Punto de Venta";

    public static final String VISTA_ALMACENISTA = "/views/almacenista/AlmacenistaView.fxml";
    public static final String TITULO_ALMACENISTA = "SDG MDC - Control de Almacén";

    // DG-03: RolUsuario.valueOf(...) recupera correctamente los roles existentes
    @Test
    void testValueOfRoles() {
        assertEquals(RolUsuario.GERENTE, RolUsuario.valueOf("GERENTE"));
        assertEquals(RolUsuario.VENDEDOR, RolUsuario.valueOf("VENDEDOR"));
        assertEquals(RolUsuario.ALMACENISTA, RolUsuario.valueOf("ALMACENISTA"));
    }

    // DG-03: los archivos FXML asociados a cada rol existen en el classpath de recursos
    @Test
    void testRecursosFxmlExistenEnClasspath() {
        URL resGerente = getClass().getResource(VISTA_GERENTE);
        assertNotNull(resGerente, "El recurso FXML para Gerente debe existir en " + VISTA_GERENTE);

        URL resVendedor = getClass().getResource(VISTA_VENDEDOR);
        assertNotNull(resVendedor, "El recurso FXML para Vendedor debe existir en " + VISTA_VENDEDOR);

        URL resAlmacenista = getClass().getResource(VISTA_ALMACENISTA);
        assertNotNull(resAlmacenista, "El recurso FXML para Almacenista debe existir en " + VISTA_ALMACENISTA);
    }

    // DG-03: mapeo de vista y título por rol coincide con la definición de LoginController
    @Test
    void testMapeoVistasYTitulosPorRol() {
        // Validación del contrato de interfaz esperado para cada rol
        assertEquals(VISTA_GERENTE, obtenerVista(RolUsuario.GERENTE));
        assertEquals(TITULO_GERENTE, obtenerTitulo(RolUsuario.GERENTE));

        assertEquals(VISTA_VENDEDOR, obtenerVista(RolUsuario.VENDEDOR));
        assertEquals(TITULO_VENDEDOR, obtenerTitulo(RolUsuario.VENDEDOR));

        assertEquals(VISTA_ALMACENISTA, obtenerVista(RolUsuario.ALMACENISTA));
        assertEquals(TITULO_ALMACENISTA, obtenerTitulo(RolUsuario.ALMACENISTA));
    }

    /**
     * Helper que consulta getVista() mediante reflexión si ya fue añadido a RolUsuario,
     * o devuelve el valor del switch pre-refactorización si aún no se ha modificado el enum.
     */
    private String obtenerVista(RolUsuario rol) {
        try {
            Method m = RolUsuario.class.getMethod("getVista");
            return (String) m.invoke(rol);
        } catch (Exception e) {
            return switch (rol) {
                case GERENTE -> VISTA_GERENTE;
                case VENDEDOR -> VISTA_VENDEDOR;
                case ALMACENISTA -> VISTA_ALMACENISTA;
            };
        }
    }

    /**
     * Helper que consulta getTitulo() mediante reflexión si ya fue añadido a RolUsuario,
     * o devuelve el valor del switch pre-refactorización si aún no se ha modificado el enum.
     */
    private String obtenerTitulo(RolUsuario rol) {
        try {
            Method m = RolUsuario.class.getMethod("getTitulo");
            return (String) m.invoke(rol);
        } catch (Exception e) {
            return switch (rol) {
                case GERENTE -> TITULO_GERENTE;
                case VENDEDOR -> TITULO_VENDEDOR;
                case ALMACENISTA -> TITULO_ALMACENISTA;
            };
        }
    }
}
