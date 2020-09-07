package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class BongoUpdateHandler implements PacketHandler<BongoUpdateHandler.BongoUpdateMessage> {

    @Override
    public Class<BongoUpdateMessage> messageClass() {
        return BongoUpdateMessage.class;
    }

    @Override
    public void encode(BongoUpdateMessage msg, PacketBuffer buffer) {
        buffer.writeCompoundTag(msg.bongo.write(new CompoundNBT())).writeString(msg.bongoMessageType.name());
    }

    @Override
    public BongoUpdateMessage decode(PacketBuffer buffer) {
        Bongo bongo = new Bongo();
        bongo.read(Objects.requireNonNull(buffer.readCompoundTag()));
        return new BongoUpdateMessage(bongo, BongoMessageType.valueOf(buffer.readString()));
    }

    @Override
    public void handle(BongoUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Bongo.updateClient(msg.bongo, msg.bongoMessageType));
        ctx.get().setPacketHandled(true);
    }

    public static class BongoUpdateMessage {

        public Bongo bongo;
        public BongoMessageType bongoMessageType;

        public BongoUpdateMessage(Bongo bongo) {
            this(bongo, BongoMessageType.GENERIC);
        }

        public BongoUpdateMessage(Bongo bongo, BongoMessageType bongoMessageType) {
            this.bongo = bongo;
            this.bongoMessageType = bongoMessageType;
        }
    }
}
