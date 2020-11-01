package io.github.noeppi_noeppi.mods.bongo;

import com.dicemc.corruptedlands.ICorrupted;
import com.jedijoe.ImmortuosCalyx.Infection.InfectionManagerCapability;
import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.data.GameDef;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.task.*;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.block.Block;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.io.IOException;

public class EventListener {

    @SubscribeEvent
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
        if (!event.player.getEntityWorld().isRemote && event.player.ticksExisted % 20 == 0) {
            Bongo bongo = Bongo.get(event.player.world);
            for (ItemStack stack : event.player.inventory.mainInventory) {
                if (!stack.isEmpty()) {
                    int count = 0;
                    for (ItemStack checkStack : event.player.inventory.mainInventory) {
                        if (ItemStack.areItemsEqual(stack, checkStack) && ItemStack.areItemStackTagsEqual(stack, checkStack))
                            count += checkStack.getCount();
                    }
                    ItemStack test = stack.copy();
                    test.setCount(count);
                    bongo.checkCompleted(TaskTypeItem.INSTANCE, event.player, test);
                }
            }
            // This is a bit hacky but it works
            ResourceLocation biomeKey = event.player.getEntityWorld().func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(event.player.getEntityWorld().getBiome(event.player.getPosition()));
            Biome realBiome = ForgeRegistries.BIOMES.getValue(biomeKey);
            bongo.checkCompleted(TaskTypeBiome.INSTANCE, event.player, realBiome);
            if (bongo.running() && bongo.getTeam(event.player) != null && bongo.getSettings().invulnerable) {
                event.player.getFoodStats().setFoodLevel(20);
                event.player.setAir(event.player.getMaxAir());
            }

            // Calyx
            event.player.getCapability(InfectionManagerCapability.INSTANCE).ifPresent(manager -> bongo.checkCompleted(TaskTypeCalyx.INSTANCE, event.player, manager.getInfectionProgress()));

            // Corrupted
            Block standing = event.player.getEntityWorld().getBlockState(event.player.getPosition().down()).getBlock();
            if (standing instanceof ICorrupted) {
                bongo.checkCompleted(TaskTypeCorrupted.INSTANCE, event.player, standing);
            }

            // Paranoia
            event.player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> bongo.checkCompleted(TaskTypeSanity.INSTANCE, event.player, sanity.getSanity()));
        }
    }

    @SubscribeEvent
    public void playerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        // Undercover Witches
        if (event.getTarget() instanceof CatEntity) {
            CatEntity cat = (CatEntity) event.getTarget();
            if (cat.isTamed() && event.getPlayer().getUniqueID().equals(cat.getOwnerId())) {
                if (cat.getTags().contains("undercover_witch")) {
                    Bongo bongo = Bongo.get(event.getWorld());
                    bongo.checkCompleted(TaskTypeWitch.INSTANCE, event.getPlayer(), TaskTypeWitch.INSTANCE);
                }
            }
        }
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
                    GameDef.loadGameDefs(resourceManager);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
