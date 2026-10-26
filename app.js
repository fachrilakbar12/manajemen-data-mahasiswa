/**
 * ========================================================
 * SIAKAD - Sistem Informasi Manajemen Data Mahasiswa
 * Modul Logika Aplikasi & Manajemen Basis Data
 * ========================================================
 */

// Deteksi Lingkungan Eksekusi (Browser vs Node.js)
const isBrowser = typeof window !== 'undefined' && typeof document !== 'undefined';

class Mahasiswa {
  constructor(nim, nama, prodi, fakultas, angkatan, ipk, email, status = 'Aktif', alamat = '') {
    this.nim = String(nim).trim();
    this.nama = String(nama).trim();
    this.prodi = String(prodi).trim();
    this.fakultas = String(fakultas).trim();
    this.angkatan = parseInt(angkatan, 10);
    this.ipk = parseFloat(Number(ipk).toFixed(2));
    this.email = String(email).trim().toLowerCase();
    this.status = status;
    this.alamat = String(alamat).trim();
    this.createdAt = new Date().toISOString();
  }

  getInitials() {
    const parts = this.nama.split(' ').filter(Boolean);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return (parts[0] ? parts[0].substring(0, 2) : 'MH').toUpperCase();
  }

  isCumlaude() {
    return this.ipk >= 3.50;
  }

  getIpkBadgeClass() {
    if (this.ipk >= 3.50) return 'badge-ipk-high';
    if (this.ipk >= 3.00) return 'badge-ipk-mid';
    return 'badge-ipk-low';
  }

  getStatusBadgeClass() {
    switch (this.status) {
      case 'Aktif':
        return 'bg-emerald-100 text-emerald-800 border-emerald-200';
      case 'Cuti':
        return 'bg-amber-100 text-amber-800 border-amber-200';
      case 'Lulus':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      default:
        return 'bg-slate-100 text-slate-800 border-slate-200';
    }
  }

  static getFacultyByProdi(prodi) {
    switch (prodi) {
      case 'Teknik Informatika':
      case 'Sains Data':
        return 'Sains & Matematika';
      case 'Sistem Informasi':
      case 'Rekayasa Perangkat Lunak':
        return 'Ilmu Komputer';
      case 'Teknik Elektro':
        return 'Teknik';
      default:
        return 'Fakultas Teknologi';
    }
  }
}

class DatabaseAuditLogger {
  constructor() {
    this.logs = [];
    this.maxLogs = 50;
  }

  logQuery(sql, type = 'QUERY', durationMs = null) {
    const time = new Date().toLocaleTimeString('id-ID');
    const latency = durationMs || (Math.floor(Math.random() * 8) + 2);
    const entry = {
      timestamp: time,
      type: type,
      query: sql,
      latency: latency
    };

    this.logs.unshift(entry);
    if (this.logs.length > this.maxLogs) {
      this.logs.pop();
    }

    // Update Status Bar
    if (typeof document !== 'undefined') {
      const statusElem = document.getElementById('lastQueryExecuted');
      if (statusElem) {
        const shortSql = sql.length > 55 ? sql.substring(0, 52) + '...' : sql;
        statusElem.textContent = `Kueri Terakhir (${latency}ms): ${shortSql}`;
      }
      this.renderLogs();
    }
  }

  renderLogs() {
    const container = document.getElementById('sqlConsoleLogs');
    if (!container) return;

    if (this.logs.length === 0) {
      container.innerHTML = '<div class="text-slate-500 italic">Belum ada query dieksekusi.</div>';
      return;
    }

    container.innerHTML = this.logs.map(log => {
      let badgeColor = 'text-emerald-400 border-emerald-800 bg-emerald-950/60';
      if (log.type === 'INSERT') badgeColor = 'text-sky-400 border-sky-800 bg-sky-950/60';
      if (log.type === 'UPDATE') badgeColor = 'text-amber-400 border-amber-800 bg-amber-950/60';
      if (log.type === 'DELETE') badgeColor = 'text-rose-400 border-rose-800 bg-rose-950/60';

      return `
        <div class="border-b border-slate-800/80 pb-1.5 pt-1">
          <div class="flex items-center justify-between text-[11px] text-slate-400">
            <span class="flex items-center gap-2">
              <span class="px-1.5 py-0.2 rounded border text-[10px] font-bold ${badgeColor}">${log.type}</span>
              <span>[${log.timestamp}] Koneksi MySQL Pool</span>
            </span>
            <span class="text-slate-400 font-mono text-[10px]">${log.latency} ms</span>
          </div>
          <div class="mt-1 text-slate-200 font-mono text-xs pl-2 border-l-2 border-slate-700 select-all">${log.query}</div>
        </div>
      `;
    }).join('');
  }

  clear() {
    this.logs = [];
    this.renderLogs();
  }
}

// -----------------------------------------------------------------------------
// 3. SERVICE: MahasiswaService (CRUD, Search, Filter, Persistence)
// -----------------------------------------------------------------------------
class MahasiswaService {
  constructor(dbAuditLogger) {
    this.storageKey = 'siakad_mahasiswa_data_v1';
    this.db = dbAuditLogger;
    this.students = [];
    this.loadData();
  }

  getMasterRecords() {
    return [
      new Mahasiswa('24060122140001', 'Fachril Akbar', 'Teknik Informatika', 'Sains & Matematika', 2024, 3.88, 'fachril.akbar@students.ac.id', 'Aktif', 'Jl. Tembalang Baru No. 12, Semarang'),
      new Mahasiswa('24060122140002', 'Aisyah Putri Rahma', 'Sistem Informasi', 'Ilmu Komputer', 2024, 3.92, 'aisyah.putri@students.ac.id', 'Aktif', 'Jl. Banjarsari Selatan No. 5, Semarang'),
      new Mahasiswa('24060122130045', 'Bima Sakti Nugraha', 'Rekayasa Perangkat Lunak', 'Ilmu Komputer', 2023, 3.65, 'bima.sakti@students.ac.id', 'Aktif', 'Jl. Prof. Soedarto No. 8, Semarang'),
      new Mahasiswa('24060122120019', 'Citra Dewi Anggraini', 'Sains Data', 'Sains & Matematika', 2022, 3.82, 'citra.dewi@students.ac.id', 'Aktif', 'Jl. Tirto Agung No. 21, Semarang'),
      new Mahasiswa('24060122140088', 'Dimas Arya Pratama', 'Teknik Elektro', 'Teknik', 2024, 3.42, 'dimas.arya@students.ac.id', 'Aktif', 'Jl. Jatingaleh Raya No. 44, Semarang'),
      new Mahasiswa('24060122130090', 'Farhan Maulana', 'Teknik Informatika', 'Sains & Matematika', 2023, 3.76, 'farhan.m@students.ac.id', 'Aktif', 'Jl. Ngesrep Timur V No. 10, Semarang'),
      new Mahasiswa('24060122110033', 'Gita Permatasari', 'Sistem Informasi', 'Ilmu Komputer', 2021, 3.95, 'gita.permata@students.ac.id', 'Lulus', 'Jl. Pleburan Barat No. 3, Semarang'),
      new Mahasiswa('24060122120054', 'Hendra Wijaya', 'Rekayasa Perangkat Lunak', 'Ilmu Komputer', 2022, 2.95, 'hendra.w@students.ac.id', 'Cuti', 'Jl. Sumurboto Raya No. 15, Semarang'),
      new Mahasiswa('24060122140112', 'Indah Kusuma Wardani', 'Sains Data', 'Sains & Matematika', 2024, 3.71, 'indah.kusuma@students.ac.id', 'Aktif', 'Jl. Gondang Barat No. 27, Semarang'),
      new Mahasiswa('24060122130071', 'Kevin Jonathan', 'Teknik Elektro', 'Teknik', 2023, 3.58, 'kevin.j@students.ac.id', 'Aktif', 'Jl. Sirojudin No. 19, Semarang')
    ];
  }

