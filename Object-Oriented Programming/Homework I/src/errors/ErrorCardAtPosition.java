package errors;

import gametable.GameTable;
import fileio.ActionsInput;
import card.Card;
import gametable.CleanTable;

public final class ErrorCardAtPosition {
    public static boolean errorCardAtPosition(final GameTable gameTable,
                                              final ActionsInput action) {
        Card card = gameTable.getGameTable().get(action.getX()).get(action.getY());
        if (card == null || card.isDead()) {
            CleanTable.cleanTable(gameTable);
            return true;
        }
        return false;
    }

    private ErrorCardAtPosition() { }
}
