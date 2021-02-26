package chumbanotz.abyssaldepths.entity.fish;

import chumbanotz.abyssaldepths.entity.ComplexCreature;
import chumbanotz.abyssaldepths.entity.WaterCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public abstract class Fish extends WaterCreature {
	private static final DataParameter<Float> SCALE = EntityDataManager.createKey(Fish.class, DataSerializers.FLOAT);

	public Fish(World world) {
		super(world);
		this.setSize(0.7F, 0.5F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(SCALE, 0.5F + this.rand.nextFloat() * 0.6F);
	}

	public float getScale() {
		return this.dataManager.get(SCALE);
	}

	protected void setScale(float scale) {
		this.dataManager.set(SCALE, scale);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if (SCALE.equals(key)) {
			float scale = this.getScale();
			this.setSize(this.width * scale, this.height * scale);
		}
	}

	@Override
	public void initSchool() {
		super.initSchool();
		this.school.setMaxSize(6 + this.rand.nextInt(6));
		this.school.setRadius((float)this.school.getMaxSize());
	}

	@Override
	protected void moveCreature() {
		for (ComplexCreature entityLivingBase : this.world.getEntitiesWithinAABB(ComplexCreature.class, this.getEntityBoundingBox().grow(2.0D))) {
			if (this.getDistanceSq(entityLivingBase) <= 4.0D && this.getEntitySenses().canSee(entityLivingBase)) {
				this.fleeFromEntity = entityLivingBase;
			}
		}

		super.moveCreature();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setFloat("Scale", this.getScale());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("Scale")) {
			this.setScale(compound.getFloat("Scale"));
		}
	}
}