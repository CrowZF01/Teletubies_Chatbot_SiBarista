package model;

import java.util.List;

/**
 * Model data yang merepresentasikan satu baris item belanjaan di dalam keranjang (Cart Item Model).
 * 1. jumlah (Kuantitas belanjaan).
 * 2. kustomisasi (Daftar modifikasi rasa/suhu kopi yang dipilih).
 * Kelas ini juga melakukan kalkulasi bisnis lokal mandiri, seperti menghitung subtotal belanjaan.
 */
public class Keranjang {

    private Produk produk;
    private int jumlah;
    private List<String> kustomisasi;

    public Keranjang(Produk produk, int jumlah, List<String> kustomisasi) {
        this.produk = produk;
        this.jumlah = jumlah;
        this.kustomisasi = kustomisasi;
    }

    // Di class model.Keranjang


    // ── Getter & Setter ───────────────────────────────────────────────────────

    public Produk getProduk()          { return produk; }
    public void   setProduk(Produk p)  { this.produk = p; }

    public int  getJumlah()            { return jumlah; }

    public List<String> getKustomisasi() {
        return kustomisasi;
    }



    public double getSubtotal() {
        return produk.getHarga() * jumlah;
    }

    public void tambah() { this.jumlah++; }

    public void kurang() { if (this.jumlah > 0) this.jumlah--; }
}