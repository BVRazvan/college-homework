package card;

import gametable.GameTable;
import fileio.ActionsInput;

import java.util.ArrayList;
import gametable.CleanTable;

public final class PhysicalAttack {
    public static void physicalAttack(final GameTable gameTable, final ActionsInput action) {
        int allyX = action.getCardAttacker().getX();
        int allyY = action.getCardAttacker().getY();
        int enemyX = action.getCardAttacked().getX();
        int enemyY = action.getCardAttacked().getY();
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        Card attacked = table.get(enemyX).get(enemyY);
        Card attacker = table.get(allyX).get(allyY);
        attacked.setHealth(attacked.getHealth() - attacker.getAttackDamage());
        if (attacked.getHealth() <= 0) {
            attacked.setDead(true);
            CleanTable.cleanTable(gameTable);
        }
        attacker.setHasAttacked(true);
    }
    private PhysicalAttack() { }
}
