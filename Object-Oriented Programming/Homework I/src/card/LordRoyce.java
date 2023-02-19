package card;

import ability.Args;
import ability.SubZero;
import gametable.GameTable;

import java.util.ArrayList;
public final class LordRoyce extends Hero {
    private SubZero subZero = new SubZero();
    public LordRoyce(final int mana, final int attackDamage, final int health,
                     final String description, final ArrayList<String> colors, final String name) {
        this.setMana(mana);
        this.setAttackDamage(attackDamage);
        this.setHealth(health);
        this.setDescription(description);
        this.setColors(colors);
        this.setName(name);
    }
    public LordRoyce() { }
    Args applyAbility(final GameTable gameTable, final int enemyX) {
        Args args = new Args(gameTable.getGameTable(), 0, 0, enemyX, 0);
        this.subZero.ability(args);
        return args;
    }
}
