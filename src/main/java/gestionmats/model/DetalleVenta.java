package gestionmats.model;

public class DetalleVenta {
    private int idDetalle;
    private int idVenta;
    private String idProducto;
    private String nombreProducto;  // Guardamos el nombre por si el producto cambia después
    private int cantidad;
    private double precioUnitario;   // Precio al momento de la venta
    private double descuentoAplicado; // Descuento específico para este producto (0 si no aplica)
    private double subtotal;
    private double total;

    public DetalleVenta(int idDetalle, int idVenta, String idProducto, String nombreProducto, int cantidad, double precioUnitario, double descuentoAplicado, double subtotal, double total) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoAplicado = descuentoAplicado;
        this.subtotal = subtotal;
        this.total = total;
    }

    // Constructor para crear un detalle nuevo (sin ID aún)
    public DetalleVenta(int idVenta, String idProducto, String nombreProducto,
                        int cantidad, double precioUnitario, double descuentoAplicado) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoAplicado = descuentoAplicado;
        this.subtotal = precioUnitario * cantidad;
        this.total = subtotal * (1 - descuentoAplicado / 100);
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getDescuentoAplicado() { return descuentoAplicado; }
    public void setDescuentoAplicado(double descuentoAplicado) { this.descuentoAplicado = descuentoAplicado; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    // REFACTOR (ID 20 / DG-05): Move Method
    // Incrementa la cantidad del renglón y recalcula subtotal y total con la misma fórmula
    // del constructor. Antes este cálculo lo hacía VendedorController.handleAgregarAlCarrito()
    // manipulando desde fuera los setters de cantidad, subtotal y total.
    public void agregarCantidad(int extra) {
        this.cantidad += extra;
        this.subtotal = precioUnitario * cantidad;
        this.total = subtotal * (1 - descuentoAplicado / 100);
    }

    @Override
    public String toString() {
        return "DetalleVenta{" +
                "idProducto='" + idProducto + '\'' +
                ", nombre='" + nombreProducto + '\'' +
                ", cantidad=" + cantidad +
                ", total=" + total +
                '}';
    }
}