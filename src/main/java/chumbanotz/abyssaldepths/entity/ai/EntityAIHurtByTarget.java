package chumbanotz.abyssaldepths.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIHurtByTarget extends EntityAIBase {
	private final EntityLiving mob;
	private int revengeTimerOld;

	public EntityAIHurtByTarget(EntityLiving mob) {
		this.mob = mob;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return this.mob.getRevengeTimer() != this.revengeTimerOld && EntityAITarget.isSuitableTarget(this.mob, this.mob.getRevengeTarget(), false, true);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return EntityAITarget.isSuitableTarget(this.mob, this.mob.getAttackTarget(), false, this.mob.getRevengeTarget() != this.mob.getAttackTarget());
	}

	@Override
	public void startExecuting() {
		this.mob.setAttackTarget(this.mob.getRevengeTarget());
		this.revengeTimerOld = this.mob.getRevengeTimer();
	}

	@Override
	public void resetTask() {
		this.mob.setAttackTarget(null);
	}
}