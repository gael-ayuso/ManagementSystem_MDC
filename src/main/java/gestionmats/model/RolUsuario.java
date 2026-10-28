package gestionmats.model;

/**
 * Representa los roles de usuario del sistema (GERENTE, VENDEDOR, ALMACENISTA).
 * 
 * REFACTOR (ID 18 / DG-03): Replace Conditional with Polymorphism.
 * Anteriormente, LoginController.handleLogin() utilizaba un 'switch (rol)' con código
 * hardcodeado para determinar la ruta FXML y el título de la ventana de cada rol.
 * 
 * Con este refactor, cada constante del enum es responsable de conocer su propia vista
 * y título de ventana, permitiendo invocar 'cargarVistaUsuario(rol.getVista(), rol.getTitulo())'
 * sin condicionales y facilitando la adición de futuros roles (Open/Closed Principle).
 */
public enum RolUsuario {
    GERENTE("/views/gerente/GerenteView.fxml", "SDG MDC - Panel de Gerente"),
    VENDEDOR("/views/VendedorView.fxml", "SDG MDC - Punto de Venta"),
    ALMACENISTA("/views/almacenista/AlmacenistaView.fxml", "SDG MDC - Control de Almacén");

    private final String vista;
    private final String titulo;

    RolUsuario(String vista, String titulo) {
        this.vista = vista;
        this.titulo = titulo;
    }

    /**
     * Obtiene la ruta del archivo FXML correspondiente a la interfaz principal del rol.
     * @return ruta del recurso FXML en el classpath
     */
    public String getVista() {
        return vista;
    }

    /**
     * Obtiene el título de la ventana asignado a la interfaz de este rol.
     * @return título de la ventana
     */
    public String getTitulo() {
        return titulo;
    }
}