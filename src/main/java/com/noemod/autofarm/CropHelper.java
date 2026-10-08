package com.noemod.autofarm;

import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CropHelper {

	public static boolean isMature(BlockState state) {
		Block block = state.getBlock();

		if (block instanceof CropBlock cropBlock) {
			return cropBlock.isMature(state);
		} else if (block instanceof NetherWartBlock) {
			return state.get(NetherWartBlock.AGE) >= 3;
		} else if (block instanceof CocoaBlock) {
			return state.get(CocoaBlock.AGE) >= 2;
		}

		return false;
	}

	public static boolean isHarvestableCrop(BlockState state) {
		return isMature(state);
	}

	public static boolean isFertilizableCrop(World world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();

		// Hanya pupuk tanaman pertanian yang belum matang
		if (block instanceof CropBlock || block instanceof CocoaBlock) {
			if (!isMature(state) && block instanceof Fertilizable fertilizable) {
				return fertilizable.isFertilizable(world, pos, state);
			}
		}

		return false;
	}

	public static Item getMatchingSeed(BlockState state) {
		Block block = state.getBlock();

		if (block == Blocks.WHEAT) {
			return Items.WHEAT_SEEDS;
		} else if (block == Blocks.CARROTS) {
			return Items.CARROT;
		} else if (block == Blocks.POTATOES) {
			return Items.POTATO;
		} else if (block == Blocks.BEETROOTS) {
			return Items.BEETROOT_SEEDS;
		} else if (block == Blocks.NETHER_WART) {
			return Items.NETHER_WART;
		} else if (block == Blocks.COCOA) {
			return Items.COCOA_BEANS;
		}

		return null;
	}

	public static boolean isValidSoilForSeed(BlockState soilState, Item seed) {
		if (soilState == null || seed == null) {
			return false;
		}

		if (seed == Items.NETHER_WART) {
			return soilState.isOf(Blocks.SOUL_SAND);
		} else if (seed == Items.COCOA_BEANS) {
			return true;
		} else {
			return soilState.isOf(Blocks.FARMLAND);
		}
	}
}
