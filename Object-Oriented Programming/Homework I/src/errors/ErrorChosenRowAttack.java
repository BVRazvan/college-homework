package errors;

import gametable.GameTable;
import player.Player;
public final class ErrorChosenRowAttack {
    public static boolean errorChosenRowAttack(final Player player, final int selectedRow) {
        int startRow = 0;
        int endRow;
        if (player.getPlayerId() == 2) {
            startRow = GameTable.ROWS - 2;
        }
        endRow = startRow + 1;
        return selectedRow < startRow || selectedRow > endRow;
    }

    private ErrorChosenRowAttack() { }
}
