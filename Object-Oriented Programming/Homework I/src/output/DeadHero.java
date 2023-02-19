package output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"gameEnded"})
public final class DeadHero {

    private String gameEnded;

    public DeadHero(final String gameEnded) {
        this.gameEnded = gameEnded;
    }

    public String getGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(final String gameEnded) {
        this.gameEnded = gameEnded;
    }
}
