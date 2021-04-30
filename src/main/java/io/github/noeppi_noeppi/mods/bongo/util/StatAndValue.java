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
        nbt.putString("category", "" + stat.getType().getRegistryName());
        //noinspection unchecked
        Registry<Object> registry = (Registry<Object>) stat.getType().getRegistry();
        nbt.putString("stat", "" + registry.getKey(stat.getValue()));
        nbt.putInt("value", value);
        return nbt;
    }

    public static StatAndValue deserializeNBT(CompoundNBT nbt) {
        ResourceLocation categoryRL = ResourceLocation.tryCreate(nbt.getString("category"));
        if (categoryRL == null) throw new IllegalStateException("Invalid stat category id: " + nbt.getString("category"));
        //noinspection unchecked
        StatType<Object> type = (StatType<Object>) ForgeRegistries.STAT_TYPES.getValue(categoryRL);
        if (type == null) throw new IllegalStateException("Unknown stat category: " + categoryRL);
        ResourceLocation statRL = ResourceLocation.tryCreate(nbt.getString("stat"));
        if (statRL == null) throw new IllegalStateException("Invalid stat value id for " + categoryRL + ": " + nbt.getString("category"));
        Object stat = type.getRegistry().getOrDefault(statRL);
        if (stat == null) throw new IllegalStateException("Unknown stat value for " + categoryRL + ": " + statRL);
        int value = nbt.getInt("value");
        return new StatAndValue(type.get(stat), value);
    }
    
    public ResourceLocation getValueId() {
        //noinspection unchecked
        return ((Registry<Object>) stat.getType().getRegistry()).getKey(stat.getValue());
    }
}
