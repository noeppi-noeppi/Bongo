package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.ClientAdvancementInfo;
import io.github.noeppi_noeppi.mods.bongo.util.Highlight;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class TaskTypeAdvancement implements TaskType<ResourceLocation> {

    public static final TaskTypeAdvancement INSTANCE = new TaskTypeAdvancement();

    private TaskTypeAdvancement() {

    }

    @Override
    public String id() {
        return "bongo.advancement";
    }

    @Override
    public Class<ResourceLocation> taskClass() {
        return ResourceLocation.class;
    }

    @Override
    public MapCodec<ResourceLocation> codec() {
        return ResourceLocation.CODEC.fieldOf("value");
    }

    @Override
    public Component name() {
        return Component.translatable("bongo.task.advancement.name");
    }

    @Override
    public Component contentName(ResourceLocation element, @Nullable MinecraftServer server) {
        if (server != null) {
            Advancement advancement = server.getAdvancements().getAdvancement(element);
            if (advancement == null) {
                return Component.translatable("bongo.task.advancement.invalid");
            } else {
                return advancement.getChatComponent();
            }
        } else {
            return DistExecutor.unsafeRunForDist(
                    () -> () -> ClientAdvancementInfo.getTranslation(element),
                    () -> () -> Component.translatable("bongo.task.advancement.invalid")
            );
        }
    }

    @Override
    public Comparator<ResourceLocation> order() {
        return Util.COMPARE_RESOURCE;
    }

    @Override
    public void validate(ResourceLocation element, MinecraftServer server) {
        if (server.getAdvancements().getAdvancement(element) == null) {
            throw new IllegalStateException("Advancement not found: " + element);
        }
    }

    @Override
    public void sync(ResourceLocation element, MinecraftServer server, @Nullable ServerPlayer target) {
        Advancement advancement = server.getAdvancements().getAdvancement(element);
        if (advancement != null) {
            if (target == null) {
                BongoMod.getNetwork().syncAdvancement(advancement);
            } else {
                BongoMod.getNetwork().syncAdvancementTo(advancement, target);
            }
        }
    }

    @Override
    public Stream<ResourceLocation> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return server.getAdvancements().getAllAdvancements().stream()
                    .filter(adv -> adv.getDisplay() != null)
                    .map(Advancement::getId);
        } else {
            return server.getAdvancements().getAllAdvancements().stream()
                    .filter(adv -> adv.getDisplay() != null)
                    .filter(adv -> player.getAdvancements().getOrStartProgress(adv).isDone())
                    .map(Advancement::getId);
        }
    }

    @Override
    public boolean shouldComplete(ServerPlayer player, ResourceLocation element, ResourceLocation compare) {
        return Objects.equals(element, compare);
    }

    @Override
    public Stream<Highlight<?>> highlight(ResourceLocation element) {
        return Stream.of(new Highlight.Advancement(element));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, GuiGraphics graphics) {
        graphics.pose().translate(-1, -1, 0);
        graphics.pose().scale(20 / 26f, 20 / 26f, 1);
        graphics.blit(RenderOverlay.BINGO_SLOTS_TEXTURE, 0, 0, 0, 18, 26, 26, 256, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, GuiGraphics graphics, ResourceLocation element, boolean bigBongo) {
        ItemStack icon = ClientAdvancementInfo.getDisplay(element);
        ItemRenderUtil.renderItem(graphics, icon, false);
    }
}
