package io.github.noeppi_noeppi.mods.bongo.data.settings;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;

@PrimaryConstructor
public record ServerSettings(
        boolean preventJoiningDuringGame
) {

    public static final Codec<ServerSettings> CODEC = Codecs.get(BongoMod.class, ServerSettings.class);

    public static final ServerSettings DEFAULT = new ServerSettings(
            true
    );
}
