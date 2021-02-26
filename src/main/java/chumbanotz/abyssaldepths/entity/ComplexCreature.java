package chumbanotz.abyssaldepths.entity;

import chumbanotz.abyssaldepths.entity.fish.Fish;
import chumbanotz.abyssaldepths.util.Bone;
import chumbanotz.abyssaldepths.util.Euler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ComplexCreature extends AquaticCreature implements IEntityMultiPart {
	private static final DataParameter<Integer> TICKS_EXISTED = EntityDataManager.createKey(ComplexCreature.class, DataSerializers.VARINT);
	private static final DataParameter<Float> LIMB_SWING = EntityDataManager.createKey(ComplexCreature.class, DataSerializers.FLOAT);
	protected int moveTick;

	public ComplexCreature(World world) {
		super(world);
		this.ignoreFrustumCheck = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TICKS_EXISTED, 0);
		this.dataManager.register(LIMB_SWING, 0.0F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}

	@Override
	public boolean isInWater() {
		return this.world.handleMaterialAcceleration(this.getEntityBoundingBox().grow(0.0D, -0.3D, 0.0D), Material.WATER, this);
	}

	@Override
	protected boolean canDespawn() {
		return this.getAttackTarget() == null;
	}

	@Override
	public void onUpdate() {
		double prevX = this.posX;
		double prevY = this.posY;
		double prevZ = this.posZ;
		super.onUpdate();

		if (!this.world.isRemote) {
			this.dataManager.set(TICKS_EXISTED, this.ticksExisted);
			this.dataManager.set(LIMB_SWING, this.limbSwing);
		} else {
			this.ticksExisted = this.dataManager.get(TICKS_EXISTED);
			if (this.dPosX * this.dPosX + this.dPosY * this.dPosY + this.dPosZ * this.dPosZ < 1.6000001778593287E-5D || this.ticksExisted % 100 == 0) {
				this.limbSwing = this.dataManager.get(LIMB_SWING);
			}
		}

		this.dPosX = this.posX - prevX;
		this.dPosY = this.posY - prevY;
		this.dPosZ = this.posZ - prevZ;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.updateArmSwingProgress();
		if (!this.isInWater()) {
			if (this.onGround) {
				this.currentPitch *= 0.6F;
			} else {
				double d = (double)MathHelper.sqrt(this.dPosX * this.dPosX + this.dPosZ * this.dPosZ);
				float pitch = -((float)Math.atan2(this.dPosY, d)) * 57.295776F;
				this.currentPitch += (pitch - this.currentPitch) * 0.25F;
			}
		} else {
			this.currentPitch += (this.rotationPitch - this.currentPitch) * 0.1F;
		}

		this.updateParts();
	}

	@Override
	protected void moveCreature() {
		double targetSpeed = 0.0D;
		EntityLivingBase target = this.getAttackTarget();
		if (target != null) {
			++this.moveTick;
			targetSpeed = this.moveByTarget(target);
		} else if (this.targetVec != null) {
			++this.moveTick;
			targetSpeed = this.moveByPathing();
		} else if (this.idleTime < 100 && this.findNewPath() && this.ticksExisted > 20) {
			this.setRandomPath();
		}

		Vec3d vec = this.getLookVec();
		this.netSpeed += (targetSpeed - this.netSpeed) * 0.1D;
		float dYaw = Math.abs(this.currentYaw - this.prevCurrentYaw);
		if (dYaw > 0.02F && this.netSpeed > targetSpeed * 0.6D) {
			this.netSpeed += (targetSpeed * 0.6D - this.netSpeed) * 0.4D;
		}

		if (this.onLand()) {
			this.netSpeed = 0.0D;
			if (this.targetVec != null && this.rand.nextInt(12) == 0) {
				this.motionX = vec.x * 0.25D;
				this.motionY = 0.20000000298023224D;
				this.motionZ = vec.z * 0.25D;
			}
		}

		this.move(MoverType.SELF, vec.x * this.netSpeed, vec.y * this.netSpeed, vec.z * this.netSpeed);
	}

	public double moveByTarget(EntityLivingBase target) {
		return 0.0D;
	}

	public double moveByPathing() {
		this.getLookHelper().setLookPosition(this.targetVec.x, this.targetVec.y, this.targetVec.z, 3.0F, 85.0F);
		if (this.collidedHorizontally && this.motionY < 0.20000000298023224D) {
			this.motionY = 0.20000000298023224D;
		}

		if (this.targetVec.distanceTo(this.getPositionVector()) < 3.0D || this.moveTick > 120) {
			this.moveTick = 0;
			if (this.rand.nextInt(5) < 3) {
				this.setRandomPath();
			} else {
				this.targetVec = null;
			}
		}

		return this.getMovementSpeed();
	}

	public final double getMovementSpeed() {
		return this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 1.8D;
	}

	@Override
	public abstract BodyPart[] getParts();

	public abstract Bone getBaseBone();

	public abstract Bone[] getBoneList();

	protected void updateParts() {
		this.getBaseBone().setRotation(this.currentPitch, this.rotationYaw, 0.0F);
		this.resetBoneAngles();
		this.updatePitchRotations(1.0F);
		this.updateYawRotations(1.0F);
		this.setBodyPartPositions();
	}

	public void updatePitchRotations(float partialTick) {
	}

	public void updateYawRotations(float partialTick) {
	}

	private void setBodyPartPositions() {
		BodyPart[] partList = this.getParts();
		Bone baseBone = this.getBaseBone();
		Bone[] boneList = this.getBoneList();
		Vec3d vec = boneList[0].getRotation().getRotated(baseBone.getRotation()).rotateVector(boneList[0].getLength() / 2.0F);
		this.world.updateEntity(partList[0]);
		partList[0].setLocationAndAngles(this.posX + vec.x, this.posY + vec.y, this.posZ + vec.z, this.rotationYaw, this.rotationPitch);
		vec = baseBone.getRotatedVector();
		Euler angle = baseBone.getRotation();

		for (int i = 1; i < partList.length; ++i) {
			angle = angle.getRotated(boneList[i].getRotation());
			float length = boneList[i].getLength();
			Vec3d midVec = angle.rotateVector(length / 2.0F);
			this.world.updateEntity(partList[i]);
			partList[i].setLocationAndAngles(this.posX + vec.x + midVec.x, this.posY + vec.y + midVec.y, this.posZ + vec.z + midVec.z, this.rotationYaw, this.rotationPitch);
			Vec3d target = angle.rotateVector(length);
			vec = vec.add(target.x, target.y, target.z);
		}
	}

	public void resetBoneAngles() {
		for (Bone bone : this.getBoneList()) {
			bone.setRotation(0.0F, 0.0F, 0.0F);
		}
	}

	public void collideWithEntity(BodyPart part, Entity entity) {
	}

	public void eatOrDamageEntity(Entity target, float dmg) {
		if (target instanceof EntityLiving && target instanceof Fish) {
			EntityLiving living = (EntityLiving)target;
			if (living.getHealth() - dmg < 0.0F) {
				this.swingArm(EnumHand.MAIN_HAND);
				living.setDead();
				this.heal(1.0F);
			}
		}

		if (target.isEntityAlive()) {
			target.attackEntityFrom(DamageSource.causeMobDamage(this), dmg);
		}
	}

	public void eatOrDamageEntity(BodyPart part, Entity target, float dmg) {
		if (target instanceof EntityLiving && target.width < this.width + 0.5F && target.height < this.height + 0.5F) {
			EntityLiving living = (EntityLiving)target;
			if (living.getHealth() - dmg < 0.0F && (part == this.getParts()[0] || part == this.getParts()[1])) {
				this.swingArm(EnumHand.MAIN_HAND);
				living.attackEntityFrom(DamageSource.causeMobDamage(this), 0.0F);
				living.setDead();
			}
		}

		if (target.isEntityAlive()) {
			if (target.attackEntityFrom(DamageSource.causeMobDamage(this), dmg)) {
				this.swingArm(EnumHand.MAIN_HAND);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if ("arrow".equals(source.getDamageType())) {
			amount *= 0.25F;
		}

		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		return this.attackEntityFrom(source, damage * 0.85F);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public void onRemovedFromWorld() {
		super.onRemovedFromWorld();
		for (BodyPart bodyPart : this.getParts()) {
			this.world.removeEntityDangerously(bodyPart);
		}
	}
}