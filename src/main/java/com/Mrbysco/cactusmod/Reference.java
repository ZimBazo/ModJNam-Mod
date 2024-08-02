package com.mrbysco.cactusmod;

import com.mrbysco.cactusmod.init.CactusDamageTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class Reference {
	public static final String MOD_ID = "cactusmod";

	public static DamageSource spikeDamage(Entity entity, Entity attacking) {
		return entity.damageSources().source(CactusDamageTypes.SPIKE, entity, attacking);
	}

	public static ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
