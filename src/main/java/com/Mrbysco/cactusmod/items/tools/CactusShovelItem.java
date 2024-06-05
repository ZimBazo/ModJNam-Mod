package com.mrbysco.cactusmod.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CactusShovelItem extends ShovelItem {

	public CactusShovelItem(Tier tier, float attackDamage, float attackSpeed, Item.Properties builder) {
		super(tier, builder.attributes(ShovelItem.createAttributes(tier, attackDamage, attackSpeed)));
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos,
	                         LivingEntity entityLiving) {
		if (level.random.nextInt(10) < 3)
			entityLiving.hurt(entityLiving.damageSources().cactus(), 1F);
		return super.mineBlock(stack, level, state, pos, entityLiving);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getLevel().random.nextInt(10) < 3)
			context.getPlayer().hurt(context.getLevel().damageSources().cactus(), 1F);
		return super.useOn(context);
	}
}
