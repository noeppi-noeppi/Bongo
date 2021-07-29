package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraftforge.registries.ForgeRegistries;

public class StatAndValue {

    public final Stat<?> stat;
    public final int value;

    public StatAndValue(Stat<?> stat, int value) {
        this.stat = stat;
        this.value = value;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("category", "" + stat.getType().getRegistryName());
        //noinspection unchecked
        Registry<Object> registry = (Registry<Object>) stat.getType().getRegistry();
        nbt.putString("stat", "" + registry.getKey(stat.getValue()));
        nbt.putInt("value", value);
        return nbt;
    }

    public static StatAndValue deserializeNBT(CompoundTag nbt) {
        ResourceLocation categoryRL = ResourceLocation.tryParse(nbt.getString("category"));
        if (categoryRL == null) throw new IllegalStateException("Invalid stat category id: " + nbt.getString("category"));
        //noinspection unchecked
        StatType<Object> type = (StatType<Object>) ForgeRegistries.STAT_TYPES.getValue(categoryRL);
        if (type == null) throw new IllegalStateException("Unknown stat category: " + categoryRL);
        ResourceLocation statRL = ResourceLocation.tryParse(nbt.getString("stat"));
        if (statRL == null) throw new IllegalStateException("Invalid stat value id for " + categoryRL + ": " + nbt.getString("category"));
        Object stat = type.getRegistry().get(statRL);
        if (stat == null) throw new IllegalStateException("Unknown stat value for " + categoryRL + ": " + statRL);
        int value = nbt.getInt("value");
        return new StatAndValue(type.get(stat), value);
    }
    
    public ResourceLocation getValueId() {
        //noinspection unchecked
        return ((Registry<Object>) stat.getType().getRegistry()).getKey(stat.getValue());
    }
}
