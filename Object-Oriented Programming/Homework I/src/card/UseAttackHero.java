package card;

import gametable.GameTable;
import player.Player;
import fileio.ActionsInput;
public final class UseAttackHero {
    public static void useAttackHero(final Player player, final GameTable gameTable,
                                     final ActionsInput action) {
        int allyX = action.getCardAttacker().getX();
        int allyY = action.getCardAttacker().getY();
        Card card = gameTable.getGameTable().get(allyX).get(allyY);
        player.getHero().setHealth(player.getHero().getHealth() - card.getAttackDamage());
    }
    private UseAttackHero() { }
}
