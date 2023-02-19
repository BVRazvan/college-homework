package errors;

import card.Card;

public final class ErrorHasAttacked {
    public static boolean errorHasAttacked(final Card card) {
        return card.isHasAttacked();
    }

    private ErrorHasAttacked() { }
}
