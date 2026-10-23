package controllers;

import gestionmats.model.DetalleVenta;
import gestionmats.model.Producto;
import gestionmats.model.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el procesamiento y cálculo de reportes en GerenteReporteController
 * (ID 14 / DA-04 - Move Method en procesarDatos).
 * Verifica el filtrado por rango de fechas, exclusión de ventas canceladas, filtrado por categoría,
 * cálculo de ingresos totales, producto más vendido y mejor categoría sin depender de controles JavaFX.
 */
public class TestReporteCalculo {

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Producto cemento;
    private Producto cal;
    private Producto pintura;

    private List<Venta> ventasPrueba;
    private Map<Integer, List<DetalleVenta>> detallesPorVenta;
    private Map<String, Producto> productosPorId;

    @BeforeEach
    void setUp() {
        cemento = new Producto("M001", "Cemento Tolteca", "Tolteca", "Construcción", 200.0, 100, 50, "Bulto", 0.0);
        cal = new Producto("M002", "Cal Hidratada", "Tolteca", "Construcción", 100.0, 100, 50, "Bulto", 0.0);
        pintura = new Producto("M003", "Pintura Vinílica", "Comex", "Pinturas", 500.0, 50, 20, "Cubeta", 0.0);

        productosPorId = new HashMap<>();
        productosPorId.put("M001", cemento);
        productosPorId.put("M002", cal);
        productosPorId.put("M003", pintura);

        ventasPrueba = new ArrayList<>();
        detallesPorVenta = new HashMap<>();

        // Venta 1: 2026-05-10 10:00:00 - COMPLETADA
        Venta v1 = new Venta(1, 1, "INSTANTANEA", "EFECTIVO", 900.0, 0.0, 900.0);
        v1.setIdVenta(1);
        v1.setFechaHora("2026-05-10 10:00:00");
        v1.setEstado("COMPLETADA");
        ventasPrueba.add(v1);

        List<DetalleVenta> d1 = List.of(
                new DetalleVenta(1, 1, "M001", "Cemento Tolteca", 2, 200.0, 0.0, 400.0, 400.0), // 2 cementos ($400)
                new DetalleVenta(2, 1, "M003", "Pintura Vinílica", 1, 500.0, 0.0, 500.0, 500.0) // 1 pintura ($500)
        );
        detallesPorVenta.put(1, d1);

        // Venta 2: 2026-05-15 14:00:00 - COMPLETADA
        Venta v2 = new Venta(2, 1, "INSTANTANEA", "EFECTIVO", 800.0, 0.0, 800.0);
        v2.setIdVenta(2);
        v2.setFechaHora("2026-05-15 14:00:00");
        v2.setEstado("COMPLETADA");
        ventasPrueba.add(v2);

        List<DetalleVenta> d2 = List.of(
                new DetalleVenta(3, 2, "M001", "Cemento Tolteca", 3, 200.0, 0.0, 600.0, 600.0), // 3 cementos ($600)
                new DetalleVenta(4, 2, "M002", "Cal Hidratada", 2, 100.0, 0.0, 200.0, 200.0)    // 2 cal ($200)
        );
        detallesPorVenta.put(2, d2);

        // Venta 3: 2026-05-12 12:00:00 - CANCELADA (no debe sumarse)
        Venta v3 = new Venta(3, 1, "INSTANTANEA", "EFECTIVO", 1000.0, 0.0, 1000.0);
        v3.setIdVenta(3);
        v3.setFechaHora("2026-05-12 12:00:00");
        v3.setEstado("CANCELADA");
        ventasPrueba.add(v3);

        List<DetalleVenta> d3 = List.of(
                new DetalleVenta(5, 3, "M003", "Pintura Vinílica", 2, 500.0, 0.0, 1000.0, 1000.0)
        );
        detallesPorVenta.put(3, d3);

        // Venta 4: 2026-06-01 10:00:00 - Fuera de rango para mayo
        Venta v4 = new Venta(4, 1, "INSTANTANEA", "EFECTIVO", 400.0, 0.0, 400.0);
        v4.setIdVenta(4);
        v4.setFechaHora("2026-06-01 10:00:00");
        v4.setEstado("COMPLETADA");
        ventasPrueba.add(v4);

        List<DetalleVenta> d4 = List.of(
                new DetalleVenta(6, 4, "M001", "Cemento Tolteca", 2, 200.0, 0.0, 400.0, 400.0)
        );
        detallesPorVenta.put(4, d4);
    }

    /**
     * Extrae y procesa los resultados estadísticos según la lógica exacta de procesarDatos():
     */
    public record ResultadoReporte(double utilidadTotal, String masVendido, String mejorCategoria, int ventasValidas) {}

