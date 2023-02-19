package ability;

import gametable.GameTable;
import card.Card;
public final class BloodThirst implements Ability {
    public void ability(final Args args) {
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            Card ally = args.getGameTable().get(args.getAllyX()).get(j);
            if (ally == null) {
                continue;
            }
            int newAttackDamage = ally.getAttackDamage() + Card.BLOOD_THIRST_ATTACK_DAMAGE;
            ally.setAttackDamage(newAttackDamage);
        }
    }
}
