package ability;

import card.Card;
public final class WeakKnees implements Ability {
    public void ability(final Args args) {
        Card enemy = args.getGameTable().get(args.getEnemyX()).get(args.getEnemyY());
        enemy.setAttackDamage(enemy.getAttackDamage() - Card.WEAK_KNESS_DAMAGE);
        enemy.setAttackDamage(Math.max(enemy.getAttackDamage(), 0));
    }
}
