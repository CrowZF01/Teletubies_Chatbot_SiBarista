package service;

import database.Database;
import model.Produk;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatbotService {

    public List<Produk> getDaftarProduk() throws SQLException {
        List<Produk> list = new ArrayList<>();
        String query = "SELECT * FROM produk";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Produk(
                        rs.getString("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("kategori"),
                        rs.getString("deskripsi"),
                        rs.getInt("harga"),
                        rs.getString("status_stok")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================
    // 1. prosesInput()  [WAJIB]
    // Method utama untuk memproses semua pesan user
    // =========================================================
    public String prosesInput(String pesan) throws SQLException {
        if (!validasiTeks(pesan)) {
            return "Silakan ketik pesan terlebih dahulu.";
        }

        String input = normalisasiInput(pesan);

        // sapaan
        if (input.contains("halo") || input.contains("hai") || input.contains("hi")
                || input.contains("selamat pagi") || input.contains("selamat siang")
                || input.contains("selamat sore") || input.contains("selamat malam")) {
            return balasanSapaan();
        }

        // bantuan / help
        if (input.contains("bantuan") || input.contains("help")
                || input.contains("tolong") || input.contains("cara pakai")
                || input.contains("harus ketik apa")) {
            return balasanBantuan();
        }

        // kategori (lebih spesifik, jadi dicek dulu)
        if (input.contains("non-coffee") || input.contains("non coffee")) {
            return balasanKategori("Non-Coffee");
        }

        if (input.contains("coffee")) {
            return balasanKategori("Coffee");
        }

        if (input.contains("snack")) {
            return balasanKategori("Snacks"); // sesuaikan dengan nama kategori di DB
        }

        // menu umum
        if (input.contains("menu")) {
            return balasanMenu();
        }

        // detail produk
        String detail = balasanDetail(input);
        if (detail != null) {
            return detail;
        }

        // fallback
        return balasanFallback();
    }


    // =========================================================
    // 2. validasiTeks() [WAJIB]
    // =========================================================
    public boolean validasiTeks(String pesan) {
        return pesan != null && !pesan.trim().isEmpty();
    }

    // =========================================================
    // 3. balasanMenu() [WAJIB]
    // =========================================================
    public String balasanMenu() {
        return """
                Baik! Silakan pilih kategori menu yang ingin Anda jelajahi:
                - Coffee
                - Non-Coffee
                - Snack
                
                Ketik salah satu kategori di atas.
                """;
    }

    // =========================================================
    // 4. balasanKategori() [WAJIB]
    // =========================================================
    public String balasanKategori(String kategori) {
        StringBuilder hasil = new StringBuilder();
        hasil.append("Berikut daftar menu kategori ").append(kategori).append(":\n");

        boolean ditemukan = false;
        String query = "SELECT produk.nama_produk FROM produk JOIN kategori ON produk.id_kategori = kategori.id_kategori WHERE kategori.nama_kategori = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, kategori);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                hasil.append("- ").append(rs.getString("nama_produk")).append("\n");
                ditemukan = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Maaf, terjadi kesalahan saat mengakses database";
        }

        if (!ditemukan) {
            return "Maaf, kategori " + kategori + " belum tersedia";
        }
        hasil.append("\nKetik nama produk untuk melihat detail");
        return hasil.toString();
    }


    // =========================================================
    // 5. balasanDetail() [WAJIB]
    // Mengembalikan null jika tidak ditemukan
    // =========================================================
    public String balasanDetail(String namaMenu){
        String input = normalisasiInput(namaMenu);
        String query = "SELECT produk.*, kategori.nama_kategori FROM produk JOIN kategori ON produk.id_kategori = kategori.id_kategori WHERE produk.nama_produk LIKE ?";

        try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1,"%" + input + "%");
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                Produk p = new Produk(
                        rs.getString("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("nama_kategori"),
                        rs.getString("deskripsi"),
                        rs.getInt("harga"),
                        rs.getString("status_stok")
                );
                return formatDetailProduk(p);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return  null;
    }

    // =========================================================
    // 6. balasanFallback() [WAJIB]
    // =========================================================
    public String balasanFallback() {
        return """
                Maaf, saya belum memahami pesan Anda.

                Coba ketik salah satu contoh berikut:
                - Menu
                - Coffee
                - Non-Coffee
                - Snack
                - Latte
                - Croissant

                Jika butuh panduan, ketik: Help
                """;
    }

    // =========================================================
    // 7. normalisasiInput() [BAGUS KALAU SEMPAT]
    // =========================================================
    public String normalisasiInput(String pesan) {
        if (pesan == null) {
            return "";
        }

        return pesan
                .trim()
                .toLowerCase()
                .replaceAll("[.,!?]", "")   // hapus tanda baca sederhana
                .replaceAll("\\s+", " ");   // rapikan spasi berlebih
    }

    // =========================================================
    // 8. balasanSapaan() [BAGUS KALAU SEMPAT]
    // =========================================================
    public String balasanSapaan() {
        return """
                Halo, saya SiBarista ☕
                Saya bisa membantu Anda melihat menu café.

                Ketik:
                - Menu  → untuk melihat kategori
                - Help  → untuk melihat cara penggunaan
                """;
    }

    // =========================================================
    // 9. balasanBantuan() [BAGUS KALAU SEMPAT]
    // =========================================================
    public String balasanBantuan() {
        return """
                Panduan penggunaan chatbot SiBarista:

                1. Ketik "Menu" untuk melihat kategori menu.
                2. Ketik kategori seperti:
                   - Coffee
                   - Non-Coffee
                   - Snack
                3. Ketik nama produk untuk melihat detail.
                   Contoh:
                   - Latte
                   - Espresso
                   - Croissant

                Jika ingin mulai dari awal, ketik "Menu".
                """;
    }

    // =========================================================
    // HELPER METHOD TAMBAHAN
    // =========================================================
    private String formatDetailProduk(Produk produk) {
        return """
                Detail Menu:
                ID Produk    : %s
                Nama         : %s
                Kategori     : %s
                Harga        : %s
                Deskripsi    : %s
                Status Stok  : %s

                Jika ingin melihat kategori lain, ketik "Menu".
                """.formatted(
                produk.getIdProduk(),
                produk.getNamaProduk(),
                produk.getNamaKategori(),
                formatRupiah(produk.getHarga()),
                produk.getDeskripsi(),
                produk.getStatusStok()
        );
    }

    private String formatRupiah(int harga) {
        String angka = String.format("%,d", harga).replace(',', '.');
        return "Rp" + angka;
    }


}