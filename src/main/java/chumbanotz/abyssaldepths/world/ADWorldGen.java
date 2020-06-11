package chumbanotz.abyssaldepths.world;

import java.util.Random;

import chumbanotz.abyssaldepths.ADConfig;
import chumbanotz.abyssaldepths.block.ADBlocks;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

public class ADWorldGen implements IWorldGenerator {
	private final int priority;

	public ADWorldGen(int priority) {
		this.priority = priority;
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimensionType() != DimensionType.OVERWORLD) {
			return;
		}

		int posX = chunkX * 16;
		int posZ = chunkZ * 16;
		Biome biome = world.getBiome(new BlockPos(posX, 0, posZ));
		boolean waterBiome = BiomeDictionary.hasType(biome, BiomeDictionary.Type.WATER);
		boolean swampBiome = BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP);
		if (this.priority == 0 && ADConfig.GENERATION.generateRocks && waterBiome) {
			for (int i = 0; i < 8; i++) {
				int x = posX + rand.nextInt(16) + 8;
				int z = posZ + rand.nextInt(16) + 8;
				int y = nextInt(rand, world.getHeight(x, z) * 2);
				generateRocks(world, rand, x, y, z);
			}
		}

		if (this.priority == 1 && ADConfig.GENERATION.generateSeaweed && (waterBiome || swampBiome)) {
			for (int i = 0; i < 10; i++) {
				int x = posX + rand.nextInt(16) + 8;
				int z = posZ + rand.nextInt(16) + 8;
				int y = nextInt(rand, world.getHeight(x, z) * 2);
				generateSeaweed(world, rand, x, y, z);
			}
		}
	}

	private static void generateRocks(World world, Random rand, int i, int j, int k) {
		for (int l = 0; l < 3; l++) {
			int x = i + rand.nextInt(5) - rand.nextInt(5);//14
			int y = j + rand.nextInt(6) - rand.nextInt(6);
			int z = k + rand.nextInt(5) - rand.nextInt(5);//14
			int focX = x + (2 + rand.nextInt(3)) * (rand.nextBoolean() ? 1 : -1);
			int focY = y + rand.nextInt(3) - rand.nextInt(3);
			int focZ = z + (2 + rand.nextInt(3)) * (rand.nextBoolean() ? 1 : -1);
			int focX1 = x + (2 + rand.nextInt(3)) * (rand.nextBoolean() ? 1 : -1);
			int focY1 = y + rand.nextInt(3) - rand.nextInt(3);
			int focZ1 = z + (2 + rand.nextInt(3)) * (rand.nextBoolean() ? 1 : -1);
			IBlockState blockState = world.getBlockState(new BlockPos(focX, focY, focZ));
			IBlockState blockState1 = world.getBlockState(new BlockPos(focX1, focY1, focZ1));
			if ((blockState.getMaterial().isSolid() && blockState.getBlock() != Blocks.STAINED_HARDENED_CLAY && blockState1.getBlock() == Blocks.WATER) || (blockState.getBlock() == Blocks.WATER && blockState1.getMaterial().isSolid() && blockState1.getBlock() != Blocks.STAINED_HARDENED_CLAY)) {
				int lowerX = (focX < focX1) ? focX : focX1;
				int lowerY = (focY < focY1) ? focY : focY1;
				int lowerZ = (focZ < focZ1) ? focZ : focZ1;
				int dX = Math.abs(focX - focX1);
				int dY = Math.abs(focY - focY1);
				int dZ = Math.abs(focZ - focZ1);
				int countOffset = 4;
				int thickness = 2;
				boolean blackClay = rand.nextInt(3) != 0;
				int oreX = 0;
				int oreY = 0;
				int oreZ = 0;
				for (int countX = 0; countX <= dX + countOffset * 2;) {
					for (int countY = 0; countY <= dY + countOffset * 2;) {
						for (int countZ = 0; countZ <= dZ + countOffset * 2; countZ++) {
							int xx = lowerX + countX - countOffset;
							int yy = lowerY + countY - countOffset;
							int zz = lowerZ + countZ - countOffset;
							boolean suitableSize = (getDistance(xx, yy, zz, focX, focY, focZ) + getDistance(xx, yy, zz, focX1, focY1, focZ1) < MathHelper.sqrt((dX * dX + dY * dY + dZ * dZ)) + (thickness / 2));
							if (suitableSize) {
								world.setBlockState(new BlockPos(xx, yy - (int)(dY / 2.0F + 0.9F + rand.nextFloat() * 0.2F), zz), blackClay ? Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK) : Blocks.STAINED_HARDENED_CLAY.getDefaultState(), 2);
								if ((oreX == 0 && oreY == 0 && oreZ == 0) || rand.nextInt(5) == 0) {
									oreX = xx;
									oreY = yy - (int)(dY / 2.0F + 1.0F);
									oreZ = zz;
								}
							}
						}

						countY++;
					}

					countX++;
				}

				BlockPos pos = new BlockPos(oreX, oreY, oreZ);
				if (rand.nextInt(4) == 0) {
					world.setBlockState(pos, Blocks.COAL_ORE.getDefaultState(), 2);
				} else if (rand.nextInt(5) == 0) {
					world.setBlockState(pos, Blocks.IRON_ORE.getDefaultState(), 2);
				} else if (rand.nextInt(8) == 0) {
					world.setBlockState(pos, Blocks.LAPIS_ORE.getDefaultState(), 2);
				} else if (rand.nextInt(10) == 0) {
					world.setBlockState(pos, Blocks.GOLD_ORE.getDefaultState(), 2);
				} else if (rand.nextInt(100) == 0) {
					world.setBlockState(pos, Blocks.DIAMOND_ORE.getDefaultState(), 2);
				}
			}
		}
	}

	private static void generateSeaweed(World world, Random rand, int i, int j, int k) {
		for (int l = 0; l < 60; l++) {
			int x = i + rand.nextInt(8) - rand.nextInt(8);//8
			int y = j + rand.nextInt(4) - rand.nextInt(4);
			int z = k + rand.nextInt(8) - rand.nextInt(8);//8
			int seaweedHeight = 1 + rand.nextInt(rand.nextInt(3) + 1);
			for (int l1 = 0; l1 < seaweedHeight; l1++) {
				if (ADBlocks.SEAWEED.canPlaceBlockAt(world, new BlockPos(x, y + l1, z))) {
					int count = 1;
					for (; world.getBlockState(new BlockPos(x, y - count, z)) == ADBlocks.SEAWEED; count++) {
						;
					}

					if (count < 4) {
						world.setBlockState(new BlockPos(x, y + l1, z), ADBlocks.SEAWEED.getDefaultState(), 2);
					}
				}
			}
		}
	}

	private static float getDistance(int x, int y, int z, int x1, int y1, int z1) {
		int dx = x - x1;
		int dy = y - y1;
		int dz = z - z1;
		return MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
	}

	private static int nextInt(Random rand, int i) {
		return i <= 1 ? 0 : rand.nextInt(i);
	}
}