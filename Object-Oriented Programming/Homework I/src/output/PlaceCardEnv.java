package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"affectedRow", "command", "handIdx", "affectedRow", "error"})

public final class PlaceCardEnv {

    public PlaceCardEnv(final int affectedRow, final String command, final int handIdx) {
        this.affectedRow = affectedRow;
        this.command = command;
        this.handIdx = handIdx;
        this.error = "Cannot place environment card on table.";
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
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
    private final int affectedRow;
}
