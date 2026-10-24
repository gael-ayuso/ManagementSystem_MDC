package gestionmats.model;

public class Cliente {

    // ── Atributos ──────────────────────────────────────────
    private int    idCliente;
    private String nombre;
    private String correoElectronico;
    private String numeroTelefonico;
    private String direccion;
    private double puntosLealtad;
    private String preferencias;

    // Factor de conversión: 1% de la venta se convierte en puntos
    private static final double FACTOR_ACUMULACION = 0.01;
    // Cada punto equivale a $0.10 de descuento POR AHORA muy seguramente luego valdrá 1
    private static final double VALOR_PUNTO        = 0.10;

    // ── Constructores ──────────────────────────────────────
    public Cliente() {}

    public Cliente(int idCliente, String nombre, String correoElectronico,
                   String numeroTelefonico, String direccion,
                   double puntosLealtad, String preferencias) {
        this.idCliente         = idCliente;
        this.nombre            = nombre;
        this.correoElectronico = correoElectronico;
        this.numeroTelefonico  = numeroTelefonico;
        this.direccion         = direccion;
        this.puntosLealtad     = puntosLealtad;
        this.preferencias      = preferencias;
    }

    // ── Métodos de negocio ─────────────────────────────────

    /**
     * Acumula puntos de lealtad a partir del monto de una venta.
     * Por cada peso vendido se acredita FACTOR_ACUMULACION puntos.
     *
     * @param montoVenta monto total de la venta en pesos MXN
     */
    public void acumularPuntos(double montoVenta) {
        if (montoVenta > 0) {
            this.puntosLealtad += montoVenta * FACTOR_ACUMULACION;
        }
    }

    /**
     * Convierte los puntos actuales del cliente en un monto de descuento
     * y resetea el saldo de puntos a cero.
     *
     * @return descuento en pesos MXN equivalente a los puntos acumulados
     */
    public double aplicarPuntosComoDescuento() {
        double descuento = this.calcularDescuentoPuntos();
        this.puntosLealtad = 0.0;
        return descuento;
    }

    public double calcularDescuentoPuntos() {
        return this.getPuntosLealtad() * VALOR_PUNTO;
    }

    // ── Getters y Setters ──────────────────────────────────
    public int    getIdCliente()           { return idCliente; }
    public void   setIdCliente(int id)     { this.idCliente = id; }

    public String getNombre()              { return nombre; }
    public void   setNombre(String n)      { this.nombre = n; }

    public String getCorreoElectronico()         { return correoElectronico; }
    public void   setCorreoElectronico(String c) { this.correoElectronico = c; }

    public String getNumeroTelefonico()          { return numeroTelefonico; }
    public void   setNumeroTelefonico(String t)  { this.numeroTelefonico = t; }

    public String getDireccion()               { return direccion; }
    public void   setDireccion(String d)       { this.direccion = d; }

    public double getPuntosLealtad()           { return puntosLealtad; }
    public void   setPuntosLealtad(double p)   { this.puntosLealtad = p; }

    public String getPreferencias()            { return preferencias; }
    public void   setPreferencias(String p)    { this.preferencias = p; }

    @Override
    public String toString() {
        return "Cliente{id=" + idCliente + ", nombre='" + nombre + '\'' +
                ", puntos=" + puntosLealtad + '}';
    }
}