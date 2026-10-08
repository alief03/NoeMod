package com.noemod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoeMod implements ModInitializer {
	public static final String MOD_ID = "noemod";
	public static final String MOD_NAME = "Noe AutoFarm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		com.noemod.config.AutoFarmConfig.load();
		LOGGER.info("{} (Minecraft 1.21.11) initialized successfully!", MOD_NAME);
	}
}
