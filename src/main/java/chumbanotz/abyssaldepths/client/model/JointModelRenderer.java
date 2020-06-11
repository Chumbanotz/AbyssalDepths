package chumbanotz.abyssaldepths.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class JointModelRenderer extends ModelRenderer {
	private ModelRenderer joint;

	public JointModelRenderer(ModelBase model) {
		this(model, null);
		super.addChild(this.joint = new ModelRenderer(model));
	}

	public JointModelRenderer(ModelBase model, int x, int y) {
		this(model);
		this.joint = new ModelRenderer(model, x, y);
		this.joint.setTextureOffset(x, y);
		super.addChild(this.joint);
	}

	public JointModelRenderer(ModelBase model, String name) {
		super(model, name);
		this.joint = new ModelRenderer(model, name);
		this.joint.setTextureOffset(0, 0);
		this.joint.setTextureSize(model.textureWidth, model.textureHeight);
		super.addChild(this.joint);
	}

	@Override
	public JointModelRenderer setTextureOffset(int x, int y) {
		if (this.joint != null) {
			this.joint.setTextureOffset(x, y);
		}

		return this;
	}

	@Override
	public JointModelRenderer setTextureSize(int w, int h) {
		if (this.joint != null) {
			this.joint.setTextureSize(w, h);
		}

		return this;
	}

	@Override
	public JointModelRenderer addBox(float x, float y, float z, int w, int h, int d) {
		this.joint.addBox(x, y, z, w, h, d);
		return this;
	}

	@Override
	public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
		this.joint.addBox(offX, offY, offZ, width, height, depth, scaleFactor);
	}

	@Override
	public JointModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
		this.joint.addBox(offX, offY, offZ, width, height, depth, mirrored);
		return this;
	}

	@Override
	public void addChild(ModelRenderer child) {
		this.joint.addChild(child);
	}

	public ModelRenderer getJoint() {
		return this.joint;
	}
}