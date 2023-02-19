package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"command", "playerIdx", "output"})

public final class GetPlayerMana {
    private String command;
    private int output, playerIdx;

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void setPlayerIdx(final int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public GetPlayerMana(final String command, final int playerIdx, final int output) {
        this.command = command;
        this.output = output;
        this.playerIdx = playerIdx;
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
