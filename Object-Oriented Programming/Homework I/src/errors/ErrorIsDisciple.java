package errors;

import gametable.GameTable;
import fileio.ActionsInput;
import player.Player;
public final class ErrorIsDisciple {
    public static boolean errorIsDisciple(final Player player, final ActionsInput action) {
        int startRow = GameTable.PLAYER_ONE_START_ROW;
        int endRow = GameTable.PLAYER_ONE_END_ROW;
        int selectedRow = action.getCardAttacked().getX();
        if (player.getPlayerId() == 2) {
            endRow = GameTable.PLAYER_TWO_END_ROW;
            startRow = GameTable.PLAYER_TWO_START_ROW;
        }

        return startRow > selectedRow || selectedRow > endRow;
    }
    private ErrorIsDisciple() { }
}
