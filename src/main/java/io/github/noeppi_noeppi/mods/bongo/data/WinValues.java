package io.github.noeppi_noeppi.mods.bongo.data;

public class WinValues {

    public static final int[][] VALUES_ROWS = new int[][]{
            {xy(0, 0), xy(1, 0), xy(2, 0), xy(3, 0), xy(4, 0)},
            {xy(0, 1), xy(1, 1), xy(2, 1), xy(3, 1), xy(4, 1)},
            {xy(0, 2), xy(1, 2), xy(2, 2), xy(3, 2), xy(4, 2)},
            {xy(0, 3), xy(1, 3), xy(2, 3), xy(3, 3), xy(4, 3)},
            {xy(0, 4), xy(1, 4), xy(2, 4), xy(3, 4), xy(4, 4)}
    };

    public static final int[][] VALUES_COLS = new int[][]{
            {xy(0, 0), xy(0, 1), xy(0, 2), xy(0, 3), xy(0, 4)},
            {xy(1, 0), xy(1, 1), xy(1, 2), xy(1, 3), xy(1, 4)},
            {xy(2, 0), xy(2, 1), xy(2, 2), xy(2, 3), xy(2, 4)},
            {xy(3, 0), xy(3, 1), xy(3, 2), xy(3, 3), xy(3, 4)},
            {xy(4, 0), xy(4, 1), xy(4, 2), xy(4, 3), xy(4, 4)}
    };

    public static final int[][] VALUES_DIAGONAL = new int[][]{
            {xy(0, 0), xy(1, 1), xy(2, 2), xy(3, 3), xy(4, 4)},
            {xy(0, 4), xy(1, 3), xy(2, 2), xy(3, 1), xy(4, 0)}
    };

    private static int xy(int x, int y) {
        return x + (5 * y);
    }
}
