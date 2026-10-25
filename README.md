# Sistem Informasi Manajemen Data Mahasiswa (SIAKAD)
> **Desktop App · 2024 & Modern Web Interface**  
> Aplikasi manajemen data mahasiswa komprehensif berbasis **Java Swing (Desktop)** dan **Antarmuka Web Modern** yang terintegrasi dengan basis data relasional **MySQL**. Mendukung siklus penuh operasi CRUD (Create, Read, Update, Delete), autentikasi administrator, penyaringan multikriteria, audit log kueri, serta pelaporan data akademik.

---

## 🎨 Palet Identitas Visual
Aplikasi dirancang dengan panduan desain modern bertema institusi akademik terpadu:
- **Aegean Sky (`#1765B0`)**: Warna utama sistem (Sidebar, Header Jendela, Kartu Metrik, Background Layar Login).
- **Sandy Linen (`#EA4501`)**: Aksen dinamis (Tombol Aksi Utama, Highlight Prestasi, Tombol Tambah Mahasiswa).
- **Scarlet (`#D21625`)**: Aksen peringatan (Tombol Hapus Data, Peringatan Error, Indikator Predikat Cumlaude).

---

## 🌟 Fitur Utama

### 1. Autentikasi Administrator (Admin Login)
- **Desain Modern Split-Card**: Dilengkapi panel visual gedung arsitektur pendidikan (*Gedung Rektorat & Akademik UTN*) dan latar belakang warna *Aegean Sky*.
- **Kredensial Resmi**:
  - **Username**: `admin`
  - **Password**: `admin`
- **Fitur Form Login**:
  - Tombol *toggle visibility* password (ikon mata).
  - Tombol *One-Click Autofill* dengan kredensial default.
  - Penanganan error validasi autentikasi (*alert banner*).
  - Manajemen sesi berbasis `sessionStorage`.
  - Tombol **Keluar (Logout)** di *Sidebar* dan *Header Mobile*.
  - Dialog autentikasi modal native pada desktop Java (`LoginDialog.java`).

### 2. Operasi CRUD Penuh & Validasi Formulir
- **Create**: Registrasi mahasiswa baru dengan form modal interaktif.
- **Read**: Tampilan tabel pangkalan data dengan pagination dan preview KTM (Kartu Tanda Mahasiswa).
- **Update**: Perubahan data mahasiswa (Nama, Prodi, Fakultas, Angkatan, IPK, Email, Status, Alamat).
- **Delete**: Penghapusan data dengan modal dialog konfirmasi demi keamanan basis data.
- **Validasi Ketat**: Validasi format NIM, keunikan data, validitas email institusi, serta pembatasan IPK (0.00 – 4.00).

### 3. Analitik & Ringkasan Metrik Kampus
- Kartu statistik dinamis yang diperbarui secara langsung (*real-time*):
  - **Total Mahasiswa Terdaftar**
  - **Rata-rata IPK Kampus**
  - **Jumlah Mahasiswa Aktif & Rasio Persentase**
  - **Mahasiswa Berprestasi (Predikat Cumlaude: IPK ≥ 3.50)**

### 4. Pencarian, Filter & Pengurutan
- **Pencarian Cepat**: Pencarian fleksibel berdasarkan Nama Lengkap, NIM, maupun Alamat Email.
- **Filter Program Studi**: Teknik Informatika, Sistem Informasi, Rekayasa Perangkat Lunak, Sains Data, Teknik Elektro.
- **Filter Status Akademik**: Aktif, Cuti, Lulus.
- **Pengurutan Data**: Urutkan berdasarkan NIM (Asc/Desc), Nama (A-Z / Z-A), dan IPK (Tertinggi/Terendah).
- **Paginasi**: Pembagian halaman 10 data per halaman dengan kontrol navigasi halaman dinamis.

### 5. Dukungan Perangkat Mobile (Mobile-Ready)
- Tata letak responsif penuh (*fluid responsive layout*) di smartphone, tablet, maupun desktop.
- Header *sticky mobile* dengan *drawer menu hamburger* dan tombol *quick logout*.
- *Auto-close drawer* saat memilih menu navigasi atau filter prodi di ponsel.
- **Mobile Floating Action Button (FAB)** di sudut layar untuk registrasi cepat dengan satu ketukan jempol.
- Perlindungan *anti auto-zoom* pada iOS Safari dan Android Chrome saat mengetik di form.
- *Smooth horizontal touch scroll* pada tabel data.

### 6. Alat Bantu & Utilitas Tambahan
- **Digital KTM Preview**: Modal Kartu Tanda Mahasiswa dengan identitas lengkap dan QR/avatar unik.
- **Konsol Audit Kueri SQL**: Menampilkan log riwayat kueri (`SELECT`, `INSERT`, `UPDATE`, `DELETE`) dan status transaksi basis data.
- **Penampil Skema & Arsitektur Java**: Tab interaktif untuk melihat struktur `database.sql`, `Mahasiswa.java`, `DatabaseConnection.java`, `MahasiswaDAO.java`, dan `MainFrame.java`.
- **Ekspor Data**: Mendukung ekspor pangkalan data ke format file **CSV** dan **JSON**.
- **Cetak Laporan**: Template dokumen cetak resmi bebas sidebar/elemen navigasi menggunakan `window.print()`.

---

## 🏗️ Arsitektur Perangkat Lunak

Aplikasi dibangun menggunakan pola arsitektur **Model-View-Controller (MVC)** dan pola **Data Access Object (DAO)**:

