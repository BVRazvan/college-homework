package card;

import java.util.ArrayList;

public final class Berserker extends Card {
    public static int getRowPlace() {
        return ROW_PLACE;
    }

    private static final int ROW_PLACE = 0;
    public Berserker(final int mana, final int attackDamage, final int health,
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
    public Berserker(final Card card) {
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
}
