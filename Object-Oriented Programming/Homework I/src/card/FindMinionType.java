package card;

public final class FindMinionType {
    public static int findMinionType(final Card card) {
        switch (card.getName()) {
            case "The Ripper":
                return TheRipper.getRowPlace();
            case "Miraj":
                return Miraj.getRowPlace();
            case "The Cursed One":
                return TheCursedOne.getRowPlace();
            case "Disciple":
                return Disciple.getRowPlace();
            case "Warden":
                return Warden.getRowPlace();
            case "Sentinel":
                return Sentinel.getRowPlace();
            case "Goliath":
                return Goliath.getRowPlace();
            case "Berserker":
                return Berserker.getRowPlace();
            default: break;
        }
        return 0;
    }
    private FindMinionType() { }
}
