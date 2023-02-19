package card;

import fileio.CardInput;

public final class FindTypeInput {
    public static Card findTypeInput(final CardInput cardInput) {
        for (int i = 0; i < Names.MINIONS.size(); ++i) {
            if (cardInput.getName().equals(Names.MINIONS.get(i))) {
                return new Card(cardInput);
            }
        }
        for (int i = 0; i < Names.ENVIRONMENT.size(); ++i) {
            if (cardInput.getName().equals(Names.ENVIRONMENT.get(i))) {
                return new Environment(cardInput);
            }
        }
        for (int i = 0; i < Names.HERO.size(); ++i) {
            if (cardInput.getName().equals(Names.HERO.get(i))) {
                return new Hero(cardInput);
            }
        }
        return null;
    }
    private FindTypeInput() { }
}
