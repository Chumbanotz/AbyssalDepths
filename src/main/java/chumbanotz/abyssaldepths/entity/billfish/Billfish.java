package chumbanotz.abyssaldepths.entity.billfish;

import chumbanotz.abyssaldepths.entity.BodyPart;
import chumbanotz.abyssaldepths.entity.ComplexCreature;
import chumbanotz.abyssaldepths.entity.ai.EntityAIHuntUnderwater;
import chumbanotz.abyssaldepths.entity.fish.CommonFish;
import chumbanotz.abyssaldepths.util.ADGlobal;
import chumbanotz.abyssaldepths.util.Bone;
import chumbanotz.abyssaldepths.util.Euler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class Billfish extends ComplexCreature implements IAnimals {
	protected static final DataParameter<Byte> MARKINGS = EntityDataManager.createKey(Billfish.class, DataSerializers.BYTE);
	private int attackTime;
	private final BodyPart[] partList = new BodyPart[4];
	private final Bone baseBone = new Bone();
	private final Bone[] boneList = new Bone[4];
	private final Euler[] targetAngles = new Euler[4];

	public Billfish(World world) {
		super(world);
		this.baseBone.setLength(0.0F);
		this.boneList[0] = new Bone(this.baseBone);
		this.boneList[0].setLength(this.getPartLength()[0] / 16.0F);

		for (int i = 1; i < this.boneList.length; ++i) {
			this.boneList[i] = new Bone(i == 1 ? this.baseBone : this.boneList[i - 1]);
			this.boneList[i].setLength(-this.getPartLength()[i] / 16.0F);
		}

		for (int i = 0; i < this.partList.length; ++i) {
			this.targetAngles[i] = new Euler();
			this.partList[i] = new BodyPart(this, this.getPartLength()[i] / 16.0F);
		}

		this.updateParts();
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAIHuntUnderwater<>(this, CommonFish.class, 0, true, false, CommonFish::isInWater));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(MARKINGS, (byte)0);
	}

	public boolean hasBanner() {
		return (this.dataManager.get(MARKINGS) & 1) != 0;
	}

	protected void setBanner(boolean banner) {
		byte b0 = this.dataManager.get(MARKINGS);
		this.dataManager.set(MARKINGS, banner ? (byte)(b0 | 1) : (byte)(b0 & -2));
	}

	public boolean hasStripes() {
		return (this.dataManager.get(MARKINGS) & 2) != 0;
	}

	protected void setStripes(boolean stripes) {
		byte b0 = this.dataManager.get(MARKINGS);
		this.dataManager.set(MARKINGS, stripes ? (byte)(b0 | 2) : (byte)(b0 & -3));
	}

	protected abstract float[] getPartLength();

	protected abstract double getStrikeRange();

	protected abstract float getAttackReach();

	protected abstract int getMaxAttackTime();

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
	public void updateYawRotations(float partialTick) {
		Euler euler;
		for (int i = this.boneList.length - 1; i > 1; --i) {
			if (partialTick == 1.0F) {
				euler = this.boneList[i].getRotation();
				euler.y += this.targetAngles[i].y = this.targetAngles[i - 1].y;
			} else {
				euler = this.boneList[i].getRotation();
				euler.y += this.targetAngles[i].y + (this.targetAngles[i - 1].y - this.targetAngles[i].y) * partialTick;
			}
		}

		this.targetAngles[1].y = -(this.currentYaw - this.prevCurrentYaw) * 1.4F;
		this.targetAngles[0].y = (this.currentYaw - this.prevCurrentYaw) * 1.4F;
		float moveScale = this.prevLimbSwingAmount + (this.limbSwingAmount - this.prevLimbSwingAmount) * partialTick;
		float moveTick = this.limbSwing - this.limbSwingAmount * (1.0F - partialTick);
		if (moveScale > 1.0F) {
			moveScale = 1.0F;
		}

		for (int i = 0; i < this.boneList.length; ++i) {
			float breatheAnim = MathHelper.sin(0.1F * ((float)this.ticksExisted + partialTick - (float)i * 1.4F));
			float moveAnim = MathHelper.sin(0.7F * (moveTick - (float)i * 1.4F)) * moveScale;
			euler = this.boneList[i].getRotation();
			euler.y += breatheAnim * 1.1F;
			euler = this.boneList[i].getRotation();
			euler.y += moveAnim * 8.0F;
		}
	}

	@Override
	protected void moveCreature() {
		super.moveCreature();
		if (this.attackTime > this.getMaxAttackTime() && this.isEntityAlive()) {
			float dmg = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
			Entity entity = ADGlobal.getPointedEntity(this, this.getAttackReach());
			ComplexCreature base = entity instanceof BodyPart ? ((BodyPart)entity).getBase() : null;
			boolean attack = true;
			if (base != null && base.getClass().equals(this.getClass())) {
				attack = false;
			}

			if (this.getAttackTarget() == base || this.rand.nextInt(160) == 0) {
				attack = true;
			}

			if (entity != null && attack) {
				this.eatOrDamageEntity(entity, dmg);
			}
		}

		--this.attackTime;
	}

	@Override
	public double moveByTarget(EntityLivingBase target) {
		if (this.attackTime > 0) {
			this.getLookHelper().setLookPositionWithEntity(target, this.attackTime > this.getMaxAttackTime() ? 15.0F : 0.0F, 85.0F);
		} else {
			this.getLookHelper().setLookPositionWithEntity(target, 8.0F, 85.0F);
		}

		double strikeRange = this.getStrikeRange();
		if (this.attackTime <= 0 && this.getDistanceSq(target) < strikeRange * strikeRange && this.rand.nextInt(4) == 0) {
			double x = target.posX - this.posX;
			double y = target.posY - this.posY;
			double z = target.posZ - this.posZ;
			double d = Math.sqrt(x * x + y * y + z * z);
			double scale = this.getMovementSpeed() / 0.8D;
			this.motionX = x / d * scale;
			this.motionY = y / d * scale;
			this.motionZ = z / d * scale;
			this.attackTime = 16 + this.rand.nextInt(5);
		}

		return this.attackTime > 0 ? 0.0D : this.getMovementSpeed() * 1.2D;
	}

	@Override
	public boolean findNewPath() {
		return this.rand.nextInt(45) == 0 || this.onLand() && this.rand.nextInt(4) == 0;
	}

	@Override
	public boolean setRandomPath() {
		double x = this.posX + (6.0D + (double)this.rand.nextFloat() * 6.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		double y = this.posY + ((double)this.rand.nextFloat() - 0.5D) * 8.0D + this.addPathY();
		double z = this.posZ + (6.0D + (double)this.rand.nextFloat() * 6.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		if (this.onLand()) {
			x = this.posX + (4.0D + (double)this.rand.nextFloat() * 12.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
			z = this.posZ + (4.0D + (double)this.rand.nextFloat() * 12.0D) * (double)(this.rand.nextBoolean() ? 1 : -1);
		}

		if (this.isClearPath(x, y, z)) {
			this.targetVec = new Vec3d(x, y, z);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("Banner", this.hasBanner());
		compound.setBoolean("Stripes", this.hasStripes());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.setBanner(compound.getBoolean("Banner"));
		this.setStripes(compound.getBoolean("Stripes"));
	}
}