# Product Requirement Document (PRD)
## Noe-Mod: AutoFarm Fabric Mod (Minecraft Java 1.21.11)

---

### 1. Ringkasan Eksekutif (Executive Summary)
**Noe-Mod** adalah mod Minecraft berbasis **Fabric Loader** yang secara spesifik ditargetkan untuk **Minecraft Java Edition 1.21.11** dengan **Fabric Loader 0.19.3** dan **Java 21**. Mod ini menggabungkan fitur otomatisasi pertanian, proteksi lahan, dan kecerdasan memori penanaman:
1. **AutoFarm Otomatis**: Memindai tanaman siap panen di sekitar pemain, menggunakan cangkul (**Hoe**) secara cerdas jika tersedia (memanfaatkan efisiensi & Fortune), memanen tanaman, serta langsung menanam kembali benih yang sesuai ke lahan pertanian (*Farmland*).
2. **Auto Bone Meal (Pemupukan Otomatis)**: Memindai tanaman muda/belum matang di sekitar pemain dan mengaplikasikan **Bone Meal** secara otomatis dari inventaris hingga tanaman mencapai pertumbuhan maksimal.
3. **Proteksi Pasif Farmland (Anti-Trample Farmland)**: Fitur pasif via Mixin yang menjaga blok *Farmland* agar **tidak hancur / tidak berubah menjadi Dirt biasa** saat pemain berjalan, berlari, atau melompat di atasnya.
4. **Memori Lahan Kosong & Penanaman Tertunda (Pending Replant Memory)**: Menyimpan koordinat lahan bekas panen yang belum sempat ditanami akibat ketiadaan atau kehabisan benih (baik saat pertama kali panen tanpa benih maupun saat benih habis di tengah panen ladang luas), lalu secara otomatis menanami kembali lahan kosong tersebut begitu benih berhasil diperoleh atau masuk ke inventaris.

> [!IMPORTANT]
> **Aturan Eksklusivitas Mode (Mutually Exclusive)**:
> Fitur **AutoFarm** dan **Auto Bone Meal** **TIDAK DAPAT AKTIF BERSAMAAN**.
> Mengaktifkan salah satu mode otomatis akan mematikan mode yang lain untuk mencegah konflik alat/item di hotbar dan menjaga kontrol pemain yang presisi.

---

### 2. Aturan Versi Semantik (Semantic Versioning Rules)
Proyek ini mengadopsi standar **SemVer 2.0.0** dengan konvensi penamaan Fabric Mod:
$$\text{MAJOR}.\text{MINOR}.\text{PATCH}+\text{MINECRAFT\_VERSION}$$

- **Format Versi Target Saat Ini**: `1.1.0+1.21.11` (Peningkatan MINOR dari `1.0.0+1.21.11` karena penambahan kapabilitas fitur baru *backward-compatible*: Memori Lahan Kosong & Penanaman Tertunda).
- **Aturan Kenaikan Versi**:
  - **MAJOR (X.0.0)**: Perubahan arsitektur besar yang merusak kompatibilitas lama (*breaking changes*), perombakan total engine interaksi, atau migrasi arsitektur loader.
  - **MINOR (1.X.0)**: Penambahan fitur baru yang *backward-compatible* (misalnya: penambahan proteksi pasif Farmland, penambahan Auto Bone Meal, penambahan GUI Settings, atau penambahan sistem memori penanaman tertunda F-10).
  - **PATCH (1.1.X)**: Perbaikan bug, optimasi performa tick, penyempurnaan durabilitas alat, atau perbaikan desinkronisasi packet (*bug fixes*).
  - **Build Metadata (`+1.21.11`)**: Menandai versi target Minecraft Java Edition yang didukung secara eksplisit.

---

