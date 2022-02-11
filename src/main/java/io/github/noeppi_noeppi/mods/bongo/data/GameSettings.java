package io.github.noeppi_noeppi.mods.bongo.data;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporter;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporterDefault;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class GameSettings {

    public static final ResourceLocation DEFAULT_ID = new ResourceLocation(BongoMod.getInstance().modid, "default");
    public static final ResourceLocation CUSTOM_ID = new ResourceLocation(BongoMod.getInstance().modid, "custom");
    public static final GameSettings DEFAULT = new GameSettings(DEFAULT_ID, new CompoundTag());
    
    public static final Map<ResourceLocation, GameSettings> GAME_SETTINGS = new HashMap<>();
    static {
        GAME_SETTINGS.put(DEFAULT.id, DEFAULT);
    }

    public final ResourceLocation id;
    // Internal use only. Only use in createCustom. Therefore marked deprecated
    private final CompoundTag rawNbt;
    private final CompoundTag nbt;
    
    public final WinCondition winCondition;
    public final boolean invulnerable;
    public final boolean pvp;
    public final boolean friendlyFire;
    public final boolean lockTaskOnDeath;
    public final boolean consumeItems;
    public final int teleportsPerTeam;
    public final int teleportRadius;
    private final List<Pair<EquipmentSlot, ItemStack>> startingInventory;
    private final List<ItemStack> backpackInventory;
    private final List<ItemStack> emergencyItems;
    private final PlayerTeleporter teleporter;
    public final int maxTime;
    public final boolean lockout;
    public final boolean leaderboard;

    public GameSettings(ResourceLocation id, CompoundTag nbt) {
        this.id = id;
        
        if (nbt.contains("winCondition", Tag.TAG_STRING)) {
            winCondition = WinCondition.getWinOrDefault(nbt.getString("winCondition"));
        } else {
            winCondition = WinCondition.DEFAULT;
        }

        if (nbt.contains("invulnerable")) {
            invulnerable = nbt.getBoolean("invulnerable");
        } else {
            invulnerable = true;
        }

        if (nbt.contains("pvp")) {
            pvp = nbt.getBoolean("pvp");
        } else {
            pvp = false;
        }

        if (nbt.contains("friendlyFire")) {
            friendlyFire = nbt.getBoolean("friendlyFire");
        } else {
            friendlyFire = false;
        }

        if (nbt.contains("lockTaskOnDeath")) {
            lockTaskOnDeath = nbt.getBoolean("lockTaskOnDeath");
        } else {
            lockTaskOnDeath = false;
        }

        if (nbt.contains("consumeItems")) {
            consumeItems = nbt.getBoolean("consumeItems");
        } else {
            consumeItems = false;
        }

        if (nbt.contains("teleportsPerTeam")) {
            teleportsPerTeam = nbt.getInt("teleportsPerTeam");
        } else {
            teleportsPerTeam = 0;
        }

        if (nbt.contains("teleportRadius")) {
            teleportRadius = nbt.getInt("teleportRadius");
        } else {
            teleportRadius = 10000;
        }

        startingInventory = new ArrayList<>();
        if (nbt.contains("startingInventory", Tag.TAG_LIST)) {
            Set<EquipmentSlot> usedTypes = new HashSet<>();
            int slotsUsedInMainInventory = 0;
            ListTag list = nbt.getList("startingInventory", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag compound = list.getCompound(i);
                if (!compound.contains("Count")) {
                    compound.putByte("Count", (byte) 1);
                }
                EquipmentSlot slotType = compound.contains("Slot", Tag.TAG_STRING) ? EquipmentSlot.byName(compound.getString("Slot")) : EquipmentSlot.MAINHAND;
                if (slotType == EquipmentSlot.MAINHAND) {
                    if (slotsUsedInMainInventory >= 36) {
                        throw new IllegalStateException("Too many starting items in main inventory. Not more than 36 are allowed.'");
                    } else {
                        slotsUsedInMainInventory += 1;
                    }
                } else {
                    if (usedTypes.contains(slotType)) {
                        throw new IllegalStateException("Slot type that is not 'mainhand' was used multiple times for starting inventory.'");
                    } else {
                        usedTypes.add(slotType);
                    }
                }
                startingInventory.add(Pair.of(slotType, ItemStack.of(compound)));
            }
        }
        
        backpackInventory = new ArrayList<>();
        if (nbt.contains("backpackInventory", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("backpackInventory", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag compound = list.getCompound(i);
                if (!compound.contains("Count")) {
                    compound.putByte("Count", (byte) 1);
                }
                if (backpackInventory.size() > 27) {
                    throw new IllegalStateException("Too many starting items in backpack. Not more than 27 are allowed.'");
                } else {
                    backpackInventory.add(ItemStack.of(compound));
                }
            }
        }
        
        emergencyItems = new ArrayList<>();
        if (nbt.contains("emergencyItems", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("emergencyItems", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag compound = list.getCompound(i);
                if (!compound.contains("Count")) {
                    compound.putByte("Count", (byte) 1);
                }
                emergencyItems.add(ItemStack.of(compound));
            }
        }
        
        if (nbt.contains("teleporter")) {
            String teleporterId = nbt.getString("teleporter");
            PlayerTeleporter tp = PlayerTeleporters.getTeleporter(teleporterId);
            if (tp == null) {
                BongoMod.getInstance().logger.error("Player Teleporter '" + teleporterId + "' not found. Using default.");
                this.teleporter = PlayerTeleporterDefault.INSTANCE;
            } else {
                this.teleporter = tp;
            }
        } else {
            this.teleporter = PlayerTeleporterDefault.INSTANCE;
        }
        
        if (nbt.contains("maxTime")) {
            this.maxTime = nbt.getInt("maxTime");
        } else {
            this.maxTime = -1;
        }
        
        if (nbt.contains("lockout")) {
            this.lockout = nbt.getBoolean("lockout");
        } else {
            this.lockout = false;
        }

        if (nbt.contains("leaderboard")) {
            this.leaderboard = nbt.getBoolean("leaderboard");
        } else {
            this.leaderboard = false;
        }

        this.nbt = new CompoundTag();
        this.nbt.putString("winCondition", winCondition.id);
        this.nbt.putBoolean("invulnerable", invulnerable);
        this.nbt.putBoolean("pvp", pvp);
        this.nbt.putBoolean("friendlyFire", friendlyFire);
        this.nbt.putBoolean("lockTaskOnDeath", lockTaskOnDeath);
        this.nbt.putBoolean("consumeItems", consumeItems);
        this.nbt.putInt("teleportsPerTeam", teleportsPerTeam);
        this.nbt.putInt("teleportRadius", teleportRadius);
        ListTag startingInventoryNBT = new ListTag();
        startingInventory.forEach(stack -> {
            CompoundTag compound = stack.getRight().save(new CompoundTag());
            compound.putString("Slot", stack.getLeft().getName());
            startingInventoryNBT.add(compound);
        });
        this.nbt.put("startingInventory", startingInventoryNBT);
        ListTag backpackInventoryNBT = new ListTag();
        backpackInventory.forEach(stack -> backpackInventoryNBT.add(stack.save(new CompoundTag())));
        this.nbt.put("backpackInventory", backpackInventoryNBT);
        ListTag emergencyItemsNBT = new ListTag();
        emergencyItems.forEach(stack -> emergencyItemsNBT.add(stack.save(new CompoundTag())));
        this.nbt.put("emergencyItems", emergencyItemsNBT);
        this.nbt.putString("teleporter", this.teleporter.getId());
        this.nbt.putInt("maxTime", this.maxTime);
        this.nbt.putBoolean("lockout", this.lockout);
        this.nbt.putBoolean("leaderboard", this.leaderboard);

        // the default settings and already merged settings should still get the normal nbt for merging.
        if (DEFAULT_ID.equals(id) || CUSTOM_ID.equals(id)) {
            this.rawNbt = this.nbt.copy();
        } else {
            this.rawNbt = nbt;
        }
    }

    public CompoundTag getTag() {
        return nbt;
    }
    
    public void fillStartingInventory(Player player) {
        Inventory container = player.getInventory();
        container.clearContent();
        for (Pair<EquipmentSlot, ItemStack> entry : startingInventory) {
            if (entry.getLeft() == EquipmentSlot.MAINHAND) {
                container.add(entry.getRight().copy());
            } else {
                player.setItemSlot(entry.getLeft(), entry.getRight().copy());
            }
        }
    }
    
    public void fillBackPackInventory(Team team, boolean suppressBingoSync) {
        team.clearBackPack(true);
        IItemHandlerModifiable inventory = team.getBackPack();
        int slot = 0;
        for (ItemStack stack : backpackInventory) {
            if (slot < inventory.getSlots()) {
                inventory.setStackInSlot(slot, stack.copy());
                slot += 1;
            }
        }
        team.setChanged(suppressBingoSync);
    }
    
    public boolean hasEmergencyItems() {
        return !emergencyItems.isEmpty();
    }
    
    public void giveEmergencyItems(Player player) {
        for (ItemStack stack : emergencyItems) {
            if (!player.getInventory().add(stack.copy())) {
                player.drop(stack.copy(), false);
            }
        }
    }

    public static void loadGameSettings(ResourceManager rm) throws IOException {
        GameDef.loadData(rm, "bingo_settings", GAME_SETTINGS, GameSettings::new);
        GAME_SETTINGS.put(DEFAULT.id, DEFAULT);
    }

    public PlayerTeleporter getTeleporter() {
        return teleporter;
    }
    
    public static GameSettings createCustom(GameSettings... settings) {
        if (settings.length <= 0) {
            return DEFAULT;
        } else if (settings.length == 1) {
            return settings[0];
        } else {
            CompoundTag merged = new CompoundTag();
            for (GameSettings elem : settings) {
                merged.merge(elem.rawNbt);
            }
            return new GameSettings(CUSTOM_ID, merged);
        }
    }
}
