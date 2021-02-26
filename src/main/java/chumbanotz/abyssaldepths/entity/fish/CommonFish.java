package chumbanotz.abyssaldepths.entity.fish;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class CommonFish extends Fish {
	public CommonFish(World world) {
		super(world);
	}

	@Override
	public void initSchool() {
		super.initSchool();
		if (this.isColorful()) {
			this.school.setOpenToCombine(true);
		}
	}

	@Override
	public boolean shouldLeaveSchool() {
		if (this.isColorful()) {
			return this.rand.nextInt(4000) == 0;
		} else {
			return super.shouldLeaveSchool();
		}
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setColor(0.2F + this.rand.nextFloat() * 0.8F, 0.2F + this.rand.nextFloat() * 0.8F, 0.2F + this.rand.nextFloat() * 0.8F);
		return super.onInitialSpawn(difficulty, livingdata);
	}
}