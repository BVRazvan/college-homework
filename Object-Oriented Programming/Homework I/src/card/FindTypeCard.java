package card;

public final class FindTypeCard {
    public static Card findTypeCard(final Card card) {
        for (int i = 0; i < Names.MINIONS.size(); ++i) {
            if (card.getName().equals(Names.MINIONS.get(i))) {
                return new Card(card);
            }
        }
        for (int i = 0; i < Names.ENVIRONMENT.size(); ++i) {
            if (card.getName().equals(Names.ENVIRONMENT.get(i))) {
                return new Environment(card);
            }
        }
        for (int i = 0; i < Names.HERO.size(); ++i) {
            if (card.getName().equals(Names.HERO.get(i))) {
                return new Hero(card);
            }
        }
        return null;
    }
    private FindTypeCard() { }
}
