package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

public class BongoNetwork extends NetworkX {

    public BongoNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected String getProtocolVersion() {
        return "3";
    }

    @Override
    protected void registerPackets() {
        register(new BongoUpdateSerializer(), () -> BongoUpdateHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
        register(new AdvancementInfoUpdateSerializer(), () -> AdvancementInfoUpdateHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void updateBongo(World world) {
        if (!world.isRemote) {
            instance.send(PacketDistributor.DIMENSION.with(world::getDimensionKey), new BongoUpdateSerializer.BongoUpdateMessage(Bongo.get(world)));
        }
    }

    public void updateBongo(PlayerEntity player) {
        if (!player.getEntityWorld().isRemote) {
            instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new BongoUpdateSerializer.BongoUpdateMessage(Bongo.get(player.getEntityWorld())));
        }
    }

    public void updateBongo(World world, BongoMessageType messageType) {
        if (!world.isRemote) {
            instance.send(PacketDistributor.ALL.noArg(), new BongoUpdateSerializer.BongoUpdateMessage(Bongo.get(world), messageType));
        }
    }

    public void updateBongo(PlayerEntity player, BongoMessageType messageType) {
        if (!player.getEntityWorld().isRemote) {
            instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new BongoUpdateSerializer.BongoUpdateMessage(Bongo.get(player.getEntityWorld()), messageType));
        }
    }

    public void syncAdvancement(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            instance.send(PacketDistributor.ALL.noArg(), getAdvancementMessage(advancement));
        }
    }

    public void syncAdvancementTo(Advancement advancement, ServerPlayerEntity playerEntity) {
        if (advancement.getDisplay() != null) {
            instance.send(PacketDistributor.PLAYER.with(() -> playerEntity), getAdvancementMessage(advancement));
        }
    }

    private static AdvancementInfoUpdateSerializer.AdvancementInfoUpdateMessage getAdvancementMessage(Advancement advancement) {
        ItemPredicate tooltip = null;
        for (Criterion criterion : advancement.getCriteria().values()) {
            ICriterionInstance inst = criterion.getCriterionInstance();
            if (inst instanceof InventoryChangeTrigger.Instance) {
                if (tooltip != null) {
                    tooltip = null;
                    break;
                }
                ItemPredicate[] predicates = ((InventoryChangeTrigger.Instance) inst).items;
                if (predicates.length == 1) {
                    tooltip = predicates[0];
                } else {
                    tooltip = null;
                    break;
                }
            }
        }

        //noinspection ConstantConditions
        return new AdvancementInfoUpdateSerializer.AdvancementInfoUpdateMessage(advancement.getId(), advancement.getDisplay().icon, advancement.getDisplay().getTitle(), tooltip);
    }
}
