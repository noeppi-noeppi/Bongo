package io.github.noeppi_noeppi.mods.bongo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.noeppi_noeppi.mods.bongo.compat.JeiIntegration;
import io.github.noeppi_noeppi.mods.bongo.compat.MineMentionIntegration;
import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.data.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.event.*;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.task.TaskType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Bongo extends SavedData {

    public static final String ID = BongoMod.getInstance().modid;

    private static Bongo clientInstance;
    private static Minecraft mc = null;

    public static Bongo get(net.minecraft.world.level.Level level) {
        if (!level.isClientSide) {
            DimensionDataStorage storage = ((ServerLevel) level).getServer().overworld().getDataStorage();
            Bongo bongo = storage.computeIfAbsent(Bongo::new, Bongo::new, ID);
            bongo.level = ((ServerLevel) level).getServer().overworld();
            return bongo;
        } else {
            return clientInstance == null ? new Bongo() : clientInstance;
        }
    }

    public static void updateClient(Bongo bongo, BongoMessageType bongoMessageType) {
        clientInstance = bongo;
        if (mc == null) {
            mc = Minecraft.getInstance();
        }
        if (bongoMessageType == BongoMessageType.START || bongoMessageType == BongoMessageType.STOP
                || bongoMessageType == BongoMessageType.FORCE) {
            if (mc.player != null) {
                mc.player.refreshDisplayName();
            }
            if (ClientConfig.addItemTooltips.get()) {
                bongo.updateTooltipPredicate();
            }
            if (ClientConfig.modifyJeiBookamrks.get()) {
                if (bongo.running()) {
                    Set<ItemStack> stacks = new HashSet<>();
                    Set<ResourceLocation> advancements = new HashSet<>();
                    for (Task task : bongo.items) {
                        stacks.addAll(task.bookmarkStacks());
                        advancements.addAll(task.bookmarkAdvancements());
                    }
                    JeiIntegration.setBookmarks(stacks, advancements);
                } else {
                    JeiIntegration.setBookmarks(ImmutableSet.of(), ImmutableSet.of());
                }
            }
        }
    }

    private ServerLevel level;

    private final Map<DyeColor, Team> teams;
    private final List<Task> items;
    private Predicate<ItemStack> tooltipPredicate;
    private final List<UUID> playersInTcMode = new ArrayList<>();
    private GameSettings settings = GameSettings.DEFAULT;
    private boolean active;
    private boolean running;
    private boolean teamWon;
    private long runningSince = 0;
    private long ranUntil = 0;
    private boolean hasTimeLimit = false;
    private long runningUntil = 0;
    private int taskAmountOutOfTime = -1;
    private DyeColor winningTeam;

    public Bongo() {
        level = null;
        
        ImmutableMap.Builder<DyeColor, Team> teamBuilder = ImmutableMap.builder();
        for (DyeColor dc : DyeColor.values()) {
            teamBuilder.put(dc, new Team(this, dc));
        }
        teams = teamBuilder.build();
        items = new ArrayList<>();
        for (int i = 0; i < 25; i++)
            items.add(Task.empty());
        tooltipPredicate = null;
        active = false;
        running = false;
        teamWon = false;
        winningTeam = null;
    }
    
    public Bongo(CompoundTag nbt) {
        this();
        this.load(nbt);
    }
    

    public Team getTeam(DyeColor color) {
        return teams.get(color);
    }

    @Nullable
    public Team getTeam(Player player) {
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
        return Set.copyOf(teams.values());
    }

    public boolean active() {
        return active;
    }

    public void activate() {
        this.active = true;
        this.running = false;
        this.teamWon = false;
        this.hasTimeLimit = settings.maxTime >= 0;
        if (level != null) {
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                player.refreshDisplayName();
                player.refreshTabListName();
            }
        }
        setDirty();
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
        if (this.hasTimeLimit) {
            this.runningUntil = System.currentTimeMillis() + (1000l * settings.maxTime);
        } else {
            this.runningUntil = 0;
        }
        this.taskAmountOutOfTime = -1;
        Set<UUID> uids = new HashSet<>();
        for (Team team : teams.values()) {
            settings.fillBackPackInventory(team, true);
            team.resetCompleted(true);
            team.resetLocked(true);
            team.teleportsLeft(getSettings().teleportsPerTeam);
            uids.addAll(team.getPlayers());
        }
        if (level != null) {
            BongoPickLevelEvent event = new BongoPickLevelEvent(this, level);
            MinecraftForge.EVENT_BUS.post(event);
            ServerLevel gameLevel = event.getLevel();
            MinecraftForge.EVENT_BUS.post(new BongoStartEvent.Level(this, gameLevel));
            level.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (uids.contains(player.getGameProfile().getId())) {
                    MinecraftForge.EVENT_BUS.post(new BongoStartEvent.Player(this, gameLevel, player));
                }
            });
            Random random = new Random();
            for (Team team : getTeams()) {
                List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers().stream().filter(team::hasPlayer).collect(ImmutableList.toImmutableList());
                if (!players.isEmpty()) {
                    settings.getTeleporter().teleportTeam(this, gameLevel, team, players, BlockPos.ZERO, settings.teleportRadius, random);
                    MinecraftForge.EVENT_BUS.post(new BongoTeleportedEvent(this, gameLevel, team, settings.getTeleporter(), players));
                }
            }
        }
        setChanged(true);
        if (level != null) {
            BongoMod.getNetwork().updateBongo(level, BongoMessageType.START);
        }
    }
    
    public void stop() {
        Set<UUID> uids = new HashSet<>();
        for (Team team : teams.values()) {
            uids.addAll(team.getPlayers());
        }
        this.active = false;
        this.running = false;
        this.teamWon = false;
        this.winningTeam = null;
        this.settings = GameSettings.DEFAULT;
        playersInTcMode.clear();
        setChanged(true);
        if (level != null) {
            MinecraftForge.EVENT_BUS.post(new BongoStopEvent.Level(this, this.level));
            for (UUID uid : uids) {
                ServerPlayer player = level.getServer().getPlayerList().getPlayer(uid);
                if (player != null) {
                    MinecraftForge.EVENT_BUS.post(new BongoStopEvent.Player(this, player.getLevel(), player));
                    updateMentions(player);
                    player.refreshDisplayName();
                    player.refreshTabListName();
                }
            }
            BongoMod.getNetwork().updateBongo(level, BongoMessageType.STOP);
        }
    }

    public boolean won() {
        return teamWon;
    }

    public Team winningTeam() {
        return teams.get(winningTeam);
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        compound.putBoolean("active", active);
        compound.putBoolean("running", running);
        compound.putBoolean("teamWon", teamWon);
        compound.putLong("runningSince", runningSince);
        compound.putLong("ranUntil", ranUntil);
        compound.putBoolean("hasTimeLimit", hasTimeLimit);
        compound.putLong("runningUntil", runningUntil);
        compound.putInt("taskAmountOutOfTime", taskAmountOutOfTime);

        if (winningTeam != null)
            compound.putInt("winningTeam", winningTeam.ordinal());

        compound.putString("settings_id", settings.id.toString());
        compound.put("settings", settings.getTag());

        for (DyeColor dc : DyeColor.values()) {
            compound.put(dc.getSerializedName(), getTeam(dc).serializeNBT());
        }

        ListTag itemList = new ListTag();
        for (Task item : items) {
            itemList.add(item.serializeNBT());
        }
        compound.put("items", itemList);

        ListTag tcPlayers = new ListTag();
        for (UUID uid : playersInTcMode) {
            CompoundTag playerNbt = new CompoundTag();
            playerNbt.putUUID("player", uid);
            tcPlayers.add(playerNbt);
        }
        compound.put("teamchat", tcPlayers);

        return compound;
    }

    public void load(@Nonnull CompoundTag nbt) {
        active = nbt.getBoolean("active");
        running = nbt.getBoolean("running");
        teamWon = nbt.getBoolean("teamWon");
        runningSince = nbt.getLong("runningSince");
        ranUntil = nbt.getLong("ranUntil");
        hasTimeLimit = nbt.getBoolean("hasTimeLimit");
        runningUntil = nbt.getLong("runningUntil");
        taskAmountOutOfTime = nbt.getInt("taskAmountOutOfTime");
        
        if (nbt.contains("winningTeam")) {
            winningTeam = DyeColor.values()[nbt.getInt("winningTeam")];
        } else {
            winningTeam = null;
        }

        if (nbt.contains("settings_id", Tag.TAG_STRING) && nbt.contains("settings", Tag.TAG_COMPOUND)) {
            settings = new GameSettings(new ResourceLocation(nbt.getString("settings_id")), nbt.getCompound("settings"));
        } else {
            settings = GameSettings.DEFAULT;
        }
        for (DyeColor dc : DyeColor.values()) {
            if (nbt.contains(dc.getSerializedName(), Tag.TAG_COMPOUND)) {
                getTeam(dc).deserializeNBT(nbt.getCompound(dc.getSerializedName()));
            }
        }

        if (nbt.contains("items", Tag.TAG_LIST)) {
            ListTag itemList = nbt.getList("items", Tag.TAG_COMPOUND);
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

        playersInTcMode.clear();
        if (nbt.contains("teamchat", Tag.TAG_LIST)) {
            ListTag tcPlayers = nbt.getList("teamchat", Tag.TAG_COMPOUND);
            for (int i = 0; i < tcPlayers.size(); i++) {
                CompoundTag playerNbt = tcPlayers.getCompound(i);
                UUID uid = playerNbt.getUUID("player");
                playersInTcMode.add(uid);
            }
        }
    }

    public void reset() {
        for (Team team : teams.values())
            team.reset(true);

        active = false;
        running = false;
        runningSince = 0;
        ranUntil = 0;
        teamWon = false;
        winningTeam = null;
        clearItems(true);
        playersInTcMode.clear();
        settings = GameSettings.DEFAULT;
        setDirty();
    }

    public GameSettings getSettings() {
        return settings == null ? GameSettings.DEFAULT : settings;
    }

    public void setSettings(GameSettings settings, boolean suppressBingoSync) {
        this.settings = settings == null ? GameSettings.DEFAULT : settings;
        setChanged(suppressBingoSync);
    }

    public void clearItems() {
        clearItems(false);
    }

    public void clearItems(boolean suppressBingoSync) {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, Task.empty());
        }
        setChanged(suppressBingoSync);
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

    // Checks whether this player can complete tasks. This should be checked before for example looping
    // through evey item in the players inventory.
    public boolean canCompleteTasks(Player player) {
        return running() && getTeam(player) != null;
    }
    
    public <C> void checkCompleted(TaskType<?, C> type, Player player, C compare) {
        if (!running)
            return;
        Team team = getTeam(player);
        if (team != null) {
            for (int i = 0; i < items.size(); i++) {
                if (!team.completed(i) && !team.locked(i) && task(i).getType() == type && items.get(i).shouldComplete(player, compare)) {
                    team.complete(i);
                    if (getSettings().consumeItems) {
                        task(i).consumeItem(player, compare);
                    }
                    if (getSettings().lockout) {
                        for (Team t : teams.values()) {
                            if (!t.completed(i)) {
                                t.lock(i);
                            }
                        }
                    }
                    if (player instanceof ServerPlayer) {
                        MinecraftForge.EVENT_BUS.post(new BongoTaskEvent(this, ((ServerPlayer) player).getLevel(), (ServerPlayer) player, task(i)));
                    }
                }
            }
        }
        checkWin();
    }

    public void checkWin() {
        if (hasTimeLimit && System.currentTimeMillis() > runningUntil) {
            if (taskAmountOutOfTime >= 0) {
                for (Team team : teams.values()) {
                    if (team.completionAmount() >= taskAmountOutOfTime) {
                        setWin(team);
                        return;
                    }
                }
            } else {
                int max = 0;
                for (Team team : teams.values()) {
                    int amount = team.completionAmount();
                    if (amount > max) max = amount;
                }
                Team winning = null;
                for (Team team : teams.values()) {
                    if (team.completionAmount() >= max) {
                        if (winning == null) {
                            winning = team;
                        } else {
                            taskAmountOutOfTime = Math.min(25, max + 1);
                            setDirty();
                            return;
                        }
                    }
                }
                if (winning == null) {
                    taskAmountOutOfTime = Math.min(25, max + 1);
                    setDirty();
                    return;
                } else {
                    setWin(winning);
                    return;
                }
            }
        }
        for (Team team : teams.values()) {
            if (getSettings().winCondition.won(this, team)) {
                setWin(team);
                return;
            }
        }
    }
    
    private void setWin(Team team) {
        running = false;
        teamWon = true;
        winningTeam = team.color;
        if (level != null) {
            MinecraftForge.EVENT_BUS.post(new BongoWinEvent(this, level, team));
        }
        ranUntil = System.currentTimeMillis();
        playersInTcMode.clear();
        setDirty();
    }

    public long runningSince() {
        return runningSince;
    }

    public long ranUntil() {
        return ranUntil;
    }

    public void setChanged(boolean suppressBingoSync) {
        super.setDirty();
        if (level != null && !suppressBingoSync) {
            BongoMod.getNetwork().updateBongo(level);
        }
    }

    public boolean toggleTeamChat(Player player) {
        return toggleTeamChat(player.getGameProfile().getId());
    }

    public boolean toggleTeamChat(UUID uid) {
        if (playersInTcMode.contains(uid)) {
            playersInTcMode.remove(uid);
            setDirty();
            return false;
        } else {
            playersInTcMode.add(uid);
            setDirty();
            return true;
        }
    }

    public boolean teamChat(Player player) {
        return teamChat(player.getGameProfile().getId());
    }

    public boolean teamChat(UUID uid) {
        return playersInTcMode.contains(uid);
    }

    @Override
    public void setDirty() {
        this.setChanged(false);
    }

    public void setTasks(List<Task> tasks) {
        for (int i = 0; i < 25; i++) {
            items.set(i, tasks.get(i).copy());
            if (level != null) {
                tasks.get(i).syncToClient(level.getServer(), null);
            }
        }
        updateTooltipPredicate();
        setChanged(true);
        if (level != null) {
            BongoMod.getNetwork().updateBongo(level, BongoMessageType.CREATE);
        }
    }

    private void updateTooltipPredicate() {
        if (level == null) {
            // We cache the predicates to reduce lag
            List<Predicate<ItemStack>> predicates = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                predicates.add(task(i).bongoTooltipStack());
            }
            tooltipPredicate = stack -> {
                for (Predicate<ItemStack> predicate : predicates) {
                    if (predicate.test(stack))
                        return true;
                }
                return false;
            };
        }
    }

    public <T> Stream<T> getElementsOf(TaskType<T, ?> type) {
        return items.stream().map(task -> task.getElement(type)).filter(Objects::nonNull);
    }

    public boolean isTooltipStack(ItemStack stack) {
        if (tooltipPredicate == null)
            updateTooltipPredicate();
        return !stack.isEmpty() && tooltipPredicate.test(stack);
    }
    
    public void updateMentions(UUID player) {
        if (this.level != null) {
            ServerPlayer entity = this.level.getServer().getPlayerList().getPlayer(player);
            if (entity != null) {
                updateMentions(entity);
            }
        }
    }
    
    public void updateMentions(ServerPlayer player) {
        if (ModList.get().isLoaded("minemention")) {
            MineMentionIntegration.availabilityChange(player);
        }
    }
    
    public boolean hasTimeLimit() {
        return hasTimeLimit;
    }
    
    public long runningUntil() {
        return runningUntil;
    }
    
    public int tasksForWin() {
        return taskAmountOutOfTime;
    }
}
