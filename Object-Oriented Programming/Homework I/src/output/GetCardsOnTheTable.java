package output;

import java.util.ArrayList;

import card.Card;
import card.FindTypeCard;
import gametable.GameTable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@JsonPropertyOrder({"command", "output"})
@JsonIgnoreProperties({"objectMapper"})
public final class GetCardsOnTheTable {

    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public GetCardsOnTheTable(final String command, final ArrayList<Card> output) {
        for (int i = 0; i < GameTable.ROWS; ++i) {
            this.output.add(this.objectMapper.createArrayNode());
        }
        this.command = command;
        for (Card card : output) {
            this.output.get(card.getPlaceX()).addPOJO(FindTypeCard.findTypeCard(card));
        }
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ArrayList<ArrayNode> getOutput() {
        return output;
    }

    public void setOutput(final ArrayList<ArrayNode> output) {
        this.output = output;
    }

    private ArrayList<ArrayNode> output = new ArrayList<>();

}
