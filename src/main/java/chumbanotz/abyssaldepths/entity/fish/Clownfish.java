package chumbanotz.abyssaldepths.entity.fish;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class Clownfish extends Fish {
	private static final DataParameter<Boolean> ONE_STRIPE = EntityDataManager.createKey(Clownfish.class, DataSerializers.BOOLEAN);

	public Clownfish(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ONE_STRIPE, false);
	}

	public boolean hasOneStripe() {
		return this.dataManager.get(ONE_STRIPE);
	}

	private void setOneStripe(boolean oneStripe) {
		this.dataManager.set(ONE_STRIPE, oneStripe);
	}

	@Override
	protected double addPathY() {
		return super.addPathY() * 0.5D;
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && this.posY <= 48.0D;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setOneStripe(this.rand.nextInt(6) == 0);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("OneStripe", this.hasOneStripe());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setOneStripe(compound.getBoolean("OneStripe"));
	}
}