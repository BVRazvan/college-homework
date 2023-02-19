package card;

import ability.Args;
import ability.FireStormAbility;
import ability.HeartHoundAbility;
import ability.WinterFellAbility;
import gametable.GameTable;
import gametable.CleanTable;
public final class UseEnvAbility {
    public static void useEnvAbility(final Card card, final GameTable gameTable,
                                     final int affectedRow) {
        ability.Args args;
        switch (card.getName()) {
            case "Firestorm":
                args = new Args(gameTable.getGameTable(), 0, 0, affectedRow, 0);
                FireStormAbility fireStormAbility = new FireStormAbility();
                fireStormAbility.ability(args);
                CleanTable.cleanTable(gameTable);
                break;
            case "Winterfell":
                args = new Args(gameTable.getGameTable(), 0, 0, affectedRow, 0);
                WinterFellAbility winterFellAbility = new WinterFellAbility();
                winterFellAbility.ability(args);
                break;
            case "Heart Hound":
                args = new Args(gameTable.getGameTable(), 0, 0, affectedRow, 0);
                HeartHoundAbility heartHoundAbility = new HeartHoundAbility();
                heartHoundAbility.ability(args);
                CleanTable.cleanTable(gameTable);
                break;
            default:
                break;
        }
    }
    private UseEnvAbility() { }
}
