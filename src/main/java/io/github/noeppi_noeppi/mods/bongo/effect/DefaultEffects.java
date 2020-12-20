package io.github.noeppi_noeppi.mods.bongo.effect;

import io.github.championash5357.naughtyornice.api.capability.CapabilityInstances;
import io.github.championash5357.naughtyornice.api.capability.INiceness;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.command.impl.AdvancementCommand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class DefaultEffects {

    public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("patchouli", "guide_book");
    
    public static void register() {

        StartingEffects.registerPlayerEffect((bongo, player) -> {
            player.inventory.clear();
            if (ModList.get().isLoaded("patchouli_jam")) {
                ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(BOOK_LOCATION));
                CompoundNBT nbt = stack.getOrCreateTag();
                nbt.putString("patchouli:book", "patchouli_jam:christmas_guide");
                stack.setTag(nbt);
                player.inventory.setInventorySlotContents(0, stack);
            }
        });
        StartingEffects.registerPlayerEffect((bongo, player) -> {
            //noinspection ConstantConditions
            AdvancementCommand.Action.REVOKE.applyToAdvancements(player, player.getServer().getAdvancementManager().getAllAdvancements());
        });
        StartingEffects.registerPlayerEffect((bongo, player) -> { 
            LazyOptional<INiceness> niceness = player.getCapability(CapabilityInstances.NICENESS_CAPABILITY);
            niceness.ifPresent(n -> n.setNiceness(0, true));
        });

        TaskEffects.registerPlayerEffect((bongo, thePlayer, task) -> {
            Team team = bongo.getTeam(thePlayer);
            if (team != null) {
                IFormattableTextComponent tc = team.getName().append(new TranslationTextComponent("bongo.task.complete")).append(task.getContentName(thePlayer.getServerWorld().getServer()));
                thePlayer.getServerWorld().getServer().getPlayerList().getPlayers().forEach(player -> {
                    player.sendMessage(tc, player.getUniqueID());
                    if (team.hasPlayer(player)) {
                        player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, player.getPosX(), player.getPosY(), player.getPosZ(), 0.5f, 1));
                    }
                });
            }
        });

        WinEffects.registerWorldEffect((bongo, world, team) -> {
            IFormattableTextComponent tc = team.getName().append(new TranslationTextComponent("bongo.win"));
            IFormattableTextComponent tcc = team.getName().append(new TranslationTextComponent("bongo.winplayers"));

            world.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (team.hasPlayer(player)) {
                    tcc.append(new StringTextComponent(" "));
                    IFormattableTextComponent pname = player.getDisplayName().deepCopy();
                    pname.setStyle(Style.EMPTY.applyFormatting(TextFormatting.RESET).applyFormatting(TextFormatting.UNDERLINE).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + player.getPosX() + " " + player.getPosY() + " " + player.getPosZ())));
                    tcc.append(pname);
                }
            });

            world.getServer().getPlayerList().getPlayers().forEach(player -> {
                player.sendMessage(tcc, player.getUniqueID());
                player.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE, tc, 10, 60, 10));
                player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, player.getPosX(), player.getPosY(), player.getPosZ(), 1.2f, 1));
            });
        });
    }
}
