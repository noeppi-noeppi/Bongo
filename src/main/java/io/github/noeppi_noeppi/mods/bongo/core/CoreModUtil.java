package io.github.noeppi_noeppi.mods.bongo.core;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.task.TaskTypePainting;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class CoreModUtil {
    
    public static void placeHangingEntity(UseOnContext ctx, @Nullable HangingEntity entity) {
        if (!ctx.getLevel().isClientSide && entity instanceof Painting painting && ctx.getPlayer() != null) {
            Holder<PaintingVariant> variant = painting.getVariant();
            try {
                Bongo.get(ctx.getLevel()).checkCompleted(TaskTypePainting.INSTANCE, ctx.getPlayer(), variant.value());
            } catch (IllegalStateException e) {
                //
            }
        }
    }
}
