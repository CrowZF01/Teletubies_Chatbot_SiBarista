package service;

import database.Database;
import model.Produk;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public boolean login(String username, String password) {
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean simpanProduk(Produk p, boolean isEdit) {
        String query;
        if (isEdit) {
            query = "UPDATE produk SET nama_produk=?, id_kategori=(SELECT id_kategori FROM kategori WHERE nama_kategori=?), deskripsi=?, harga=?, status_stok=?, gambar=? WHERE id_produk=?";
        } else {
            query = "INSERT INTO produk (nama_produk, id_kategori, deskripsi, harga, status_stok, gambar) VALUES (?, (SELECT id_kategori FROM kategori WHERE nama_kategori=?), ?, ?, ?, ?)";
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, p.getNamaProduk());
            pstmt.setString(2, p.getNamaKategori()); // Mencari ID berdasarkan Nama Kategori
            pstmt.setString(3, p.getDeskripsi());
            pstmt.setInt(4, p.getHarga());
            pstmt.setString(5, p.getStatusStok());
            pstmt.setString(6, p.getGambar());

            if (isEdit){
                pstmt.setString(7, p.getIdProduk());
            }

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hapusProduk(String idProduk) {
        String query = "DELETE FROM produk WHERE id_produk = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, idProduk);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}