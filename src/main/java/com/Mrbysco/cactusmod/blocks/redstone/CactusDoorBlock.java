package com.mrbysco.cactusmod.blocks.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

public class CactusDoorBlock extends DoorBlock {
	public CactusDoorBlock(BlockBehaviour.Properties builder) {
		super(BlockSetType.OAK, builder);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		player.hurt(player.damageSources().cactus(), 1.0F);
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}
}
