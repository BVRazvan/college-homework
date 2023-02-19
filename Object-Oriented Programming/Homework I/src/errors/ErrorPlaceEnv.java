package errors;

import card.IsEnvironment;
import card.Card;

public final class ErrorPlaceEnv {
    public static boolean errorPlaceEnv(final Card card) {
        return IsEnvironment.isEnvironment(card);
    }

    private ErrorPlaceEnv() { }
}