### 3. Lingkup & Target Teknis (Technical Scope)
| Parameter | Spesifikasi |
|---|---|
| **Platform** | Minecraft: Java Edition |
| **Versi Minecraft** | **1.21.11** |
| **Mod Loader** | Fabric Loader `0.19.3` |
| **Java Version** | Java 21 (LTS) |
| **Arsitektur Mod** | Client-Side Interaction, Mutually Exclusive State Machine, Pending Farm Memory & Mixin Injection |
| **Standar Versi** | SemVer 2.0.0 (`1.1.0+1.21.11`) |
| **Branding / Logo** | `D:\laragon\www\Noe-Mod\avatarHead3.png` |
| **Direktori Output Hasil** | **`D:\laragon\www\Noe-Mod\Hasil\`** |

---

### 4. Fitur Utama (Core Features)

#### F-1: Deteksi Tanaman Matang (Crop Maturity Detection)
- Memindai blok di sekitar pemain dalam radius terkonfigurasi (default: 4.0 blok).
- Mendukung jenis tanaman pertanian:
  - **Gandum (Wheat)**: `Blocks.WHEAT` (Age = 7)
  - **Wortel (Carrots)**: `Blocks.CARROTS` (Age = 7)
  - **Kentang (Potatoes)**: `Blocks.POTATOES` (Age = 7)
  - **Bit (Beetroots)**: `Blocks.BEETROOTS` (Age = 3)
  - **Nether Wart**: `Blocks.NETHER_WART` (Age = 3)
  - *(Opsional)*: **Biji Kakao (Cocoa Beans)** (Age = 2).
- Menargetkan blok tanaman yang telah mencapai usia pertumbuhan maksimal (`isMature()`).

#### F-2: Pemilihan Alat Otomatis (Smart Hoe Usage)
- Saat tanaman matang terdeteksi untuk dipanen:
  1. Mod memeriksa Hotbar pemain untuk item cangkul (`HoeItem`).
  2. Prioritaskan cangkul dengan durabilitas aman (> 5) atau yang memiliki enchantment `Fortune`.
  3. Mod menukar slot hotbar aktif ke cangkul sebelum mengirim aksi penghancuran blok (*break block*).
  4. Setelah blok hancur, slot hotbar dialihkan ke benih atau dikembalikan ke item sebelumnya.
  5. Jika tidak ada cangkul di inventaris, pemanenan tetap berjalan menggunakan tangan/item aktif.

#### F-3: Pemanenan Otomatis (Auto-Harvest)
- Menggunakan `ClientPlayerInteractionManager.attackBlock()` pada koordinat blok target.
- Memiliki sistem **Action Queue / Cooldown Delay** (default: 1 panen per 3 tick) agar pergerakan alami dan aman dari anti-cheat kick.

#### F-4: Penanaman Kembali Otomatis (Auto-Replant Langsung)
- Setelah blok tanaman dipanen dan menjadi blok Farmland kosong (atau Soul Sand):
  1. Memeriksa ketersediaan benih yang sesuai di hotbar/inventaris:
     - Gandum $\rightarrow$ `Items.WHEAT_SEEDS`
     - Wortel $\rightarrow$ `Items.CARROT`
     - Kentang $\rightarrow$ `Items.POTATO`
     - Bit $\rightarrow$ `Items.BEETROOT_SEEDS`
     - Nether Wart $\rightarrow$ `Items.NETHER_WART`
  2. Jika benih tersedia: Beralih ke slot benih dan mengirim interaksi klik kanan (`interactBlock`) pada sisi atas blok Farmland (`Direction.UP`).
  3. Tanaman langsung tertanam kembali.
  4. **Jika benih TIDAK tersedia**: Koordinat lahan dan tipe benih yang dibutuhkan diteruskan ke **F-10 (Memori Lahan Kosong)**.

#### F-5: Pemupukan Otomatis (Auto Bone Meal)
- Hanya aktif ketika mode **BONE MEAL** dipilih (tombol `B`).
- Memindai blok tanaman belum matang dalam radius jangkauan yang mengimplementasikan interface `Fertilizable`.
- Memeriksa keberadaan `Items.BONE_MEAL` di hotbar atau inventaris utama.
- Jika Bone Meal tersedia:
  - Menukar slot aktif pemain ke slot Bone Meal.
  - Mengirim interaksi klik kanan (`interactBlock`) ke tanaman tersebut.
- Jika Bone Meal habis, mod berhenti sementara tanpa menimbulkan error loop.

#### F-6: Proteksi Pasif Farmland (Passive Anti-Trample Farmland)
- **Tujuan**: Mencegah blok `Farmland` terinjak dan berubah menjadi blok `Dirt` biasa saat pemain melompat, jatuh, berlari, atau mendarat di atas lahan pertanian.
- **Mekanisme**:
  - Diimplementasikan melalui Mixin Fabric (`FarmlandBlockMixin`) yang mencegat `onLandedUpon(World, BlockState, BlockPos, Entity, float)`.
  - Jika entitas yang mendarat adalah pemain (`PlayerEntity`), aksi konversi lahan ke `Dirt` dibatalkan (`ci.cancel()`), namun fall damage normal pada pemain tetap berjalan.
  - Tanaman muda yang sedang tumbuh terlindungi dari risiko rontok akibat lompatan.
  - Berjalan **secara pasif terus menerus** tanpa terpengaruh mode toggle aktif/nonaktif.

#### F-7: Kontrol & Status UI (Mutually Exclusive Dual Keybinds)
- **Sistem State Machine**:
  - `FarmingMode.OFF`: Semua otomatisasi mati (dan memori lahan sementara dibersihkan).
  - `FarmingMode.AUTOFARM`: AutoFarm aktif (Bone Meal nonaktif).
  - `FarmingMode.BONEMEAL`: Auto Bone Meal aktif (AutoFarm nonaktif).
- **Tombol Pintas (Keybind)**:
  1. **Tombol `H` (AutoFarm)**:
     - Jika sedang `OFF` atau `BONEMEAL` $\rightarrow$ beralih ke `AUTOFARM`. Notifikasi: `§a[Noe-Mod] Mode: AUTOFARM AKTIF`.
     - Jika sedang `AUTOFARM` $\rightarrow$ beralih ke `OFF`. Notifikasi: `§c[Noe-Mod] AutoFarm: NONAKTIF`.
  2. **Tombol `B` (Auto Bone Meal)**:
     - Jika sedang `OFF` atau `AUTOFARM` $\rightarrow$ beralih ke `BONEMEAL`. Notifikasi: `§b[Noe-Mod] Mode: AUTO BONE MEAL AKTIF`.
     - Jika sedang `BONEMEAL` $\rightarrow$ beralih ke `OFF`. Notifikasi: `§c[Noe-Mod] Auto Bone Meal: NONAKTIF`.
- **Konfigurasi (`config/noemod.json`)**:
  - `radius`: Jangkauan scan (1 - 6 blok, default 4.0).
  - `tickDelay`: Jeda antar aksi dalam ticks (default 3 ticks).
  - `useHoe`: Boolean (aktif/nonaktif pemakaian cangkul otomatis, default true).
  - `autoReplant`: Boolean (aktif/nonaktif penanaman kembali, default true).
  - `protectFarmland`: Boolean (aktif/nonaktif proteksi pasif anti-trample, default true).
  - `enableFarmMemory`: Boolean (aktif/nonaktif memori penanaman tertunda, default true).

#### F-8: Branding & Identitas Mod
- Menggunakan file `avatarHead3.png` sebagai ikon resmi mod:
  - File disalin dan dioptimalkan ke `assets/noemod/icon.png`.
  - Tampil pada Mod Menu Fabric dan in-game mod list.
- Informasi mod di `fabric.mod.json`:
  - `id`: `noemod`
  - `name`: `Noe AutoFarm`
  - `version`: `1.1.0+1.21.11`
  - `icon`: `assets/noemod/icon.png`

#### F-9: Integrasi ModMenu & Layar Pengaturan GUI (ModMenu & GUI Config Screen)
- **Tujuan**: Memungkinkan pemain menyesuaikan konfigurasi mod secara langsung di dalam game melalui antarmuka visual ModMenu tanpa perlu mengedit file JSON secara manual.
- **Mekanisme**:
  - Mengimplementasikan `ModMenuApi` melalui `NoeModMenuIntegration.java` dengan entrypoint `"modmenu"` di `fabric.mod.json`.
  - Menggunakan layar GUI vanilla `NoeModConfigScreen.java` (`net.minecraft.client.gui.screen.Screen`):
    1. **Slider Radius Deteksi**: Mengatur jarak pindaian blok (1.0 s/d 6.0 blok).
    2. **Slider Jeda Waktu (Tick Delay)**: Mengatur delay per aksi (1 s/d 20 tick) untuk keamanan anti-cheat.
    3. **Tombol Toggle Gunakan Cangkul**: Menyalakan / mematikan pemilihan cangkul otomatis.
    4. **Tombol Toggle Tanam Otomatis**: Menyalakan / mematikan penanaman bibit otomatis setelah panen.
    5. **Tombol Toggle Proteksi Farmland**: Menyalakan / mematikan proteksi anti-trample lahan pertanian.
    6. **Tombol Toggle Memori Lahan**: Menyalakan / mematikan fitur antrean penanaman tertunda F-10.
    7. **Tombol Selesai & Simpan**: Menyimpan perubahan langsung ke `.minecraft/config/noemod.json`.
- **Kompatibilitas**: Opsional runtime (`modCompileOnly`), aman digunakan baik dengan ModMenu terpasang maupun tanpa ModMenu.

#### F-10: Memori Lahan Kosong & Penanaman Tertunda (Pending Replant Memory Queue)
- **Latar Belakang & Masalah**:
  1. *Kasus 1*: Pemain memulai panen pertama kali dengan 0 benih di inventaris. Tanaman matang dipanen dan menjatuhkan benih ke tanah, tetapi blok lahan menjadi kosong (*Farmland* kosong).
  2. *Kasus 2*: Benih di inventaris/hotbar habis di tengah-tengah proses pemanenan ladang besar. Tanaman berikutnya dipanen dan menghasilkan benih drop baru, namun lahan-lahan sebelumnya sudah terlanjur tertinggal kosong tanpa benih.
  3. *Kelemahan Tanpa Memori*: Scanner panen konvensional hanya mencari tanaman matang (`CropHelper.isHarvestableCrop`). Akibatnya lahan kosong yang tertinggal tidak akan pernah ditanami kembali kecuali pemain menanamnya secara manual.
- **Solusi & Spesifikasi Perilaku**:
  1. **Perekaman Memori (Recording)**:
     - Setiap kali tanaman dipanen dan penanaman langsung gagal/tidak dapat dilakukan karena ketiadaan benih, posisi koordinat blok (`BlockPos`) dan jenis benih yang sesuai (`Item matchingSeed`) disimpan ke antrean memori internal (`FarmMemoryManager`).
  2. **Refill Benih Cerdas (Smart Inventory Refill)**:
     - Jika benih tidak ada di hotbar (slot 0-8) namun ada di inventaris utama (slot 9-35), mod secara otomatis memindahkan benih ke hotbar agar selalu siap digunakan.
  3. **Prioritas Eksekusi Penanaman Kembali (Priority Replanting)**:
     - Pada setiap tick AutoFarm: mod memprioritaskan mengecek antrean memori lahan kosong (`pendingReplantQueue`) yang berada dalam jarak jangkau pemain (`distSq <= reachSq`).
     - Jika pemain memiliki benih yang cocok dan lahan kosong tersebut valid:
       - Mod segera menanam benih ke koordinat lahan kosong tersebut.
       - Menghapus koordinat tersebut dari memori.
       - Memicu cooldown tick delay agar tidak terdeteksi spam.
  4. **Pembersihan & Validasi Otomatis (Memory Pruning)**:
     - Memori memvalidasi target sebelum ditanam: jika blok sudah ditanam atau bukan lagi Farmland/Soul Sand, entri langsung dihapus dari memori.
     - Membersihkan entri yang berada di luar jangkauan (> 48 blok) atau melebihi batas waktu retensi (5 menit) untuk mencegah kebocoran memori.
     - Membersihkan seluruh antrean memori saat mode AutoFarm dimatikan (`FarmingMode.OFF`) atau pemain berpindah server/world.

---

### 5. Matriks Kondisi Operasi Eksklusif

| Mode Terpilih | Tombol Pemicu | Aktivitas yang Dijalankan | Tindakan yang Diblokir |
|---|---|---|---|
| **AUTOFARM** | Tekan **`H`** | 1. Memeriksa antrean memori lahan kosong $\rightarrow$ menanam benih tertunda jika benih tersedia.<br>2. Memanen tanaman matang dengan cangkul $\rightarrow$ menanam kembali bibit (atau mencatat ke memori jika benih habis). | Tidak akan menggunakan Bone Meal sama sekali |
| **BONE MEAL** | Tekan **`B`** | Pemupukan tanaman muda dengan Bone Meal hingga matang | Tidak akan memanen atau merusak tanaman; memori replant tidak dieksekusi |
| **OFF (Mati)** | Tekan tombol aktif | Tidak ada tindakan aktif yang dikirim ke server; antrean memori lahan dibersihkan | Semua otomatisasi aktif mati |
| **Proteksi Farmland** | *Pasif Selalu* | Mencegah Farmland hancur saat dilompati pemain | Berjalan di seluruh mode di atas |

---

### 6. Kriteria Keberhasilan & Pengiriman (Deliverables)
1. Mod berhasil dimuat di Fabric Loader 0.19.3 pada **Minecraft Java 1.21.11** dengan versi **`1.1.0+1.21.11`**.
2. Ikon mod `avatarHead3.png` muncul di Mod Menu.
3. Menekan `H` saat Bone Meal sedang aktif akan mematikan Bone Meal dan mengaktifkan AutoFarm (tidak pernah aktif bersamaan).
4. Menekan `B` saat AutoFarm sedang aktif akan mematikan AutoFarm dan mengaktifkan Bone Meal.
5. Pemain melompat di atas Farmland tidak menghancurkan lahan menjadi tanah biasa dalam kondisi apa pun.
6. Dalam mode AutoFarm, cangkul digunakan untuk panen dan benih ditanam kembali jika tersedia.
7. **Lahan bekas panen yang kosong akibat ketiadaan/kehabisan benih (baik panen pertama kali tanpa benih maupun kehabisan benih di tengah panen) tercatat dalam memori dan otomatis ditanami kembali begitu benih diperoleh**.
8. Pengambilan benih dari inventaris utama ke hotbar berjalan mulus tanpa mengganggu aktivitas pemain.
9. Dalam mode Bone Meal, Bone Meal diaplikasikan pada tanaman muda tanpa merusak tanaman.
10. **File JAR final hasil build otomatis tersalin dan tersedia di direktori `D:\laragon\www\Noe-Mod\Hasil\`**.

