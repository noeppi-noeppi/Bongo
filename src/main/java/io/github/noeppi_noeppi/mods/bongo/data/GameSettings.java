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

    public GameSettings(CompoundNBT nbt) {
        this.nbt = nbt;

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
    }

    public CompoundNBT getTag() {
        return nbt;
    }
}
