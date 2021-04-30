package io.github.noeppi_noeppi.mods.bongo;

import io.github.noeppi_noeppi.libx.event.DatapacksReloadedEvent;
import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.data.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.data.GameTasks;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.network.BongoMessageType;
import io.github.noeppi_noeppi.mods.bongo.task.*;
import io.github.noeppi_noeppi.mods.bongo.util.StatAndValue;
import io.github.noeppi_noeppi.mods.bongo.util.TagWithCount;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EventListener {

    @SubscribeEvent(priority = EventPriority.LOW) // We need to run after JEA
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        BongoMod.getNetwork().updateBongo(event.getPlayer());
        World world = event.getPlayer().getEntityWorld();
        if (!world.isRemote && world instanceof ServerWorld && event.getPlayer() instanceof ServerPlayerEntity) {
            Bongo bongo = Bongo.get(world);
            if (bongo.running()) {
                boolean playerFound = false;
                for (Team team : bongo.getTeams()) {
                    if (team.hasPlayer(event.getPlayer())) {
                        playerFound = true;
                    }
                }
                if (!playerFound && !event.getPlayer().hasPermissionLevel(2)) {
                    ((ServerPlayerEntity) event.getPlayer()).connection.disconnect(new TranslationTextComponent("bongo.disconnect"));
                    return;
                }
            }
            for (Task task : bongo.tasks()) {
                if (task != null)
                    task.syncToClient(world.getServer(), (ServerPlayerEntity) event.getPlayer());
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
        World world = event.getPlayer().getEntityWorld();
        if (!world.isRemote) {
            Bongo.get(world).checkCompleted(TaskTypeAdvancement.INSTANCE, event.getPlayer(), event.getAdvancement().getId());
        }
    }

    @SubscribeEvent
    public void potionAdd(PotionEvent.PotionAddedEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            World world = player.getEntityWorld();
            if (!world.isRemote) {
                Bongo.get(world).checkCompleted(TaskTypePotion.INSTANCE, player, event.getPotionEffect().getPotion());
            }
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.getEntityWorld().isRemote && event.player.ticksExisted % 20 == 0 && event.player instanceof ServerPlayerEntity) {
            Bongo bongo = Bongo.get(event.player.world);
            if (bongo.canCompleteTasks(event.player)) {
                Map<ItemStack, Integer> stacks = new HashMap<>();
                bongo.getElementsOf(TaskTypeItem.INSTANCE)
                        .forEach(stack -> stacks.put(stack, 0));
                
                Map<TagWithCount, Integer> tags = new HashMap<>();
                bongo.getElementsOf(TaskTypeTag.INSTANCE)
                        .forEach(tag -> tags.put(tag, 0));
                
                for (ItemStack stack : event.player.inventory.mainInventory) {
                    if (!stack.isEmpty()) {
                        for (Map.Entry<ItemStack, Integer> entry : stacks.entrySet()) {
                            ItemStack test = entry.getKey();
                            if (ItemStack.areItemsEqual(stack, test) && ItemStack.areItemStackTagsEqual(stack, test)) {
                                entry.setValue(entry.getValue() + stack.getCount());
                            }
                        }
                        for (Map.Entry<TagWithCount, Integer> entry : tags.entrySet()) {
                            ITag<Item> test = entry.getKey().getTag();
                            if (test.contains(stack.getItem())) {
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
                ResourceLocation biomeKey = event.player.getEntityWorld().func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(event.player.getEntityWorld().getBiome(event.player.getPosition()));
                Biome realBiome = ForgeRegistries.BIOMES.getValue(biomeKey);
                bongo.checkCompleted(TaskTypeBiome.INSTANCE, event.player, realBiome);
                if (bongo.getSettings().invulnerable) {
                    event.player.getFoodStats().setFoodLevel(20);
                    event.player.setAir(event.player.getMaxAir());
                }

                ServerStatisticsManager mgr = ((ServerPlayerEntity) event.player).getServerWorld().getServer().getPlayerList().getPlayerStats(event.player);
                bongo.getElementsOf(TaskTypeStat.INSTANCE)
                        .map(value -> new StatAndValue(value.stat, mgr.getValue(value.stat)))
                        .forEach(value -> bongo.checkCompleted(TaskTypeStat.INSTANCE, event.player, value));
            }
        }
    }

    @SubscribeEvent
    public void serverStart(FMLServerStartedEvent event) {
        GameTasks.validateAllTasks(event.getServer());
    }
    
    @SubscribeEvent
    public void resourcesReload(AddReloadListenerEvent event) {
        event.addListener(new ReloadListener<Object>() {
            @Nonnull
            @Override
            protected Object prepare(@Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
                return new Object();
            }

            @Override
            protected void apply(@Nonnull Object unused, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
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
    public void resourcesReloaded(DatapacksReloadedEvent event) {
        GameTasks.validateAllTasks(event.getServer());
    }

    @SubscribeEvent
    public void damage(LivingHurtEvent event) {
        if (!event.getEntityLiving().getEntityWorld().isRemote && event.getEntityLiving() instanceof PlayerEntity && !event.getSource().canHarmInCreative()) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            Bongo bongo = Bongo.get(player.getEntityWorld());
            Team team = bongo.getTeam(player);
            if (bongo.running() && team != null) {
                if (event.getSource().getTrueSource() instanceof PlayerEntity) {
                    PlayerEntity source = (PlayerEntity) event.getSource().getTrueSource();
                    if (!bongo.getSettings().pvp) {
                        event.setCanceled(true);
                    } else if (team.hasPlayer(source)) {
                        if (!bongo.getSettings().friendlyFire) {
                            event.setCanceled(true);
                        }
                    }
                } else if (bongo.getSettings().invulnerable) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void attack(LivingAttackEvent event) {
        if (!event.getEntityLiving().getEntityWorld().isRemote && event.getEntityLiving() instanceof PlayerEntity && !event.getSource().canHarmInCreative()) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            Bongo bongo = Bongo.get(player.getEntityWorld());
            Team team = bongo.getTeam(player);
            if (bongo.running() && team != null) {
                if (event.getSource().getTrueSource() instanceof PlayerEntity) {
                    PlayerEntity source = (PlayerEntity) event.getSource().getTrueSource();
                    if (!bongo.getSettings().pvp) {
                        event.setCanceled(true);
                    } else if (team.hasPlayer(source)) {
                        if (!bongo.getSettings().friendlyFire) {
                            event.setCanceled(true);
                        }
                    }
                } else if (bongo.getSettings().invulnerable) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void addTooltip(ItemTooltipEvent event) {
        if (ClientConfig.addItemTooltips.get()) {
            ItemStack stack = event.getItemStack();
            if (stack.isEmpty() || event.getPlayer() == null)
                return;
            Bongo bongo = Bongo.get(event.getPlayer().world);
            if (bongo.active() && bongo.isTooltipStack(stack)) {
                event.getToolTip().add(new TranslationTextComponent("bongo.tooltip.required").mergeStyle(TextFormatting.GOLD));
            }
        }
    }

    @SubscribeEvent
    public void playerName(PlayerEvent.NameFormat event) {
        PlayerEntity player = event.getPlayer();
        Bongo bongo = Bongo.get(player.getEntityWorld());
        if (bongo.active()) {
            Team team = bongo.getTeam(player);
            if (team != null) {
                ITextComponent tc = event.getDisplayname();
                if (tc instanceof IFormattableTextComponent)
                    ((IFormattableTextComponent) tc).mergeStyle(team.getFormatting());
                event.setDisplayname(tc);
            }
        }
    }

    @SubscribeEvent
    public void entityDie(LivingDeathEvent event) {
        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            Bongo bongo = Bongo.get(player.getEntityWorld());
            bongo.checkCompleted(TaskTypeEntity.INSTANCE, player, event.getEntity().getType());
        }
        if (event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().getEntityWorld().isRemote) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            Bongo bongo = Bongo.get(player.getEntityWorld());
            if (bongo.getSettings().lockTaskOnDeath) {
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

    @SubscribeEvent
    public void serverChat(ServerChatEvent event) {
        if (!ModList.get().isLoaded("minemention")) {
            Bongo bongo = Bongo.get(event.getPlayer().getEntityWorld());
            if (bongo.teamChat(event.getPlayer())) {
                Team team = bongo.getTeam(event.getPlayer());
                if (team != null) {
                    event.setCanceled(true);
                    IFormattableTextComponent tc = new StringTextComponent("[");
                    tc.append(team.getName());
                    tc.append(new StringTextComponent("] ").mergeStyle(TextFormatting.RESET));
                    tc.append(event.getComponent());
                    Util.broadcastTeam(event.getPlayer().getEntityWorld(), team, tc);
                }
            }
        }
    }
}