  loadData() {
    if (typeof localStorage !== 'undefined') {
      try {
        const stored = localStorage.getItem(this.storageKey);
        if (stored) {
          const parsed = JSON.parse(stored);
          if (Array.isArray(parsed) && parsed.length > 0) {
            this.students = parsed.map(item => new Mahasiswa(
              item.nim, item.nama, item.prodi, item.fakultas,
              item.angkatan, item.ipk, item.email, item.status, item.alamat
            ));
            if (this.db) this.db.logQuery('SELECT * FROM mahasiswa ORDER BY nim ASC;', 'SELECT');
            return;
          }
        }
      } catch (e) {
        console.error('Gagal membaca data dari storage:', e);
      }
    }
    // Sinkronisasi data awal
    this.students = this.getMasterRecords();
    this.saveData();
    if (this.db) this.db.logQuery('SELECT * FROM mahasiswa ORDER BY nim ASC;', 'SELECT');
  }

  saveData() {
    if (typeof localStorage !== 'undefined') {
      try {
        localStorage.setItem(this.storageKey, JSON.stringify(this.students));
      } catch (e) {
        console.error('Gagal menyimpan data:', e);
      }
    }
  }

  resetToDefaults() {
    this.students = this.getMasterRecords();
    this.saveData();
    this.db.logQuery('TRUNCATE TABLE mahasiswa; -- Sinkronisasi Master Data', 'DELETE');
    return this.students;
  }

  getAll() {
    return [...this.students];
  }

  getByNim(nim) {
    this.db.logQuery(`SELECT * FROM mahasiswa WHERE nim = '${nim}' LIMIT 1;`, 'SELECT');
    return this.students.find(s => s.nim === String(nim).trim());
  }

  existsNim(nim) {
    return this.students.some(s => s.nim === String(nim).trim());
  }

  add(mahasiswa) {
    if (this.existsNim(mahasiswa.nim)) {
      throw new Error(`Mahasiswa dengan NIM ${mahasiswa.nim} sudah terdaftar!`);
    }

    this.students.unshift(mahasiswa);
    this.saveData();

    // SQL Simulation
    const sql = `INSERT INTO mahasiswa (nim, nama, prodi, fakultas, angkatan, ipk, email, status, alamat) VALUES ('${mahasiswa.nim}', '${mahasiswa.nama}', '${mahasiswa.prodi}', '${mahasiswa.fakultas}', ${mahasiswa.angkatan}, ${mahasiswa.ipk}, '${mahasiswa.email}', '${mahasiswa.status}', '${mahasiswa.alamat.replace(/'/g, "''")}');`;
    this.db.logQuery(sql, 'INSERT');

    return mahasiswa;
  }

  update(originalNim, updatedData) {
    const index = this.students.findIndex(s => s.nim === originalNim);
    if (index === -1) {
      throw new Error(`Data mahasiswa dengan NIM ${originalNim} tidak ditemukan!`);
    }

    // If NIM changed, check if new NIM is already used
    if (originalNim !== updatedData.nim && this.existsNim(updatedData.nim)) {
      throw new Error(`NIM ${updatedData.nim} sudah digunakan oleh mahasiswa lain!`);
    }

    const updatedStudent = new Mahasiswa(
      updatedData.nim,
      updatedData.nama,
      updatedData.prodi,
      updatedData.fakultas,
      updatedData.angkatan,
      updatedData.ipk,
      updatedData.email,
      updatedData.status,
      updatedData.alamat
    );

    this.students[index] = updatedStudent;
    this.saveData();

    // SQL Simulation
    const sql = `UPDATE mahasiswa SET nim='${updatedStudent.nim}', nama='${updatedStudent.nama}', prodi='${updatedStudent.prodi}', ipk=${updatedStudent.ipk}, email='${updatedStudent.email}', status='${updatedStudent.status}' WHERE nim='${originalNim}';`;
    this.db.logQuery(sql, 'UPDATE');

    return updatedStudent;
  }

  delete(nim) {
    const index = this.students.findIndex(s => s.nim === nim);
    if (index === -1) {
      throw new Error(`Mahasiswa dengan NIM ${nim} tidak ditemukan!`);
    }

    const deleted = this.students.splice(index, 1)[0];
    this.saveData();

    // SQL Simulation
    this.db.logQuery(`DELETE FROM mahasiswa WHERE nim = '${nim}';`, 'DELETE');

    return deleted;
  }

  filterAndSearch(keyword = '', prodi = '', status = '', sortBy = 'nim-asc') {
    let result = [...this.students];

    // Filter Keyword (NIM, Nama, Email)
    if (keyword) {
      const q = keyword.toLowerCase().trim();
      result = result.filter(s =>
        s.nim.toLowerCase().includes(q) ||
        s.nama.toLowerCase().includes(q) ||
        s.email.toLowerCase().includes(q)
      );
    }

    // Filter Prodi
    if (prodi) {
      result = result.filter(s => s.prodi === prodi);
    }

    // Filter Status
    if (status) {
      result = result.filter(s => s.status === status);
    }

    // Sorting
    switch (sortBy) {
      case 'nim-asc':
        result.sort((a, b) => a.nim.localeCompare(b.nim));
        break;
      case 'nim-desc':
        result.sort((a, b) => b.nim.localeCompare(a.nim));
        break;
      case 'nama-asc':
        result.sort((a, b) => a.nama.localeCompare(b.nama));
        break;
      case 'nama-desc':
        result.sort((a, b) => b.nama.localeCompare(a.nama));
        break;
      case 'ipk-desc':
        result.sort((a, b) => b.ipk - a.ipk);
        break;
      case 'ipk-asc':
        result.sort((a, b) => a.ipk - b.ipk);
        break;
      default:
        result.sort((a, b) => a.nim.localeCompare(b.nim));
    }

    // Log Query Simulation if filter exists
    if (keyword || prodi || status) {
      let conditions = [];
      if (keyword) conditions.push(`(nama LIKE '%${keyword}%' OR nim LIKE '%${keyword}%')`);
      if (prodi) conditions.push(`prodi = '${prodi}'`);
      if (status) conditions.push(`status = '${status}'`);
      const whereClause = conditions.length > 0 ? ` WHERE ${conditions.join(' AND ')}` : '';
      this.db.logQuery(`SELECT * FROM mahasiswa${whereClause} ORDER BY ${sortBy.split('-')[0]} ${sortBy.split('-')[1].toUpperCase()};`, 'SELECT');
    }

    return result;
  }

