package chumbanotz.abyssaldepths.entity.fish;

import net.minecraft.world.World;

public class Basslet extends Fish {
	public Basslet(World world) {
		super(world);
	}

	@Override
	public void initSchool() {
		super.initSchool();
		this.school.setOpenToCombine(false);
	}

	@Override
	protected double addPathY() {
		return super.addPathY() * 0.5D;
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.posY <= 48.0D;
	}

	public static class Fairy extends Basslet {
		public Fairy(World world) {
			super(world);
		}
	}

	public static class BlackCap extends Basslet {
		public BlackCap(World world) {
			super(world);
		}
	}
}