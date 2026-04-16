package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private String url = "jdbc:mysql://localhost:3306/chatbot";
    private String username = "root" ;
    private String password = "";

    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, username, password);
    }

    public void cekKoneksi() {
        try(Connection conn = getConnection()) {
            System.out.println("Terhubung ke Database");
        }catch (SQLException e ){
            System.out.println("Koneksi gagal : " + e.getMessage());
        }
    }
}