  getStatistics() {
    const total = this.students.length;
    if (total === 0) {
      return { total: 0, avgGpa: '0.00', activeCount: 0, activeRatio: '0%', cumlaudeCount: 0 };
    }

    const sumGpa = this.students.reduce((acc, curr) => acc + curr.ipk, 0);
    const avgGpa = (sumGpa / total).toFixed(2);
    const activeCount = this.students.filter(s => s.status === 'Aktif').length;
    const activeRatio = `${Math.round((activeCount / total) * 100)}%`;
    const cumlaudeCount = this.students.filter(s => s.isCumlaude()).length;

    return { total, avgGpa, activeCount, activeRatio, cumlaudeCount };
  }
}

// -----------------------------------------------------------------------------
// 4. UI CONTROLLER: Mengelola DOM, Interaksi, Animasi & Validasi
// -----------------------------------------------------------------------------
function safeCreateIcons() {
  if (typeof window !== 'undefined' && window.lucide && typeof window.lucide.createIcons === 'function') {
    try {
      window.lucide.createIcons();
    } catch (e) {
      // Ignored safely
    }
  }
}

class UIManager {
  constructor(service, dbAuditLogger) {
    this.service = service;
    this.db = dbAuditLogger;
    
    this.currentPage = 1;
    this.pageSize = 8;
    this.currentFilteredData = [];
    this.selectedNimForDelete = null;

    this.initElements();
    this.attachEventListeners();
    this.render();
  }

  initElements() {
    // Inputs & Filters
    this.searchInput = document.getElementById('searchInput');
    this.clearSearchBtn = document.getElementById('clearSearchBtn');
    this.filterProdi = document.getElementById('filterProdi');
    this.filterStatus = document.getElementById('filterStatus');
    this.sortBy = document.getElementById('sortBy');
    this.btnResetFilters = document.getElementById('btnResetFilters');

    // Table & Pagination
    this.tableBody = document.getElementById('studentTableBody');
    this.emptyState = document.getElementById('emptyState');
    this.dataCountBadge = document.getElementById('dataCountBadge');
    this.paginationInfo = document.getElementById('paginationInfo');
    this.prevPageBtn = document.getElementById('prevPageBtn');
    this.nextPageBtn = document.getElementById('nextPageBtn');
    this.pageNumberButtons = document.getElementById('pageNumberButtons');

    // Stats
    this.statTotalStudents = document.getElementById('statTotalStudents');
    this.statAverageGpa = document.getElementById('statAverageGpa');
    this.statActiveStudents = document.getElementById('statActiveStudents');
    this.statActiveRatio = document.getElementById('statActiveRatio');
    this.statCumlaudeCount = document.getElementById('statCumlaudeCount');

    // Modals
    this.studentModal = document.getElementById('studentModal');
    this.studentForm = document.getElementById('studentForm');
    this.detailModal = document.getElementById('detailModal');
    this.deleteModal = document.getElementById('deleteModal');
    this.sqlModal = document.getElementById('sqlModal');
    this.javaCodeModal = document.getElementById('javaCodeModal');
    this.confirmDeleteBtn = document.getElementById('confirmDeleteBtn');

    // Window View Mode
    this.viewModeWindow = document.getElementById('viewModeWindow');
    this.viewModeFull = document.getElementById('viewModeFull');
    this.desktopAppWindow = document.getElementById('desktopAppWindow');
    this.sidebarTotalBadge = document.getElementById('sidebarTotalBadge');
  }

  attachEventListeners() {
    // Live Search
    this.searchInput.addEventListener('input', () => {
      this.clearSearchBtn.classList.toggle('hidden', !this.searchInput.value);
      this.currentPage = 1;
      this.render();
    });

    this.clearSearchBtn.addEventListener('click', () => {
      this.searchInput.value = '';
      this.clearSearchBtn.classList.add('hidden');
      this.currentPage = 1;
      this.render();
    });

    // Filters & Sorting
    this.filterProdi.addEventListener('change', () => { this.currentPage = 1; this.render(); });
    this.filterStatus.addEventListener('change', () => { this.currentPage = 1; this.render(); });
    this.sortBy.addEventListener('change', () => { this.render(); });
    this.btnResetFilters.addEventListener('click', () => { this.resetFilters(); });

    // Pagination
    this.prevPageBtn.addEventListener('click', () => {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.renderTable();
      }
    });

    this.nextPageBtn.addEventListener('click', () => {
      const maxPage = Math.ceil(this.currentFilteredData.length / this.pageSize) || 1;
      if (this.currentPage < maxPage) {
        this.currentPage++;
        this.renderTable();
      }
    });

    // View Mode Toggle
    if (this.viewModeWindow) this.viewModeWindow.addEventListener('click', () => this.setViewMode('window'));
    if (this.viewModeFull) this.viewModeFull.addEventListener('click', () => this.setViewMode('full'));

    // Delete Confirmation
    if (this.confirmDeleteBtn) this.confirmDeleteBtn.addEventListener('click', () => this.handleConfirmDelete());

    // Auto-sync Fakultas on Prodi change in form
    const inputProdi = document.getElementById('inputProdi');
    if (inputProdi) {
      inputProdi.addEventListener('change', (e) => {
        const fakultasField = document.getElementById('inputFakultas');
        if (fakultasField) {
          fakultasField.value = Mahasiswa.getFacultyByProdi(e.target.value);
        }
      });
    }

