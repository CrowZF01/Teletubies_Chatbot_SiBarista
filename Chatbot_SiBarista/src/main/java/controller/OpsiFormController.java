package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.OpsiKustom;
import service.AdminService;

import java.util.List;

/**
 * Controller untuk mengelola daftar opsi kustomisasi kopi (Opsi Kustom Form Controller).
 * Controller ini menangani tampilan popup modal opsi-form-view.fxml.
 * Berfungsi untuk menambah dan menghapus opsi modifikasi menu kopi (misalnya menambahkan pilihan
 * "Suhu: Hangat" atau "Ukuran: Large") langsung ke database opsi_kustom secara real-time.
 */
public class OpsiFormController {

    @FXML private TableView<OpsiKustom> opsiTable;
    @FXML private TableColumn<OpsiKustom, Void> colNo;
    @FXML private TableColumn<OpsiKustom, String> colGrup;
    @FXML private TableColumn<OpsiKustom, String> colNama;

    @FXML private TextField txtGrup;
    @FXML private TextField txtNama;

    private final AdminService adminService = new AdminService();
    private final ObservableList<OpsiKustom> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup nomor urut otomatis
        colNo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
            }
        });

        colGrup.setCellValueFactory(new PropertyValueFactory<>("grupOpsi"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaOpsi"));

        opsiTable.setItems(masterData);
        loadData();
    }

    private void loadData() {
        List<OpsiKustom> list = adminService.getSemuaOpsiKustom();
        masterData.setAll(list);
    }

    @FXML
    private void handleTambah() {
        String grup = txtGrup.getText().trim();
        String nama = txtNama.getText().trim();

        if (grup.isEmpty() || nama.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Grup Kustom dan Nama Pilihan wajib diisi!").show();
            return;
        }

        if (adminService.tambahOpsiKustom(nama, grup)) {
            txtNama.clear(); // Bersihkan nama pilihan agar mudah menginput pilihan kedua (misal: setelah Panas, ketik Dingin)
            loadData();
        } else {
            new Alert(Alert.AlertType.ERROR, "Gagal menambahkan opsi custom!").show();
        }
    }

    @FXML
    private void handleDelete() {
        OpsiKustom selected = opsiTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus opsi custom ini?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (adminService.hapusOpsiKustom(selected.getIdOpsi())) {
                        loadData();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Gagal menghapus data dari Database!").show();
                    }
                }
            });
        } else {
            new Alert(Alert.AlertType.WARNING, "Pilih baris pada tabel terlebih dahulu!").show();
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) opsiTable.getScene().getWindow()).close();
    }
}