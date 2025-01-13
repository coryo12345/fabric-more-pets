package morepets.interfaces;

import java.util.List;

import net.minecraft.entity.mob.MobEntity;

public interface IPetList {
    public List<MobEntity> getPets();

    public void setPets(List<MobEntity> pets);

    public void addPet(MobEntity pet);
}
