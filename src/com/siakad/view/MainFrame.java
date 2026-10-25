package com.siakad.view;

import com.siakad.controller.MahasiswaController;
import com.siakad.model.Mahasiswa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Main Frame Antarmuka Desktop Java Swing
 * Layout: Modern Sidebar Navigation + Workstation Table
 * Warna Visual Utama:
 * - Aegean Sky : #1765B0 (Primary UI, Sidebar, Window Header)
 * - Sandy Linen: #EA4501 (Accent / Primary Action Buttons)
 * - Scarlet    : #D21625 (Danger / Delete Action)
 * 
 * @author Fachril Akbar · Desktop App 2024
 */
public class MainFrame extends JFrame {
    public static final Color COLOR_AEGEAN = Color.decode("#1765B0");
    public static final Color COLOR_AEGEAN_DARK = Color.decode("#092442");
    public static final Color COLOR_AEGEAN_SURFACE = Color.decode("#0D355E");
    public static final Color COLOR_SANDY = Color.decode("#EA4501");
    public static final Color COLOR_SCARLET = Color.decode("#D21625");
    public static final Color COLOR_BG_LIGHT = new Color(244, 247, 251);

    private final MahasiswaController controller;
    
    // UI Components
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterProdi;
    private JComboBox<String> cmbFilterStatus;
    private JLabel lblTotalStudents;
    private JLabel lblAverageGpa;
    private JLabel lblActiveCount;
    private JLabel lblSidebarTotalBadge;
    private JLabel lblStatusDatabase;

    public MainFrame() {
        this.controller = new MahasiswaController();
        initWindow();
        initComponents();
        loadData();
    }

    private void initWindow() {
        setTitle("SIAKAD Pro - Sistem Informasi Data Mahasiswa");
        setSize(1180, 750);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_BG_LIGHT);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        // Left Sidebar (Aegean Sky Dark Palette)
        add(createSidebarPanel(), BorderLayout.WEST);

        // Center Content Workspace
        add(createCenterPanel(), BorderLayout.CENTER);

