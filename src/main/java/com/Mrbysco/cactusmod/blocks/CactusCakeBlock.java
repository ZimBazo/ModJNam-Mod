package com.mrbysco.cactusmod.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class CactusCakeBlock extends CakeBlock {
	public CactusCakeBlock(BlockBehaviour.Properties builder) {
		super(builder);
	}

	@Override
	protected ItemInteractionResult useItemOn(
			ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result
	) {
		Item item = stack.getItem();
		if (stack.is(ItemTags.CANDLES) && state.getValue(BITES) == 0) {
			Block block = Block.byItem(item);
			if (block instanceof CandleBlock candleBlock) {
				if (!player.isCreative()) {
					stack.shrink(1);
				}

				level.playSound((Player) null, pos, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.setBlockAndUpdate(pos, CandleCakeBlock.byCandle(candleBlock));
				level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
				player.awardStat(Stats.ITEM_USED.get(item));
				return ItemInteractionResult.SUCCESS;
			}
		}

		if (level.isClientSide) {
			if (eatCactusCake(level, pos, state, player).consumesAction()) {
				return ItemInteractionResult.SUCCESS;
			}

			if (stack.isEmpty()) {
				return ItemInteractionResult.CONSUME;
			}
		}

		return eatCactusCake(level, pos, state, player);
	}

	public static ItemInteractionResult eatCactusCake(LevelAccessor levelAccessor, BlockPos pos, BlockState state, Player player) {
		if (!player.canEat(false)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		} else {
			player.awardStat(Stats.EAT_CAKE_SLICE);
			player.hurt(player.damageSources().cactus(), 1.0F);
			player.getFoodData().eat(2, 0.1F);
			int i = state.getValue(BITES);
			if (i < 6) {
				levelAccessor.setBlock(pos, state.setValue(BITES, i + 1), 3);
			} else {
				levelAccessor.removeBlock(pos, false);
			}

			return ItemInteractionResult.SUCCESS;
		}
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entityIn) {
		entityIn.hurt(entityIn.damageSources().cactus(), 1.0F);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		tooltip.add(Component.translatable("cactus.cake.info").withStyle(ChatFormatting.GREEN));
	}
}
