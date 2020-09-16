package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.util.CommandUtil;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class StartCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        Bongo bongo = Bongo.get(player.getEntityWorld());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.start.notcreated")).create();
        } else if (bongo.running() || bongo.won()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.start.alreadyrunning")).create();
        }
        bongo.start();

        if (CommandUtil.getArgumentOrDefault(context, "randomize_positions", Boolean.class, true)) {
            Random random = new Random();
            for (Team team : bongo.getTeams())
                randomizeTeamAround(random, player.getServerWorld(), team, (int) Math.round(player.getPosX()), (int) Math.round(player.getPosZ()), 10000);
        }

        Util.broadcast(player.getEntityWorld(), new TranslationTextComponent("bongo.info").append(player.getDisplayName()).append(new TranslationTextComponent("bongo.cmd.start.done")));

        return 0;
    }

    public static void randomizeTeamAround(Random random, ServerWorld world, Team team, int centerX, int centerZ, int radius) {
        if (team.getPlayers().size() <= 0)
            return;
        int x = centerX + (random.nextInt(2 * radius) - radius);
        int z = centerZ + (random.nextInt(2 * radius) - radius);
        BlockPos.Mutable mpos = new BlockPos.Mutable(x, world.getHeight(), z);
        while (mpos.getY() > 5 && world.getBlockState(mpos).isAir(world, mpos))
            mpos.move(Direction.DOWN);
        BlockPos pos = mpos.toImmutable().up();
        world.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (team.hasPlayer(player)) {
                player.teleport(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getRotationYawHead(), 0);
            }
        });
    }
}
