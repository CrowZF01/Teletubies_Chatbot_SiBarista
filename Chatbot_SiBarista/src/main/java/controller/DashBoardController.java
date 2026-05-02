package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    @FXML private TableColumn<Produk, Void> colNo; // Kolom No Urut
    @FXML private TableColumn<Produk, String> colNama, colKategori, colDeskripsi, colStok;
    @FXML private TableColumn<Produk, Integer> colHarga;
    @FXML private Label lblTotalProduk, lblTersedia, lblHabis;
    @FXML private TextField txtSearch;

    private ChatbotService chatbotService = new ChatbotService();
    private AdminService adminService = new AdminService();

    // 1. Deklarasikan List di tingkat class agar tidak ter-reset
    private ObservableList<Produk> masterData = FXCollections.observableArrayList();
    private FilteredList<Produk> filteredData;

    @FXML
    public void initialize() {
        // Setup Kolom No Urut
        colNo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
            }
        });

        colNama.setCellValueFactory(new PropertyValueFactory<>("namaProduk"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("namaKategori"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("statusStok"));

        // 2. Setup FilteredList (Search Logic)
        filteredData = new FilteredList<>(masterData, p -> true);

        // Listener untuk TextField Search
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(produk -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                if (produk.getNamaProduk().toLowerCase().contains(lowerCaseFilter)) return true;
                if (produk.getNamaKategori().toLowerCase().contains(lowerCaseFilter)) return true;
                if (produk.getDeskripsi().toLowerCase().contains(lowerCaseFilter)) return true;

                return false;
            });
            // Update statistik setiap kali filter berubah (opsional)
            updateStatistics(filteredData);
        });

        // 3. Setup SortedList agar tabel tetap bisa di-sort oleh user
        SortedList<Produk> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(productTable.comparatorProperty());

        // 4. PASANG DATA KE TABEL (Hanya sekali di sini)
        productTable.setItems(sortedData);

        loadData();
    }

    private void loadData() {
        try {
            List<Produk> listProduk = chatbotService.getDaftarProduk();

            // 5. JANGAN gunakan productTable.setItems() lagi di sini.
            // Cukup update masterData-nya saja, TableView akan otomatis terupdate.
            masterData.setAll(listProduk);

            updateStatistics(masterData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStatistics(List<Produk> list) {
        long total = list.size();
        long tersedia = list.stream().filter(p -> "Tersedia".equalsIgnoreCase(p.getStatusStok())).count();
        long habis = total - tersedia;

        lblTotalProduk.setText(String.valueOf(total));
        lblTersedia.setText(String.valueOf(tersedia));
        lblHabis.setText(String.valueOf(habis));
    }

    private void setupSearch() {
        FilteredList<Produk> filteredData = new FilteredList<>(masterData, p -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(produk -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return produk.getNamaProduk().toLowerCase().contains(lowerCaseFilter) ||
                        produk.getNamaKategori().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<Produk> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sortedData);
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
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus produk ini?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    adminService.hapusProduk(selected.getIdProduk());
                    loadData();
                }
            });
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
        stage.setTitle(produk == null ? "Tambah Produk" : "Edit Produk");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        loadData();
    }
}