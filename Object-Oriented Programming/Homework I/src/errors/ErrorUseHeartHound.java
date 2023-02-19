package errors;

import card.Card;
import gametable.GameTable;
import java.util.ArrayList;

public final class ErrorUseHeartHound {
    public static int errorUseHeartHound(final Card card, final GameTable gameTable,
                                         final int selectedRow) {
        if (!card.getName().equals("Heart Hound")) {
            return 0;
        }
        int mirrorRow = GameTable.ROWS - selectedRow - 1;
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            if ((table.get(mirrorRow).get(j) == null)
                    || (table.get(mirrorRow).get(j).isDead())) {
                return GameTable.ERROR;
            }
        }
        return 0;
    }
    private ErrorUseHeartHound() { }
}
