package controllers;

import gestionmats.model.DetalleVenta;
import gestionmats.model.Producto;
import gestionmats.model.Venta;
import gestionmats.services.ReporteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestReporteCalculo {

    private ReporteService reporteService;

    private Producto cemento;
    private Producto cal;
    private Producto pintura;

    private List<Venta> ventasPrueba;
    private Map<Integer, List<DetalleVenta>> detallesPorVenta;
    private Map<String, Producto> productosPorId;

    @BeforeEach
    void setUp() {

        reporteService =
                new ReporteService();

        cemento = new Producto(
                "M001",
                "Cemento Tolteca",
                "Tolteca",
                "Construcción",
                200.0,
                100,
                50,
                "Bulto",
                0.0
        );

        cal = new Producto(
                "M002",
                "Cal Hidratada",
                "Tolteca",
                "Construcción",
                100.0,
                100,
                50,
                "Bulto",
                0.0
        );

        pintura = new Producto(
                "M003",
                "Pintura Vinílica",
                "Comex",
                "Pinturas",
                500.0,
                50,
                20,
                "Cubeta",
                0.0
        );

        productosPorId =
                new HashMap<>();

        productosPorId.put(
                "M001",
                cemento
        );

        productosPorId.put(
                "M002",
                cal
        );

        productosPorId.put(
                "M003",
                pintura
        );

        ventasPrueba =
                new ArrayList<>();

        detallesPorVenta =
                new HashMap<>();


        // Venta 1
        Venta v1 = new Venta(
                1,
                1,
                "INSTANTANEA",
                "EFECTIVO",
                900.0,
                0.0,
                900.0
        );

        v1.setIdVenta(1);

        v1.setFechaHora(
                "2026-05-10 10:00:00"
        );

        v1.setEstado(
                "COMPLETADA"
        );

        ventasPrueba.add(v1);

        List<DetalleVenta> d1 =
                List.of(

                        new DetalleVenta(
                                1,
                                1,
                                "M001",
                                "Cemento Tolteca",
                                2,
                                200.0,
                                0.0,
                                400.0,
                                400.0
                        ),

                        new DetalleVenta(
                                2,
                                1,
                                "M003",
                                "Pintura Vinílica",
                                1,
                                500.0,
                                0.0,
                                500.0,
                                500.0
                        )
                );

        detallesPorVenta.put(
                1,
                d1
        );


        // Venta 2
        Venta v2 = new Venta(
                2,
                1,
                "INSTANTANEA",
                "EFECTIVO",
                800.0,
                0.0,
                800.0
        );

        v2.setIdVenta(2);

        v2.setFechaHora(
                "2026-05-15 14:00:00"
        );

        v2.setEstado(
                "COMPLETADA"
        );

        ventasPrueba.add(v2);

        List<DetalleVenta> d2 =
                List.of(

                        new DetalleVenta(
                                3,
                                2,
                                "M001",
                                "Cemento Tolteca",
                                3,
                                200.0,
                                0.0,
                                600.0,
                                600.0
                        ),

                        new DetalleVenta(
                                4,
                                2,
                                "M002",
                                "Cal Hidratada",
                                2,
                                100.0,
                                0.0,
                                200.0,
                                200.0
                        )
                );

        detallesPorVenta.put(
                2,
                d2
        );


        // Venta 3 CANCELADA
        Venta v3 = new Venta(
                3,
                1,
                "INSTANTANEA",
                "EFECTIVO",
                1000.0,
                0.0,
                1000.0
        );

        v3.setIdVenta(3);

        v3.setFechaHora(
                "2026-05-12 12:00:00"
        );

        v3.setEstado(
                "CANCELADA"
        );

        ventasPrueba.add(v3);

        List<DetalleVenta> d3 =
                List.of(

                        new DetalleVenta(
                                5,
                                3,
                                "M003",
                                "Pintura Vinílica",
                                2,
                                500.0,
                                0.0,
                                1000.0,
                                1000.0
                        )
                );

        detallesPorVenta.put(
                3,
                d3
        );


        // Venta 4 fuera de rango
        Venta v4 = new Venta(
                4,
                1,
                "INSTANTANEA",
                "EFECTIVO",
                400.0,
                0.0,
                400.0
        );

        v4.setIdVenta(4);

        v4.setFechaHora(
                "2026-06-01 10:00:00"
        );

        v4.setEstado(
                "COMPLETADA"
        );

        ventasPrueba.add(v4);

        List<DetalleVenta> d4 =
                List.of(

                        new DetalleVenta(
                                6,
                                4,
                                "M001",
                                "Cemento Tolteca",
                                2,
                                200.0,
                                0.0,
                                400.0,
                                400.0
                        )
                );

        detallesPorVenta.put(
                4,
                d4
        );
    }

    @Test
    void testFiltradoPorRangoFechas() {

        LocalDate inicio =
                LocalDate.of(
                        2026,
                        5,
                        1
                );

        LocalDate fin =
                LocalDate.of(
                        2026,
                        5,
                        31
                );

        reporteService.procesarReporte(
                inicio,
                fin,
                "Todas",
                ventasPrueba,
                detallesPorVenta,
                productosPorId
        );

        assertEquals(
                2,
                reporteService
                        .getVentasFiltradas()
                        .size()
        );

        assertEquals(
                1700.0,
                reporteService
                        .getUtilidadTotal(),
                0.001
        );
    }

    @Test
    void testExclusionVentasCanceladas() {

        LocalDate inicio =
                LocalDate.of(
                        2026,
                        5,
                        12
                );

        LocalDate fin =
                LocalDate.of(
                        2026,
                        5,
                        12
                );

        reporteService.procesarReporte(
                inicio,
                fin,
                "Todas",
                ventasPrueba,
                detallesPorVenta,
                productosPorId
        );

        assertEquals(
                0,
                reporteService
                        .getVentasFiltradas()
                        .size()
        );

        assertEquals(
                0.0,
                reporteService
                        .getUtilidadTotal(),
                0.001
        );

        assertEquals(
                "--",
                reporteService
                        .getProductoMasVendido()
        );

        assertEquals(
                "--",
                reporteService
                        .getMejorCategoria()
        );
    }

    @Test
    void testFiltradoPorCategoria() {

        LocalDate inicio =
                LocalDate.of(
                        2026,
                        5,
                        1
                );

        LocalDate fin =
                LocalDate.of(
                        2026,
                        5,
                        31
                );

        reporteService.procesarReporte(
                inicio,
                fin,
                "Pinturas",
                ventasPrueba,
                detallesPorVenta,
                productosPorId
        );

        assertEquals(
                500.0,
                reporteService
                        .getUtilidadTotal(),
                0.001
        );

        assertEquals(
                "Pintura Vinílica",
                reporteService
                        .getProductoMasVendido()
        );

        assertEquals(
                "Pinturas",
                reporteService
                        .getMejorCategoria()
        );
    }

    @Test
    void testCalculoProductoMasVendidoYMejorCategoria() {

        LocalDate inicio =
                LocalDate.of(
                        2026,
                        5,
                        1
                );

        LocalDate fin =
                LocalDate.of(
                        2026,
                        5,
                        31
                );

        reporteService.procesarReporte(
                inicio,
                fin,
                "Todas",
                ventasPrueba,
                detallesPorVenta,
                productosPorId
        );

        assertEquals(
                "Cemento Tolteca",
                reporteService
                        .getProductoMasVendido()
        );

        assertEquals(
                "Construcción",
                reporteService
                        .getMejorCategoria()
        );
    }

    @Test
    void testSinDatosRetornaValoresPorDefecto() {

        LocalDate inicio =
                LocalDate.of(
                        2025,
                        1,
                        1
                );

        LocalDate fin =
                LocalDate.of(
                        2025,
                        1,
                        31
                );

        reporteService.procesarReporte(
                inicio,
                fin,
                "Todas",
                ventasPrueba,
                detallesPorVenta,
                productosPorId
        );

        assertEquals(
                0.0,
                reporteService
                        .getUtilidadTotal(),
                0.001
        );

        assertEquals(
                "--",
                reporteService
                        .getProductoMasVendido()
        );

        assertEquals(
                "--",
                reporteService
                        .getMejorCategoria()
        );

        assertTrue(
                reporteService
                        .getVentasFiltradas()
                        .isEmpty()
        );
    }
}