package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"command", "output"})
public final class GetPlayerTurn {
    private String command;
    private int output;

    public GetPlayerTurn(final String command, final int output) {
        this.command = command;
        this.output = output;
    }
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
}