        // Bottom Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    /**
     * Sidebar Navigation Panel (Aegean Sky Modern Sidebar)
     */
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(COLOR_AEGEAN_DARK);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255, 255, 255, 40)));

        // Top Section: Brand & JDBC Card
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.setBorder(new EmptyBorder(20, 16, 20, 16));

        // Brand Logo & Title
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("J");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setOpaque(true);
        lblLogo.setBackground(COLOR_AEGEAN);
        lblLogo.setBorder(new EmptyBorder(4, 10, 4, 10));

        JPanel brandText = new JPanel(new GridLayout(2, 1));
        brandText.setOpaque(false);
        JLabel lblBrand = new JLabel("SIAKAD PRO");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBrand.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Desktop Java · 2024");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(new Color(189, 218, 247));
        brandText.add(lblBrand);
        brandText.add(lblSub);

        brandPanel.add(lblLogo);
        brandPanel.add(brandText);
        topSection.add(brandPanel);
        topSection.add(Box.createVerticalStrut(18));

        // JDBC Status Box
        JPanel jdbcCard = new JPanel(new GridLayout(3, 1, 2, 2));
        jdbcCard.setBackground(COLOR_AEGEAN_SURFACE);
        jdbcCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 30), 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblJdbcStatus = new JLabel("● MySQL 8.0: ONLINE");
        lblJdbcStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblJdbcStatus.setForeground(new Color(52, 211, 153));

        JLabel lblHost = new JLabel("Host: localhost:3306");
        lblHost.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lblHost.setForeground(new Color(203, 213, 225));

        JLabel lblDb = new JLabel("DB: db_mahasiswa");
        lblDb.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lblDb.setForeground(new Color(125, 211, 252));

        jdbcCard.add(lblJdbcStatus);
        jdbcCard.add(lblHost);
        jdbcCard.add(lblDb);
        topSection.add(jdbcCard);
        topSection.add(Box.createVerticalStrut(20));

        // Navigation Header
        JLabel lblMenu = new JLabel("NAVIGASI UTAMA");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblMenu.setForeground(new Color(148, 163, 184));
        topSection.add(lblMenu);
        topSection.add(Box.createVerticalStrut(8));

        // Nav Buttons
        JButton btnNavDashboard = createSidebarNavButton("📊  Dashboard & Data", true);
        btnNavDashboard.addActionListener(e -> resetFilters());
        topSection.add(btnNavDashboard);
        topSection.add(Box.createVerticalStrut(6));

        JButton btnNavAdd = createSidebarNavButton("+   Tambah Mahasiswa", false);
        btnNavAdd.setForeground(new Color(251, 146, 60));
        btnNavAdd.addActionListener(e -> showAddDialog());
        topSection.add(btnNavAdd);
        topSection.add(Box.createVerticalStrut(6));

        JButton btnNavRefresh = createSidebarNavButton("🔄  Muat Ulang Data", false);
        btnNavRefresh.addActionListener(e -> loadData());
        topSection.add(btnNavRefresh);

        sidebar.add(topSection, BorderLayout.NORTH);

        // Bottom User Session Info
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setBackground(new Color(6, 25, 46));
        bottomSection.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblUser = new JLabel("<html><b>Fachril Akbar</b><br><small style='color:#94A3B8;'>Admin Akademik</small></html>");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUser.setForeground(Color.WHITE);

        sidebarTotalBadge = new JLabel("0 Mhs");
        sidebarTotalBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sidebarTotalBadge.setForeground(COLOR_SANDY);

        bottomSection.add(lblUser, BorderLayout.WEST);
        bottomSection.add(sidebarTotalBadge, BorderLayout.EAST);
        sidebar.add(bottomSection, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createSidebarNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));

        if (active) {
            btn.setBackground(COLOR_AEGEAN);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(COLOR_AEGEAN_DARK);
            btn.setForeground(new Color(203, 213, 225));
        }
        return btn;
    }

    /**
     * Center Panel: Metric Cards, Filter Bar, dan Data Table
     */
    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Top Area: Metric Cards & Search/Filter Controls
        JPanel topContainer = new JPanel(new BorderLayout(0, 14));
        topContainer.setOpaque(false);

        // Metric Stats Cards Row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 14, 0));
        statsRow.setOpaque(false);

        lblTotalStudents = new JLabel();
        lblAverageGpa = new JLabel();
        lblActiveCount = new JLabel();

        statsRow.add(createMetricCard("TOTAL MAHASISWA", lblTotalStudents, COLOR_AEGEAN));
        statsRow.add(createMetricCard("RATA-RATA IPK", lblAverageGpa, COLOR_SANDY));
        statsRow.add(createMetricCard("STATUS AKTIF", lblActiveCount, new Color(16, 185, 129)));

        topContainer.add(statsRow, BorderLayout.NORTH);

        // Filter & Action Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));

        // Search & Select Filters
        JPanel filterBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBox.setOpaque(false);

        txtSearch = new JTextField(14);
        txtSearch.setPreferredSize(new Dimension(170, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari NIM/Nama/Email...");

        JButton btnSearch = createButton("Cari", COLOR_AEGEAN, Color.WHITE);
        btnSearch.addActionListener(e -> applyFilter());

        cmbFilterProdi = new JComboBox<>(new String[]{
            "Semua Program Studi",
            "Teknik Informatika",
            "Sistem Informasi",
            "Rekayasa Perangkat Lunak",
            "Sains Data",
            "Teknik Elektro"
        });
        cmbFilterProdi.setPreferredSize(new Dimension(165, 32));
        cmbFilterProdi.addActionListener(e -> applyFilter());

        cmbFilterStatus = new JComboBox<>(new String[]{
            "Semua Status",
            "Aktif",
            "Cuti",
            "Lulus"
        });
        cmbFilterStatus.setPreferredSize(new Dimension(115, 32));
        cmbFilterStatus.addActionListener(e -> applyFilter());

        JButton btnReset = createButton("Reset", Color.WHITE, Color.DARK_GRAY);
        btnReset.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        btnReset.addActionListener(e -> resetFilters());

        filterBox.add(new JLabel("Pencarian:"));
        filterBox.add(txtSearch);
        filterBox.add(btnSearch);
        filterBox.add(cmbFilterProdi);
        filterBox.add(cmbFilterStatus);
        filterBox.add(btnReset);

        // Action Buttons
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBox.setOpaque(false);

        JButton btnTambah = createButton("+ Tambah", COLOR_SANDY, Color.WHITE);
        btnTambah.addActionListener(e -> showAddDialog());

        JButton btnEdit = createButton("Edit", COLOR_AEGEAN, Color.WHITE);
        btnEdit.addActionListener(e -> showEditDialog());

        JButton btnHapus = createButton("Hapus", COLOR_SCARLET, Color.WHITE);
        btnHapus.addActionListener(e -> deleteSelectedStudent());

        actionBox.add(btnTambah);
        actionBox.add(btnEdit);
        actionBox.add(btnHapus);

        toolbar.add(filterBox, BorderLayout.WEST);
        toolbar.add(actionBox, BorderLayout.EAST);
        topContainer.add(toolbar, BorderLayout.SOUTH);

        center.add(topContainer, BorderLayout.NORTH);

        // Table Component
        center.add(createTableScrollPane(), BorderLayout.CENTER);

        return center;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 16, 12, 16)
            )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(new Color(15, 23, 42));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JScrollPane createTableScrollPane() {
        String[] columnNames = {"No", "NIM", "Nama Lengkap", "Program Studi", "Angkatan", "IPK", "Email", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(34);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setGridColor(new Color(241, 245, 249));
        studentTable.setShowVerticalLines(false);

        // Header Styling
        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(30, 41, 59));
        header.setPreferredSize(new Dimension(100, 38));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        studentTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(125);
        studentTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(190);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(170);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(75);
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(6).setPreferredWidth(190);
        studentTable.getColumnModel().getColumn(7).setPreferredWidth(85);
        studentTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(new Color(238, 242, 248));
        status.setBorder(new EmptyBorder(6, 16, 6, 16));

        lblStatusDatabase = new JLabel("Status: Terhubung ke basis data MySQL (db_mahasiswa@localhost:3306)");
        lblStatusDatabase.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatusDatabase.setForeground(new Color(15, 118, 110));

        JLabel lblCopyright = new JLabel("© 2024 Biro Administrasi Akademik & Kemahasiswaan");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(Color.GRAY);

        status.add(lblStatusDatabase, BorderLayout.WEST);
        status.add(lblCopyright, BorderLayout.EAST);
        return status;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ---------------- LOGIKA CRUD & FILTER ----------------

    public void loadData() {
        try {
            List<Mahasiswa> list = controller.getAllMahasiswa();
            populateTable(list);
            updateStats(list);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data dari database MySQL:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        String prodi = cmbFilterProdi.getSelectedIndex() > 0 ? (String) cmbFilterProdi.getSelectedItem() : "";
        String status = cmbFilterStatus.getSelectedIndex() > 0 ? (String) cmbFilterStatus.getSelectedItem() : "";

        try {
            List<Mahasiswa> filtered = controller.searchAndFilter(keyword, prodi, status);
            populateTable(filtered);
            updateStats(filtered);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memfilter data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilters() {
        txtSearch.setText("");
        cmbFilterProdi.setSelectedIndex(0);
        cmbFilterStatus.setSelectedIndex(0);
        loadData();
    }

    private void populateTable(List<Mahasiswa> list) {
        tableModel.setRowCount(0);
        int no = 1;
        for (Mahasiswa m : list) {
            tableModel.addRow(new Object[]{
                no++,
                m.getNim(),
                m.getNama(),
                m.getProdi(),
                m.getAngkatan(),
                String.format("%.2f", m.getIpk()),
                m.getEmail(),
                m.getStatus()
            });
        }
    }

    private void updateStats(List<Mahasiswa> list) {
        lblTotalStudents.setText(String.valueOf(list.size()) + " Mahasiswa");
        if (sidebarTotalBadge != null) {
            sidebarTotalBadge.setText(list.size() + " Mhs");
        }

        double avg = 0.0;
        int activeCount = 0;
        if (!list.isEmpty()) {
            double sum = 0.0;
            for (Mahasiswa m : list) {
                sum += m.getIpk();
                if ("Aktif".equalsIgnoreCase(m.getStatus())) {
                    activeCount++;
                }
            }
            avg = sum / list.size();
        }

        lblAverageGpa.setText(String.format("%.2f", avg));
        lblActiveCount.setText(activeCount + " Mahasiswa");
    }

    private void showAddDialog() {
        FormMahasiswaDialog dialog = new FormMahasiswaDialog(this, "Tambah Mahasiswa Baru", null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Mahasiswa newM = dialog.getMahasiswaData();
            try {
                controller.tambahMahasiswa(newM);
                JOptionPane.showMessageDialog(this, "Mahasiswa berhasil ditambahkan ke database!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris mahasiswa yang ingin diedit terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nim = (String) tableModel.getValueAt(selectedRow, 1);
        Mahasiswa m = controller.getMahasiswaByNim(nim);
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Data mahasiswa tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FormMahasiswaDialog dialog = new FormMahasiswaDialog(this, "Edit Data Mahasiswa", m);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Mahasiswa updated = dialog.getMahasiswaData();
            try {
                controller.perbaruiMahasiswa(updated);
                JOptionPane.showMessageDialog(this, "Data mahasiswa berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih mahasiswa yang ingin dihapus terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nim = (String) tableModel.getValueAt(selectedRow, 1);
        String nama = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda yakin ingin menghapus data mahasiswa:\n" + nama + " (" + nim + ")?\nData akan dihapus secara permanen.",
            "Konfirmasi Penghapusan Data",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.hapusMahasiswa(nim);
                JOptionPane.showMessageDialog(this, "Mahasiswa berhasil dihapus dari database.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Hapus", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
