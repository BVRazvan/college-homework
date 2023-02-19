package errors;

import gametable.GameTable;
import player.Player;

public final class ErrorChosenRow {
    public static int errorChosenRow(final Player player, final int selectedRow) {
        int startRow = 0;
        int endRow;
        if (player.getPlayerId() == 1) {
            startRow = GameTable.ROWS - 2;
        }
        endRow = startRow + 1;
        if (selectedRow >= startRow && selectedRow <= endRow) {
            return -1;
        }
        return 0;
    }

    private ErrorChosenRow() { }
}
