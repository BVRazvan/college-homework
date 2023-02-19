package card;

import java.util.ArrayList;
import ability.GodsPlan;
import ability.Args;
import gametable.GameTable;

public final class Disciple extends Card {
    public static int getRowPlace() {
        return ROW_PLACE;
    }

    private static final int ROW_PLACE = 0;

    public GodsPlan getGodsPlan() {
        return godsPlan;
    }

    public void setGodsPlan(final GodsPlan godsPlan) {
        this.godsPlan = godsPlan;
    }

    private GodsPlan godsPlan = new GodsPlan();

    public Disciple(final int mana, final int attackDamage, final int health,
                    final String description, final ArrayList<String> colors, final String name) {
        this.setMana(mana);
        this.setAttackDamage(attackDamage);
        this.setHealth(health);
        this.setDescription(description);
        this.setColors(colors);
        this.setName(name);
        this.setTank(false);
        this.setFrozen(false);
    }
    public Disciple(final Card card) {
        this.setAttackDamage(card.getAttackDamage());
        this.setColors(card.getColors());
        this.setDescription(card.getDescription());
        this.setFrozen(card.isFrozen());
        this.setHealth(card.getHealth());
        this.setDead(card.isDead());
        this.setMana(card.getMana());
        this.setTank(card.isTank());
        this.setName(card.getName());
        this.setDead(card.isDead());
    }
    public Disciple() { }
    Args applyAbility(final GameTable gameTable, final int enemyX, final int enemyY) {
        Args args = new Args(gameTable.getGameTable(), 0, 0, enemyX, enemyY);
        this.godsPlan.ability(args);
        return args;
    }
}
