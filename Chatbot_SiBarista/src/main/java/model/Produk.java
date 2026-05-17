package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import database.Database;

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


    public List<Produk> getAllProduk() throws SQLException {
        List<Produk> listProduk = new ArrayList<>();
        String query = "SELECT * FROM produk";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Produk p = new Produk();
                p.setIdProduk(rs.getString("id_produk"));
                p.setNamaProduk(rs.getString("nama_produk"));
                p.setNamaKategori(rs.getString("id_kategori"));
                p.setDeskripsi(rs.getString("deskripsi"));
                p.setHarga(rs.getInt("harga"));
                p.setStatusStok(rs.getString("status_stok"));
                p.setGambar(rs.getString("gambar"));

                listProduk.add(p);
            }
        }
        return listProduk;
    }

    public List<Produk> getProdukByKategori(int idKategori) throws SQLException {
        List<Produk> listProduk = new ArrayList<>();
        String query = "SELECT * FROM produk WHERE id_kategori = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idKategori);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produk p = new Produk();
                    p.setIdProduk(rs.getString("id_produk"));
                    p.setNamaProduk(rs.getString("nama_produk"));
                    p.setNamaKategori(rs.getString("id_kategori"));
                    p.setDeskripsi(rs.getString("deskripsi"));
                    p.setHarga(rs.getInt("harga"));
                    p.setStatusStok(rs.getString("status_stok"));
                    p.setGambar(rs.getString("gambar"));

                    listProduk.add(p);
                }
            }
        }
        return listProduk;
    }
}