    // Close modals on Escape key
    window.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        this.closeAllModals();
      }
    });
  }

  setViewMode(mode) {
    if (mode === 'window') {
      this.desktopAppWindow.classList.remove('fullscreen-mode');
      this.desktopAppWindow.classList.add('desktop-window-mode');
      this.viewModeWindow.classList.add('bg-aegean', 'text-white', 'shadow-sm');
      this.viewModeWindow.classList.remove('text-slate-300');
      this.viewModeFull.classList.remove('bg-aegean', 'text-white', 'shadow-sm');
      this.viewModeFull.classList.add('text-slate-300');
    } else {
      this.desktopAppWindow.classList.add('fullscreen-mode');
      this.desktopAppWindow.classList.remove('desktop-window-mode');
      this.viewModeFull.classList.add('bg-aegean', 'text-white', 'shadow-sm');
      this.viewModeFull.classList.remove('text-slate-300');
      this.viewModeWindow.classList.remove('bg-aegean', 'text-white', 'shadow-sm');
      this.viewModeWindow.classList.add('text-slate-300');
    }
  }

  resetFilters() {
    this.searchInput.value = '';
    this.clearSearchBtn.classList.add('hidden');
    this.filterProdi.value = '';
    this.filterStatus.value = '';
    this.sortBy.value = 'nim-asc';
    this.currentPage = 1;

    // Reset 3D elevated state on top metric cards
    ['cardStatTotal', 'cardStatGpa', 'cardStatAktif', 'cardStatCumlaude'].forEach(id => {
      const card = document.getElementById(id);
      if (card) card.classList.remove('is-active-forward');
    });

    this.render();
  }

  render() {
    this.currentFilteredData = this.service.filterAndSearch(
      this.searchInput.value,
      this.filterProdi.value,
      this.filterStatus.value,
      this.sortBy.value
    );

    this.renderStats();
    this.renderTable();
    this.renderPagination();

    // Re-initialize lucide icons for newly created DOM nodes
    safeCreateIcons();
  }

  renderStats() {
    const stats = this.service.getStatistics();
    if (this.statTotalStudents) this.statTotalStudents.textContent = stats.total;
    if (this.statAverageGpa) this.statAverageGpa.textContent = stats.avgGpa;
    if (this.statActiveStudents) this.statActiveStudents.textContent = stats.activeCount;
    if (this.statActiveRatio) this.statActiveRatio.textContent = `(${stats.activeRatio})`;
    if (this.statCumlaudeCount) this.statCumlaudeCount.textContent = stats.cumlaudeCount;
    if (this.sidebarTotalBadge) this.sidebarTotalBadge.textContent = stats.total;
  }

  renderTable() {
    const data = this.currentFilteredData;
    this.dataCountBadge.textContent = `${data.length} Mahasiswa`;
    const mobileCardsContainer = document.getElementById('studentMobileCards');

    if (data.length === 0) {
      this.tableBody.innerHTML = '';
      if (mobileCardsContainer) mobileCardsContainer.innerHTML = '';
      this.emptyState.classList.remove('hidden');
      return;
    }

    this.emptyState.classList.add('hidden');

    // Calculate pagination slice
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const paginatedItems = data.slice(startIndex, startIndex + this.pageSize);

    // 1. Render Desktop Table Rows (>= md)
    this.tableBody.innerHTML = paginatedItems.map((mhs, idx) => {
      const rowNumber = startIndex + idx + 1;
      const initials = mhs.getInitials();
      const ipkBadge = mhs.getIpkBadgeClass();
      const statusBadge = mhs.getStatusBadgeClass();

      return `
        <tr class="hover:bg-slate-50 transition border-b border-slate-100/90 text-xs sm:text-sm">
          <td class="py-3 px-4 text-center font-mono text-slate-400 font-semibold">${rowNumber}</td>
          
          <!-- NIM -->
          <td class="py-3 px-4 font-mono font-bold text-slate-800 whitespace-nowrap">
            <span class="px-2 py-0.5 rounded bg-slate-100 text-slate-800 border border-slate-200">
              ${mhs.nim}
            </span>
          </td>

          <!-- Nama Mahasiswa -->
          <td class="py-3 px-4">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-slate-100 text-slate-700 font-heading font-bold text-xs flex items-center justify-center shrink-0 border border-slate-200/60">
                ${initials}
              </div>
              <div class="min-w-0">
                <button onclick="ui.openDetailModal('${mhs.nim}')" class="font-bold text-slate-900 hover:text-aegean transition truncate block text-left">
                  ${mhs.nama}
                </button>
                <span class="text-xs text-slate-400 truncate block font-mono">${mhs.email}</span>
              </div>
            </div>
          </td>

          <!-- Prodi -->
          <td class="py-3 px-4 whitespace-nowrap">
            <span class="badge-prodi">
              ${mhs.prodi}
            </span>
          </td>

          <!-- Angkatan -->
          <td class="py-3 px-4 text-center font-mono text-slate-700 whitespace-nowrap">
            ${mhs.angkatan}
          </td>

          <!-- IPK -->
          <td class="py-3 px-4 text-center whitespace-nowrap">
            <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-mono font-bold ${ipkBadge}">
              ${mhs.ipk.toFixed(2)}
            </span>
          </td>

          <!-- Status -->
          <td class="py-3 px-4 text-center whitespace-nowrap">
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${statusBadge}">
              ${mhs.status}
            </span>
          </td>

          <!-- Action Buttons -->
          <td class="py-3 px-4 text-right whitespace-nowrap">
            <div class="flex items-center justify-end gap-1.5">
              <!-- View Detail -->
              <button onclick="ui.openDetailModal('${mhs.nim}')" title="Lihat Kartu Mahasiswa" class="btn-action-view p-2 sm:p-1.5 rounded-lg transition active:scale-95">
                <i data-lucide="eye" class="w-4 h-4"></i>
              </button>
              
              <!-- Edit -->
              <button onclick="ui.openEditModal('${mhs.nim}')" title="Edit Data Mahasiswa" class="btn-action-edit p-2 sm:p-1.5 rounded-lg transition active:scale-95">
                <i data-lucide="edit-3" class="w-4 h-4"></i>
              </button>

              <!-- Delete (Scarlet Accent) -->
              <button onclick="ui.openDeleteModal('${mhs.nim}')" title="Hapus Data" class="btn-action-delete p-2 sm:p-1.5 rounded-lg transition active:scale-95">
                <i data-lucide="trash-2" class="w-4 h-4"></i>
              </button>
            </div>
          </td>
        </tr>
      `;
    }).join('');

    // 2. Render Mobile Responsive Cards (< md)
    if (mobileCardsContainer) {
      mobileCardsContainer.innerHTML = paginatedItems.map((mhs) => {
        const initials = mhs.getInitials();
        const ipkBadge = mhs.getIpkBadgeClass();
        const statusBadge = mhs.getStatusBadgeClass();

        return `
          <div class="p-4 hover:bg-slate-50/90 transition flex flex-col gap-2.5">
            <div class="flex items-start justify-between gap-2">
              <div class="flex items-center gap-2.5 min-w-0">
                <div class="w-9 h-9 rounded-xl bg-slate-100 text-slate-700 font-heading font-bold text-xs flex items-center justify-center shrink-0 border border-slate-200/60">
                  ${initials}
                </div>
                <div class="min-w-0">
                  <button onclick="ui.openDetailModal('${mhs.nim}')" class="font-bold text-slate-900 text-sm hover:text-aegean text-left truncate block max-w-[200px]">
                    ${mhs.nama}
                  </button>
                  <div class="text-[11px] font-mono text-slate-500">${mhs.nim}</div>
                </div>
              </div>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold border shrink-0 ${statusBadge}">
                ${mhs.status}
              </span>
            </div>

            <div class="flex items-center justify-between text-xs py-1.5 px-2.5 rounded-lg bg-slate-50 border border-slate-100">
              <div class="flex items-center gap-2 min-w-0">
                <span class="badge-prodi text-[10px] truncate max-w-[150px]">${mhs.prodi}</span>
                <span class="text-slate-400 font-mono text-[11px]">Angk. ${mhs.angkatan}</span>
              </div>
              <div class="flex items-center gap-1 shrink-0">
                <span class="text-slate-500 text-[11px]">IPK:</span>
                <span class="font-mono font-bold text-xs px-1.5 py-0.5 rounded ${ipkBadge}">
                  ${mhs.ipk.toFixed(2)}
                </span>
              </div>
            </div>

            <div class="grid grid-cols-3 gap-2 pt-1">
              <button onclick="ui.openDetailModal('${mhs.nim}')" class="py-1.5 px-2 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 text-xs font-semibold flex items-center justify-center gap-1.5 transition active:scale-95 shadow-2xs">
                <i data-lucide="eye" class="w-3.5 h-3.5 text-slate-500"></i> KTM
              </button>
              <button onclick="ui.openEditModal('${mhs.nim}')" class="py-1.5 px-2 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 text-xs font-semibold flex items-center justify-center gap-1.5 transition active:scale-95 shadow-2xs">
                <i data-lucide="edit-3" class="w-3.5 h-3.5 text-slate-500"></i> Edit
              </button>
              <button onclick="ui.openDeleteModal('${mhs.nim}')" class="py-1.5 px-2 rounded-lg border border-slate-200 bg-white hover:bg-rose-50 hover:text-rose-600 hover:border-rose-200 text-slate-600 text-xs font-semibold flex items-center justify-center gap-1.5 transition active:scale-95 shadow-2xs">
                <i data-lucide="trash-2" class="w-3.5 h-3.5"></i> Hapus
              </button>
            </div>
          </div>
        `;
      }).join('');
    }
  }

  renderPagination() {
    const totalRecords = this.currentFilteredData.length;
    const maxPage = Math.ceil(totalRecords / this.pageSize) || 1;

    // Boundary check
    if (this.currentPage > maxPage) this.currentPage = maxPage;
    if (this.currentPage < 1) this.currentPage = 1;

    const start = totalRecords === 0 ? 0 : (this.currentPage - 1) * this.pageSize + 1;
    const end = Math.min(this.currentPage * this.pageSize, totalRecords);

    this.paginationInfo.innerHTML = `
      Menampilkan <span class="font-semibold text-slate-800">${start}</span> - 
      <span class="font-semibold text-slate-800">${end}</span> dari 
      <span class="font-semibold text-slate-800">${totalRecords}</span> mahasiswa
    `;

    this.prevPageBtn.disabled = this.currentPage <= 1;
    this.nextPageBtn.disabled = this.currentPage >= maxPage;

    // Generate page buttons
    let buttonsHtml = '';
    for (let i = 1; i <= maxPage; i++) {
      if (i === this.currentPage) {
        buttonsHtml += `
          <button class="w-7 h-7 rounded text-xs font-bold bg-aegean text-white shadow-sm">${i}</button>
        `;
      } else if (i === 1 || i === maxPage || Math.abs(i - this.currentPage) <= 1) {
        buttonsHtml += `
          <button onclick="ui.goToPage(${i})" class="w-7 h-7 rounded text-xs font-semibold bg-white border border-slate-200 text-slate-700 hover:bg-slate-100 transition">${i}</button>
        `;
      } else if (Math.abs(i - this.currentPage) === 2) {
        buttonsHtml += `<span class="px-1 text-slate-400 text-xs">...</span>`;
      }
    }
    this.pageNumberButtons.innerHTML = buttonsHtml;
  }

  goToPage(pageNum) {
    this.currentPage = pageNum;
    this.renderTable();
    this.renderPagination();
    safeCreateIcons();
  }

  // ---------------- MODAL MANAGERS ----------------

  openAddModal() {
    this.studentForm.reset();
    document.getElementById('formMode').value = 'add';
    document.getElementById('originalNim').value = '';
    document.getElementById('modalTitle').textContent = 'Tambah Data Mahasiswa Baru';
    document.getElementById('inputNim').removeAttribute('readonly');
    document.getElementById('inputNim').classList.remove('bg-slate-100');
    document.getElementById('inputFakultas').value = '';
    
    const submitBtn = document.getElementById('modalSubmitBtn');
    submitBtn.className = 'px-5 py-2.5 rounded-lg bg-sandy hover:bg-sandy-600 text-white font-semibold text-sm shadow-md shadow-sandy/20 transition flex items-center gap-2 cursor-pointer';
    submitBtn.innerHTML = '<i data-lucide="user-plus" class="w-4 h-4"></i><span>Simpan Mahasiswa</span>';

    this.clearValidationErrors();
    this.studentModal.classList.remove('hidden');
    this.studentModal.classList.add('flex');
    safeCreateIcons();
  }

  openEditModal(nim) {
    const student = this.service.getByNim(nim);
    if (!student) {
      this.showToast('Mahasiswa tidak ditemukan!', 'error');
      return;
    }

    document.getElementById('formMode').value = 'edit';
    document.getElementById('originalNim').value = student.nim;
    document.getElementById('modalTitle').textContent = `Ubah Data Mahasiswa · ${student.nim}`;
    
    document.getElementById('inputNim').value = student.nim;
    document.getElementById('inputNim').setAttribute('readonly', 'true');
    document.getElementById('inputNim').classList.add('bg-slate-100');

    document.getElementById('inputNama').value = student.nama;
    document.getElementById('inputProdi').value = student.prodi;
    document.getElementById('inputFakultas').value = student.fakultas;
    document.getElementById('inputAngkatan').value = student.angkatan;
    document.getElementById('inputIpk').value = student.ipk;
    document.getElementById('inputEmail').value = student.email;
    document.getElementById('inputStatus').value = student.status;
    document.getElementById('inputAlamat').value = student.alamat;

    const submitBtn = document.getElementById('modalSubmitBtn');
    submitBtn.className = 'px-5 py-2.5 rounded-lg bg-aegean hover:bg-aegean-600 text-white font-semibold text-sm shadow-md shadow-aegean/20 transition flex items-center gap-2 cursor-pointer';
    submitBtn.innerHTML = '<i data-lucide="check" class="w-4 h-4"></i><span>Perbarui Data</span>';

    this.clearValidationErrors();
    this.studentModal.classList.remove('hidden');
    this.studentModal.classList.add('flex');
    safeCreateIcons();
  }

  openDetailModal(nim) {
    const student = this.service.getByNim(nim);
    if (!student) return;

    document.getElementById('detailAvatar').textContent = student.getInitials();
    document.getElementById('detailNama').textContent = student.nama;
    document.getElementById('detailNim').textContent = student.nim;
    document.getElementById('detailProdi').textContent = student.prodi;
    document.getElementById('detailFakultas').textContent = student.fakultas;
    document.getElementById('detailAngkatan').textContent = student.angkatan;
    document.getElementById('detailIpk').textContent = student.ipk.toFixed(2);
    document.getElementById('detailEmail').textContent = student.email;
    document.getElementById('detailAlamat').textContent = student.alamat || 'Alamat belum diisi.';

    const statusBadge = document.getElementById('detailStatusBadge');
    statusBadge.innerHTML = `<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${student.getStatusBadgeClass()}">${student.status}</span>`;

    const editBtn = document.getElementById('detailEditBtn');
    editBtn.onclick = () => {
      this.closeDetailModal();
      this.openEditModal(student.nim);
    };

    this.detailModal.classList.remove('hidden');
    this.detailModal.classList.add('flex');
    safeCreateIcons();
  }

  closeDetailModal() {
    this.detailModal.classList.add('hidden');
    this.detailModal.classList.remove('flex');
  }

  openDeleteModal(nim) {
    const student = this.service.getByNim(nim);
    if (!student) return;

    this.selectedNimForDelete = student.nim;
    document.getElementById('deleteNama').textContent = student.nama;
    document.getElementById('deleteNim').textContent = student.nim;
    document.getElementById('deleteProdi').textContent = student.prodi;

    this.deleteModal.classList.remove('hidden');
    this.deleteModal.classList.add('flex');
    safeCreateIcons();
  }

  closeDeleteModal() {
    this.selectedNimForDelete = null;
    this.deleteModal.classList.add('hidden');
    this.deleteModal.classList.remove('flex');
  }

  handleConfirmDelete() {
    if (!this.selectedNimForDelete) return;

    try {
      const deleted = this.service.delete(this.selectedNimForDelete);
      this.closeDeleteModal();
      this.render();
      this.showToast(`Data mahasiswa ${deleted.nama} (${deleted.nim}) berhasil dihapus.`, 'scarlet');
    } catch (err) {
      this.showToast(err.message, 'error');
    }
  }

  closeModal() {
    this.studentModal.classList.add('hidden');
    this.studentModal.classList.remove('flex');
    this.clearValidationErrors();
  }

  closeAllModals() {
    this.closeModal();
    this.closeDetailModal();
    this.closeDeleteModal();
    this.closeSqlModal();
    this.closeJavaCodeModal();
  }

  clearValidationErrors() {
    ['errorNim', 'errorNama', 'errorIpk', 'errorEmail'].forEach(id => {
      const el = document.getElementById(id);
      if (el) {
        el.textContent = '';
        el.classList.add('hidden');
      }
    });
  }

  validateForm(data, mode, originalNim) {
    let isValid = true;
    this.clearValidationErrors();

    // NIM Validation
    const nimRegex = /^[0-9]{8,18}$/;
    if (!data.nim) {
      this.showFieldError('errorNim', 'NIM wajib diisi.');
      isValid = false;
    } else if (!nimRegex.test(data.nim)) {
      this.showFieldError('errorNim', 'NIM harus berupa angka (8 - 18 digit).');
      isValid = false;
    } else if (mode === 'add' && this.service.existsNim(data.nim)) {
      this.showFieldError('errorNim', `NIM ${data.nim} sudah terdaftar dalam sistem.`);
      isValid = false;
    }

    // Nama Validation
    if (!data.nama || data.nama.length < 3) {
      this.showFieldError('errorNama', 'Nama lengkap minimal 3 karakter.');
      isValid = false;
    }

    // IPK Validation
    const ipkVal = parseFloat(data.ipk);
    if (isNaN(ipkVal) || ipkVal < 0 || ipkVal > 4.0) {
      this.showFieldError('errorIpk', 'IPK harus di antara 0.00 dan 4.00.');
      isValid = false;
    }

    // Email Validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!data.email || !emailRegex.test(data.email)) {
      this.showFieldError('errorEmail', 'Format email tidak valid.');
      isValid = false;
    }

    return isValid;
  }

  showFieldError(elementId, message) {
    const el = document.getElementById(elementId);
    if (el) {
      el.textContent = message;
      el.classList.remove('hidden');
    }
  }

  handleFormSubmit(event) {
    event.preventDefault();

    const mode = document.getElementById('formMode').value;
    const originalNim = document.getElementById('originalNim').value;

    const nim = document.getElementById('inputNim').value.trim();
    const nama = document.getElementById('inputNama').value.trim();
    const prodi = document.getElementById('inputProdi').value;
    const fakultas = document.getElementById('inputFakultas').value || Mahasiswa.getFacultyByProdi(prodi);
    const angkatan = document.getElementById('inputAngkatan').value;
    const ipk = document.getElementById('inputIpk').value;
    const email = document.getElementById('inputEmail').value.trim();
    const status = document.getElementById('inputStatus').value;
    const alamat = document.getElementById('inputAlamat').value.trim();

    const formData = { nim, nama, prodi, fakultas, angkatan, ipk, email, status, alamat };

    if (!this.validateForm(formData, mode, originalNim)) {
      return;
    }

    try {
      if (mode === 'add') {
        const newMhs = new Mahasiswa(nim, nama, prodi, fakultas, angkatan, ipk, email, status, alamat);
        this.service.add(newMhs);
        this.closeModal();
        this.render();
        this.showToast(`Mahasiswa ${nama} berhasil didaftarkan ke pangkalan data.`, 'success');
      } else {
        this.service.update(originalNim, formData);
        this.closeModal();
        this.render();
        this.showToast(`Data mahasiswa ${nama} berhasil diperbarui.`, 'success');
      }
    } catch (err) {
      this.showToast(err.message, 'error');
    }
  }

  // ---------------- SQL CONSOLE & JAVA MODAL ----------------

  openSqlModal() {
    this.sqlModal.classList.remove('hidden');
    this.sqlModal.classList.add('flex');
    this.db.renderLogs();
    safeCreateIcons();
  }

  closeSqlModal() {
    this.sqlModal.classList.add('hidden');
    this.sqlModal.classList.remove('flex');
  }

  openJavaCodeModal() {
    this.javaCodeModal.classList.remove('hidden');
    this.javaCodeModal.classList.add('flex');
    safeCreateIcons();
  }

  closeJavaCodeModal() {
    this.javaCodeModal.classList.add('hidden');
    this.javaCodeModal.classList.remove('flex');
  }

  // ---------------- TOAST NOTIFICATION ----------------

  showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    let icon = 'info';
    let bgColor = 'bg-aegean text-white';
    if (type === 'success') {
      icon = 'check';
      bgColor = 'bg-emerald-600 text-white';
    } else if (type === 'error' || type === 'scarlet') {
      icon = 'alert-circle';
      bgColor = 'bg-scarlet text-white';
    } else if (type === 'sandy') {
      icon = 'bell';
      bgColor = 'bg-sandy text-white';
    }

    const toast = document.createElement('div');
    toast.className = `toast px-4 py-3 rounded-xl shadow-xl flex items-center gap-3 text-xs font-semibold ${bgColor} border border-white/20`;
    toast.innerHTML = `
      <i data-lucide="${icon}" class="w-4 h-4 shrink-0"></i>
      <span>${message}</span>
    `;

    container.appendChild(toast);
    safeCreateIcons();

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 3800);
  }
}

