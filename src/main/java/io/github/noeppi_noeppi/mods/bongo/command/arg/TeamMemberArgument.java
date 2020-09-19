package io.github.noeppi_noeppi.mods.bongo.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class TeamMemberArgument implements ArgumentType<PlayerEntity> {

    private final EntityArgument parent = EntityArgument.player();

    @Override
    public PlayerEntity parse(StringReader reader) throws CommandSyntaxException {
        EntitySelector sel = parent.parse(reader);
        sel.
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return null;
    }

    @Override
    public Collection<String> getExamples() {
        return null;
    }
}
