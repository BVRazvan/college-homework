package gametable;

import java.util.ArrayList;
import card.Card;

public final class CleanTable {
    public static void cleanTable(final GameTable gameTable) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        for (int i = 0; i < GameTable.ROWS; ++i) {
            for (int j = 0; j < GameTable.COLUMNS; ++j) {
                if (table.get(i).get(j) != null && table.get(i).get(j).isDead()) {
                    table.get(i).set(j, null);
                }
            }
            shiftCards(gameTable, i);
        }
    }

    public static void shiftCards(final GameTable gameTable, final int selectedRow) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            if (table.get(selectedRow).get(j) != null) {
                int place = j - 1, found = -1;
                while (place >= 0 && table.get(selectedRow).get(place) == null) {
                    found = place;
                    --place;
                }
                if (found != -1) {
                    Card card = new Card(table.get(selectedRow).get(j));
                    table.get(selectedRow).set(found, card);
                    table.get(selectedRow).set(j, null);
                    j = -1;
                }
            }
        }
    }
    private CleanTable() { }
}
