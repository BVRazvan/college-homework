package ability;

import gametable.GameTable;
import card.Card;

public final class FireStormAbility implements Ability {
    public void ability(final Args args) {
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            Card enemy = args.getGameTable().get(args.getEnemyX()).get(j);
            if (enemy == null) {
                continue;
            }
            int newHealth = enemy.getHealth() - Card.FIRESTORM_HEALTH;
            enemy.setHealth(newHealth);
            if (enemy.getHealth() <= 0) {
                enemy.setDead(true);
            }
        }
    }
}