// -----------------------------------------------------------------------------
// 5. GLOBAL INITIALIZATION & HELPERS
// -----------------------------------------------------------------------------
let dbAuditLogger;
let studentService;
let ui;

if (isBrowser) {
  dbAuditLogger = new DatabaseAuditLogger();
  studentService = new MahasiswaService(dbAuditLogger);

  document.addEventListener('DOMContentLoaded', () => {
    ui = new UIManager(studentService, dbAuditLogger);
    window.ui = ui;
    window.dbAuditLogger = dbAuditLogger;
    window.studentService = studentService;
    dbAuditLogger.renderLogs();

    // Pastikan halaman awal selalu menampilkan layar login Whiteout
    sessionStorage.removeItem('siakad_admin_authenticated');
    checkAuthState();
  });
} else {
  // Mode Server / CLI Node.js (misalnya saat dijalankan dari VS Code Debugger)
  console.log('✅ [SIAKAD] Modul JavaScript backend siap digunakan.');
  console.log('ℹ️  Buka file index.html di browser untuk menjalankan antarmuka grafis SIAKAD.');
  if (typeof module !== 'undefined' && module.exports) {
    module.exports = { Mahasiswa, MahasiswaService, DatabaseAuditLogger };
  }
}

// -----------------------------------------------------------------------------
// AUTHENTICATION & SESSION MANAGEMENT (Admin Login / Logout)
// -----------------------------------------------------------------------------
function checkAuthState() {
  if (!isBrowser) return;
  const loginView = document.getElementById('loginView');
  const appView = document.getElementById('appView');
  if (!loginView || !appView) return;

  const isAuthenticated = sessionStorage.getItem('siakad_admin_authenticated') === 'true';
  if (isAuthenticated) {
    loginView.classList.add('hidden');
    loginView.classList.remove('flex');
    appView.classList.remove('hidden');
    appView.classList.add('flex');
    if (ui) ui.render();
    safeCreateIcons();
  } else {
    loginView.classList.remove('hidden');
    loginView.classList.add('flex');
    appView.classList.add('hidden');
    appView.classList.remove('flex');
    safeCreateIcons();
  }
}

