package io.github.noeppi_noeppi.mods.bongo.data.settings;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporter;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporterDefault;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporters;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.Param;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;

import java.util.Set;

@PrimaryConstructor
public record LevelSettings(
        @Param(KeptLevelData.class) Set<KeptLevelData> keep,
        @Param(PlayerTeleporters.class) PlayerTeleporter teleporter,
        int teleportRadius
) {

    public static final Codec<LevelSettings> CODEC = Codecs.get(BongoMod.class, LevelSettings.class);
    
    public static final LevelSettings DEFAULT = new LevelSettings(
            Set.of(),
            PlayerTeleporterDefault.INSTANCE,
            10000
    );
}
