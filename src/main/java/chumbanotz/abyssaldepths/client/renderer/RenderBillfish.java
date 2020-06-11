package chumbanotz.abyssaldepths.client.renderer;

import java.util.HashMap;
import java.util.Map;

import chumbanotz.abyssaldepths.AbyssalDepths;
import chumbanotz.abyssaldepths.client.model.ModelBillfish;
import chumbanotz.abyssaldepths.entity.billfish.Billfish;
import chumbanotz.abyssaldepths.entity.billfish.Swordfish;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.util.ResourceLocation;

public class RenderBillfish extends RenderAquaticCreature<Billfish> {
	private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = new HashMap<>();
	private static final ResourceLocation SAILFISH_TEXTURE = AbyssalDepths.getEntityTexture("billfish/sailfish");
	private static final ResourceLocation SWORDFISH_TEXTURE = AbyssalDepths.getEntityTexture("billfish/swordfish");
	private static final ResourceLocation BLUE_SWORDFISH_TEXTURE = AbyssalDepths.getEntityTexture("billfish/blue_swordfish");

	public RenderBillfish(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelBillfish(), 0.0F);
	}

	private ResourceLocation getBillfishTexture(Billfish billfish) {
		if (billfish instanceof Swordfish) {
			return ((Swordfish)billfish).isBlue() ? BLUE_SWORDFISH_TEXTURE : SWORDFISH_TEXTURE;
		} else {
			return SAILFISH_TEXTURE;
		}
	}

	@Override
	protected void preRenderCallback(Billfish living, float partialTick) {
		if (living instanceof Swordfish) {
			GlStateManager.scale(1.3F, 1.3F, 1.3F);
		}
	}

	@Override
	public ResourceLocation getEntityTexture(Billfish entity) {
		if (!entity.hasBanner() && !entity.hasStripes()) {
			return this.getBillfishTexture(entity);
		} else {
			String textureId = this.getTexture(entity);
			ResourceLocation layered = LAYERED_LOCATION_CACHE.get(textureId);
			if (layered == null) {
				layered = new ResourceLocation(textureId);
				String[] textures = new String[] {"abyssaldepths:" + this.getBillfishTexture(entity).getPath(), null, null};
				if (entity.hasBanner()) {
					textures[1] = "abyssaldepths:textures/entity/billfish/banner.png";
				}

				if (entity.hasStripes()) {
					textures[2] = "abyssaldepths:textures/entity/billfish/stripes.png";
				}

//				System.out.println(layered);
				Minecraft.getMinecraft().getTextureManager().loadTexture(layered, new LayeredTexture(textures));
				LAYERED_LOCATION_CACHE.put(textureId, layered);
			}

			return layered;
		}
	}

	private String getTexture(Billfish billfishEntity) {
		String path = AbyssalDepths.MOD_ID + ":textures/entity/billfish/" + billfishEntity.getClass().getSimpleName().replace("Entity", "").toLowerCase() + "/";
		if (billfishEntity instanceof Swordfish && ((Swordfish)billfishEntity).isBlue())
			path += "blue";
		if (billfishEntity.hasBanner())
			path += "banner";
		if (billfishEntity.hasStripes())
			path += "stripes";
		return path + ".png";
	}
}