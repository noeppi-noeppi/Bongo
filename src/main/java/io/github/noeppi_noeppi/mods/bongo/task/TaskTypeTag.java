package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.util.Highlight;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.bongo.util.TagWithCount;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.moddingx.libx.render.ClientTickHandler;
import org.moddingx.libx.util.Misc;
import org.moddingx.libx.util.data.TagAccess;
import org.moddingx.libx.util.game.ComponentUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TaskTypeTag implements TaskType<TagWithCount> {

    public static final TaskTypeTag INSTANCE = new TaskTypeTag();

    private TaskTypeTag() {

    }

    @Override
    public String id() {
        return "bongo.tag";
    }

    @Override
    public Class<TagWithCount> taskClass() {
        return TagWithCount.class;
    }

    @Override
    public MapCodec<TagWithCount> codec() {
        return TagWithCount.CODEC.fieldOf("value");
    }

    @Override
    public Component name() {
        return Component.translatable("bongo.task.item.name");
    }

    @Override
    public Component contentName(TagWithCount element, @Nullable MinecraftServer server) {
        return Component.literal(Util.resourceStr(element.getId()));
    }

    @Override
    public Comparator<TagWithCount> order() {
        return Comparator.comparing(TagWithCount::getId, Util.COMPARE_RESOURCE).thenComparingInt(TagWithCount::getCount);
    }

    @Override
    public void validate(TagWithCount element, MinecraftServer server) {
        if (!element.getId().equals(Misc.MISSIGNO)) {
            Optional<HolderSet.Named<Item>> tag = TagAccess.ROOT.tryGet(element.getTag());
            if (tag.isEmpty() || tag.get().stream().toList().isEmpty()) {
                throw new IllegalStateException("Empty or unknown tag: " + element.getId());
            }
        }
    }

    @Override
    public Stream<TagWithCount> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        @SuppressWarnings("unchecked")
        Registry<Item> registry = (Registry<Item>) Registry.REGISTRY.get(Registry.ITEM_REGISTRY.location());
        if (registry == null) {
            new IllegalStateException("Item registry not found").printStackTrace();
            return Stream.empty();
        }
        if (player == null) {
            return registry.getTags().map(Pair::getFirst).map(key -> new TagWithCount(key.location(), 1));
        } else {
            return player.getInventory().items.stream()
                    .filter(stack -> !stack.isEmpty())
                    .flatMap(stack -> registry.getResourceKey(stack.getItem()).flatMap(registry::getHolder).stream()
                            .flatMap(Holder::tags).map(key -> new TagWithCount(key.location(), stack.getCount()))
                    );
        }
    }

    @Override
    public boolean shouldComplete(ServerPlayer player, TagWithCount element, TagWithCount compare) {
        return element.getId().equals(compare.getId()) && element.getCount() <= compare.getCount();
    }

    @Override
    public void consume(ServerPlayer player, TagWithCount element, TagWithCount found) {
        Util.removeItems(player, element.getCount(), stack -> element.contains(stack.getItem()));
    }

    @Override
    public Stream<Highlight<?>> highlight(TagWithCount element) {
        return element.getItems().stream().map(ItemStack::new).map(Highlight.Item::new);
    }

    @Override
    public void invalidate(TagWithCount element) {
        element.invalidate();
    }

    @Override
    public FormattedCharSequence renderDisplayName(Minecraft mc, TagWithCount element) {
        ItemStack stack = cycle(element);
        if (stack == null) {
            return Component.translatable("bongo.task.tag.empty").getVisualOrderText();
        } else {
            FormattedCharSequence name = ComponentUtil.subSequence(stack.getHoverName().getVisualOrderText(), 0, 16);
            if (element.getCount() != 1) {
                return FormattedCharSequence.composite(name, FormattedCharSequence.forward(" x " + element.getCount(), Style.EMPTY));
            } else {
                return name;
            }
        }
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        GuiComponent.blit(poseStack, 0, 0, 0, 0, 18, 18, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, TagWithCount element, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        ItemStack stack = cycle(element);
        ItemRenderUtil.renderItem(poseStack, buffer, stack == null ? new ItemStack(Items.BARRIER) : stack, !bigBongo);
    }

    @Nullable
    private static ItemStack cycle(TagWithCount element) {
        List<Item> items = element.getItems();
        if (items.isEmpty()) {
            return null;
        } else {
            ItemStack stack = new ItemStack(items.get((ClientTickHandler.ticksInGame / 20) % items.size()));
            stack.setCount(element.getCount());
            return stack;
        }
    }
}
