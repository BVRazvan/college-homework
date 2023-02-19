package ability;

import gametable.GameTable;
import card.Card;
public final class GetHighestAttackDamage {
    public static int getHighestAttackDamage(final Args args) {
        int maxAttackDamage = 0, maxAttackDamageId = -1;
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            Card enemy = args.getGameTable().get(args.getEnemyX()).get(j);
            if (enemy == null) {
                continue;
            }
            if (enemy.getHealth() > maxAttackDamage) {
                maxAttackDamageId = j;
            }
            maxAttackDamage = Math.max(maxAttackDamage, enemy.getHealth());
        }
        return maxAttackDamageId;
    }
    private GetHighestAttackDamage() { }
}
