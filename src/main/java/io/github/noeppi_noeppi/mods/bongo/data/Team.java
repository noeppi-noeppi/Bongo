package io.github.noeppi_noeppi.mods.bongo.data;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.compat.MineMentionIntegration;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class Team {

    private final Bongo bongo;
    public final DyeColor color;

    private int completed;
    private int locked;
    private int teleportsLeft;
    private final List<UUID> players;
    private final ItemStackHandler backpack;
    
    public Team(Bongo bongo, DyeColor color) {
        this.bongo = bongo;
        this.color = color;
        this.completed = 0;
        this.locked = 0;
        teleportsLeft = 0;
        this.players = new ArrayList<>();
        this.backpack = new ItemStackHandler(27);
    }

    public IFormattableTextComponent getName() {
        return new TranslationTextComponent("bongo.team." + color.getString()).mergeStyle(Util.getTextFormatting(color));
    }

    public Style getFormatting() {
        return Util.getTextFormatting(color);
    }

    public boolean completed(int slot) {
        return (completed & (1 << (slot % 25))) > 0;
    }

    public void complete(int slot) {
        completed |= (1 << (slot % 25));
        bongo.markDirty();
    }
    
    public int completionAmount() {
        int completed = 0;
        for (int i = 0 ; i < 25; i++) {
            completed += completed(i) ? 1 : 0;
        }
        return completed;
    }

    public boolean locked(int slot) {
        return (locked & (1 << (slot % 25))) > 0;
    }

    public void lock(int slot) {
        locked |= (1 << (slot % 25));
        bongo.markDirty();
    }

    public List<UUID> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public boolean hasPlayer(UUID uid) {
        return players.contains(uid);
    }

    public boolean hasPlayer(PlayerEntity player) {
        return players.contains(player.getGameProfile().getId());
    }

    private void addPlayer(UUID uid) {
        for (Team team : bongo.getTeams()) {
            team.removePlayer(uid);
        }
        players.add(uid);
        bongo.markDirty();
    }

    public void addPlayer(PlayerEntity player) {
        addPlayer(player.getGameProfile().getId());
        if (player instanceof ServerPlayerEntity) this.bongo.updateMentions((ServerPlayerEntity) player);
        player.refreshDisplayName();
    }

    private void removePlayer(UUID uid) {
        players.remove(uid);
        bongo.markDirty();
    }

    public void removePlayer(PlayerEntity player) {
        removePlayer(player.getGameProfile().getId());
        if (player instanceof ServerPlayerEntity) this.bongo.updateMentions((ServerPlayerEntity) player);
        player.refreshDisplayName();
    }

    /**
     * IMPORTANT: You must mark the team dirty afterwards.
     */
    public IItemHandlerModifiable getBackPack() {
        return backpack;
    }

    public void openBackPack(PlayerEntity player) {
        if (!player.getEntityWorld().isRemote) {
            player.openContainer(new BackpackContainerProvider(getName(), backpack, bongo::markDirty));
        }
    }

    public int teleportsLeft() {
        return teleportsLeft;
    }

    public void teleportsLeft(int teleportsLeft) {
        this.teleportsLeft = teleportsLeft;
        bongo.markDirty();
    }

    public boolean consumeTeleport() {
        if (teleportsLeft < 0) {
            return true;
        } else if (teleportsLeft > 0) {
            teleportsLeft -= 1;
            bongo.markDirty();
            return true;
        } else {
            return false;
        }
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("completed", completed);
        nbt.putInt("locked", locked);
        nbt.putInt("teleportsLeft", teleportsLeft);

        ListNBT playerList = new ListNBT();
        for (UUID uuid : players) {
            CompoundNBT uuidTag = new CompoundNBT();
            uuidTag.putUniqueId("player", uuid);
            playerList.add(uuidTag);
        }
        nbt.put("players", playerList);
        nbt.put("backpack", backpack.serializeNBT());
        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        completed = nbt.getInt("completed");
        locked = nbt.getInt("locked");
        teleportsLeft = nbt.getInt("teleportsLeft");

        if (nbt.contains("players", Constants.NBT.TAG_LIST)) {
            ListNBT playerList = nbt.getList("players", Constants.NBT.TAG_COMPOUND);
            players.clear();
            for (int i = 0;i < playerList.size(); i++) {
                players.add(playerList.getCompound(i).getUniqueId("player"));
            }
        }

        if (nbt.contains("backpack",Constants.NBT.TAG_COMPOUND)) {
            backpack.deserializeNBT(nbt.getCompound("backpack"));
        } else {
            backpack.deserializeNBT(new CompoundNBT());
        }
    }

    public void reset(boolean suppressBingoSync) {
        completed = 0;
        locked = 0;
        players.forEach(bongo::updateMentions);
        players.clear();
        teleportsLeft = 0;
        clearBackPack(true);
        bongo.markDirty(suppressBingoSync);
    }

    public void reset() {
        reset(false);
    }

    public void resetCompleted(boolean suppressBingoSync) {
        completed = 0;
        bongo.markDirty(suppressBingoSync);
    }

    public void resetLocked(boolean suppressBingoSync) {
        locked = 0;
        bongo.markDirty(suppressBingoSync);
    }

    public boolean lockRandomTask() {
        int notCompletedTasks = 0;
        for (int i = 0; i < 25; i++) {
            if (!completed(i) && !locked((i)))
                notCompletedTasks += 1;
        }
        int task = new Random().nextInt(notCompletedTasks);
        for (int i = 0; i < 25; i++) {
            if (!completed(i) && !locked((i))) {
                if (task == 0) {
                    lock(i);
                    return true;
                } else {
                    task -= 1;
                }
            }
        }
        return false;
    }

    public void clearBackPack(boolean suppressBingoSync) {
        for (int slot = 0; slot < backpack.getSlots(); slot++) {
            backpack.setStackInSlot(slot, ItemStack.EMPTY);
        }
        bongo.markDirty(suppressBingoSync);
    }

    public void clearBackPack() {
        clearBackPack(false);
    }

    public void clearPlayers() {
        players.clear();
        bongo.markDirty();
    }

    public void resetCompleted() {
        resetCompleted(false);
    }

    public void resetLocked() {
        resetLocked(false);
    }
    
    public void markDirty(boolean suppressBingoSync) {
        bongo.markDirty(suppressBingoSync);
    }
    
    public void markDirty() {
        bongo.markDirty();
    }
}
