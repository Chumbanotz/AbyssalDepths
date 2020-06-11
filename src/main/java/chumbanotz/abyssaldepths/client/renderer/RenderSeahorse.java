package chumbanotz.abyssaldepths.client.renderer;

import java.util.Map;

import com.google.common.collect.Maps;

import chumbanotz.abyssaldepths.AbyssalDepths;
import chumbanotz.abyssaldepths.client.model.ModelSeahorse;
import chumbanotz.abyssaldepths.entity.Seahorse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.util.ResourceLocation;

public class RenderSeahorse extends RenderAquaticCreature<Seahorse> {
	private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.newHashMap();
	private static final ResourceLocation TEXTURE = AbyssalDepths.getEntityTexture("seahorse/grayscale");
	private static final ResourceLocation EYES_TEXTURE = AbyssalDepths.getEntityTexture("seahorse/eyes");

	public RenderSeahorse(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSeahorse(), 0.4F);
		this.addLayer(new RenderSeahorse.LayerEyes());
	}

	@Override
	public ResourceLocation getEntityTexture(Seahorse entity) {
		if (entity.getMark() == 0) {
			return TEXTURE;
		} else {
			String textureId = this.getTextureId(entity);
			ResourceLocation layered = LAYERED_LOCATION_CACHE.get(textureId);
			if (layered == null) {
				layered = new ResourceLocation(textureId);
				String[] textures = new String[] {"abyssaldepths:textures/entity/seahorse/grayscale.png", "abyssaldepths:textures/entity/seahorse/mark" + entity.getMark() + ".png"};
				Minecraft.getMinecraft().getTextureManager().loadTexture(layered, new LayeredTexture(textures));
				LAYERED_LOCATION_CACHE.put(textureId, layered);
			}

			return layered;
		}
	}

	@Override
	protected void renderModel(Seahorse living, float par2, float par3, float par4, float par5, float par6, float par7) {
		this.bindEntityTexture(living);
		int color = living.getColor();
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color >> 0 & 255) / 255.0F;
		GlStateManager.color(red, green, blue);
		if (!living.isInvisible()) {
			this.mainModel.render(living, par2, par3, par4, par5, par6, par7);
		} else if (!living.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
			GlStateManager.pushMatrix();
			GlStateManager.color(red, green, blue, 0.15F);
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
	}

	private String getTextureId(Seahorse seahorse) {
		String path = "abyssaldepths:textures/entity/seahorse/";
		String markNo = String.valueOf(seahorse.getMark());
		return path + markNo + ".png";
	}

	class LayerEyes implements LayerRenderer<Seahorse> {
		private final ModelSeahorse modelSeahorse = new ModelSeahorse(0.01F);

		@Override
		public void doRenderLayer(Seahorse entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			if (!entitylivingbaseIn.isInvisible()) {
				bindTexture(EYES_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				this.modelSeahorse.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			}
		}

		@Override
		public boolean shouldCombineTextures() {
			return true;
		}
	}
}