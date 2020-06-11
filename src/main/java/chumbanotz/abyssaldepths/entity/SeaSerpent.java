package chumbanotz.abyssaldepths.entity;

import com.google.common.base.Predicate;

import chumbanotz.abyssaldepths.entity.ai.EntityAIHuntUnderwater;
import chumbanotz.abyssaldepths.util.Bone;
import chumbanotz.abyssaldepths.util.Euler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SeaSerpent extends ComplexCreature implements IMob {
	private static final Predicate<EntityLivingBase> IN_BOAT = target -> {
		return target.getRidingEntity() instanceof EntityBoat && target.getRidingEntity().isInWater();
	};
	private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(SeaSerpent.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Byte> HUNGER = EntityDataManager.createKey(SeaSerpent.class, DataSerializers.BYTE);
	public static final int[] PART_HEIGHT = new int[] {8, 14, 12, 10, 8, 6, 5, 4};
	public static final int[] PART_LENGTH = new int[] {18, 24, 24, 20, 14, 16, 18, 22};
	private final BodyPart[] partList = new BodyPart[8];
	private final Bone baseBone = new Bone();
	private final Bone[] boneList = new Bone[8];
	private final Euler[] targetAngles = new Euler[8];
	private int strikeTick;
	private boolean strikeStopped;
	private Vec3d oldVec;

	public SeaSerpent(World world) {
		super(world);
		this.baseBone.setLength(0.0F);
		this.boneList[0] = new Bone(this.baseBone);
		this.boneList[0].setLength((float)PART_LENGTH[0] / 16.0F);

		for (int i = 1; i < this.boneList.length; ++i) {
			this.boneList[i] = new Bone(i == 1 ? this.baseBone : this.boneList[i - 1]);
			this.boneList[i].setLength((float)(-PART_LENGTH[i]) / 16.0F);
		}

		for (int i = 0; i < this.partList.length; ++i) {
			this.targetAngles[i] = new Euler();
			this.partList[i] = new BodyPart(this, (float)PART_LENGTH[i] / 16.0F);
		}

		this.updateParts();
		this.setSize(1.1F, 1.1F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ATTACKING, false);
		this.dataManager.register(HUNGER, (byte)this.rand.nextInt(16));
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new SeaSerpent.AIHuntPrey(this, IN_BOAT));
		this.targetTasks.addTask(3, new SeaSerpent.AIHuntPrey(this, EntityLivingBase::isInWater));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(90.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
	}

	public boolean isAttacking() {
		return this.dataManager.get(ATTACKING);
	}

	private void setAttacking(boolean attacking) {
		this.dataManager.set(ATTACKING, attacking);
	}

	public int getHunger() {
		return this.dataManager.get(HUNGER);
	}

	private void setHunger(int hunger) {
		this.dataManager.set(HUNGER, (byte)hunger);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 25;
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	public BodyPart[] getPartList() {
		return this.partList;
	}

	@Override
	public Bone getBaseBone() {
		return this.baseBone;
	}

	@Override
	public Bone[] getBoneList() {
		return this.boneList;
	}

	@Override
	public void updatePitchRotations(float partialTick) {
		Euler euler;
		for (int i = this.boneList.length - 1; i > 1; --i) {
			if (partialTick == 1.0F) {
				euler = this.boneList[i].getRotation();
				euler.x += this.targetAngles[i].x = this.targetAngles[i - 1].x;
			} else {
				euler = this.boneList[i].getRotation();
				euler.x += this.targetAngles[i].x + (this.targetAngles[i - 1].x - this.targetAngles[i].x) * partialTick;
			}
		}

		this.targetAngles[1].x = -(this.currentPitch - this.prevCurrentPitch) * 2.4F;
		float moveScale = this.prevLimbSwingAmount + (this.limbSwingAmount - this.prevLimbSwingAmount) * partialTick;
		float moveTick = this.limbSwing - this.limbSwingAmount * (1.0F - partialTick);
		if (moveScale > 1.0F) {
			moveScale = 1.0F;
		}

		for (int i = 0; i < this.boneList.length; ++i) {
			float breatheAnim = MathHelper.sin(0.1F * ((float)this.ticksExisted + partialTick - (float)i * 6.0F));
			float moveAnim = MathHelper.sin(0.2F * (moveTick - (float)i * 2.0F)) * moveScale;
			euler = this.boneList[i].getRotation();
			euler.x += breatheAnim * 1.1F;
			euler = this.boneList[i].getRotation();
			euler.x += moveAnim * 6.0F;
			if (i == 0 && partialTick == 1.0F) {
				Euler angle = new Euler(this.currentPitch + 90.0F, this.rotationYaw, 0.0F);
				Vec3d vec = angle.rotateVector(moveAnim * 0.03F);
				this.motionX += vec.x;
				this.motionY += vec.y;
				this.motionZ += vec.z;
			}
		}
	}

	@Override
	public void updateYawRotations(float partialTick) {
		for (int i = this.boneList.length - 1; i > 1; --i) {
			Euler euler;
			if (partialTick == 1.0F) {
				euler = this.boneList[i].getRotation();
				euler.y += this.targetAngles[i].y = this.targetAngles[i - 1].y;
			} else {
				euler = this.boneList[i].getRotation();
				euler.y += this.targetAngles[i].y + (this.targetAngles[i - 1].y - this.targetAngles[i].y) * partialTick;
			}
		}

		this.targetAngles[1].y = -(this.currentYaw - this.prevCurrentYaw) * 2.5F;
		this.targetAngles[0].y = (this.currentYaw - this.prevCurrentYaw) * 1.5F;
	}

	private double getStrikeRange() {
		return 6.0D;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.world.isRemote) {
			if (this.getHunger() <= 0) {
				if (this.ticksExisted % 40 == 0) {
					this.heal(1.0F);
				}

				if (this.rand.nextInt(3600) == 0) {
					this.setHunger(14 + this.rand.nextInt(8));
				}
			}

			this.strikeTick = Math.max(0, this.strikeTick - 1);
		}
	}

	@Override
	public double moveByTarget(EntityLivingBase target) {
		double targetSpeed = this.getMovementSpeed() * 1.06D;
		double dSq = this.getDistanceSq(target);
		double range = 32.0D;
		double strikeRange = this.getStrikeRange();
		if (this.isAttacking()) {
			if (dSq < strikeRange * strikeRange) {
				if (this.targetVec == null) {
					this.targetVec = new Vec3d(target.posX - this.posX, target.posY - this.posY, target.posZ - this.posZ);
					this.targetVec = this.targetVec.normalize();
				}

				if (this.oldVec == null) {
					this.oldVec = this.getPositionVector();
				}

				if (this.isInWater()) {
					this.getLookHelper().setLookPositionWithEntity(target, 15.0F, 85.0F);
					float scale = 9.0F;
					targetSpeed *= 0.30000001192092896D;
					this.motionX += (this.targetVec.x * targetSpeed * (double)scale - this.motionX) * 0.6D;
					this.motionY += (this.targetVec.y * targetSpeed * (double)scale - this.motionY) * 0.6D;
					this.motionZ += (this.targetVec.z * targetSpeed * (double)scale - this.motionZ) * 0.6D;
				}
			} else if (!this.strikeStopped) {
				this.getLookHelper().setLookPositionWithEntity(target, 8.0F, 85.0F);
				this.targetVec = new Vec3d(target.posX - this.posX, target.posY - this.posY, target.posZ - this.posZ);
				this.targetVec = this.targetVec.normalize();
				this.oldVec = this.getPositionVector();
			}

			double dSq1 = this.getDistanceSq(this.oldVec.x, this.oldVec.y, this.oldVec.z);
			if (dSq1 > strikeRange * strikeRange || !this.isInWater() && !this.onGround || this.strikeStopped) {
				this.setAttacking(false);
				this.strikeStopped = false;
				this.strikeTick = 60 + this.rand.nextInt(20);
				this.targetVec = null;
				this.oldVec = null;
			}
		} else if (this.targetVec != null) {
			this.getLookHelper().setLookPosition(this.targetVec.x, this.targetVec.y, this.targetVec.z, 3.0F, 85.0F);
			if (this.collidedHorizontally && this.motionY < 0.20000000298023224D) {
				this.motionY = 0.20000000298023224D;
			}

			if (dSq > range * range) {
				this.moveTick = 0;
				this.targetVec = null;
			} else if (this.strikeTick <= 0 && dSq > strikeRange * strikeRange && this.rand.nextInt(70) == 0) {
				this.setAttacking(true);
				this.moveTick = 0;
				this.targetVec = null;
			}
		} else if (dSq > range * range) {
			this.getLookHelper().setLookPositionWithEntity(target, 10.0F, 85.0F);
		} else {
			targetSpeed = 0.0D;
			this.setRandomPathAround(target);
		}

		if (this.targetVec != null && this.targetVec.distanceTo(this.getPositionVector()) < 3.0D || this.moveTick > 120) {
			this.moveTick = 0;
			this.setRandomPathAround(target);
		}

		return targetSpeed;
	}

	@Override
	public boolean findNewPath() {
		return this.rand.nextInt(40) == 0 || this.onLand() && this.rand.nextInt(4) == 0;
	}

	@Override
	public boolean setRandomPath() {
		double x = this.posX + (10.0D + (double)this.rand.nextFloat() * 12.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		double y = this.posY + ((double)this.rand.nextFloat() - 0.5D) * 12.0D;
		double z = this.posZ + (10.0D + (double)this.rand.nextFloat() * 12.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		if (this.onLand()) {
			x = this.posX + (4.0D + (double)this.rand.nextFloat() * 16.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
			z = this.posZ + (4.0D + (double)this.rand.nextFloat() * 16.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		}

		if (this.isClearPath(x, y, z)) {
			this.targetVec = new Vec3d(x, y, z);
			return true;
		} else {
			return false;
		}
	}

	private boolean setRandomPathAround(EntityLivingBase living) {
		double x = living.posX + (4.0D + (double)this.rand.nextFloat() * 5.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		double y = living.getEntityBoundingBox().minY - 5.0D + ((double)this.rand.nextFloat() - 0.5D) * 20.0D;
		double z = living.posZ + (4.0D + (double)this.rand.nextFloat() * 5.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		if (this.onLand()) {
			x = this.posX + (4.0D + (double)this.rand.nextFloat() * 16.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
			z = this.posZ + (4.0D + (double)this.rand.nextFloat() * 16.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		}

		if (this.isClearPathWaterBelow(x, y, z)) {
			this.targetVec = new Vec3d(x, y, z);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void collideWithEntity(BodyPart part, Entity entity) {
		EntityLivingBase target = this.getAttackTarget();
		float dmg = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		BodyPart[] partList = this.getPartList();

		if (partList != null && part == partList[partList.length - 1] && this.dPosX * this.dPosX + this.dPosY * this.dPosY + this.dPosZ * this.dPosZ > 0.04000000000000001D) {
			entity.attackEntityFrom(DamageSource.causeMobDamage(this), dmg * 0.6F);
			double x = entity.posX - this.posX;
			double y = entity.posY - this.posY;
			double z = entity.posZ - this.posZ;
			double d0 = (double)MathHelper.sqrt(x * x + y * y + z * z);
			float scale = 0.12F * dmg;
			entity.motionX = x / d0 * (double)scale;
			entity.motionY = y / d0 * (double)scale;
			entity.motionZ = z / d0 * (double)scale;
			entity.velocityChanged = true;
		}

		if (this.isAttacking() && target != null && this.getDistanceSq(target) < this.getStrikeRange() * this.getStrikeRange()) {
			if (!this.world.isRemote && entity == target) {
				this.strikeStopped = true;
			}

			this.eatOrDamageEntity(part, entity, dmg);
			double x = entity.posX - this.posX;
			double y = entity.posY - this.posY;
			double z = entity.posZ - this.posZ;
			double d0 = (double)MathHelper.sqrt(x * x + y * y + z * z);
			Vec3d vec = new Vec3d(this.motionX, this.motionY, this.motionZ);
			vec = vec.normalize();
			float scale = 0.8F;
			entity.motionX = (x / d0 + vec.x) * (double)scale;
			entity.motionY = (y / d0 + vec.y) * (double)scale;
			entity.motionZ = (z / d0 + vec.z) * (double)scale;
			entity.velocityChanged = true;
		}
	}

	@Override
	public void onKillEntity(EntityLivingBase living) {
		int hunger = this.getHunger();
		boolean edible = false;
		if (living instanceof EntityPlayer || living instanceof IAnimals || living instanceof INpc) {
			edible = true;
		}

		if (!this.world.isRemote && hunger > 0 && edible) {
			this.setHunger(hunger - 1);
		}
	}

	@Override
	public void knockBack(Entity entity, float f, double x, double z) {
		if (!this.isInWater()) {
			super.knockBack(entity, f, x, z);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tagcompound) {
		super.writeEntityToNBT(tagcompound);
		tagcompound.setByte("Hunger", (byte)this.getHunger());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tagcompound) {
		super.readEntityFromNBT(tagcompound);
		this.setHunger(tagcompound.getByte("Hunger"));
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.rand.nextInt(12) == 0 && this.posY < 48.0D && super.getCanSpawnHere();
	}

	@Override
	protected float getSoundVolume() {
		return 0.8F;
	}

	static class AIHuntPrey extends EntityAIHuntUnderwater<EntityLivingBase> {
		public AIHuntPrey(SeaSerpent creature, Predicate<? super EntityLivingBase> targetSelector) {
			super(creature, EntityLivingBase.class, 0, true, false, entity -> {
				return !entity.isEntityUndead() && !(entity instanceof IMob) && !(entity instanceof EntityGolem) && targetSelector.test(entity);
			});
		}

		@Override
		public boolean shouldExecute() {
			return ((SeaSerpent)this.taskOwner).getHunger() > 0 && super.shouldExecute();
		}
	}
}