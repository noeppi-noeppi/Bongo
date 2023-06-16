package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.util.Highlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.util.game.ComponentUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.stream.Stream;

public interface TaskType<T> {

    String id();
    Class<T> taskClass();
    MapCodec<T> codec();
    Component name();
    Component contentName(T element, @Nullable MinecraftServer server);
    Comparator<T> order();
    
    default void validate(T element, MinecraftServer server) {}
    default void sync(T element, MinecraftServer server, @Nullable ServerPlayer target) {}
    Stream<T> listElements(MinecraftServer server, @Nullable ServerPlayer player);
    
    boolean shouldComplete(ServerPlayer player, T element, T compare);
    default void consume(ServerPlayer player, T element, T found) {}
    default Stream<Highlight<?>> highlight(T element) { return Stream.empty(); }
    default void invalidate(T element) {}
    
    @OnlyIn(Dist.CLIENT)
    default FormattedCharSequence renderDisplayName(Minecraft mc, T element) {
        return ComponentUtil.subSequence(this.contentName(element, null).getVisualOrderText(), 0, 16);
    }
    
    @OnlyIn(Dist.CLIENT) void renderSlot(Minecraft mc, GuiGraphics graphics);
    @OnlyIn(Dist.CLIENT) void renderSlotContent(Minecraft mc, GuiGraphics graphics, T element, boolean bigBongo);
}
