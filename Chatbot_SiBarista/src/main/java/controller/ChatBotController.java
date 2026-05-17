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


    @FXML private Button adminModeButton;
    @FXML private Button cartButton;
    @FXML private VBox welcomeBox;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private VBox chatAreaWrapper;
    @FXML private VBox chatContainer;
    @FXML private TextField chatInputField;

    @FXML private javafx.scene.layout.BorderPane mainContainer;
    @FXML private javafx.scene.layout.VBox centerChatArea;
    @FXML private javafx.scene.layout.HBox menuChat;

    private final ChatbotService    chatbotService    = new ChatbotService();
    private final KeranjangService  keranjangService  = KeranjangService.getInstance();

    @FXML
    public void initialize() {
        chatAreaWrapper.setVisible(false);
        chatAreaWrapper.setManaged(false);
        refreshKeranjangBadge();
    }

    //ngerefresh keranjang
    private void refreshKeranjangBadge() {
        if (cartButton == null) return;
        int total = keranjangService.getTotalJumlah();
        if (total > 0) {
            cartButton.setText("🛒  Keranjang (" + total + ")");
        } else {
            cartButton.setText("🛒  Keranjang");
        }
    }


    //munculin halaman login admin
    @FXML
    private void handleAdminMode(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/login-view.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) adminModeButton.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.setTitle("Admin Login - SiBarista");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    //method untuk menerima inputan user
    private void kirimPesanDari(TextField field, boolean perluTransisi) throws SQLException {
        String input = field.getText().trim();
        if (!input.isEmpty()) {
            if (perluTransisi) transisiKeModeChat();
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
        if (welcomeBox.isVisible()) transisiKeModeChat();
        prosesInput("Menu");
        scrollKeBelow();
    }

    @FXML
    private void handleRekomendasi(ActionEvent event) throws SQLException {
        if (welcomeBox.isVisible()) transisiKeModeChat();
        prosesInput("rekomendasi");
        scrollKeBelow();
    }


    private void transisiKeModeChat() {
        welcomeBox.setVisible(false);
        welcomeBox.setManaged(false);
        chatAreaWrapper.setVisible(true);
        chatAreaWrapper.setManaged(true);
    }

    //cara respon bot ke user
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

    //menampilkan gambar
    private Image loadGambarProduk(String namaFileGambar) {
        if (namaFileGambar == null || namaFileGambar.isEmpty()) return null;
        try {
            Path resourcesPath = Paths.get(
                    "Chatbot_SiBarista", "src", "main", "resources", "images", namaFileGambar
            );
            if (Files.exists(resourcesPath)) return new Image(resourcesPath.toUri().toString());
            System.out.println("Gambar tidak ditemukan: " + namaFileGambar);
        } catch (Exception e) {
            System.out.println("Gagal load gambar: " + e.getMessage());
        }
        return null;
    }

    //menampilkan gelembung chat
//    private void tambahGelembungChat(String pesan, boolean isUser, Produk p) {
//        HBox barisChat = new HBox();
//        barisChat.setMaxWidth(Double.MAX_VALUE);
//
//        if (isUser) {
//            // --- Bubble User ---
//            Label labelPesan = new Label(pesan);
//            labelPesan.setWrapText(true);
//            labelPesan.setMaxWidth(420);
//            labelPesan.setPadding(new Insets(12, 18, 12, 18));
//            labelPesan.setStyle(
//                    "-fx-background-color: #1C0A00;" +
//                            "-fx-text-fill: #FBF7F0;" +
//                            "-fx-background-radius: 15 15 0 15;" +
//                            "-fx-font-size: 13px;"
//            );
//            barisChat.setAlignment(Pos.CENTER_RIGHT);
//            barisChat.getChildren().add(labelPesan);
//
//        } else {
//            // --- Bubble Bot ---
//            Label labelNama = new Label("☕  SiBarista");
//            labelNama.setStyle(
//                    "-fx-text-fill: #A0522D; -fx-font-size: 11px; -fx-font-style: italic;"
//            );
//
//            VBox bubbleBox = new VBox(10);
//            bubbleBox.setPadding(new Insets(12, 18, 12, 18));
//            bubbleBox.setStyle(
//                    "-fx-background-color: #FBF7F0;" +
//                            "-fx-background-radius: 15 15 15 0;" +
//                            "-fx-border-color: #C8A882;" +
//                            "-fx-border-width: 1;" +
//                            "-fx-border-radius: 15 15 15 0;"
//            );
//
//            // 1. Gambar produk (jika ada)
//            if (p != null && p.getGambar() != null && !p.getGambar().isEmpty()) {
//                try {
//                    Image image = loadGambarProduk(p.getGambar());
//                    if (image != null) {
//                        ImageView imageView = new ImageView(image);
//                        imageView.setFitWidth(220);
//                        imageView.setPreserveRatio(true);
//                        bubbleBox.getChildren().add(imageView);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            // 2. Teks detail produk
//            Label labelPesan = new Label(pesan);
//            labelPesan.setWrapText(true);
//            labelPesan.setStyle("-fx-text-fill: #1C0A00; -fx-font-size: 13px;");
//            bubbleBox.getChildren().add(labelPesan);
//
//            // 3. Tombol "Tambah ke Keranjang" — hanya muncul jika ada produk
//            if (p != null) {
//                Button btnKeranjang = new Button("🛒  Tambah ke Keranjang");
//                final String styleDefault =
//                        "-fx-background-color: #1C0A00; -fx-text-fill: #FBF7F0;" +
//                                "-fx-font-size: 12px; -fx-cursor: hand;" +
//                                "-fx-background-radius: 8; -fx-padding: 7 18 7 18;";
//                final String styleSuccess =
//                        "-fx-background-color: #6B3A2A; -fx-text-fill: #FBF7F0;" +
//                                "-fx-font-size: 12px; -fx-cursor: hand;" +
//                                "-fx-background-radius: 8; -fx-padding: 7 18 7 18;";
//
//                btnKeranjang.setStyle(styleDefault);
//
//                final Produk produkRef = p;
//                btnKeranjang.setOnAction(e -> {
//                    keranjangService.tambahProduk(produkRef);
//                    refreshKeranjangBadge();
//
//                    // Feedback visual singkat
//                    btnKeranjang.setText("✓  Ditambahkan!");
//                    btnKeranjang.setStyle(styleSuccess);
//                    btnKeranjang.setDisable(true);
//
//                    new Thread(() -> {
//                        try { Thread.sleep(1500); } catch (InterruptedException ex) { /* abaikan */ }
//                        javafx.application.Platform.runLater(() -> {
//                            btnKeranjang.setText("🛒  Tambah ke Keranjang");
//                            btnKeranjang.setStyle(styleDefault);
//                            btnKeranjang.setDisable(false);
//                        });
//                    }).start();
//                });
//
//                bubbleBox.getChildren().add(btnKeranjang);
//            }
//
//            VBox botWrapper = new VBox(4, labelNama, bubbleBox);
//            botWrapper.setAlignment(Pos.TOP_LEFT);
//            barisChat.setAlignment(Pos.CENTER_LEFT);
//            barisChat.getChildren().add(botWrapper);
//        }
//
//        chatContainer.getChildren().add(barisChat);
//    }

    private void tambahGelembungChat(String pesan, boolean isUser, Produk p) {
        HBox barisChat = new HBox();
        barisChat.setMaxWidth(Double.MAX_VALUE);

        if (isUser) {
            //pesan user
            Label labelPesan = new Label(pesan);
            labelPesan.setWrapText(true);
            labelPesan.setMaxWidth(420);
            labelPesan.setPadding(new Insets(12, 18, 12, 18));
            labelPesan.setStyle("-fx-background-color: #1C0A00; -fx-text-fill: #FBF7F0; -fx-background-radius: 15 15 0 15; -fx-font-size: 13px;");
            barisChat.setAlignment(Pos.CENTER_RIGHT);
            barisChat.getChildren().add(labelPesan);
        } else {
            //pesan bot
            Label labelNama = new Label("☕  SiBarista");
            labelNama.setStyle("-fx-text-fill: #A0522D; -fx-font-size: 11px; -fx-font-style: italic;");

            VBox bubbleBox = new VBox(10);
            bubbleBox.setMaxWidth(450);
            bubbleBox.setPadding(new Insets(12, 18, 12, 18));
            bubbleBox.setStyle("-fx-background-color: #FBF7F0; -fx-background-radius: 15 15 15 0; -fx-border-color: #C8A882; -fx-border-width: 1; -fx-border-radius: 15 15 15 0;");

            //gambar
            if (p != null && p.getGambar() != null) {
                Image img = loadGambarProduk(p.getGambar());
                if (img != null) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(220);
                    iv.setPreserveRatio(true);
                    bubbleBox.getChildren().add(iv);
                }
            }

            //detail
            Label labelPesan = new Label(pesan);
            labelPesan.setWrapText(true);
            bubbleBox.setMaxWidth(450);
            labelPesan.setStyle("-fx-text-fill: #1C0A00; -fx-font-size: 13px;");
            bubbleBox.getChildren().add(labelPesan);

            //menu costum
            List<CheckBox> listCheckbox = new ArrayList<>();

            if (p != null && "Coffee".equalsIgnoreCase(p.getNamaKategori())) {
                Map<String, List<String>> opsiMap = chatbotService.getOpsiKustom(Integer.parseInt(p.getIdProduk()));
                if (opsiMap != null && !opsiMap.isEmpty()) {
                    VBox kustomContainer = new VBox(8);

                    Label lblMenuCostum = new Label("Menu Costum");
                    lblMenuCostum.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1C0A00; -fx-padding: 5 0 2 0;");
                    kustomContainer.getChildren().add(lblMenuCostum);

                    opsiMap.forEach((grup, namaOpsi) -> {
                        Label lblGrup = new Label(grup + ":");
                        lblGrup.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #6B3A2A;");

                        FlowPane fp = new FlowPane(10, 5);
                        bubbleBox.setMaxWidth(450);
                        for (String opsi : namaOpsi) {
                            CheckBox cb = new CheckBox(opsi);
                            cb.setStyle("-fx-font-size: 11px; -fx-text-fill: #1C0A00;");
                            listCheckbox.add(cb);
                            fp.getChildren().add(cb);
                        }
                        kustomContainer.getChildren().addAll(lblGrup, fp);
                    });

                    bubbleBox.getChildren().add(kustomContainer);
                }
            }

            //tombol keranjang
            if (p != null) {
                Button btnKeranjang = new Button("🛒  Tambah ke Keranjang");
                btnKeranjang.setStyle("-fx-background-color: #1C0A00; -fx-text-fill: #FBF7F0; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 7 15 7 15;");

                final Produk produkRef = p;
                btnKeranjang.setOnAction(e -> {
                    List<String> pilihanUser = new ArrayList<>();
                    for (CheckBox cb : listCheckbox) {
                        if (cb.isSelected()) pilihanUser.add(cb.getText());
                    }

                    keranjangService.tambahProduk(produkRef, pilihanUser);
                    refreshKeranjangBadge();

                    btnKeranjang.setText("✓ Berhasil!");
                    btnKeranjang.setDisable(true);
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                        Platform.runLater(() -> {
                            btnKeranjang.setText("🛒  Tambah ke Keranjang");
                            btnKeranjang.setDisable(false);
                        });
                    }).start();
                });
                bubbleBox.getChildren().add(btnKeranjang);
            }

            VBox botWrapper = new VBox(4, labelNama, bubbleBox);
            barisChat.getChildren().add(botWrapper);
        }
        chatContainer.getChildren().add(barisChat);
    }

    //tombol keranjang disamping chatbot
    @FXML
    private void handleCart(ActionEvent event) {
        try {
            // Load FXML Keranjang
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/felix_71241153/app/chatbot_sibarista/keranjang-view.fxml"));
            javafx.scene.Node cartView = loader.load();

            // Ganti isi tengah BorderPane dengan tampilan Keranjang
            mainContainer.setCenter(cartView);

            // Styling menu sidebar (Keranjang aktif, Chat pasif)
            cartButton.setStyle("-fx-background-color: #6B3A2A; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0;");
            menuChat.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kembali ke halaman Chat / Home.
     */
    //menghandle ketika user balik ke chatbot datanya masih tersimpan/tersisihkan
    @FXML
    private void handleShowChat(javafx.scene.input.MouseEvent event) {
        // Kembalikan isi tengah BorderPane ke tampilan Chat awal
        mainContainer.setCenter(centerChatArea);

        // Styling menu sidebar (Chat aktif, Keranjang pasif)
        menuChat.setStyle("-fx-background-color: #6B3A2A; -fx-background-radius: 8; -fx-cursor: hand;");
        cartButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0;");

        // Refresh badge keranjang untuk berjaga-jaga
        refreshKeranjangBadge();
    }
}