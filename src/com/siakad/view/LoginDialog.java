package com.siakad.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Dialog Login Administrator SIAKAD
 * Mengimplementasikan autentikasi admin sebelum memasuki pangkalan data.
 * Kredensial Default:
 * - Username: admin
 * - Password: admin
 *
 * Palet Warna Visual:
 * - Aegean Sky : #1765B0
 * - Sandy Linen: #EA4501
 * - Scarlet    : #D21625
 *
 * @author Fachril Akbar · Desktop App 2024
 */
public class LoginDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;
    private boolean authenticated = false;

    // Palet Warna
    private static final Color COLOR_AEGEAN = new Color(0x17, 0x65, 0xB0);
    private static final Color COLOR_SANDY = new Color(0xEA, 0x45, 0x01);
    private static final Color COLOR_SCARLET = new Color(0xD2, 0x16, 0x25);
    private static final Color COLOR_SLATE_900 = new Color(0x0F, 0x17, 0x2A);
    private static final Color COLOR_SLATE_800 = new Color(0x1E, 0x29, 0x3B);
    private static final Color COLOR_SLATE_100 = new Color(0xF1, 0xF5, 0xF9);
    private static final Color COLOR_BORDER = new Color(0xCB, 0xD5, 0xE1);

    public LoginDialog(Frame parent) {
        super(parent, "Autentikasi Administrator - SIAKAD UTN", true);
        initComponents();
    }

    private void initComponents() {
        setSize(480, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Panel dengan Aegean Sky
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(COLOR_AEGEAN);
        headerPanel.setBorder(new EmptyBorder(24, 28, 20, 28));

        JLabel lblBadge = new JLabel("PUSAT LAYANAN AKADEMIK · BAAK");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(new Color(255, 255, 255, 200));

        JLabel lblTitle = new JLabel("Hi Administrator");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Sistem Informasi Akademik - Universitas Teknologi Nasional");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));

        headerPanel.add(lblBadge);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSubtitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Body Panel
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(24, 32, 20, 32));

        // Pesan Error (sembunyi saat awal)
        lblError = new JLabel("Username atau password salah!");
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblError.setForeground(COLOR_SCARLET);
        lblError.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        lblError.setVisible(false);

        // Input Username
        JLabel lblUser = new JLabel("Username Administrator");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(COLOR_SLATE_800);

        txtUsername = new JTextField("admin");
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(0, 36));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        // Input Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(COLOR_SLATE_800);

        txtPassword = new JPasswordField("admin");
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(0, 36));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));

        // Enter key to login
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);

        // Fast Autofill Button
        JButton btnAutofill = new JButton("Gunakan Kredensial Default (admin / admin)");
        btnAutofill.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnAutofill.setBackground(COLOR_SLATE_100);
        btnAutofill.setForeground(COLOR_SLATE_800);
        btnAutofill.setFocusPainted(false);
        btnAutofill.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        btnAutofill.setPreferredSize(new Dimension(0, 32));
        btnAutofill.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnAutofill.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAutofill.addActionListener(e -> {
            txtUsername.setText("admin");
            txtPassword.setText("admin");
            lblError.setVisible(false);
            performLogin();
        });

        // Submit Button (Sandy Linen / Vibrant Red)
        JButton btnLogin = new JButton("Masuk ke Sistem");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(COLOR_SANDY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(new EmptyBorder(10, 16, 10, 16));
        btnLogin.setPreferredSize(new Dimension(0, 40));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> performLogin());

        // Cancel Button
        JButton btnCancel = new JButton("Batal & Keluar");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(Color.GRAY);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> {
            authenticated = false;
            dispose();
        });

        // Susun komponen ke bodyPanel
        bodyPanel.add(lblError);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(lblUser);
        bodyPanel.add(Box.createVerticalStrut(6));
        bodyPanel.add(txtUsername);
        bodyPanel.add(Box.createVerticalStrut(14));
        bodyPanel.add(lblPass);
        bodyPanel.add(Box.createVerticalStrut(6));
        bodyPanel.add(txtPassword);
        bodyPanel.add(Box.createVerticalStrut(16));
        bodyPanel.add(btnAutofill);
        bodyPanel.add(Box.createVerticalStrut(14));
        bodyPanel.add(btnLogin);
        bodyPanel.add(Box.createVerticalStrut(8));
        bodyPanel.add(btnCancel);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        // Footer copyright / info
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(COLOR_SLATE_100);
        footerPanel.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel lblFooter = new JLabel("Kredensial Resmi BAAK · Akses Terlindungi");
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setForeground(Color.GRAY);
        footerPanel.add(lblFooter);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.equalsIgnoreCase("admin") && password.equals("admin")) {
            authenticated = true;
            dispose();
        } else {
            lblError.setText("Username atau password salah! (admin / admin)");
            lblError.setVisible(true);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}

