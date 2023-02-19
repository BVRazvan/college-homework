package errors;

import card.Hero;
import gametable.GameTable;
import player.Player;
public final class ErrorHeroAttackedEnemy {
    public static boolean errorHeroAttackedEnemy(final Player player, final Hero hero,
                                                 final int selectedRow) {
        String name = hero.getName();
        if (name.equals("General Kocioraw") || name.equals("King Mudface")) {
            int startRow = GameTable.PLAYER_ONE_START_ROW;
            int endRow = GameTable.PLAYER_ONE_END_ROW;
            if (player.getPlayerId() == 1) {
                startRow = GameTable.PLAYER_TWO_START_ROW;
                endRow = GameTable.PLAYER_TWO_END_ROW;
            }
            return startRow <= selectedRow && selectedRow <= endRow;
        }
        return false;
    }
    private ErrorHeroAttackedEnemy() { }
}
