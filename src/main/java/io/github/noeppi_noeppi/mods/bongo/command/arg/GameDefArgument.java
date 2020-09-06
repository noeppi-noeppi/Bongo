package io.github.noeppi_noeppi.mods.bongo.command.arg;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.noeppi_noeppi.mods.bongo.data.GameDef;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameDefArgument implements ArgumentType<GameDef> {

    @Nullable
    private final Map<ResourceLocation, GameDef> games;

    private final ResourceLocationArgument rla = new ResourceLocationArgument();

    public GameDefArgument(@Nullable Map<ResourceLocation, GameDef> games) {
        this.games = games;
    }

    public static GameDefArgument gameDef() {
        return new GameDefArgument(null);
    }

    public static GameDefArgument gameDef(Map<ResourceLocation, GameDef> games) {
        return new GameDefArgument(games);
    }

    public GameDef parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation rl = rla.parse(reader);
        GameDef gd = games().get(rl);
        if (gd == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.create.notfound")).create();
        }
        return gd;
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

    private Map<ResourceLocation, GameDef> games() {
        if (games == null) {
            return GameDef.GAMES;
        } else {
            return games;
        }
    }

    public static class Serialzier implements IArgumentSerializer<GameDefArgument> {

        @Override
        public void write(GameDefArgument argument, PacketBuffer buffer) {
            buffer.writeInt(argument.games().size());
            for (GameDef gd : argument.games().values()) {
                buffer.writeResourceLocation(gd.id);
                buffer.writeCompoundTag(gd.getNbt());
            }
        }

        @Nonnull
        @Override
        public GameDefArgument read(@Nonnull PacketBuffer buffer) {
            int amount = buffer.readInt();
            Map<ResourceLocation, GameDef> defs = new HashMap<>();
            for (int i = 0;i < amount; i++) {
                ResourceLocation id = buffer.readResourceLocation();
                //noinspection ConstantConditions
                defs.put(id, new GameDef(id, buffer.readCompoundTag()));
            }
            return new GameDefArgument(defs);
        }

        @Override
        public void write(GameDefArgument argument, @Nonnull JsonObject json) {
            for (GameDef gd : argument.games().values())
                json.addProperty(gd.id.toString(), gd.id.toString());
        }
    }
}
