package chumbanotz.abyssaldepths.entity;

import chumbanotz.abyssaldepths.AbyssalDepths;
import chumbanotz.abyssaldepths.util.ADGlobal;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class AquaticCreature extends EntityCreature implements IEntityAdditionalSpawnData {
	public int randNumTick;
	protected float currentYaw;
	public float currentPitch;
	protected float prevCurrentYaw;
	public float prevCurrentPitch;
	protected double netSpeed;
	protected double dPosX;
	protected double dPosY;
	protected double dPosZ;
	protected Vec3d targetVec;

	public AquaticCreature(World world) {
		super(world);
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 1 + this.world.rand.nextInt(3);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return super.canBeRidden(entityIn) && entityIn instanceof EntityLivingBase;
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return false;
	}

	public boolean onLand() {
		return !this.isInWater() && this.onGround;
	}

	public boolean getTamed() {
		return false;
	}

	public boolean getRotatePitch() {
		return true;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.rotationYaw = this.renderYawOffset = this.rotationYawHead = MathHelper.wrapDegrees(this.rotationYawHead);
		if (this.dPosX * this.dPosX + this.dPosY * this.dPosY + this.dPosZ * this.dPosZ < 1.6000001778593287E-5D) {
			this.rotationPitch = 0.0F;
		}

		this.currentYaw = ADGlobal.wrapAngleAround(this.currentYaw, this.rotationYaw);
		this.currentPitch = ADGlobal.wrapAngleAround(this.currentPitch, this.rotationPitch);
		this.prevCurrentYaw = this.currentYaw;
		this.prevCurrentPitch = this.currentPitch;
		this.prevCurrentYaw = ADGlobal.wrapAngleAround(this.prevCurrentYaw, this.currentYaw);
		this.prevCurrentPitch = ADGlobal.wrapAngleAround(this.prevCurrentPitch, this.currentPitch);
		this.currentYaw += (this.rotationYaw - this.currentYaw) * 0.6F;
	}

	@Override
	protected void updateAITasks() {
		this.moveCreature();
	}

	/** Server side only */
	protected abstract void moveCreature();

	public boolean findNewPath() {
		return this.rand.nextInt(70) == 0 || this.onLand() && this.rand.nextInt(10) == 0;
	}

	protected double addPathY() {
		return (double)this.getEyeHeight();
	}

	public boolean setRandomPath() {
		double x = this.posX + (3.0D + (double)this.rand.nextFloat() * 3.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		double y = this.posY + ((double)this.rand.nextFloat() - 0.5D) * 6.0D + this.addPathY();
		double z = this.posZ + (3.0D + (double)this.rand.nextFloat() * 3.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
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

	public boolean isClearPath(double x, double y, double z) {
		boolean water = this.world.getBlockState(new BlockPos((int)x, (int)y, (int)z)).getMaterial() == Material.WATER;
		boolean seen = this.world.rayTraceBlocks(this.getPositionEyes(1.0F), new Vec3d(x, y, z)) == null;
		return water && seen;
	}

	public boolean isClearPathWaterBelow(double x, double y, double z) {
		boolean water = this.world.getBlockState(new BlockPos((int)x, (int)y, (int)z)).getMaterial() == Material.WATER;
		boolean waterBelow = this.world.getBlockState(new BlockPos((int)x, (int)y - 1, (int)z)).getMaterial() == Material.WATER;
		boolean seen = this.world.rayTraceBlocks(this.getPositionEyes(1.0F), new Vec3d(x, y, z)) == null;
		return (water || waterBelow) && seen;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.randNumTick = this.rand.nextInt(100);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		double posY;
		if (this.isInWater()) {
			posY = this.posY;
			this.moveRelative(strafe, vertical, forward, 0.04F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.84D;
			this.motionY *= 0.84D;
			this.motionZ *= 0.84D;
			if (this.collidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + posY, this.motionZ)) {
				this.motionY = 0.30000001192092896D;
			}
		} else if (this.isInLava()) {
			posY = this.posY;
			this.moveRelative(strafe, vertical, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
			if (!this.hasNoGravity()) {
				this.motionY -= 0.02D;
			}

			if (this.collidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + posY, this.motionZ)) {
				this.motionY = 0.30000001192092896D;
			}
		} else {
			float f2 = 0.91F;
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(this.posX, this.getEntityBoundingBox().minY - 1.0D, this.posZ);

			if (this.onGround) {
				IBlockState underState = this.world.getBlockState(blockpos$pooledmutableblockpos);
				f2 = underState.getBlock().getSlipperiness(underState, this.world, blockpos$pooledmutableblockpos, this) * 0.91F;
			}

			float f3 = 0.16277136F / (f2 * f2 * f2);
			float friction;
			if (this.onGround) {
				friction = this.getAIMoveSpeed() * f3;
			} else {
				friction = this.jumpMovementFactor;
			}

			this.moveRelative(strafe, vertical, forward, friction);
			f2 = 0.91F;
			if (this.onGround) {
				IBlockState underState = this.world.getBlockState(blockpos$pooledmutableblockpos);
				f2 = underState.getBlock().getSlipperiness(underState, this.world, blockpos$pooledmutableblockpos, this) * 0.91F;
				f2 *= 0.1F;
			}

			if (this.isOnLadder()) {
				this.motionX = MathHelper.clamp(this.motionX, -0.15000000596046448D, 0.15000000596046448D);
				this.motionZ = MathHelper.clamp(this.motionZ, -0.15000000596046448D, 0.15000000596046448D);
				this.fallDistance = 0.0F;

				if (this.motionY < -0.15D) {
					this.motionY = -0.15D;
				}
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			if (this.collidedHorizontally && this.isOnLadder()) {
				this.motionY = 0.2D;
			}

			if (this.isPotionActive(MobEffects.LEVITATION)) {
				this.motionY += (0.05D * (double)(this.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motionY) * 0.2D;
			} else {
				blockpos$pooledmutableblockpos.setPos(this.posX, 0.0D, this.posZ);

				if (!this.world.isRemote || this.world.isBlockLoaded(blockpos$pooledmutableblockpos) && this.world.getChunk(blockpos$pooledmutableblockpos).isLoaded()) {
					if (!this.hasNoGravity()) {
						this.motionY -= 0.08D;
					}
				} else if (this.posY > 0.0D) {
					this.motionY = -0.1D;
				} else {
					this.motionY = 0.0D;
				}
			}

			f2 *= 0.4F;
			this.motionY *= 0.9800000190734863D;
			this.motionX *= (double)f2;
			this.motionZ *= (double)f2;
			blockpos$pooledmutableblockpos.release();
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double x = this.posX - this.prevPosX;
		double y = this.posY - this.prevPosY;
		double z = this.posZ - this.prevPosZ;
		if (!this.world.isRemote) {
			x = this.dPosX;
			y = this.dPosY;
			z = this.dPosZ;
		}

		float f6 = MathHelper.sqrt(x * x + y * y + z * z) * 4.0F;
		if (f6 > 1.0F) {
			f6 = 1.0F;
		}

		if (!this.isInWater() && !this.onGround) {
			this.limbSwingAmount *= 0.4F;
		} else {
			float delta = f6 - this.limbSwingAmount;
			if (delta >= 0.0F) {
				this.limbSwingAmount += delta * 0.4F;
			} else {
				this.limbSwingAmount += delta * 0.1F;
			}
		}

		this.limbSwing += this.limbSwingAmount;
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
		double xx = this.motionX;
		double yy = this.motionY;
		double zz = this.motionZ;
		super.knockBack(entityIn, strength, xRatio, zRatio);
		if (this.isInWater()) {
			this.motionX += (xx - this.motionX) * 0.5D;
			this.motionY += (yy - this.motionY) * 0.800000011920929D;
			this.motionZ += (zz - this.motionZ) * 0.5D;
		} else {
			this.motionY += (yy - this.motionY) * 0.4000000059604645D;
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		return true;
	}

	@Override
	public boolean isNotColliding() {
		return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
	}

	@Override
	public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
		if (forSpawnCount && this.isNoDespawnRequired()) {
			return false;
		} else {
			return type == EnumCreatureType.WATER_CREATURE;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.randNumTick);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.randNumTick = additionalData.readInt();
	}

	@Override
	protected ResourceLocation getLootTable() {
		return AbyssalDepths.prefix("entities/" + EntityList.getKey(this).getPath());
	}
}