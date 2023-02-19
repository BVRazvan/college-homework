package card;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fileio.CardInput;

@JsonPropertyOrder({ "mana", "description", "colors", "name"})
@JsonIgnoreProperties({"frozen", "tank", "hasAttacked", "health", "attackDamage", "dead",
        "minion", "environment", "hero", "placeX", "placeY"})
public final class Environment extends Card {
    public Environment(final CardInput cardInput) {
        this.setColors(cardInput.getColors());
        this.setMana(cardInput.getMana());
        this.setName(cardInput.getName());
        this.setDescription(cardInput.getDescription());
        this.setFrozen(false);
        this.setTank(false);
    }
    public Environment(final Card card) {
        this.setColors(card.getColors());
        this.setMana(card.getMana());
        this.setName(card.getName());
        this.setDescription(card.getDescription());
        this.setFrozen(false);
        this.setTank(false);
    }
}
