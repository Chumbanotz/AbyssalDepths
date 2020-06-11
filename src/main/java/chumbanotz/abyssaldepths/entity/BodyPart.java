package chumbanotz.abyssaldepths.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BodyPart extends Entity {
	private static final DataParameter<Float> SCALE = EntityDataManager.createKey(BodyPart.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> BASE_ENTITY_ID = EntityDataManager.createKey(BodyPart.class, DataSerializers.VARINT);
	protected boolean spawned;
	private int inWaterTick;
	private boolean wasInWater;
	private ComplexCreature base;

	public BodyPart(World world) {
		super(world);
		this.spawned = true;
		this.preventEntitySpawning = true;
		this.ignoreFrustumCheck = true;
	}

	public BodyPart(ComplexCreature creature, float scale) {
		super(creature.world);
		this.base = creature;
		this.spawned = false;
		this.setScale(scale);
		this.setSize(scale, scale);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(SCALE, 0.0F);
		this.dataManager.register(BASE_ENTITY_ID, -1);
	}

	public float getScale() {
		return this.dataManager.get(SCALE);
	}

	private void setScale(float scale) {
		this.dataManager.set(SCALE, scale);
	}

	public int getBaseId() {
		return this.dataManager.get(BASE_ENTITY_ID);
	}

	private void setDataBase(ComplexCreature base) {
		this.dataManager.set(BASE_ENTITY_ID, base.getEntityId());
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if (SCALE.equals(key)) {
			this.setSize(this.getScale(), this.getScale());
		}
	}

	private boolean compareDataSize() {
		return this.width == this.getScale();
	}

	public ComplexCreature getBase() {
		return this.base;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return this.base.getPickedResult(target);
	}

	@Override
	public void onUpdate() {
		boolean wasInWater = this.world.getBlockState(new BlockPos((int)this.posX, (int)this.getEntityBoundingBox().minY, (int)this.posZ)).getMaterial() == Material.WATER;
		super.onUpdate();
		if (!this.compareDataSize()) {
			float w = this.getScale();
			this.setSize(w, w);
		}

		int baseId = this.getBaseId();
		if (!this.world.isRemote) {
			if (this.base != null && baseId == -1) {
				this.setDataBase(this.base);
			}
		} else if (this.base == null && baseId != -1) {
			this.base = (ComplexCreature)this.world.getEntityByID(baseId);
		}

		if (this.base != null && this.base.isDead) {
			this.setDead();
		}

		this.collideWithNearbyEntities();
		--this.inWaterTick;
		float scale = this.getScale();

		if (this.world.getBlockState(new BlockPos((int)this.posX, (int)this.getEntityBoundingBox().minY, (int)this.posZ)).getMaterial() == Material.WATER) {
			this.inWaterTick = 20;
			if (!this.wasInWater) {
				for (int i = 0; i < 12; ++i) {
					float x = (scale * 0.4F + this.rand.nextFloat() * 0.6F) * (float)(this.rand.nextBoolean() ? 1 : -1);
					float z = (scale * 0.4F + this.rand.nextFloat() * 0.6F) * (float)(this.rand.nextBoolean() ? 1 : -1);
					this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)x, this.posY + 0.5D + 0.5D * (double)scale * (double)this.rand.nextFloat(), this.posZ + (double)z, (double)(x * 0.1F), (double)(0.1F + 0.3F * scale * this.rand.nextFloat()), (double)(z * 0.1F), Block.getIdFromBlock(Blocks.WATER));
				}
			}
		} else if (this.inWaterTick > 0) {
			for (int i = 0; i < 4; ++i) {
				float x = scale * 1.2F * (this.rand.nextFloat() - 0.5F);
				float y = scale * 0.4F * this.rand.nextFloat();
				float z = scale * 1.2F * (this.rand.nextFloat() - 0.5F);
				this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + (double)x, this.getEntityBoundingBox().minY + (double)y, this.posZ + (double)z, 0.0D, 0.0D, 0.0D);
			}
		}

		this.wasInWater = wasInWater;
	}

	private void collideWithNearbyEntities() {
		if (this.base == null) {
			return;
		}

		for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(0.2D, 0.0D, 0.2D))) {
			if (entity != this.base && (!(entity instanceof BodyPart) || ((BodyPart)entity).base != this.base)) {
				if (entity.canBePushed()) {
					entity.applyEntityCollision(this);
				}

				this.base.collideWithEntity(this, entity);
			}
		}
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (!this.isRidingSameEntity(entity)) {
			double d0 = entity.posX - this.posX;
			double d1 = entity.posZ - this.posZ;
			double d2 = MathHelper.absMax(d0, d1);
			if (d2 >= 0.009999999776482582D) {
				d2 = (double)MathHelper.sqrt(d2);
				d0 /= d2;
				d1 /= d2;
				double d3 = 1.0D / d2;
				if (d3 > 1.0D) {
					d3 = 1.0D;
				}

				d0 *= d3;
				d1 *= d3;
				d0 *= 0.05000000074505806D;
				d1 *= 0.05000000074505806D;
				d0 *= (double)(1.0F - this.entityCollisionReduction);
				d1 *= (double)(1.0F - this.entityCollisionReduction);
				entity.addVelocity(d0, 0.0D, d1);
			}
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		return this.base.processInitialInteract(player, hand);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return this.base != null && this.base.attackEntityFrom(source, amount);
	}

	@Override
	public boolean isEntityEqual(Entity entityIn) {
		return super.isEntityEqual(entityIn) || entityIn == this.base || entityIn instanceof BodyPart && ((BodyPart)entityIn).base == this.base;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}
}