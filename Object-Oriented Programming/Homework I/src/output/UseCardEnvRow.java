package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"command", "affectedRow", "error", "handIdx"})

public final class UseCardEnvRow {

    public UseCardEnvRow(final String command, final int affectedRow, final int handIdx) {
        this.command = command;
        this.affectedRow = affectedRow;
        this.handIdx = handIdx;
        this.error = "Chosen row does not belong to the enemy.";
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
