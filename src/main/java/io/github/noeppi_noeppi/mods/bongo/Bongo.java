package io.github.noeppi_noeppi.mods.bongo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.noeppi_noeppi.mods.bongo.event.*;
import io.github.noeppi_noeppi.mods.bongo.compat.JeiIntegration;
import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.data.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.task.TaskType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Bongo extends WorldSavedData {

    public static final String ID = BongoMod.getInstance().modid;

    private static Bongo clientInstance;
    private static Minecraft mc = null;

    public static Bongo get(World world) {
        if (!world.isRemote) {
            DimensionSavedDataManager storage = ((ServerWorld) world).getServer().func_241755_D_().getSavedData();
            Bongo bongo = storage.getOrCreate(Bongo::new, ID);
            bongo.world = ((ServerWorld) world).getServer().func_241755_D_();
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
                if (ClientConfig.addItemTooltips.get()) {
                    JeiIntegration.reloadJeiTooltips();
                    if (ClientConfig.modifyJeiBookamrks.get()) {
                        if (bongo.running()) {
                            NonNullList<ItemStack> stacks = NonNullList.create();
                            ForgeRegistries.ITEMS.getValues().forEach(item -> {
                                ItemGroup group = item.getGroup();
                                if (group != null) {
                                    item.fillItemGroup(group, stacks);
                                } else {
                                    stacks.add(new ItemStack(item));
                                }
                            });
                            JeiIntegration.setBookmarks(stacks.stream().filter(bongo.tooltipPredicate));
                        } else {
                            JeiIntegration.setBookmarks(Stream.empty());
                        }
                    }
                }
            }
        }
    }

    private ServerWorld world;

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
    private DyeColor winningTeam;

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
        tooltipPredicate = null;
        active = false;
        running = false;
        teamWon = false;
        winningTeam = null;
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
            settings.fillBackPackInventory(team);
            team.resetCompleted(true);
            team.resetLocked(true);
            team.teleportsLeft(getSettings().teleportsPerTeam);
            uids.addAll(team.getPlayers());
        }
        if (world != null) {
            BongoPickWorldEvent event = new BongoPickWorldEvent(this, world);
            MinecraftForge.EVENT_BUS.post(event);
            ServerWorld gameWorld = event.getWorld();
            MinecraftForge.EVENT_BUS.post(new BongoStartEvent.World(this, gameWorld));
            world.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (uids.contains(player.getGameProfile().getId())) {
                    MinecraftForge.EVENT_BUS.post(new BongoStartEvent.Player(this, gameWorld, player));
                }
            });
            Random random = new Random();
            for (Team team : getTeams()) {
                //noinspection UnstableApiUsage
                List<ServerPlayerEntity> players = world.getServer().getPlayerList().getPlayers().stream().filter(team::hasPlayer).collect(ImmutableList.toImmutableList());
                if (!players.isEmpty()) {
                    settings.getTeleporter().teleportTeam(this, gameWorld, team, players, BlockPos.ZERO, 10000, random);
                }
            }
        }
        markDirty(true);
        if (world != null) {
            BongoMod.getNetwork().updateBongo(world, BongoMessageType.START);
        }
    }

    private void randomizeTeams(ServerWorld world) {
        Random random = new Random();
        for (Team team : getTeams()) {
            randomizeTeamAround(random, world, team, 0, 0, 10000);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void randomizeTeamAround(Random random, ServerWorld world, Team team, int centerX, int centerZ, int radius) {
        if (team.getPlayers().size() <= 0)
            return;
        int x = centerX + (random.nextInt(2 * radius) - radius);
        int z = centerZ + (random.nextInt(2 * radius) - radius);
        BlockPos.Mutable mpos = new BlockPos.Mutable(x, world.getHeight(), z);
        //noinspection deprecation
        while (mpos.getY() > 5 && world.getBlockState(mpos).isAir(world, mpos))
            mpos.move(Direction.DOWN);
        BlockPos pos = mpos.toImmutable().up();
        world.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (team.hasPlayer(player)) {
                player.teleport(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getRotationYawHead(), 0);
            }
        });
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
        markDirty(true);
        if (world != null) {
            MinecraftForge.EVENT_BUS.post(new BongoStopEvent.World(this, this.world));
            for (ServerPlayerEntity player : world.getServer().getPlayerList().getPlayers()) {
                if (uids.contains(player.getGameProfile().getId())) {
                    MinecraftForge.EVENT_BUS.post(new BongoStopEvent.Player(this, player.getServerWorld(), player));
                }
                player.refreshDisplayName();
            }
            BongoMod.getNetwork().updateBongo(world, BongoMessageType.STOP);
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
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        nbt.putBoolean("active", active);
        nbt.putBoolean("running", running);
        nbt.putBoolean("teamWon", teamWon);
        nbt.putLong("runningSince", runningSince);
        nbt.putLong("ranUntil", ranUntil);

        if (winningTeam != null)
            nbt.putInt("winningTeam", winningTeam.ordinal());

        nbt.putString("settings_id", settings.id.toString());
        nbt.put("settings", settings.getTag());

        for (DyeColor dc : DyeColor.values()) {
            nbt.put(dc.getString(), getTeam(dc).serializeNBT());
        }

        ListNBT itemList = new ListNBT();
        for (Task item : items) {
            itemList.add(item.serializeNBT());
        }
        nbt.put("items", itemList);

        ListNBT tcPlayers = new ListNBT();
        for (UUID uid : playersInTcMode) {
            CompoundNBT playerNbt = new CompoundNBT();
            playerNbt.putUniqueId("player", uid);
            tcPlayers.add(playerNbt);
        }
        nbt.put("teamchat", tcPlayers);

        return nbt;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        active = nbt.getBoolean("active");
        running = nbt.getBoolean("running");
        teamWon = nbt.getBoolean("teamWon");
        runningSince = nbt.getLong("runningSince");
        ranUntil = nbt.getLong("ranUntil");

        if (nbt.contains("winningTeam")) {
            winningTeam = DyeColor.values()[nbt.getInt("winningTeam")];
        } else {
            winningTeam = null;
        }

        if (nbt.contains("settings_id", Constants.NBT.TAG_STRING) && nbt.contains("settings", Constants.NBT.TAG_COMPOUND)) {
            settings = new GameSettings(new ResourceLocation(nbt.getString("settings_id")), nbt.getCompound("settings"));
        } else {
            settings = GameSettings.DEFAULT;
        }
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

        playersInTcMode.clear();
        if (nbt.contains("teamchat", Constants.NBT.TAG_LIST)) {
            ListNBT tcPlayers = nbt.getList("teamchat", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < tcPlayers.size(); i++) {
                CompoundNBT playerNbt = tcPlayers.getCompound(i);
                UUID uid = playerNbt.getUniqueId("player");
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
        markDirty(); // only call markDirty once
    }

    public GameSettings getSettings() {
        return settings == null ? GameSettings.DEFAULT : settings;
    }

    public void setSettings(GameSettings settings, boolean suppressBingoSync) {
        if (settings == null) {
            this.settings = GameSettings.DEFAULT;
        } else {
            this.settings = settings;
        }
        markDirty(suppressBingoSync);
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

    // Checks whether this player can complete tasks. This should be checked before for example looping
    // through evey item in the players inventory.
    public boolean canCompleteTasks(PlayerEntity player) {
        return running() && getTeam(player) != null;
    }
    
    public <T> void checkCompleted(TaskType<T> type, PlayerEntity player, T compare) {
        if (!running)
            return;
        Team team = getTeam(player);
        if (team != null) {
            for (int i = 0; i < items.size(); i++) {
                if (!team.completed(i) && !team.locked(i) && task(i).getType() == type && items.get(i).shouldComplete(player, compare)) {
                    team.complete(i);
                    if (getSettings().consumeItems) {
                        task(i).consumeItem(player);
                    }
                    if (player instanceof ServerPlayerEntity) {
                        MinecraftForge.EVENT_BUS.post(new BongoTaskEvent(this, ((ServerPlayerEntity) player).getServerWorld(), (ServerPlayerEntity) player, task(i)));
                    }
                }
            }
        }
        checkWin();
    }

    public void checkWin() {
        for (Team team : teams.values()) {
            if (getSettings().winCondition.won(this, team)) {
                running = false;
                teamWon = true;
                winningTeam = team.color;
                if (world != null) {
                    MinecraftForge.EVENT_BUS.post(new BongoWinEvent(this, world, team));
                }
                ranUntil = System.currentTimeMillis();
                playersInTcMode.clear();
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
            BongoMod.getNetwork().updateBongo(world);
        }
    }

    public boolean toggleTeamChat(PlayerEntity player) {
        return toggleTeamChat(player.getGameProfile().getId());
    }

    public boolean toggleTeamChat(UUID uid) {
        if (playersInTcMode.contains(uid)) {
            playersInTcMode.remove(uid);
            markDirty();
            return false;
        } else {
            playersInTcMode.add(uid);
            markDirty();
            return true;
        }
    }

    public boolean teamChat(PlayerEntity player) {
        return teamChat(player.getGameProfile().getId());
    }

    public boolean teamChat(UUID uid) {
        return playersInTcMode.contains(uid);
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
        updateTooltipPredicate();
        markDirty(true);
        if (world != null) {
            BongoMod.getNetwork().updateBongo(world, BongoMessageType.CREATE);
        }
    }

    private void updateTooltipPredicate() {
        if (world == null) {
            // We cache the predicates to reduce lagg
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

    public <T> Stream<T> getElementsOf(TaskType<T> type) {
        return items.stream().map(task -> task.getElement(type)).filter(Objects::nonNull);
    }

    public boolean isTooltipStack(ItemStack stack) {
        if (tooltipPredicate == null)
            updateTooltipPredicate();
        return !stack.isEmpty() && tooltipPredicate.test(stack);
    }
}
