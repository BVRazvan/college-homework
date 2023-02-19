package card;

import ability.Args;
import ability.WeakKnees;
import gametable.GameTable;

import java.util.ArrayList;

public final class TheRipper extends Card {
    public static int getRowPlace() {
        return ROW_PLACE;
    }

    private static final int ROW_PLACE = 1;

    public WeakKnees getWeakKnees() {
        return weakKnees;
    }

    public void setWeakKnees(final WeakKnees weakKnees) {
        this.weakKnees = weakKnees;
    }

    private WeakKnees weakKnees = new WeakKnees();
    public TheRipper(final int mana, final int attackDamage, final int health,
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
    public TheRipper(final Card card) {
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
    public TheRipper() { }
    Args applyAbility(final GameTable gameTable, final int enemyX, final int enemyY) {
        Args args = new Args(gameTable.getGameTable(), 0, 0, enemyX, enemyY);
        this.weakKnees.ability(args);
        return args;
    }
}
