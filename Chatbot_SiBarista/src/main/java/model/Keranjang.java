package model;

import java.util.List;

/**
 * Model satu item di dalam keranjang belanja.
 * Menyimpan referensi ke Produk dan jumlah (quantity).
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
    public void setJumlah(int jumlah)  { this.jumlah = jumlah; }

    public List<String> getKustomisasi() {
        return kustomisasi;
    }

    public void setKustomisasi(List<String> kustomisasi) {
        this.kustomisasi = kustomisasi;
    }

    // ── Kalkulasi ─────────────────────────────────────────────────────────────

    /** Subtotal = harga × jumlah. */
    public double getSubtotal() {
        return produk.getHarga() * jumlah;
    }

    /** Tambah jumlah 1. */
    public void tambah() { this.jumlah++; }

    /** Kurangi jumlah 1 (minimal 0). */
    public void kurang() { if (this.jumlah > 0) this.jumlah--; }
}