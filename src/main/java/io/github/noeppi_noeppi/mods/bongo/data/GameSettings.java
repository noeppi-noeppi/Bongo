package io.github.noeppi_noeppi.mods.bongo.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class GameSettings {

    public static final GameSettings DEFAULT = new GameSettings(new CompoundNBT());

    private final CompoundNBT nbt;

    public final WinCondition winCondition;
    public final boolean invulnerable;
    public final boolean pvp;
    public final boolean friendlyFire;
    public final boolean lockTaskOnDeath;
    public final boolean consumeItems;
    public final int teleportsPerTeam;

    public GameSettings(CompoundNBT nbt) {
        if (nbt.contains("winCondition", Constants.NBT.TAG_STRING)) {
            winCondition = WinCondition.getWinOrDefault(nbt.getString("winCondition"));
        } else {
            winCondition = WinCondition.DEFAULT;
        }

        if (nbt.contains("invulnerable")) {
            invulnerable = nbt.getBoolean("invulnerable");
        } else {
            invulnerable = true;
        }

        if (nbt.contains("pvp")) {
            pvp = nbt.getBoolean("pvp");
        } else {
            pvp = false;
        }

        if (nbt.contains("friendlyFire")) {
            friendlyFire = nbt.getBoolean("friendlyFire");
        } else {
            friendlyFire = false;
        }

        if (nbt.contains("lockTaskOnDeath")) {
            lockTaskOnDeath = nbt.getBoolean("lockTaskOnDeath");
        } else {
            lockTaskOnDeath = false;
        }

        if (nbt.contains("consumeItems")) {
            consumeItems = nbt.getBoolean("consumeItems");
        } else {
            consumeItems = false;
        }

        if (nbt.contains("teleportsPerTeam")) {
            teleportsPerTeam = nbt.getInt("teleportsPerTeam");
        } else {
            teleportsPerTeam = 0;
        }

        this.nbt = new CompoundNBT();
        this.nbt.putString("winCondition", winCondition.id);
        this.nbt.putBoolean("invulnerable", invulnerable);
        this.nbt.putBoolean("pvp", pvp);
        this.nbt.putBoolean("friendlyFire", friendlyFire);
        this.nbt.putBoolean("lockTaskOnDeath", lockTaskOnDeath);
        this.nbt.putBoolean("consumeItems", consumeItems);
        this.nbt.putInt("teleportsPerTeam", teleportsPerTeam);
    }

    public CompoundNBT getTag() {
        return nbt;
    }
}
