package io.github.noeppi_noeppi.mods.bongo.data.settings;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.Unit;
import org.moddingx.libx.codec.MoreCodecs;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public enum KeptLevelData {
    game_mode,
    equipment,
    advancements,
    experience,
    statistics,
    time,
    weather,
    wandering_trader_time;
    
    public static final Set<KeptLevelData> ALL = Set.of(values());
    
    public static final Codec<Set<KeptLevelData>> CODEC = Codec.either(
            MoreCodecs.fixed("all"),
            MoreCodecs.enumCodec(KeptLevelData.class).listOf().xmap(Set::copyOf, List::copyOf)
    ).xmap(either -> either.map(unit -> ALL, Function.identity()), set -> ALL.equals(set) ? Either.left(Unit.INSTANCE) : Either.right(set));
}
