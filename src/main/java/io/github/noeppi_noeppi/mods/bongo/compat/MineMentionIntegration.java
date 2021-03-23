package io.github.noeppi_noeppi.mods.bongo.compat;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.minemention.api.SpecialMention;
import io.github.noeppi_noeppi.mods.minemention.api.SpecialMentions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Predicate;

public class MineMentionIntegration {
    
    public static void setup() {
        SpecialMentions.registerMention(new ResourceLocation(BongoMod.getInstance().modid, "team"), "team", new TeamMention());
    }
    
    public static void availabilityChange(ServerPlayerEntity player) {
        SpecialMentions.notifyAvailabilityChange(player);
    }
    
    public static class TeamMention implements SpecialMention {

        @Override
        public IFormattableTextComponent description() {
            return new TranslationTextComponent("bongo.mention");
        }

        @Override
        public Predicate<ServerPlayerEntity> selectPlayers(ServerPlayerEntity sender) {
            return player -> {
                Bongo bongo = Bongo.get(sender.getServerWorld());
                Team team = bongo.getTeam(sender);
                return team != null && team.hasPlayer(player);
            };
        }

        @Override
        public boolean available(ServerPlayerEntity sender) {
            Bongo bongo = Bongo.get(sender.getServerWorld());
            return bongo.active() && bongo.getTeam(sender) != null;
        }
    }
}
