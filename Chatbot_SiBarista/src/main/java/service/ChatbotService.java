package service;

import model.Produk;

import java.util.ArrayList;
import java.util.List;

public class ChatbotService {

    private final List<Produk> daftarProduk = new ArrayList<>();

    public ChatbotService() {
        initDummyData();
    }

    // =========================================================
    // DUMMY DATA (sementara untuk Day 2, nanti bisa diganti DB)
    // =========================================================
    private void initDummyData() {
        daftarProduk.add(new Produk("P001", "Latte", "Coffee",
                "Perpaduan espresso lembut dengan susu steamed.", 28000, "Tersedia"));

        daftarProduk.add(new Produk("P002", "Mocha", "Coffee",
                "Espresso dengan cokelat dan susu creamy.", 30000, "Tersedia"));

        daftarProduk.add(new Produk("P003", "Americano", "Coffee",
                "Espresso dengan tambahan air panas, ringan dan kuat.", 25000, "Tersedia"));

        daftarProduk.add(new Produk("P004", "Espresso", "Coffee",
                "Ekstrak kopi murni yang pekat dan beraroma.", 22000, "Tersedia"));

        daftarProduk.add(new Produk("P005", "Matcha Latte", "Non-Coffee",
                "Matcha premium dipadukan dengan susu creamy.", 32000, "Tersedia"));

        daftarProduk.add(new Produk("P006", "Signature Chocolate", "Non-Coffee",
                "Cokelat premium yang lembut dan kaya rasa.", 30000, "Tersedia"));

        daftarProduk.add(new Produk("P007", "Lychee Tea", "Non-Coffee",
                "Teh segar dengan aroma leci dan rasa manis ringan.", 26000, "Tersedia"));

        daftarProduk.add(new Produk("P008", "Orange Juice", "Jus",
                "Jus jeruk segar tanpa tambahan pengawet.", 24000, "Tersedia"));

        daftarProduk.add(new Produk("P009", "Mango Juice", "Jus",
                "Jus mangga manis dan segar.", 25000, "Tersedia"));

        daftarProduk.add(new Produk("P010", "Brownies", "Snack",
                "Brownies cokelat padat dengan tekstur lembut.", 20000, "Tersedia"));

        daftarProduk.add(new Produk("P011", "Butter Croissant", "Snack",
                "Pastry klasik renyah dengan aroma butter.", 22000, "Tersedia"));
    }

    // =========================================================
    // 1. prosesInput()  [WAJIB]
    // Method utama untuk memproses semua pesan user
    // =========================================================
    public String prosesInput(String pesan) {
        if (!validasiTeks(pesan)) {
            return "Silakan ketik pesan terlebih dahulu.";
        }

        String input = normalisasiInput(pesan);

        // sapaan
        if (input.equals("halo") || input.equals("hai") || input.equals("hi")
                || input.equals("selamat pagi") || input.equals("selamat siang")
                || input.equals("selamat sore") || input.equals("selamat malam")) {
            return balasanSapaan();
        }

        // bantuan / help
        if (input.equals("bantuan") || input.equals("help")
                || input.equals("tolong") || input.equals("cara pakai")
                || input.equals("harus ketik apa")) {
            return balasanBantuan();
        }

        // menu
        if (input.equals("menu") || input.equals("daftar menu") || input.equals("lihat menu")) {
            return balasanMenu();
        }

        // kategori
        if (input.equals("coffee")) {
            return balasanKategori("Coffee");
        }

        if (input.equals("non-coffee") || input.equals("non coffee")) {
            return balasanKategori("Non-Coffee");
        }

        if (input.equals("jus")) {
            return balasanKategori("Jus");
        }

        if (input.equals("snack")) {
            return balasanKategori("Snack");
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
                - Jus
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

        for (Produk produk : daftarProduk) {
            if (produk.getNamaKategori().equalsIgnoreCase(kategori)) {
                hasil.append("- ").append(produk.getNamaProduk()).append("\n");
                ditemukan = true;
            }
        }

        if (!ditemukan) {
            return "Maaf, kategori " + kategori + " belum memiliki produk.";
        }

        hasil.append("\nKetik nama produk untuk melihat detail.");
        return hasil.toString();
    }

    // =========================================================
    // 5. balasanDetail() [WAJIB]
    // Mengembalikan null jika tidak ditemukan
    // =========================================================
    public String balasanDetail(String namaMenu) {
        if (!validasiTeks(namaMenu)) {
            return null;
        }

        String input = normalisasiInput(namaMenu);

        // cari exact match dulu
        for (Produk produk : daftarProduk) {
            if (normalisasiInput(produk.getNamaProduk()).equals(input)) {
                return formatDetailProduk(produk);
            }
        }

        // cari contains match (lebih fleksibel)
        for (Produk produk : daftarProduk) {
            if (normalisasiInput(produk.getNamaProduk()).contains(input)) {
                return formatDetailProduk(produk);
            }
        }

        return null;
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
                - Jus
                - Snack
                - Latte
                - Brownies

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
                   - Jus
                   - Snack
                3. Ketik nama produk untuk melihat detail.
                   Contoh:
                   - Latte
                   - Espresso
                   - Brownies

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

    public List<Produk> getDaftarProduk() {
        return daftarProduk;
    }
}