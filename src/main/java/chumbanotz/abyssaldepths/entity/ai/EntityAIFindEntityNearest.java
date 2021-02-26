package chumbanotz.abyssaldepths.entity.ai;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIFindEntityNearest<T extends EntityLivingBase> extends EntityAIBase {
	protected final EntityLiving mob;
	private final Predicate<EntityLivingBase> predicate;
	private final EntityAINearestAttackableTarget.Sorter sorter;
	private EntityLivingBase target;
	private final Class<T> classToCheck;

	public EntityAIFindEntityNearest(EntityLiving mobIn, Class<T> classToCheck, Predicate<EntityLivingBase> predicate) {
		this.mob = mobIn;
		this.classToCheck = classToCheck;
		this.predicate = predicate;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(mobIn);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (!this.mob.isInWater() || this.mob.getRNG().nextInt(10) != 0) {
			return false;
		}

		if (this.classToCheck == EntityPlayer.class) {
			double followRange = this.getFollowRange();
			this.target = this.mob.world.getNearestAttackablePlayer(this.mob.posX, this.mob.posY, this.mob.posZ, followRange, followRange, null, player -> this.predicate.apply(player));
		} else {
			List<T> list = this.mob.world.getEntitiesWithinAABB(this.classToCheck, this.mob.getEntityBoundingBox().grow(this.getFollowRange()), this.predicate);
			if (!list.isEmpty()) {
				list.sort(this.sorter);
				this.target = list.get(0);
			}
		}

		return EntityAITarget.isSuitableTarget(this.mob, this.target, false, true);
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (!EntityAITarget.isSuitableTarget(this.mob, this.target, false, false)) {
			return false;
		}

		double followRange = this.getFollowRange();
		if (this.mob.getDistanceSq(this.target) > followRange * followRange) {
			return false;
		}

		return this.predicate.apply(this.target);
	}

	@Override
	public void startExecuting() {
		this.mob.setAttackTarget(this.target);
	}

	@Override
	public void resetTask() {
		this.mob.setAttackTarget(null);
		this.target = null;
	}

	private double getFollowRange() {
		IAttributeInstance iattributeinstance = this.mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
		return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
	}
}