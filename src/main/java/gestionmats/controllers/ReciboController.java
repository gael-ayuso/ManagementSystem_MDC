package gestionmats.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReciboController {

    @FXML private TextArea txtRecibo;

    public void setContenidoRecibo(String contenido) {
        txtRecibo.setText(contenido);
    }

    @FXML

    private void handleCerrar() {
        Stage stage = (Stage) txtRecibo.getScene().getWindow();
        stage.close();
    }
}