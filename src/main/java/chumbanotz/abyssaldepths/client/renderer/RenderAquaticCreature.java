package chumbanotz.abyssaldepths.client.renderer;

import chumbanotz.abyssaldepths.entity.AquaticCreature;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

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
		if (!entityLiving.getRotatePitch()) {
			super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
		} else {
			float fixedPitch = entityLiving.prevCurrentPitch + (entityLiving.currentPitch - entityLiving.prevCurrentPitch) * partialTicks;
			GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-fixedPitch, 1.0F, 0.0F, 0.0F);
			if (entityLiving.deathTime > 0) {
				float f3 = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
				f3 = MathHelper.sqrt(f3);
				if (f3 > 1.0F) {
					f3 = 1.0F;
				}

				GlStateManager.rotate(f3 * this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
			} else {
				String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName());
				if (s.equals("Dinnerbone") || s.equals("Grumm")) {
					GlStateManager.translate(0.0F, entityLiving.height + 0.1F, 0.0F);
					GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				}
			}
		}
	}
}