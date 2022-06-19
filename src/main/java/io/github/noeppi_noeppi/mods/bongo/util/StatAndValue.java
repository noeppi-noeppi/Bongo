package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public record StatAndValue(Stat<?> stat, int value) {
    
    public static final Codec<StatAndValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.STAT_TYPES.getCodec().fieldOf("category").forGetter(stat -> stat.stat().getType()),
            ResourceLocation.CODEC.fieldOf("stat").forGetter(StatAndValue::getValueId),
            Codec.INT.fieldOf("value").forGetter(StatAndValue::value)
    ).apply(instance, (StatType<?> type, ResourceLocation id, Integer value) -> new StatAndValue(resolve(type, id), value)));
    
    private static Stat<?> resolve(StatType<?> statType, ResourceLocation id) {
        Object elem = statType.getRegistry().get(id);
        if (elem == null) {
            throw new IllegalStateException("Invalid stat value id for " + ForgeRegistries.STAT_TYPES.getKey(statType) + ": " + id);
        } else {
            //noinspection unchecked
            return ((StatType<Object>) statType).get(elem);
        }
    }

    public ResourceLocation getValueId() {
        //noinspection unchecked
        return Objects.requireNonNull(((Registry<Object>) stat.getType().getRegistry()).getKey(stat.getValue()), "Invalid stat object: No id");
    }
}
