package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.util.ClientAdvancementInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.function.Supplier;

public record AdvancementInfoUpdateMessage(ResourceLocation id, ItemStack display, Component translation) {
    
    public static class Serializer implements PacketSerializer<AdvancementInfoUpdateMessage> {

        @Override
        public Class<AdvancementInfoUpdateMessage> messageClass() {
            return AdvancementInfoUpdateMessage.class;
        }

        @Override
        public void encode(AdvancementInfoUpdateMessage msg, FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(msg.id);
            buffer.writeNbt(msg.display.save(new CompoundTag()));
            buffer.writeComponent(msg.translation);
        }

        @Override
        public AdvancementInfoUpdateMessage decode(FriendlyByteBuf buffer) {
            ResourceLocation id = buffer.readResourceLocation();
            @SuppressWarnings("ConstantConditions")
            ItemStack display = ItemStack.of(buffer.readNbt());
            Component translation = buffer.readComponent();
            return new AdvancementInfoUpdateMessage(id, display, translation);
        }
    }
    
    public static class Handler implements PacketHandler<AdvancementInfoUpdateMessage> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(AdvancementInfoUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
            ClientAdvancementInfo.updateAdvancementInfo(msg.id, msg.display, msg.translation);
            return true;
        }
    }
}
