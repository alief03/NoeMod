package com.noemod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class AutoFarmConfig {
	public static double radius = 4.0;
	public static int tickDelay = 3;
	public static boolean useHoe = true;
	public static boolean autoReplant = true;
	public static boolean protectFarmland = true;
	public static boolean enableFarmMemory = true;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("noemod.json");

	public static class ConfigData {
		public double radius = 4.0;
		public int tickDelay = 3;
		public boolean useHoe = true;
		public boolean autoReplant = true;
		public boolean protectFarmland = true;
		public boolean enableFarmMemory = true;
	}

	public static void load() {
		File file = CONFIG_PATH.toFile();
		if (!file.exists()) {
			save();
			return;
		}

		try (FileReader reader = new FileReader(file)) {
			ConfigData data = GSON.fromJson(reader, ConfigData.class);
			if (data != null) {
				radius = Math.max(1.0, Math.min(6.0, data.radius));
				tickDelay = Math.max(1, Math.min(20, data.tickDelay));
				useHoe = data.useHoe;
				autoReplant = data.autoReplant;
				protectFarmland = data.protectFarmland;
				enableFarmMemory = data.enableFarmMemory;
			}
		} catch (Exception e) {
			System.err.println("[Noe-Mod] Gagal memuat config, menggunakan nilai default: " + e.getMessage());
			save();
		}
	}

	public static void save() {
		ConfigData data = new ConfigData();
		data.radius = radius;
		data.tickDelay = tickDelay;
		data.useHoe = useHoe;
		data.autoReplant = autoReplant;
		data.protectFarmland = protectFarmland;
		data.enableFarmMemory = enableFarmMemory;

		try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
			GSON.toJson(data, writer);
		} catch (IOException e) {
			System.err.println("[Noe-Mod] Gagal menyimpan config: " + e.getMessage());
		}
	}
}
