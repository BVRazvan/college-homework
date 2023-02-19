package card;

public final class IsEnvironment {
    public static boolean isEnvironment(final Card card) {
        for (int i = 0; i < Names.ENVIRONMENT.size(); ++i) {
            if (card.getName().equals(Names.ENVIRONMENT.get(i))) {
                return true;
            }
        }
        return false;
    }
    private IsEnvironment() { }
}
