package card;

import ability.Args;

import java.util.ArrayList;
import ability.LowBlow;
import gametable.GameTable;

public final class EmpressThorina extends Hero {
    public LowBlow getLowBlow() {
        return lowBlow;
    }

    public void setLowBlow(final LowBlow lowBlow) {
        this.lowBlow = lowBlow;
    }

    private LowBlow lowBlow = new LowBlow();
    public EmpressThorina(final int mana, final int attackDamage, final int health,
                          final String description, final ArrayList<String> colors,
                          final String name) {
        this.setMana(mana);
        this.setAttackDamage(attackDamage);
        this.setHealth(health);
        this.setDescription(description);
        this.setColors(colors);
        this.setName(name);
    }
    public EmpressThorina() { }
    Args applyAbility(final GameTable gameTable, final int enemyX) {
        Args args = new Args(gameTable.getGameTable(), 0, 0, enemyX, 0);
        this.lowBlow.ability(args);
        return args;
    }
}
