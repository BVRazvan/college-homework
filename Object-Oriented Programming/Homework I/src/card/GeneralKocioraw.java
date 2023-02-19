package card;

import ability.BloodThirst;
import gametable.GameTable;

import java.util.ArrayList;
import ability.Args;

public final class GeneralKocioraw extends Hero {
    public BloodThirst getBloodThirst() {
        return bloodThirst;
    }

    public void setBloodThirst(final BloodThirst bloodThirst) {
        this.bloodThirst = bloodThirst;
    }

    private BloodThirst bloodThirst = new BloodThirst();
    public GeneralKocioraw(final int mana, final int attackDamage, final int health,
                           final String description, final ArrayList<String> colors,
                           final String name) {
        this.setMana(mana);
        this.setAttackDamage(attackDamage);
        this.setHealth(health);
        this.setDescription(description);
        this.setColors(colors);
        this.setName(name);
    }
    public GeneralKocioraw() { }
    Args applyAbility(final GameTable gameTable, final int allyX) {
        Args args = new Args(gameTable.getGameTable(), allyX, 0, 0, 0);
        this.bloodThirst.ability(args);
        return args;
    }
}
