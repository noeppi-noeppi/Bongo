package io.github.noeppi_noeppi.mods.bongo.command.arg;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.noeppi_noeppi.mods.bongo.data.task.GameTasks;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameTasksArgument implements ArgumentType<ResourceLocation> {

    @Nullable
    private final Set<ResourceLocation> games;

    private final ResourceLocationArgument rla = new ResourceLocationArgument();

    public GameTasksArgument(@Nullable Set<ResourceLocation> games) {
        this.games = games;
    }

    public static GameTasksArgument gameTasks() {
        return new GameTasksArgument(null);
    }

    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return rla.parse(reader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader reader = new StringReader(context.getInput());
        reader.setCursor(builder.getStart());
        String theString = reader.getRemaining().toLowerCase();
        for (ResourceLocation rl : games()) {
            if (rl.toString().toLowerCase().startsWith(theString))
                builder.suggest(rl.toString());
        }
        return CompletableFuture.completedFuture(builder.build());
    }

    public Collection<String> getExamples() {
        List<String> examples = new ArrayList<>();
        for (ResourceLocation rl : games())
            examples.add(rl.toString());
        return examples;
    }

    private Set<ResourceLocation> games() {
        return games == null ? GameTasks.gameTasks().keySet() : games;
    }

    public static class Info implements ArgumentTypeInfo<GameTasksArgument, GameTasksArgument.Info.Template> {

        public static final GameTasksArgument.Info INSTANCE = new GameTasksArgument.Info();

        private Info() {

        }

        @Override
        public void serializeToNetwork(@Nonnull GameTasksArgument.Info.Template template, @Nonnull FriendlyByteBuf buffer) {
            buffer.writeInt(template.argument.games().size());
            for (ResourceLocation id : template.argument.games()) {
                buffer.writeResourceLocation(id);
            }
        }

        @Nonnull
        @Override
        public GameTasksArgument.Info.Template deserializeFromNetwork(@Nonnull FriendlyByteBuf buffer) {
            int amount = buffer.readInt();
            Set<ResourceLocation> ids = new HashSet<>();
            for (int i = 0; i < amount; i++) {
                ids.add(buffer.readResourceLocation());
            }
            return new GameTasksArgument.Info.Template(new GameTasksArgument(ids));
        }

        @Override
        public void serializeToJson(@Nonnull GameTasksArgument.Info.Template template, @Nonnull JsonObject json) {
            for (ResourceLocation id : template.argument.games()) {
                json.addProperty(id.toString(), id.toString());
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
