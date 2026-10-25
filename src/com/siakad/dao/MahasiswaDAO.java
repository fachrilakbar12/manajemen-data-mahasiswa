package com.siakad.dao;

import com.siakad.model.Mahasiswa;
import java.util.List;

/**
 * Interface DAO (Data Access Object) untuk entitas Mahasiswa
 * Memisahkan logika bisnis dari akses database (Abstraction & Loose Coupling)
 * 
 * @author Fachril Akbar · Desktop App 2024
 */
public interface MahasiswaDAO {
    /**
     * Menyimpan data mahasiswa baru ke basis data (Create)
     * @param mahasiswa Objek mahasiswa yang akan disimpan
     * @return boolean true jika berhasil, false jika gagal
     */
    boolean insert(Mahasiswa mahasiswa);

    /**
     * Memperbarui data mahasiswa yang sudah ada (Update)
     * @param mahasiswa Objek mahasiswa dengan data terbaru
     * @return boolean true jika berhasil, false jika gagal
     */
    boolean update(Mahasiswa mahasiswa);

    /**
     * Menghapus data mahasiswa berdasarkan NIM (Delete)
     * @param nim Nomor Induk Mahasiswa yang akan dihapus
     * @return boolean true jika berhasil, false jika gagal
     */
    boolean delete(String nim);

    /**
     * Mencari mahasiswa berdasarkan NIM spesifik (Read)
     * @param nim Nomor Induk Mahasiswa
     * @return Objek Mahasiswa atau null jika tidak ditemukan
     */
    Mahasiswa findByNim(String nim);

    /**
     * Mengambil seluruh data mahasiswa (Read All)
     * @return List seluruh mahasiswa
     */
    List<Mahasiswa> findAll();

    /**
     * Fitur pencarian cepat dan filter dinamis berdasarkan kata kunci, prodi, dan status
     * @param keyword Kata kunci (NIM, Nama, Email)
     * @param prodi Filter Program Studi (kosong untuk semua)
     * @param status Filter Status (kosong untuk semua)
     * @return List mahasiswa hasil filter
     */
    List<Mahasiswa> searchAndFilter(String keyword, String prodi, String status);

    /**
     * Memeriksa apakah NIM sudah terdaftar di database
     * @param nim NIM yang diuji
     * @return boolean true jika sudah ada
     */
    boolean isNimExists(String nim);
}

