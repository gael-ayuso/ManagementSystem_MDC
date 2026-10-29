package gestionmats.controllers.gerente;

import gestionmats.model.*;
import gestionmats.services.GestorSesion;
import gestionmats.services.ReporteService;
import gestionmats.utils.UIComponents;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GerenteReporteController implements Initializable {

    @FXML private DatePicker dpInicio, dpFin;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private Label lblMasVendido, lblMejorCategoria, lblUtilidadTotal;
    @FXML private LineChart<String, Number> chartTendencia;
    @FXML private PieChart chartDistribucion;
    @FXML private HBox hboxCustomDates;
    @FXML private Button btnExportar;

    private final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReporteService reporteService =
            new ReporteService();

    @FXML private Button btnHoy, btnSemana, btnMes, btnAño, btnPersonalizado;

    private List<Button> listaBotones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        listaBotones = Arrays.asList(
                btnHoy,
                btnSemana,
                btnMes,
                btnAño,
                btnPersonalizado
        );

        marcarBotonActivo(btnSemana);

        setupInitialFilters();

        UIComponents.applyButtonAdd(btnExportar);

        procesarDatos();
    }

    private void setupInitialFilters() {

        dpFin.setValue(LocalDate.now());

        dpInicio.setValue(
                LocalDate.now().minusDays(7)
        );

        cmbCategoria.setItems(
                FXCollections.observableArrayList(
                        "Todas",
                        "Cemento y concreto",
                        "Acero y metales",
                        "Madera y derivados",
                        "Herramientas"
                )
        );

        cmbCategoria
                .getSelectionModel()
                .selectFirst();

        dpInicio
                .valueProperty()
                .addListener(
                        (o, old, n) -> procesarDatos()
                );

        dpFin
                .valueProperty()
                .addListener(
                        (o, old, n) -> procesarDatos()
                );

        cmbCategoria
                .valueProperty()
                .addListener(
                        (o, old, n) -> procesarDatos()
                );
    }

    private void marcarBotonActivo(Button btn) {

        listaBotones.forEach(
                b -> b
                        .getStyleClass()
                        .remove("active-period")
        );

        btn
                .getStyleClass()
                .add("active-period");
    }

    @FXML
    private void handlePresetHoy() {

        marcarBotonActivo(btnHoy);

        ocultarCustomDates();

        dpInicio.setValue(LocalDate.now());
        dpFin.setValue(LocalDate.now());
    }

    @FXML
    private void handlePresetSemana() {

        marcarBotonActivo(btnSemana);

        ocultarCustomDates();

        dpInicio.setValue(
                LocalDate.now().minusWeeks(1)
        );

        dpFin.setValue(LocalDate.now());
    }

    @FXML
    private void handlePresetMes() {

        marcarBotonActivo(btnMes);

        ocultarCustomDates();

        dpInicio.setValue(
                LocalDate.now().withDayOfMonth(1)
        );

        dpFin.setValue(LocalDate.now());
    }

    @FXML
    private void handlePresetAño() {

        marcarBotonActivo(btnAño);

        ocultarCustomDates();

        dpInicio.setValue(
                LocalDate.now().withDayOfYear(1)
        );

        dpFin.setValue(LocalDate.now());
    }

    @FXML
    private void handleTogglePersonalizado() {

        marcarBotonActivo(btnPersonalizado);

        boolean estaVisible =
                hboxCustomDates.isVisible();

        hboxCustomDates.setVisible(
                !estaVisible
        );

        hboxCustomDates.setManaged(
                !estaVisible
        );
    }

    private void ocultarCustomDates() {

        hboxCustomDates.setVisible(false);

        hboxCustomDates.setManaged(false);
    }

    private void procesarDatos() {

        LocalDate inicio =
                dpInicio.getValue();

        LocalDate fin =
                dpFin.getValue();

        String catFiltro =
                cmbCategoria.getValue();

        // LLAMADA AL MODELO
        Usuario usuario =
                GestorSesion
                        .getInstancia()
                        .getUsuarioActual();

        if (usuario instanceof Gerente) {

            ((Gerente) usuario)
                    .obtenerReportes(
                            "Filtro: "
                                    + catFiltro
                                    + " ("
                                    + inicio
                                    + " a "
                                    + fin
                                    + ")"
                    );
        }

        // PROCESAMIENTO EN EL SERVICIO
        reporteService.procesarReporte(
                inicio,
                fin,
                catFiltro
        );

        List<Venta> ventasFiltradas =
                reporteService
                        .getVentasFiltradas();

        Map<String, Integer> unidadesPorProducto =
                reporteService
                        .getUnidadesPorProducto();

        Map<String, Double> dineroPorProducto =
                reporteService
                        .getDineroPorProducto();

        double utilidadTotal =
                reporteService
                        .getUtilidadTotal();

        // ACTUALIZACIÓN DE INTERFAZ
        lblUtilidadTotal.setText(
                String.format(
                        "$%,.2f",
                        utilidadTotal
                )
        );

        lblMasVendido.setText(
                reporteService
                        .getProductoMasVendido()
        );

        lblMejorCategoria.setText(
                reporteService
                        .getMejorCategoria()
        );

        chartDistribucion
                .getData()
                .clear();

        unidadesPorProducto.forEach(
                (nombre, cant) -> {

                    PieChart.Data slice =
                            new PieChart.Data(
                                    nombre,
                                    cant
                            );

                    chartDistribucion
                            .getData()
                            .add(slice);
                }
        );

        Platform.runLater(() -> {

            for (PieChart.Data slice :
                    chartDistribucion.getData()) {

                javafx.scene.Node node =
                        slice.getNode();

                if (node != null) {

                    double ingresos =
                            dineroPorProducto
                                    .getOrDefault(
                                            slice.getName(),
                                            0.0
                                    );

                    Tooltip t =
                            new Tooltip(
                                    String.format(
                                            "Producto: %s\nUnidades: %.0f\nIngresos: $%,.2f",
                                            slice.getName(),
                                            slice.getPieValue(),
                                            ingresos
                                    )
                            );

                    t.setShowDelay(
                            Duration.ZERO
                    );

                    t.setStyle(
                            "-fx-font-size: 14px; "
                                    + "-fx-background-color: #1a1d24; "
                                    + "-fx-text-fill: white; "
                                    + "-fx-border-color: #F58220; "
                                    + "-fx-border-width: 1px; "
                                    + "-fx-padding: 8px;"
                    );

                    Tooltip.install(
                            node,
                            t
                    );

                    node.setStyle(
                            "-fx-cursor: hand;"
                    );
                }
            }
        });

        actualizarGraficaTendencia(
                ventasFiltradas
        );
    }

    private void actualizarGraficaTendencia(
            List<Venta> ventas) {

        chartTendencia
                .getData()
                .clear();

        chartTendencia
                .setAnimated(false);

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        Map<LocalDate, Double> mapa =
                ventas.stream()
                        .collect(
                                Collectors.groupingBy(
                                        v -> LocalDateTime
                                                .parse(
                                                        v.getFechaHora(),
                                                        fmt
                                                )
                                                .toLocalDate(),
                                        TreeMap::new,
                                        Collectors.summingDouble(
                                                Venta::getTotal
                                        )
                                )
                        );

        mapa.forEach(
                (f, t) ->
                        series
                                .getData()
                                .add(
                                        new XYChart.Data<>(
                                                f.toString(),
                                                t
                                        )
                                )
        );

        chartTendencia
                .getData()
                .add(series);

        Platform.runLater(() -> {

            for (XYChart.Data<String, Number> data :
                    series.getData()) {

                javafx.scene.Node node =
                        data.getNode();

                if (node != null) {

                    Tooltip t =
                            new Tooltip(
                                    String.format(
                                            "Fecha: %s\nIngresos: $%,.2f",
                                            data.getXValue(),
                                            data.getYValue()
                                                    .doubleValue()
                                    )
                            );

                    t.setShowDelay(
                            Duration.ZERO
                    );

                    t.setStyle(
                            "-fx-font-size: 14px; "
                                    + "-fx-background-color: #1a1d24; "
                                    + "-fx-text-fill: white; "
                                    + "-fx-border-color: #60a5fa; "
                                    + "-fx-border-width: 1px; "
                                    + "-fx-padding: 8px;"
                    );

                    Tooltip.install(
                            node,
                            t
                    );

                    node.setStyle(
                            "-fx-cursor: hand;"
                    );
                }
            }
        });
    }

    @FXML
    private void handleExportarReporte() {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Guardar Reporte de Ventas"
        );

        fileChooser.setInitialFileName(
                "Reporte_Ventas_"
                        + LocalDate.now()
                        + ".txt"
        );

        fileChooser
                .getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "Texto Plano (*.txt)",
                                "*.txt"
                        )
                );

        File file =
                fileChooser.showSaveDialog(
                        btnExportar
                                .getScene()
                                .getWindow()
                );

        if (file != null) {

            try (PrintWriter writer =
                         new PrintWriter(file)) {

                writer.println(
                        "=================================================="
                );

                writer.println(
                        "   REPORTE DE INTELIGENCIA DE NEGOCIO - SDG_MDC   "
                );

                writer.println(
                        "=================================================="
                );

                writer.println(
                        "Periodo analizado: "
                                + dpInicio.getValue()
                                + " AL "
                                + dpFin.getValue()
                );

                writer.println(
                        "Filtro de categoría: "
                                + cmbCategoria.getValue()
                );

                writer.println(
                        "Fecha de impresión: "
                                + LocalDateTime.now()
                                .format(fmt)
                );

                writer.println(
                        "--------------------------------------------------"
                );

                writer.println(
                        "RESUMEN FINANCIERO:"
                );

                writer.println(
                        "- Ingresos Totales:    "
                                + lblUtilidadTotal.getText()
                );

                writer.println(
                        "- Producto Estrella:   "
                                + lblMasVendido.getText()
                );

                writer.println(
                        "- Categoría Dominante: "
                                + lblMejorCategoria.getText()
                );

                writer.println(
                        "--------------------------------------------------"
                );

                writer.println(
                        "Este reporte fue generado por el sistema de gestión."
                );

                writer.println(
                        "Uso exclusivo para la Gerencia General."
                );

                UIComponents.showNotification(
                        "Reporte guardado con éxito.",
                        "success"
                );

            } catch (Exception e) {

                e.printStackTrace();

                UIComponents.showNotification(
                        "Error al guardar el archivo de texto.",
                        "error"
                );
            }
        }
    }
}