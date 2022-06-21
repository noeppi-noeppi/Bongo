package io.github.noeppi_noeppi.mods.bongo.network;

import com.google.gson.JsonElement;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.network.PacketSerializer;

public class AdvancementInfoUpdateSerializer implements PacketSerializer<AdvancementInfoUpdateSerializer.AdvancementInfoUpdateMessage> {

    @Override
    public Class<AdvancementInfoUpdateMessage> messageClass() {
        return AdvancementInfoUpdateMessage.class;
    }

    @Override
    public void encode(AdvancementInfoUpdateMessage msg, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(msg.id);
        buffer.writeNbt(msg.display.save(new CompoundTag()));
        buffer.writeComponent(msg.translation);
        buffer.writeBoolean(msg.tooltip != null);
        if (msg.tooltip != null)
            buffer.writeUtf(BongoMod.GSON.toJson(msg.tooltip.serializeToJson()), 0x40000);
    }

    @Override
    public AdvancementInfoUpdateMessage decode(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        @SuppressWarnings("ConstantConditions")
        ItemStack display = ItemStack.of(buffer.readNbt());
        Component translation = buffer.readComponent();
        ItemPredicate tooltip = null;
        if (buffer.readBoolean()) {
            tooltip = ItemPredicate.fromJson(BongoMod.GSON.fromJson(buffer.readUtf(0x40000), JsonElement.class));
        }
        return new AdvancementInfoUpdateMessage(id, display, translation, tooltip);
    }

    public static class AdvancementInfoUpdateMessage {

        public final ResourceLocation id;
        public final ItemStack display;
        public final Component translation;
        public final ItemPredicate tooltip;

        public AdvancementInfoUpdateMessage(ResourceLocation id, ItemStack display, Component translation, ItemPredicate tooltip) {
            this.id = id;
            this.display = display;
            this.translation = translation;
            this.tooltip = tooltip;
        }
    }
}
