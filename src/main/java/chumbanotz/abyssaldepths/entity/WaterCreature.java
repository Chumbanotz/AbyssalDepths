package chumbanotz.abyssaldepths.entity;

import chumbanotz.abyssaldepths.util.ADGlobal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class WaterCreature extends AquaticCreature implements IAnimals {
	public School school;
	protected double fleeDistance = 10.0D;
	protected EntityLivingBase fleeFromEntity;
	protected EntityLivingBase followEntity;
	protected Vec3d fleeLookVec;

	public WaterCreature(World world) {
		super(world);
	}

	public void initSchool() {
		this.school = new School(this);
	}

	@Override
	public boolean isInWater() {
		return this.world.handleMaterialAcceleration(this.getEntityBoundingBox().grow(0.0D, -0.1D, 0.0D), Material.WATER, this);
	}

	public boolean shouldLeaveSchool() {
		return this.rand.nextInt(1800) == 0;
	}

	public boolean canCombineWith(School otherSchool) {
		return true;
	}

	@Override
	public void onUpdate() {
		double prevX = this.posX;
		double prevY = this.posY;
		double prevZ = this.posZ;
		int i = this.getAir();
		super.onUpdate();
		this.dPosX = this.posX - prevX;
		this.dPosY = this.posY - prevY;
		this.dPosZ = this.posZ - prevZ;
		if (this.isEntityAlive() && !this.isInWater()) {
			--i;
			this.setAir(i);
			if (this.getAir() == -20) {
				this.setAir(0);
				this.attackEntityFrom(DamageSource.DROWN, 2.0F);
			}
		} else {
			this.setAir(400);
		}

		if (!this.world.isRemote) {
			if (this.getTamed() && this.ticksExisted % 100 == 0) {
				this.heal(2.0F);
			}

			if (this.school == null) {
				this.initSchool();
			} else {
				WaterCreature leader = this.school.getLeader();
				if (leader == null || leader.isDead) {
					this.school.chooseRandomLeader();
				}

				if (leader == this) {
					this.school.updateSchool();
				}

				if (!this.school.ridinSolo() && this.shouldLeaveSchool()) {
					this.school.removeCreature(this);
				}
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.isInWater()) {
			if (this.onGround) {
				this.currentPitch *= 0.6F;
			} else {
				double d = (double)MathHelper.sqrt(this.dPosX * this.dPosX + this.dPosZ * this.dPosZ);
				float pitch = -((float)Math.atan2(this.dPosY, d)) * 57.295776F;
				this.currentPitch += (pitch - this.currentPitch) * 0.4F;
			}
		} else {
			this.currentPitch += (this.rotationPitch - this.currentPitch) * 0.05F;
		}
	}

	@Override
	protected void moveCreature() {
		double targetSpeed = 0.0D;
		if (this.fleeFromEntity != null && this.getRidingEntity() == null) {
			if (this.fleeLookVec == null || this.rand.nextInt(30) == 0) {
				this.fleeLookVec = new Vec3d(this.posX - this.fleeFromEntity.posX, 2.0D + (2.0D + (double)this.rand.nextFloat() * 3.0D) * (double)(this.rand.nextBoolean() ? 1 : -1), this.posZ - this.fleeFromEntity.posZ);

				for (int i = 0; i < 4 && !this.isClearPath(this.posX + this.fleeLookVec.x, this.posY + this.fleeLookVec.y, this.posZ + this.fleeLookVec.z); ++i) {
					this.fleeLookVec = new Vec3d(0.0D, 2.0D + (2.0D + (double)this.rand.nextFloat() * 3.0D) * (double)(this.rand.nextBoolean() ? 1 : -1), 0.0D);
				}

				this.fleeLookVec = this.fleeLookVec.normalize();
				this.fleeLookVec.scale(4.0F);
				this.fleeLookVec.add((double)this.rand.nextFloat(), 0.0D, (double)this.rand.nextFloat());
			}

			this.getLookHelper().setLookPosition(this.posX + this.fleeLookVec.x, this.posY + this.fleeLookVec.y, this.posZ + this.fleeLookVec.z, 10.0F, 85.0F);
			if (this.collidedHorizontally && this.motionY < 0.20000000298023224D) {
				this.motionY = 0.20000000298023224D;
			}

			targetSpeed = this.getMovementSpeed() * 2.0D;
			if (this.getDistanceSq(this.fleeFromEntity) > this.fleeDistance * this.fleeDistance) {
				this.resetFleeDistance();
				this.fleeFromEntity = null;
				this.fleeLookVec = null;
			}
		} else if (this.followEntity != null) {
			this.getLookHelper().setLookPositionWithEntity(this.followEntity, 6.0F, 85.0F);
			if (this.collidedHorizontally && this.motionY < 0.20000000298023224D) {
				this.motionY = 0.20000000298023224D;
			}

			if (this.getDistanceSq(this.followEntity) > 1.0D) {
				targetSpeed = this.getMovementSpeed();
			}
		} else if (this.targetVec != null) {
			this.getLookHelper().setLookPosition(this.targetVec.x, this.targetVec.y, this.targetVec.z, 10.0F, 85.0F);
			if (this.collidedHorizontally && this.motionY < 0.20000000298023224D) {
				this.motionY = 0.20000000298023224D;
			}

			targetSpeed = this.getMovementSpeed();
			if (!this.getTamed() && this.getRidingEntity() instanceof EntityPlayer) {
				targetSpeed *= 2.200000047683716D;
			}

			double closestDist = (double)this.getClosestPathDist();
			if (this.targetVec.squareDistanceTo(this.getPositionVector()) < closestDist * closestDist) {
				this.targetVec = null;
			}
		}

		if (this.findNewPath() && this.ticksExisted > 20 && this.school.getLeader() == this) {
			this.setRandomPath();
		}

		if (this.getFlees()) {
			this.fleeFromNearbyPlayers();
		}

		Vec3d vec = this.getLookVec();
		this.netSpeed += (targetSpeed - this.netSpeed) * 0.1D;
		if (this.onLand()) {
			this.netSpeed = 0.0D;
			if (this.targetVec != null && this.rand.nextInt(20) == 0) {
				this.motionX = vec.x * 0.17000000178813934D;
				this.motionY = 0.20000000298023224D;
				this.motionZ = vec.z * 0.17000000178813934D;
			}
		}

		this.move(MoverType.SELF, vec.x * this.netSpeed, vec.y * this.netSpeed, vec.z * this.netSpeed);
	}

	public final double getMovementSpeed() {
		return this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 0.800000011920929D;
	}

	public void resetFleeDistance() {
		this.fleeDistance = 8.0D + (double)this.rand.nextFloat() * 4.0D;
	}

	public float getClosestPathDist() {
		return 1.0F;
	}

	public boolean getFlees() {
		return !this.getTamed();
	}

	private void fleeFromNearbyPlayers() {
		EntityPlayer player = this.world.getNearestPlayerNotCreative(this, 9.0D);
		if (player != null) {
			float angle = (float)Math.atan2(player.posZ - this.posZ, player.posX - this.posX) * 57.295776F - 90.0F;
			angle = ADGlobal.wrapAngleAround(angle, this.rotationYaw);
			float delta = Math.abs(angle - this.rotationYaw);
			if (delta < 90.0F || this.getDistanceSq(player) < 4.0D && delta < 120.0F) {
				this.fleeFromEntity = player;
				this.targetVec = null;
			}
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		if (!this.world.isRemote && this.school != null) {
			if (!this.school.ridinSolo()) {
				this.school.removeCreature(this);
			}

			if (this.school.getLeader() == this) {
				this.school.chooseRandomLeader();
			}
		}
	}
}