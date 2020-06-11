package chumbanotz.abyssaldepths.entity.billfish;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class Swordfish extends Billfish {
	private static final float[] PART_LENGTH = new float[] {12.0F, 16.0F, 16.0F, 13.0F};

	public Swordfish(World world) {
		super(world);
		this.setSize(0.8F, 0.7F);
	}

	@Override
	protected float[] getPartLength() {
		return PART_LENGTH;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(36.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
	}

	public boolean isBlue() {
		return (this.dataManager.get(MARKINGS) & 4) != 0;
	}

	private void setBlue(boolean blue) {
		byte b0 = this.dataManager.get(MARKINGS);
		this.dataManager.set(MARKINGS, blue ? (byte)(b0 | 4) : (byte)(b0 & -5));
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 10;
	}

	@Override
	protected double getStrikeRange() {
		return 5.0D;
	}

	@Override
	protected float getAttackReach() {
		return 2.1F;
	}

	@Override
	protected int getMaxAttackTime() {
		return 8;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setBlue(this.rand.nextInt(3) == 0);
		this.setBanner(this.rand.nextInt(3) == 0);
		this.setStripes(this.rand.nextInt(15) == 0);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("Blue", this.isBlue());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setBlue(compound.getBoolean("Blue"));
	}
}