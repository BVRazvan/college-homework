package ability;

import card.Card;

public final class ShapeShift implements Ability {
    public void ability(final Args args) {
        Card enemy = args.getGameTable().get(args.getEnemyX()).get(args.getEnemyY());
        int temp = enemy.getHealth();
        enemy.setHealth(enemy.getAttackDamage());
        enemy.setAttackDamage(temp);
        if (enemy.getHealth() <= 0) {
            enemy.setDead(true);
        }
    }
}
