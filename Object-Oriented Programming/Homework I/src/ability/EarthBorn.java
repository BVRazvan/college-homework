package ability;

import gametable.GameTable;
import card.Card;
public final class EarthBorn implements Ability {
    public void ability(final Args args) {
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            Card ally = args.getGameTable().get(args.getAllyX()).get(j);
            if (ally == null) {
                continue;
            }
            int newHealth = ally.getHealth() + Card.EARTH_BORN_HEALTH;
            ally.setHealth(newHealth);
        }
    }
}
