package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class BongoNetwork extends NetworkX {

    public BongoNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected Protocol getProtocol() {
        return Protocol.of("3");
    }

    @Override
    protected void registerPackets() {
        register(new BongoUpdateSerializer(), () -> BongoUpdateHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
        register(new AdvancementInfoUpdateSerializer(), () -> AdvancementInfoUpdateHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void updateBongo(Level level) {
        if (!level.isClientSide) {
            channel.send(PacketDistributor.ALL.noArg(), new BongoUpdateSerializer.BongoUpdateMessage(Bongo.get(level)));
        }
    }

    public void updateBongo(Player player) {
        if (!player.getCommandSenderWorld().isClientSide) {
            channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new BongoUpdateSerializer.BongoUpdateMessage(Bongo.get(player.getCommandSenderWorld())));
        }
    }

    public void updateBongo(Level level, BongoMessageType messageType) {
        if (!level.isClientSide) {
            channel.send(PacketDistributor.ALL.noArg(), new BongoUpdateSerializer.BongoUpdateMessage(Bongo.get(level), messageType));
        }
    }

    public void updateBongo(Player player, BongoMessageType messageType) {
        if (!player.getCommandSenderWorld().isClientSide) {
            channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new BongoUpdateSerializer.BongoUpdateMessage(Bongo.get(player.getCommandSenderWorld()), messageType));
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

    private static AdvancementInfoUpdateSerializer.AdvancementInfoUpdateMessage getAdvancementMessage(Advancement advancement) {
        ItemPredicate tooltip = null;
        for (Criterion criterion : advancement.getCriteria().values()) {
            CriterionTriggerInstance inst = criterion.getTrigger();
            if (inst instanceof InventoryChangeTrigger.TriggerInstance) {
                if (tooltip != null) {
                    tooltip = null;
                    break;
                }
                ItemPredicate[] predicates = ((InventoryChangeTrigger.TriggerInstance) inst).predicates;
                if (predicates.length == 1) {
                    tooltip = predicates[0];
                } else {
                    break;
                }
            }
        }

        //noinspection ConstantConditions
        return new AdvancementInfoUpdateSerializer.AdvancementInfoUpdateMessage(advancement.getId(), advancement.getDisplay().getIcon(), advancement.getDisplay().getTitle(), tooltip);
    }
}
