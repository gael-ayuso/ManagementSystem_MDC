package gestionmats.model;

import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompra {
    private int idOrden;
    private Date fechaEmision;
    private String estado;
    private Proveedor proveedor;
    private List<DetalleOrden> detalles;

    public OrdenCompra() {
        this.detalles = new ArrayList<>();
        this.estado = "EMITIDA";
        this.fechaEmision = new Date();
    }

    public OrdenCompra(int idOrden, Proveedor proveedor) {
        this();
        this.idOrden = idOrden;
        this.proveedor = proveedor;
    }

    // --- GETTERS ---
    public int getIdOrden() { return idOrden; }
    public Date getFechaEmision() { return fechaEmision; }
    public String getEstado() { return estado; }
    public Proveedor getProveedor() { return proveedor; }
    public List<DetalleOrden> getDetalles() { return Collections.unmodifiableList(this.detalles); }

    // --- SETTERS ---
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    /**
     * MÉTODO AGREGADO: Permite al DAO establecer la fecha guardada en el CSV.
     */
    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public void recibirMercancia() {
        // Lógica para el almacenista (RF-07)
    }

    public void addDetalleOrden(DetalleOrden detalle) {
        if (detalle != null) {
            this.detalles.add(detalle);
        }
    }

    public boolean removerDetalleOrden(DetalleOrden detalle) {
        if (detalle != null) {
            return this.detalles.remove(detalle);
        }
        return false;
    }
}