package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fileio.Coordinates;

@JsonPropertyOrder({"cardAttacker", "command", "error"})
public final class AttackedHasTankHero {
    private Coordinates cardAttacker;

    public Coordinates getCardAttacker() {
        return cardAttacker;
    }

    public void setCardAttacker(final Coordinates cardAttacker) {
        this.cardAttacker = cardAttacker;
    }

    public AttackedHasTankHero(final String command, final Coordinates cardAttacker) {
        this.command = command;
        this.error = "Attacked card is not of type 'Tank'.";
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