package com.mrbysco.cactusmod.client;

import com.mrbysco.cactusmod.Reference;
import com.mrbysco.cactusmod.blocks.container.CactusWorkbenchContainer;
import com.mrbysco.cactusmod.client.render.CactoniRenderer;
import com.mrbysco.cactusmod.client.render.CactusBoatRenderer;
import com.mrbysco.cactusmod.client.render.CactusCartRenderer;
import com.mrbysco.cactusmod.client.render.CactusCowRenderer;
import com.mrbysco.cactusmod.client.render.CactusCreeperRenderer;
import com.mrbysco.cactusmod.client.render.CactusGolemRenderer;
import com.mrbysco.cactusmod.client.render.CactusPigRenderer;
import com.mrbysco.cactusmod.client.render.CactusSheepRenderer;
import com.mrbysco.cactusmod.client.render.CactusSkeletonRenderer;
import com.mrbysco.cactusmod.client.render.CactusSlimeRenderer;
import com.mrbysco.cactusmod.client.render.CactusSnowmanRenderer;
import com.mrbysco.cactusmod.client.render.CactusSpiderRenderer;
import com.mrbysco.cactusmod.client.render.CactusTNTRenderer;
import com.mrbysco.cactusmod.client.render.SpikeRenderer;
import com.mrbysco.cactusmod.client.render.block.CactusChestBER;
import com.mrbysco.cactusmod.client.render.block.CactusChestBEWLR;
import com.mrbysco.cactusmod.client.render.models.CactoniModel;
import com.mrbysco.cactusmod.client.render.models.CactusSheepModel;
import com.mrbysco.cactusmod.client.render.models.CactusSpiderModel;
import com.mrbysco.cactusmod.client.render.models.CactusWoolModel;
import com.mrbysco.cactusmod.init.CactusRegistry;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ClientHandler {
	public static final ModelLayerLocation CACTONI = new ModelLayerLocation(Reference.modLoc("cactoni"), "cactoni");
	public static final ModelLayerLocation CACTUS_SPIDER = new ModelLayerLocation(Reference.modLoc("cactus_spider"), "cactus_spider");
	public static final ModelLayerLocation CACTUS_SHEEP = new ModelLayerLocation(Reference.modLoc("cactus_sheep"), "cactus_sheep");
	public static final ModelLayerLocation CACTUS_SHEEP_WOOL = new ModelLayerLocation(Reference.modLoc("cactus_sheep_wool"), "cactus_sheep_wool");

	public static void registerRenders(FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(CactusRegistry.CACTUS_SLIME_BLOCK.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(CactusRegistry.CACTUS_TNT.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CactusRegistry.CARVED_CACTUS.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CactusRegistry.JACKO_CACTUS.get(), RenderType.cutout());

		event.enqueueWork(() -> {
			ItemProperties.register(CactusRegistry.CACTUS_BOW.get(), ResourceLocation.withDefaultNamespace("pull"), (stack, level, livingEntity, i) -> {
				if (livingEntity == null) {
					return 0.0F;
				} else {
					return livingEntity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
				}
			});
			ItemProperties.register(CactusRegistry.CACTUS_BOW.get(), ResourceLocation.withDefaultNamespace("pulling"), (stack, level, entity, i) -> entity != null &&
					entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		});
	}

	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions() {
			final BlockEntityWithoutLevelRenderer renderer = new CactusChestBEWLR();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CactusRegistry.CACTUS_CHEST_ITEM.get());
	}

	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(CactusRegistry.CACTUS_WORKBENCH_CONTAINER.get(), new Factory());
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(CactusRegistry.CACTUS_CHEST_BLOCK_ENTITY.get(), CactusChestBER::new);

		event.registerEntityRenderer(CactusRegistry.CACTUS_GOLEM.get(), CactusGolemRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_COW.get(), CactusCowRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_CART_ENTITY.get(), CactusCartRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_TNT_ENTITY.get(), CactusTNTRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_SPIKE.get(), SpikeRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_CREEPER.get(), CactusCreeperRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_SNOW_GOLEM.get(), CactusSnowmanRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_SLIME.get(), CactusSlimeRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_SHEEP.get(), CactusSheepRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_PIG.get(), CactusPigRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_SPIDER.get(), CactusSpiderRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_SKELETON.get(), CactusSkeletonRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTUS_BOAT_ENTITY.get(), CactusBoatRenderer::new);
		event.registerEntityRenderer(CactusRegistry.CACTONI.get(), CactoniRenderer::new);
	}

	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(CACTONI, CactoniModel::createBodyLayer);
		event.registerLayerDefinition(CACTUS_SPIDER, CactusSpiderModel::createSpiderBodyLayer);
		event.registerLayerDefinition(CACTUS_SHEEP, CactusSheepModel::createBodyLayer);
		event.registerLayerDefinition(CACTUS_SHEEP_WOOL, CactusWoolModel::createFurLayer);
	}

	@SuppressWarnings("rawtypes")
	private static class Factory implements ScreenConstructor {
		@Override
		public CraftingScreen create(AbstractContainerMenu container, Inventory pInv, Component name) {
			return new CraftingScreen((CactusWorkbenchContainer) container, pInv, name);
		}
	}

	public static final ResourceLocation CACTUS_CHEST_LOCATION = Reference.modLoc("entity/chest/cactus_chest");
}
