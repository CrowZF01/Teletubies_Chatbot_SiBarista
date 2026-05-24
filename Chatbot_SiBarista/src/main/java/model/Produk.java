package model;

/**
 * Model data yang merepresentasikan entitas menu kafe (Produk Model).
 * Kelas ini berfungsi sebagai penampung
 * data (Data Transfer Object) untuk mempermudah transfer informasi produk dari database 
 * menuju user interface JavaFX. Atribut di dalamnya memetakan kolom-kolom tabel 'produk' 
 * seperti id_produk, nama_produk, kategori, deskripsi, harga, stok, dan gambar.
 */
public class Produk {
    private String idProduk;
    private String namaProduk;
    private String namaKategori;
    private String deskripsi;
    private int harga;
    private String statusStok;
    private String gambar;

    public Produk() {
    }

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
    public String getHargaFormatted() {
        return String.format("Rp%,d", harga).replace(',', '.');
    }

    @Override
    public String toString() {
        return "Produk{" +
                "id='" + idProduk + '\'' +
                ", nama='" + namaProduk + '\'' +
                ", kategori='" + namaKategori + '\'' +
                '}';
    }
}