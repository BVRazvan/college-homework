package ability;

import gametable.GameTable;
import card.Card;
public final class GetHighestHealth {
    public static int getHighestHealth(final Args args) {
        int maxHealth = 0, maxHealthId = -1;
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            Card enemy = args.getGameTable().get(args.getEnemyX()).get(j);
            if (enemy == null) {
                continue;
            }
            if (enemy.getHealth() > maxHealth) {
                maxHealthId = j;
            }
            maxHealth = Math.max(maxHealth, enemy.getHealth());
        }
        return maxHealthId;
    }
    private GetHighestHealth() { }
}
