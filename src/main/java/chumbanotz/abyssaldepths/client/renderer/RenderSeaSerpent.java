package chumbanotz.abyssaldepths.client.renderer;

import chumbanotz.abyssaldepths.AbyssalDepths;
import chumbanotz.abyssaldepths.client.model.ModelSeaSerpent;
import chumbanotz.abyssaldepths.entity.SeaSerpent;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderSeaSerpent extends RenderAquaticCreature<SeaSerpent> {
	private static final ResourceLocation TEXTURE = AbyssalDepths.getEntityTexture("sea_serpent");

	public RenderSeaSerpent(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSeaSerpent(), 0.0F);
	}

	@Override
	public ResourceLocation getEntityTexture(SeaSerpent entity) {
		return TEXTURE;
	}
}