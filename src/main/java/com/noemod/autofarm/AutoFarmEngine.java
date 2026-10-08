package com.noemod.autofarm;

import com.noemod.config.AutoFarmConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AutoFarmEngine {

	public static FarmingMode currentMode = FarmingMode.OFF;
	private static int cooldown = 0;

	public static void onClientTick(MinecraftClient client) {
		if (client == null || client.player == null || client.world == null) {
			return;
		}

		if (currentMode == FarmingMode.OFF) {
			return;
		}

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		ClientPlayerEntity player = client.player;
		World world = client.world;
		BlockPos playerPos = player.getBlockPos();
		int r = (int) Math.ceil(AutoFarmConfig.radius);
		double reachSq = 4.5 * 4.5;

		// Pembersihan berkala memori kadaluarsa setiap 100 tick (~5 detik)
		if (AutoFarmConfig.enableFarmMemory && world.getTime() % 100 == 0) {
			FarmMemoryManager.pruneInvalidAndExpired(world, player);
		}

		if (currentMode == FarmingMode.AUTOFARM) {
			handleAutoFarm(client, player, world, playerPos, r, reachSq);
		} else if (currentMode == FarmingMode.BONEMEAL) {
			handleAutoBoneMeal(client, player, world, playerPos, r, reachSq);
		}
	}

	private static void handleAutoFarm(MinecraftClient client, ClientPlayerEntity player, World world, BlockPos playerPos, int r, double reachSq) {
		// =========================================================================
		// PRIORITAS 1: Periksa antrean memori lahan kosong (Pending Replant)
		// =========================================================================
		if (AutoFarmConfig.enableFarmMemory) {
			BlockPos pendingTarget = FarmMemoryManager.findBestPendingTarget(player, world, reachSq);
			if (pendingTarget != null) {
				Item requiredSeed = FarmMemoryManager.getRequiredSeed(pendingTarget);
				if (requiredSeed != null) {
					int originalSlot = player.getInventory().getSelectedSlot();
					int seedSlot = InventoryHelper.ensureSeedInHotbar(client, requiredSeed);

					if (seedSlot != -1) {
						InventoryHelper.switchToSlot(client, seedSlot);

						BlockPos clickPos = (requiredSeed == Items.COCOA_BEANS) ? pendingTarget : pendingTarget.down();
						BlockHitResult hit = new BlockHitResult(
							Vec3d.ofBottomCenter(pendingTarget),
							Direction.UP,
							clickPos,
							false
						);

						if (client.interactionManager != null) {
							client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
							player.swingHand(Hand.MAIN_HAND);
						}

						InventoryHelper.switchToSlot(client, originalSlot);
						FarmMemoryManager.removeTarget(pendingTarget);
						cooldown = AutoFarmConfig.tickDelay;
						return; // Selesai menanam 1 lahan kosong tertunda, beri jeda cooldown
					}
				}
			}
		}

		// =========================================================================
		// PRIORITAS 2: Pindai dan panen tanaman matang di sekitar pemain
		// =========================================================================
		BlockPos bestTarget = null;
		double closestDistSq = Double.MAX_VALUE;

		for (int x = -r; x <= r; x++) {
			for (int y = -2; y <= 2; y++) {
				for (int z = -r; z <= r; z++) {
					BlockPos targetPos = playerPos.add(x, y, z);
					double distSq = player.squaredDistanceTo(Vec3d.ofCenter(targetPos));
					if (distSq > reachSq) {
						continue;
					}

					BlockState state = world.getBlockState(targetPos);
					if (CropHelper.isHarvestableCrop(state)) {
						if (distSq < closestDistSq) {
							closestDistSq = distSq;
							bestTarget = targetPos;
						}
					}
				}
			}
		}

		if (bestTarget != null) {
			BlockState state = world.getBlockState(bestTarget);
			Item matchingSeed = CropHelper.getMatchingSeed(state);
			int originalSlot = player.getInventory().getSelectedSlot();

			// 1. Pilih Hoe jika tersedia di hotbar
			if (AutoFarmConfig.useHoe) {
				int hoeSlot = ToolHelper.findBestHoeSlot(player.getInventory());
				if (hoeSlot != -1) {
					InventoryHelper.switchToSlot(client, hoeSlot);
				}
			}

			// 2. Panen tanaman matang
			if (client.interactionManager != null) {
				client.interactionManager.attackBlock(bestTarget, Direction.UP);
				player.swingHand(Hand.MAIN_HAND);
			}

			// 3. Tanam kembali benih secara otomatis
			if (AutoFarmConfig.autoReplant && matchingSeed != null) {
				int seedSlot = InventoryHelper.ensureSeedInHotbar(client, matchingSeed);
				if (seedSlot != -1) {
					InventoryHelper.switchToSlot(client, seedSlot);

					BlockPos clickPos = (matchingSeed == Items.COCOA_BEANS) ? bestTarget : bestTarget.down();
					BlockHitResult hit = new BlockHitResult(
						Vec3d.ofBottomCenter(bestTarget),
						Direction.UP,
						clickPos,
						false
					);

					if (client.interactionManager != null) {
						client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
						player.swingHand(Hand.MAIN_HAND);
					}
				} else if (AutoFarmConfig.enableFarmMemory) {
					// Benih tidak ada di hotbar maupun tas inventaris (0 benih / habis).
					// Simpan koordinat lahan dan jenis benih ke memori agar ditanam kembali saat benih didapat!
					FarmMemoryManager.recordEmptyFarmland(bestTarget, matchingSeed, world.getTime());
				}
			}

			// 4. Kembalikan slot awal pemain
			InventoryHelper.switchToSlot(client, originalSlot);
			cooldown = AutoFarmConfig.tickDelay;
		}
	}

	private static void handleAutoBoneMeal(MinecraftClient client, ClientPlayerEntity player, World world, BlockPos playerPos, int r, double reachSq) {
		int boneMealSlot = InventoryHelper.findItemSlotInHotbar(player.getInventory(), Items.BONE_MEAL);
		if (boneMealSlot == -1) {
			return; // Tidak ada Bone Meal di hotbar
		}

		BlockPos bestTarget = null;
		double closestDistSq = Double.MAX_VALUE;

		for (int x = -r; x <= r; x++) {
			for (int y = -2; y <= 2; y++) {
				for (int z = -r; z <= r; z++) {
					BlockPos targetPos = playerPos.add(x, y, z);
					double distSq = player.squaredDistanceTo(Vec3d.ofCenter(targetPos));
					if (distSq > reachSq) {
						continue;
					}

					BlockState state = world.getBlockState(targetPos);
					if (CropHelper.isFertilizableCrop(world, targetPos, state)) {
						if (distSq < closestDistSq) {
							closestDistSq = distSq;
							bestTarget = targetPos;
						}
					}
				}
			}
		}

		if (bestTarget != null) {
			int originalSlot = player.getInventory().getSelectedSlot();

			// Ganti ke Bone Meal
			InventoryHelper.switchToSlot(client, boneMealSlot);

			BlockHitResult hit = new BlockHitResult(
				Vec3d.ofCenter(bestTarget),
				Direction.UP,
				bestTarget,
				false
			);

			if (client.interactionManager != null) {
				client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
				player.swingHand(Hand.MAIN_HAND);
			}

			// Kembalikan slot awal pemain
			InventoryHelper.switchToSlot(client, originalSlot);
			cooldown = AutoFarmConfig.tickDelay;
		}
	}
}
