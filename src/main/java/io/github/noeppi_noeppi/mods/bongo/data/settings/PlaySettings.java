package io.github.noeppi_noeppi.mods.bongo.data.settings;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.WinCondition;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.Dynamic;
import org.moddingx.libx.annotation.codec.Param;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;

@PrimaryConstructor
public record PlaySettings(
        @Param WinCondition winCondition,
        boolean invulnerable,
        boolean pvp,
        boolean friendlyFire,
        @Dynamic TimeSetting time,
        boolean consumeItems,
        boolean lockTaskOnDeath,
        int teleportsPerTeam,
        boolean leaderboard,
        boolean lockout
) {
    
    public static final Codec<PlaySettings> CODEC = Codecs.get(BongoMod.class, PlaySettings.class);
    
    public static final PlaySettings DEFAULT = new PlaySettings(
            WinCondition.DEFAULT,
            true,
            false,
            false,
            new TimeSetting.Unlimited(),
            false,
            false,
            0,
            false,
            false
    );
}
