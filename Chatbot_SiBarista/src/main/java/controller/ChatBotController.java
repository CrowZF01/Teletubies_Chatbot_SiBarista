package controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import service.KeranjangService;
import service.ChatbotService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import model.Produk;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatBotController {

    @FXML
    private Button adminModeButton;
    @FXML
    private Button cartButton;
    @FXML
    private VBox welcomeBox;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private VBox chatAreaWrapper;
    @FXML
    private VBox chatContainer;
    @FXML
    private TextField chatInputField;

    @FXML
    private javafx.scene.layout.BorderPane mainContainer;
    @FXML
    private javafx.scene.layout.VBox centerChatArea;
    @FXML
    private javafx.scene.layout.HBox menuChat;

    private final ChatbotService chatbotService = new ChatbotService();
    private final KeranjangService keranjangService = KeranjangService.getInstance();

    /**
     * Metode inisialisasi bawaan JavaFX.
     * Dipanggil secara otomatis setelah file FXML dimuat.
     * Tugas utamanya:
     * 1. Menyembunyikan area chat (chatAreaWrapper) di awal karena user belum mengirim pesan.
     * 2. Menyegarkan teks badge (jumlah item) di tombol keranjang belanja.
     */
    @FXML
    public void initialize() {
        chatAreaWrapper.setVisible(false);
        chatAreaWrapper.setManaged(false);
        refreshKeranjangBadge();
    }

    /**
     * Memperbarui label jumlah barang pada tombol keranjang di sidebar.
     * Mengambil total jumlah item dari KeranjangService secara real-time.
     */
    private void refreshKeranjangBadge() {
        if (cartButton == null)
            return;
        int total = keranjangService.getTotalJumlah();
        if (total > 0) {
            cartButton.setText("🛒  Keranjang (" + total + ")");
        } else {
            cartButton.setText("🛒  Keranjang");
        }
    }

    /**
     * Navigasi ke halaman login admin.
     * Memuat file login-view.fxml dan menetapkannya ke jendela (Stage) yang sedang aktif.
     */
    @FXML
    private void handleAdminMode(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/login-view.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) adminModeButton.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.setTitle("Admin Login - SiBarista");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Memproses pengiriman pesan dari text field.
     * Mengambil teks input, mengirimkannya ke fungsi chatbot, mengosongkan text field,
     * dan menggulir (scroll) area chat ke bagian paling bawah secara otomatis.
     */
    private void kirimPesanDari(TextField field, boolean perluTransisi) throws SQLException {
        String input = field.getText().trim();
        if (!input.isEmpty()) {
            if (perluTransisi)
                transisiKeModeChat();
            prosesInput(input);
            field.clear();
            scrollKeBelow();
        }
    }

    @FXML
    private void handleSend(ActionEvent event) throws SQLException {
        kirimPesanDari(messageField, true);
    }

    @FXML
    private void handleSendFromChat(ActionEvent event) throws SQLException {
        kirimPesanDari(chatInputField, false);
    }

    @FXML
    private void handleMenu(ActionEvent event) throws SQLException {
        if (welcomeBox.isVisible())
            transisiKeModeChat();
        prosesInput("Menu");
        scrollKeBelow();
    }

    @FXML
    private void handleRekomendasi(ActionEvent event) throws SQLException {
        if (welcomeBox.isVisible())
            transisiKeModeChat();
        prosesInput("rekomendasi");
        scrollKeBelow();
    }

    private void transisiKeModeChat() {
        welcomeBox.setVisible(false);
        welcomeBox.setManaged(false);
        chatAreaWrapper.setVisible(true);
        chatAreaWrapper.setManaged(true);
    }

    /**
     * Logika utama pemrosesan input chatbot di sisi Controller.
     * 1. Menampilkan gelembung chat dari user terlebih dahulu.
     * 2. Memanggil ChatbotService untuk mencari tahu apakah kalimat user mengandung satu atau beberapa nama produk.
     * 3. Jika ya: Tampilkan detail produk (termasuk gambar & opsi kustom) untuk setiap produk yang terdeteksi.
     * 4. Jika tidak: Kirim kalimat ke chatbotService untuk diproses secara reguler (sapaan, bantuan, kategori, dll).
     */
    private void prosesInput(String pesanUser) throws SQLException {
        tambahGelembungChat(pesanUser, true, null);
        List<Produk> listProduk = chatbotService.cariSemuaProdukDalamKalimat(pesanUser);

        if (!listProduk.isEmpty()) {
            for (Produk p : listProduk) {
                String balasanBot = chatbotService.formatDetailProduk(p);
                tambahGelembungChat(balasanBot, false, p);
            }
        } else {
            String balasanBot = chatbotService.prosesInput(pesanUser);
            tambahGelembungChat(balasanBot, false, null);
        }
    }

    private void scrollKeBelow() {
        javafx.application.Platform.runLater(() -> {
            for (Node node : chatAreaWrapper.getChildren()) {
                if (node instanceof ScrollPane scrollPane) {
                    scrollPane.setVvalue(1.0);
                    break;
                }
            }
        });
    }

    // menampilkan gambar
    private Image loadGambarProduk(String namaFileGambar) {
        if (namaFileGambar == null || namaFileGambar.isEmpty())
            return null;
        try {
            Path resourcesPath = Paths.get(
                    "Chatbot_SiBarista", "src", "main", "resources", "images", namaFileGambar);
            if (Files.exists(resourcesPath))
                return new Image(resourcesPath.toUri().toString());
            System.out.println("Gambar tidak ditemukan: " + namaFileGambar);
        } catch (Exception e) {
            System.out.println("Gagal load gambar: " + e.getMessage());
        }
        return null;
    }

    private void tambahGelembungChat(String pesan, boolean isUser, Produk p) {
        HBox barisChat = new HBox();
        barisChat.setMaxWidth(Double.MAX_VALUE);

        if (isUser) {
            // pesan user
            Label labelPesan = new Label(pesan);
            labelPesan.setWrapText(true);
            labelPesan.setMaxWidth(420);
            labelPesan.setPadding(new Insets(12, 18, 12, 18));
            labelPesan.setStyle(
                    "-fx-background-color: #1C0A00; -fx-text-fill: #FBF7F0; -fx-background-radius: 15 15 0 15; -fx-font-size: 13px;");
            barisChat.setAlignment(Pos.CENTER_RIGHT);
            barisChat.getChildren().add(labelPesan);
        } else {
            // pesan bot
            Label labelNama = new Label("☕  SiBarista");
            labelNama.setStyle("-fx-text-fill: #A0522D; -fx-font-size: 11px; -fx-font-style: italic;");

            VBox bubbleBox = new VBox(10);
            bubbleBox.setMaxWidth(450);
            bubbleBox.setPadding(new Insets(12, 18, 12, 18));
            bubbleBox.setStyle(
                    "-fx-background-color: #FBF7F0; -fx-background-radius: 15 15 15 0; -fx-border-color: #C8A882; -fx-border-width: 1; -fx-border-radius: 15 15 15 0;");

            // gambar
            if (p != null && p.getGambar() != null) {
                Image img = loadGambarProduk(p.getGambar());
                if (img != null) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(220);
                    iv.setPreserveRatio(true);
                    bubbleBox.getChildren().add(iv);
                }
            }

            // detail
            Label labelPesan = new Label(pesan);
            labelPesan.setWrapText(true);
            bubbleBox.setMaxWidth(450);
            labelPesan.setStyle("-fx-text-fill: #1C0A00; -fx-font-size: 13px;");
            bubbleBox.getChildren().add(labelPesan);

            // menu costum
            List<RadioButton> listRadioButton = new ArrayList<>();

            if (p != null && "Coffee".equalsIgnoreCase(p.getNamaKategori())) {
                Map<String, List<String>> opsiMap = chatbotService.getOpsiKustom(Integer.parseInt(p.getIdProduk()));
                if (opsiMap != null && !opsiMap.isEmpty()) {
                    VBox kustomContainer = new VBox(8);

                    Label lblMenuCostum = new Label("Menu Costum");
                    lblMenuCostum.setStyle(
                            "-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1C0A00; -fx-padding: 5 0 2 0;");
                    kustomContainer.getChildren().add(lblMenuCostum);

                    for (Map.Entry<String, List<String>> data : opsiMap.entrySet()) {
                        String key = data.getKey();
                        List<String> namaOpsi = data.getValue();
                        Label lblGrup = new Label(key + ":");
                        lblGrup.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #6B3A2A;");
                        ToggleGroup grupTombol = new ToggleGroup();
                        FlowPane fp = new FlowPane(10, 5);
                        bubbleBox.setMaxWidth(450);
                        for (String opsi : namaOpsi) {
                            RadioButton rb = new RadioButton(opsi);
                            rb.setStyle("-fx-font-size: 11px; -fx-text-fill: #1C0A00;");
                            rb.setToggleGroup(grupTombol);
                            listRadioButton.add(rb);
                            fp.getChildren().add(rb);
                        }
                        kustomContainer.getChildren().addAll(lblGrup, fp);
                    };


                    bubbleBox.getChildren().add(kustomContainer);
                }
            }

            // tombol keranjang
            if (p != null) {
                Button btnKeranjang = new Button("🛒  Tambah ke Keranjang");
                btnKeranjang.setStyle(
                        "-fx-background-color: #1C0A00; -fx-text-fill: #FBF7F0; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 7 15 7 15;");

                final Produk produkRef = p;
                btnKeranjang.setOnAction(e -> {
                    List<String> pilihanUser = new ArrayList<>();
                    for (RadioButton rb : listRadioButton) {
                        if (rb.isSelected())
                            pilihanUser.add(rb.getText());
                    }

                    keranjangService.tambahProduk(produkRef, pilihanUser);
                    refreshKeranjangBadge();

                    btnKeranjang.setText("✓ Berhasil!");
                    btnKeranjang.setDisable(true);
                    
                    // Menggunakan PauseTransition bawaan JavaFX untuk menjadwalkan aksi di UI Thread secara aman
                    // tanpa perlu membuat Thread latar belakang manual (new Thread) yang boros resources
                    javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                    pause.setOnFinished(eventPause -> {
                        btnKeranjang.setText("🛒  Tambah ke Keranjang");
                        btnKeranjang.setDisable(false);
                    });
                    pause.play();
                });
                bubbleBox.getChildren().add(btnKeranjang);
            }

            VBox botWrapper = new VBox(4, labelNama, bubbleBox);
            barisChat.getChildren().add(botWrapper);
        }
        chatContainer.getChildren().add(barisChat);
    }

    // tombol keranjang disamping chatbot
    @FXML
    private void handleCart(ActionEvent event) {
        try {
            // Load FXML Keranjang
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/keranjang-view.fxml"));
            javafx.scene.Node cartView = loader.load();

            // Ganti isi tengah BorderPane dengan tampilan Keranjang
            mainContainer.setCenter(cartView);

            // Styling menu sidebar (Keranjang aktif, Chat pasif)
            cartButton.setStyle(
                    "-fx-background-color: #6B3A2A; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0;");
            menuChat.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kembali ke halaman Chat / Home.
     */
    // menghandle ketika user balik ke chatbot datanya masih tersimpan/tersisihkan
    @FXML
    private void handleShowChat(javafx.scene.input.MouseEvent event) {
        // Kembalikan isi tengah BorderPane ke tampilan Chat awal
        mainContainer.setCenter(centerChatArea);

        // Styling menu sidebar (Chat aktif, Keranjang pasif)
        menuChat.setStyle("-fx-background-color: #6B3A2A; -fx-background-radius: 8; -fx-cursor: hand;");
        cartButton.setStyle(
                "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0;");

        // Refresh badge keranjang untuk berjaga-jaga
        refreshKeranjangBadge();
    }
}