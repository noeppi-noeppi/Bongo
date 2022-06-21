package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.world.phys.shapes.BooleanOp;

import java.util.function.BiFunction;

import static io.github.noeppi_noeppi.mods.bongo.data.WinValues.*;

public enum WinCondition {

    ONE("bongo.one", one()),
    ALL("bongo.all", all()),
    ROWS("bongo.rows", when(VALUES_ROWS)),
    COLUMNS("bongo.columns", when(VALUES_COLS)),
    DIAGONALS("bongo.diagonals", when(VALUES_DIAGONAL)),
    ROWS_AND_COLUMNS("bongo.rows_and_columns", compose(BooleanOp.OR, ROWS, COLUMNS)),
    DEFAULT("bongo.default", compose(BooleanOp.OR, ROWS, COLUMNS, DIAGONALS)),
    ROW_AND_COLUMN("bongo.row_and_column", compose(BooleanOp.AND, ROWS, COLUMNS));
    
    public static final Codec<WinCondition> CODEC = Codec.STRING.xmap(WinCondition::getWinOrDefault, wc -> wc.id);

    public final String id;
    private final BiFunction<Bongo, Team, Boolean> won;

    WinCondition(String id, BiFunction<Bongo, Team, Boolean> won) {
        this.id = id;
        this.won = won;
    }

    public boolean won(Bongo bongo, Team team) {
        return won.apply(bongo, team);
    }

    public static WinCondition getWin(String id) {
        for (WinCondition wc : values()) {
            if (wc.id.equalsIgnoreCase(id)) {
                return wc;
            }
        }
        return null;
    }

    public static WinCondition getWinOrDefault(String id) {
        for (WinCondition wc : values()) {
            if (wc.id.equalsIgnoreCase(id))
                return wc;
        }
        return DEFAULT;
    }

    private static BiFunction<Bongo, Team, Boolean> when(int[][] winValues) {
        return (bongo, team) -> {
            wincheck:
            for (int[] win : winValues) {
                for (int slot : win) {
                    if (!team.completed(slot))
                        continue wincheck;
                }
                return true;
            }
            return false;
        };
    }

    @SuppressWarnings("SameParameterValue")
    private static BiFunction<Bongo, Team, Boolean> compose(BooleanOp combine, WinCondition c1, WinCondition c2) {
        return (bongo, team) -> combine.apply(c1.won(bongo, team), c2.won(bongo, team));
    }

    @SuppressWarnings("SameParameterValue")
    private static BiFunction<Bongo, Team, Boolean> compose(BooleanOp combine, WinCondition c1, WinCondition c2, WinCondition c3) {
        return (bongo, team) -> combine.apply(combine.apply(c1.won(bongo, team), c2.won(bongo, team)), c3.won(bongo, team));
    }

    private static BiFunction<Bongo, Team, Boolean> one() {
        return (bongo, team) -> {
            for (int i = 0; i < 25; i++) {
                if (team.completed(i))
                    return true;
            }
            return false;
        };
    }

    private static BiFunction<Bongo, Team, Boolean> all() {
        return (bongo, team) -> {
            for (int i = 0; i < 25; i++) {
                if (!team.completed(i))
                    return false;
            }
            return true;
        };
    }
}
