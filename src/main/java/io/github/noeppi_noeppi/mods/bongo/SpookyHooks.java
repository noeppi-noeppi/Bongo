package io.github.noeppi_noeppi.mods.bongo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import subaraki.badbone.registry.BadBoneEffects;

// Methods called by coremods
public class SpookyHooks {
    
    public static boolean addBadBoneEffect(ServerPlayer player, MobEffectInstance effect) {
        if (effect.getEffect() != BadBoneEffects.BLIND.get()) {
            return player.addEffect(effect);
        } else {
            return false;
        }
    }
}
