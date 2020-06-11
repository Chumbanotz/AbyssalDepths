package chumbanotz.abyssaldepths.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class School {
	private int maxSchoolSize = 10;
	private float radius = 8.0F;
	private boolean openToCombine;
	private WaterCreature leader;
	private final World world;
	private final List<WaterCreature> creatureList = new ArrayList<>();

	public School(WaterCreature creature) {
		this.openToCombine = creature.world.rand.nextInt(4) == 0;
		this.world = creature.world;
		this.setLeader(creature);
	}

	public void setLeader(WaterCreature creature) {
		this.leader = creature;
		if (!this.creatureList.contains(this.leader)) {
			this.addCreature(this.leader);
		}
	}

	public void chooseRandomLeader() {
		this.setLeader(this.creatureList.get(this.world.rand.nextInt(this.creatureList.size())));
	}

	public WaterCreature getLeader() {
		return this.leader;
	}

	public void addCreature(WaterCreature creature) {
		if (!this.creatureList.contains(creature)) {
			this.creatureList.add(creature);
		}
	}

	public boolean ridinSolo() {
		return this.creatureList.size() == 1;
	}

	public boolean containsCreature(WaterCreature creature) {
		return this.creatureList.contains(creature);
	}

	public void removeCreature(WaterCreature creature) {
		this.creatureList.remove(creature);
		if (this.creatureList.size() > 1 && this.getLeader() == creature) {
			this.chooseRandomLeader();
		}

		creature.initSchool();
		creature.school.setOpenToCombine(false);
		creature.followEntity = null;
		creature.targetVec = null;
	}

	public void setMaxSize(int maxSchoolSize) {
		this.maxSchoolSize = maxSchoolSize;
	}

	public int getMaxSize() {
		return this.maxSchoolSize;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return this.radius;
	}

	public void setOpenToCombine(boolean openToCombine) {
		this.openToCombine = openToCombine;
	}

	public boolean getOpenToCombine() {
		return this.openToCombine;
	}

	public void updateSchool() {
		boolean combine = this.getOpenToCombine();
		if (!combine && this.world.rand.nextInt(1500) == 0) {
			this.setOpenToCombine(true);
		} else if (combine && this.world.rand.nextInt(1800) == 0) {
			this.setOpenToCombine(false);
		}

		if (this.getOpenToCombine()) {
			List<WaterCreature> list = this.world.getEntitiesWithinAABB(WaterCreature.class, this.getLeader().getEntityBoundingBox().grow(16.0D, 12.0D, 16.0D));

			for (int i = 0; i < list.size(); ++i) {
				WaterCreature creature = (WaterCreature)list.get(i);
				if (!this.containsCreature(creature) && creature.school != null && creature.canCombineWith(this)) {
					int netSize = this.creatureList.size() + creature.school.creatureList.size();
					if (creature.school.getOpenToCombine() && creature.getClass().equals(this.getLeader().getClass()) && netSize <= this.getMaxSize() && netSize <= creature.school.getMaxSize()) {
						combineSchools(this, creature.school);
					}
				}
			}
		}

		boolean nearLeader = false;
		List<WaterCreature> leaderList = this.world.getEntitiesWithinAABB(WaterCreature.class, this.leader.getEntityBoundingBox().grow(3.0D));

		int i;
		WaterCreature creature;
		for (i = 0; i < leaderList.size(); ++i) {
			creature = (WaterCreature)leaderList.get(i);
			if (creature != this.leader && this.containsCreature(creature)) {
				nearLeader = true;
				break;
			}
		}

		for (i = 0; i < this.creatureList.size(); ++i) {
			creature = (WaterCreature)this.creatureList.get(i);
			if (creature.isEntityAlive() && creature.isInWater() && creature.getDistanceSq(this.leader) <= 1024.0D && creature.getRidingEntity() == null) {
				if (creature != this.leader) {
					if (creature.getDistanceSq(this.leader) <= (double)(this.radius * this.radius) && nearLeader) {
						List<WaterCreature> list = this.world.getEntitiesWithinAABB(WaterCreature.class, creature.getEntityBoundingBox().grow(16.0D));
						WaterCreature closestEntity = null;
						double distSq = 1024.0D;
						boolean hasNearby = false;

						double dy;
						for (int j = 0; j < list.size(); ++j) {
							WaterCreature thing = (WaterCreature)list.get(j);
							if (creature != thing && this.containsCreature(thing)) {
								dy = creature.getDistanceSq(thing);
								float w = (creature.width + thing.width) * 0.5F + 1.0F;
								if (dy < (double)(w * w)) {
									hasNearby = true;
									break;
								}

								if (dy < distSq * distSq) {
									closestEntity = thing;
									distSq = dy;
								}
							}
						}

						if (!hasNearby && closestEntity != null) {
							creature.followEntity = closestEntity;
							creature.targetVec = null;
						} else {
							creature.followEntity = null;
							if (this.leader.targetVec != null) {
								if (creature.targetVec == null || this.world.rand.nextInt(10) == 0) {
									double dx = this.leader.targetVec.x - this.leader.posX + (double)((this.world.rand.nextFloat() - 0.5F) * 1.1F);
									dy = this.leader.targetVec.y - this.leader.posY + (double)((this.world.rand.nextFloat() - 0.5F) * 1.2F);
									double dz = this.leader.targetVec.z - this.leader.posZ + (double)((this.world.rand.nextFloat() - 0.5F) * 1.1F);
									creature.targetVec = new Vec3d(creature.posX + dx, creature.posY + dy, creature.posZ + dz);
								}
							} else {
								creature.targetVec = null;
								Vec3d vec = this.leader.getLookVec();
								creature.getLookHelper().setLookPosition(creature.posX + vec.x, creature.posY + vec.y, creature.posZ + vec.z, 6.0F, 85.0F);
							}
						}
					} else {
						creature.followEntity = this.leader;
						creature.targetVec = null;
					}
				}
			} else {
				this.removeCreature(creature);
				--i;
			}
		}
	}

	public static School combineSchools(School school1, School school2) {
		if (school2.creatureList.size() > school1.creatureList.size()) {
			school1.setLeader(school2.getLeader());
		}

		if (school2.getMaxSize() < school1.getMaxSize()) {
			school1.setMaxSize(school2.getMaxSize());
		}

		if (school2.getRadius() < school1.getRadius()) {
			school1.setRadius(school2.getRadius());
		}

		school1.creatureList.addAll(school2.creatureList);

		WaterCreature creature;
		for (Iterator<WaterCreature> i$ = school2.creatureList.iterator(); i$.hasNext(); creature.school = school1) {
			creature = (WaterCreature)i$.next();
		}

		return school1;
	}
}