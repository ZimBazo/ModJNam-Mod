package com.mrbysco.cactusmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

public class PricklyIronBlock extends Block {
	public static final VoxelShape ALMOST_FULL = Block.box(0.08, 0, 0.08, 15.98, 15.98, 15.98);

	public PricklyIronBlock(BlockBehaviour.Properties builder) {
		super(builder);
	}

	@Override
	public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
		if (level instanceof Level actualLevel) {
			if (plant.canSurvive(actualLevel, soilPosition)) {
				return TriState.TRUE;
			}
		}
		return super.canSustainPlant(state, level, soilPosition, facing, plant);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		entity.hurt(entity.damageSources().cactus(), 1.0F);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return ALMOST_FULL;
	}
}
