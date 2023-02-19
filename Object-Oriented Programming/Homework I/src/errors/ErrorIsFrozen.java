package errors;

import card.Card;

public final class ErrorIsFrozen {
    public static boolean errorIsFrozen(final Card card) {
        return card.isFrozen();
    }

    private ErrorIsFrozen() { }
}
