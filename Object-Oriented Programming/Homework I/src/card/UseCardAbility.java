package card;

import ability.Args;
import gametable.GameTable;
import fileio.ActionsInput;
import gametable.CleanTable;
import java.util.ArrayList;

public final class UseCardAbility {
    public static void useCardAbility(final GameTable gameTable, final ActionsInput action) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        Args args;
        int allyX = action.getCardAttacker().getX();
        int allyY = action.getCardAttacker().getY();
        int enemyX = action.getCardAttacked().getX();
        int enemyY = action.getCardAttacked().getY();
        switch (table.get(allyX).get(allyY).getName()) {
            case "The Ripper":
                TheRipper theRipper = new TheRipper();
                args = theRipper.applyAbility(gameTable, enemyX, enemyY);
                gameTable.setGameTable(args.getGameTable());
                CleanTable.cleanTable(gameTable);
                break;
            case "Miraj":
                Miraj miraj = new Miraj();
                args = miraj.applyAbility(gameTable, allyX, allyY, enemyX, enemyY);
                gameTable.setGameTable(args.getGameTable());
                break;
            case "The Cursed One":
                TheCursedOne theCursedOne = new TheCursedOne();
                args = theCursedOne.applyAbility(gameTable, enemyX, enemyY);
                gameTable.setGameTable(args.getGameTable());
                CleanTable.cleanTable(gameTable);
                break;
            case "Disciple":
                Disciple disciple = new Disciple();
                args = disciple.applyAbility(gameTable, enemyX, enemyY);
                gameTable.setGameTable(args.getGameTable());
                break;
            default:
                break;
        }
    }
    private UseCardAbility() { }
}
