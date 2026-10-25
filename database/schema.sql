-- ====================================================================
-- Database Schema: db_mahasiswa
-- Aplikasi Manajemen Data Mahasiswa (Desktop App · 2024)
-- Konektivitas: MySQL via JDBC (Java Database Connectivity)
-- ====================================================================

CREATE DATABASE IF NOT EXISTS db_mahasiswa
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE db_mahasiswa;

-- Hapus tabel lama jika sudah ada (opsional untuk instalasi bersih)
DROP TABLE IF EXISTS mahasiswa;

-- Tabel Utama: mahasiswa
CREATE TABLE mahasiswa (
    nim VARCHAR(20) NOT NULL PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    prodi VARCHAR(50) NOT NULL,
    fakultas VARCHAR(50) NOT NULL,
    angkatan INT NOT NULL,
    ipk DECIMAL(3,2) NOT NULL,
    email VARCHAR(100) NOT NULL,
    status ENUM('Aktif', 'Cuti', 'Lulus') NOT NULL DEFAULT 'Aktif',
    alamat TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraint validasi
    CONSTRAINT chk_ipk CHECK (ipk >= 0.00 AND ipk <= 4.00),
    CONSTRAINT chk_angkatan CHECK (angkatan >= 2000 AND angkatan <= 2099)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indeks untuk mempercepat operasi pencarian dan filter
CREATE INDEX idx_mahasiswa_nama ON mahasiswa(nama);
CREATE INDEX idx_mahasiswa_prodi ON mahasiswa(prodi);
CREATE INDEX idx_mahasiswa_status ON mahasiswa(status);
CREATE INDEX idx_mahasiswa_angkatan ON mahasiswa(angkatan);

-- Data Awal (Sample Records)
INSERT INTO mahasiswa (nim, nama, prodi, fakultas, angkatan, ipk, email, status, alamat) VALUES
('24060122140001', 'Fachril Akbar', 'Teknik Informatika', 'Sains & Matematika', 2024, 3.88, 'fachril.akbar@students.ac.id', 'Aktif', 'Jl. Tembalang Baru No. 12, Semarang'),
('24060122140002', 'Aisyah Putri Rahma', 'Sistem Informasi', 'Ilmu Komputer', 2024, 3.92, 'aisyah.putri@students.ac.id', 'Aktif', 'Jl. Banjarsari Selatan No. 5, Semarang'),
('24060122130045', 'Bima Sakti Nugraha', 'Rekayasa Perangkat Lunak', 'Ilmu Komputer', 2023, 3.65, 'bima.sakti@students.ac.id', 'Aktif', 'Jl. Prof. Soedarto No. 8, Semarang'),
('24060122120019', 'Citra Dewi Anggraini', 'Sains Data', 'Sains & Matematika', 2022, 3.82, 'citra.dewi@students.ac.id', 'Aktif', 'Jl. Tirto Agung No. 21, Semarang'),
('24060122140088', 'Dimas Arya Pratama', 'Teknik Elektro', 'Teknik', 2024, 3.42, 'dimas.arya@students.ac.id', 'Aktif', 'Jl. Jatingaleh Raya No. 44, Semarang'),
('24060122130090', 'Farhan Maulana', 'Teknik Informatika', 'Sains & Matematika', 2023, 3.76, 'farhan.m@students.ac.id', 'Aktif', 'Jl. Ngesrep Timur V No. 10, Semarang'),
('24060122110033', 'Gita Permatasari', 'Sistem Informasi', 'Ilmu Komputer', 2021, 3.95, 'gita.permata@students.ac.id', 'Lulus', 'Jl. Pleburan Barat No. 3, Semarang'),
('24060122120054', 'Hendra Wijaya', 'Rekayasa Perangkat Lunak', 'Ilmu Komputer', 2022, 2.95, 'hendra.w@students.ac.id', 'Cuti', 'Jl. Sumurboto Raya No. 15, Semarang'),
('24060122140112', 'Indah Kusuma Wardani', 'Sains Data', 'Sains & Matematika', 2024, 3.71, 'indah.kusuma@students.ac.id', 'Aktif', 'Jl. Gondang Barat No. 27, Semarang'),
('24060122130071', 'Kevin Jonathan', 'Teknik Elektro', 'Teknik', 2023, 3.58, 'kevin.j@students.ac.id', 'Aktif', 'Jl. Sirojudin No. 19, Semarang');

