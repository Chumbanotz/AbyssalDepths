package chumbanotz.abyssaldepths.client.model;

import chumbanotz.abyssaldepths.entity.fish.Butterflyfish;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelButterflyfish extends ModelBase {
	private final ModelRenderer body = new ModelRenderer(this, 0, 12);
	private final ModelRenderer head;
	private final JointModelRenderer topTail;
	private final ModelRenderer bottomTail;
	private final ModelRenderer topFin;
	private final ModelRenderer topFinS;
	private final ModelRenderer bottomFin;
	private final ModelRenderer leftFin;
	private final ModelRenderer rightFin;
	private final ModelRenderer bottomFin1;
	private final ModelRenderer bottomFin2;
	private final ModelRenderer longFin1;
	private final ModelRenderer longFin2;

	public ModelButterflyfish() {
		this.body.addBox(-1.0F, -4.0F, -3.5F, 2, 8, 7);
		this.body.setRotationPoint(0.0F, 19.5F, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.addBox(-1.0F, -3.0F, -3.0F, 2, 6, 6, -0.1F);
		this.head.setRotationPoint(0.0F, 0.0F, -3.5F);
		this.body.addChild(this.head);
		this.topTail = new JointModelRenderer(this, 10, 0);
		this.topTail.addBox(-0.5F, -1.0F, -1.0F, 1, 2, 4, -0.1F);
		this.topTail.setRotationPoint(0.0F, 0.25F, 3.5F);
		this.body.addChild(this.topTail);
		this.bottomTail = new ModelRenderer(this, 12, 8);
		this.bottomTail.addBox(-0.5F, -1.0F, -1.0F, 1, 2, 4, -0.11F);
		this.bottomTail.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.topTail.addChild(this.bottomTail);
		this.leftFin = new ModelRenderer(this, 20, 0);
		this.leftFin.addBox(-1.0F, -1.5F, -1.0F, 2, 3, 4, -0.5F);
		this.leftFin.setRotationPoint(-1.25F, 0.5F, -3.5F);
		this.body.addChild(this.leftFin);
		this.rightFin = new ModelRenderer(this, 20, 0);
		this.rightFin.mirror = true;
		this.rightFin.addBox(-1.0F, -1.5F, -1.0F, 2, 3, 4, -0.5F);
		this.rightFin.setRotationPoint(1.25F, 0.5F, -3.5F);
		this.body.addChild(this.rightFin);
		this.topFin = new ModelRenderer(this, 25, 0);
		this.topFin.addBox(-0.5F, -4.0F, -3.5F, 1, 4, 7);
		this.topFin.setRotationPoint(0.0F, -1.25F, 1.5F);
		this.body.addChild(this.topFin);
		this.topFinS = new ModelRenderer(this, 26, 1);
		this.topFinS.addBox(-0.5F, -3.0F, -3.0F, 1, 3, 6);
		this.topFinS.setRotationPoint(0.0F, -1.5F, 2.0F);
		this.body.addChild(this.topFinS);
		this.bottomFin = new ModelRenderer(this, 17, 9);
		this.bottomFin.addBox(-0.5F, 0.0F, -2.5F, 1, 3, 5);
		this.bottomFin.setRotationPoint(0.0F, 1.75F, 2.0F);
		this.body.addChild(this.bottomFin);
		this.bottomFin1 = new ModelRenderer(this, 18, 17);
		this.bottomFin1.addBox(-1.0F, 0.0F, -1.5F, 2, 5, 3, -0.75F);
		this.bottomFin1.setRotationPoint(-0.5F, 2.75F, -3.0F);
		this.body.addChild(this.bottomFin1);
		this.bottomFin2 = new ModelRenderer(this, 18, 17);
		this.bottomFin2.mirror = true;
		this.bottomFin2.addBox(-1.0F, 0.0F, -1.5F, 2, 5, 3, -0.75F);
		this.bottomFin2.setRotationPoint(0.5F, 2.75F, -3.0F);
		this.body.addChild(this.bottomFin2);
		this.longFin1 = new ModelRenderer(this, 34, 0);
		this.longFin1.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1, 0.1F);
		this.longFin1.setRotationPoint(0.0F, -3.5F, -3.0F);
		this.body.addChild(this.longFin1);
		this.longFin2 = new ModelRenderer(this, 29, 11);
		this.longFin2.addBox(-0.5F, -8.0F, -0.5F, 1, 8, 1);
		this.longFin2.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.longFin1.addChild(this.longFin2);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Butterflyfish fish = (Butterflyfish)entity;
//		this.leftFin.showModel = this.rightFin.showModel = fish.showFins;
//		this.bottomFin1.showModel = this.bottomFin2.showModel = fish.showBottomFins;
		this.topFin.showModel = !(fish instanceof Butterflyfish.Banner);
		this.topFinS.showModel = this.longFin1.showModel = !this.topFin.showModel;
		this.setAngles();
		this.animate((Butterflyfish)entity, f, f1, f2, f3, f4, f5);
		this.body.render(f5);
	}

	public void resetAngles(ModelRenderer... models) {
		for (ModelRenderer model : models) {
			model.rotateAngleX = 0.0F;
			model.rotateAngleY = 0.0F;
			model.rotateAngleZ = 0.0F;
		}
	}

	public void setAngles() {
		this.resetAngles(this.body, this.head, this.leftFin, this.rightFin, this.topTail, this.topTail.getModel(), this.bottomTail);
		this.resetAngles(this.bottomFin1, this.bottomFin2, this.longFin1, this.longFin2);
		this.head.rotateAngleX = 0.7853982F;
		this.leftFin.rotateAngleX += -0.19634955F;
		this.leftFin.rotateAngleY += -0.5235988F;
		this.rightFin.rotateAngleX += -0.19634955F;
		this.rightFin.rotateAngleY += 0.5235988F;
		this.topTail.getModel().rotateAngleX = 0.3926991F;
		this.bottomTail.rotateAngleX = -0.7853982F;
		this.bottomFin1.rotateAngleX += 0.62831855F;
		this.bottomFin1.rotateAngleZ += 0.20943952F;
		this.bottomFin2.rotateAngleX += 0.62831855F;
		this.bottomFin2.rotateAngleZ += -0.20943952F;
		this.longFin1.rotateAngleX += -1.1423974F;
		this.longFin2.rotateAngleX += -0.31415927F;
	}

	public void animate(Butterflyfish fish, float f, float f1, float f2, float f3, float f4, float f5) {
		float breatheAnim = MathHelper.sin(f2 * 0.15F);
		float breatheAnim1 = MathHelper.sin(f2 * 0.12F);
		float walkAnim = MathHelper.sin(f * 1.1F) * f1;
		this.topTail.rotateAngleY += breatheAnim1 * (float)Math.PI / 24.0F;
		this.leftFin.rotateAngleY += breatheAnim * (float)Math.PI / 20.0F;
		this.rightFin.rotateAngleY += -breatheAnim * (float)Math.PI / 20.0F;
		this.bottomFin1.rotateAngleZ += breatheAnim1 * (float)Math.PI / 30.0F;
		this.bottomFin2.rotateAngleZ += -breatheAnim1 * (float)Math.PI / 30.0F;
		this.topTail.rotateAngleY += walkAnim * (float)Math.PI / 6.0F;
	}
}