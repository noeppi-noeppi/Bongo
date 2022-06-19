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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.create.notfound")).create();
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
        return games == null ? GameTasks.GAME_TASKS : games;
    }

    public static class Info implements ArgumentTypeInfo<GameTasksArgument, GameTasksArgument.Info.Template> {

        public static final GameTasksArgument.Info INSTANCE = new GameTasksArgument.Info();

        private Info() {

        }

        @Override
        public void serializeToNetwork(@Nonnull GameTasksArgument.Info.Template template, @Nonnull FriendlyByteBuf buffer) {
            buffer.writeInt(template.argument.games().size());
            for (GameTasks gt : template.argument.games().values()) {
                buffer.writeResourceLocation(gt.id);
                buffer.writeNbt(gt.getTag());
            }
        }

        @Nonnull
        @Override
        public GameTasksArgument.Info.Template deserializeFromNetwork(@Nonnull FriendlyByteBuf buffer) {
            int amount = buffer.readInt();
            Map<ResourceLocation, GameTasks> defs = new HashMap<>();
            for (int i = 0; i < amount; i++) {
                ResourceLocation id = buffer.readResourceLocation();
                defs.put(id, new GameTasks(id, Objects.requireNonNull(buffer.readNbt())));
            }
            return new GameTasksArgument.Info.Template(new GameTasksArgument(defs));
        }

        @Override
        public void serializeToJson(@Nonnull GameTasksArgument.Info.Template template, @Nonnull JsonObject json) {
            for (GameTasks gs : template.argument.games().values()) {
                json.addProperty(gs.id.toString(), gs.id.toString());
            }
        }

        @Nonnull
        @Override
        public GameTasksArgument.Info.Template unpack(@Nonnull GameTasksArgument argument) {
            return new GameTasksArgument.Info.Template(argument);
        }

        public class Template implements ArgumentTypeInfo.Template<GameTasksArgument> {

            private final GameTasksArgument argument;

            private Template(GameTasksArgument argument) {
                this.argument = argument;
            }

            @Nonnull
            @Override
            public ArgumentTypeInfo<GameTasksArgument, ?> type() {
                return GameTasksArgument.Info.this;
            }

            @Nonnull
            @Override
            public GameTasksArgument instantiate(@Nonnull CommandBuildContext ctx) {
                return this.argument;
            }
        }
    }
}
