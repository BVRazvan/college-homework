package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fileio.Coordinates;

@JsonPropertyOrder({"cardAttacked", "cardAttacker", "command", "error"})
public final class AttackerIsFrozen {
    private Coordinates cardAttacker;
    private Coordinates cardAttacked;

    public Coordinates getCardAttacker() {
        return cardAttacker;
    }

    public void setCardAttacker(final Coordinates cardAttacker) {
        this.cardAttacker = cardAttacker;
    }

    public Coordinates getCardAttacked() {
        return cardAttacked;
    }

    public void setCardAttacked(final Coordinates cardAttacked) {
        this.cardAttacked = cardAttacked;
    }

    public AttackerIsFrozen(final String command, final Coordinates cardAttacked,
                            final Coordinates cardAttacker) {
        this.command = command;
        this.error = "Attacker card is frozen.";
        this.cardAttacked = cardAttacked;
        this.cardAttacker = cardAttacker;
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    private String error;
    private String command;

}
