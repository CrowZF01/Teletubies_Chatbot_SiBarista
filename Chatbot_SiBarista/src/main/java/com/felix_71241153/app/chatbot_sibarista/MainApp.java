package com.felix_71241153.app.chatbot_sibarista;

import database.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("Chat-view.fxml"));
        // Ubah ukuran scene ke 1100x700 agar UI tidak terpotong
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);

        // Ganti judul aplikasinya
        stage.setTitle("SiBarista - Information Cafe");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


    public class TestKoneksi {
        public static void main(String[] args) {
            Database db = new Database();
            db.cekKoneksi();
        }
    }

}