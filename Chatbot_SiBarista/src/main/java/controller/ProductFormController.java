package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Produk;
import service.AdminService;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.InputStream;

public class ProductFormController {
    @FXML private TextField txtId, txtNama, txtHarga;
    @FXML private TextArea txtDeskripsi;
    @FXML private ComboBox<String> cbKategori;
    @FXML private Button btnStokAda, btnStokHabis;
    @FXML private Label lblTitle, lblNamaGambar;
    @FXML private ImageView imgPreview;

    private AdminService adminService = new AdminService();
    private boolean isEdit = false;
    private String currentStatusStok = "Tersedia";
    private String namaFileGambar = null;

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

            this.namaFileGambar = p.getGambar();
            if (namaFileGambar != null && !namaFileGambar.isEmpty()) {
                lblNamaGambar.setText(namaFileGambar);
                // Load preview dari folder resources
                try {
                    InputStream is = getClass().getResourceAsStream("/images/" + namaFileGambar);
                    if (is != null) {
                        imgPreview.getImage();
                    }
                } catch (Exception e) {
                    System.out.println("Gagal memuat preview: " + e.getMessage());
                }
            } else {
                lblNamaGambar.setText("Tidak ada gambar");
            }
            updateStokUI();
        } else {
            // MODE TAMBAH
            isEdit = false;
            lblTitle.setText("Tambah Menu Baru");
            txtId.setText("AUTO"); // Indikator auto increment
            txtId.setEditable(false);
            lblNamaGambar.setText("Belum pilih gambar");
            imgPreview.setImage(null);
        }
    }

    @FXML
    private void handlePilihGambar() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Pilih Gambar Menu");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(txtId.getScene().getWindow());

        if (selectedFile != null) {
            this.namaFileGambar = selectedFile.getName();
            lblNamaGambar.setText(namaFileGambar);

            // Tampilkan preview langsung dari file yang dipilih di komputer
            Image img = new Image(selectedFile.toURI().toString());
            imgPreview.setImage(img);
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
                    currentStatusStok,
                    this.namaFileGambar
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