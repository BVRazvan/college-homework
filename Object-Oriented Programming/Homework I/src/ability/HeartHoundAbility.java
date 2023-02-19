package ability;

import gametable.GameTable;
import card.Card;

public final class HeartHoundAbility implements Ability {
    public void ability(final Args args) {
        int maxHealthId = GetHighestHealth.getHighestHealth(args);
        Card enemy = args.getGameTable().get(args.getEnemyY()).get(maxHealthId);
        int allyX = GameTable.ROWS - args.getEnemyX() - 1;
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            Card ally = args.getGameTable().get(allyX).get(j);
            if (ally == null) {
                ally = enemy;
                enemy = null;
                return;
            }
        }
    }
}
