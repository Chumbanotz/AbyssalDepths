package chumbanotz.abyssaldepths.client.renderer;

import chumbanotz.abyssaldepths.AbyssalDepths;
import chumbanotz.abyssaldepths.client.model.ModelFish;
import chumbanotz.abyssaldepths.entity.fish.Basslet;
import chumbanotz.abyssaldepths.entity.fish.Butterflyfish;
import chumbanotz.abyssaldepths.entity.fish.Clownfish;
import chumbanotz.abyssaldepths.entity.fish.CommonFish;
import chumbanotz.abyssaldepths.entity.fish.Fish;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderFish extends RenderAquaticCreature<Fish> {
	private static final ResourceLocation fishTexture = AbyssalDepths.getEntityTexture("fish");
	private static final ResourceLocation fishGrayTexture = AbyssalDepths.getEntityTexture("fish_grayscale");
	private static final ResourceLocation clownfishTexture = AbyssalDepths.getEntityTexture("clownfish");
	private static final ResourceLocation clownfishTexture1 = AbyssalDepths.getEntityTexture("clownfish_1");
	private static final ResourceLocation butterflyfishTexture = AbyssalDepths.getEntityTexture("butterflyfish/grayscale");
	private static final ResourceLocation butterflyfishMaskedTexture = AbyssalDepths.getEntityTexture("butterflyfish/masked");
	private static final ResourceLocation butterflyfishRaccoonTexture = AbyssalDepths.getEntityTexture("butterflyfish/raccoon");
	private static final ResourceLocation butterflyfishSpotfinTexture = AbyssalDepths.getEntityTexture("butterflyfish/spotfin");
	private static final ResourceLocation bannerfishTexture = AbyssalDepths.getEntityTexture("bannerfish");
	private static final ResourceLocation bassletFairyTexture = AbyssalDepths.getEntityTexture("basslet/fairy");
	private static final ResourceLocation bassletBlackCapTexture = AbyssalDepths.getEntityTexture("basslet/blackcap");

	public RenderFish(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelFish(), 0.4F);
	}

	public RenderFish(RenderManager rendermanagerIn, ModelBase base, float shadowSize) {
		super(rendermanagerIn, base, shadowSize);
	}

	@Override
	public ResourceLocation getEntityTexture(Fish entity) {
		if (entity instanceof CommonFish && ((CommonFish)entity).isColorful()) {
			return fishGrayTexture;
		} else if (entity instanceof Clownfish) {
			return ((Clownfish)entity).hasOneStripe() ? clownfishTexture1 : clownfishTexture;
		} else if (entity instanceof Butterflyfish.Masked) {
			return butterflyfishMaskedTexture;
		} else if (entity instanceof Butterflyfish.Raccoon) {
			return butterflyfishRaccoonTexture;
		} else if (entity instanceof Butterflyfish.Spotfin) {
			return butterflyfishSpotfinTexture;
		} else if (entity instanceof Butterflyfish.Banner) {
			return bannerfishTexture;
		} else if (entity instanceof Butterflyfish) {
			return butterflyfishTexture;
		} else if (entity instanceof Basslet.Fairy) {
			return bassletFairyTexture;
		} else {
			return entity instanceof Basslet.BlackCap ? bassletBlackCapTexture : fishTexture;
		}
	}

	@Override
	protected void preRenderCallback(Fish living, float partialTick) {
		super.preRenderCallback(living, partialTick);
		if (living.getScale() != 1.0F) {
			GlStateManager.scale(living.getScale(), living.getScale(), living.getScale());
		}
	}

	@Override
	protected void renderModel(Fish living, float par2, float par3, float par4, float par5, float par6, float par7) {
		this.shadowSize = living.getScale() * 0.25F;
		if (!living.isColorful()) {
			super.renderModel(living, par2, par3, par4, par5, par6, par7);
		} else {
			this.bindEntityTexture(living);
			GlStateManager.color(living.getRed(), living.getGreen(), living.getBlue());
			if (!living.isInvisible()) {
				this.mainModel.render(living, par2, par3, par4, par5, par6, par7);
			} else if (!living.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
				GlStateManager.pushMatrix();
				GlStateManager.color(living.getRed(), living.getGreen(), living.getBlue(), 0.15F);
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(770, 771);
				GlStateManager.alphaFunc(516, 0.003921569F);
				this.mainModel.render(living, par2, par3, par4, par5, par6, par7);
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.popMatrix();
				GlStateManager.depthMask(true);
			} else {
				this.mainModel.setRotationAngles(par2, par3, par4, par5, par6, par7, living);
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F);
		}
	}
}