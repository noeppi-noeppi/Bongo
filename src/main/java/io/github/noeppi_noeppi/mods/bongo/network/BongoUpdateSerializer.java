package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.moddingx.libx.network.PacketSerializer;

import java.util.Objects;

public class BongoUpdateSerializer implements PacketSerializer<BongoUpdateSerializer.BongoUpdateMessage> {

    @Override
    public Class<BongoUpdateMessage> messageClass() {
        return BongoUpdateMessage.class;
    }

    @Override
    public void encode(BongoUpdateMessage msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.bongo.save(new CompoundTag()));
        buffer.writeUtf(msg.bongoMessageType.name());
    }

    @Override
    public BongoUpdateMessage decode(FriendlyByteBuf buffer) {
        Bongo bongo = new Bongo();
        bongo.load(Objects.requireNonNull(buffer.readNbt()));
        return new BongoUpdateMessage(bongo, BongoMessageType.valueOf(buffer.readUtf(32767)));
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
