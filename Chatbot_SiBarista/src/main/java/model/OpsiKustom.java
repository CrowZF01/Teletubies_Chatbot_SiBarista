package model;

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

}