package chumbanotz.abyssaldepths.client.model;

import chumbanotz.abyssaldepths.entity.billfish.Billfish;
import chumbanotz.abyssaldepths.entity.billfish.Sailfish;
import chumbanotz.abyssaldepths.entity.billfish.Swordfish;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class ModelBillfish extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer mouth;
	private final ModelRenderer sword;
	private final ModelRenderer nose;
	private final ModelRenderer body1;
	private final ModelRenderer body2;
	private final ModelRenderer body3;
	private final ModelRenderer topTail;
	private final ModelRenderer bottomTail;
	private final ModelRenderer fin1;
	private final ModelRenderer fin2;
	private final ModelRenderer fin3;
	private final ModelRenderer dorsalFin;
	private final ModelRenderer sailFin1;
	private final ModelRenderer sailFin2;
	private final ModelRenderer[] anatomy;

	public ModelBillfish() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new JointModelRenderer(this, 0, 23);
		this.head.addBox(-1.5F, 0.0F, -7.0F, 3, 5, 7);
		this.head.setRotationPoint(0.0F, 15.0F, 0.0F);
		this.mouth = new ModelRenderer(this, 18, 23);
		this.mouth.addBox(-1.0F, 0.0F, -6.0F, 2, 1, 6);
		this.mouth.setRotationPoint(0.0F, 4.75F, -0.5F);
		this.head.addChild(this.mouth);
		this.sword = new ModelRenderer(this, 35, 24);
		this.sword.addBox(-0.5F, -14.0F, -0.5F, 1, 14, 1, 0.1F);
		this.sword.setRotationPoint(0.0F, 4.0F, -7.0F);
		this.head.addChild(this.sword);
		this.nose = new ModelRenderer(this, 40, 24);
		this.nose.addBox(-0.5F, -8.0F, -0.5F, 1, 8, 1, 0.1F);
		this.nose.setRotationPoint(0.0F, 4.0F, -7.0F);
		this.head.addChild(this.nose);
		this.body1 = new ModelRenderer(this, 0, 0);
		this.body1.addBox(-2.5F, 0.0F, 0.0F, 5, 9, 14);
		this.body1.setRotationPoint(0.0F, -1.5F, -1.0F);
		this.head.addChild(this.body1);
		this.body2 = new ModelRenderer(this, 38, 0);
		this.body2.addBox(-2.0F, 0.0F, 0.0F, 4, 7, 14);
		this.body2.setRotationPoint(0.0F, 1.0F, 12.0F);
		this.body1.addChild(this.body2);
		this.body3 = new ModelRenderer(this, 74, 0);
		this.body3.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 12);
		this.body3.setRotationPoint(0.0F, 1.0F, 12.0F);
		this.body2.addChild(this.body3);
		this.topTail = new ModelRenderer(this, 91, 0);
		this.topTail.addBox(-0.5F, -1.5F, 0.0F, 1, 3, 8);
		this.topTail.setRotationPoint(0.0F, 2.0F, 9.5F);
		this.body3.addChild(this.topTail);
		this.bottomTail = new ModelRenderer(this, 110, 0);
		this.bottomTail.addBox(-0.5F, -1.5F, 0.0F, 1, 3, 8);
		this.bottomTail.setRotationPoint(0.0F, 2.0F, 9.0F);
		this.body3.addChild(this.bottomTail);
		this.fin1 = new ModelRenderer(this, 0, 35);
		this.fin1.addBox(-0.5F, 0.0F, -1.5F, 1, 6, 3);
		this.fin1.setRotationPoint(-2.0F, 8.0F, 3.0F);
		this.body1.addChild(this.fin1);
		this.fin2 = new ModelRenderer(this, 0, 35);
		this.fin2.mirror = true;
		this.fin2.addBox(-0.5F, 0.0F, -1.5F, 1, 6, 3);
		this.fin2.setRotationPoint(2.0F, 8.0F, 3.0F);
		this.body1.addChild(this.fin2);
		this.fin3 = new ModelRenderer(this, 9, 35);
		this.fin3.addBox(-0.5F, 0.0F, -1.5F, 1, 4, 3);
		this.fin3.setRotationPoint(0.0F, 6.0F, 7.0F);
		this.body2.addChild(this.fin3);
		this.dorsalFin = new ModelRenderer(this, 18, 34);
		this.dorsalFin.addBox(-0.5F, -7.0F, -2.0F, 1, 7, 4);
		this.dorsalFin.setRotationPoint(0.0F, 1.0F, 4.0F);
		this.body1.addChild(this.dorsalFin);
		this.sailFin1 = new ModelRenderer(this, 45, 8);
		this.sailFin1.addBox(0.0F, -12.0F, 0.0F, 0, 12, 14);
		this.sailFin1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body1.addChild(this.sailFin1);
		this.sailFin2 = new ModelRenderer(this, 74, 9);
		this.sailFin2.addBox(0.0F, -13.0F, 0.0F, 0, 13, 12);
		this.sailFin2.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.body2.addChild(this.sailFin2);
		this.anatomy = new ModelRenderer[] {this.head, this.body1, this.body2, this.body3};
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.sword.showModel = entity instanceof Swordfish;
		this.nose.showModel = entity instanceof Sailfish;
		this.dorsalFin.showModel = entity instanceof Swordfish;
		this.sailFin1.showModel = this.sailFin2.showModel = entity instanceof Sailfish;
		this.setAngles();
		this.animate((Billfish)entity, f, f1, f2, f3, f4, f5);
		this.head.render(f5);
	}

	public void resetAngles(ModelRenderer... models) {
		for (ModelRenderer model : models) {
			model.rotateAngleX = 0.0F;
			model.rotateAngleY = 0.0F;
			model.rotateAngleZ = 0.0F;
		}
	}

	public void setAngles() {
		this.resetAngles(this.head, this.mouth, this.sword, this.nose, this.body1, this.body2, this.body3);
		this.resetAngles(this.topTail, this.bottomTail, this.fin1, this.fin2, this.fin3);
		this.resetAngles(this.dorsalFin, this.sailFin1, this.sailFin2);
		this.mouth.rotateAngleX += 0.07853982F;
		this.sword.rotateAngleX += 1.5707963268F;
		this.nose.rotateAngleX += 1.5707963268F;
		this.topTail.rotateAngleX += 0.9666439F;
		this.bottomTail.rotateAngleX += -0.8975979F;
		this.fin1.rotateAngleX += 0.2617994F;
		this.fin1.rotateAngleZ += 0.5235988F;
		this.fin2.rotateAngleX += 0.2617994F;
		this.fin2.rotateAngleZ += -0.5235988F;
		this.fin3.rotateAngleX += 0.3926991F;
		this.dorsalFin.rotateAngleX += -0.3926991F;
	}

	public void animate(Billfish billfish, float f, float f1, float f2, float f3, float f4, float f5) {
		float breatheAnim = MathHelper.sin(f2 * 0.12F);

		for (int i = 0; i < billfish.getBoneList().length; ++i) {
			this.anatomy[i].rotateAngleX += billfish.getBoneList()[i].getRotation().x * 0.017453292F;
			this.anatomy[i].rotateAngleY += billfish.getBoneList()[i].getRotation().y * 0.017453292F;
		}

		this.mouth.rotateAngleX += breatheAnim * 0.03F + (MathHelper.sin(this.swingProgress * (float)Math.PI) * 0.2F);
		this.fin1.rotateAngleZ += breatheAnim * 0.05F;
		this.fin2.rotateAngleZ += -breatheAnim * 0.05F;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
		Billfish billfish = (Billfish)entitylivingbaseIn;
		billfish.resetBoneAngles();
		billfish.updatePitchRotations(partialTickTime);
		billfish.updateYawRotations(partialTickTime);
	}
}