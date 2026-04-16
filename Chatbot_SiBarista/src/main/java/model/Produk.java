package model;

public class Produk {
    private String idProduk;
    private String namaProduk;
    private String namaKategori;
    private String deskripsi;
    private Integer harga;
    private String statusstok;

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

    public Integer getHarga() {
        return harga;
    }

    public void setHarga(Integer harga) {
        this.harga = harga;
    }

    public String getStatusstok() {
        return statusstok;
    }

    public void setStatusstok(String statusstok) {
        this.statusstok = statusstok;
    }

    public String getDetailProduk(){
        return String.format("%s (%s) - %d [%s]", namaProduk,namaKategori, harga, statusstok);
    }
}
