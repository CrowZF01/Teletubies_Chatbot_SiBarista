package model;

public class Admin {
    private String username;
    private String password;

    // Tambahkan Constructor Kosong (Penting untuk framework tertentu)
    public Admin() {}

    // Tambahkan Constructor Lengkap
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter dan Setter tetap ada...
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}