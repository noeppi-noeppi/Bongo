package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.bongo.util.StatAndValue;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.render.RenderHelper;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

public class TaskTypeStat implements TaskType<StatAndValue> {

    public static final TaskTypeStat INSTANCE = new TaskTypeStat();
    
    public static final ResourceLocation STAT_ICONS_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/stats_icons.png");

    private TaskTypeStat() {

    }

    @Override
    public String id() {
        return "bongo.stat";
    }

    @Override
    public Class<StatAndValue> taskClass() {
        return StatAndValue.class;
    }

    @Override
    public MapCodec<StatAndValue> codec() {
        return StatAndValue.CODEC.fieldOf("value");
    }

    @Override
    public Component name() {
        return Component.translatable("bongo.task.stat.name");
    }

    @Override
    public Component contentName(StatAndValue element, @Nullable MinecraftServer server) {
        Component cmp = Component.empty();
        Object value = element.stat().getValue();
        if (value instanceof ItemLike item) {
            cmp = new ItemStack(item).getHoverName();
        } else if (value instanceof EntityType<?> entity) {
            cmp = entity.getDescription();
        } else if (value instanceof ResourceLocation rl) {
            return Component.literal(Util.resourceStr(rl));
        }
        //noinspection ConstantConditions
        return Component.translatable("stat_type." + ForgeRegistries.STAT_TYPES.getKey(element.stat().getType()).toString().replace(':', '.')).append(Component.literal(" ")).append(cmp);
    }

    @Override
    public Comparator<StatAndValue> order() {
        return Comparator.<StatAndValue, ResourceLocation>comparing(stat -> ForgeRegistries.STAT_TYPES.getKey(stat.stat().getType()), Util.COMPARE_RESOURCE)
                .thenComparing(StatAndValue::getValueId, Util.COMPARE_RESOURCE)
                .thenComparingInt(StatAndValue::value);
    }

    @Override
    public void validate(StatAndValue element, MinecraftServer server) {
        element.getValueId();
    }

    @Override
    public Stream<StatAndValue> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player != null) {
            ServerStatsCounter mgr = server.getPlayerList().getPlayerStats(player);
            return mgr.stats.object2IntEntrySet().stream().filter(entry -> entry.getIntValue() > 0).map(entry -> new StatAndValue(entry.getKey(), entry.getIntValue()));
        } else {
            return ForgeRegistries.STAT_TYPES.getEntries().stream()
                    .map(entry -> Map.entry(entry.getKey().location(), entry.getValue()))
                    .flatMap(entry -> entry.getValue().getRegistry().entrySet().stream()
                            .map(Map.Entry::getValue)
                            .map(obj -> new StatAndValue(entry.getValue().get(obj), 1))
                    );
        }
    }

    @Override
    public boolean shouldComplete(ServerPlayer player, StatAndValue element, StatAndValue compare) {
        return element.stat().getType().equals(compare.stat().getType()) && element.stat().getValue().equals(compare.stat().getValue()) && compare.value() >= element.value();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, GuiGraphics graphics) {
        graphics.blit(RenderOverlay.BINGO_SLOTS_TEXTURE, 0, 0, 36, 0, 18, 18, 256, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, GuiGraphics graphics, StatAndValue element, boolean bigBongo) {
        Object value = element.stat().getValue();
        if (value instanceof ItemLike) {
            ItemStack renderStack = new ItemStack((ItemLike) value, element.value());
            ItemRenderUtil.renderItem(graphics, renderStack, false);
            int x = -1;
            if (element.stat().getType() == Stats.ITEM_CRAFTED) {
                x = 19;
            } else if (element.stat().getType() == Stats.ITEM_USED) {
                x = 37;
            } else if (element.stat().getType() == Stats.BLOCK_MINED) {
                x = 55;
            } else if (element.stat().getType() == Stats.ITEM_BROKEN) {
                x = 74;
            } else if (element.stat().getType() == Stats.ITEM_PICKED_UP) {
                x = 91;
            } else if (element.stat().getType() == Stats.ITEM_DROPPED) {
                x = 109;
            }
            if (x >= 0) {
                graphics.pose().pushPose();
                graphics.pose().translate(9, -1, 100);
                graphics.pose().scale(0.5f, 0.5f, 1);
                graphics.blit(RenderOverlay.ICONS_TEXTURE, 0, 0, 0, 32, 0, 16, 16, 256, 256);
                graphics.pose().translate(0, 0, 10);
                graphics.blit(STAT_ICONS_TEXTURE, 0, 0, 0, x, 19, 16, 16, 128, 128);
                graphics.pose().popPose();
            }
        } else if (value instanceof EntityType<?>) {
            TaskTypeEntity.INSTANCE.renderSlotContent(mc, graphics, (EntityType<?>) value, bigBongo);
        } else {
            boolean foundCustomTex = false;
            if (Stats.CUSTOM.equals(element.stat().getType()) && element.stat().getValue() instanceof ResourceLocation) {
                ResourceLocation textureLocation = new ResourceLocation(((ResourceLocation) element.stat().getValue()).getNamespace(), "textures/icon/stat/" + ((ResourceLocation) element.stat().getValue()).getPath() + ".png");
                RenderSystem.setShaderTexture(0, textureLocation);
                AbstractTexture texture = mc.getTextureManager().getTexture(textureLocation);
                //noinspection ConstantConditions
                if (texture != null && texture.getId() != MissingTextureAtlasSprite.getTexture().getId()) {
                    foundCustomTex = true;
                    graphics.pose().pushPose();
                    graphics.pose().scale(0.5f, 0.5f, 0.5f);
                    graphics.blit(textureLocation, 0, 0, 0, 0, 0, 32, 32, 32, 32);
                    graphics.pose().popPose();
                }
            }
            if (!foundCustomTex) {
                graphics.pose().pushPose();
                graphics.pose().scale(0.5f, 0.5f, 0.5f);
                graphics.blit(RenderOverlay.ICONS_TEXTURE, 0, 0, 0, 0, 0, 32, 32, 256, 256);
                graphics.pose().popPose();
            }
        }
        if (!bigBongo) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200);
            graphics.pose().scale(2/3f, 2/3f, 1);
            String text = element.stat().format(element.value());
            RenderHelper.resetColor();
            Font font = Minecraft.getInstance().font;
            font.drawInBatch(text, (float) (25 - font.width(text)), 17, 0xffffff, true, graphics.pose().last().pose(), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            graphics.pose().popPose();
        }
    }
}
