package output;

import card.Card;
import card.Hero;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonPropertyOrder({"command", "output", "playerIdx"})
@JsonIgnoreProperties({"objectMapper"})
public final class GetPlayerHero {
    private String command;
    private int playerIdx;

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void setPlayerIdx(final int playerIdx) {
        this.playerIdx = playerIdx;
    }

    public GetPlayerHero(final String command, final int playerIdx, final Card output) {
        this.command = command;
        this.playerIdx = playerIdx;
        this.output = new Hero(output);
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public Card getOutput() {
        return output;
    }

    public void setOutput(final Card output) {
        this.output = output;
    }
    private Card output;
}
