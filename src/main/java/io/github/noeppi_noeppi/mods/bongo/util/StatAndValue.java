package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

public class StatAndValue {

    public final Stat<?> stat;
    public final int value;

    public StatAndValue(Stat<?> stat, int value) {
        this.stat = stat;
        this.value = value;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        //noinspection ConstantConditions
        nbt.putString("category", stat.getType().getRegistryName().toString());
        //noinspection unchecked
        Registry<Object> registry = (Registry<Object>) stat.getType().getRegistry();
        //noinspection ConstantConditions
        nbt.putString("stat", registry.getKey(stat.getValue()).toString());
        nbt.putInt("value", value);
        return nbt;
    }

    public static StatAndValue deserializeNBT(CompoundNBT nbt) {
        //noinspection unchecked
        StatType<Object> type = (StatType<Object>) ForgeRegistries.STAT_TYPES.getValue(new ResourceLocation(nbt.getString("category")));
        //noinspection ConstantConditions
        Object stat = type.getRegistry().getOrDefault(new ResourceLocation(nbt.getString("stat")));
        int value = nbt.getInt("value");
        //noinspection ConstantConditions
        return new StatAndValue(type.get(stat), value);
    }
}
