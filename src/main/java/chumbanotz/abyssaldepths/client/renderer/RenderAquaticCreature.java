package chumbanotz.abyssaldepths.client.renderer;

import chumbanotz.abyssaldepths.entity.AquaticCreature;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.MathHelper;

public abstract class RenderAquaticCreature<T extends AquaticCreature> extends RenderLiving<T> {
	public RenderAquaticCreature(RenderManager rendermanagerIn, ModelBase base, float shadowSize) {
		super(rendermanagerIn, base, shadowSize);
	}

	@Override
	protected void preRenderCallback(T living, float partialTick) {
		float x = MathHelper.cos(((float)(living.ticksExisted + living.randNumTick) + partialTick) * 0.16F);
		float y = MathHelper.sin(((float)(living.ticksExisted + living.randNumTick) + partialTick) * 0.12F);
		float z = MathHelper.sin(((float)(living.ticksExisted + living.randNumTick) + partialTick) * 0.08F);
		float moveScale = 0.04F;
		if (living.isInWater() && !living.isBeingRidden()) {
			GlStateManager.translate(x * moveScale, y * moveScale, z * moveScale);
		}
	}

	@Override
	protected void applyRotations(T entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
		super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
		if (entityLiving.getRotatePitch()) {
			GlStateManager.rotate(-entityLiving.getCurrentPitch(partialTicks), 1.0F, 0.0F, 0.0F);
		}
	}
}