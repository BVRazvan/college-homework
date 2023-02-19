package errors;

import gametable.GameTable;

import player.Player;
import fileio.ActionsInput;
public final class ErrorAttackAlly {
    public static boolean errorAttackAlly(final Player player, final ActionsInput action) {
        int startRow = GameTable.PLAYER_TWO_START_ROW;
        int endRow = GameTable.PLAYER_TWO_END_ROW;
        int selectedRow = action.getCardAttacked().getX();
        if (player.getPlayerId() == 1) {
            startRow = GameTable.PLAYER_ONE_START_ROW;
            endRow = GameTable.PLAYER_ONE_END_ROW;
        }
        return selectedRow >= startRow && selectedRow <= endRow;
    }

    private ErrorAttackAlly() { }
}
