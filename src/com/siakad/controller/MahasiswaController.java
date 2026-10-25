package com.siakad.controller;

import com.siakad.dao.MahasiswaDAO;
import com.siakad.dao.MahasiswaDAOImpl;
import com.siakad.model.Mahasiswa;

import java.util.List;

/**
 * Controller Layer: MahasiswaController
 * Menghubungkan View dengan DAO, memvalidasi aturan bisnis (Business Logic Validation),
 * dan menangani error secara terpusat (Centralized Exception Handling).
 * 
 * @author Fachril Akbar · Desktop App 2024
 */
public class MahasiswaController {
    private final MahasiswaDAO dao;

    public MahasiswaController() {
        this.dao = new MahasiswaDAOImpl();
    }

    public MahasiswaController(MahasiswaDAO dao) {
        this.dao = dao;
    }

    public List<Mahasiswa> getAllMahasiswa() {
        return dao.findAll();
    }

    public List<Mahasiswa> searchAndFilter(String keyword, String prodi, String status) {
        return dao.searchAndFilter(keyword, prodi, status);
    }

    public Mahasiswa getMahasiswaByNim(String nim) {
        if (nim == null || nim.trim().isEmpty()) {
            return null;
        }
        return dao.findByNim(nim.trim());
    }

    public void tambahMahasiswa(Mahasiswa m) throws IllegalArgumentException {
        validateMahasiswa(m);

        if (dao.isNimExists(m.getNim())) {
            throw new IllegalArgumentException("Mahasiswa dengan NIM " + m.getNim() + " sudah terdaftar di database!");
        }

        boolean success = dao.insert(m);
        if (!success) {
            throw new RuntimeException("Gagal menyimpan data mahasiswa ke MySQL. Periksa koneksi database!");
        }
    }

    public void perbaruiMahasiswa(Mahasiswa m) throws IllegalArgumentException {
        validateMahasiswa(m);

        if (!dao.isNimExists(m.getNim())) {
            throw new IllegalArgumentException("Data mahasiswa dengan NIM " + m.getNim() + " tidak ditemukan!");
        }

        boolean success = dao.update(m);
        if (!success) {
            throw new RuntimeException("Gagal memperbarui data mahasiswa di MySQL!");
        }
    }

    public void hapusMahasiswa(String nim) throws IllegalArgumentException {
        if (nim == null || nim.trim().isEmpty()) {
            throw new IllegalArgumentException("NIM tidak boleh kosong!");
        }

        if (!dao.isNimExists(nim.trim())) {
            throw new IllegalArgumentException("Mahasiswa dengan NIM " + nim + " tidak ditemukan!");
        }

        boolean success = dao.delete(nim.trim());
        if (!success) {
            throw new RuntimeException("Gagal menghapus data mahasiswa dari database!");
        }
    }

    private void validateMahasiswa(Mahasiswa m) {
        if (m == null) {
            throw new IllegalArgumentException("Data mahasiswa tidak boleh bernilai null!");
        }

        if (m.getNim() == null || !m.getNim().matches("^[0-9]{8,18}$")) {
            throw new IllegalArgumentException("NIM harus berupa angka antara 8 hingga 18 digit!");
        }

        if (m.getNama() == null || m.getNama().trim().length() < 3) {
            throw new IllegalArgumentException("Nama lengkap minimal harus 3 karakter!");
        }

        if (m.getProdi() == null || m.getProdi().trim().isEmpty()) {
            throw new IllegalArgumentException("Program studi wajib dipilih!");
        }

        if (m.getIpk() < 0.00 || m.getIpk() > 4.00) {
            throw new IllegalArgumentException("IPK harus berada di rentang 0.00 hingga 4.00!");
        }

        if (m.getEmail() == null || !m.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Format email tidak valid!");
        }
    }
}

