package chumbanotz.abyssaldepths.entity.ai;

import chumbanotz.abyssaldepths.entity.AquaticCreature;
import chumbanotz.abyssaldepths.util.ADGlobal;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.util.math.MathHelper;

public class AquaticBodyHelper extends EntityBodyHelper {
	private final AquaticCreature creature;
	protected float yaw;
	protected float pitch;
	protected float prevYaw;
	protected float prevPitch;
	protected double posX;
	protected double posY;
	protected double posZ;
	private final float pitchOffset;

	public AquaticBodyHelper(AquaticCreature livingIn, float pitchOffset) {
		super(livingIn);
		this.creature = livingIn;
		this.pitchOffset = pitchOffset;
	}

	@Override
	public void updateRenderAngles() {
		super.updateRenderAngles();
		double prevX = this.creature.posX;
		double prevY = this.creature.posY;
		double prevZ = this.creature.posZ;
		this.posX = this.creature.posX - prevX;
		this.posY = this.creature.posY - prevY;
		this.posZ = this.creature.posZ - prevZ;
		this.creature.rotationYaw = this.creature.renderYawOffset = this.creature.rotationYawHead = MathHelper.wrapDegrees(this.creature.rotationYawHead);
		if (this.getDifferenceLength() < 1.6000001778593287E-5D) {
			this.creature.rotationPitch = 0.0F;
		}

		this.yaw = ADGlobal.wrapAngleAround(this.yaw, this.creature.rotationYaw);
		this.pitch = ADGlobal.wrapAngleAround(this.pitch, this.creature.rotationPitch);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.prevYaw = ADGlobal.wrapAngleAround(this.prevYaw, this.yaw);
		this.prevPitch = ADGlobal.wrapAngleAround(this.prevPitch, this.pitch);
		this.yaw += (this.creature.rotationYaw - this.yaw) * 0.6F;

		if (!this.creature.isInWater()) {
			if (this.creature.onGround) {
				this.pitch *= 0.6F;
			} else {
				double d = (double)MathHelper.sqrt(this.posX * this.posX + this.posZ * this.posZ);
				float pitch = -((float)Math.atan2(this.posY, d)) * 57.295776F;
				this.pitch += (pitch - this.pitch) * 0.4F;
			}
		} else {
			this.pitch += (this.creature.rotationPitch - this.pitch) * this.pitchOffset;
		}
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPrevPitch() {
		return this.prevPitch;
	}

	public float getPrevYaw() {
		return this.prevYaw;
	}

	public double getDifferenceLength() {
		return this.posX * this.posX + this.posY * this.posY + this.posZ * this.posZ;
	}

	public float getBodyPitch(float partialTicks) {
		return this.prevPitch + (this.pitch - this.prevPitch) * partialTicks;
	}
}