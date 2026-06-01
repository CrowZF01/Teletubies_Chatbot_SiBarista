# 🍽️ SiBarista - Aplikasi Chatbot Penelusuran & Pemesanan Menu Kafe

[![Java Version](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![JavaFX Version](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)
[![Build Tool](https://img.shields.io/badge/Maven-3.8%2B-red.svg)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/Database-MySQL-blue.svg)](https://www.mysql.com/)

**SiBarista** adalah aplikasi desktop modern berbasis **JavaFX** yang dirancang sebagai asisten virtual (chatbot) interaktif untuk membantu pelanggan menjelajah menu, melihat detail menu, serta melakukan pemesanan (manajemen keranjang belanja) secara instan dan dinamis. Aplikasi ini juga dilengkapi dengan panel administrasi khusus untuk mengelola inventaris menu dan opsi kustomisasi produk.

Aplikasi ini mengadopsi standar arsitektur perangkat lunak yang bersih menggunakan **MVC (Model-View-Controller)**, pemisahan logika bisnis menggunakan **Service Layer** dengan **Singleton Pattern**, serta utilitas koneksi database JDBC MySQL yang modular.

---

## ✨ Fitur Utama

Aplikasi SiBarista terbagi ke dalam empat pilar fitur utama:

### 💬 1. Asisten Virtual Chatbot Pintar (SiBarista)
*   **Pencarian Menu & Detail Instan:** Pelanggan dapat berinteraksi secara real-time dengan mengetikkan sapaan, kata kunci bantuan (`Help`), kategori menu, atau nama produk tertentu untuk melihat detailnya secara otomatis.
*   **Natural Language Processing (NLP) Sederhana:** Pemrosesan teks input menggunakan mekanisme normalisasi (menghilangkan spasi ganda, tanda baca, dan mengubah ke huruf kecil) untuk pencocokan pola (*pattern matching*) yang akurat.
*   **Rekomendasi Cerdas:** Menyajikan rekomendasi menu secara acak (*random recommendations*) dari database MySQL berdasarkan ketersediaan stok produk teraktif (`status_stok = 'Tersedia'`).

### 🛒 2. State Management Keranjang Belanja Global
*   **Pola Desain Singleton:** Menggunakan `KeranjangService` global yang sinkron antara panel chat, detail produk, dan daftar keranjang belanja.
*   **Kustomisasi Pemesanan Kopi:** Menambahkan produk ke keranjang belanja dengan opsi kustomisasi dinamis (misalnya opsi "Suhu: Panas/Dingin" untuk kategori Coffee).
*   **Penggabungan Kuantitas Cerdas:** Jika produk dengan kustomisasi yang sama ditambahkan lagi, kuantitas item otomatis bertambah (*quantity++*) alih-alih membuat baris baru di keranjang.
*   **Manajemen Kuantitas Interaktif:** Pelanggan dapat menambah, mengurangi, atau mengosongkan item langsung pada antarmuka keranjang belanja dengan kalkulasi subtotal dan total harga secara real-time.

### 🔐 3. Autentikasi Admin & Proteksi Sesi
*   **Login Administrator:** Keamanan login terenkripsi menggunakan algoritma hashing **BCrypt** untuk kredensial administrator.
*   **Gerbang Akses Terproteksi:** Memisahkan hak akses pelanggan biasa (chat & keranjang) dengan hak akses khusus pengelolaan data kafe (Dashboard Admin).

### ⚙️ 4. Dasbor Pengelolaan Menu & Opsi Kustom (CRUD Admin)
*   **Operasi CRUD Produk Lengkap:** Memungkinkan admin untuk menambah, membaca, memperbarui (Edit), dan menghapus (Delete) menu produk beserta status stok dan gambar.
*   **Manajemen Kustomisasi Dinamis:** Admin dapat menambah atau menghapus pilihan opsi kustomisasi (seperti grup opsi "Suhu") khusus untuk kategori produk tertentu.
*   **Pencegahan Nama Kembar:** Validasi database secara real-time untuk mendeteksi kesamaan nama produk guna menjaga konsistensi data menu.

---

## 🛠️ Spesifikasi Teknologi (Tech Stack)

*   **Bahasa Pemrograman:** Java (JDK 25 ke atas)
*   **Framework GUI:** JavaFX 21+ & OpenJFX (FXML, CSS Styling, & BootstrapFX)
*   **Manajemen Dependensi:** Maven
*   **Database:** MySQL (dengan Driver JDBC `mysql-connector-j` 8.3.0)
*   **Keamanan Hashing:** BCrypt (`jbcrypt` 0.4)
*   **Desain Pattern:** MVC, Service Layer (Singleton Pattern), & OOP modular

---

## 📂 Struktur Direktori Proyek

```bash
Teletubies_Chatbot_SiBarista/
│
├── Chatbot_SiBarista/                  # Modul utama aplikasi Maven
│   ├── .mvn/
│   ├── src/main/java/
│   │   ├── com/felix_71241153/app/chatbot_sibarista/
│   │   │   ├── Launcher.java           # Launcher alternatif untuk classpath JavaFX
│   │   │   ├── MainApp.java            # Entrypoint utama aplikasi JavaFX
│   │   │   └── TestKoneksi.java        # Utilitas penguji koneksi database
│   │   │
│   │   ├── controller/                 # Logika interaksi Antarmuka (UI Controllers)
│   │   │   ├── ChatBotController.java  # Controller antarmuka chatbot
│   │   │   ├── DashBoardController.java # Controller dashboard admin
│   │   │   ├── KeranjangController.java # Controller keranjang belanja
│   │   │   ├── LoginController.java    # Controller login admin
│   │   │   ├── OpsiFormController.java # Controller form opsi kustom
│   │   │   └── ProductFormController.java # Controller form tambah/edit produk
│   │   │
│   │   ├── database/                   # Kelas Manajemen Database
│   │   │   ├── Database.java           # Utilitas koneksi database JDBC MySQL
│   │   │   └── GeneratePasswordHash.java # Utilitas generator hash password admin
│   │   │
│   │   ├── model/                      # Class Representasi Objek (POJO)
│   │   │   ├── Keranjang.java          # Model item keranjang belanja
│   │   │   ├── OpsiKustom.java         # Model opsi kustomisasi produk
│   │   │   └── Produk.java             # Model menu produk
│   │   │
│   │   └── service/                    # Business Logic Layer (Singleton Services)
│   │       ├── AdminService.java       # Pengendali autentikasi & CRUD admin
│   │       ├── ChatbotService.java     # Pengendali NLP chatbot & pencarian kata kunci
│   │       └── KeranjangService.java   # Pengendali status keranjang belanja global
│   │
│   │   └── module-info.java            # Deskriptor modular Java Platform Module System (JPMS)
│   │
│   ├── src/main/resources/
│   │   ├── com/felix_71241153/app/chatbot_sibarista/  # Berkas XML Desain GUI (.fxml)
│   │   └── images/                     # Direktori penyimpanan aset gambar produk
│   │
│   ├── pom.xml                         # Berkas konfigurasi dependensi Maven
│   └── mvnw
│
└── README.md                           # Berkas dokumentasi proyek (berkas ini!)
```

---

## 🚀 Cara Menjalankan Proyek di Lokal

### 1. Prasyarat Sistem
*   Java Development Kit (JDK) 25 atau versi terbaru.
*   Apache Maven terpasang di sistem Anda.
*   XAMPP / MySQL Server aktif.

### 2. Konfigurasi Database MySQL
1.  Aktifkan MySQL di control panel XAMPP Anda.
2.  Buka browser lalu akses `http://localhost/phpmyadmin/`.
3.  Buat database baru bernama **`sibarista`**.
4.  Import skema tabel database (tabel `admin`, `kategori`, `produk`, dan `opsi_kustom`).
5.  *(Opsional)* Anda dapat menyesuaikan port, username, dan password database di berkas:
    `Chatbot_SiBarista/src/main/java/database/Database.java`

### 3. Kompilasi & Jalankan Aplikasi
Buka terminal/command prompt di direktori root proyek ini, masuk ke direktori modul utama, kemudian jalankan perintah Maven berikut:

```bash
# Masuk ke direktori modul utama
cd Chatbot_SiBarista

# Bersihkan proyek dan kompilasi ulang kelas
mvn clean compile

# Jalankan aplikasi JavaFX
mvn javafx:run
```

---

## 👥 Tim Pengembang (Teletubies)
Aplikasi SiBarista ini dikembangkan dengan dedikasi tinggi oleh tim **Teletubies** melalui metode kolaborasi pembagian tugas pengerjaan yang terstruktur.
