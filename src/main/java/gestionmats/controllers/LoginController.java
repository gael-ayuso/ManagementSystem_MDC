package gestionmats.controllers;

import gestionmats.model.RolUsuario;
import gestionmats.services.GestorSesion;
import gestionmats.utils.AlertUtils;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    @FXML private VBox loginContainer;
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPasswordHidden;
    @FXML private TextField txtPasswordVisible;
    @FXML private ToggleButton btnTogglePass;
    @FXML private ImageView imgEye;
    @FXML private Button btnLogin;
    @FXML private HBox errorBox;

    private final String PATH_OJO_ABIERTO = "/images/ojoAbierto_Naranja.png";
    private final String PATH_OJO_CERRADO = "/images/ojoCerrado_Naranja.png";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPasswordVisibility();
        playEntryAnimation();
        setupEnterNavigation();
    }

    private void playEntryAnimation() {
        FadeTransition fade = new FadeTransition(Duration.millis(800), loginContainer);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition translate = new TranslateTransition(Duration.millis(800), loginContainer);
        translate.setFromY(40);
        translate.setToY(0);

        fade.play();
        translate.play();
    }
    private void setupEnterNavigation() {
        // 1. Si presionas Enter en el Usuario, pasa el cursor a la Contraseña
        txtUser.setOnAction(event -> {
            // Revisa cuál de los dos campos de contraseña está activo en ese momento
            if (txtPasswordVisible.isVisible()) {
                txtPasswordVisible.requestFocus();
            } else {
                txtPasswordHidden.requestFocus();
            }
        });

        // 2. Si presionas Enter en la Contraseña, simula el clic en el botón de "Ingresar"
        txtPasswordHidden.setOnAction(event -> handleLogin());
        txtPasswordVisible.setOnAction(event -> handleLogin());
    }

    private void setupPasswordVisibility() {
        // Vinculamos el texto de ambos campos para que tengan lo mismo
        txtPasswordVisible.textProperty().bindBidirectional(txtPasswordHidden.textProperty());
    }

    @FXML
    private void togglePasswordVisibility() {
        boolean isVisible = btnTogglePass.isSelected();

        txtPasswordVisible.setVisible(isVisible);
        txtPasswordVisible.setManaged(isVisible);
        txtPasswordHidden.setVisible(!isVisible);
        txtPasswordHidden.setManaged(!isVisible);

        updateEyeIcon(isVisible ? PATH_OJO_ABIERTO : PATH_OJO_CERRADO);
    }

    private void updateEyeIcon(String path) {
        try {
            imgEye.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + path);
        }
    }

    @FXML
    private void handleLogin() {
        String user = txtUser.getText();
        String pass = txtPasswordHidden.getText();

        // 1. Usamos el Singleton para validar en el CSV
        boolean loginExitoso = GestorSesion.getInstancia().iniciarSesion(user, pass);

        if (loginExitoso) {
            // 2. Obtenemos el rol del usuario que acaba de iniciar sesión
            RolUsuario rol = GestorSesion.getInstancia().getRolActual();

            // 3. Redirigimos según el rol
            // Replace Conditional with Polymorphism: el switch (rol) se eliminó; cada RolUsuario
            // ya conoce su vista y su título (getVista() / getTitulo()).
            // El if conserva la alerta del antiguo "default" y evita un error si el rol llega null.
            if (rol == null) {
                AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error de Acceso", "Rol no reconocido", "Consulte con soporte.");
                return;
            }
            cargarVistaUsuario(rol.getVista(), rol.getTitulo());
        } else {
            // Animación de error si falla el login
            shakeNode(txtUser);
            shakeNode(txtPasswordVisible.isVisible() ? txtPasswordVisible : txtPasswordHidden);

            if (errorBox != null) {
                errorBox.setVisible(true);
                errorBox.setManaged(true);
            }
        }
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setByX(8f);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();

        // Opcional: Poner el borde rojo temporalmente usando CSS inline solo para el error
        node.setStyle("-fx-border-color: #e74c3c;");
        tt.setOnFinished(e -> node.setStyle("")); // Limpia el estilo al terminar
    }

    private void cargarVistaUsuario(String rutaFxml, String tituloVentana) {
        try {
            URL vistaUrl = getClass().getResource(rutaFxml);

            if (vistaUrl == null) {
                // Si la vista del vendedor o almacenista aún no existe, mostramos una alerta temporal
                AlertUtils.mostrarAlerta(Alert.AlertType.INFORMATION, "Módulo en desarrollo", tituloVentana, "La interfaz para este rol aún está en construcción.");
                return;
            }

            Parent root = FXMLLoader.load(vistaUrl);
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tituloVentana);
            stage.centerOnScreen();

        } catch (IOException e) {
            AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error de Sistema", "No se pudo cargar la vista.", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "Ocurrió un error inesperado al navegar.", e.getMessage());
            e.printStackTrace();
        }
    }


}
