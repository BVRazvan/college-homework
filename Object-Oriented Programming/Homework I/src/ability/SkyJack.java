package ability;
import card.Card;

public final class SkyJack implements Ability {
    public void ability(final Args args) {
        Card enemy = args.getGameTable().get(args.getEnemyX()).get(args.getEnemyY());
        Card ally = args.getGameTable().get(args.getAllyX()).get(args.getAllyY());
        int temp = enemy.getHealth();
        enemy.setHealth(ally.getHealth());
        ally.setHealth(temp);
    }
}
