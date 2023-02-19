package ability;

public final class LowBlow implements Ability {
    public void ability(final Args args) {
        int maxHealthId = GetHighestHealth.getHighestHealth(args);
        args.getGameTable().get(args.getEnemyX()).get(maxHealthId).setDead(true);
    }
}
