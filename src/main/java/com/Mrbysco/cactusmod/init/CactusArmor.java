package com.mrbysco.cactusmod.init;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class CactusArmor {
	public static final Holder<ArmorMaterial> CACTUS = register("cactus", Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
		map.put(ArmorItem.Type.BOOTS, 0);
		map.put(ArmorItem.Type.LEGGINGS, 0);
		map.put(ArmorItem.Type.CHESTPLATE, 0);
		map.put(ArmorItem.Type.HELMET, 0);
	}), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(Items.CACTUS));

	private static Holder<ArmorMaterial> register(
			String pName,
			EnumMap<ArmorItem.Type, Integer> pDefense,
			int pEnchantmentValue,
			Holder<SoundEvent> pEquipSound,
			float pToughness,
			float pKnockbackResistance,
			Supplier<Ingredient> pRepairIngredient
	) {
		List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(ResourceLocation.tryParse(pName)));
		return register(pName, pDefense, pEnchantmentValue, pEquipSound, pToughness, pKnockbackResistance, pRepairIngredient, list);
	}

	private static Holder<ArmorMaterial> register(
			String pName,
			EnumMap<ArmorItem.Type, Integer> pDefense,
			int pEnchantmentValue,
			Holder<SoundEvent> pEquipSound,
			float pToughness,
			float pKnockbackResistance,
			Supplier<Ingredient> pRepairIngridient,
			List<ArmorMaterial.Layer> pLayers
	) {
		EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

		for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
			enummap.put(armoritem$type, pDefense.get(armoritem$type));
		}

		return Registry.registerForHolder(
				BuiltInRegistries.ARMOR_MATERIAL,
				ResourceLocation.tryParse(pName),
				new ArmorMaterial(enummap, pEnchantmentValue, pEquipSound, pRepairIngridient, pLayers, pToughness, pKnockbackResistance)
		);
	}
}