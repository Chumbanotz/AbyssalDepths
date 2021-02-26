package chumbanotz.abyssaldepths.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class BodyPart extends MultiPartEntityPart {
	private int inWaterTick;
	private boolean wasInWater;

	public BodyPart(ComplexCreature parent, float size) {
		super(parent, "bodyPart", size, size);
		this.preventEntitySpawning = true;
	}

	public ComplexCreature getParent() {
		return (ComplexCreature)this.parent;
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return this.getParent().getPickedResult(target);
	}

	@Override
	public void onUpdate() {
		boolean wasInWater = this.world.getBlockState(new BlockPos((int)this.posX, (int)this.getEntityBoundingBox().minY, (int)this.posZ)).getMaterial() == Material.WATER;
		super.onUpdate();
		this.collideWithNearbyEntities();
		--this.inWaterTick;

		if (this.world.getBlockState(new BlockPos((int)this.posX, (int)this.getEntityBoundingBox().minY, (int)this.posZ)).getMaterial() == Material.WATER) {
			this.inWaterTick = 20;
			if (!this.wasInWater) {
				for (int i = 0; i < 12; ++i) {
					float x = (this.width * 0.4F + this.rand.nextFloat() * 0.6F) * (float)(this.rand.nextBoolean() ? 1 : -1);
					float z = (this.width * 0.4F + this.rand.nextFloat() * 0.6F) * (float)(this.rand.nextBoolean() ? 1 : -1);
					this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)x, this.posY + 0.5D + 0.5D * (double)this.width * (double)this.rand.nextFloat(), this.posZ + (double)z, (double)(x * 0.1F), (double)(0.1F + 0.3F * this.width * this.rand.nextFloat()), (double)(z * 0.1F), Block.getIdFromBlock(Blocks.WATER));
				}
			}
		} else if (this.inWaterTick > 0) {
			for (int i = 0; i < 4; ++i) {
				float x = this.width * 1.2F * (this.rand.nextFloat() - 0.5F);
				float y = this.width * 0.4F * this.rand.nextFloat();
				float z = this.width * 1.2F * (this.rand.nextFloat() - 0.5F);
				this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + (double)x, this.getEntityBoundingBox().minY + (double)y, this.posZ + (double)z, 0.0D, 0.0D, 0.0D);
			}
		}

		this.wasInWater = wasInWater;
	}

	private void collideWithNearbyEntities() {
		for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(0.2D, 0.0D, 0.2D))) {
			if (entity.canBeCollidedWith() && !this.isEntityEqual(entity)) {
				if (entity.canBePushed()) {
					entity.applyEntityCollision(this);
				}

				this.getParent().collideWithEntity(this, entity);
			}
		}
	}

	@Override
	public boolean isEntityEqual(Entity entityIn) {
		return super.isEntityEqual(entityIn) || entityIn instanceof BodyPart && ((BodyPart)entityIn).parent == this.parent;
	}
}