package com.noemod.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MessageHelper {

	public static void sendActionBar(String message) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null && client.player != null) {
			client.player.sendMessage(Text.literal(message), true);
		}
	}
}
