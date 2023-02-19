package errors;

import player.Player;
import card.Hero;
public final class ErrorAbilityMana {
    public static boolean errorAbilityMana(final Player player, final Hero hero) {
        return player.getMana() < hero.getMana();
    }

    private ErrorAbilityMana() { }
}
