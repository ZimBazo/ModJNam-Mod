package com.mrbysco.cactusmod.blocks.redstone;

import com.mojang.serialization.MapCodec;
import com.mrbysco.cactusmod.blockentities.CactusChestBlockEntity;
import com.mrbysco.cactusmod.init.CactusRegistry;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class CactusChestBlock extends AbstractChestBlock<CactusChestBlockEntity> implements SimpleWaterloggedBlock {
	public static final MapCodec<CactusChestBlock> CODEC = simpleCodec(CactusChestBlock::new);

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
	private final Supplier<BlockEntityType<? extends CactusChestBlockEntity>> tileEntityTypeSupplier = CactusRegistry.CACTUS_CHEST_BLOCK_ENTITY::get;

	@Override
	protected MapCodec<? extends AbstractChestBlock<CactusChestBlockEntity>> codec() {
		return CODEC;
	}

	public CactusChestBlock(BlockBehaviour.Properties builder) {
		super(builder, CactusRegistry.CACTUS_CHEST_BLOCK_ENTITY::get);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
	}

	@Override
	public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(
			BlockState pState, Level pLevel, BlockPos pPos, boolean pOverride
	) {
		return DoubleBlockCombiner.Combiner::acceptNone;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
			if (menuProvider != null) {
				player.openMenu(menuProvider);
				player.awardStat(this.getOpenStat());
			}

			return InteractionResult.CONSUME;
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CactusChestBlockEntity(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? createTickerHelper(blockEntityType, CactusRegistry.CACTUS_CHEST_BLOCK_ENTITY.get(), CactusChestBlockEntity::lidAnimateTick) : createTickerHelper(blockEntityType, CactusRegistry.CACTUS_CHEST_BLOCK_ENTITY.get(), CactusChestBlockEntity::serverTick);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

	public DoubleBlockCombiner.NeighborCombineResult<? extends CactusChestBlockEntity> getWrapper(BlockState state, Level level, BlockPos pos, boolean override) {
		BiPredicate<LevelAccessor, BlockPos> biPredicate;
		if (override) {
			biPredicate = (levelAccessor, blockPos) -> false;
		} else {
			biPredicate = CactusChestBlock::isBlocked;
		}

		return DoubleBlockCombiner.combineWithNeigbour(tileEntityTypeSupplier.get(), CactusChestBlock::getMergerType, CactusChestBlock::getDirectionToAttached, FACING, state, level, pos, biPredicate);
	}

	public static DoubleBlockCombiner.BlockType getMergerType(BlockState blockState) {
		return DoubleBlockCombiner.BlockType.SINGLE;
	}

	public static Direction getDirectionToAttached(BlockState state) {
		Direction direction = state.getValue(FACING);
		return direction.getCounterClockWise();
	}

	protected Stat<ResourceLocation> getOpenStat() {
		return Stats.CUSTOM.get(Stats.OPEN_CHEST);
	}

	@OnlyIn(Dist.CLIENT)
	public static DoubleBlockCombiner.Combiner<CactusChestBlockEntity, Float2FloatFunction> opennessCombiner(final LidBlockEntity lid) {
		return new DoubleBlockCombiner.Combiner<>() {
			public Float2FloatFunction acceptDouble(CactusChestBlockEntity blockEntity, CactusChestBlockEntity blockEntity1) {
				return (angle) -> Math.max(blockEntity.getOpenNess(angle), blockEntity1.getOpenNess(angle));
			}

			public Float2FloatFunction acceptSingle(CactusChestBlockEntity blockEntity) {
				return blockEntity::getOpenNess;
			}

			public Float2FloatFunction acceptNone() {
				return lid::getOpenNess;
			}
		};
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof Container) {
				Containers.dropContents(level, pos, (Container) blockEntity);
				level.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	public static boolean isBlocked(LevelAccessor levelAccessor, BlockPos pos) {
		return isBelowSolidBlock(levelAccessor, pos) || isCatSittingOn(levelAccessor, pos);
	}

	private static boolean isBelowSolidBlock(BlockGetter reader, BlockPos level) {
		BlockPos blockpos = level.above();
		return reader.getBlockState(blockpos).isRedstoneConductor(reader, blockpos);
	}

	private static boolean isCatSittingOn(LevelAccessor levelAccessor, BlockPos pos) {
		List<Cat> list = levelAccessor.getEntitiesOfClass(Cat.class,
				new AABB(pos.getX(), pos.getY() + 1, pos.getZ(),
						pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
		if (!list.isEmpty()) {
			for (Cat cat : list) {
				if (cat.isInSittingPose()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromContainer((Container) level.getBlockEntity(pos));
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof CactusChestBlockEntity) {
			((CactusChestBlockEntity) blockentity).recheckOpen();
		}
	}
}
