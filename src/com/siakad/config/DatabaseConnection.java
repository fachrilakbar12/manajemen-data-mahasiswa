package com.siakad.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Konfigurasi Koneksi Database MySQL menggunakan JDBC (Java Database Connectivity)
 * Menerapkan Design Pattern: Singleton Pattern untuk efisiensi resource connection pool.
 * Dilengkapi dengan Exception Handling menyeluruh.
 * 
 * @author Fachril Akbar · Desktop App 2024
 */
public class DatabaseConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "db_mahasiswa";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    // JDBC URL with character encoding and timezone parameters
    private static final String JDBC_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + 
                                          "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    
    private static Connection connection = null;

    // Private constructor to prevent direct instantiation (Singleton)
    private DatabaseConnection() {}

    /**
     * Mendapatkan instance koneksi tunggal ke MySQL.
     * Menguji validitas koneksi dan melakukan reconnect otomatis jika terputus.
     * 
     * @return Connection JDBC instance
     * @throws SQLException jika koneksi gagal
     */
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Register MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish connection
                connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                System.out.println("✅ [JDBC] Berhasil terhubung ke database MySQL: " + DATABASE);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ [JDBC Error] Driver MySQL Connector/J tidak ditemukan di classpath!");
            System.err.println("Pastikan mysql-connector-j-x.x.x.jar telah ditambahkan ke library proyek.");
            throw new SQLException("Driver MySQL JDBC tidak ditemukan: " + e.getMessage(), e);
        } catch (SQLException e) {
            System.err.println("❌ [JDBC Error] Gagal menyambungkan ke MySQL (" + JDBC_URL + "): " + e.getMessage());
            throw e;
        }
        return connection;
    }

    /**
     * Menutup koneksi database secara aman jika aplikasi keluar
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("ℹ️ [JDBC] Koneksi database MySQL ditutup dengan aman.");
                }
            } catch (SQLException e) {
                System.err.println("⚠️ [JDBC Warning] Terjadi kesalahan saat menutup koneksi: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}

