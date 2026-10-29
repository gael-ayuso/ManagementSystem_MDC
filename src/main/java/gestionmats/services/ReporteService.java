package gestionmats.services;

import gestionmats.dao.DetalleVentaDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.dao.VentaDaoCsv;
import gestionmats.model.DetalleVenta;
import gestionmats.model.Producto;
import gestionmats.model.Venta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReporteService {

    private final VentaDaoCsv ventaDao = new VentaDaoCsv();
    private final DetalleVentaDaoCsv detalleDao = new DetalleVentaDaoCsv();
    private final ProductoDaoCsv productoDao = new ProductoDaoCsv();

    private final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<Venta> ventasFiltradas;
    private Map<String, Integer> unidadesPorProducto;
    private Map<String, Double> dineroPorCategoria;
    private Map<String, Double> dineroPorProducto;
    private double utilidadTotal;

    // Método usado por el sistema real
    // Obtiene las ventas, detalles y productos directamente desde los DAO
    // y después envía esos datos al método que realiza el procesamiento
    public void procesarReporte(
            LocalDate inicio,
            LocalDate fin,
            String categoriaFiltro) {

        List<Venta> ventas = ventaDao.listarTodos();

        Map<Integer, List<DetalleVenta>> detallesPorVenta =
                new HashMap<>();

        Map<String, Producto> productosPorId =
                new HashMap<>();

        for (Venta venta : ventas) {

            List<DetalleVenta> detalles =
                    detalleDao.listarPorIdVenta(
                            venta.getIdVenta()
                    );

            detallesPorVenta.put(
                    venta.getIdVenta(),
                    detalles
            );

            for (DetalleVenta detalle : detalles) {

                Producto producto =
                        productoDao.buscarPorId(
                                detalle.getIdProducto()
                        );

                if (producto != null) {
                    productosPorId.put(
                            producto.getIdProducto(),
                            producto
                    );
                }
            }
        }

        procesarReporte(
                inicio,
                fin,
                categoriaFiltro,
                ventas,
                detallesPorVenta,
                productosPorId
        );
    }

    // Método que contiene la lógica real del procesamiento del reporte.
    // Recibe los datos directamente para poder reutilizar la misma lógica
    // tanto en el sistema como en las pruebas unitarias.
    public void procesarReporte(
            LocalDate inicio,
            LocalDate fin,
            String categoriaFiltro,
            List<Venta> ventas,
            Map<Integer, List<DetalleVenta>> detallesPorVenta,
            Map<String, Producto> productosPorId) {

        ventasFiltradas = ventas.stream()
                .filter(v -> !v.getEstado().equals("CANCELADA"))
                .filter(v -> {

                    LocalDate fecha =
                            LocalDateTime.parse(
                                    v.getFechaHora(),
                                    fmt
                            ).toLocalDate();

                    return (fecha.isAfter(inicio)
                            || fecha.isEqual(inicio))
                            && (fecha.isBefore(fin)
                            || fecha.isEqual(fin));
                })
                .collect(Collectors.toList());

        unidadesPorProducto = new HashMap<>();
        dineroPorCategoria = new HashMap<>();
        dineroPorProducto = new HashMap<>();
        utilidadTotal = 0.0;

        for (Venta venta : ventasFiltradas) {

            List<DetalleVenta> detalles =
                    detallesPorVenta.getOrDefault(
                            venta.getIdVenta(),
                            Collections.emptyList()
                    );

            for (DetalleVenta detalle : detalles) {

                Producto producto =
                        productosPorId.get(
                                detalle.getIdProducto()
                        );

                if (producto == null) {
                    continue;
                }

                if (!categoriaFiltro.equals("Todas")
                        && !producto.getCategoria()
                        .equals(categoriaFiltro)) {
                    continue;
                }

                unidadesPorProducto.put(
                        producto.getNombre(),
                        unidadesPorProducto.getOrDefault(
                                producto.getNombre(),
                                0
                        ) + detalle.getCantidad()
                );

                dineroPorCategoria.put(
                        producto.getCategoria(),
                        dineroPorCategoria.getOrDefault(
                                producto.getCategoria(),
                                0.0
                        ) + detalle.getTotal()
                );

                dineroPorProducto.put(
                        producto.getNombre(),
                        dineroPorProducto.getOrDefault(
                                producto.getNombre(),
                                0.0
                        ) + detalle.getTotal()
                );

                utilidadTotal += detalle.getTotal();
            }
        }
    }

    public List<Venta> getVentasFiltradas() {
        return ventasFiltradas;
    }

    public Map<String, Integer> getUnidadesPorProducto() {
        return unidadesPorProducto;
    }

    public Map<String, Double> getDineroPorCategoria() {
        return dineroPorCategoria;
    }

    public Map<String, Double> getDineroPorProducto() {
        return dineroPorProducto;
    }

    public double getUtilidadTotal() {
        return utilidadTotal;
    }

    public String getProductoMasVendido() {
        return unidadesPorProducto
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("--");
    }

    public String getMejorCategoria() {
        return dineroPorCategoria
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("--");
    }
}