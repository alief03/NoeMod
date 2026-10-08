package com.noemod.autofarm;

import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FarmMemoryManager {

	public record PendingReplant(
		BlockPos cropPos,
		Item requiredSeed,
		long addedGameTime
	) {}

	private static final Map<BlockPos, PendingReplant> pendingReplants = new ConcurrentHashMap<>();
	private static final long MAX_RETENTION_TICKS = 6000L; // 5 menit (20 tick * 60 detik * 5)
	private static final double MAX_DISTANCE_SQ = 48.0 * 48.0;
	private static final int MAX_QUEUE_SIZE = 500;

	public static void recordEmptyFarmland(BlockPos pos, Item requiredSeed, long gameTime) {
		if (pos == null || requiredSeed == null) {
			return;
		}

		if (pendingReplants.size() >= MAX_QUEUE_SIZE) {
			BlockPos oldest = null;
			long oldestTime = Long.MAX_VALUE;
			for (PendingReplant pr : pendingReplants.values()) {
				if (pr.addedGameTime() < oldestTime) {
					oldestTime = pr.addedGameTime();
					oldest = pr.cropPos();
				}
			}
			if (oldest != null) {
				pendingReplants.remove(oldest);
			}
		}

		pendingReplants.put(pos.toImmutable(), new PendingReplant(pos.toImmutable(), requiredSeed, gameTime));
	}

	public static BlockPos findBestPendingTarget(ClientPlayerEntity player, World world, double reachSq) {
		if (pendingReplants.isEmpty() || player == null || world == null) {
			return null;
		}

		BlockPos bestPos = null;
		double closestDistSq = Double.MAX_VALUE;

		for (Map.Entry<BlockPos, PendingReplant> entry : pendingReplants.entrySet()) {
			BlockPos pos = entry.getKey();
			PendingReplant pending = entry.getValue();

			// 1. Validasi jarak jangkau pemain
			double distSq = player.squaredDistanceTo(Vec3d.ofCenter(pos));
			if (distSq > reachSq) {
				continue;
			}

			// 2. Validasi kondisi blok di dunia
			BlockState cropState = world.getBlockState(pos);
			BlockState soilState = world.getBlockState(pos.down());

			// Jika blok sudah terisi (bukan air), hapus dari memori
			if (!cropState.isAir()) {
				pendingReplants.remove(pos);
				continue;
			}

			// Validasi jenis tanah di bawahnya
			if (!CropHelper.isValidSoilForSeed(soilState, pending.requiredSeed())) {
				pendingReplants.remove(pos);
				continue;
			}

			// 3. Validasi apakah pemain memiliki benih yang cocok (di hotbar atau tas)
			if (!InventoryHelper.hasSeedInInventory(player.getInventory(), pending.requiredSeed())) {
				continue;
			}

			// 4. Pilih target terdekat
			if (distSq < closestDistSq) {
				closestDistSq = distSq;
				bestPos = pos;
			}
		}

		return bestPos;
	}

	public static Item getRequiredSeed(BlockPos pos) {
		PendingReplant pr = pendingReplants.get(pos);
		return pr != null ? pr.requiredSeed() : null;
	}

	public static void removeTarget(BlockPos pos) {
		if (pos != null) {
			pendingReplants.remove(pos);
		}
	}

	public static void pruneInvalidAndExpired(World world, ClientPlayerEntity player) {
		if (pendingReplants.isEmpty()) {
			return;
		}

		long now = world != null ? world.getTime() : 0;

		pendingReplants.entrySet().removeIf(entry -> {
			BlockPos pos = entry.getKey();
			PendingReplant pr = entry.getValue();

			// Kedaluwarsa durasi
			if (now > 0 && (now - pr.addedGameTime() > MAX_RETENTION_TICKS)) {
				return true;
			}

			// Terlalu jauh dari posisi pemain
			if (player != null && player.squaredDistanceTo(Vec3d.ofCenter(pos)) > MAX_DISTANCE_SQ) {
				return true;
			}

			// Jika pemain berada cukup dekat (<= 32 blok), kita validasi blok langsung di dunia
			if (player != null && player.squaredDistanceTo(Vec3d.ofCenter(pos)) <= 32.0 * 32.0 && world != null) {
				BlockState cropState = world.getBlockState(pos);
				BlockState soilState = world.getBlockState(pos.down());
				if (!cropState.isAir() || !CropHelper.isValidSoilForSeed(soilState, pr.requiredSeed())) {
					return true;
				}
			}

			return false;
		});
	}

	public static void clear() {
		pendingReplants.clear();
	}

	public static int getPendingCount() {
		return pendingReplants.size();
	}
}
