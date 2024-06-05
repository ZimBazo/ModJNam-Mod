package com.mrbysco.cactusmod.init;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class CactusTiers {
	public static final Tier CACTUS = new SimpleTier(CactusTags.INCORRECT_FOR_CACTUS_TOOL, 67, 3.0F, 0.2F, 16,
			() -> Ingredient.of(Items.CACTUS));
}
