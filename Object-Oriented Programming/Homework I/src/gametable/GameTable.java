package gametable;

import card.Card;
import java.util.ArrayList;

public final class GameTable {
    private ArrayList<ArrayList<Card>> gameTable = new ArrayList<>();
    public static final int ROWS = 4;
    public static final int COLUMNS = 5;
    public static final int PLAYER_ONE_START_ROW = 2;
    public static final int PLAYER_ONE_END_ROW = 3;
    public static final int PLAYER_TWO_START_ROW = 0;
    public static final int PLAYER_TWO_END_ROW = 1;
    public static final int TANK_ROW_ONE = 2;
    public static final int TANK_ROW_TWO = 1;

    public static final int PLAYER_ONE = 1;
    public static final int PLAYER_TWO = 2;
    public static final int ERROR = -1;

    public GameTable() {
        for (int i = 0; i < ROWS; ++i) {
            this.gameTable.add(new ArrayList<>());
        }
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                this.gameTable.get(i).add(null);
            }
        }
    }
    public ArrayList<ArrayList<Card>> getGameTable() {
        return gameTable;
    }

    public void setGameTable(final ArrayList<ArrayList<Card>> gameTable) {
        this.gameTable = gameTable;
    }
    public void cleanBoard() {
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                this.gameTable.get(i).set(j, null);
            }
        }
    }
}
