package chumbanotz.abyssaldepths.util;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ADGlobal {
	public static final float PI = 3.1415927F;
	public static final float DEG_TO_RAD = 0.017453292F;
	public static final float RAD_TO_DEG = 57.295776F;

	public static float wrapAngleAround(float angle, float target) {
		while (target - angle >= 180.0F) {
			angle += 360.0F;
		}

		while (target - angle < -180.0F) {
			angle -= 360.0F;
		}

		return angle;
	}

	@Nullable
	public static Entity getPointedEntity(EntityLivingBase living, float maxDist) {
		Vec3d eyePos = living.getPositionEyes(1.0F);
		Vec3d lookVec = living.getLookVec();
		Vec3d reachVec = eyePos.add(lookVec.x * (double)maxDist, lookVec.y * (double)maxDist, lookVec.z * (double)maxDist);
		Entity pointedEntity = null;
		double distance = (double)maxDist;

		for (Entity entity : living.world.getEntitiesWithinAABBExcludingEntity(living, living.getEntityBoundingBox().offset(lookVec.x * (double)maxDist, lookVec.y * (double)maxDist, lookVec.z * (double)maxDist).grow(1.0D))) {
			if (entity.canBeCollidedWith() && !entity.isEntityEqual(living)) {
				AxisAlignedBB bbox = entity.getEntityBoundingBox().grow((double)entity.getCollisionBorderSize());
				RayTraceResult result = bbox.calculateIntercept(eyePos, reachVec);
				if (bbox.contains(eyePos)) {
					if (0.0D < distance || distance == 0.0D) {
						pointedEntity = entity;
						distance = 0.0D;
					}
				} else if (result != null) {
					double d3 = eyePos.distanceTo(result.hitVec);
					if (d3 < distance || distance == 0.0D) {
						if (entity == living.getRidingEntity() && !entity.canRiderInteract()) {
							if (distance == 0.0D) {
								pointedEntity = entity;
							}
						} else {
							pointedEntity = entity;
							distance = d3;
						}
					}
				}
			}
		}

		return pointedEntity;
	}
}