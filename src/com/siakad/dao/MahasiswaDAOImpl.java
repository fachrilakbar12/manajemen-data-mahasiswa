package com.siakad.dao;

import com.siakad.config.DatabaseConnection;
import com.siakad.model.Mahasiswa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi MahasiswaDAO menggunakan JDBC dan MySQL
 * Menerapkan PreparedStatement untuk mencegah SQL Injection dan try-with-resources untuk mencegah memory leak.
 * 
 * @author Fachril Akbar · Desktop App 2024
 */
public class MahasiswaDAOImpl implements MahasiswaDAO {

    @Override
    public boolean insert(Mahasiswa m) {
        String sql = "INSERT INTO mahasiswa (nim, nama, prodi, fakultas, angkatan, ipk, email, status, alamat) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNim());
            ps.setString(2, m.getNama());
            ps.setString(3, m.getProdi());
            ps.setString(4, m.getFakultas());
            ps.setInt(5, m.getAngkatan());
            ps.setDouble(6, m.getIpk());
            ps.setString(7, m.getEmail());
            ps.setString(8, m.getStatus());
            ps.setString(9, m.getAlamat());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("❌ [DAO Error] Gagal insert mahasiswa: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Mahasiswa m) {
        String sql = "UPDATE mahasiswa SET nama = ?, prodi = ?, fakultas = ?, angkatan = ?, " +
                     "ipk = ?, email = ?, status = ?, alamat = ? WHERE nim = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNama());
            ps.setString(2, m.getProdi());
            ps.setString(3, m.getFakultas());
            ps.setInt(4, m.getAngkatan());
            ps.setDouble(5, m.getIpk());
            ps.setString(6, m.getEmail());
            ps.setString(7, m.getStatus());
            ps.setString(8, m.getAlamat());
            ps.setString(9, m.getNim());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("❌ [DAO Error] Gagal update data mahasiswa NIM " + m.getNim() + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String nim) {
        String sql = "DELETE FROM mahasiswa WHERE nim = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nim);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("❌ [DAO Error] Gagal delete mahasiswa NIM " + nim + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public Mahasiswa findByNim(String nim) {
        String sql = "SELECT * FROM mahasiswa WHERE nim = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMahasiswa(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [DAO Error] Gagal mencari mahasiswa NIM " + nim + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Mahasiswa> findAll() {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa ORDER BY nim ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToMahasiswa(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ [DAO Error] Gagal mengambil seluruh data mahasiswa: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Mahasiswa> searchAndFilter(String keyword, String prodi, String status) {
        List<Mahasiswa> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM mahasiswa WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (nim LIKE ? OR nama LIKE ? OR email LIKE ?)");
            String pattern = "%" + keyword.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (prodi != null && !prodi.trim().isEmpty()) {
            sql.append(" AND prodi = ?");
            params.add(prodi.trim());
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status.trim());
        }

        sql.append(" ORDER BY nim ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMahasiswa(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [DAO Error] Gagal melakukan search/filter: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean isNimExists(String nim) {
        String sql = "SELECT 1 FROM mahasiswa WHERE nim = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("❌ [DAO Error] Gagal cek keberadaan NIM: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method mapping ResultSet ke instance entitas Mahasiswa (OOP Mapping)
     */
    private Mahasiswa mapResultSetToMahasiswa(ResultSet rs) throws SQLException {
        return new Mahasiswa(
            rs.getString("nim"),
            rs.getString("nama"),
            rs.getString("prodi"),
            rs.getString("fakultas"),
            rs.getInt("angkatan"),
            rs.getDouble("ipk"),
            rs.getString("email"),
            rs.getString("status"),
            rs.getString("alamat")
        );
    }
}

