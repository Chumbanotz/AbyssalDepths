package chumbanotz.abyssaldepths.entity.fish;

import chumbanotz.abyssaldepths.entity.SeaSerpent;
import chumbanotz.abyssaldepths.entity.WaterCreature;
import chumbanotz.abyssaldepths.entity.billfish.Billfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class Fish extends WaterCreature {
	private static final DataParameter<Float> SCALE = EntityDataManager.createKey(Fish.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(Fish.class, DataSerializers.VARINT);

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
		this.dataManager.register(COLOR, 0);
	}

	public int getColor() {
		return this.dataManager.get(COLOR);
	}

	protected void setColor(int color) {
		this.dataManager.set(COLOR, color);
	}

	public boolean isColorful() {
		return this.getColor() > 0;
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
			this.setSize(this.width * this.getScale(), this.height * this.getScale());
		}
	}

	@Override
	public void initSchool() {
		super.initSchool();
		this.school.setMaxSize(6 + this.rand.nextInt(6));
		this.school.setRadius((float)this.school.getMaxSize());
	}

	private boolean shouldFleeFrom(Entity entity) {
		return this.getDistanceSq(entity) <= 4.0D && (entity instanceof SeaSerpent || entity instanceof Billfish);
	}

	@Override
	protected void moveCreature() {
		for (EntityLivingBase entityLivingBase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(2.0D), this::shouldFleeFrom)) {
			this.fleeFromEntity = entityLivingBase;
		}

		super.moveCreature();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float dmg) {
		EntityLivingBase living = null;
		if (source.getTrueSource() instanceof EntityLivingBase) {
			living = (EntityLivingBase)source.getTrueSource();
		}

		if (!this.world.isRemote) {
			this.fleeFromEntity = living;
		}

		return super.attackEntityFrom(source, dmg);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setFloat("Scale", this.getScale());
		if (this.isColorful()) {
			compound.setInteger("Color", this.getColor());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("Scale")) {
			this.setScale(compound.getFloat("Scale"));
		}

		if (compound.hasKey("Color")) {
			this.setColor(compound.getInteger("Color"));
		}
	}
}