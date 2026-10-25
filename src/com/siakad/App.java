package com.siakad;

import com.siakad.view.LoginDialog;
import com.siakad.view.MainFrame;

import javax.swing.*;

/**
 * Entry Point Aplikasi Desktop Java
 * Aplikasi Manajemen Data Mahasiswa (Desktop App · 2024)
 * Dilengkapi dengan Autentikasi Admin (admin / admin)
 * 
 * @author Fachril Akbar · Portofolio Proyek
 */
public class App {
    public static void main(String[] args) {
        // Set Look and Feel sistem untuk tampilan modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback ke default Look and Feel jika terjadi error
        }

        // Jalankan aplikasi di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            if (loginDialog.isAuthenticated()) {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
