package ability;

import card.Card;

public final class SubZero implements Ability {
    public void ability(final Args args) {
        int maxAttackDamageId = GetHighestAttackDamage.getHighestAttackDamage(args);
        if (maxAttackDamageId == -1) {
            return;
        }
        Card enemy = args.getGameTable().get(args.getEnemyX()).get(maxAttackDamageId);
        enemy.setFrozen(true);
    }
}
