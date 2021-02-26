package chumbanotz.abyssaldepths.entity.fish;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class Butterflyfish extends Fish {
	public Butterflyfish(World world) {
		super(world);
		this.setSize(0.7F, 0.6F);
	}

	@Override
	public void initSchool() {
		super.initSchool();
		if (!this.isCommon()) {
			this.school.setOpenToCombine(true);
		}
	}

	@Override
	protected double addPathY() {
		return !this.isCommon() ? super.addPathY() * 0.5D : super.addPathY();
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.isCommon() || this.posY <= 48.0D;
	}

	public boolean isCommon() {
		return this.getClass() == Butterflyfish.class;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		if (this.isCommon()) {
			this.setColor(0.2F + this.rand.nextFloat() * 0.8F, 0.2F + this.rand.nextFloat() * 0.8F, 0.2F + this.rand.nextFloat() * 0.8F);
		}

		this.setScale(0.7F + this.rand.nextFloat() * 0.5F);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	public static class Banner extends Butterflyfish {
		public Banner(World world) {
			super(world);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
			livingdata = super.onInitialSpawn(difficulty, livingdata);
			this.setScale(0.6F + this.rand.nextFloat() * 0.4F);
			return livingdata;
		}
	}

	public static class Spotfin extends Butterflyfish {
		public Spotfin(World world) {
			super(world);
		}
	}

	public static class Raccoon extends Butterflyfish {
		public Raccoon(World world) {
			super(world);
		}
	}

	public static class Masked extends Butterflyfish {
		public Masked(World world) {
			super(world);
		}
	}
}