package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.network.NetworkX;

public class BongoNetwork extends NetworkX {

    public BongoNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected Protocol getProtocol() {
        return Protocol.of("4");
    }

    @Override
    protected void registerPackets() {
        registerGame(NetworkDirection.PLAY_TO_CLIENT, new BongoUpdateMessage.Serializer(), () -> BongoUpdateMessage.Handler::new);
        registerGame(NetworkDirection.PLAY_TO_CLIENT, new AdvancementInfoUpdateMessage.Serializer(), () -> AdvancementInfoUpdateMessage.Handler::new);
    }

    public void updateBongo(Level level) {
        if (!level.isClientSide) {
            channel.send(PacketDistributor.ALL.noArg(), new BongoUpdateMessage(Bongo.get(level)));
        }
    }

    public void updateBongo(Player player) {
        if (!player.getCommandSenderWorld().isClientSide) {
            channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new BongoUpdateMessage(Bongo.get(player.getCommandSenderWorld())));
        }
    }

    public void updateBongo(Level level, BongoMessageType messageType) {
        if (!level.isClientSide) {
            channel.send(PacketDistributor.ALL.noArg(), new BongoUpdateMessage(Bongo.get(level), messageType));
        }
    }

    public void updateBongo(Player player, BongoMessageType messageType) {
        if (!player.getCommandSenderWorld().isClientSide) {
            channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new BongoUpdateMessage(Bongo.get(player.getCommandSenderWorld()), messageType));
        }
    }

    public void syncAdvancement(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            channel.send(PacketDistributor.ALL.noArg(), getAdvancementMessage(advancement));
        }
    }

    public void syncAdvancementTo(Advancement advancement, ServerPlayer playerEntity) {
        if (advancement.getDisplay() != null) {
            channel.send(PacketDistributor.PLAYER.with(() -> playerEntity), getAdvancementMessage(advancement));
        }
    }

    private static AdvancementInfoUpdateMessage getAdvancementMessage(Advancement advancement) {
        //noinspection ConstantConditions
        return new AdvancementInfoUpdateMessage(advancement.getId(), advancement.getDisplay().getIcon(), advancement.getDisplay().getTitle());
    }
}
