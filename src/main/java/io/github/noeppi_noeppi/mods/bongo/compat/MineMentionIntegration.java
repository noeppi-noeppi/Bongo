package io.github.noeppi_noeppi.mods.bongo.compat;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.minemention.api.SpecialMention;
import io.github.noeppi_noeppi.mods.minemention.api.SpecialMentions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public class MineMentionIntegration {
    
    public static void setup() {
        SpecialMentions.registerMention(new ResourceLocation(BongoMod.getInstance().modid, "team"), "team", new TeamMention());
    }
    
    public static void availabilityChange(ServerPlayer player) {
        SpecialMentions.notifyAvailabilityChange(player);
    }
    
    public static class TeamMention implements SpecialMention {

        @Override
        public Component description() {
            return Component.translatable("bongo.mention");
        }

        @Override
        public Predicate<ServerPlayer> selectPlayers(ServerPlayer sender) {
            return player -> {
                Bongo bongo = Bongo.get(sender.level());
                Team team = bongo.getTeam(sender);
                return team != null && team.hasPlayer(player);
            };
        }

        @Override
        public boolean available(ServerPlayer sender) {
            Bongo bongo = Bongo.get(sender.level());
            return bongo.active() && bongo.getTeam(sender) != null;
        }
    }
}
