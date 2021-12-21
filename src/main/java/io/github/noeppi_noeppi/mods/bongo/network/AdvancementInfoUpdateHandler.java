package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.util.ClientAdvancementInfo;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AdvancementInfoUpdateHandler {

    public static void handle(AdvancementInfoUpdateSerializer.AdvancementInfoUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientAdvancementInfo.updateAdvancementInfo(msg.id, msg.display, msg.translation, stack -> { if (msg.tooltip == null) return false; else return msg.tooltip.matches(stack); }));
        ctx.get().setPacketHandled(true);
    }
}
