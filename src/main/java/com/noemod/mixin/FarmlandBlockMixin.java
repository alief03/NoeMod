package com.noemod.mixin;

import com.noemod.config.AutoFarmConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {

	@Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
	private void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
		if (AutoFarmConfig.protectFarmland && entity instanceof PlayerEntity) {
			entity.handleFallDamage(fallDistance, 1.0F, world.getDamageSources().fall());
			ci.cancel();
		}
	}
}
