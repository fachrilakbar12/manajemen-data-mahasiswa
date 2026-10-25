package com.siakad.model;

/**
 * Model Class: Mahasiswa
 * Menerapkan prinsip Object-Oriented Programming (OOP):
 * - Encapsulation (data hiding menggunakan private field dan public getter/setter)
 * - Constructor Overloading
 * - Validasi domain logic internal
 * 
 * @author Fachril Akbar · Desktop App 2024
 */
public class Mahasiswa {
    private String nim;
    private String nama;
    private String prodi;
    private String fakultas;
    private int angkatan;
    private double ipk;
    private String email;
    private String status;
    private String alamat;

    // Default Constructor
    public Mahasiswa() {
        this.status = "Aktif";
    }

    // Parameterized Constructor
    public Mahasiswa(String nim, String nama, String prodi, String fakultas,
                     int angkatan, double ipk, String email, String status, String alamat) {
        this.nim = nim != null ? nim.trim() : "";
        this.nama = nama != null ? nama.trim() : "";
        this.prodi = prodi != null ? prodi.trim() : "";
        this.fakultas = fakultas != null ? fakultas.trim() : "";
        this.angkatan = angkatan;
        this.ipk = ipk;
        this.email = email != null ? email.trim() : "";
        this.status = status != null ? status.trim() : "Aktif";
        this.alamat = alamat != null ? alamat.trim() : "";
    }

    // Getters and Setters (Encapsulation)
    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim != null ? nim.trim() : "";
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama != null ? nama.trim() : "";
    }

    public String getProdi() {
        return prodi;
    }

    public void setProdi(String prodi) {
        this.prodi = prodi != null ? prodi.trim() : "";
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas != null ? fakultas.trim() : "";
    }

    public int getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(int angkatan) {
        this.angkatan = angkatan;
    }

    public double getIpk() {
        return ipk;
    }

    public void setIpk(double ipk) {
        this.ipk = ipk;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.trim() : "Aktif";
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat != null ? alamat.trim() : "";
    }

    // Domain Business Helper
    public boolean isCumlaude() {
        return this.ipk >= 3.50;
    }

    @Override
    public String toString() {
        return "Mahasiswa{" +
                "nim='" + nim + '\'' +
                ", nama='" + nama + '\'' +
                ", prodi='" + prodi + '\'' +
                ", ipk=" + ipk +
                ", status='" + status + '\'' +
                '}';
    }
}

