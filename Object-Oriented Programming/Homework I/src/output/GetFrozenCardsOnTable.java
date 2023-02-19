package output;

import java.util.ArrayList;

import card.Card;
import card.FindTypeCard;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@JsonPropertyOrder({"command", "output"})
@JsonIgnoreProperties({"objectMapper"})
public final class GetFrozenCardsOnTable {
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

    public GetFrozenCardsOnTable(final String command, final ArrayList<Card> output) {
        this.command = command;
        for (Card card : output) {
            this.output.addPOJO(FindTypeCard.findTypeCard(card));
        }
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