function handleAdminLogin(event) {
  if (event) event.preventDefault();

  const userField = document.getElementById('loginUsername');
  const passField = document.getElementById('loginPassword');
  const errorAlert = document.getElementById('loginErrorAlert');
  const errorMsg = document.getElementById('loginErrorMessage');

  const username = userField ? userField.value.trim().toLowerCase() : '';
  const password = passField ? passField.value.trim() : '';

  if (username === 'admin' && password === 'admin') {
    if (errorAlert) errorAlert.classList.add('hidden');
    sessionStorage.setItem('siakad_admin_authenticated', 'true');
    checkAuthState();
    if (ui) {
      ui.showToast('Login berhasil. Selamat datang kembali, Administrator BAAK!', 'success');
    }
  } else {
    if (errorAlert) {
      errorAlert.classList.remove('hidden');
      if (errorMsg) errorMsg.textContent = 'Username atau password salah! Gunakan: admin / admin';
    }
    if (passField) {
      passField.focus();
      passField.classList.add('border-red-400');
    }
  }
}

function handleAdminLogout() {
  sessionStorage.removeItem('siakad_admin_authenticated');
  checkAuthState();
  if (ui) {
    ui.showToast('Anda telah keluar dari sesi administrator.', 'info');
  }
}

function autofillAndLogin() {
  const userField = document.getElementById('loginUsername');
  const passField = document.getElementById('loginPassword');
  if (userField) userField.value = 'admin';
  if (passField) passField.value = 'admin';
  handleAdminLogin();
}

