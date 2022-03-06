package io.github.noeppi_noeppi.mods.bongo;

import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.data.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.data.GameTasks;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import io.github.noeppi_noeppi.mods.bongo.task.*;
import io.github.noeppi_noeppi.mods.bongo.util.StatAndValue;
import io.github.noeppi_noeppi.mods.bongo.util.TagWithCount;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.tags.Tag;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EventListener {

    @SubscribeEvent(priority = EventPriority.LOW) // We need to run after JEA
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        BongoMod.getNetwork().updateBongo(event.getPlayer());
        Level level = event.getPlayer().getCommandSenderWorld();
        if (!level.isClientSide && level instanceof ServerLevel && event.getPlayer() instanceof ServerPlayer) {
            Bongo bongo = Bongo.get(level);
            if (bongo.running()) {
                boolean playerFound = false;
                for (Team team : bongo.getTeams()) {
                    if (team.hasPlayer(event.getPlayer())) {
                        playerFound = true;
                    }
                }
                if (!playerFound && !event.getPlayer().hasPermissions(2)) {
                    ((ServerPlayer) event.getPlayer()).connection.disconnect(new TranslatableComponent("bongo.disconnect"));
                    return;
                }
            }
            for (Task task : bongo.tasks()) {
                if (task != null)
                    task.syncToClient(level.getServer(), (ServerPlayer) event.getPlayer());
            }
            BongoMod.getNetwork().updateBongo(event.getPlayer(), BongoMessageType.FORCE);
        }
    }

    @SubscribeEvent
    public void playerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        BongoMod.getNetwork().updateBongo(event.getPlayer());
    }

    @SubscribeEvent
    public void advancementGrant(AdvancementEvent event) {
        Level level = event.getPlayer().getCommandSenderWorld();
        if (!level.isClientSide) {
            Bongo.get(level).checkCompleted(TaskTypeAdvancement.INSTANCE, event.getPlayer(), event.getAdvancement().getId());
        }
    }

    @SubscribeEvent
    public void potionAdd(PotionEvent.PotionAddedEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            Level level = player.getCommandSenderWorld();
            if (!level.isClientSide) {
                Bongo.get(level).checkCompleted(TaskTypePotion.INSTANCE, player, event.getPotionEffect().getEffect());
            }
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.getCommandSenderWorld().isClientSide && event.player.tickCount % 20 == 0 && event.player instanceof ServerPlayer) {
            Bongo bongo = Bongo.get(event.player.level);
            if (bongo.canCompleteTasks(event.player)) {
                Map<ItemStack, Integer> stacks = new HashMap<>();
                bongo.getElementsOf(TaskTypeItem.INSTANCE)
                        .forEach(stack -> stacks.put(stack, 0));
                
                Map<TagWithCount, Integer> tags = new HashMap<>();
                bongo.getElementsOf(TaskTypeTag.INSTANCE)
                        .forEach(tag -> tags.put(tag, 0));
                
                for (ItemStack stack : event.player.getInventory().items) {
                    if (!stack.isEmpty()) {
                        for (Map.Entry<ItemStack, Integer> entry : stacks.entrySet()) {
                            ItemStack test = entry.getKey();
                            if (ItemStack.isSame(stack, test) && ItemStack.tagMatches(stack, test)) {
                                entry.setValue(entry.getValue() + stack.getCount());
                            }
                        }
                        for (Map.Entry<TagWithCount, Integer> entry : tags.entrySet()) {
                            if (entry.getKey().contains(stack.getItem())) {
                                entry.setValue(entry.getValue() + stack.getCount());
                            }
                        }
                    }
                }
                
                stacks.forEach((stack, count) -> {
                    ItemStack test = stack.copy();
                    test.setCount(count);
                    bongo.checkCompleted(TaskTypeItem.INSTANCE, event.player, test);
                });
                
                tags.forEach((tag, count) -> bongo.checkCompleted(TaskTypeTag.INSTANCE, event.player, tag.withCount(count)));
                
                // This is a bit hacky but it works
                try {
                    ResourceLocation biomeKey = event.player.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(event.player.level.getBiome(event.player.blockPosition()).value());
                    Biome realBiome = ForgeRegistries.BIOMES.getValue(biomeKey);
                    bongo.checkCompleted(TaskTypeBiome.INSTANCE, event.player, realBiome);
                } catch (Exception e) {
                    // In case of unbound values
                    e.printStackTrace();
                }
                
                if (bongo.getSettings().invulnerable) {
                    event.player.getFoodData().setFoodLevel(20);
                    event.player.setAirSupply(event.player.getMaxAirSupply());
                }

                ServerStatsCounter mgr = ((ServerPlayer) event.player).getLevel().getServer().getPlayerList().getPlayerStats(event.player);
                bongo.getElementsOf(TaskTypeStat.INSTANCE)
                        .map(value -> new StatAndValue(value.stat, mgr.getValue(value.stat)))
                        .forEach(value -> bongo.checkCompleted(TaskTypeStat.INSTANCE, event.player, value));
            }
        }
    }

    @SubscribeEvent
    public void serverStart(ServerStartedEvent event) {
        GameTasks.validateAllTasks(event.getServer());
    }
    
    @SubscribeEvent
    public void resourcesReload(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<>() {
            @Nonnull
            @Override
            protected Object prepare(@Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
                return new Object();
            }

            @Override
            protected void apply(@Nonnull Object object, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
                try {
                    GameTasks.loadGameTasks(resourceManager);
                    GameSettings.loadGameSettings(resourceManager);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    @SubscribeEvent
    public void resourcesReloaded(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            GameTasks.validateAllTasks(event.getPlayerList().getServer());
        }
    }

    @SubscribeEvent
    public void damage(LivingHurtEvent event) {
        handleCommonDamageEvent(event, event.getSource(), () -> event.setAmount(0));
    }

    @SubscribeEvent
    public void attack(LivingAttackEvent event) {
        handleCommonDamageEvent(event, event.getSource(), () -> {}); // Can't modify damage
    }

    private void handleCommonDamageEvent(LivingEvent event, DamageSource source, Runnable setDamageToZero) {
        if (!event.getEntityLiving().level.isClientSide && event.getEntityLiving() instanceof Player player && !source.isBypassInvul()) {
            Bongo bongo = Bongo.get(player.level);
            Team team = bongo.getTeam(player);
            if (bongo.running() && team != null) {
                if (source.getEntity() instanceof Player damageSourcePlayer) {
                    if (!bongo.getSettings().pvp) {
                        cancelDamage(event, player, source, setDamageToZero);
                    } else if (team.hasPlayer(damageSourcePlayer)) {
                        if (!bongo.getSettings().friendlyFire) {
                            cancelDamage(event, player, source, setDamageToZero);
                        }
                    }
                } else if (bongo.getSettings().invulnerable) {
                    cancelDamage(event, player, source, setDamageToZero);
                }
            }
        }
    }

    private static void cancelDamage(LivingEvent event, Player player, DamageSource source, Runnable setDamageToZero) {
        if (player.isDamageSourceBlocked(source)) {
            setDamageToZero.run();
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void addTooltip(ItemTooltipEvent event) {
        if (ClientConfig.addItemTooltips.get()) {
            ItemStack stack = event.getItemStack();
            if (stack.isEmpty() || event.getPlayer() == null)
                return;
            Bongo bongo = Bongo.get(event.getPlayer().level);
            if (bongo.active() && bongo.isTooltipStack(stack)) {
                event.getToolTip().add(Util.REQUIRED_ITEM);
            }
        }
    }

    @SubscribeEvent
    public void playerName(PlayerEvent.NameFormat event) {
        Player player = event.getPlayer();
        Bongo bongo = Bongo.get(player.level);
        if (bongo.active()) {
            Team team = bongo.getTeam(player);
            if (team != null) {
                Component tc = event.getDisplayname();
                if (tc instanceof MutableComponent) {
                    tc = ((MutableComponent) tc).withStyle(team.getFormatting());
                } else {
                    tc = new TextComponent(event.getPlayer().getScoreboardName()).withStyle(team.getFormatting());
                }
                event.setDisplayname(tc);
            }
        }
    }
    
    @SubscribeEvent
    public void tablistName(PlayerEvent.TabListNameFormat event) {
        Player player = event.getPlayer();
        Bongo bongo = Bongo.get(player.level);
        if (bongo.active()) {
            Team team = bongo.getTeam(player);
            if (team != null) {
                Component tc = event.getDisplayName();
                if (tc instanceof MutableComponent) {
                    tc = ((MutableComponent) tc).withStyle(team.getFormatting());
                } else {
                    tc = new TextComponent(event.getPlayer().getScoreboardName()).withStyle(team.getFormatting());
                }
                event.setDisplayName(tc);
            }
        }
    }

    @SubscribeEvent
    public void entityDie(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            Bongo bongo = Bongo.get(player.level);
            bongo.checkCompleted(TaskTypeEntity.INSTANCE, player, event.getEntity().getType());
        }
        if (event.getEntityLiving() instanceof Player player && !event.getEntityLiving().level.isClientSide) {
            Bongo bongo = Bongo.get(player.level);
            Util.handleTaskLocking(bongo, player);
        }
    }

    @SubscribeEvent
    public void serverChat(ServerChatEvent event) {
        if (!ModList.get().isLoaded("minemention")) {
            Bongo bongo = Bongo.get(event.getPlayer().level);
            if (bongo.teamChat(event.getPlayer())) {
                Team team = bongo.getTeam(event.getPlayer());
                if (team != null) {
                    event.setCanceled(true);
                    MutableComponent tc = new TextComponent("[");
                    tc.append(team.getName());
                    tc.append(new TextComponent("] ").withStyle(ChatFormatting.RESET));
                    tc.append(event.getComponent());
                    Util.broadcastTeam(event.getPlayer().level, team, tc);
                }
            }
        }
    }
}
