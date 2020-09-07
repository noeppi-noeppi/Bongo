package io.github.noeppi_noeppi.mods.bongo.command.arg;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UppercaseEnumArgument<T extends Enum<T>> implements ArgumentType<T> {

    public static <R extends Enum<R>> UppercaseEnumArgument<R> enumArgument(Class<R> enumClass) {
        return new UppercaseEnumArgument<>(enumClass);
    }

    private final Class<T> enumClass;

    private UppercaseEnumArgument(final Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T parse(final StringReader reader) {
        return Enum.valueOf(enumClass, reader.readUnquotedString().toUpperCase());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(Stream.of(enumClass.getEnumConstants()).map(Object::toString), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Stream.of(enumClass.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class Serializer implements IArgumentSerializer<UppercaseEnumArgument<?>> {

        @Override
        public void write(UppercaseEnumArgument argument, PacketBuffer buffer) {
            buffer.writeString(argument.enumClass.getName());
        }

        @Nonnull
        @Override
        public UppercaseEnumArgument read(PacketBuffer buffer) {
            String name = buffer.readString();
            try {
                return new UppercaseEnumArgument(Class.forName(name));
            } catch (ClassNotFoundException e) {
                System.err.println("Can' get enum value of type " + name + ". " + e.getMessage());
                //noinspection ConstantConditions
                return null;
            }
        }

        @Override
        public void write(UppercaseEnumArgument argument, JsonObject json) {
            json.addProperty("enum", argument.enumClass.getName());
        }
    }
}