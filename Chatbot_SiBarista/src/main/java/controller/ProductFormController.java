package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Produk;
import service.AdminService;

public class ProductFormController {
    @FXML private TextField txtId, txtNama, txtHarga;
    @FXML private TextArea txtDeskripsi;
    @FXML private ComboBox<String> cbKategori;
    @FXML private Button btnStokAda, btnStokHabis;
    @FXML private Label lblTitle;

    private AdminService adminService = new AdminService();
    private boolean isEdit = false;
    private String currentStatusStok = "Tersedia";

    @FXML
    public void initialize() {
        cbKategori.setItems(javafx.collections.FXCollections.observableArrayList("Coffee", "Non-Coffee", "Snacks"));
        updateStokUI();
    }

    public void setProdukData(Produk p) {
        if (p != null) {
            // MODE EDIT
            isEdit = true;
            lblTitle.setText("Edit Produk");

            // Tampilkan ID Asli Database di TextField txtId
            txtId.setText(p.getIdProduk());
            txtId.setEditable(false); // Kunci agar tidak bisa diedit
            txtId.setStyle("-fx-background-color: #EEEAE5; -fx-text-fill: #4A3020;");

            // Isi field lainnya
            txtNama.setText(p.getNamaProduk());
            txtHarga.setText(String.valueOf(p.getHarga()));
            txtDeskripsi.setText(p.getDeskripsi());
            cbKategori.setValue(p.getNamaKategori());
            currentStatusStok = p.getStatusStok();
            updateStokUI();
        } else {
            // MODE TAMBAH
            isEdit = false;
            lblTitle.setText("Tambah Menu Baru");
            txtId.setText("AUTO"); // Indikator auto increment
            txtId.setEditable(false);
        }
    }


    @FXML
    private void handleStokTersedia() {
        currentStatusStok = "Tersedia";
        updateStokUI();
    }

    @FXML
    private void handleStokHabis() {
        currentStatusStok = "Habis";
        updateStokUI();
    }

    private void updateStokUI() {
        if (currentStatusStok.equals("Tersedia")) {
            btnStokAda.setStyle("-fx-background-color: #3AB07A; -fx-text-fill: white; -fx-background-radius: 8;");
            btnStokHabis.setStyle("-fx-background-color: transparent; -fx-border-color: #D4B896; -fx-text-fill: #8B7560; -fx-background-radius: 8; -fx-border-radius: 8;");
        } else {
            btnStokAda.setStyle("-fx-background-color: transparent; -fx-border-color: #D4B896; -fx-text-fill: #8B7560; -fx-background-radius: 8; -fx-border-radius: 8;");
            btnStokHabis.setStyle("-fx-background-color: #C44A4A; -fx-text-fill: white; -fx-background-radius: 8;");
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validasi input (Nama dan Harga tidak boleh kosong)
            if (txtNama.getText().isEmpty() || txtHarga.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Nama dan Harga wajib diisi!").show();
                return;
            }

            // Jika Tambah Baru, kirim ID sebagai null atau string kosong
            // agar Database (Auto Increment) yang menghandle
            String idToSave = isEdit ? txtId.getText() : null;

            Produk p = new Produk(
                    idToSave,
                    txtNama.getText(),
                    cbKategori.getValue(),
                    txtDeskripsi.getText(),
                    Integer.parseInt(txtHarga.getText()),
                    currentStatusStok
            );

            if (adminService.simpanProduk(p, isEdit)) {
                ((Stage) txtId.getScene().getWindow()).close();
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Harga harus berupa angka!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Terjadi kesalahan: " + e.getMessage()).show();
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) txtId.getScene().getWindow()).close();
    }
}