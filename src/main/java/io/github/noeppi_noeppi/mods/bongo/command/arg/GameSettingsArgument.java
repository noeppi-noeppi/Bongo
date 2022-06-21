package io.github.noeppi_noeppi.mods.bongo.command.arg;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.noeppi_noeppi.mods.bongo.data.settings.GameSettings;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameSettingsArgument implements ArgumentType<List<ResourceLocation>> {

    @Nullable
    private final Set<ResourceLocation> games;

    private final ResourceLocationArgument rla = new ResourceLocationArgument();

    public GameSettingsArgument(@Nullable Set<ResourceLocation> games) {
        this.games = games;
    }

    public static GameSettingsArgument gameSettings() {
        return new GameSettingsArgument(null);
    }

    public List<ResourceLocation> parse(StringReader reader) throws CommandSyntaxException {
        List<ResourceLocation> list = new ArrayList<>();
        while(true) {
            reader.skipWhitespace();
            if (!reader.canRead()) break;
            ResourceLocation rl = rla.parse(reader);
            list.add(rl);
        }
        return List.copyOf(list);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader reader = new StringReader(context.getInput());
        reader.setCursor(builder.getStart());
        String theString = reader.getRemaining().toLowerCase();
        String start;
        String current;
        if (theString.contains(" ")) {
            start = theString.substring(0, theString.lastIndexOf(' ') + 1);
            current = theString.substring(theString.lastIndexOf(' ') + 1);
        } else {
            start = "";
            current = theString;
        }
        for (ResourceLocation rl : games()) {
            if (rl.toString().toLowerCase().startsWith(current))
                builder.suggest(start + rl);
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
        return games == null ? GameSettings.gameSettings() : games;
    }

    public static class Info implements ArgumentTypeInfo<GameSettingsArgument, Info.Template> {

        public static final Info INSTANCE = new Info();
        
        private Info() {
            
        }
        
        @Override
        public void serializeToNetwork(@Nonnull Template template, @Nonnull FriendlyByteBuf buffer) {
            buffer.writeInt(template.argument.games().size());
            for (ResourceLocation rl : template.argument.games()) {
                buffer.writeResourceLocation(rl);
            }
        }

        @Nonnull
        @Override
        public Template deserializeFromNetwork(@Nonnull FriendlyByteBuf buffer) {
            int amount = buffer.readInt();
            Set<ResourceLocation> ids = new HashSet<>();
            for (int i = 0; i < amount; i++) {
                ids.add(buffer.readResourceLocation());
            }
            return new Template(new GameSettingsArgument(ids));
        }

        @Override
        public void serializeToJson(@Nonnull Template template, @Nonnull JsonObject json) {
            for (ResourceLocation id : template.argument.games()) {
                json.addProperty(id.toString(), id.toString());
            }
        }

        @Nonnull
        @Override
        public Template unpack(@Nonnull GameSettingsArgument argument) {
            return new Template(argument);
        }

        public class Template implements ArgumentTypeInfo.Template<GameSettingsArgument> {
            
            private final GameSettingsArgument argument;
            
            private Template(GameSettingsArgument argument) {
                this.argument = argument;
            }

            @Nonnull
            @Override
            public ArgumentTypeInfo<GameSettingsArgument, ?> type() {
                return Info.this;
            }

            @Nonnull
            @Override
            public GameSettingsArgument instantiate(@Nonnull CommandBuildContext ctx) {
                return this.argument;
            }
        }
    }
}
