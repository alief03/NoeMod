package com.noemod;

import com.noemod.autofarm.AutoFarmEngine;
import com.noemod.autofarm.FarmingMode;
import com.noemod.util.MessageHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class NoeModClient implements ClientModInitializer {

	public static KeyBinding keyToggleAutoFarm;
	public static KeyBinding keyToggleBoneMeal;

	public static final KeyBinding.Category NOEMOD_CATEGORY = KeyBinding.Category.create(net.minecraft.util.Identifier.of("noemod", "general"));

	@Override
	public void onInitializeClient() {
		// Registrasi tombol H untuk AutoFarm
		keyToggleAutoFarm = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.noemod.toggle_autofarm",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_H,
			NOEMOD_CATEGORY
		));

		// Registrasi tombol B untuk Auto Bone Meal
		keyToggleBoneMeal = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.noemod.toggle_bonemeal",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_B,
			NOEMOD_CATEGORY
		));

		// Handler tick client
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyToggleAutoFarm.wasPressed()) {
				if (AutoFarmEngine.currentMode == FarmingMode.AUTOFARM) {
					AutoFarmEngine.currentMode = FarmingMode.OFF;
					com.noemod.autofarm.FarmMemoryManager.clear();
					MessageHelper.sendActionBar("§c[Noe-Mod] AutoFarm: NONAKTIF");
				} else {
					AutoFarmEngine.currentMode = FarmingMode.AUTOFARM;
					MessageHelper.sendActionBar("§a[Noe-Mod] Mode: AUTOFARM AKTIF");
				}
			}

			while (keyToggleBoneMeal.wasPressed()) {
				if (AutoFarmEngine.currentMode == FarmingMode.BONEMEAL) {
					AutoFarmEngine.currentMode = FarmingMode.OFF;
					MessageHelper.sendActionBar("§c[Noe-Mod] Auto Bone Meal: NONAKTIF");
				} else {
					if (AutoFarmEngine.currentMode == FarmingMode.AUTOFARM) {
						com.noemod.autofarm.FarmMemoryManager.clear();
					}
					AutoFarmEngine.currentMode = FarmingMode.BONEMEAL;
					MessageHelper.sendActionBar("§b[Noe-Mod] Mode: AUTO BONE MEAL AKTIF");
				}
			}

			AutoFarmEngine.onClientTick(client);
		});
	}
}