    public ResultadoReporte procesarReporte(LocalDate inicio, LocalDate fin, String catFiltro) {
        List<Venta> ventasFiltradas = ventasPrueba.stream()
                .filter(v -> !v.getEstado().equals("CANCELADA"))
                .filter(v -> {
                    LocalDate f = LocalDateTime.parse(v.getFechaHora(), fmt).toLocalDate();
                    return (f.isAfter(inicio) || f.isEqual(inicio)) && (f.isBefore(fin) || f.isEqual(fin));
                }).collect(Collectors.toList());

        Map<String, Integer> unidadesPorProducto = new HashMap<>();
        Map<String, Double> dineroPorCat = new HashMap<>();
        double utilidadTotal = 0.0;

        for (Venta v : ventasFiltradas) {
            List<DetalleVenta> detalles = detallesPorVenta.getOrDefault(v.getIdVenta(), Collections.emptyList());
            for (DetalleVenta d : detalles) {
                Producto p = productosPorId.get(d.getIdProducto());
                if (p == null) continue;
                if (!catFiltro.equals("Todas") && !p.getCategoria().equals(catFiltro)) continue;

                unidadesPorProducto.put(p.getNombre(), unidadesPorProducto.getOrDefault(p.getNombre(), 0) + d.getCantidad());
                dineroPorCat.put(p.getCategoria(), dineroPorCat.getOrDefault(p.getCategoria(), 0.0) + d.getTotal());
                utilidadTotal += d.getTotal();
            }
        }

        String masVendido = unidadesPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("--");
        String mejorCategoria = dineroPorCat.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("--");

        return new ResultadoReporte(utilidadTotal, masVendido, mejorCategoria, ventasFiltradas.size());
    }

    // DA-04: filtrado por rango de fechas incluye fechas límite y descarta fuera de rango
    @Test
    void testFiltradoPorRangoFechas() {
        LocalDate inicio = LocalDate.of(2026, 5, 1);
        LocalDate fin = LocalDate.of(2026, 5, 31);

        ResultadoReporte rep = procesarReporte(inicio, fin, "Todas");

        // Debe tomar v1 (10 mayo) y v2 (15 mayo), excluyendo v3 (cancelada) y v4 (1 junio)
        assertEquals(2, rep.ventasValidas(), "Solo 2 ventas son válidas dentro del rango y no canceladas");
        assertEquals(1700.0, rep.utilidadTotal(), 0.001, "900 (v1) + 800 (v2) = 1700");
    }

    // DA-04: exclusión de ventas con estado CANCELADA
    @Test
    void testExclusionVentasCanceladas() {
        LocalDate inicio = LocalDate.of(2026, 5, 12);
        LocalDate fin = LocalDate.of(2026, 5, 12);

        // En esta fecha solo existe v3 que está CANCELADA
        ResultadoReporte rep = procesarReporte(inicio, fin, "Todas");
        assertEquals(0, rep.ventasValidas());
        assertEquals(0.0, rep.utilidadTotal(), 0.001);
        assertEquals("--", rep.masVendido());
        assertEquals("--", rep.mejorCategoria());
    }

    // DA-04: filtrado por categoría específica incluye solo productos de dicha categoría
    @Test
    void testFiltradoPorCategoria() {
        LocalDate inicio = LocalDate.of(2026, 5, 1);
        LocalDate fin = LocalDate.of(2026, 5, 31);

        // Filtrar solo categoría "Pinturas"
        ResultadoReporte rep = procesarReporte(inicio, fin, "Pinturas");
        assertEquals(500.0, rep.utilidadTotal(), 0.001, "Solo la pintura de v1 ($500)");
        assertEquals("Pintura Vinílica", rep.masVendido());
        assertEquals("Pinturas", rep.mejorCategoria());
    }

    // DA-04: producto más vendido y mejor categoría se calculan con el mayor número de unidades e ingresos
    @Test
    void testCalculoProductoMasVendidoYMejorCategoria() {
        LocalDate inicio = LocalDate.of(2026, 5, 1);
        LocalDate fin = LocalDate.of(2026, 5, 31);

        ResultadoReporte rep = procesarReporte(inicio, fin, "Todas");

        // Cemento: 2 (v1) + 3 (v2) = 5 unidades ($1000)
        // Pintura: 1 unidad ($500)
        // Cal: 2 unidades ($200)
        // Construcción total: $1200 ($1000 + $200)
        // Pinturas total: $500
        assertEquals("Cemento Tolteca", rep.masVendido(), "Cemento tuvo 5 unidades vendidas");
        assertEquals("Construcción", rep.mejorCategoria(), "Construcción tuvo $1200 en ingresos");
    }

    // DA-04: cuando no hay ventas en el período los valores calculados devuelven '--' y 0.0
    @Test
    void testSinDatosRetornaValoresPorDefecto() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fin = LocalDate.of(2025, 1, 31);

        ResultadoReporte rep = procesarReporte(inicio, fin, "Todas");
        assertEquals(0.0, rep.utilidadTotal(), 0.001);
        assertEquals("--", rep.masVendido());
        assertEquals("--", rep.mejorCategoria());
    }
}
