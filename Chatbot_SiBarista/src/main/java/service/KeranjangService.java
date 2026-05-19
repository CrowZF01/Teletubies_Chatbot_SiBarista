package service;

import model.Keranjang;
import model.Produk;
import java.util.ArrayList;
import java.util.List;

public class KeranjangService {

    private static KeranjangService instance;
    private KeranjangService() {}

    public static KeranjangService getInstance() {
        if (instance == null) {
            instance = new KeranjangService();
        }
        return instance;
    }

    private final List<Keranjang> items = new ArrayList<>();

    /**
     * Tambah produk ke keranjang dengan kustomisasi.
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
     * Kurangi jumlah produk berdasarkan kustomisasi spesifik.
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



    // --- Method lainnya tetap sama ---
    public void kosongkanKeranjang() { items.clear(); }
    public List<Keranjang> getItems() { return items; }
    public double getTotalHarga() { return items.stream().mapToDouble(Keranjang::getSubtotal).sum(); }
    public int getTotalJumlah() { return items.stream().mapToInt(Keranjang::getJumlah).sum(); }
}