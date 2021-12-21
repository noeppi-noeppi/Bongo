package io.github.noeppi_noeppi.mods.bongo.data;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Team {

    private final Bongo bongo;
    public final DyeColor color;

    private int completed;
    private int locked;
    private int teleportsLeft;
    private boolean redeemedEmergency;
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

    public MutableComponent getName() {
        return new TranslatableComponent("bongo.team." + color.getSerializedName()).withStyle(Util.getTextFormatting(color));
    }

    public Style getFormatting() {
        return Util.getTextFormatting(color);
    }

    public boolean completed(int slot) {
        return (completed & (1 << (slot % 25))) > 0;
    }

    public void complete(int slot) {
        completed |= (1 << (slot % 25));
        bongo.setDirty();
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
        bongo.setDirty();
    }

    public List<UUID> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public boolean hasPlayer(UUID uid) {
        return players.contains(uid);
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player.getGameProfile().getId());
    }

    private void addPlayer(UUID uid) {
        for (Team team : bongo.getTeams()) {
            team.removePlayer(uid);
        }
        players.add(uid);
        bongo.setDirty();
    }

    public void addPlayer(Player player) {
        addPlayer(player.getGameProfile().getId());
        if (player instanceof ServerPlayer) this.bongo.updateMentions((ServerPlayer) player);
        player.refreshDisplayName();
        if (player instanceof ServerPlayer) {
            ((ServerPlayer) player).refreshTabListName();
        }
    }

    private void removePlayer(UUID uid) {
        players.remove(uid);
        bongo.setDirty();
    }

    public void removePlayer(Player player) {
        removePlayer(player.getGameProfile().getId());
        if (player instanceof ServerPlayer) this.bongo.updateMentions((ServerPlayer) player);
        player.refreshDisplayName();
        if (player instanceof ServerPlayer) {
            ((ServerPlayer) player).refreshTabListName();
        }
    }

    /**
     * IMPORTANT: You must mark the team dirty afterwards.
     */
    public IItemHandlerModifiable getBackPack() {
        return backpack;
    }

    public void openBackPack(Player player) {
        if (!player.getCommandSenderWorld().isClientSide) {
            player.openMenu(new BackpackContainerProvider(getName(), backpack, bongo::setDirty));
        }
    }

    public int teleportsLeft() {
        return teleportsLeft;
    }

    public void teleportsLeft(int teleportsLeft) {
        this.teleportsLeft = teleportsLeft;
        bongo.setDirty();
    }
    
    public boolean redeemedEmergency() {
        return redeemedEmergency;
    }
    
    public void redeemedEmergency(boolean redeemedEmergency) {
        this.redeemedEmergency = redeemedEmergency;
        setDirty();
    }

    public boolean consumeTeleport() {
        if (teleportsLeft < 0) {
            return true;
        } else if (teleportsLeft > 0) {
            teleportsLeft -= 1;
            setDirty();
            return true;
        } else {
            return false;
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("completed", completed);
        nbt.putInt("locked", locked);
        nbt.putInt("teleportsLeft", teleportsLeft);
        nbt.putBoolean("redeemedEmergency", redeemedEmergency);

        ListTag playerList = new ListTag();
        for (UUID uuid : players) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putUUID("player", uuid);
            playerList.add(uuidTag);
        }
        nbt.put("players", playerList);
        nbt.put("backpack", backpack.serializeNBT());
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        completed = nbt.getInt("completed");
        locked = nbt.getInt("locked");
        teleportsLeft = nbt.getInt("teleportsLeft");
        redeemedEmergency = nbt.getBoolean("redeemedEmergency");

        if (nbt.contains("players", Tag.TAG_LIST)) {
            ListTag playerList = nbt.getList("players", Tag.TAG_COMPOUND);
            players.clear();
            for (int i = 0;i < playerList.size(); i++) {
                players.add(playerList.getCompound(i).getUUID("player"));
            }
        }

        if (nbt.contains("backpack", Tag.TAG_COMPOUND)) {
            backpack.deserializeNBT(nbt.getCompound("backpack"));
        } else {
            backpack.deserializeNBT(new CompoundTag());
        }
    }

    public void reset(boolean suppressBingoSync) {
        completed = 0;
        locked = 0;
        players.forEach(bongo::updateMentions);
        players.clear();
        teleportsLeft = 0;
        redeemedEmergency = false;
        clearBackPack(true);
        bongo.setChanged(suppressBingoSync);
    }

    public void reset() {
        reset(false);
    }

    public void resetCompleted(boolean suppressBingoSync) {
        completed = 0;
        bongo.setChanged(suppressBingoSync);
    }

    public void resetLocked(boolean suppressBingoSync) {
        locked = 0;
        bongo.setChanged(suppressBingoSync);
    }

    public boolean lockRandomTask() {
        return lockRandomTasks(1);
    }
    
    public boolean lockRandomTasks(int amount) {
        ArrayList<Integer> lockableTasks = IntStream.range(0, 25).boxed()
                .filter(i -> !completed(i) && !locked((i)))
                .collect(Collectors.toCollection(ArrayList::new));
        if (lockableTasks.size() < amount) {
            return false;
        } else {
            Random random = new Random();
            for (int i = 0; i < amount; i++) {
                int lockIdx = random.nextInt(lockableTasks.size());
                int taskId = lockableTasks.remove(lockIdx);
                lock(taskId);
            }
            return true;
        }
    }

    public void clearBackPack(boolean suppressBingoSync) {
        for (int slot = 0; slot < backpack.getSlots(); slot++) {
            backpack.setStackInSlot(slot, ItemStack.EMPTY);
        }
        bongo.setChanged(suppressBingoSync);
    }

    public void clearBackPack() {
        clearBackPack(false);
    }

    public void clearPlayers() {
        players.clear();
        bongo.setDirty();
    }

    public void resetCompleted() {
        resetCompleted(false);
    }

    public void resetLocked() {
        resetLocked(false);
    }
    
    public void setChanged(boolean suppressBingoSync) {
        bongo.setChanged(suppressBingoSync);
    }
    
    public void setDirty() {
        bongo.setDirty();
    }
}
