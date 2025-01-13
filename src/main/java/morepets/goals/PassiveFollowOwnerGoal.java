package morepets.goals;

import java.util.UUID;

import morepets.MorePets;
import morepets.interfaces.ITameable;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PassiveFollowOwnerGoal extends Goal {
    private final MobEntity entity;
    private final double speed;
    private final float minDistance;
    private final float maxDistance;
    private UUID ownerUuid;
    private PlayerEntity owner;

    public PassiveFollowOwnerGoal(MobEntity entity, double speed, float minDistance, float maxDistance) {
        this.entity = entity;
        this.speed = speed;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean canStart() {
        if (!(entity instanceof ITameable mobt))
            return false;
        if (!(entity instanceof MobEntity mobe))
            return false;
        if (mobe.isLeashed())
            return false;
        this.ownerUuid = mobt.getOwner();
        if (this.ownerUuid == null)
            return false;
        this.owner = mobe.getWorld().getPlayerByUuid(this.ownerUuid);
        if (this.owner == null)
            return false;
        if (!this.owner.isOnGround())
            return false;
        return this.entity.squaredDistanceTo(this.owner) > (double) (this.minDistance * this.minDistance);
    }

    @Override
    public boolean shouldContinue() {
        return this.entity.squaredDistanceTo(this.owner) > (double) (this.minDistance * this.minDistance);
    }

    @Override
    public void start() {
        this.entity.getNavigation().startMovingTo(this.owner, this.speed);
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.entity.squaredDistanceTo(this.owner) > (double) this.maxDistance * this.maxDistance) {
            if (this.owner.isOnGround()) {
                // Teleport to the owner if too far away
                this.entity.refreshPositionAndAngles(this.owner.getX(), this.owner.getY(), this.owner.getZ(),
                        this.entity.getYaw(), this.entity.getPitch());
            }
        } else {
            this.entity.getNavigation().startMovingTo(this.owner, this.speed);
        }
    }
}