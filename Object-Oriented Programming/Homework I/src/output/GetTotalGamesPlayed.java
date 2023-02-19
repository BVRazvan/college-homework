package output;

public final class GetTotalGamesPlayed {
    private String command;
    private int output;

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(final int output) {
        this.output = output;
    }
    public GetTotalGamesPlayed(final String command, final int output) {
        this.command = command;
        this.output = output;
    }
}
