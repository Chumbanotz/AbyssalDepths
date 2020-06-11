package chumbanotz.abyssaldepths.entity.ai;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIHuntUnderwater<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
	public EntityAIHuntUnderwater(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, Predicate<? super T> targetSelector) {
		super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
	}

	@Override
	public boolean shouldExecute() {
		return this.taskOwner.isInWater() && super.shouldExecute();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.targetEntitySelector.test(this.targetEntity) && super.shouldContinueExecuting();
	}

	@Override
	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return this.taskOwner.getEntityBoundingBox().grow(targetDistance);
	}
}