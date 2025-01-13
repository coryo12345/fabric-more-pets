package morepets.interfaces;

import net.minecraft.nbt.NbtCompound;

public interface ICustomNbt {
    public void writeCustomDataToNbt(NbtCompound nbt);

    public void readCustomDataFromNbt(NbtCompound nbt);
}
