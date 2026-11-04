package gestionmats.dao;

import gestionmats.model.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdenCompraDaoCsv extends AbstractDaoCsv<OrdenCompra> {

    private ProveedorDaoCsv proveedorDao = new ProveedorDaoCsv();
    // REFACTOR [ID 02 / CRIS-02 - Move Method]: Se reemplaza el acceso directo a DetallesOrden.csv y ProductoDaoCsv por DetalleOrdenDaoCsv
    private DetalleOrdenDaoCsv detalleOrdenDao = new DetalleOrdenDaoCsv();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OrdenCompraDaoCsv() {
        super("dataBase/OrdenesCompra.csv");
    }

    @Override
    protected OrdenCompra mapearDeCsv(String linea) {
        String[] datos = linea.split(",");
        try {
            int idOrden = Integer.parseInt(datos[0]);
            Date fecha = sdf.parse(datos[1]);
            int idProveedor = Integer.parseInt(datos[2]);
            String estado = datos[3];

            // Buscamos al proveedor en su propio DAO
            Proveedor prov = proveedorDao.buscarPorId(idProveedor);

            OrdenCompra orden = new OrdenCompra(idOrden, prov);
            orden.setFechaEmision(fecha);
            orden.setEstado(estado);

            // Cargamos la lista de materiales que pertenecen a esta orden
            cargarDetalles(orden);

            return orden;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String mapearACsv(OrdenCompra orden) {
        return orden.getIdOrden() + "," + sdf.format(orden.getFechaEmision()) + "," +
                orden.getProveedor().getIdProveedor() + "," + orden.getEstado();
    }

    @Override
    protected String obtenerId(OrdenCompra orden) {
        return String.valueOf(orden.getIdOrden());
    }

    @Override
    public boolean guardar(OrdenCompra orden) {
        // 1. Guardamos la "cabecera" de la orden
        boolean guardado = super.guardar(orden);
        // 2. Si tuvo éxito, guardamos sus detalles
        if (guardado) {
            guardarDetalles(orden);
        }
        return guardado;
    }

    /**
     * REFACTOR [ID 02 / CRIS-02 - Move Method]:
     * Se delega la lectura y mapeo de los detalles a DetalleOrdenDaoCsv.buscarPorIdOrden().
     */
    private void cargarDetalles(OrdenCompra orden) {
        for (DetalleOrden det : detalleOrdenDao.buscarPorIdOrden(orden.getIdOrden())) {
            orden.addDetalleOrden(det);
        }
    }

    /**
     * REFACTOR [ID 02 / CRIS-02 - Move Method]:
     * Se delega la persistencia de cada detalle a DetalleOrdenDaoCsv.guardar().
     */
    private void guardarDetalles(OrdenCompra orden) {
        for (DetalleOrden det : orden.getDetalles()) {
            detalleOrdenDao.guardar(orden.getIdOrden(), det);
        }
    }

    public int generarSiguienteId() {
        int maxId = 0;
        for (OrdenCompra o : this.listarTodos()) {
            if (o.getIdOrden() > maxId) maxId = o.getIdOrden();
        }
        return maxId + 1;
    }
}