package chumbanotz.abyssaldepths.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class JointModelRenderer extends ModelRenderer {
	private ModelRenderer model;

	public JointModelRenderer(ModelBase model) {
		this(model, null);
		super.addChild(this.model = new ModelRenderer(model));
	}

	public JointModelRenderer(ModelBase model, int x, int y) {
		this(model);
		this.model = new ModelRenderer(model, x, y);
		this.model.setTextureOffset(x, y);
		super.addChild(this.model);
	}

	public JointModelRenderer(ModelBase model, String name) {
		super(model, name);
		this.model = new ModelRenderer(model, name);
		this.model.setTextureOffset(0, 0);
		this.model.setTextureSize(model.textureWidth, model.textureHeight);
		super.addChild(this.model);
	}

	@Override
	public JointModelRenderer setTextureOffset(int x, int y) {
		if (this.model != null) {
			this.model.setTextureOffset(x, y);
		}

		return this;
	}

	@Override
	public JointModelRenderer setTextureSize(int w, int h) {
		if (this.model != null) {
			this.model.setTextureSize(w, h);
		}

		return this;
	}

	@Override
	public JointModelRenderer addBox(float x, float y, float z, int w, int h, int d) {
		this.model.addBox(x, y, z, w, h, d);
		return this;
	}

	@Override
	public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
		this.model.addBox(offX, offY, offZ, width, height, depth, scaleFactor);
	}

	@Override
	public JointModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
		this.model.addBox(offX, offY, offZ, width, height, depth, mirrored);
		return this;
	}

	@Override
	public void addChild(ModelRenderer child) {
		this.model.addChild(child);
	}

	public ModelRenderer getModel() {
		return this.model;
	}
}