package output;

import java.util.ArrayList;

import card.Card;
import card.FindTypeCard;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@JsonPropertyOrder({"command", "playerIdx", "output"})
@JsonIgnoreProperties({"objectMapper"})
public final class GetCardsInHand {
    public ArrayNode getOutput() {
        return output;
    }

    public void setOutput(final ArrayNode output) {
        this.output = output;
    }

    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    private int playerIdx;

    public int getPlayerIdx() {
        return playerIdx;
    }
    public GetCardsInHand(final String command, final int playerIdx,
                          final ArrayList<Card> output) {
        this.command = command;
        this.playerIdx = playerIdx;
        for (Card card : output) {
            this.output.addPOJO(FindTypeCard.findTypeCard(card));
        }
    }
    public void setPlayerIdx(final int playerIdx) {
        this.playerIdx = playerIdx;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private ArrayNode output = objectMapper.createArrayNode();

}
