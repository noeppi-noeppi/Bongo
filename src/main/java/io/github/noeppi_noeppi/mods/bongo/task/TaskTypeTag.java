package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.Highlight;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.bongo.util.TagWithCount;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import net.minecraftforge.registries.tags.ITagManager;
import org.moddingx.libx.render.ClientTickHandler;
import org.moddingx.libx.util.Misc;
import org.moddingx.libx.util.game.ComponentUtil;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
        if (!element.getId().equals(Misc.MISSINGNO)) {
            ITagManager<Item> mgr = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
            if (!mgr.isKnownTagName(element.getKey())) throw new IllegalStateException("Unknown tag: " + element.getId());
            if (mgr.getTag(element.getKey()).isEmpty()) throw new IllegalStateException("Empty tag: " + element.getId());
        }
    }

    @Override
    public Stream<TagWithCount> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        ITagManager<Item> mgr = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
        if (player == null) {
            return mgr.getTagNames().map(key -> new TagWithCount(key.location(), 1));
        } else {
            return player.getInventory().items.stream()
                    .filter(stack -> !stack.isEmpty())
                    .flatMap(stack -> mgr.getReverseTag(stack.getItem()).stream())
                    .flatMap(IReverseTag::getTagKeys)
                    .distinct()
                    .map(key -> new TagWithCount(key.location(), 1));
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
    @OnlyIn(Dist.CLIENT)
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
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, GuiGraphics graphics) {
        graphics.blit(RenderOverlay.BINGO_SLOTS_TEXTURE, 0, 0, 0, 0, 18, 18, 256, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, GuiGraphics graphics, TagWithCount element, boolean bigBongo) {
        ItemStack stack = cycle(element);
        ItemRenderUtil.renderItem(graphics, stack == null ? new ItemStack(Items.BARRIER) : stack, !bigBongo);
    }

    @Nullable
    private static ItemStack cycle(TagWithCount element) {
        List<Item> items = element.getItems();
        if (items.isEmpty()) {
            return null;
        } else {
            ItemStack stack = new ItemStack(items.get((ClientTickHandler.ticksInGame() / 20) % items.size()));
            stack.setCount(element.getCount());
            return stack;
        }
    }
}
