package errors;

import card.Card;
import card.FindMinionType;
import gametable.GameTable;
import player.Player;

import java.util.ArrayList;

public final class ErrorPlaceRow {
    public static int errorPlaceRow(final Player player, final Card card,
                                    final GameTable gameTable) {
        int selectedRow = FindMinionType.findMinionType(card);
        if (player.getPlayerId() == 1) {
            selectedRow = GameTable.ROWS - selectedRow - 1;
        }
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            if ((table.get(selectedRow).get(j) == null)
                    || (table.get(selectedRow).get(j).isDead())) {
                return selectedRow;
            }
        }
        return GameTable.ERROR;
    }
    private ErrorPlaceRow() { }
}
