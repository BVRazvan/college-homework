package card;

import ability.Args;
import gametable.GameTable;
import fileio.ActionsInput;
import gametable.CleanTable;

import java.util.ArrayList;

public final class UseHeroAbility {
    public static void useHeroAbility(final GameTable gameTable, final Hero hero,
                                      final ActionsInput action) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        Args args;
        int selectedRow = action.getAffectedRow();
        switch (hero.getName()) {
            case "Lord Royce":
                LordRoyce lordRoyce = new LordRoyce();
                args = lordRoyce.applyAbility(gameTable, selectedRow);
                gameTable.setGameTable(args.getGameTable());
                break;
            case "Empress Thorina":
                EmpressThorina empressThorina = new EmpressThorina();
                args = empressThorina.applyAbility(gameTable, selectedRow);
                gameTable.setGameTable(args.getGameTable());
                CleanTable.cleanTable(gameTable);
                break;
            case "King Mudface":
                KingMudface kingMudface = new KingMudface();
                args = kingMudface.applyAbility(gameTable, selectedRow);
                gameTable.setGameTable(args.getGameTable());
                break;
            case "General Kocioraw":
                GeneralKocioraw generalKocioraw = new GeneralKocioraw();
                args = generalKocioraw.applyAbility(gameTable, selectedRow);
                gameTable.setGameTable(args.getGameTable());
                break;
            default:
                break;
        }
    }
    private UseHeroAbility() { }
}
