package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Produk;
import service.AdminService;
import service.ChatbotService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DashBoardController {
    @FXML private TableView<Produk> productTable;
    @FXML private TableColumn<Produk, String> colId, colNama, colKategori, colDeskripsi, colStok;
    @FXML private TableColumn<Produk, Integer> colHarga;

    private ChatbotService chatbotService = new ChatbotService();
    private AdminService adminService = new AdminService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProduk"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaProduk"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("namaKategori"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("statusStok"));

        loadData();
    }

    private void loadData() {
        try {
            // Ambil data terbaru dari database
            List<Produk> listProduk = chatbotService.getDaftarProduk();
            ObservableList<Produk> data = FXCollections.observableArrayList(listProduk);

            productTable.setItems(data);
            productTable.refresh(); // Paksa refresh tabel
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() throws IOException {
        showForm(null);
    }

    @FXML
    private void handleEdit() throws IOException {
        Produk selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showForm(selected);
        } else {
            new Alert(Alert.AlertType.WARNING, "Pilih produk terlebih dahulu!").show();
        }
    }

    @FXML
    private void handleDelete() {
        Produk selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            adminService.hapusProduk(selected.getIdProduk());
            loadData();
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/Chat-view.fxml"));
        Stage stage = (Stage) productTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showForm(Produk produk) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/product-form-view.fxml"));
        Parent root = loader.load();

        ProductFormController controller = loader.getController();
        controller.setProdukData(produk);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Form Produk");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        loadData();
    }
}