package io.github.noeppi_noeppi.mods.bongo.command.arg;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.noeppi_noeppi.mods.bongo.data.GameSettings;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameSettingsArgument implements ArgumentType<GameSettings> {

    @Nullable
    private final Map<ResourceLocation, GameSettings> games;

    private final ResourceLocationArgument rla = new ResourceLocationArgument();

    public GameSettingsArgument(@Nullable Map<ResourceLocation, GameSettings> games) {
        this.games = games;
    }

    public static GameSettingsArgument gameSettings() {
        return new GameSettingsArgument(null);
    }

    public GameSettings parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation rl = rla.parse(reader);
        GameSettings gs = games().get(rl);
        if (gs == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.create.notfound")).create();
        }
        return gs;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader reader = new StringReader(context.getInput());
        reader.setCursor(builder.getStart());
        String theString = reader.getRemaining().toLowerCase();
        for (ResourceLocation rl : games().keySet()) {
            if (rl.toString().toLowerCase().startsWith(theString))
                builder.suggest(rl.toString());
        }
        return CompletableFuture.completedFuture(builder.build());
    }

    public Collection<String> getExamples() {
        List<String> examples = new ArrayList<>();
        for (ResourceLocation rl : games().keySet())
            examples.add(rl.toString());
        return examples;
    }

    private Map<ResourceLocation, GameSettings> games() {
        if (games == null) {
            return GameSettings.GAME_SETTINGS;
        } else {
            return games;
        }
    }

    public static class Serializer implements IArgumentSerializer<GameSettingsArgument> {

        @Override
        public void write(GameSettingsArgument argument, PacketBuffer buffer) {
            buffer.writeInt(argument.games().size());
            for (GameSettings gs : argument.games().values()) {
                buffer.writeResourceLocation(gs.id);
                buffer.writeCompoundTag(gs.getTag());
            }
        }

        @Nonnull
        @Override
        public GameSettingsArgument read(@Nonnull PacketBuffer buffer) {
            int amount = buffer.readInt();
            Map<ResourceLocation, GameSettings> defs = new HashMap<>();
            for (int i = 0;i < amount; i++) {
                ResourceLocation id = buffer.readResourceLocation();
                //noinspection ConstantConditions
                defs.put(id, new GameSettings(id, buffer.readCompoundTag()));
            }
            return new GameSettingsArgument(defs);
        }

        @Override
        public void write(GameSettingsArgument argument, @Nonnull JsonObject json) {
            for (GameSettings gs : argument.games().values())
                json.addProperty(gs.id.toString(), gs.id.toString());
        }
    }
}
