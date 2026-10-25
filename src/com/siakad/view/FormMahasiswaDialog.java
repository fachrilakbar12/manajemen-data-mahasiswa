package com.siakad.view;

import com.siakad.model.Mahasiswa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog Formulir Input & Edit Data Mahasiswa
 * Dilengkapi dengan validasi GUI sebelum dikirimkan ke controller.
 * 
 * @author Fachril Akbar · Desktop App 2024
 */
public class FormMahasiswaDialog extends JDialog {
    private JTextField txtNim;
    private JTextField txtNama;
    private JComboBox<String> cmbProdi;
    private JTextField txtFakultas;
    private JComboBox<String> cmbAngkatan;
    private JTextField txtIpk;
    private JTextField txtEmail;
    private JComboBox<String> cmbStatus;
    private JTextArea txtAlamat;

    private boolean saved = false;
    private final Mahasiswa existingData;

    public FormMahasiswaDialog(Frame parent, String title, Mahasiswa existingData) {
        super(parent, title, true);
        this.existingData = existingData;
        setSize(480, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        populateFields();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Dialog
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        headerPanel.setBackground(MainFrame.COLOR_AEGEAN);
        JLabel lblTitle = new JLabel(getTitle());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // Form Fields (GridBagLayout)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNim = new JTextField();
        txtNama = new JTextField();
        cmbProdi = new JComboBox<>(new String[]{
            "Teknik Informatika",
            "Sistem Informasi",
            "Rekayasa Perangkat Lunak",
            "Sains Data",
            "Teknik Elektro"
        });
        txtFakultas = new JTextField("Sains & Matematika");
        txtFakultas.setEditable(false);
        txtFakultas.setBackground(new Color(240, 243, 246));

        cmbProdi.addActionListener(e -> {
            String p = (String) cmbProdi.getSelectedItem();
            if ("Teknik Elektro".equals(p)) {
                txtFakultas.setText("Teknik");
            } else if ("Sistem Informasi".equals(p) || "Rekayasa Perangkat Lunak".equals(p)) {
                txtFakultas.setText("Ilmu Komputer");
            } else {
                txtFakultas.setText("Sains & Matematika");
            }
        });

        cmbAngkatan = new JComboBox<>(new String[]{"2024", "2023", "2022", "2021", "2020"});
        txtIpk = new JTextField();
        txtEmail = new JTextField();
        cmbStatus = new JComboBox<>(new String[]{"Aktif", "Cuti", "Lulus"});
        txtAlamat = new JTextArea(3, 20);
        txtAlamat.setLineWrap(true);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "NIM (8-18 Angka):", txtNim);
        addFormRow(formPanel, gbc, row++, "Nama Lengkap:", txtNama);
        addFormRow(formPanel, gbc, row++, "Program Studi:", cmbProdi);
        addFormRow(formPanel, gbc, row++, "Fakultas:", txtFakultas);
        addFormRow(formPanel, gbc, row++, "Angkatan:", cmbAngkatan);
        addFormRow(formPanel, gbc, row++, "IPK (0.00 - 4.00):", txtIpk);
        addFormRow(formPanel, gbc, row++, "Email Kampus:", txtEmail);
        addFormRow(formPanel, gbc, row++, "Status:", cmbStatus);
        addFormRow(formPanel, gbc, row++, "Alamat / Domisili:", scrollAlamat);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> dispose());

        JButton btnSimpan = new JButton("Simpan Data");
        btnSimpan.setBackground(MainFrame.COLOR_SANDY);
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpan.addActionListener(e -> validateAndSave());

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        panel.add(comp, gbc);
    }

    private void populateFields() {
        if (existingData != null) {
            txtNim.setText(existingData.getNim());
            txtNim.setEditable(false);
            txtNim.setBackground(new Color(240, 243, 246));
            txtNama.setText(existingData.getNama());
            cmbProdi.setSelectedItem(existingData.getProdi());
            txtFakultas.setText(existingData.getFakultas());
            cmbAngkatan.setSelectedItem(String.valueOf(existingData.getAngkatan()));
            txtIpk.setText(String.valueOf(existingData.getIpk()));
            txtEmail.setText(existingData.getEmail());
            cmbStatus.setSelectedItem(existingData.getStatus());
            txtAlamat.setText(existingData.getAlamat());
        }
    }

    private void validateAndSave() {
        String nim = txtNim.getText().trim();
        String nama = txtNama.getText().trim();
        String prodi = (String) cmbProdi.getSelectedItem();
        String fakultas = txtFakultas.getText().trim();
        int angkatan = Integer.parseInt((String) cmbAngkatan.getSelectedItem());
        String ipkStr = txtIpk.getText().trim();
        String email = txtEmail.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();
        String alamat = txtAlamat.getText().trim();

        if (nim.isEmpty() || nama.isEmpty() || ipkStr.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua bidang bertanda wajib harus diisi!", "Validasi Formulir", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nim.matches("^[0-9]{8,18}$")) {
            JOptionPane.showMessageDialog(this, "NIM harus berupa deretan angka 8 - 18 digit!", "Validasi Formulir", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double ipk;
        try {
            ipk = Double.parseDouble(ipkStr);
            if (ipk < 0.0 || ipk > 4.0) {
                JOptionPane.showMessageDialog(this, "IPK harus dalam rentang 0.00 hingga 4.00!", "Validasi Formulir", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format IPK tidak valid! Gunakan angka desimal (contoh: 3.75).", "Validasi Formulir", JOptionPane.WARNING_MESSAGE);
            return;
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Mahasiswa getMahasiswaData() {
        return new Mahasiswa(
            txtNim.getText().trim(),
            txtNama.getText().trim(),
            (String) cmbProdi.getSelectedItem(),
            txtFakultas.getText().trim(),
            Integer.parseInt((String) cmbAngkatan.getSelectedItem()),
            Double.parseDouble(txtIpk.getText().trim()),
            txtEmail.getText().trim(),
            (String) cmbStatus.getSelectedItem(),
            txtAlamat.getText().trim()
        );
    }
}

