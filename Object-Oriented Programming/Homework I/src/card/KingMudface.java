package card;

import ability.EarthBorn;

import java.util.ArrayList;
import ability.Args;
import gametable.GameTable;

public final class KingMudface extends Hero {
    public EarthBorn getEarthBorn() {
        return earthBorn;
    }

    public void setEarthBorn(final EarthBorn earthBorn) {
        this.earthBorn = earthBorn;
    }

    private EarthBorn earthBorn = new EarthBorn();
    public KingMudface(final int mana, final int attackDamage, final int health,
                       final String description, final ArrayList<String> colors,
                       final String name) {
        this.setMana(mana);
        this.setAttackDamage(attackDamage);
        this.setHealth(health);
        this.setDescription(description);
        this.setColors(colors);
        this.setName(name);
    }
    public KingMudface() { }
    Args applyAbility(final GameTable gameTable, final int allyX) {
        Args args = new Args(gameTable.getGameTable(), allyX, 0, 0, 0);
        this.earthBorn.ability(args);
        return args;
    }
}
