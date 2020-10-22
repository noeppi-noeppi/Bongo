package io.github.noeppi_noeppi.mods.bongo.network;

import com.google.gson.JsonElement;
import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AdvancementInfoUpdateSerializer implements PacketSerializer<AdvancementInfoUpdateSerializer.AdvancementInfoUpdateMessage> {

    @Override
    public Class<AdvancementInfoUpdateMessage> messageClass() {
        return AdvancementInfoUpdateMessage.class;
    }

    @Override
    public void encode(AdvancementInfoUpdateMessage msg, PacketBuffer buffer) {
        buffer.writeResourceLocation(msg.id);
        buffer.writeCompoundTag(msg.display.write(new CompoundNBT()));
        buffer.writeTextComponent(msg.translation);
        buffer.writeBoolean(msg.tooltip != null);
        if (msg.tooltip != null)
            buffer.writeString(BongoMod.GSON.toJson(msg.tooltip.serialize()), 0x40000);
    }

    @Override
    public AdvancementInfoUpdateMessage decode(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        @SuppressWarnings("ConstantConditions")
        ItemStack display = ItemStack.read(buffer.readCompoundTag());
        ITextComponent translation = buffer.readTextComponent();
        ItemPredicate tooltip = null;
        if (buffer.readBoolean()) {
            tooltip = ItemPredicate.deserialize(BongoMod.GSON.fromJson(buffer.readString(0x40000), JsonElement.class));
        }
        return new AdvancementInfoUpdateMessage(id, display, translation, tooltip);
    }

    public static class AdvancementInfoUpdateMessage {

        public final ResourceLocation id;
        public final ItemStack display;
        public final ITextComponent translation;
        public final ItemPredicate tooltip;

        public AdvancementInfoUpdateMessage(ResourceLocation id, ItemStack display, ITextComponent translation, ItemPredicate tooltip) {
            this.id = id;
            this.display = display;
            this.translation = translation;
            this.tooltip = tooltip;
        }
    }
}
