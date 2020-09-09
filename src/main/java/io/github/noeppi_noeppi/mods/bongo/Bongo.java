package io.github.noeppi_noeppi.mods.bongo;

import com.google.common.collect.ImmutableMap;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.effect.StartingEffects;
import io.github.noeppi_noeppi.mods.bongo.effect.TaskEffects;
import io.github.noeppi_noeppi.mods.bongo.effect.WinEffects;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import io.github.noeppi_noeppi.mods.bongo.network.BongoNetwork;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.task.TaskType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class Bongo extends WorldSavedData {

    public static final String ID = BongoMod.MODID;

    private static Bongo clientInstance;
    private static Minecraft mc = null;

    public static Bongo get(World world) {
        if (!world.isRemote) {
            DimensionSavedDataManager storage = ((ServerWorld) world).getServer().func_241755_D_().getSavedData();
            Bongo bongo = storage.getOrCreate(Bongo::new, ID);
            bongo.world = (ServerWorld) world;
            return bongo;
        } else {
            return clientInstance == null ? new Bongo() : clientInstance;
        }
    }

    public static void updateClient(Bongo bongo, BongoMessageType bongoMessageType) {
        clientInstance = bongo;
        if (mc == null)
            mc = Minecraft.getInstance();
        if (bongoMessageType == BongoMessageType.START || bongoMessageType == BongoMessageType.STOP) {
            if (mc.player != null)
                mc.player.refreshDisplayName();
            if (mc.getResourceManager() instanceof SimpleReloadableResourceManager) {
                SimpleReloadableResourceManager resourceManager = (SimpleReloadableResourceManager) mc.getResourceManager();
                try {
                    Class c = Class.forName("mezz.jei.startup.ClientLifecycleHandler$JeiReloadListener");
                    for (IFutureReloadListener listener : resourceManager.reloadListeners) {
                        if (listener instanceof ISelectiveResourceReloadListener && c.isInstance(listener)) {
                            ((ISelectiveResourceReloadListener) listener).onResourceManagerReload(resourceManager);
                        }
                    }
                } catch (ClassNotFoundException ignore) {
                } catch (Throwable e) {
                    BongoMod.LOGGER.warn("Could not reload JEI item list: ", e);
                }
            }
        }
    }

    private ServerWorld world;

    private final Map<DyeColor, Team> teams;
    private final List<Task> items;
    private boolean active;
    private boolean running;
    private boolean teamWon;
    private long runningSince = 0;
    private long ranUntil = 0;

    public Bongo() {
        this(ID);
    }

    public Bongo(String name) {
        super(name);
        world = null;

        if (!ID.equals(name))
            throw new IllegalStateException("A Bongo must be created with the id '" + ID + "' but got '" + name + "'.");

        ImmutableMap.Builder<DyeColor, Team> teamBuilder = ImmutableMap.builder();
        for (DyeColor dc : DyeColor.values()) {
            teamBuilder.put(dc, new Team(this, dc));
        }
        teams = teamBuilder.build();
        items = new ArrayList<>();
        for (int i = 0; i < 25; i++)
            items.add(Task.empty());
        active = false;
        running = false;
        teamWon = false;
    }

    public Team getTeam(DyeColor color) {
        return teams.get(color);
    }

    @Nullable
    public Team getTeam(PlayerEntity player) {
        return getTeam(player.getGameProfile().getId());
    }

    @Nullable
    public Team getTeam(UUID uid) {
        for (Team team : teams.values()) {
            if (team.hasPlayer(uid))
                return team;
        }
        return null;
    }

    public Set<Team> getTeams() {
        return Collections.unmodifiableSet(new HashSet<>(teams.values()));
    }

    public boolean active() {
        return active;
    }

    public void activate() {
        this.active = true;
        this.running = false;
        this.teamWon = false;
        if (world != null) {
            for (PlayerEntity player : world.getServer().getPlayerList().getPlayers())
                player.refreshDisplayName();
        }
        markDirty();
    }

    public boolean running() {
        return running;
    }

    public void start() {
        this.active = true;
        this.running = true;
        this.teamWon = false;
        this.runningSince = System.currentTimeMillis();
        this.ranUntil = 0;
        Set<UUID> uids = new HashSet<>();
        for (Team team : teams.values()) {
            team.clearBackPack(true);
            team.resetCompleted(true);
            uids.addAll(team.getPlayers());
        }
        if (world != null) {
            StartingEffects.callWorldEffects(this, world);
            world.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (uids.contains(player.getGameProfile().getId()))
                    StartingEffects.callPlayerEffects(this, player);
            });
        }
        markDirty(true);
        if (world != null) {
            BongoNetwork.updateBongo(world, BongoMessageType.START);
        }
    }

    public void stop() {
        this.active = false;
        this.running = false;
        this.teamWon = false;
        markDirty(true);
        if (world != null) {
            for (PlayerEntity player : world.getServer().getPlayerList().getPlayers())
                player.refreshDisplayName();
            BongoNetwork.updateBongo(world, BongoMessageType.STOP);
        }
    }

    public boolean won() {
        return teamWon;
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        nbt.putBoolean("active", active);
        nbt.putBoolean("running", running);
        nbt.putBoolean("teamWon", teamWon);
        nbt.putLong("runningSince", runningSince);
        nbt.putLong("ranUntil", ranUntil);
        for (DyeColor dc : DyeColor.values()) {
            nbt.put(dc.getString(), getTeam(dc).serializeNBT());
        }

        ListNBT itemList = new ListNBT();
        for (Task item : items) {
            itemList.add(item.serializeNBT());
        }
        nbt.put("items", itemList);
        return nbt;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        active = nbt.getBoolean("active");
        running = nbt.getBoolean("running");
        teamWon = nbt.getBoolean("teamWon");
        runningSince = nbt.getLong("runningSince");
        ranUntil = nbt.getLong("ranUntil");
        for (DyeColor dc : DyeColor.values()) {
            if (nbt.contains(dc.getString(), Constants.NBT.TAG_COMPOUND)) {
                getTeam(dc).deserializeNBT(nbt.getCompound(dc.getString()));
            }
        }
        if (nbt.contains("items", Constants.NBT.TAG_LIST)) {
            ListNBT itemList = nbt.getList("items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.size(); i++) {
                if (i < itemList.size()) {
                    items.get(i).deserializeNBT(itemList.getCompound(i));
                } else {
                    items.set(i, Task.empty());
                }
            }
        } else {
            clearItems();
        }
    }
    
    public void reset() {
        for (Team team : teams.values())
            team.reset(true);

        active = false;
        running = false;
        runningSince = 0;
        ranUntil = 0;
        clearItems(true);
        markDirty(); // only call markDirty once
    }

    public void clearItems() {
        clearItems(false);
    }

    public void clearItems(boolean suppressBingoSync) {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, Task.empty());
        }
        markDirty(suppressBingoSync);
    }

    public Task task(int slot) {
        Task task = items.get(slot);
        if (task == null)
            return Task.empty();
        return task;
    }

    public List<Task> tasks() {
        return Collections.unmodifiableList(items);
    }

    public <T> void checkCompleted(TaskType<T> type, PlayerEntity player, T compare) {
        if (!running)
            return;
        Team team = getTeam(player);
        if (team != null) {
            for (int i = 0; i < items.size(); i++) {
                if (!team.completed(i) && task(i).getType() == type && items.get(i).shouldComplete(player, compare)) {
                    team.complete(i);
                    if (player instanceof ServerPlayerEntity) {
                        TaskEffects.callPlayerEffects(this, (ServerPlayerEntity) player, task(i));
                    }
                }
            }
        }
        checkWin();
    }

    public void checkWin() {
        for (Team team : teams.values()) {
            wincheck: for (int[] win : WIN_VALUES) {
                for (int slot : win) {
                    if (!team.completed(slot))
                        continue wincheck;
                }
                running = false;
                teamWon = true;
                if (world != null) {
                    WinEffects.callWorldEffects(this, world, team);
                }
                ranUntil = System.currentTimeMillis();
                markDirty();
                return;
            }
        }
    }

    public long runningSince() {
        return runningSince;
    }

    public long ranUntil() {
        return ranUntil;
    }

    public void markDirty(boolean suppressBingoSync) {
        super.markDirty();
        if (world != null && !suppressBingoSync) {
            BongoNetwork.updateBongo(world);
        }
    }

    @Override
    public void markDirty() {
        this.markDirty(false);
    }

    public void setTasks(List<Task> tasks) {
        for (int i = 0; i < 25; i++) {
            items.set(i, tasks.get(i).copy());
            if (world != null) {
                tasks.get(i).syncToClient(world.getServer(), null);
            }
        }
        markDirty();
    }

    private static final int[][] WIN_VALUES = new int[][]{
            { xy(0, 0), xy(0, 1), xy(0, 2), xy(0, 3), xy(0, 4) },
            { xy(1, 0), xy(1, 1), xy(1, 2), xy(1, 3), xy(1, 4) },
            { xy(2, 0), xy(2, 1), xy(2, 2), xy(2, 3), xy(2, 4) },
            { xy(3, 0), xy(3, 1), xy(3, 2), xy(3, 3), xy(3, 4) },
            { xy(4, 0), xy(4, 1), xy(4, 2), xy(4, 3), xy(4, 4) },
            { xy(0, 0), xy(1, 0), xy(2, 0), xy(3, 0), xy(4, 0) },
            { xy(0, 1), xy(1, 1), xy(2, 1), xy(3, 1), xy(4, 1) },
            { xy(0, 2), xy(1, 2), xy(2, 2), xy(3, 2), xy(4, 2) },
            { xy(0, 3), xy(1, 3), xy(2, 3), xy(3, 3), xy(4, 3) },
            { xy(0, 4), xy(1, 4), xy(2, 4), xy(3, 4), xy(4, 4) },
            { xy(0, 0), xy(1, 1), xy(2, 2), xy(3, 3), xy(4, 4) },
            { xy(0, 4), xy(1, 3), xy(2, 2), xy(3, 1), xy(4, 0) }
    };

    private static int xy(int x, int y) {
        return x + (5 * y);
    }
}
