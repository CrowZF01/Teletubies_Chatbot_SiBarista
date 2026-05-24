package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.AdminService;

import java.io.IOException;

/**
 * Controller ini menghubungkan file view login-view.fxml dengan logika database di AdminService.
 * Bertugas memvalidasi username & password, menampilkan dialog box peringatan jika login gagal,
 * serta memandu pengguna masuk ke dashboard menu admin jika verifikasi sukses.
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private AdminService adminService = new AdminService();

    //login
    @FXML
    private void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (adminService.login(user, pass)) {
            navigateToDashboard();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username atau Password Salah!");
            alert.show();
        }
    }

    //kembali ke chatbot
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/Chat-view.fxml"));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    //ketika berhasil login lalu dibawa ke dashboard
    private void navigateToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/dashboard-view.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}