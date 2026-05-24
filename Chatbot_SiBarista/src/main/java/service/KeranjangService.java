package service;

import model.Keranjang;
import model.Produk;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer yang mengelola status keranjang belanja secara global (Cart State Manager).
 * KeranjangService didesain dengan pola Singleton (getInstance()) untuk memastikan bahwa hanya ada 
 * satu objek keranjang belanja yang aktif sepanjang aplikasi berjalan. Dengan begitu, data keranjang 
 * yang diakses oleh ChatBotController dan KeranjangController tetap sinkron dan konsisten.
 */
public class KeranjangService {

    private static KeranjangService instance;
    private KeranjangService() {}

    public static KeranjangService getInstance() {
        if (instance == null) {
            instance = new KeranjangService();
        }
        return instance;
    }

    // List penampung item belanja
    private final List<Keranjang> items = new ArrayList<>();

    /**
     * Menambahkan produk ke dalam keranjang dengan mempertimbangkan kustomisasi.
     * Logika penggabungan barang:
     * Jika nama produk DAN daftar kustomisasinya sama persis dengan yang sudah ada di keranjang,
     * sistem cukup menambah kuantitas jumlahnya (quantity++), bukan membuat baris baru.
     */
    public void tambahProduk(Produk produk, List<String> kustomisasi) {
        for (Keranjang item : items) {
            // Cek apakah produk DAN kustomisasinya SAMA PERSIS
            if (item.getProduk().getNamaProduk().equalsIgnoreCase(produk.getNamaProduk()) &&
                    item.getKustomisasi().equals(kustomisasi)) {

                item.tambah(); // Jika sama persis, cukup tambah jumlahnya
                return;
            }
        }
        // Jika beda produk atau beda kustomisasi, buat baris baru
        items.add(new Keranjang(produk, 1, kustomisasi));
    }

    /**
     * Mengurangi kuantitas produk di keranjang berdasarkan kustomisasi spesifik.
     * Jika jumlah item menjadi 0 setelah dikurangi, item tersebut akan otomatis dihapus dari keranjang.
     */
    public void kurangiProduk(Produk produk, List<String> kustomisasi) {
        items.removeIf(item -> {
            if (item.getProduk().getNamaProduk().equalsIgnoreCase(produk.getNamaProduk()) &&
                    item.getKustomisasi().equals(kustomisasi)) {

                item.kurang();
                return item.getJumlah() == 0;
            }
            return false;
        });
    }

    public void kosongkanKeranjang() { items.clear(); }
    
    public List<Keranjang> getItems() { return items; }

    public double getTotalHarga() { return items.stream().mapToDouble(Keranjang::getSubtotal).sum(); }
    
    public int getTotalJumlah() { return items.stream().mapToInt(Keranjang::getJumlah).sum(); }
}