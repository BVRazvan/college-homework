package errors;

import card.Card;
import player.Player;

public final class ErrorPlaceMana {
    public static int errorPlaceMana(final Player player, final Card card) {
        if (card.getMana() > player.getMana()) {
            return -1;
        }
        return 0;
    }
    private ErrorPlaceMana() { }
}
