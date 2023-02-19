package output;

public final class PlaceCardRow {

    public PlaceCardRow(final String command, final int handIdx) {
        this.command = command;
        this.handIdx = handIdx;
        this.error = "Cannot place card on table since row is full.";
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
}
