package com.noemod.autofarm;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;

public class ToolHelper {

	/**
	 * Mencari slot cangkul (Hoe) terbaik di hotbar pemain (slot 0-8).
	 * Mengabaikan cangkul yang durabilitasnya hampir habis (<= 3).
	 * Mengembalikan indeks slot hotbar (0-8), atau -1 jika tidak ada.
	 */
	public static int findBestHoeSlot(PlayerInventory inventory) {
		int bestSlot = -1;
		int highestScore = -1;

		for (int i = 0; i < 9; i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty() || !(stack.getItem() instanceof HoeItem)) {
				continue;
			}

			// Proteksi durabilitas agar cangkul tidak pecah
			if (stack.isDamageable()) {
				int remainingDurability = stack.getMaxDamage() - stack.getDamage();
				if (remainingDurability <= 3) {
					continue;
				}
			}

			int score = calculateHoeScore(stack);
			if (score > highestScore) {
				highestScore = score;
				bestSlot = i;
			}
		}

		return bestSlot;
	}

	private static int calculateHoeScore(ItemStack stack) {
		int score = 10;
		String translationKey = stack.getItem().getTranslationKey().toLowerCase();

		if (translationKey.contains("netherite")) {
			score += 60;
		} else if (translationKey.contains("diamond")) {
			score += 50;
		} else if (translationKey.contains("iron")) {
			score += 40;
		} else if (translationKey.contains("stone")) {
			score += 30;
		} else if (translationKey.contains("golden")) {
			score += 20;
		} else if (translationKey.contains("wooden")) {
			score += 10;
		}

		return score;
	}
}
