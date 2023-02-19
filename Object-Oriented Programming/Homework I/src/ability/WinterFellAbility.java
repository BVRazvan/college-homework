package ability;

import gametable.GameTable;
import card.Card;

public final class WinterFellAbility implements Ability {
    public void ability(final Args args) {
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            Card enemy = args.getGameTable().get(args.getEnemyX()).get(j);
            if (enemy != null) {
                enemy.setFrozen(true);
            }
        }
    }
}
