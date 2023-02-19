package ability;

import card.Card;

import java.util.ArrayList;

public final class Args {
    private ArrayList<ArrayList<Card>> gameTable;
    private int allyX;
    private int allyY;
    private int enemyX;

    public void setAllyX(final int allyX) {
        this.allyX = allyX;
    }

    public void setAllyY(final int allyY) {
        this.allyY = allyY;
    }

    public void setEnemyX(final int enemyX) {
        this.enemyX = enemyX;
    }

    public void setEnemyY(final int enemyY) {
        this.enemyY = enemyY;
    }

    private int enemyY;

    public ArrayList<ArrayList<Card>> getGameTable() {
        return gameTable;
    }

    public int getAllyX() {
        return allyX;
    }

    public int getAllyY() {
        return allyY;
    }


    public int getEnemyX() {
        return enemyX;
    }

    public int getEnemyY() {
        return enemyY;
    }

    public void setGameTable(ArrayList<ArrayList<Card>> gameTable) {
        this.gameTable = gameTable;
    }

    public Args(final ArrayList<ArrayList<Card>> gameTable, final int allyX,
                final int allyY, final int enemyX, final int enemyY) {
        this.gameTable = gameTable;
        this.allyX = allyX;
        this.allyY = allyY;
        this.enemyX = enemyX;
        this.enemyY = enemyY;
    }
}
