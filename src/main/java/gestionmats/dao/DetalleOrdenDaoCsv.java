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
                int idOrdenCsv = Integer.parseInt(datos[0].trim());

                // Filtramos solo los que pertenecen a la orden que estamos buscando
                if (idOrdenCsv == idOrden) {
                    String idProd = datos[1].trim();
                    int esperada = Integer.parseInt(datos[2].trim());
                    int recibida = Integer.parseInt(datos[3].trim());

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
     */
    public boolean actualizarRecepcion(int idOrden, String idProducto, int nuevaCantidadRecibida) {
        List<String> lineas = CsvUtils.leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String[] datos = lineas.get(i).split(",", -1);
            if (datos.length >= 4) {
                // Buscamos la fila que coincida con la Orden Y el Producto
                if (datos[0].trim().equals(String.valueOf(idOrden)) &&
                        datos[1].trim().equals(idProducto)) {

                    datos[3] = String.valueOf(nuevaCantidadRecibida);
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