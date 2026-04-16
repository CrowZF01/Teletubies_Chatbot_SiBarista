package model;

public class Produk {
    private String idProduk;
    private String namaProduk;
    private String namaKategori;
    private String deskripsi;
    private int harga;
    private String statusStok;

    // Constructor kosong
    public Produk() {
    }

    // Constructor lengkap
    public Produk(String idProduk, String namaProduk, String namaKategori, String deskripsi, int harga, String statusStok) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.namaKategori = namaKategori;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.statusStok = statusStok;
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

    public String getStatusStok() {
        return statusStok;
    }

    public void setStatusStok(String statusStok) {
        this.statusStok = statusStok;
    }

    public String getDetailProduk() {
        return String.format("%s (%s) - Rp%d [%s]", namaProduk, namaKategori, harga, statusStok);
    }
}