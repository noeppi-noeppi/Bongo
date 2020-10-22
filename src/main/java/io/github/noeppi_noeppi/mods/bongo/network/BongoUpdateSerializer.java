package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.Objects;

public class BongoUpdateSerializer implements PacketSerializer<BongoUpdateSerializer.BongoUpdateMessage> {

    @Override
    public Class<BongoUpdateMessage> messageClass() {
        return BongoUpdateMessage.class;
    }

    @Override
    public void encode(BongoUpdateMessage msg, PacketBuffer buffer) {
        buffer.writeCompoundTag(msg.bongo.write(new CompoundNBT()));
        buffer.writeString(msg.bongoMessageType.name());
    }

    @Override
    public BongoUpdateMessage decode(PacketBuffer buffer) {
        Bongo bongo = new Bongo();
        bongo.read(Objects.requireNonNull(buffer.readCompoundTag()));
        return new BongoUpdateMessage(bongo, BongoMessageType.valueOf(buffer.readString()));
    }

    public static class BongoUpdateMessage {

        public final Bongo bongo;
        public final BongoMessageType bongoMessageType;

        public BongoUpdateMessage(Bongo bongo) {
            this(bongo, BongoMessageType.GENERIC);
        }

        public BongoUpdateMessage(Bongo bongo, BongoMessageType bongoMessageType) {
            this.bongo = bongo;
            this.bongoMessageType = bongoMessageType;
        }
    }
}
