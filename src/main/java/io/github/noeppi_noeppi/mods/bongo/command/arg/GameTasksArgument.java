package io.github.noeppi_noeppi.mods.bongo.command.arg;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.noeppi_noeppi.mods.bongo.data.GameTasks;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameTasksArgument implements ArgumentType<GameTasks> {

    @Nullable
    private final Map<ResourceLocation, GameTasks> games;

    private final ResourceLocationArgument rla = new ResourceLocationArgument();

    public GameTasksArgument(@Nullable Map<ResourceLocation, GameTasks> games) {
        this.games = games;
    }

    public static GameTasksArgument gameTasks() {
        return new GameTasksArgument(null);
    }

    public GameTasks parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation rl = rla.parse(reader);
        GameTasks gt = games().get(rl);
        if (gt == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.create.notfound")).create();
        }
        return gt;
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

    private Map<ResourceLocation, GameTasks> games() {
        if (games == null) {
            return GameTasks.GAME_TASKS;
        } else {
            return games;
        }
    }

    public static class Serializer implements IArgumentSerializer<GameTasksArgument> {

        @Override
        public void write(GameTasksArgument argument, PacketBuffer buffer) {
            buffer.writeInt(argument.games().size());
            for (GameTasks gt : argument.games().values()) {
                buffer.writeResourceLocation(gt.id);
                buffer.writeCompoundTag(gt.getTag());
            }
        }

        @Nonnull
        @Override
        public GameTasksArgument read(@Nonnull PacketBuffer buffer) {
            int amount = buffer.readInt();
            Map<ResourceLocation, GameTasks> defs = new HashMap<>();
            for (int i = 0;i < amount; i++) {
                ResourceLocation id = buffer.readResourceLocation();
                defs.put(id, new GameTasks(id, buffer.readCompoundTag()));
            }
            return new GameTasksArgument(defs);
        }

        @Override
        public void write(GameTasksArgument argument, @Nonnull JsonObject json) {
            for (GameTasks gt : argument.games().values())
                json.addProperty(gt.id.toString(), gt.id.toString());
        }
    }
}
