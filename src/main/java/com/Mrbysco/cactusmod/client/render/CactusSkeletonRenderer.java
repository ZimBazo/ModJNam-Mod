package com.mrbysco.cactusmod.client.render;

import com.mrbysco.cactusmod.Reference;
import com.mrbysco.cactusmod.entities.hostile.CactusSkeletonEntity;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class CactusSkeletonRenderer extends HumanoidMobRenderer<CactusSkeletonEntity, SkeletonModel<CactusSkeletonEntity>> {
	private static final ResourceLocation texture = Reference.modLoc("textures/entity/cactus_skeleton.png");

	public CactusSkeletonRenderer(EntityRendererProvider.Context context) {
		super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 0.5F);
		this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
				new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)), context.getModelManager()));

	}

	@Override
	public ResourceLocation getTextureLocation(CactusSkeletonEntity entity) {
		return texture;
	}
}