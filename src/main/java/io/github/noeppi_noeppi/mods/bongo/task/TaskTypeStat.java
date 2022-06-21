package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.bongo.util.StatAndValue;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
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
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        GuiComponent.blit(poseStack, 0, 0, 36, 0, 18, 18, 256, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, StatAndValue element, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        Object value = element.stat().getValue();
        if (value instanceof ItemLike) {
            ItemStack renderStack = new ItemStack((ItemLike) value, element.value());
            ItemRenderUtil.renderItem(poseStack, buffer, renderStack, false);
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
                poseStack.pushPose();
                poseStack.translate(9, -1, 100);
                poseStack.scale(0.5f, 0.5f, 1);
                RenderSystem.setShaderTexture(0, RenderOverlay.ICONS_TEXTURE);
                GuiComponent.blit(poseStack, 0, 0, 0, 32, 0, 16, 16, 256, 256);
                poseStack.translate(0, 0, 10);
                RenderSystem.setShaderTexture(0, STAT_ICONS_TEXTURE);
                GuiComponent.blit(poseStack, 0, 0, 0, x, 19, 16, 16, 128, 128);
                poseStack.popPose();
            }
        } else if (value instanceof EntityType<?>) {
            TaskTypeEntity.INSTANCE.renderSlotContent(mc, (EntityType<?>) value, poseStack, buffer, bigBongo);
        } else {
            boolean foundCustomTex = false;
            if (Stats.CUSTOM.equals(element.stat().getType()) && element.stat().getValue() instanceof ResourceLocation) {
                ResourceLocation textureLocation = new ResourceLocation(((ResourceLocation) element.stat().getValue()).getNamespace(), "textures/icon/stat/" + ((ResourceLocation) element.stat().getValue()).getPath() + ".png");
                RenderSystem.setShaderTexture(0, textureLocation);
                AbstractTexture texture = mc.getTextureManager().getTexture(textureLocation);
                //noinspection ConstantConditions
                if (texture != null && texture.getId() != MissingTextureAtlasSprite.getTexture().getId()) {
                    foundCustomTex = true;
                    poseStack.pushPose();
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    GuiComponent.blit(poseStack, 0, 0, 0, 0, 0, 32, 32, 32, 32);
                    poseStack.popPose();
                }
            }
            if (!foundCustomTex) {
                poseStack.pushPose();
                poseStack.scale(0.5f, 0.5f, 0.5f);
                RenderSystem.setShaderTexture(0, RenderOverlay.ICONS_TEXTURE);
                GuiComponent.blit(poseStack, 0, 0, 0, 0, 0, 32, 32, 256, 256);
                poseStack.popPose();
            }
        }
        if (!bigBongo) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 200);
            poseStack.scale(2/3f, 2/3f, 1);
            Font fr = Minecraft.getInstance().font;
            String text = element.stat().format(element.value());
            fr.drawInBatch(text, (float) (25 - fr.width(text)), 17, 0xffffff, true, poseStack.last().pose(), buffer, false, 0, 15728880);
            poseStack.popPose();
        }
    }
}
