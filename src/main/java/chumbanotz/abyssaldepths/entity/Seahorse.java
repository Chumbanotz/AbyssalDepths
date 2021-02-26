package chumbanotz.abyssaldepths.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import chumbanotz.abyssaldepths.util.ADGlobal;
import chumbanotz.abyssaldepths.util.Euler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class Seahorse extends WaterCreature implements IEntityOwnable {
	private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(Seahorse.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Byte> TAME_AMOUNT = EntityDataManager.createKey(Seahorse.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> MARK = EntityDataManager.createKey(Seahorse.class, DataSerializers.BYTE);

	public Seahorse(World world) {
		super(world);
		this.setSize(0.95F, 2.2F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
		this.dataManager.register(TAME_AMOUNT, (byte)(3 + this.rand.nextInt(3)));
		this.dataManager.register(MARK, (byte)0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	public void initSchool() {
		super.initSchool();
		this.school.setMaxSize(6 + this.rand.nextInt(2));
		this.school.setRadius((float)this.school.getMaxSize() * 1.2F);
	}

	@Override
	@Nullable
	public UUID getOwnerId() {
		return this.dataManager.get(OWNER_UNIQUE_ID).orNull();
	}

	private void setOwnerId(@Nullable UUID uuid) {
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(uuid));
	}

	@Override
	@Nullable
	public EntityPlayer getOwner() {
		UUID uuid = this.getOwnerId();
		return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
	}

	@Override
	public boolean getTamed() {
		return this.getOwnerId() != null;
	}

	public int getTameAmount() {
		return this.dataManager.get(TAME_AMOUNT);
	}

	private void setTameAmount(int amount) {
		this.dataManager.set(TAME_AMOUNT, (byte)amount);
	}

	public int getMark() {
		return this.dataManager.get(MARK);
	}

	private void setMark(int mark) {
		this.dataManager.set(MARK, (byte)mark);
	}

	@Override
	public boolean isInWater() {
		return this.world.handleMaterialAcceleration(this.getEntityBoundingBox().grow(0.0D, -1.1D, 0.0D), Material.WATER, this);
	}

	@Override
	public boolean getRotatePitch() {
		return false;
	}

	@Override
	public float getClosestPathDist() {
		return 2.0F;
	}

	@Override
	public boolean setRandomPath() {
		double x = this.posX + (4.0D + (double)this.rand.nextFloat() * 5.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		double y = this.posY + ((double)this.rand.nextFloat() - 0.5D) * 8.0D + this.addPathY();
		double z = this.posZ + (4.0D + (double)this.rand.nextFloat() * 5.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		if (this.onLand()) {
			x = this.posX + (2.0D + (double)this.rand.nextFloat() * 8.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
			z = this.posZ + (2.0D + (double)this.rand.nextFloat() * 8.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		}

		if (this.isClearPath(x, y, z)) {
			this.targetVec = new Vec3d(x, y, z);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetFleeDistance() {
		this.fleeDistance = 10.0D + (double)this.rand.nextFloat() * 6.0D;
		if (this.rand.nextInt(10) == 0) {
			this.fleeDistance = 24.0D + (double)this.rand.nextFloat() * 8.0D;
		}
	}

	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			Euler angle = new Euler(0.0F, this.rotationYaw, 0.0F);
			Vec3d vec = angle.rotateVector(-0.3F);
			passenger.setPosition(this.posX + vec.x, this.posY + vec.y + passenger.getYOffset() + 1.4D, this.posZ + vec.z);
		}
	}

	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 7) {
			this.playTameEffect(true);
		} else if (id == 6) {
			this.playTameEffect(false);
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!this.world.isRemote && !this.school.ridinSolo() && this.getTamed()) {
			this.school.removeCreature(this);
			this.school.setOpenToCombine(false);
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.isBeingRidden() || this.getLeashed()) {
			if (this.getTamed() && this.isBeingRidden() && this.getPassengers().get(0) == this.getOwner()) {
				this.targetVec = null;
			}

			this.followEntity = null;
		}
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		if (this.rand.nextInt(3) == 0) {
			this.setColor(this.rand.nextFloat(), this.rand.nextFloat(), this.rand.nextFloat());
		} else {
			this.setColor(0.79607844F, 0.8509804F, 0.43529412F);
		}

		this.setMark(this.rand.nextInt(3));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.isBeingRidden() && this.canBeSteered() && this.getTamed();
	}

	@Override
	public boolean canBeSteered() {
		return this.getControllingPassenger() instanceof EntityLivingBase;
	}

	@Override
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		if (this.isBeingRidden() && this.canBeSteered()) {
			EntityLivingBase passenger = (EntityLivingBase)this.getControllingPassenger();
			if (!this.getTamed()) {
				if (!this.world.isRemote) {
					if (this.targetVec == null || this.rand.nextInt(15) == 0) {
						this.setRandomPath();
					}

					this.fleeFromEntity = passenger;
					if (this.rand.nextInt(50) == 0) {
						this.setTameAmount(this.getTameAmount() - 1);
						if (this.getTameAmount() <= 0 && passenger instanceof EntityPlayer) {
							this.setOwnerId(passenger.getUniqueID());
							this.fleeFromEntity = null;
							this.fleeLookVec = null;
							this.world.setEntityState(this, (byte)7);
						} else {
							this.targetVec = null;
							passenger.dismountRidingEntity();
							this.world.setEntityState(this, (byte)6);
						}
					}

					super.travel(strafe, vertical, forward);
				}
			} else {
				float target = ADGlobal.wrapAngleAround(passenger.rotationYaw, this.rotationYaw);
				float f = this.rotationYaw + (target - this.rotationYaw) * 0.2F;
				if (Math.abs(target - f) > 30.0F || this.dPosX * this.dPosX + this.dPosY * this.dPosY + this.dPosZ * this.dPosZ > 1.6000001778593287E-5D) {
					this.prevRotationYaw = this.rotationYaw = this.rotationYawHead = this.renderYawOffset = f;
				}

				double speed = this.getMovementSpeed() * 1.8D;
				float pitch = this.rotationPitch;
				this.rotationPitch = passenger.rotationPitch * 0.9F - 10.0F;
				Vec3d vec = this.getLookVec();
				this.rotationPitch = pitch;
				this.netSpeed = (float)((double)this.netSpeed + (speed * (double)passenger.moveForward - (double)this.netSpeed) * 0.10000000149011612D);
				if (this.isInWater()) {
					this.move(MoverType.SELF, vec.x * (double)this.netSpeed, vec.y * (double)this.netSpeed, vec.z * (double)this.netSpeed);
				}

				super.travel(passenger.moveStrafing, vertical, 0.0F);
				this.handleLimbSwing();
			}
		} else {
			super.travel(strafe, vertical, forward);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float dmg) {
		Entity entity = source.getTrueSource();
		return this.isBeingRidden() && entity != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(source, dmg);
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!this.isBeingRidden()) {
			player.rotationYaw = this.rotationYawHead;
			if (!this.world.isRemote) {
				player.startRiding(this);
			}

			return true;
		}

		return false;
	}

	private void playTameEffect(boolean tamed) {
		EnumParticleTypes enumparticletypes = tamed ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;

		for (int i = 0; i < 7; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.posY <= 48.0D;
	}

	@Override
	public void despawnEntity() {
		if (!this.getTamed()) {
			super.despawnEntity();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setByte("Mark", (byte)this.getMark());
		if (this.getTamed()) {
			compound.setUniqueId("OwnerUUID", this.getOwnerId());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setMark(compound.getByte("Mark"));
		if (compound.hasUniqueId("OwnerUUID")) {
			this.setOwnerId(compound.getUniqueId("OwnerUUID"));
		}
	}
}