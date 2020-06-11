package chumbanotz.abyssaldepths.entity.billfish;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class Sailfish extends Billfish {
	private static final float[] PART_LENGTH = new float[] {10.0F, 12.0F, 12.0F, 10.0F};

	public Sailfish(World world) {
		super(world);
		this.setSize(0.7F, 0.6F);
	}

	@Override
	protected float[] getPartLength() {
		return PART_LENGTH;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 5;
	}

	@Override
	protected double getStrikeRange() {
		return 4.0D;
	}

	@Override
	protected float getAttackReach() {
		return 1.0F;
	}

	@Override
	protected int getMaxAttackTime() {
		return 5;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setBanner(this.rand.nextBoolean());
		this.setStripes(this.rand.nextInt(3) == 0);
		return super.onInitialSpawn(difficulty, livingdata);
	}
}