```text
┌────────────────┐        ┌───────────────────────┐        ┌──────────────────────┐
│      VIEW      │ <----> │      CONTROLLER       │ <----> │      DAO / MODEL     │
│ MainFrame      │        │ MahasiswaController   │        │ MahasiswaDAO (Impl)  │
│ LoginDialog    │        │ (Validasi & Alur Data)│        │ Mahasiswa.java       │
│ FormMahasiswa  │        └───────────────────────┘        └──────────┬───────────┘
└────────────────┘                                                    │
                                                                      ▼
                                                           ┌──────────────────────┐
                                                           │   DATABASE ENGINE    │
                                                           │   MySQL (JDBC)       │
                                                           └──────────────────────┘
```

- **`Model` (`Mahasiswa.java`)**: Merepresentasikan entitas mahasiswa dengan atribut terenkapsulasi.
- **`DAO` (`MahasiswaDAO.java`, `MahasiswaDAOImpl.java`)**: Memisahkan logika persistensi SQL dengan *Prepared Statement* aman.
- **`Controller` (`MahasiswaController.java`)**: Menjembatani antarmuka dengan basis data serta menangani eksepsi.
- **`View` (`MainFrame.java`, `LoginDialog.java`, `FormMahasiswaDialog.java`)**: Komponen antarmuka pengguna grafis berbasis Java Swing.

---

## 📁 Struktur Direktori Proyek

```text
d:\apilkasi-manajemen-data-mahasiswa\
├── index.html                  # Antarmuka web portal akademik terpadu
├── style.css                   # Desain visual, tema warna, & optimasi mobile
├── app.js                      # Logika frontend, manajemen sesi, audit log, & CRUD
├── database/
│   └── schema.sql              # Skema tabel MySQL & data master awal
├── src/
│   └── com/
│       └── siakad/
│           ├── App.java                    # Main entry point Java Desktop
│           ├── config/
│           │   └── DatabaseConnection.java # Konfigurasi & koneksi JDBC MySQL
│           ├── controller/
│           │   └── MahasiswaController.java# Lapisan kendali alur data & validasi
│           ├── dao/
│           │   ├── MahasiswaDAO.java       # Interface kontrak akses database
│           │   └── MahasiswaDAOImpl.java   # Implementasi CRUD JDBC MySQL
│           ├── model/
│           │   └── Mahasiswa.java          # Kelas entitas objek mahasiswa
│           └── view/
│               ├── LoginDialog.java        # Modal autentikasi login admin (Swing)
│               ├── FormMahasiswaDialog.java# Dialog form tambah/edit mahasiswa
│               └── MainFrame.java          # Jendela workstation utama aplikasi
└── README.md                   # Dokumentasi teknis sistem
```

---

## 🚀 Petunjuk Menjalankan Aplikasi

### 1. Menjalankan Antarmuka Web
Cukup buka berkas `index.html` menggunakan peramban web modern (Google Chrome, Microsoft Edge, Safari, Firefox):
```bash
# Melalui PowerShell
Start-Process "index.html"
```
Gunakan kredensial berikut untuk masuk:
- **Username**: `admin`
- **Password**: `admin`

---

### 2. Menjalankan Aplikasi Desktop Java (Swing)

#### Prasyarat:
- **Java SE Development Kit (JDK) 17** atau yang lebih baru.
- **MySQL Server** aktif pada port default `3306`.
- Pustaka driver **`mysql-connector-j-8.0.33.jar`** (ditempatkan di folder `lib/`).

#### Langkah A: Inisialisasi Basis Data MySQL
Impor skema basis data ke MySQL Anda:
```bash
mysql -u root -p < database/schema.sql
```

#### Langkah B: Kompilasi Kode Sumber Java
```bash
# Buat direktori output jika belum ada
mkdir bin

# Kompilasi seluruh berkas Java
javac -cp ".;lib/mysql-connector-j-8.0.33.jar" -d bin src/com/siakad/model/*.java src/com/siakad/config/*.java src/com/siakad/dao/*.java src/com/siakad/controller/*.java src/com/siakad/view/*.java src/com/siakad/App.java
```

#### Langkah C: Menjalankan Aplikasi
```bash
java -cp "bin;lib/mysql-connector-j-8.0.33.jar" com.siakad.App
```
Aplikasi desktop akan menampilkan jendela **Login Administrator**. Masukkan username `admin` dan password `admin` untuk masuk ke jendela kerja utama.

---

## 🔒 Keamanan & Praktik Rekayasa Perangkat Lunak
- **Pencegahan SQL Injection**: Penggunaan parameter kueri `PreparedStatement` secara konsisten pada lapisan DAO.
- **Pencegahan Kebocoran Resource**: Implementasi blok `try-with-resources` untuk penutupan otomatis koneksi `Connection`, `PreparedStatement`, dan `ResultSet`.
- **Exception Handling Menyeluruh**: Penanganan `SQLException` dan `NumberFormatException` dengan umpan balik visual yang ramah pengguna.
- **Enkapsulasi & Modularitas**: Penerapan prinsip Object-Oriented Programming (OOP) yang ketat untuk kemudahan pemeliharaan (*maintainability*).

---

## 📄 Lisensi & Hak Cipta
© 2024 Biro Administrasi Akademik & Kemahasiswaan (BAAK) · Universitas Teknologi Nasional.  
Dikembangkan oleh **Fachril Akbar** · Portofolio Proyek Desktop & Web 2024.

---

## 📄 Lisensi & Hak Cipta
© 2024 Biro Administrasi Akademik & Kemahasiswaan (BAAK) · Universitas Teknologi Nasional.  
Dikembangkan oleh **Fachril Akbar** · Portofolio Proyek Desktop & Web 2024.

