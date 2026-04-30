package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Produk;
import service.AdminService;

public class ProductFormController {
    @FXML private TextField txtId, txtNama, txtHarga;
    @FXML private TextArea txtDeskripsi;
    @FXML private ComboBox<String> cbKategori, cbStok;
    @FXML private Label lblTitle;

    private AdminService adminService = new AdminService();
    private boolean isEdit = false;

    @FXML
    public void initialize() {
        cbKategori.setItems(javafx.collections.FXCollections.observableArrayList("Coffee", "Non-Coffee", "Snacks"));
        cbStok.setItems(javafx.collections.FXCollections.observableArrayList("Tersedia", "Habis"));
    }

    public void setProdukData(Produk p) {
        if (p != null) {
            isEdit = true;
            lblTitle.setText("Edit Produk");
            txtId.setText(p.getIdProduk());
            txtId.setEditable(false);
            txtNama.setText(p.getNamaProduk());
            txtHarga.setText(String.valueOf(p.getHarga()));
            txtDeskripsi.setText(p.getDeskripsi());
            cbKategori.setValue(p.getNamaKategori());
            cbStok.setValue(p.getStatusStok());
        }
    }

    @FXML
    private void handleSave() {
        try {
            Produk p = new Produk(
                    txtId.getText(),
                    txtNama.getText(),
                    cbKategori.getValue(),
                    txtDeskripsi.getText(),
                    Integer.parseInt(txtHarga.getText()),
                    cbStok.getValue()
            );

            if (adminService.simpanProduk(p, isEdit)) {
                ((Stage) txtId.getScene().getWindow()).close();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Input tidak valid!").show();
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) txtId.getScene().getWindow()).close();
    }
}