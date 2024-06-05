package com.mrbysco.cactusmod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrbysco.cactusmod.entities.AbstractSpikeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public abstract class AbstractSpikeRenderer<T extends AbstractSpikeEntity> extends EntityRenderer<T> {
	public AbstractSpikeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn) {
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		float f9 = (float) entityIn.spikeShake - partialTicks;
		if (f9 > 0.0F) {
			float f10 = -Mth.sin(f9 * 3.0F) * f9;
			poseStack.mulPose(Axis.ZP.rotationDegrees(f10));
		}

		poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
		poseStack.scale(0.05625F, 0.05625F, 0.05625F);
		poseStack.translate(-4.0D, 0.0D, 0.0D);
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		PoseStack.Pose poseLast = poseStack.last();
		Matrix4f pose = poseLast.pose();
		this.drawVertex(pose, poseLast, vertexConsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLightIn);
		this.drawVertex(pose, poseLast, vertexConsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLightIn);
		this.drawVertex(pose, poseLast, vertexConsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLightIn);
		this.drawVertex(pose, poseLast, vertexConsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLightIn);
		this.drawVertex(pose, poseLast, vertexConsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLightIn);
		this.drawVertex(pose, poseLast, vertexConsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLightIn);
		this.drawVertex(pose, poseLast, vertexConsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLightIn);
		this.drawVertex(pose, poseLast, vertexConsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLightIn);

		for (int j = 0; j < 4; ++j) {
			poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
			this.drawVertex(pose, poseLast, vertexConsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLightIn);
			this.drawVertex(pose, poseLast, vertexConsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLightIn);
			this.drawVertex(pose, poseLast, vertexConsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLightIn);
			this.drawVertex(pose, poseLast, vertexConsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLightIn);
		}

		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
	}

	public void drawVertex(Matrix4f pose, PoseStack.Pose poseLast, VertexConsumer vertexConsumer, int offsetX, int offsetY, int offsetZ, float textureX, float textureY, int p_229039_9_, int p_229039_10_, int p_229039_11_, int packedLightIn) {
		vertexConsumer.vertex(pose, (float) offsetX, (float) offsetY, (float) offsetZ).color(255, 255, 255, 255).uv(textureX, textureY)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(poseLast, (float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_).endVertex();
	}
}
