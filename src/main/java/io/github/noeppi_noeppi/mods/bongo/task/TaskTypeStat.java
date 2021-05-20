package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.StatAndValue;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeStat implements TaskTypeSimple<StatAndValue> {

    public static final TaskTypeStat INSTANCE = new TaskTypeStat();
    
    public static final ResourceLocation STAT_ICONS_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/stats_icons.png");

    private TaskTypeStat() {

    }

    @Override
    public Class<StatAndValue> getTaskClass() {
        return StatAndValue.class;
    }

    @Override
    public String getId() {
        return "bongo.stat";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.task.stat.name";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        AbstractGui.blit(matrixStack, 0, 0, 36, 0, 18, 18, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, StatAndValue content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        Object value = content.stat.getValue();
        if (value instanceof IItemProvider) {
            ItemStack renderStack = new ItemStack((IItemProvider) value, content.value);
            RenderHelperItem.renderItemGui(matrixStack, buffer, renderStack, 0, 0, 16, false);
            int x = -1;
            if (content.stat.getType() == Stats.ITEM_CRAFTED) {
                x = 19;
            } else if (content.stat.getType() == Stats.ITEM_USED) {
                x = 37;
            } else if (content.stat.getType() == Stats.BLOCK_MINED) {
                x = 55;
            } else if (content.stat.getType() == Stats.ITEM_BROKEN) {
                x = 74;
            } else if (content.stat.getType() == Stats.ITEM_PICKED_UP) {
                x = 91;
            } else if (content.stat.getType() == Stats.ITEM_DROPPED) {
                x = 109;
            }
            if (x >= 0) {
                matrixStack.push();
                matrixStack.translate(9, -1, 100);
                matrixStack.scale(0.5f, 0.5f, 1);
                Minecraft.getInstance().getTextureManager().bindTexture(RenderOverlay.ICONS_TEXTURE);
                AbstractGui.blit(matrixStack, 0, 0, 0, 32, 0, 16, 16, 256, 256);
                matrixStack.translate(0, 0, 10);
                Minecraft.getInstance().getTextureManager().bindTexture(STAT_ICONS_TEXTURE);
                AbstractGui.blit(matrixStack, 0, 0, 0, x, 19, 16, 16, 128, 128);
                matrixStack.pop();
            }
        } else if (value instanceof EntityType<?>) {
            TaskTypeEntity.INSTANCE.renderSlotContent(mc, (EntityType<?>) value, matrixStack, buffer, bigBongo);
        } else {
            boolean foundCustomTex = false;
            if (Stats.CUSTOM.equals(content.stat.getType()) && content.stat.getValue() instanceof ResourceLocation) {
                ResourceLocation textureLocation = new ResourceLocation(((ResourceLocation) content.stat.getValue()).getNamespace(), "textures/icon/stat/" + ((ResourceLocation) content.stat.getValue()).getPath() + ".png");
                mc.getTextureManager().bindTexture(textureLocation);
                Texture texture = mc.getTextureManager().getTexture(textureLocation);
                if (texture != null && texture.getGlTextureId() != MissingTextureSprite.getDynamicTexture().getGlTextureId()) {
                    foundCustomTex = true;
                    matrixStack.push();
                    matrixStack.scale(0.5f, 0.5f, 0.5f);
                    AbstractGui.blit(matrixStack, 0, 0, 0, 0, 0, 32, 32, 32, 32);
                    matrixStack.pop();
                }
            }
            if (!foundCustomTex) {
                matrixStack.push();
                matrixStack.scale(0.5f, 0.5f, 0.5f);
                mc.getTextureManager().bindTexture(RenderOverlay.ICONS_TEXTURE);
                AbstractGui.blit(matrixStack, 0, 0, 0, 0, 0, 32, 32, 256, 256);
                matrixStack.pop();
            }
        }
        if (!bigBongo) {
            matrixStack.push();
            matrixStack.translate(0, 0, 200);
            matrixStack.scale(2/3f, 2/3f, 1);
            FontRenderer fr = Minecraft.getInstance().fontRenderer;
            String text = content.stat.format(content.value);
            fr.renderString(text, (float) (25 - fr.getStringWidth(text)), 17, 0xffffff, true, matrixStack.getLast().getMatrix(), buffer, false, 0, 15728880);
            matrixStack.pop();
        }
    }

    @Override
    public String getTranslatedContentName(StatAndValue content) {
        String text = getContentName(content, null).getStringTruncated(16);
        return text + ": " + content.stat.format(content.value);
    }

    @Override
    public ITextComponent getContentName(StatAndValue content, @CheckForNull MinecraftServer server) {
        ITextComponent tc = new StringTextComponent("");
        Object value = content.stat.getValue();
        if (value instanceof IItemProvider) {
            tc = new ItemStack((IItemProvider) value).getDisplayName();
        } else if (value instanceof EntityType<?>) {
            tc = ((EntityType<?>) value).getName();
        } else if (value instanceof ResourceLocation) {
            return new StringTextComponent(((ResourceLocation) value).getPath().replace('_', ' '));
        }
        //noinspection ConstantConditions
        return new TranslationTextComponent("stat_type." + ForgeRegistries.STAT_TYPES.getKey(content.stat.getType()).toString().replace(':', '.')).appendSibling(new StringTextComponent(" ")).appendSibling(tc);
    }

    @Override
    public boolean shouldComplete(StatAndValue element, PlayerEntity player, StatAndValue compare) {
        return element.stat.getType().equals(compare.stat.getType()) && element.stat.getValue().equals(compare.stat.getValue()) && compare.value >= element.value;
    }

    @Override
    public CompoundNBT serializeNBT(StatAndValue element) {
        return element.serializeNBT();
    }

    @Override
    public StatAndValue deserializeNBT(CompoundNBT nbt) {
        return StatAndValue.deserializeNBT(nbt);
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(StatAndValue element) {
        Item item = element.stat.getValue() instanceof IItemProvider ? ((IItemProvider) element.stat.getValue()).asItem() : null;
        return stack -> item != null && stack.getItem() == item;
    }

    @Nullable
    @Override
    public Comparator<StatAndValue> getSorting() {
        return Comparator.comparing((StatAndValue stat) -> stat.stat.getType().getRegistryName(), Util.COMPARE_RESOURCE)
                .thenComparing(StatAndValue::getValueId, Util.COMPARE_RESOURCE)
                .thenComparingInt((StatAndValue stat) -> stat.value);
    }

    @Override
    public Stream<StatAndValue> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        if (player != null) {
            ServerStatisticsManager mgr = server.getPlayerList().getPlayerStats(player);
            return mgr.statsData.object2IntEntrySet().stream().filter(entry -> entry.getIntValue() > 0).map(entry -> new StatAndValue(entry.getKey(), entry.getIntValue()));
        } else {
            return Stream.empty();
        }
    }
}
