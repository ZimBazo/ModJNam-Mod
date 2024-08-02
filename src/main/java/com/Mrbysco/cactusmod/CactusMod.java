package com.mrbysco.cactusmod;

import com.mojang.logging.LogUtils;
import com.mrbysco.cactusmod.blocks.CarvedCactusBlock;
import com.mrbysco.cactusmod.client.ClientHandler;
import com.mrbysco.cactusmod.config.CactusConfig;
import com.mrbysco.cactusmod.handlers.CactusMobHandler;
import com.mrbysco.cactusmod.handlers.CactusModCompatHandlers;
import com.mrbysco.cactusmod.handlers.CactusToolHandler;
import com.mrbysco.cactusmod.handlers.CactusWorkbenchHandler;
import com.mrbysco.cactusmod.init.CactusRegistry;
import com.mrbysco.cactusmod.init.CactusSpawns;
import com.mrbysco.cactusmod.init.CactusTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Reference.MOD_ID)
public class CactusMod {
	public static final Logger logger = LogUtils.getLogger();

	public CactusMod(IEventBus eventBus, ModContainer container, Dist dist) {
		container.registerConfig(ModConfig.Type.COMMON, CactusConfig.commonSpec);
		eventBus.register(CactusConfig.class);

		eventBus.addListener(this::setup);

		CactusRegistry.FEATURES.register(eventBus);
		CactusRegistry.BLOCKS.register(eventBus);
		CactusRegistry.ITEMS.register(eventBus);
		CactusTabs.CREATIVE_MODE_TABS.register(eventBus);
		CactusRegistry.ENTITIES.register(eventBus);
		CactusRegistry.BLOCK_ENTITIES.register(eventBus);
		CactusRegistry.SOUND_EVENTS.register(eventBus);
		CactusRegistry.MENU.register(eventBus);

		eventBus.addListener(CactusSpawns::registerEntityAttributes);
		eventBus.addListener(CactusSpawns::registerSpawnPlacements);
		eventBus.addListener(CactusRegistry::registerCapabilities);

		NeoForge.EVENT_BUS.register(new CactusWorkbenchHandler());
		NeoForge.EVENT_BUS.register(new CactusToolHandler());
		NeoForge.EVENT_BUS.register(new CactusMobHandler());
		NeoForge.EVENT_BUS.register(new CactusModCompatHandlers());

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
			eventBus.addListener(ClientHandler::registerRenders);
			eventBus.addListener(ClientHandler::registerClientExtensions);
			eventBus.addListener(ClientHandler::registerMenuScreens);
			eventBus.addListener(ClientHandler::registerEntityRenders);
			eventBus.addListener(ClientHandler::registerLayerDefinitions);
		}
	}

	private void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> DispenserBlock.registerBehavior(CactusRegistry.CARVED_CACTUS.get(), new OptionalDispenseItemBehavior() {
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				Level level = source.level();
				BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
				CarvedCactusBlock carvedCactusBlock = CactusRegistry.CARVED_CACTUS.get();
				if (level.isEmptyBlock(blockpos) && carvedCactusBlock.canSpawnGolem(level, blockpos)) {
					if (!level.isClientSide) {
						level.setBlock(blockpos, carvedCactusBlock.defaultBlockState(), 3);
						level.gameEvent((Entity) null, GameEvent.BLOCK_PLACE, blockpos);
					}

					stack.shrink(1);
					this.setSuccess(true);
				} else {
					this.setSuccess(ArmorItem.dispenseArmor(source, stack));
				}

				return stack;
			}
		}));
	}
}
