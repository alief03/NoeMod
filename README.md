# Noe AutoFarm (Fabric Mod)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-brightgreen.svg)](https://minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.19.3+-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.1.0%2B1.21.11-purple.svg)](https://github.com/alief03/NoeMod/releases)

<p align="center">
  <img src="avatarHead3.png" alt="Noe AutoFarm Logo" width="128" height="128">
</p>

Mod client-side Fabric untuk Minecraft Java Edition **1.21.11** yang dirancang untuk mempermudah aktivitas bertani (farming) secara otomatis, cerdas, aman, dan efisien.

---

## ✨ Fitur Utama

- 🌾 **Smart AutoFarm & Auto-Replant (`Keybind: H`)**
  - Memindai dan memanen tanaman yang telah matang (*Wheat, Carrot, Potato, Beetroot, Nether Wart*) secara otomatis dalam radius tertentu.
  - Secara otomatis menukar tool ke cangkul (*Hoe*) yang paling optimal saat memanen.
  - Menanam kembali (*replant*) benih yang sesuai ke tanah pertanian secara instan.
  - Auto-refill benih dari inventaris utama ke hotbar jika slot hotbar habis.

- 🦴 **Auto Bone Meal Mode (`Keybind: B`)**
  - Memupuk tanaman muda secara otomatis hingga matang sepenuhnya.
  - Bersifat **Mutually Exclusive** dengan mode AutoFarm untuk menjaga kestabilan dan efisiensi farming.

- 🧠 **Pending Replant Memory (Farm Memory Manager)**
  - Mengingat titik lahan kosong saat pemain kehabisan benih selama panen.
  - Segera setelah pemain memperoleh atau memegang benih kembali, mod akan langsung menanami lahan kosong yang tersimpan dalam antrean memori.

- 🛡️ **Proteksi Pasif Anti-Trample Farmland**
  - Mencegah tanah pertanian (*Farmland*) berubah menjadi *Dirt* saat pemain melompat atau mendarat di atasnya (Zero-tick mixin event).

- ⚙️ **Konfigurasi Dalam Game (Mod Menu Support)**
  - Tampilan GUI konfigurasi bawaan yang dapat disesuaikan (jangkauan radius, tick delay, toggle fitur memori, toggle proteksi farmland).

---

## 📋 Persyaratan Sistem

- **Minecraft**: Java Edition `1.21.11`
- **Fabric Loader**: Versi `0.19.3` atau lebih baru
- **Fabric API**: Versi yang kompatibel dengan 1.21.11
- **Java Runtime**: Java 21+

---

## 📥 Instalasi

1. Pastikan Anda telah memasang **Fabric Loader** untuk Minecraft 1.21.11.
2. Unduh file `noemod-1.1.0+1.21.11.jar` dari halaman [Releases](https://github.com/alief03/NoeMod/releases).
3. Letakkan file `.jar` ke dalam folder `.minecraft/mods/`.
4. Pastikan **Fabric API** juga terpasang di folder `mods/`.
5. Jalankan Minecraft dan nikmati fitur AutoFarm cerdas!

---

## 🛠️ Kompilasi dari Source Code (Build)

Jika Anda ingin melakukan kompilasi mandiri dari kode sumber:

```bash
# Clone repository
git clone https://github.com/alief03/NoeMod.git
cd NoeMod

# Kompilasi JAR menggunakan Gradle Wrapper
./gradlew build
```

File output hasil build akan berada di `build/libs/`.

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah lisensi **MIT**. Silakan lihat file [fabric.mod.json](fabric.mod.json) untuk detail atribusi.
