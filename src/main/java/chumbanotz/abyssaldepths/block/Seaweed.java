package chumbanotz.abyssaldepths.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class Seaweed extends Block implements IPlantable {
	public Seaweed() {
		super(Material.WATER);
		this.setTickRandomly(true);
		this.setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockLiquid.LEVEL, 0));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockLiquid.LEVEL);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (rand.nextInt(15) == 0) {
			if (worldIn.getBlockState(pos.up()) == Blocks.WATER) {
				int count;
				for (count = 1; worldIn.getBlockState(pos.down(count)) == this; ++count) {
				}

				if (count < 3 || count == 3 && rand.nextInt(3) == 0) {
					worldIn.setBlockState(pos.up(), this.getDefaultState());
//					this.onNeighborBlockChange(worldIn, pos.up(), this);
				}

				if (count == 4 && rand.nextBoolean()) {
					worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
					worldIn.setBlockState(pos.down(), Blocks.WATER.getDefaultState());
					if (rand.nextInt(3) == 0) {
						worldIn.setBlockState(pos.down(2), Blocks.WATER.getDefaultState());
					}
				}
			}
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		return block == Blocks.WATER && this.canBlockStay(worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!this.canBlockStay(worldIn, pos.getX(), pos.getY(), pos.getZ())) {
			worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
		}
	}

	public boolean canBlockStay(World world, int i, int j, int k) {
		return this.isNearWater(world, i, j, k) && this.isSuitableBottom(world.getBlockState(new BlockPos(i, j - 1, k)).getBlock());
	}

	public boolean isNearWater(World world, int i, int j, int k) {
		IBlockState block = world.getBlockState(new BlockPos(i, j + 1, k));
		IBlockState block1 = world.getBlockState(new BlockPos(i + 1, j, k));
		IBlockState block2 = world.getBlockState(new BlockPos(i - 1, j, k));
		IBlockState block3 = world.getBlockState(new BlockPos(i, j, k + 1));
		IBlockState block4 = world.getBlockState(new BlockPos(i, j, k - 1));
		if (block.getMaterial() != Material.WATER) {
			return false;
		} else if (block1.getMaterial() == Material.WATER) {
			return true;
		} else if (block2.getMaterial() == Material.WATER) {
			return true;
		} else if (block3.getMaterial() == Material.WATER) {
			return true;
		} else {
			return block4.getMaterial() == Material.WATER;
		}
	}

	public boolean isSuitableBottom(Block block) {
		return block == Blocks.GRAVEL || block == Blocks.SAND || block == Blocks.DIRT || block == ADBlocks.SEAWEED;
	}

	@Override
	public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));

		if (plant.getBlock() == ADBlocks.SEAWEED) {
			return true;
		} else {
			return super.canSustainPlant(state, world, pos, direction, plantable);
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Water;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return this.getDefaultState();
	}
}