package gestionmats.model;

public class DetalleOrden {
    // Con esto sabemos que estamos pidiendo
    private Producto producto;

    private int cantidadEsperada;
    private int cantidadRecibida;

    public DetalleOrden() {}

    public DetalleOrden(Producto producto, int cantidadEsperada, int cantidadRecibida) {
        this.producto = producto;
        this.cantidadEsperada = cantidadEsperada;
        this.cantidadRecibida = cantidadRecibida;
    }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidadEsperada() { return cantidadEsperada; }
    public void setCantidadEsperada(int cantidadEsperada) { this.cantidadEsperada = cantidadEsperada; }

    public int getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(int cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    // Método de negocio (Estaba en tu UML)
    public double calcularSubtotal() {
        Producto producto = this.getProducto();
        if (producto != null) {
            // Asumimos que el proveedor nos vende a un costo, pero usaremos el precioVenta 
            // temporalmente o puedes agregar 'costoProveedor' a Producto después.
            return producto.getPrecioVenta() * this.getCantidadEsperada();
        }
        return 0.0;
    }
}