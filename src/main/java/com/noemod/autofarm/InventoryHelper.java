package com.noemod.autofarm;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryHelper {

	public static int findItemSlotInHotbar(PlayerInventory inventory, Item item) {
		if (inventory == null || item == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inventory.getStack(i);
			if (!stack.isEmpty() && stack.getItem() == item) {
				return i;
			}
		}
		return -1;
	}

	public static boolean hasSeedInInventory(PlayerInventory inventory, Item item) {
		if (inventory == null || item == null) {
			return false;
		}
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (!stack.isEmpty() && stack.getItem() == item) {
				return true;
			}
		}
		return false;
	}

	public static int ensureSeedInHotbar(MinecraftClient client, Item seedItem) {
		if (client == null || client.player == null || seedItem == null) {
			return -1;
		}

		PlayerInventory inventory = client.player.getInventory();

		// 1. Cek apakah sudah ada di hotbar (slot 0-8)
		int hotbarSlot = findItemSlotInHotbar(inventory, seedItem);
		if (hotbarSlot != -1) {
			return hotbarSlot;
		}

		// 2. Cek apakah ada di tas utama (slot 9-35)
		int mainInvSlot = -1;
		for (int i = 9; i < 36; i++) {
			ItemStack stack = inventory.getStack(i);
			if (!stack.isEmpty() && stack.getItem() == seedItem) {
				mainInvSlot = i;
				break;
			}
		}

		if (mainInvSlot == -1) {
			return -1; // Tidak ada benih di tas
		}

		// 3. Tentukan slot hotbar target untuk swap
		int targetHotbar = -1;
		for (int i = 0; i < 9; i++) {
			if (inventory.getStack(i).isEmpty()) {
				targetHotbar = i;
				break;
			}
		}

		if (targetHotbar == -1) {
			targetHotbar = inventory.getSelectedSlot();
		}

		// 4. Lakukan swap slot dari tas ke hotbar
		if (client.interactionManager != null && client.player.playerScreenHandler != null) {
			client.interactionManager.clickSlot(
				client.player.playerScreenHandler.syncId,
				mainInvSlot,
				targetHotbar,
				SlotActionType.SWAP,
				client.player
			);
			return targetHotbar;
		}

		return -1;
	}

	public static void switchToSlot(MinecraftClient client, int hotbarSlot) {
		if (client == null || client.player == null || hotbarSlot < 0 || hotbarSlot > 8) {
			return;
		}

		if (client.player.getInventory().getSelectedSlot() != hotbarSlot) {
			client.player.getInventory().setSelectedSlot(hotbarSlot);
			if (client.player.networkHandler != null) {
				client.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(hotbarSlot));
			}
		}
	}
}
