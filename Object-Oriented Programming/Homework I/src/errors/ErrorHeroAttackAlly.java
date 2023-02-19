package errors;

import card.Hero;
import gametable.GameTable;
import player.Player;
public final class ErrorHeroAttackAlly {
    public static boolean errorHeroAttackAlly(final Player player, final Hero hero,
                                              final int selectedRow) {
        String name = hero.getName();
        if (name.equals("Lord Royce") || name.equals("Empress Thorina")) {
            int startRow = GameTable.PLAYER_ONE_START_ROW;
            int endRow = GameTable.PLAYER_ONE_END_ROW;
            if (player.getPlayerId() == 2) {
                startRow = GameTable.PLAYER_TWO_START_ROW;
                endRow = GameTable.PLAYER_TWO_END_ROW;
            }
            return startRow <= selectedRow && selectedRow <= endRow;
        }
        return false;
    }
    private ErrorHeroAttackAlly() { }
}
