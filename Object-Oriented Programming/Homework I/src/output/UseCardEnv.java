package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"affectedRow", "command", "handIdx", "affectedRow", "error"})

public final class UseCardEnv {

    public UseCardEnv(final int affectedRow, final String command, final int handIdx) {
        this.affectedRow = affectedRow;
        this.command = command;
        this.handIdx = handIdx;
        this.error = "Chosen card is not of type environment.";
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }
    private int affectedRow;

    public int getAffectedRow() {
        return affectedRow;
    }

    public void setAffectedRow(final int affectedRow) {
        this.affectedRow = affectedRow;
    }

    public int getHandIdx() {
        return handIdx;
    }

    public void setHandIdx(final int handIdx) {
        this.handIdx = handIdx;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    private String error;
    private String command;
    private int handIdx;
}
