package morepets.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import morepets.MorePets;
import morepets.goals.PassiveFollowOwnerGoal;
import morepets.interfaces.ICustomNbt;
import morepets.interfaces.IPetList;
import morepets.interfaces.ITameable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(CowEntity.class)
public abstract class CowFollowMixin extends MobEntity implements ITameable, ICustomNbt {
	private UUID ownerUuid;

	public CowFollowMixin(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initGoals", at = @At("TAIL"))
	private void goals(CallbackInfo ci) {
		PassiveFollowOwnerGoal goal = new PassiveFollowOwnerGoal((MobEntity) (Object) this, 2.0D, 5.0F, 10.0F);
		this.goalSelector.add(1, goal);
	}

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemStack stack = player.getStackInHand(hand);
		if (!(player instanceof IPetList))
			return;
		if (stack.isOf(this.getTameItem()) && this.getOwner() == null) {
			if (this.getWorld().isClient()) {
				cir.setReturnValue(ActionResult.SUCCESS);
			} else {
				this.setOwner(player.getUuid());
				stack.decrement(1);
				((IPetList) player).addPet(this);
				cir.setReturnValue(ActionResult.SUCCESS);
			}
		}
	}

	// @Override
	public Item getTameItem() {
		return Items.HAY_BLOCK;
	}

	@Override
	public UUID getOwner() {
		return this.ownerUuid;
	}

	@Override
	public void setOwner(UUID owner) {
		this.ownerUuid = owner;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.containsUuid(MorePets.NBT_OWNER_KEY)) {
			this.ownerUuid = nbt.getUuid(MorePets.NBT_OWNER_KEY);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.ownerUuid != null) {
			nbt.putUuid(MorePets.NBT_OWNER_KEY, this.ownerUuid);
		}
	}

}