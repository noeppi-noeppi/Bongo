package io.github.noeppi_noeppi.mods.bongo.data.settings;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.util.DynamicUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;
import org.moddingx.libx.codec.MoreCodecs;
import org.moddingx.libx.datapack.DataLoader;
import org.moddingx.libx.util.lazy.CachedValue;

import java.io.IOException;
import java.util.*;

@PrimaryConstructor
public record GameSettings(
        PlaySettings game,
        LevelSettings level,
        EquipmentSettings equipment,
        ServerSettings server
) {
    
    public static final Codec<GameSettings> CODEC = Codecs.get(BongoMod.class, GameSettings.class);
    
    public static final ResourceLocation DEFAULT_ID = BongoMod.getInstance().resource("default");
    public static final GameSettings DEFAULT = new GameSettings(PlaySettings.DEFAULT, LevelSettings.DEFAULT, EquipmentSettings.DEFAULT, ServerSettings.DEFAULT);
    
    private static Map<ResourceLocation, Dynamic<JsonElement>> GAME_SETTINGS = Map.of();
    private static final CachedValue<Set<ResourceLocation>> SETTING_KEYS = new CachedValue<>(() -> {
        Set<ResourceLocation> set = new HashSet<>(GAME_SETTINGS.keySet());
        set.add(DEFAULT_ID);
        return Set.copyOf(set);
    });
    
    public static Codec<GameSettings> mergeTo(GameSettings fallback) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MoreCodecs.optionalFieldOf(DynamicUtil.createMergedCodec(PlaySettings.CODEC, fallback.game()), "game", fallback.game()).forGetter(GameSettings::game),
                MoreCodecs.optionalFieldOf(DynamicUtil.createMergedCodec(LevelSettings.CODEC, fallback.level()), "level", fallback.level()).forGetter(GameSettings::level),
                MoreCodecs.optionalFieldOf(DynamicUtil.createMergedCodec(EquipmentSettings.CODEC, fallback.equipment()), "equipment", fallback.equipment()).forGetter(GameSettings::equipment),
                MoreCodecs.optionalFieldOf(DynamicUtil.createMergedCodec(ServerSettings.CODEC, fallback.server()), "server", fallback.server()).forGetter(GameSettings::server)
        ).apply(instance, GameSettings::new));
    }

    public static Set<ResourceLocation> gameSettings() {
        return SETTING_KEYS.get();
    }
    
    public static GameSettings load(List<ResourceLocation> settings) {
        if (settings.isEmpty()) return DEFAULT;
        ResourceLocation last = settings.get(settings.size() - 1);
        if (DEFAULT_ID.equals(last)) {
            return DEFAULT;
        } else if (!GAME_SETTINGS.containsKey(last)) {
            throw new NoSuchElementException("Settings not found: " + last);
        } else {
            return mergeTo(load(settings.subList(0, settings.size() - 1))).decode(GAME_SETTINGS.get(last)).getOrThrow(false, err -> {}).getFirst();
        }
    }

    public static void loadGameSettings(ResourceManager rm) throws IOException {
        SETTING_KEYS.invalidate();
        GAME_SETTINGS = DataLoader.loadJson(rm, "bingo_settings", (id, json) -> new Dynamic<>(JsonOps.INSTANCE, json));
    }
}
