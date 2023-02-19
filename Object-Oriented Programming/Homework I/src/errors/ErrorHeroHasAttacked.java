package errors;

import card.Hero;
public final class ErrorHeroHasAttacked {
    public static boolean errorHeroHasAttacked(final Hero hero) {
        return hero.isHasAttacked();
    }

    private ErrorHeroHasAttacked() { }
}
