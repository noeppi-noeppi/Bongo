package io.github.noeppi_noeppi.mods.bongo.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.util.Highlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.codec.CodecHelper;
import org.moddingx.libx.codec.MoreCodecs;
import org.moddingx.libx.render.RenderHelper;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Task {

    public static Task EMPTY = new Task(TaskContent.EMPTY, TaskExtension.EMPTY);

    public static final Codec<Task> CODEC = MoreCodecs.extend(TaskContent.CODEC, TaskExtension.CODEC, task -> Pair.of(task.content, task.ext), Task::new);
    
    public static final Set<JsonElement> RESERVED_KEYS = Stream.concat(
            Stream.of(new JsonPrimitive("type"), new JsonPrimitive("weight")),
            TaskExtension.CODEC.keys(JsonOps.INSTANCE)
    ).collect(Collectors.toUnmodifiableSet());
    
    private final TaskContent content;
    private final TaskExtension ext;
    
    public <T> Task(TaskType<T> type, T element) {
        this(new TaskContent(type, element), TaskExtension.EMPTY);
    }
    
    public <T> Task(TaskType<T> type, T element, boolean inverted, @Nullable ResourceLocation customTexture) {
        this(new TaskContent(type, element), new TaskExtension(inverted, Optional.ofNullable(customTexture)));
    }
    
    private Task(TaskContent content, TaskExtension ext) {
        this.content = content;
        this.ext = ext;
    }

    public TaskType<?> getType() {
        return content.type;
    }

    public Component typeName() {
        if (this.inverted()) {
            return Component.translatable("bongo.avoid", this.content.type.name());
        } else {
            return this.content.type.name();
        }
    }
    
    public Component contentName(@Nullable MinecraftServer server) {
        //noinspection unchecked
        return ((TaskType<Object>) this.content.type).contentName(this.content.element, server);
    }
    
    public void sync(MinecraftServer server, @Nullable ServerPlayer target) {
        //noinspection unchecked
        ((TaskType<Object>) this.content.type).sync(this.content.element, server, target);
    }
    
    public <T> CompletionState shouldComplete(TaskType<T> type, ServerPlayer player, T compare) {
        if (this.content.type == type) {
            //noinspection unchecked
            if (((TaskType<Object>) this.content.type).shouldComplete(player, this.content.element, compare)) {
                return this.inverted() ? CompletionState.LOCK : CompletionState.COMPLETE;
            } else {
                return CompletionState.KEEP;
            }
        } else {
            return CompletionState.KEEP;
        }
    }
    
    public <T> void consume(TaskType<T> type, ServerPlayer player, T found) {
        if (!this.inverted()) {
            if (this.content.type == type) {
                //noinspection unchecked
                ((TaskType<Object>) this.content.type).consume(player, this.content.element, found);
            }
        }
    }
    
    public Stream<Highlight<?>> highlight() {
        if (this.inverted()) return Stream.empty();
        //noinspection unchecked
        return ((TaskType<Object>) this.content.type).highlight(this.content.element);
    }
    
    public void invalidate() {
        //noinspection unchecked
        ((TaskType<Object>) this.content.type).invalidate(this.content.element);
    }

    @OnlyIn(Dist.CLIENT)
    public FormattedCharSequence renderDisplayName(Minecraft mc) {
        //noinspection unchecked
        return ((TaskType<Object>) this.content.type).renderDisplayName(mc, this.content.element);
    }
    
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, GuiGraphics graphics) {
        if (this.customTexture() == null) {
            this.content.type.renderSlot(mc, graphics);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, GuiGraphics graphics, boolean bigBongo) {
        ResourceLocation tex = this.customTexture();
        if (tex == null) {
            //noinspection unchecked
            ((TaskType<Object>) this.content.type).renderSlotContent(mc, graphics, this.content.element, bigBongo);
        } else {
            RenderHelper.resetColor();
            graphics.blit(tex, -1, -1, 0, 0, 18, 18, 18, 18);
        }
    }
    
    public void validate(MinecraftServer server) {
        try {
            //noinspection unchecked
            ((TaskType<Object>) this.content.type).validate(this.content.element, server);
        } catch (Exception e) {
            BongoMod.logger.error("Failed to validate task of type {}: {}", this.content.type.id(), e.getMessage());
        }
    }
    
    public <T> Optional<T> getElement(TaskType<T> type) {
        if (this.content.type == type) {
            //noinspection unchecked
            return Optional.of((T) this.content.element);
        } else {
            return Optional.empty();
        }
    }
    
    @Nullable
    public ResourceLocation customTexture() {
        return this.ext.customTexture.orElse(null);
    }
    
    public boolean inverted() {
        return this.ext.inverted;
    }
    
    public boolean isWinningTask(boolean completed, boolean locked) {
        return this.inverted() ? !locked : completed;
    }
    
    private static class TaskContent {

        public static final TaskContent EMPTY = new TaskContent(TaskTypeEmpty.INSTANCE, Unit.INSTANCE);
        
        @SuppressWarnings("unchecked")
        public static final Codec<TaskContent> CODEC = MoreCodecs.mapDispatch(
                TaskTypes.CODEC.fieldOf("type"),
                TaskTypes::getCodec,
                content -> Pair.of(content.type, content.element),
                (type, element) -> CodecHelper.doesNotThrow(() -> new TaskContent((TaskType<Object>) type, element))
        );
        
        private final TaskType<?> type;
        private final Object element;

        private <T> TaskContent(TaskType<T> type, T element) {
            this.type = type;
            this.element = element;
            validateElementType();
        }
    
        private void validateElementType() {
            if (!type.taskClass().isAssignableFrom(element.getClass())) {
                throw new IllegalStateException("Can't create task of type " + type.id() + " with element of type " + element.getClass());
            }
        }
    }
    
    private record TaskExtension(boolean inverted, Optional<ResourceLocation> customTexture) {

        public static final TaskExtension EMPTY = new TaskExtension(false, Optional.empty());
        
        public static final MapCodec<TaskExtension> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("inverted", false).forGetter(TaskExtension::inverted),
                ResourceLocation.CODEC.optionalFieldOf("custom_texture").forGetter(TaskExtension::customTexture)
        ).apply(instance, TaskExtension::new));
    }
    
    public enum CompletionState {
        KEEP(false, false),
        COMPLETE(true, false),
        LOCK(false, true);
        
        public final boolean shouldComplete;
        public final boolean shouldLock;

        CompletionState(boolean shouldComplete, boolean shouldLock) {
            this.shouldComplete = shouldComplete;
            this.shouldLock = shouldLock;
        }
    }
}
