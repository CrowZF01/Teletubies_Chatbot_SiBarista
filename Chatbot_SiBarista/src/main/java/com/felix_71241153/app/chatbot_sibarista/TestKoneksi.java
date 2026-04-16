package com.felix_71241153.app.chatbot_sibarista;


import database.Database;

public class TestKoneksi {
    public static void main(String[] args) {
        Database db = new Database();
        db.cekKoneksi();
    }
}

