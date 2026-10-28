package gestionmats.dao;

import gestionmats.model.DetalleOrden;
import gestionmats.model.Producto;
import gestionmats.utils.CsvUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetalleOrdenDaoCsv {

    // REFACTOR [ID 04 / CRIS-04 - Replace Magic Number with Symbolic Constant]:
    // Constantes simbólicas para los índices de columna y longitud mínima en DetallesOrden.csv
    private static final int COL_ID_ORDEN = 0;
    private static final int COL_ID_PRODUCTO = 1;
    private static final int COL_CANT_ESPERADA = 2;
    private static final int COL_CANT_RECIBIDA = 3;
    private static final int MIN_COLUMNAS = 4;

    private final String ruta = "dataBase/DetallesOrden.csv";
    private ProductoDaoCsv productoDao = new ProductoDaoCsv();

    /**
     * Method definido explícitamente en el UML.
     * Recupera la lista de productos esperados para una orden en específico.
     */
    public List<DetalleOrden> buscarPorIdOrden(int idOrden) {
        List<DetalleOrden> detalles = new ArrayList<>();
        List<String> lineas = CsvUtils.leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (i == 0 && linea.toLowerCase().startsWith("id")) continue;
            if (linea == null || linea.trim().isEmpty()) continue;

            String[] datos = linea.split(",", -1);
            try {
                // REFACTOR [ID 04 / CRIS-04 - Replace Magic Number with Symbolic Constant]
                int idOrdenCsv = Integer.parseInt(datos[COL_ID_ORDEN].trim());

                // Filtramos solo los que pertenecen a la orden que estamos buscando
                if (idOrdenCsv == idOrden) {
                    String idProd = datos[COL_ID_PRODUCTO].trim();
                    int esperada = Integer.parseInt(datos[COL_CANT_ESPERADA].trim());
                    int recibida = Integer.parseInt(datos[COL_CANT_RECIBIDA].trim());

                    Producto p = productoDao.buscarPorId(idProd);
                    if (p != null) {
                        detalles.add(new DetalleOrden(p, esperada, recibida));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al parsear detalle de orden: " + linea);
            }
        }
        return detalles;
    }

    /**
     * Guarda un nuevo detalle de orden en el CSV.
     */
    public boolean guardar(int idOrden, DetalleOrden d) {
        String linea = idOrden + "," +
                d.getProducto().getIdProducto() + "," +
                d.getCantidadEsperada() + "," +
                d.getCantidadRecibida();

        // Llamamos al method void de csvutils
        CsvUtils.agregarLinea(ruta, linea.split(",", -1));

        return true; // Retornamos true indicando que se ejecutó
    }

    /**
     * Méethod VITAL para el Almacenista.
     * Actualiza la "cantidadRecibida" de un producto específico en la orden.
     * REFACTOR [ID 03 / CRIS-03 - Remove Control Flag]:
     * Se elimina la bandera booleana 'modificado' y el 'break', retornando directamente
     * el resultado de guardar el archivo en cuanto se encuentra y actualiza la fila buscada.
     * REFACTOR [ID 04 / CRIS-04 - Replace Magic Number with Symbolic Constant]:
     * Se utilizan constantes simbólicas para el mínimo de columnas y los índices del CSV.
     */
    public boolean actualizarRecepcion(int idOrden, String idProducto, int nuevaCantidadRecibida) {
        List<String> lineas = CsvUtils.leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String[] datos = lineas.get(i).split(",", -1);
            if (datos.length >= MIN_COLUMNAS) {
                // Buscamos la fila que coincida con la Orden Y el Producto
                if (datos[COL_ID_ORDEN].trim().equals(String.valueOf(idOrden)) &&
                        datos[COL_ID_PRODUCTO].trim().equals(idProducto)) {

                    datos[COL_CANT_RECIBIDA] = String.valueOf(nuevaCantidadRecibida);
                    lineas.set(i, String.join(",", datos));
                    return guardarLineasEnArchivo(lineas);
                }
            }
        }

        return false;
    }

    private boolean guardarLineasEnArchivo(List<String> lineas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al actualizar DetallesOrden.csv");
            return false;
        }
    }
}