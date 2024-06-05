package com.mrbysco.cactusmod.blockentities;

import com.mrbysco.cactusmod.init.CactusRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CactusChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
	private static final int EVENT_SET_OPEN_COUNT = 1;
	private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			CactusChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_OPEN);
		}

		protected void onClose(Level level, BlockPos pos, BlockState state) {
			CactusChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_CLOSE);
		}

		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int p_155364_, int p_155365_) {
			CactusChestBlockEntity.this.signalOpenCount(level, pos, state, p_155364_, p_155365_);
		}

		protected boolean isOwnContainer(Player player) {
			if (!(player.containerMenu instanceof ChestMenu)) {
				return false;
			} else {
				Container container = ((ChestMenu) player.containerMenu).getContainer();
				return container == CactusChestBlockEntity.this || container instanceof CompoundContainer && ((CompoundContainer) container).contains(CactusChestBlockEntity.this);
			}
		}
	};
	private final ChestLidController chestLidController = new ChestLidController();
	private int ticksSinceDeleted = 0;

	protected CactusChestBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	public CactusChestBlockEntity(BlockPos pos, BlockState state) {
		this(CactusRegistry.CACTUS_CHEST_BLOCK_ENTITY.get(), pos, state);
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getContainerSize() {
		return 27;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.cactus.chest");
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(tag)) {
			ContainerHelper.loadAllItems(tag, this.items, registries);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registries) {
		super.saveAdditional(compoundTag, registries);
		if (!this.trySaveLootTable(compoundTag)) {
			ContainerHelper.saveAllItems(compoundTag, this.items, registries);
		}
	}

	public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, CactusChestBlockEntity chestBlockEntity) {
		chestBlockEntity.chestLidController.tickLid();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CactusChestBlockEntity chestBlockEntity) {
		++chestBlockEntity.ticksSinceDeleted;
		if (!chestBlockEntity.isEmpty()) {
			int randInt = level.random.nextInt(chestBlockEntity.getContainerSize());
			if (chestBlockEntity.getItems().get(randInt) != ItemStack.EMPTY && chestBlockEntity.ticksSinceDeleted >= 200) {
				ItemStack stack = chestBlockEntity.getItems().get(randInt);
				int size = stack.getCount();
				int minusSize = level.random.nextInt(size + 1);

				stack.shrink(minusSize);
				chestBlockEntity.ticksSinceDeleted = 0;
			}
		}
	}

	static void playSound(Level level, BlockPos pos, BlockState state, SoundEvent soundEvent) {
		double d0 = (double) pos.getX() + 0.5D;
		double d1 = (double) pos.getY() + 0.5D;
		double d2 = (double) pos.getZ() + 0.5D;
		level.playSound((Player) null, d0, d1, d2, soundEvent, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public boolean triggerEvent(int p_59114_, int p_59115_) {
		if (p_59114_ == 1) {
			this.chestLidController.shouldBeOpen(p_59115_ > 0);
			return true;
		} else {
			return super.triggerEvent(p_59114_, p_59115_);
		}
	}

	@Override
	public void startOpen(Player p_59120_) {
		if (!this.remove && !p_59120_.isSpectator()) {
			this.openersCounter.incrementOpeners(p_59120_, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	public void stopOpen(Player p_59118_) {
		if (!this.remove && !p_59118_.isSpectator()) {
			this.openersCounter.decrementOpeners(p_59118_, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> itemsIn) {
		this.items = itemsIn;
	}

	@Override
	public float getOpenNess(float p_59080_) {
		return this.chestLidController.getOpenness(p_59080_);
	}

	public static int getOpenCount(BlockGetter getter, BlockPos pos) {
		BlockState blockstate = getter.getBlockState(pos);
		if (blockstate.hasBlockEntity()) {
			BlockEntity blockentity = getter.getBlockEntity(pos);
			if (blockentity instanceof CactusChestBlockEntity) {
				return ((CactusChestBlockEntity) blockentity).openersCounter.getOpenerCount();
			}
		}

		return 0;
	}

	public static void swapContents(CactusChestBlockEntity chest, CactusChestBlockEntity otherChest) {
		NonNullList<ItemStack> nonnulllist = chest.getItems();
		chest.setItems(otherChest.getItems());
		otherChest.setItems(nonnulllist);
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player) {
		return ChestMenu.threeRows(id, player, this);
	}

	public void recheckOpen() {
		if (!this.remove) {
			this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int p_155336_, int p_155337_) {
		Block block = state.getBlock();
		level.blockEvent(pos, block, 1, p_155337_);
	}
}
