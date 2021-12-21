package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BongoUpdateHandler {

    public static void handle(BongoUpdateSerializer.BongoUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Bongo.updateClient(msg.bongo, msg.bongoMessageType));
        ctx.get().setPacketHandled(true);
    }
}
