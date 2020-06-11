package chumbanotz.abyssaldepths.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Euler {
	public float x;
	public float y;
	public float z;

	public Euler() {
		this.setAngles(0.0F, 0.0F, 0.0F);
	}

	public Euler(float x, float y, float z) {
		this.setAngles(x, y, z);
	}

	public Euler(Euler angle) {
		this.setAngles(angle);
	}

	public Euler(Vec3d vec) {
		this.setAngles(vec);
	}

	public Euler clone() {
		return new Euler(this);
	}

	public void setAngles(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setAngles(Euler angle) {
		this.x = angle.x;
		this.y = angle.y;
		this.z = angle.z;
	}

	public void setAngles(Vec3d vec) {
		this.x = (float)Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z));
		this.y = (float)Math.atan2(vec.x, vec.z);
		this.z = 0.0F;
	}

	public void rotate(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void rotate(Euler angle) {
		this.x += angle.x;
		this.y += angle.y;
		this.z += angle.z;
	}

	public Vec3d rotateVector(float length) {
		if (length == 0.0F) {
			return Vec3d.ZERO;
		} else {
			Euler rad = this.toRad();
			float yawOffset = 1.5707964F;
			float xSin = MathHelper.sin(rad.x);
			float xCos = MathHelper.cos(rad.x);
			float ySin = MathHelper.sin(rad.y + yawOffset);
			float yCos = MathHelper.cos(rad.y + yawOffset);
			float zSin = MathHelper.sin(rad.z);
			float zCos = MathHelper.cos(rad.z);
			double xOffset = (double)(xCos * yCos);
			double yOffset = (double)(xCos * ySin * zSin - zCos * xSin);
			double zOffset = (double)(xSin * zSin + xCos * zCos * ySin);
			return new Vec3d(xOffset * (double)length, yOffset * (double)length, zOffset * (double)length);
		}
	}

	public Euler getRotated(float x, float y, float z) {
		Euler angle = new Euler(this);
		angle.rotate(x, y, z);
		return angle;
	}

	public Euler getRotated(Euler angle) {
		Euler newAngle = new Euler(this);
		newAngle.rotate(angle);
		return newAngle;
	}

	public Euler toRad() {
		float conv = 0.017453292F;
		return new Euler(this.x * conv, this.y * conv, this.z * conv);
	}

	public Euler toDegrees() {
		float conv = 57.295776F;
		return new Euler(this.x * conv, this.y * conv, this.z * conv);
	}
}