package io.github.noeppi_noeppi.mods.bongo.util;

import com.google.common.collect.ImmutableList;
import io.github.noeppi_noeppi.libx.util.Misc;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Util {

    public static final Comparator<ResourceLocation> COMPARE_RESOURCE = Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath);

    public static final List<DyeColor> PREFERRED_COLOR_ORDER = ImmutableList.of(
            DyeColor.ORANGE, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.PINK, DyeColor.CYAN,
            DyeColor.YELLOW, DyeColor.RED, DyeColor.GREEN, DyeColor.BLUE, DyeColor.PURPLE,
            DyeColor.GRAY, DyeColor.MAGENTA, DyeColor.BLACK, DyeColor.WHITE, DyeColor.BROWN,
            DyeColor.LIGHT_GRAY
    );

    private static final Map<DyeColor, Color> COLOR_CACHE = new HashMap<>();

    public static Style getTextFormatting(@Nullable DyeColor color) {
        if (color == null) {
            return Style.EMPTY.applyFormatting(TextFormatting.RESET);
        } else {
            if (!COLOR_CACHE.containsKey(color)) {
                int colorValue = color.getColorValue();
                float[] hsb = java.awt.Color.RGBtoHSB((colorValue >> 16) & 0xFF, (colorValue >> 8) & 0xFF, colorValue & 0xFF, null);
                // Remove alpha bits as the value can not be serialized when using the alpha bits.
                COLOR_CACHE.put(color, Color.fromInt(0x00FFFFFF & java.awt.Color.HSBtoRGB(hsb[0], Math.min(1, hsb[1] + 0.1f), Math.min(1, hsb[2] + 0.1f))));
            }
            return Style.EMPTY.setColor(COLOR_CACHE.get(color));
        }
    }

    public static void broadcastTeam(World world, Team team, ITextComponent message) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(player -> {
                if (team.hasPlayer(player))
                    player.sendMessage(message, player.getUniqueID());
            });
        }
    }

    public static boolean matchesNBT(@Nullable CompoundNBT required, @Nullable CompoundNBT actual) {
        if (required == null || required.isEmpty())
            return true;

        if (actual == null || actual.isEmpty())
            return false;

        CompoundNBT copy = actual.copy();
        copy.merge(required);
        return copy.equals(actual);
    }

    public static String formatTime(int hours, int minutes, int seconds) {
        if (hours == 0) {
            return minutes + ":" + String.format("%02d", seconds);
        } else {
            return hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        }
    }

    public static String formatTime(int hours, int minutes, int seconds, int decimal) {
        if (hours == 0) {
            return minutes + ":" + String.format("%02d", seconds) + "." + decimal;
        } else {
            return hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "." + decimal;
        }
    }

    public static boolean validSpawn(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().canSpawnInBlock()
                && world.getBlockState(pos.up()).getBlock().canSpawnInBlock()
                && world.getBlockState(pos.down()).isSolidSide(world, pos, Direction.UP);
    }

    public static ResourceLocation getLocationFor(CompoundNBT nbt, String id) {
        if (!nbt.contains(id, Constants.NBT.TAG_STRING)) {
            throw new IllegalStateException("Resource property for " + id + " missing or not a string.");
        }
        ResourceLocation rl = ResourceLocation.tryCreate(nbt.getString(id));
        if (rl == null) {
            throw new IllegalStateException("Invalid " + id + " resource location: '" + nbt.getString(id) + "'");
        }
        return rl;
    }

    public static <T extends IForgeRegistryEntry<T>> T getFromRegistry(IForgeRegistry<T> registry, CompoundNBT nbt, String id) {
        ResourceLocation rl = getLocationFor(nbt, id);
        T element = registry.getValue(rl);
        if (element == null) {
            throw new IllegalStateException("Unknown " + id + ": " + rl);
        }
        return element;
    }

    public static <T extends IForgeRegistryEntry<T>> void putByForgeRegistry(IForgeRegistry<T> registry, CompoundNBT nbt, String id, T element) {
        ResourceLocation rl = registry.getKey(element);
        if (rl == null) {
            BongoMod.getInstance().logger.warn("Failed to serialise " + id + " location: Not found in forge registry: " + element);
            rl = Misc.MISSIGNO;
        }
        nbt.putString(id, rl.toString());
    }

    public static void removeItems(PlayerEntity player, int amount, Predicate<ItemStack> test) {
        int removeLeft = amount;
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            if (removeLeft <= 0) {
                break;
            }
            ItemStack playerSlot = player.inventory.getStackInSlot(slot);
            if (test.test(playerSlot)) {
                int rem = Math.min(removeLeft, playerSlot.getCount());
                playerSlot.shrink(rem);
                player.inventory.setInventorySlotContents(slot, playerSlot.isEmpty() ? ItemStack.EMPTY : playerSlot);
                removeLeft -= rem;
            }
        }
    }
    
    public static void handleTaskLocking(Bongo bongo, PlayerEntity player) {
        if (bongo.running() && bongo.getSettings().lockTaskOnDeath) {
            Team team = bongo.getTeam(player);
            if (team != null && team.lockRandomTask()) {
                IFormattableTextComponent tc = new TranslationTextComponent("bongo.task_locked.death", player.getDisplayName());
                if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) player).getServerWorld().getServer().getPlayerList().getPlayers().forEach(thePlayer -> {
                        if (team.hasPlayer(thePlayer)) {
                            thePlayer.sendMessage(tc, thePlayer.getUniqueID());
                            thePlayer.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.MASTER, thePlayer.getPosX(), thePlayer.getPosY(), thePlayer.getPosZ(), 1f, 1));
                        }
                    });
                }
            }
        }
    }
}
