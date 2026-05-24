package model;

/**
 * Model data yang merepresentasikan opsi kustomisasi rasa/suhu produk (Customization Option Model).
 * Kelas OpsiKustom ini memetakan data dari tabel database 'opsi_kustom'.
 * Digunakan untuk menyimpan informasi modifikasi menu (misal, Grup: "Suhu", Opsi: "Panas", "Dingin") 
 * yang terkait secara relasional dengan ID Kategori Coffee.
 */
public class OpsiKustom {
    private int idOpsi;
    private String namaOpsi;
    private String grupOpsi;
    private int idKategori;

    public OpsiKustom() {}

    public OpsiKustom(int idOpsi, String namaOpsi, String grupOpsi, int idKategori) {
        this.idOpsi = idOpsi;
        this.namaOpsi = namaOpsi;
        this.grupOpsi = grupOpsi;
        this.idKategori = idKategori;
    }

    // Getter dan Setter
    public int getIdOpsi() { return idOpsi; }

    public void setIdOpsi(int idOpsi) {
        this.idOpsi = idOpsi;
    }

    public String getNamaOpsi() {
        return namaOpsi;
    }

    public void setNamaOpsi(String namaOpsi) {
        this.namaOpsi = namaOpsi;
    }

    public String getGrupOpsi() {
        return grupOpsi;
    }

    public void setGrupOpsi(String grupOpsi) {
        this.grupOpsi = grupOpsi;
    }

    public int getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(int idKategori) {
        this.idKategori = idKategori;
    }
}