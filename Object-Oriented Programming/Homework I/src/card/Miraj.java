package card;

import ability.Args;
import ability.SkyJack;
import gametable.GameTable;

import java.util.ArrayList;

public final class Miraj extends Card {
    public static int getRowPlace() {
        return ROW_PLACE;
    }

    private static final int ROW_PLACE = 1;

    public SkyJack getSkyJack() {
        return skyJack;
    }

    public void setSkyJack(final SkyJack skyJack) {
        this.skyJack = skyJack;
    }

    private SkyJack skyJack = new SkyJack();
    public Miraj(final int mana, final int attackDamage, final int health,
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
    public Miraj(final Card card) {
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
    public Miraj() { }
    Args applyAbility(final GameTable gameTable, final int allyX, final int allyY,
                      final int enemyX, final int enemyY) {
        Args args = new Args(gameTable.getGameTable(), allyX, allyY, enemyX, enemyY);
        this.skyJack.ability(args);
        return args;
    }
}
