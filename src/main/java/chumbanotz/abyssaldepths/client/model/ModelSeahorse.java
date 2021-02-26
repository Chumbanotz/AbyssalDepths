package chumbanotz.abyssaldepths.client.model;

import chumbanotz.abyssaldepths.entity.Seahorse;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelSeahorse extends ModelBase {
	private final ModelRenderer base;
	private final ModelRenderer body;
	private final ModelRenderer neck1;
	private final ModelRenderer neck2;
	private final JointModelRenderer head;
	private final ModelRenderer snout;
	private final ModelRenderer crown;
	private final ModelRenderer butt;
	private final ModelRenderer fin;
	private final ModelRenderer[] tail;

	public ModelSeahorse() {
		this(0.0F);
	}

	public ModelSeahorse(float scale) {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.tail = new ModelRenderer[6];
		this.base = new ModelRenderer(this);
		this.base.setRotationPoint(0.0F, 4.0F, 0.0F);
		this.body = new ModelRenderer(this, 0, 0);
		this.body.addBox(-2.5F, -4.0F, -4.0F, 5, 8, 8);
		this.base.addChild(this.body);
		this.neck1 = new ModelRenderer(this, 0, 16);
		this.neck1.addBox(-2.0F, -6.0F, -6.0F, 4, 6, 6);
		this.neck1.setRotationPoint(0.0F, -4.0F, 3.5F);
		this.body.addChild(this.neck1);
		this.neck2 = new ModelRenderer(this, 0, 28);
		this.neck2.addBox(-1.5F, -8.0F, -4.0F, 3, 8, 4);
		this.neck2.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.neck1.addChild(this.neck2);
		this.head = new JointModelRenderer(this, 0, 40);
		this.head.addBox(-2.0F, -2.5F, -7.0F, 4, 5, 7, scale);
		this.head.setRotationPoint(0.0F, -9.0F, 0.5F);
		this.neck2.addChild(this.head);
		this.snout = new ModelRenderer(this, 0, 52);
		this.snout.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 6);
		this.snout.setRotationPoint(0.0F, 1.0F, -7.0F);
		this.head.addChild(this.snout);
		this.crown = new ModelRenderer(this, 14, 16);
		this.crown.addBox(-1.0F, -2.0F, -1.5F, 2, 2, 3);
		this.crown.setRotationPoint(0.0F, -1.5F, -3.0F);
		this.head.addChild(this.crown);
		this.butt = new ModelRenderer(this, 26, 0);
		this.butt.addBox(-2.0F, 0.0F, -3.0F, 4, 6, 6);
		this.butt.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.body.addChild(this.butt);
		this.fin = new ModelRenderer(this, 24, 12);
		this.fin.addBox(-0.5F, -3.0F, 0.0F, 1, 6, 4, -0.1F);
		this.fin.setRotationPoint(0.0F, 2.5F, 2.5F);
		this.butt.addChild(this.fin);
		this.tail[0] = new ModelRenderer(this, 46, 0);
		this.tail[0].addBox(-1.5F, 0.0F, -2.0F, 3, 10, 4);
		this.tail[0].setRotationPoint(0.0F, 5.0F, 0.5F);
		this.butt.addChild(this.tail[0]);
		this.tail[1] = new ModelRenderer(this, 34, 12);
		this.tail[1].addBox(-1.0F, 0.0F, -3.0F, 2, 8, 3);
		this.tail[1].setRotationPoint(0.0F, 10.0F, 1.5F);
		this.tail[0].addChild(this.tail[1]);
		this.tail[2] = new ModelRenderer(this, 44, 14);
		this.tail[2].addBox(-1.0F, 0.0F, -2.0F, 2, 8, 2, -0.1F);
		this.tail[2].setRotationPoint(0.0F, 8.0F, 0.0F);
		this.tail[1].addChild(this.tail[2]);
		this.tail[3] = new ModelRenderer(this, 52, 14);
		this.tail[3].addBox(-1.0F, 0.0F, -2.0F, 2, 5, 2, -0.2F);
		this.tail[3].setRotationPoint(0.0F, 8.0F, 0.0F);
		this.tail[2].addChild(this.tail[3]);
		this.tail[4] = new ModelRenderer(this, 0, 0);
		this.tail[4].addBox(-0.5F, 0.0F, -1.0F, 1, 3, 1);
		this.tail[4].setRotationPoint(0.0F, 4.8F, -0.5F);
		this.tail[3].addChild(this.tail[4]);
		this.tail[5] = new ModelRenderer(this, 0, 4);
		this.tail[5].addBox(-0.5F, 0.0F, -1.0F, 1, 3, 1, -0.05F);
		this.tail[5].setRotationPoint(0.0F, 3.0F, 0.0F);
		this.tail[4].addChild(this.tail[5]);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.setAngles();
		this.animate((Seahorse)entity, f, f1, f2, f3, f4, f5);
		this.base.render(f5);
	}

	private void resetAngles(ModelRenderer... models) {
		for (ModelRenderer model : models) {
			model.rotateAngleX = 0.0F;
			model.rotateAngleY = 0.0F;
			model.rotateAngleZ = 0.0F;
		}
	}

	public void setAngles() {
		this.resetAngles(this.body, this.neck1, this.neck2, this.head, this.head.getModel(), this.snout, this.crown, this.butt, this.fin);
		this.resetAngles(this.tail);
		this.body.rotateAngleX += -(float)Math.PI / 8.0F;
		this.neck1.rotateAngleX += (float)Math.PI / 16.0F;
		this.neck2.rotateAngleX += (float)Math.PI / 4.0F;
		this.head.rotateAngleX += (float)Math.PI / 24.0F;
		this.crown.rotateAngleX += -(float)Math.PI / 10.0F;
		this.butt.rotateAngleX += (float)Math.PI / 5.0F;
		this.fin.rotateAngleX += -(float)Math.PI / 9.0F;
		this.tail[0].rotateAngleX += (float)Math.PI / 20.0F;
		this.tail[1].rotateAngleX += -(float)Math.PI / 3.5F;
		this.tail[2].rotateAngleX += -(float)Math.PI / 2.5F;
		this.tail[3].rotateAngleX += -(float)Math.PI / 2.3F;
		this.tail[4].rotateAngleX += -(float)Math.PI / 2.5F;
		this.tail[5].rotateAngleX += -(float)Math.PI / 2.3F;
	}

	public void animate(Seahorse horse, float f, float f1, float f2, float f3, float f4, float f5) {
		float breatheAnim = MathHelper.sin(f2 * 0.08F);
		float moveAnim = MathHelper.sin(f * 0.4F) * f1;
		this.neck1.rotateAngleX += breatheAnim * 0.02F;
		this.neck1.rotateAngleX += breatheAnim * 0.02F;
		this.head.rotateAngleX += breatheAnim * 0.02F;

		for (ModelRenderer model : this.tail) {
			model.rotateAngleX += -breatheAnim * 0.06F;
		}

		this.body.rotateAngleX += f1 * 0.2F;
		this.neck1.rotateAngleX += moveAnim * 0.03F;
		this.neck2.rotateAngleX += moveAnim * 0.03F;
		this.head.rotateAngleX += -f1 * 0.25F;
		this.head.rotateAngleX += -moveAnim * 0.06F;

		for (ModelRenderer model : this.tail) {
			model.rotateAngleX += f1 * 0.1F + moveAnim * 0.1F;
		}
	}
}