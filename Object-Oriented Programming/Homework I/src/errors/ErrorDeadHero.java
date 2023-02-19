package errors;

import player.Player;

public final class ErrorDeadHero {
    public static boolean errorDeadHero(final Player player) {
        return (player.getHero().getHealth() <= 0);
    }

    private ErrorDeadHero() { }
}
