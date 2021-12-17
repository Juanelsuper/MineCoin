package com.juanelsuper.minecoin.blocks;

import javax.annotation.Nullable;

import com.juanelsuper.minecoin.inventory.ATMContainer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ATMBlock extends DirectionalBlock {

	private static final VoxelShape AABB = Block.box(0.5D, 0.5D, 0.5D, 15.5D, 27.5D, 15.5D);

	public ATMBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	   }

   @Override
   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateContainer) {
	   stateContainer.add(FACING);
   }
   @Override
   public BlockState getStateForPlacement(BlockItemUseContext context) {
	   return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
   }
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return AABB;
	}
	
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTrace) {
      if (worldIn.isClientSide) {
         return ActionResultType.SUCCESS;
      } else {
         player.openMenu(state.getMenuProvider(worldIn, pos));
         return ActionResultType.CONSUME;
      }
   }
	@Nullable
	public INamedContainerProvider getMenuProvider(BlockState state, World worldIn, BlockPos pos) {
		return new SimpleNamedContainerProvider((id, player, text) -> {
			return new ATMContainer(id, player, IWorldPosCallable.create(worldIn, pos));
	    }, ATMContainer.CONTAINER_TITLE);
	}
	
	
	
}
