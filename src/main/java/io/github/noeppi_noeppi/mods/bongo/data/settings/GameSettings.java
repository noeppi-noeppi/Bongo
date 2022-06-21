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
import org.moddingx.libx.datapack.DataLoader;
import org.moddingx.libx.util.lazy.CachedValue;

import java.io.IOException;
import java.util.*;

@PrimaryConstructor
public record GameSettings(
        PlaySettings game,
        LevelSettings level,
        EquipmentSettings equipment
) {
    
    public static final Codec<GameSettings> CODEC = Codecs.get(BongoMod.class, GameSettings.class);
    
    public static final ResourceLocation DEFAULT_ID = BongoMod.getInstance().resource("default");
    public static final GameSettings DEFAULT = new GameSettings(PlaySettings.DEFAULT, LevelSettings.DEFAULT, EquipmentSettings.DEFAULT);
    
    private static Map<ResourceLocation, Dynamic<JsonElement>> GAME_SETTINGS = Map.of();
    private static final CachedValue<Set<ResourceLocation>> SETTING_KEYS = new CachedValue<>(() -> {
        Set<ResourceLocation> set = new HashSet<>(GAME_SETTINGS.keySet());
        set.add(DEFAULT_ID);
        return Set.copyOf(set);
    });
    
    public static Codec<GameSettings> mergeTo(GameSettings fallback) {
        return RecordCodecBuilder.create(instance -> instance.group(
                DynamicUtil.createMergedCodec(PlaySettings.CODEC, PlaySettings.DEFAULT).fieldOf("game").orElse(PlaySettings.DEFAULT).forGetter(GameSettings::game),
                DynamicUtil.createMergedCodec(LevelSettings.CODEC, LevelSettings.DEFAULT).fieldOf("level").orElse(LevelSettings.DEFAULT).forGetter(GameSettings::level),
                DynamicUtil.createMergedCodec(EquipmentSettings.CODEC, EquipmentSettings.DEFAULT).fieldOf("equipment").orElse(EquipmentSettings.DEFAULT).forGetter(GameSettings::equipment)
        ).apply(instance, GameSettings::new));
    }

    public static Set<ResourceLocation> gameSettings() {
        return SETTING_KEYS.get();
    }
    
    public static GameSettings load(List<ResourceLocation> settings) {
        if (settings.isEmpty() || DEFAULT_ID.equals(settings.get(0))) {
            return DEFAULT;
        } else if (!GAME_SETTINGS.containsKey(settings.get(0))) {
            throw new NoSuchElementException("Settings not found: " + settings.get(0));
        } else try {
            return mergeTo(load(settings.subList(1, settings.size()))).decode(GAME_SETTINGS.get(settings.get(0))).getOrThrow(false, err -> {}).getFirst();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to merge settings");
        }
    }

    public static void loadGameSettings(ResourceManager rm) throws IOException {
        SETTING_KEYS.invalidate();
        GAME_SETTINGS = DataLoader.loadJson(rm, "bingo_settings", (id, json) -> new Dynamic<>(JsonOps.INSTANCE, json));
    }
}
