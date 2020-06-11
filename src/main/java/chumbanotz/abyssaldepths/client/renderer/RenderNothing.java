package chumbanotz.abyssaldepths.client.renderer;

import chumbanotz.abyssaldepths.entity.BodyPart;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderNothing extends Render<BodyPart> {
	public RenderNothing(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(BodyPart entity, double x, double y, double z, float entityYaw, float partialTicks) {
	}

	@Override
	protected ResourceLocation getEntityTexture(BodyPart entity) {
		return null;
	}
}