function togglePasswordVisibility() {
  const passField = document.getElementById('loginPassword');
  const icon = document.getElementById('passwordToggleIcon');
  if (!passField) return;

  if (passField.type === 'password') {
    passField.type = 'text';
    if (icon) icon.setAttribute('data-lucide', 'eye-off');
  } else {
    passField.type = 'password';
    if (icon) icon.setAttribute('data-lucide', 'eye');
  }
  safeCreateIcons();
}

// Sidebar & Navigation Helpers
function toggleSidebar(forceState) {
  const sidebar = document.getElementById('sidebarNav');
  const backdrop = document.getElementById('sidebarBackdrop');
  if (!sidebar) return;

  const isClosed = sidebar.classList.contains('-translate-x-full');
  const shouldOpen = typeof forceState === 'boolean' ? forceState : isClosed;

  if (shouldOpen) {
    sidebar.classList.remove('-translate-x-full');
    if (backdrop) backdrop.classList.remove('hidden');
  } else {
    sidebar.classList.add('-translate-x-full');
    if (backdrop) backdrop.classList.add('hidden');
  }
}

function setActiveNav(navKey) {
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  const activeEl = document.getElementById(`nav-${navKey}`);
  if (activeEl) activeEl.classList.add('active');
  if (typeof window !== 'undefined' && window.innerWidth < 1024) {
    toggleSidebar(false);
  }
}

function filterByProdiSidebar(prodiName) {
  const select = document.getElementById('filterProdi');
  if (select) {
    select.value = prodiName;
    ui.currentPage = 1;
    ui.render();
  }

  // Highlight active sidebar prodi button
  document.querySelectorAll('.prodi-sidebar-btn').forEach(btn => {
    if ((prodiName === '' && btn.textContent.includes('Semua Program Studi')) ||
        (prodiName !== '' && btn.textContent.includes(prodiName))) {
      btn.classList.add('bg-white/15', 'text-white', 'font-bold');
      btn.classList.remove('text-slate-300');
    } else {
      btn.classList.remove('bg-white/15', 'text-white', 'font-bold');
      btn.classList.add('text-slate-300');
    }
  });

  // On mobile, close sidebar after selecting filter
  if (window.innerWidth < 1024) {
    toggleSidebar(false);
  }

  if (prodiName) {
    ui.showToast(`Menampilkan mahasiswa Program Studi: ${prodiName}`, 'info');
  }
}

