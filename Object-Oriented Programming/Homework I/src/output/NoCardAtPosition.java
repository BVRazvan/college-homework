package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"command", "x", "y", "output"})
public final class NoCardAtPosition {
    private String command, output;

    public String getOutput() {
        return output;
    }

    public void setOutput(final String output) {
        this.output = output;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    private int x, y;

    public NoCardAtPosition(final String command, final int x, final int y) {
        this.x = x;
        this.y = y;
        this.command = command;
        this.output = "No card available at that position.";

    }

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

}
