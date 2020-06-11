package chumbanotz.abyssaldepths.client.model;

import chumbanotz.abyssaldepths.entity.fish.CommonFish;
import chumbanotz.abyssaldepths.entity.fish.Fish;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelFish extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final JointModelRenderer topTail;
	private final ModelRenderer bottomTail;
	private final ModelRenderer straightTail;
	private final ModelRenderer topFin;
	private final ModelRenderer leftFin;
	private final ModelRenderer rightFin;

	public ModelFish() {
		this.body = new ModelRenderer(this, 3, 0);
		this.body.addBox(-1.0F, -5.0F, -4.0F, 2, 5, 9);
		this.body.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.addBox(-1.0F, -5.0F, -4.0F, 2, 5, 4, -0.1F);
		this.head.setRotationPoint(0.0F, 0.0F, -3.75F);
		this.body.addChild(this.head);
		this.topTail = new JointModelRenderer(this, 0, 14);
		this.topTail.addBox(-0.5F, -1.5F, 0.0F, 1, 3, 5);
		this.topTail.setRotationPoint(0.0F, -2.5F, 4.5F);
		this.body.addChild(this.topTail);
		this.bottomTail = new ModelRenderer(this, 0, 22);
		this.bottomTail.addBox(-0.5F, -1.5F, 0.0F, 1, 3, 5, -0.01F);
		this.topTail.addChild(this.bottomTail);
		this.straightTail = new ModelRenderer(this, 12, 22);
		this.straightTail.addBox(-0.5F, -1.5F, 0.0F, 1, 3, 5);
		this.straightTail.setRotationPoint(0.0F, -2.5F, 4.5F);
		this.body.addChild(this.straightTail);
		this.topFin = new ModelRenderer(this, 12, 14);
		this.topFin.addBox(-0.5F, -1.0F, -4.0F, 1, 1, 7);
		this.topFin.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.body.addChild(this.topFin);
		this.leftFin = new ModelRenderer(this, 16, 0);
		this.leftFin.addBox(-1.0F, -1.5F, -1.0F, 2, 3, 4, -0.5F);
		this.leftFin.setRotationPoint(-1.25F, -1.5F, -2.5F);
		this.body.addChild(this.leftFin);
		this.rightFin = new ModelRenderer(this, 28, 0);
		this.rightFin.addBox(-1.0F, -1.5F, -1.0F, 2, 3, 4, -0.5F);
		this.rightFin.setRotationPoint(1.25F, -1.5F, -2.5F);
		this.body.addChild(this.rightFin);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
//		this.topFin.showModel = fish.showTopFin;
//		this.leftFin.showModel = this.rightFin.showModel = fish.showFins;
		this.topTail.showModel = entity instanceof CommonFish;
		this.straightTail.showModel = !this.topTail.showModel;
		this.setAngles();
		this.animate((Fish)entity, f, f1, f2, f3, f4, f5);
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
		this.resetAngles(this.head, this.body, this.topTail, this.topTail.getJoint(), this.bottomTail, this.straightTail, this.topFin, this.leftFin, this.rightFin);
		this.topTail.getJoint().rotateAngleX = 0.62831855F;
		this.bottomTail.rotateAngleX = -1.2566371F;
		this.leftFin.rotateAngleX = -0.15707964F;
		this.leftFin.rotateAngleY = -0.19634955F;
		this.rightFin.rotateAngleX = -0.15707964F;
		this.rightFin.rotateAngleY = 0.19634955F;
	}

	public void animate(Fish fish, float f, float f1, float f2, float f3, float f4, float f5) {
		float breatheAnim = MathHelper.sin(f2 * 0.15F);
		float breatheAnim1 = MathHelper.sin(f2 * 0.2F);
		float walkAnim = MathHelper.sin(f * 1.2F) * f1;
		this.topTail.rotateAngleY += breatheAnim1 * (float)Math.PI / 24.0F;
		this.straightTail.rotateAngleY += breatheAnim1 * (float)Math.PI / 24.0F;
		this.leftFin.rotateAngleY += breatheAnim * (float)Math.PI / 20.0F;
		this.rightFin.rotateAngleY += -breatheAnim * (float)Math.PI / 20.0F;
		this.head.rotateAngleY += -walkAnim * (float)Math.PI / 20.0F;
		this.topTail.rotateAngleY += walkAnim * (float)Math.PI / 6.0F;
		this.straightTail.rotateAngleY += walkAnim * (float)Math.PI / 6.0F;
	}
}