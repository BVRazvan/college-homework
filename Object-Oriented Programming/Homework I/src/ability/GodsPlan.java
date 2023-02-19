package ability;

import card.Card;

public final class GodsPlan implements Ability {
    public void ability(final Args args) {
        Card ally = args.getGameTable().get(args.getEnemyX()).get(args.getEnemyY());
        int newHealth = ally.getHealth() + Card.GODS_PLAN_HEALTH;
        ally.setHealth(newHealth);
    }
}
