package model;

public class Produk {
    private String idProduk;
    private String namaProduk;
    private String namaKategori;
    private String deskripsi;
    private int harga;
    private String statusStok;
    private String gambar;

    // Constructor kosong
    public Produk() {
    }

    // Constructor lengkap
    public Produk(String idProduk, String namaProduk, String namaKategori, String deskripsi, int harga, String statusStok, String gambar) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.namaKategori = namaKategori;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.statusStok = statusStok;
        this.gambar = gambar;
    }

    public String getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getStatusStok() {
        return statusStok;
    }

    public void setStatusStok(String statusStok) {
        this.statusStok = statusStok;
    }

    public String getDetailProduk() {
        return String.format("%s (%s) - Rp%d [%s]", namaProduk, namaKategori, harga, statusStok);
    }

    // Method tambahan untuk mempercantik tampilan di Tabel atau UI lainnya
    public String getHargaFormatted() {
        return String.format("Rp%,d", harga).replace(',', '.');
    }

    // Tambahkan toString() untuk memudahkan debugging di console
    @Override
    public String toString() {
        return "Produk{" +
                "id='" + idProduk + '\'' +
                ", nama='" + namaProduk + '\'' +
                ", kategori='" + namaKategori + '\'' +
                '}';
    }
}