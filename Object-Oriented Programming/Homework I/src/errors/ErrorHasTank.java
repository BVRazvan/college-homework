package errors;

import card.Card;
import gametable.GameTable;

import player.Player;
import fileio.ActionsInput;

import java.util.ArrayList;

public final class ErrorHasTank {
    public static boolean errorHasTank(final GameTable gameTable, final Player player,
                                       final ActionsInput action) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        if (action.getCardAttacked() != null) {
            int enemyX = action.getCardAttacked().getX();
            int enemyY = action.getCardAttacked().getY();
            if (table.get(enemyX).get(enemyY) != null && table.get(enemyX).get(enemyY).isTank()) {
                return false;
            }
        }
        int tankRow;
        if (player.getPlayerId() == 1) {
            tankRow = GameTable.TANK_ROW_TWO;
        } else {
            tankRow = GameTable.TANK_ROW_ONE;
        }
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            Card card = table.get(tankRow).get(j);
            if (card != null && card.isTank()) {
                return true;
            }
        }
        return false;
    }
    private ErrorHasTank() { }
}
