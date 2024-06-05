package com.mrbysco.cactusmod.datagen.data;

import com.mrbysco.cactusmod.init.CactusDamageTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

public class CactusDamageTypeProvider {
	public static void bootstrap(BootstrapContext<DamageType> context) {
		context.register(CactusDamageTypes.SPIKE, new DamageType("cactusmod.spike", 0.1F));
	}
}
