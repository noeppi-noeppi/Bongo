package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.serialization.Codec;
import net.minecraft.world.phys.shapes.BooleanOp;

import java.util.function.Function;

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
    private final Function<TeamCompletion, Boolean> won;

    WinCondition(String id, Function<TeamCompletion, Boolean> won) {
        this.id = id;
        this.won = won;
    }

    public boolean won(TeamCompletion completion) {
        return won.apply(completion);
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

    private static Function<TeamCompletion, Boolean> when(int[][] winValues) {
        return completion -> {
            wincheck:
            for (int[] win : winValues) {
                for (int slot : win) {
                    if (!completion.has(slot))
                        continue wincheck;
                }
                return true;
            }
            return false;
        };
    }

    @SuppressWarnings("SameParameterValue")
    private static Function<TeamCompletion, Boolean> compose(BooleanOp combine, WinCondition c1, WinCondition c2) {
        return completion -> combine.apply(c1.won(completion), c2.won(completion));
    }

    @SuppressWarnings("SameParameterValue")
    private static Function<TeamCompletion, Boolean> compose(BooleanOp combine, WinCondition c1, WinCondition c2, WinCondition c3) {
        return completion -> combine.apply(combine.apply(c1.won(completion), c2.won(completion)), c3.won(completion));
    }

    private static Function<TeamCompletion, Boolean> one() {
        return completion -> {
            for (int i = 0; i < 25; i++) {
                if (completion.has(i))
                    return true;
            }
            return false;
        };
    }

    private static Function<TeamCompletion, Boolean> all() {
        return completion -> {
            for (int i = 0; i < 25; i++) {
                if (!completion.has(i))
                    return false;
            }
            return true;
        };
    }
}
