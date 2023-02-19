package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"command", "handIdx", "affectedRow", "error"})

public final class UseHeartHound {

    public UseHeartHound(final String command, final int affectedRow, final int handIdx) {
        this.command = command;
        this.handIdx = handIdx;
        this.affectedRow = affectedRow;
        this.error = "Cannot steal enemy card since the player's row is full.";
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public int getAffectedRow() {
        return affectedRow;
    }

    public void setAffectedRow(final int affectedRow) {
        this.affectedRow = affectedRow;
    }

    private int affectedRow;
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
