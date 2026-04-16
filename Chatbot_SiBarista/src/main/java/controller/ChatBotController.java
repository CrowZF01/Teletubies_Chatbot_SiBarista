package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatBotController {

    // --- Deklarasi elemen FXML ---
    @FXML private Button adminModeButton;
    @FXML private VBox welcomeBox;
    @FXML private TextField messageField;
    @FXML private Button sendButton;

    @FXML private VBox chatAreaWrapper;
    @FXML private VBox chatContainer;
    @FXML private TextField chatInputField;

    // TODO: Nanti kamu bisa panggil ChatbotService di sini
    // private ChatbotService chatbotService = new ChatbotService();

    @FXML
    public void initialize() {
        // Method ini otomatis dipanggil saat FXML di-load.
        // Pastikan area chat disembunyikan di awal.
        chatAreaWrapper.setVisible(false);
        chatAreaWrapper.setManaged(false);
    }

    // --- Handling Tombol & Input ---

    @FXML
    private void handleAdminMode(ActionEvent event) {
        System.out.println("Tombol Admin Mode ditekan!");
        // Nanti tambahkan logika untuk mengganti scene ke login-view.fxml di sini
    }

    private void kirimPesanDari(TextField field, boolean perluTransisi) {
        String input = field.getText().trim();
        if (!input.isEmpty()) {
            if (perluTransisi) {
                transisiKeModeChat();
            }
            prosesInput(input);
            field.clear();
        }
    }

    @FXML
    private void handleSend(ActionEvent event) {
        // Dipanggil dari TextField halaman Welcome
        kirimPesanDari(messageField, true);
    }

    @FXML
    private void handleSendFromChat(ActionEvent event) {
        // Dipanggil dari TextField saat area chat sudah aktif
        kirimPesanDari(chatInputField, false);
    }

    @FXML
    private void handleMenu(ActionEvent event) {
        // Jika user klik tombol "Menu", kita anggap dia mengetik "Menu"
        String input = "Menu";
        if (welcomeBox.isVisible()) {
            transisiKeModeChat();
        }
        prosesInput(input);
    }

    // --- Logika Internal Controller ---

    private void transisiKeModeChat() {
        // Hilangkan halaman Welcome
        welcomeBox.setVisible(false);
        welcomeBox.setManaged(false);

        // Tampilkan area obrolan
        chatAreaWrapper.setVisible(true);
        chatAreaWrapper.setManaged(true);
    }

    private void prosesInput(String pesanUser) {
        tambahGelembungChat(pesanUser, true);
        String balasanBot;
        if (pesanUser.equalsIgnoreCase("Menu")) {
            balasanBot = "Baik! Silakan ketik kategori menu yang ingin Anda jelajahi hari ini:\n- Coffee\n- Non-Coffee\n- Jus\n- Snack";
        } else if (pesanUser.equalsIgnoreCase("Coffee")) {
            balasanBot = "Pilihan yang mantap! Berikut adalah daftar menu Coffee Based:\n- Latte\n- Mocha\n- Americano";
        } else if (pesanUser.equalsIgnoreCase("Non-Coffee")) {
            balasanBot = "Berikut adalah daftar menu Non-Coffee:\n- Matcha Latte\n- Signature Chocolate";
        } else if (pesanUser.equalsIgnoreCase("Jus")) {
            balasanBot = "Berikut adalah pilihan Jus:\n- Orange Juice\n- Mango Juice";
        } else if (pesanUser.equalsIgnoreCase("Snack")) {
            balasanBot = "Berikut adalah pilihan Snack:\n- Brownies\n- Croissant";
        } else {
            balasanBot = "Halo, saya SiBarista. Ketik 'Menu' untuk melihat daftar kategori yang tersedia.";
        }
        tambahGelembungChat(balasanBot, false);
    }


    private void tambahGelembungChat(String pesan, boolean isUser) {
        Label labelPesan = new Label(pesan);
        labelPesan.setWrapText(true);
        labelPesan.setMaxWidth(400); // Batasi lebar gelembung agar tidak terlalu panjang
        labelPesan.setPadding(new Insets(10, 15, 10, 15));

        HBox barisChat = new HBox();

        if (isUser) {
            // Styling untuk User (Kanan, Abu-abu gelap, teks putih)
            labelPesan.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 15 15 0 15;");
            barisChat.setAlignment(Pos.CENTER_RIGHT);
        } else {
            // Styling untuk Bot (Kiri, Abu-abu terang, teks hitam)
            labelPesan.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: black; -fx-background-radius: 15 15 15 0;");
            barisChat.setAlignment(Pos.CENTER_LEFT);
        }

        barisChat.getChildren().add(labelPesan);
        chatContainer.getChildren().add(barisChat);
    }
}
