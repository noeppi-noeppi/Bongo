package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.util.ClientAdvancementInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AdvancementInfoUpdateHandler implements PacketHandler<AdvancementInfoUpdateHandler.AdvancementInfoUpdateMessage> {

    @Override
    public Class<AdvancementInfoUpdateMessage> messageClass() {
        return AdvancementInfoUpdateMessage.class;
    }

    @Override
    public void encode(AdvancementInfoUpdateMessage msg, PacketBuffer buffer) {
        buffer.writeResourceLocation(msg.id);
        buffer.writeCompoundTag(msg.display.write(new CompoundNBT()));
        buffer.writeTextComponent(msg.translation);
    }

    @Override
    public AdvancementInfoUpdateMessage decode(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        @SuppressWarnings("ConstantConditions")
        ItemStack display = ItemStack.read(buffer.readCompoundTag());
        ITextComponent translation = buffer.readTextComponent();
        return new AdvancementInfoUpdateMessage(id, display, translation);
    }

    @Override
    public void handle(AdvancementInfoUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientAdvancementInfo.updateAdvancementInfo(msg.id, msg.display, msg.translation));
        ctx.get().setPacketHandled(true);
    }

    public static class AdvancementInfoUpdateMessage {

        public final ResourceLocation id;
        public final ItemStack display;
        public final ITextComponent translation;

        public AdvancementInfoUpdateMessage(ResourceLocation id, ItemStack display, ITextComponent translation) {
            this.id = id;
            this.display = display;
            this.translation = translation;
        }
    }
}
