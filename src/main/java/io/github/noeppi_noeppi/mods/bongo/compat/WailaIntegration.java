package io.github.noeppi_noeppi.mods.bongo.compat;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class WailaIntegration implements IWailaPlugin {

    private static final WailaIntegration INSTANCE = new WailaIntegration();
    private static final ResourceLocation BONGO = BongoMod.getInstance().resource("bingo_items");

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(IWailaClientRegistration registration) {
        registration.addTooltipCollectedCallback((tooltip, accessor) -> {
            if (Minecraft.getInstance().level != null) {
                if (accessor.getHitResult() instanceof BlockHitResult hit) {
                    Bongo bongo = Bongo.get(Minecraft.getInstance().level);
                    if (bongo.active()) {
                        Block block = accessor.getLevel().getBlockState(hit.getBlockPos()).getBlock();
                        if (bongo.isTooltipStack(new ItemStack(block))) {
                            tooltip.add(Util.REQUIRED_ITEM);
                        }
                    }
                }
            }
        });
    }
}
