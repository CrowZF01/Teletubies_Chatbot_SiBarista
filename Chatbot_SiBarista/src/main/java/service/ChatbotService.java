package service;

import database.Database;
import model.Produk;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatbotService {

    //query untuk menampilkan daftar produk
    public List<Produk> getDaftarProduk() throws SQLException {
        List<Produk> list = new ArrayList<>();

        // Query dengan JOIN untuk mendapatkan nama_kategori
        String query = "SELECT p.*, k.nama_kategori " +
                "FROM produk p " +
                "JOIN kategori k ON p.id_kategori = k.id_kategori";

        try (Connection conn = Database.getConnection(); //koneksi
             Statement stmt = conn.createStatement(); //untuk querynya
             ResultSet rs = stmt.executeQuery(query)) { //untuk memberikan datanya dari db

            while (rs.next()) {
                list.add(new Produk(
                        rs.getString("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("nama_kategori"),
                        rs.getString("deskripsi"),
                        rs.getInt("harga"),
                        rs.getString("status_stok"),
                        rs.getString("gambar")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Kesalahan Query: " + e.getMessage());
        }
        return list;
    }

    //untuk ngecek klo pesannya tidak boleh kosong
    public String prosesInput(String pesan) throws SQLException {
        if (!validasiTeks(pesan)) {
            return "Silakan ketik pesan terlebih dahulu.";
        }

        String input = normalisasiInput(pesan);

        // 1. Sapaan
        if (input.equals("halo") || input.equals("hai") || input.equals("hi")
                || input.equals("selamat pagi") || input.equals("selamat siang")
                || input.equals("selamat sore") || input.equals("selamat malam") ||
                input.equals("apa kabar") || input.equals("haloo") || input.equals("helo") ||
                input.equals("hello") || input.equals("heloo") || input.equals("halloo") ||
                input.equals("haii") || input.equals("hii") || input.equals("haloha") || input.equals("halohai")
                || input.equals("hola") || input.equals("holaa")) {
            return balasanSapaan();
        }

        // 2. Bantuan
        if (input.contains("bantuan") || input.contains("help")
                || input.contains("tolong") || input.contains("cara pakai")
                || input.contains("harus ketik apa") || input.contains("saya bingung")
                ||input.contains("helep") || input.contains("tulung") || input.contains("ketik apa?")
                || input.contains("ketik apa") || input.contains("ketik apa ya?") || input.contains("ketik apa ya")) {
            return balasanBantuan();
        }

        // 3. Kategori spesifik dulu (lebih spesifik daripada "menu")
        if (input.contains("non-coffee") || input.contains("non coffee") || input.contains("non cofee")
                || input.contains("non coffe") || input.contains("non cofe") || input.contains("non-cofe")
                || input.contains("non-cofee") || input.contains("non-coffe")) {
            return balasanKategori("Non-Coffee");
        }

        if (input.contains("coffee") || input.contains("coffe") || input.contains("cofee") || input.contains("cofe")) {
            return balasanKategori("Coffee");
        }

        if (input.contains("snack") || input.contains("snacks") || input.contains("snak") || input.contains("snac")) {
            return balasanKategori("Snacks");
        }

        // 4. Menu umum
        if (input.contains("menu")) {
            return balasanMenu();
        }

        if(input.contains("rekomendasi") || input.contains("rekomen") || input.contains("saran") || input.contains("best seller")){
            return balasanRekomendasi();
        }

        //untuk mencari nama produk
        String produkDitemukan = cariNamaProdukDalamKalimat(input);
        if (produkDitemukan != null) {
            Produk p = balasanDetail(produkDitemukan); // Ambil objek Produk-nya dulu
            if (p != null) {
                return formatDetailProduk(p); // Ubah ke String menggunakan helper method Anda
            }
        }

        // 6. Kalau user mengetik nama produk langsung
        Produk p = balasanDetail(input);
        if (p != null) {
            return formatDetailProduk(p); // Ubah ke String sebelum di-return
        }

        // 7. Fallback
        return balasanFallback();
    }



    //validasi teks
    public boolean validasiTeks(String pesan) {
        return pesan != null && !pesan.trim().isEmpty();
    }

    //balasan menu
    public String balasanMenu() {
        return """
                Baik! Silakan pilih kategori menu yang ingin Anda jelajahi:
                - Coffee
                - Non-Coffee
                - Snack
                
                Ketik salah satu kategori di atas.
                """;
    }

    //balasan daftar menu sesuai kategori
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


    //untuk balasan detail produk
    public Produk balasanDetail(String namaMenu) {
        String input = normalisasiInput(namaMenu);

        String query = """
            SELECT produk.*, kategori.nama_kategori
            FROM produk
            JOIN kategori ON produk.id_kategori = kategori.id_kategori
            WHERE LOWER(produk.nama_produk) = LOWER(?)
            LIMIT 1
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, input);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Produk(
                        rs.getString("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("nama_kategori"),
                        rs.getString("deskripsi"),
                        rs.getInt("harga"),
                        rs.getString("status_stok"),
                        rs.getString("gambar")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //untuk mencari nama produk dalam kalimat
    public String cariNamaProdukDalamKalimat(String input){
        String query = "SELECT nama_produk FROM produk";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()){
                String namaProduk = rs.getString("nama_produk").toLowerCase();

                if (input.contains(namaProduk)){
                    return namaProduk;
                }

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //fallback
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

    //normalisasi inputan
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

    //balasan sapaan
    public String balasanSapaan() {
        return """
                Halo, saya SiBarista ☕
                Saya bisa membantu Anda melihat menu café.

                Ketik:
                - Menu  → untuk melihat kategori
                - Help  → untuk melihat cara penggunaan
                """;
    }

    //balasan bantuan
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

    //format untuk detail produk
    public String formatDetailProduk(Produk produk) {
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

    //format rupiah
    private String formatRupiah(int harga) {
        String angka = String.format("%,d", harga).replace(',', '.');
        return "Rp" + angka;
    }

    //balasan untuk rekomendasi (random dari bd dipilih 2)
    public String balasanRekomendasi(){
        StringBuilder hasil = new StringBuilder();
        hasil.append("Ini adalah beberapa rekomendasi dari SiBarista :\n\n");
        String query = """
                SELECT produk.nama_produk, kategori.nama_kategori, produk.harga FROM produk 
                JOIN kategori ON produk.id_kategori = kategori.id_kategori
                WHERE produk.status_stok = 'Tersedia'
                ORDER BY RAND() LIMIT 2
                """;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                hasil.append("- ").append(rs.getString("nama_produk"))
                        .append(" (").append(rs.getString("nama_kategori")).append(")\n")
                        .append("   Harga: ").append(formatRupiah(rs.getInt("harga"))).append("\n\n");
            }
        }catch (SQLException e){
            return "Maaf, rekomendasi untuk sekarang tidak ada";
        }
        hasil.append("Ketik nama produk diatas untuk melihat detailnya");
        return hasil.toString();
    }

    //cari produk dalam kalimat (bisa lebih dari 1)
    public List<Produk> cariSemuaProdukDalamKalimat(String pesan) {
        List<Produk> produkDitemukan = new ArrayList<>();
        String input = normalisasiInput(pesan);

        // Ambil semua nama produk dari database untuk dicek satu per satu
        String query = "SELECT nama_produk FROM produk";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String namaProduk = rs.getString("nama_produk").toLowerCase();

                // Jika kalimat user mengandung nama produk ini, tambahkan ke List
                if (input.contains(namaProduk)) {
                    Produk p = balasanDetail(namaProduk);
                    if (p != null) {
                        produkDitemukan.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produkDitemukan;
    }

//    public List<String> getOpsiKustom(int idProduk) {
//        List<String> listOpsi = new ArrayList<>();
//        String query = "SELECT nama_opsi FROM opsi_kustom WHERE id_kategori = (SELECT id_kategori FROM produk WHERE id_produk = ?)";
//
//        try (Connection conn = Database.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setInt(1, idProduk);
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                listOpsi.add(rs.getString("nama_opsi"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return listOpsi;
//    }

    public Map<String, List<String>> getOpsiKustom(int idProduk) {
        Map<String, List<String>> groupedOptions = new LinkedHashMap<>();
        String query = """
            SELECT nama_opsi, grup_opsi 
            FROM opsi_kustom 
            WHERE id_kategori = (SELECT id_kategori FROM produk WHERE id_produk = ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idProduk);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String grup = rs.getString("grup_opsi"); // Misal: "Suhu"
                String nama = rs.getString("nama_opsi"); // Misal: "Panas"

                if (!groupedOptions.containsKey(grup)) {
                    groupedOptions.put(grup, new ArrayList<>());
                }
                groupedOptions.get(grup).add(nama);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupedOptions;
    }
}