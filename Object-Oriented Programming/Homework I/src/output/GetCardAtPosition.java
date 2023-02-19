package output;


import card.Card;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"command", "output", "x", "y"})
@JsonIgnoreProperties({"objectMapper"})
public final class GetCardAtPosition {
    private String command;
    private Card output;
    private int x, y;

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public Card getOutput() {
        return output;
    }

    public void setOutput(final Card output) {
        this.output = output;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }
    public GetCardAtPosition(final String command, final Card output) {
        this.command = command;
        this.output = output;
        this.x = output.getPlaceX();
        this.y = output.getPlaceY();
    }
}
