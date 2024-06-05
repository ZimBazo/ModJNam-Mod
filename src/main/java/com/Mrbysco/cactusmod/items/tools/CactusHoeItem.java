package com.mrbysco.cactusmod.items.tools;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;

public class CactusHoeItem extends HoeItem {

	public CactusHoeItem(Tier tier, int attackDamage, float attackSpeed, Item.Properties builder) {
		super(tier, builder.attributes(HoeItem.createAttributes(tier, attackDamage, attackSpeed)));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getLevel().random.nextInt(10) < 3)
			context.getPlayer().hurt(context.getLevel().damageSources().cactus(), 1F);
		return super.useOn(context);
	}
}
