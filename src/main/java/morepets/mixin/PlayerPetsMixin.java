package morepets.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import morepets.MorePets;
import morepets.interfaces.IPetList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;

@Mixin(PlayerEntity.class)
public class PlayerPetsMixin implements IPetList {

    private List<MobEntity> pets;

    @Override
    public List<MobEntity> getPets() {
        if (pets == null) {
            this.pets = new ArrayList<MobEntity>();
        }
        return this.pets;
    }

    @Override
    public void setPets(List<MobEntity> pets) {
        this.pets = pets;
    }

    @Override
    public void addPet(MobEntity pet) {
        List<MobEntity> pets = this.getPets();
        pets.add(pet);
        this.setPets(pets);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!(player.getWorld() instanceof ServerWorld))
            return;
        ServerWorld world = (ServerWorld) player.getWorld();
        if (nbt.contains(MorePets.NBT_PETS_LIST_KEY, NbtCompound.LIST_TYPE)) {
            NbtList petList = nbt.getList(MorePets.NBT_PETS_LIST_KEY, NbtCompound.COMPOUND_TYPE);
            for (int i = 0; i < petList.size(); i++) {
                NbtCompound nbtCompound = petList.getCompound(i);
                Entity pet = world.getEntity(nbtCompound.getUuid("uuid"));
                if (pet != null && pet instanceof MobEntity) {
                    this.addPet((MobEntity) pet);
                }
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        if (this.pets != null) {
            NbtList petList = new NbtList();
            for (MobEntity pet : pets) {
                MorePets.LOGGER.info(String.format("Writing Data: storing pet: %s", pet.getUuidAsString()));
                NbtCompound petCompound = new NbtCompound();
                petCompound.putUuid("uuid", pet.getUuid());
                petList.add(petCompound);
            }
            nbt.put(MorePets.NBT_PETS_LIST_KEY, petList);
        }
    }
}
