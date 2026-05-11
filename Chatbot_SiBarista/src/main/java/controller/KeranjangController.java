package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Keranjang;
import model.Produk;
import service.KeranjangService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KeranjangController {

    @FXML private VBox menuListContainer;
    @FXML private VBox cartItemContainer;
    @FXML private Label totalLabel;
    @FXML private Label sectionTitle;
    @FXML private Button btnFilterSemua;
    @FXML private Button btnFilterKopi;
    @FXML private Button btnFilterNonKopi;
    @FXML private Button btnFilterMakanan;

    private final KeranjangService keranjangService = KeranjangService.getInstance();
    private final Produk produkModel = new Produk();
    private static final NumberFormat RUPIAH = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        muatSemuaMenu();
        refreshKeranjang();
    }

    private void muatSemuaMenu() {
        if (sectionTitle != null) sectionTitle.setText("Semua Menu");
        try {
            tampilkanDaftarProduk(produkModel.getAllProduk());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setTombolAktif(Button tombolAktif) {
        String stylePasif = "-fx-background-color: transparent; -fx-text-fill: #C8A882; -fx-font-size: 12px; -fx-cursor: hand; -fx-border-color: #C8A882; -fx-border-radius: 20; -fx-border-width: 1; -fx-background-radius: 20; -fx-padding: 5 14 5 14;";
        String styleAktif = "-fx-background-color: #C8A882; -fx-text-fill: #1C0A00; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-weight: bold;";

        // 1. Kembalikan semua tombol ke mode garis pinggir (Pasif)
        btnFilterSemua.setStyle(stylePasif);
        btnFilterKopi.setStyle(stylePasif);
        btnFilterNonKopi.setStyle(stylePasif);
        btnFilterMakanan.setStyle(stylePasif);

        // 2. Beri warna pekat pada tombol yang dipilih (Aktif)
        tombolAktif.setStyle(styleAktif);
    }


    @FXML
    private void handleFilterSemua(ActionEvent event) {
        muatSemuaMenu();
        setTombolAktif(btnFilterSemua);
    }

    @FXML
    private void handleFilterKopi(ActionEvent event) {
        muatMenuByKategori(1, "Kopi");
        setTombolAktif(btnFilterKopi);
    }

    @FXML
    private void handleFilterNonKopi(ActionEvent event) {
        muatMenuByKategori(2, "Non-Kopi");
        setTombolAktif(btnFilterNonKopi);
    }

    @FXML
    private void handleFilterMakanan(ActionEvent event) {
        muatMenuByKategori(3, "Snack");
        setTombolAktif(btnFilterMakanan);
    }

    private void muatMenuByKategori(int idKategori, String judul) {
        if (sectionTitle != null) sectionTitle.setText(judul);
        try {
            tampilkanDaftarProduk(produkModel.getProdukByKategori(idKategori));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── LOGIKA RENDER (SANGAT BERSIH SEKARANG) ────────────────────────────────

    private void tampilkanDaftarProduk(List<Produk> produkList) {
        menuListContainer.getChildren().clear();
        for (Produk p : produkList) {
            menuListContainer.getChildren().add(buatKartuProduk(p));
        }
    }

    private HBox buatKartuProduk(Produk p) {
        try {
            // Load Mini FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/kartu-produk.fxml"));
            HBox kartu = loader.load();

            // Isi Data menggunakan lookup ID
            ((Label) kartu.lookup("#lblNama")).setText(p.getNamaProduk());
            ((Label) kartu.lookup("#lblHarga")).setText(formatRupiah(p.getHarga()));

            ImageView iv = (ImageView) kartu.lookup("#imgProduk");
            Image img = loadGambarProduk(p.getGambar());
            if (img != null) iv.setImage(img);

            Button btnTambah = (Button) kartu.lookup("#btnTambah");
            btnTambah.setOnAction(e -> {
                keranjangService.tambahProduk(p);
                refreshKeranjang();
            });

            // Hover effect (Satu-satunya styling yang tersisa di Java)
            btnTambah.setOnMouseEntered(e -> btnTambah.setStyle(btnTambah.getStyle().replace("#1C0A00", "#6B3A2A")));
            btnTambah.setOnMouseExited(e -> btnTambah.setStyle(btnTambah.getStyle().replace("#6B3A2A", "#1C0A00")));

            VBox.setMargin(kartu, new Insets(0, 0, 8, 0));
            return kartu;
        } catch (Exception e) {
            e.printStackTrace();
            return new HBox();
        }
    }

    private void refreshKeranjang() {
        cartItemContainer.getChildren().clear();
        List<Keranjang> items = keranjangService.getItems();

        if (items.isEmpty()) {
            Label kosong = new Label("Keranjang masih kosong ☕");
            kosong.setStyle("-fx-text-fill: #A0522D; -fx-font-size: 13px; -fx-font-style: italic; -fx-padding: 12;");
            cartItemContainer.getChildren().add(kosong);
        } else {
            for (Keranjang item : items) {
                cartItemContainer.getChildren().add(buatBarisKeranjang(item));
            }
        }

        if (totalLabel != null) totalLabel.setText(formatRupiah(keranjangService.getTotalHarga()));
    }

    private HBox buatBarisKeranjang(Keranjang item) {
        try {
            // Load Mini FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/baris-keranjang.fxml"));
            HBox baris = loader.load();
            Produk p = item.getProduk();

            // Isi Data
            ((Label) baris.lookup("#lblNama")).setText(p.getNamaProduk());
            ((Label) baris.lookup("#lblSubtotal")).setText(formatRupiah(item.getSubtotal()));
            ((Label) baris.lookup("#lblJumlah")).setText(String.valueOf(item.getJumlah()));

            // Aksi Tombol
            ((Button) baris.lookup("#btnKurang")).setOnAction(e -> {
                keranjangService.kurangiProduk(p);
                refreshKeranjang();
            });

            ((Button) baris.lookup("#btnTambah")).setOnAction(e -> {
                keranjangService.tambahProduk(p);
                refreshKeranjang();
            });

            VBox.setMargin(baris, new Insets(0, 0, 6, 0));
            return baris;
        } catch (Exception e) {
            e.printStackTrace();
            return new HBox();
        }
    }

    @FXML
    private void handleCheckout(ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Checkout");
        alert.setHeaderText("Total Hitung Harga☕");
        alert.setContentText("Total Harga: " + formatRupiah(keranjangService.getTotalHarga()));
        alert.showAndWait();
        keranjangService.kosongkanKeranjang();
        refreshKeranjang();
    }

    private Image loadGambarProduk(String namaFile) {
        if (namaFile == null || namaFile.isEmpty()) return null;
        try {
            Path path = Paths.get("Chatbot_SiBarista", "src", "main", "resources", "images", namaFile);
            if (Files.exists(path)) return new Image(path.toUri().toString());
        } catch (Exception e) {
            System.out.println("Gagal load gambar: " + e.getMessage());
        }
        return null;
    }

    private String formatRupiah(double angka) {
        return RUPIAH.format(angka);
    }
}