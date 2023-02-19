package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"affectedRow", "command", "error"})

public final class HeroHasAttacked {

    public HeroHasAttacked(final int affectedRow, final String command) {
        this.affectedRow = affectedRow;
        this.command = command;
        this.error = "Hero has already attacked this turn.";
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
    private int affectedRow;

    public int getAffectedRow() {
        return affectedRow;
    }

    public void setAffectedRow(final int affectedRow) {
        this.affectedRow = affectedRow;
    }
}
