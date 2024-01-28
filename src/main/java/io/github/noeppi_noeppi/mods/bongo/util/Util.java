package io.github.noeppi_noeppi.mods.bongo.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class Util {

    public static final Component REQUIRED_ITEM = Component.translatable("bongo.tooltip.required").withStyle(ChatFormatting.GOLD);
    public static final Comparator<ResourceLocation> COMPARE_RESOURCE = Comparator.nullsFirst(Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath));

    public static final List<DyeColor> PREFERRED_COLOR_ORDER = ImmutableList.of(
            DyeColor.YELLOW, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.PINK, DyeColor.CYAN,
            DyeColor.ORANGE, DyeColor.RED, DyeColor.GREEN, DyeColor.BLUE, DyeColor.PURPLE,
            DyeColor.GRAY, DyeColor.MAGENTA, DyeColor.BLACK, DyeColor.WHITE, DyeColor.BROWN,
            DyeColor.LIGHT_GRAY
    );

    private static final Map<DyeColor, TextColor> COLOR_CACHE = new HashMap<>();

    public static String resourceStr(ResourceLocation rl) {
        if ("minecraft".equals(rl.getNamespace())) {
            return rl.getPath();
        } else {
            return rl.toString();
        }
    }
    
    public static Style getTextFormatting(@Nullable DyeColor color) {
        if (color == null) {
            return Style.EMPTY.applyFormat(ChatFormatting.RESET);
        } else {
            if (!COLOR_CACHE.containsKey(color)) {
                int colorValue = color.getTextColor();
                float[] hsb = java.awt.Color.RGBtoHSB((colorValue >>> 16) & 0xFF, (colorValue >>> 8) & 0xFF, colorValue & 0xFF, null);
                // Remove alpha bits as the value can not be serialized when using the alpha bits.
                COLOR_CACHE.put(color, TextColor.fromRgb(0x00FFFFFF & java.awt.Color.HSBtoRGB(hsb[0], hsb[1] < 0.2 ? hsb[1] : 1, hsb[2])));
            }
            return Style.EMPTY.withColor(COLOR_CACHE.get(color));
        }
    }

    public static void broadcastTeam(Level level, Team team, Component message) {
        MinecraftServer server = level.getServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(player -> {
                if (team.hasPlayer(player))
                    player.sendSystemMessage(message);
            });
        }
    }

    public static boolean matchesNBT(@Nullable CompoundTag required, @Nullable CompoundTag actual) {
        if (required == null || required.isEmpty())
            return true;

        if (actual == null || actual.isEmpty())
            return false;

        CompoundTag copy = actual.copy();
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

    public static boolean validSpawn(Level level, BlockPos pos) {
        return canRespawnIn(level.getBlockState(pos)) && canRespawnIn(level.getBlockState(pos.above()))
                && level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP);
    }
    
    private static boolean canRespawnIn(BlockState state) {
        return state.getBlock().isPossibleToRespawnInThis(state);
    }
    
    public static Optional<ResourceLocation> biome(ServerLevel level, BlockPos pos) {
        return join(level.getBiome(pos).unwrap()
                .mapLeft(ResourceKey::location)
                .mapLeft(Optional::of)
                .mapRight(ForgeRegistries.BIOMES::getKey)
                .mapRight(Optional::ofNullable));
    }
    
    public static <T> T join(Either<? extends T, ? extends T> either) {
        if (either.left().isPresent()) {
            return either.left().get();
        } else {
            //noinspection OptionalGetWithoutIsPresent
            return either.right().get();
        }
    }

    public static void removeItems(Player player, int amount, Predicate<ItemStack> test) {
        int removeLeft = amount;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (removeLeft <= 0) {
                break;
            }
            ItemStack playerSlot = player.getInventory().getItem(slot);
            if (test.test(playerSlot)) {
                int rem = Math.min(removeLeft, playerSlot.getCount());
                playerSlot.shrink(rem);
                player.getInventory().setItem(slot, playerSlot.isEmpty() ? ItemStack.EMPTY : playerSlot);
                removeLeft -= rem;
            }
        }
    }
    
    public static void handleTaskLocking(Bongo bongo, Player player) {
        if (bongo.running() && bongo.getSettings().game().lockTaskOnDeath()) {
            Team team = bongo.getTeam(player);
            if (team != null && team.lockRandomTask()) {
                MutableComponent tc = Component.translatable("bongo.task_locked.death", player.getDisplayName());
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.serverLevel().getServer().getPlayerList().getPlayers().forEach(thePlayer -> {
                        if (team.hasPlayer(thePlayer)) {
                            thePlayer.sendSystemMessage(tc);
                            thePlayer.connection.send(new ClientboundSoundPacket(sound(SoundEvents.ANVIL_LAND), SoundSource.MASTER, thePlayer.getX(), thePlayer.getY(), thePlayer.getZ(), 1f, 1, 0));
                        }
                    });
                }
            }
        }
    }
    
    public static Holder<SoundEvent> sound(SoundEvent event) {
        return ForgeRegistries.SOUND_EVENTS.getHolder(event).orElseGet(() -> Holder.direct(event));
    }
}
