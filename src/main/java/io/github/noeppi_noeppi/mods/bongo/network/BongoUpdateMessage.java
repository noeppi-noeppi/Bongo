package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.Objects;
import java.util.function.Supplier;

public record BongoUpdateMessage(Bongo bongo, BongoMessageType bongoMessageType) {
    
    public BongoUpdateMessage(Bongo bongo) {
        this(bongo, BongoMessageType.GENERIC);
    }
    
    public static class Serializer implements PacketSerializer<BongoUpdateMessage> {

        @Override
        public Class<BongoUpdateMessage> messageClass() {
            return BongoUpdateMessage.class;
        }

        @Override
        public void encode(BongoUpdateMessage msg, FriendlyByteBuf buffer) {
            buffer.writeNbt(msg.bongo.save(new CompoundTag()));
            buffer.writeEnum(msg.bongoMessageType);
        }

        @Override
        public BongoUpdateMessage decode(FriendlyByteBuf buffer) {
            Bongo bongo = new Bongo();
            bongo.load(Objects.requireNonNull(buffer.readNbt()));
            return new BongoUpdateMessage(bongo, buffer.readEnum(BongoMessageType.class));
        }
    }
    
    public static class Handler implements PacketHandler<BongoUpdateMessage> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(BongoUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
            Bongo.updateClient(msg.bongo, msg.bongoMessageType);
            return true;
        }
    }
}
