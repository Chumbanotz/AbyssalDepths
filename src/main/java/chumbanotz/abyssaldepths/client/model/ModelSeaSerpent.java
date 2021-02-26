package chumbanotz.abyssaldepths.client.model;

import static chumbanotz.abyssaldepths.entity.SeaSerpent.PART_HEIGHT;
import static chumbanotz.abyssaldepths.entity.SeaSerpent.PART_LENGTH;

import chumbanotz.abyssaldepths.entity.SeaSerpent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class ModelSeaSerpent extends ModelBase {
	private static final float[] TOP_FIN_Y = new float[] {-6.0F, -4.0F, -1.0F, -1.0F, 1.5F, 2.5F};
	private static final float[] TOP_FIN_Z = new float[] {13.0F, 13.0F, 10.0F, 8.0F, 8.0F, 8.0F};
	private final JointModelRenderer head;
	private final JointModelRenderer mouth;
	private final JointModelRenderer[] body;
	private final JointModelRenderer[] tail;
	private final ModelRenderer fin1;
	private final ModelRenderer fin2;
	private final ModelRenderer fin3;
	private final ModelRenderer fin4;
	private final ModelRenderer[][] tailFin;
	private final ModelRenderer[] topFin;
	private final JointModelRenderer[] anatomy;

	public ModelSeaSerpent() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.body = new JointModelRenderer[4];
		this.tail = new JointModelRenderer[3];
		this.tailFin = new ModelRenderer[2][3];
		this.topFin = new ModelRenderer[6];
		this.head = new JointModelRenderer(this, 0, 0);
		this.head.addBox(-5.0F, -4.0F, -18.0F, 10, PART_HEIGHT[0], PART_LENGTH[0]);
		this.head.setRotationPoint(0.0F, 17.0F, 0.0F);
		this.mouth = new JointModelRenderer(this, 38, 0);
		this.mouth.addBox(-4.0F, -2.0F, -16.0F, 8, 2, 16);
		this.mouth.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.head.addChild(this.mouth);
		this.body[0] = new JointModelRenderer(this, 0, 26);
		this.body[0].addBox(-8.0F, -7.0F, 0.0F, 16, PART_HEIGHT[1], PART_LENGTH[1]);
		this.body[0].setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head.addChild(this.body[0]);
		this.body[1] = new JointModelRenderer(this, 0, 64);
		this.body[1].addBox(-6.0F, -5.0F, 0.0F, 12, PART_HEIGHT[2], PART_LENGTH[2]);
		this.body[1].setRotationPoint(0.0F, 0.0F, 23.0F);
		this.body[0].addChild(this.body[1]);
		this.body[2] = new JointModelRenderer(this, 56, 18);
		this.body[2].addBox(-5.0F, -3.0F, 0.0F, 10, PART_HEIGHT[3], PART_LENGTH[3]);
		this.body[2].setRotationPoint(0.0F, 0.0F, 23.0F);
		this.body[1].addChild(this.body[2]);
		this.body[3] = new JointModelRenderer(this, 66, 50);
		this.body[3].addBox(-4.0F, -1.0F, 0.0F, 8, PART_HEIGHT[4], PART_LENGTH[4]);
		this.body[3].setRotationPoint(0.0F, 0.0F, 19.0F);
		this.body[2].addChild(this.body[3]);
		this.tail[0] = new JointModelRenderer(this, 30, 100);
		this.tail[0].addBox(-3.0F, 1.0F, 0.0F, 6, PART_HEIGHT[5], PART_LENGTH[5]);
		this.tail[0].setRotationPoint(0.0F, 0.0F, 13.0F);
		this.body[3].addChild(this.tail[0]);
		this.tail[1] = new JointModelRenderer(this, 58, 104);
		this.tail[1].addBox(-2.5F, 2.0F, 0.0F, 5, PART_HEIGHT[6], PART_LENGTH[6]);
		this.tail[1].setRotationPoint(0.0F, 0.0F, 15.0F);
		this.tail[0].addChild(this.tail[1]);
		this.tail[2] = new JointModelRenderer(this, 0, 100);
		this.tail[2].addBox(-2.0F, 3.0F, 0.0F, 4, PART_HEIGHT[7], PART_LENGTH[7]);
		this.tail[2].setRotationPoint(0.0F, 0.0F, 17.0F);
		this.tail[1].addChild(this.tail[2]);
		this.fin1 = new ModelRenderer(this, 96, 0);
		this.fin1.addBox(-1.0F, 0.0F, -6.0F, 2, 16, 12);
		this.fin1.setRotationPoint(-5.0F, 2.0F, 14.0F);
		this.body[0].addChild(this.fin1);
		this.fin2 = new ModelRenderer(this, 96, 0);
		this.fin2.mirror = true;
		this.fin2.addBox(-1.0F, 0.0F, -6.0F, 2, 16, 12);
		this.fin2.setRotationPoint(5.0F, 2.0F, 14.0F);
		this.body[0].addChild(this.fin2);
		this.fin3 = new ModelRenderer(this, 0, 26);
		this.fin3.addBox(-0.5F, 0.0F, -4.0F, 1, 10, 8);
		this.fin3.setRotationPoint(-2.0F, 4.0F, 8.0F);
		this.body[3].addChild(this.fin3);
		this.fin4 = new ModelRenderer(this, 0, 26);
		this.fin4.mirror = true;
		this.fin4.addBox(-0.5F, 0.0F, -4.0F, 1, 10, 8);
		this.fin4.setRotationPoint(2.0F, 4.0F, 8.0F);
		this.body[3].addChild(this.fin4);

		for (int i = 0; i < this.tailFin.length; ++i) {
			for (int j = 0; j < this.tailFin[i].length; ++j) {
				int k = j * 2;
				int l = 22 - k;
				boolean flag = i == 0;
				this.tailFin[i][j] = new ModelRenderer(this, 48 + k, 72);
				if (!flag) {
					this.tailFin[i][j].mirror = true;
				}

				this.tailFin[i][j].addBox(flag ? (float)(-l) : 0.0F, 4.0F, -2.0F, l, 2, 4);
				this.tailFin[i][j].setRotationPoint(0.0F, 0.0F, (float)j * 7.0F + 3.0F);
				this.tail[2].addChild(this.tailFin[i][j]);
			}
		}

		for (int i = 0; i < this.topFin.length; ++i) {
			this.topFin[i] = new ModelRenderer(this, 72, 78);
			if (i < 3) {
				this.topFin[i].addBox(-0.5F, -8.0F, -4.0F, 1, 16, 8);
			} else {
				this.topFin[i].setTextureOffset(0, 0).addBox(-0.5F, -4.0F, -2.0F, 1, 8, 4);
			}

			this.topFin[i].setRotationPoint(0.0F, TOP_FIN_Y[i], TOP_FIN_Z[i]);
		}

		for (int i = 0; i < this.body.length; ++i) {
			this.body[i].addChild(this.topFin[i]);
		}

		for (int i = 0; i < this.tail.length - 1; ++i) {
			this.tail[i].addChild(this.topFin[this.body.length + i]);
		}

		this.anatomy = new JointModelRenderer[] {this.head, this.body[0], this.body[1], this.body[2], this.body[3], this.tail[0], this.tail[1], this.tail[2]};
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.setAngles();
		this.animate((SeaSerpent)entity, f, f1, f2, f3, f4, f5);
		this.head.render(f5);
	}

	public void setAngles() {
		for (JointModelRenderer model : this.anatomy) {
			resetAngles(model);
			resetAngles(model.getModel());
		}

		setRotation(this.mouth, 0.02617994F, 0.0F, 0.0F);
		setRotation(this.fin1, 0.62831855F, -0.15707964F, 1.0471976F);
		setRotation(this.fin2, 0.62831855F, 0.15707964F, -1.0471976F);
		setRotationToModel(this.fin3, this.fin1);
		setRotationToModel(this.fin4, this.fin2);
		setRotation(this.tailFin[0][0], 0.0F, 0.7853982F, 0.0F);
		setRotation(this.tailFin[1][0], 0.0F, -0.7853982F, 0.0F);

		for (int i = 0; i < this.tailFin[0].length; ++i) {
			setRotation(this.tailFin[0][i], 0.0F, 0.7853982F, 0.0F);
			setRotation(this.tailFin[1][i], 0.0F, -0.7853982F, 0.0F);
		}

		for (ModelRenderer model : this.topFin) {
			setRotation(model, -1.2566371F, 0.0F, 0.0F);
		}
	}

	public void animate(SeaSerpent serpent, float f, float f1, float f2, float f3, float f4, float f5) {
		float breatheAnim = MathHelper.sin(f2 * 0.1F);

		for (int i = 0; i < serpent.getBoneList().length; ++i) {
			this.anatomy[i].getModel().rotateAngleX += serpent.getBoneList()[i].getRotation().x * 0.017453292F;
			this.anatomy[i].getModel().rotateAngleY += serpent.getBoneList()[i].getRotation().y * 0.017453292F;
		}

		this.mouth.rotateAngleX += Math.max(0.0F, breatheAnim) * 0.05F + (MathHelper.sin(this.swingProgress * (float)Math.PI) * 0.4F);
		this.fin1.rotateAngleZ += breatheAnim * 0.1F;
		this.fin2.rotateAngleZ -= breatheAnim * 0.1F;
		this.fin3.rotateAngleZ -= breatheAnim * 0.2F;
		this.fin4.rotateAngleZ += breatheAnim * 0.2F;

		for (int i = 0; i < 2; ++i) {
			this.tailFin[i][0].rotateAngleZ += this.tail[2].rotateAngleX * 0.4F;
			this.tailFin[i][1].rotateAngleZ -= this.tail[2].rotateAngleX * 0.4F;
			this.tailFin[i][2].rotateAngleZ += this.tail[2].rotateAngleX * 0.4F;
		}
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
		SeaSerpent serpent = (SeaSerpent)entitylivingbaseIn;
		serpent.resetBoneAngles();
		serpent.updatePitchRotations(partialTickTime);
		serpent.updateYawRotations(partialTickTime);
	}

	private static void resetAngles(ModelRenderer model) {
		setRotation(model, 0.0F, 0.0F, 0.0F);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	private static void setRotationToModel(ModelRenderer model, ModelRenderer model1) {
		model.rotateAngleX = model1.rotateAngleX;
		model.rotateAngleY = model1.rotateAngleY;
		model.rotateAngleZ = model1.rotateAngleZ;
	}
}