// Helper functions for inline HTML event handlers
function openAddModal() { 
  if (typeof window !== 'undefined' && window.innerWidth < 1024) toggleSidebar(false);
  ui.openAddModal(); 
}
function closeModal() { ui.closeModal(); }
function closeDetailModal() { ui.closeDetailModal(); }
function closeDeleteModal() { ui.closeDeleteModal(); }
function openSqlModal() { 
  if (typeof window !== 'undefined' && window.innerWidth < 1024) toggleSidebar(false);
  ui.openSqlModal(); 
}
function closeSqlModal() { ui.closeSqlModal(); }
function openJavaCodeModal() { 
  if (typeof window !== 'undefined' && window.innerWidth < 1024) toggleSidebar(false);
  ui.openJavaCodeModal(); 
}
function closeJavaCodeModal() { ui.closeJavaCodeModal(); }
function openInfoModal() { ui.openJavaCodeModal(); }
function clearSqlLogs() { dbAuditLogger.clear(); }
function resetFilters() { ui.resetFilters(); }
function handleFormSubmit(e) { ui.handleFormSubmit(e); }

function resetDefaultData() {
  if (confirm('Apakah Anda yakin ingin menyinkronkan ulang data master mahasiswa?')) {
    studentService.resetToDefaults();
    ui.render();
    ui.showToast('Data master mahasiswa berhasil disinkronkan kembali.', 'sandy');
  }
}

function testConnection() {
  dbAuditLogger.logQuery('SELECT 1; -- Ping Server Basis Data MySQL', 'SELECT', 3);
  ui.showToast('Koneksi basis data MySQL aktif dan stabil (3ms).', 'success');
}

function syncFakultas() {
  const prodi = document.getElementById('inputProdi').value;
  document.getElementById('inputFakultas').value = Mahasiswa.getFacultyByProdi(prodi);
}

function sortTable(column) {
  const sortSelect = document.getElementById('sortBy');
  const currentVal = sortSelect.value;
  if (currentVal === `${column}-asc`) {
    sortSelect.value = `${column}-desc`;
  } else {
    sortSelect.value = `${column}-asc`;
  }
  ui.render();
}

// -------------------------------------------------------------
// RESPONSIVE 3D POP FORWARD EFFECT FOR TOP METRIC CARDS
// -------------------------------------------------------------
function toggleMetricCardForward(type) {
  const cards = {
    total: document.getElementById('cardStatTotal'),
    gpa: document.getElementById('cardStatGpa'),
    aktif: document.getElementById('cardStatAktif'),
    cumlaude: document.getElementById('cardStatCumlaude')
  };

  const targetCard = cards[type];
  if (!targetCard) return;

  const isAlreadyActive = targetCard.classList.contains('is-active-forward');

  // Deactivate all cards first
  Object.values(cards).forEach(card => {
    if (card) card.classList.remove('is-active-forward');
  });

  if (!isAlreadyActive) {
    // Highlight selected card
    targetCard.classList.add('is-active-forward');

    // Perform associated filtering quietly without toast messages
    if (type === 'aktif') {
      const filterStatus = document.getElementById('filterStatus');
      if (filterStatus) filterStatus.value = 'Aktif';
      if (ui) {
        ui.currentPage = 1;
        ui.render();
      }
    } else if (type === 'cumlaude') {
      const sortBy = document.getElementById('sortBy');
      if (sortBy) sortBy.value = 'ipk-desc';
      if (ui) {
        ui.render();
      }
    } else if (type === 'gpa') {
      const sortBy = document.getElementById('sortBy');
      if (sortBy) sortBy.value = 'ipk-desc';
      if (ui) {
        ui.render();
      }
    } else if (type === 'total') {
      if (ui) {
        ui.resetFilters();
      }
    }
  } else {
    // Clicked again -> return to normal state and reset filter
    if (ui) {
      ui.resetFilters();
    }
  }
}

function switchCodeTab(tabId) {
  document.querySelectorAll('.code-tab-btn').forEach(btn => {
    btn.classList.remove('active', 'border-aegean', 'text-aegean');
    btn.classList.add('border-transparent', 'text-slate-600');
  });
  
  document.querySelectorAll('.code-tab-content').forEach(content => {
    content.classList.add('hidden');
  });

  const activeContent = document.getElementById(tabId);
  if (activeContent) activeContent.classList.remove('hidden');

  event.currentTarget.classList.add('active', 'border-aegean', 'text-aegean');
  event.currentTarget.classList.remove('border-transparent', 'text-slate-600');
}

// ---------------- EXPORT FEATURES (CSV & JSON) ----------------
function exportCSV() {
  const students = studentService.getAll();
  if (students.length === 0) {
    ui.showToast('Tidak ada data untuk diekspor!', 'error');
    return;
  }

  const headers = ['NIM', 'Nama Mahasiswa', 'Program Studi', 'Fakultas', 'Angkatan', 'IPK', 'Email', 'Status', 'Alamat'];
  const rows = students.map(s => [
    `"${s.nim}"`,
    `"${s.nama.replace(/"/g, '""')}"`,
    `"${s.prodi}"`,
    `"${s.fakultas}"`,
    s.angkatan,
    s.ipk.toFixed(2),
    `"${s.email}"`,
    `"${s.status}"`,
    `"${(s.alamat || '').replace(/"/g, '""')}"`
  ]);

  const csvContent = 'data:text/csv;charset=utf-8,\uFEFF' + [headers.join(','), ...rows.map(e => e.join(','))].join('\n');
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement('a');
  link.setAttribute('href', encodedUri);
  link.setAttribute('download', `data_mahasiswa_siakad_${new Date().toISOString().slice(0, 10)}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  dbAuditLogger.logQuery("SELECT * INTO OUTFILE 'data_mahasiswa.csv' FIELDS TERMINATED BY ',' FROM mahasiswa;", 'SELECT');
  ui.showToast('Berhasil mengekspor data ke file CSV!', 'success');
}

function exportJSON() {
  const students = studentService.getAll();
  const dataStr = 'data:text/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(students, null, 2));
  const link = document.createElement('a');
  link.setAttribute('href', dataStr);
  link.setAttribute('download', `data_mahasiswa_${new Date().toISOString().slice(0, 10)}.json`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  ui.showToast('Berhasil mengekspor data ke format JSON!', 'success');
}

