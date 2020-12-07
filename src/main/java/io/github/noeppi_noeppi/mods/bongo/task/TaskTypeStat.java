package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.StatAndValue;
import net.minecraft.block.Block;
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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeStat implements TaskType<StatAndValue> {

    public static final TaskTypeStat INSTANCE = new TaskTypeStat();

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
        } else if (value instanceof EntityType<?>) {
            TaskTypeEntity.INSTANCE.renderSlotContent(mc, (EntityType<?>) value, matrixStack, buffer, bigBongo);
        } else {
            boolean foundCustomTex = false;
            if (Stats.CUSTOM.equals(content.stat.getType()) && content.stat.getValue() instanceof ResourceLocation) {
                ResourceLocation textureLocation = new ResourceLocation(((ResourceLocation) content.stat.getValue()).getNamespace(), "textures/icon/stat/" + ((ResourceLocation) content.stat.getValue()).getPath() + ".png");
                mc.getTextureManager().bindTexture(textureLocation);
                Texture texture = mc.getTextureManager().getTexture(textureLocation);
                if (texture != null && texture.getGlTextureId() != MissingTextureSprite.getDynamicTexture().getGlTextureId()) {
                    AbstractGui.blit(matrixStack, 0, 0, 0, 0, 0, 32, 32, 256, 256);
                    foundCustomTex = true;
                }
            }
            if (!foundCustomTex) {
                matrixStack.push();
                matrixStack.scale(0.5f, 0.5f, 0.5f);
                mc.getTextureManager().bindTexture(RenderOverlay.ICONS_TEXTURE);
                AbstractGui.blit(matrixStack, 0, 0, 0, 0, 0, 16, 16, 16, 16);
                matrixStack.pop();
            }
        }
        if (!bigBongo) {
            matrixStack.push();
            matrixStack.translate(0, 0, 200);
            FontRenderer fr = Minecraft.getInstance().fontRenderer;
            String text = Integer.toString(content.value);
            fr.renderString(text, (float) (17 - fr.getStringWidth(text)), 9, 0xffffff, true, matrixStack.getLast().getMatrix(), buffer, false, 0, 15728880);
            matrixStack.pop();
        }
    }

    @Override
    public String getTranslatedContentName(StatAndValue content) {
        return getContentName(content, null).getUnformattedComponentText();
    }

    @Override
    public ITextComponent getContentName(StatAndValue content, @CheckForNull MinecraftServer server) {
        IFormattableTextComponent tc = new StringTextComponent(" ");
        Object value = content.stat.getValue();
        if (value instanceof Item) {
            tc.append(((Item) value).getName());
        } else if (value instanceof Block) {
            tc.append(((Block) value).getTranslatedName());
        } else if (value instanceof EntityType<?>) {
            tc.append(((EntityType<?>) value).getName());
        } else if (value instanceof ResourceLocation) {
            tc.append(new StringTextComponent(((ResourceLocation) value).getPath().replace('_', ' ')));
        }
        return new TranslationTextComponent(content.stat.getType().getTranslationKey()).append(tc)
                .append(new StringTextComponent(": " + content.value));

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
