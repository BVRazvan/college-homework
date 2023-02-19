package card;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fileio.CardInput;

@JsonPropertyOrder({"colors", "description", "health", "mana", "name"})
@JsonIgnoreProperties({"frozen", "tank", "hasAttacked", "attackDamage", "dead",
        "minion", "environment", "hero", "placeX", "placeY"})
public class Hero extends Card {

    public Hero(final CardInput cardInput) {
        this.setColors(cardInput.getColors());
        this.setMana(cardInput.getMana());
        this.setName(cardInput.getName());
        this.setDescription(cardInput.getDescription());
        this.setFrozen(false);
        this.setTank(false);
        this.setHealth(cardInput.getHealth());
        this.setAttackDamage(cardInput.getAttackDamage());
        this.setHealth(HERO_HEALTH);
    }
    public Hero(final Card card) {
        this.setColors(card.getColors());
        this.setMana(card.getMana());
        this.setName(card.getName());
        this.setDescription(card.getDescription());
        this.setFrozen(false);
        this.setTank(false);
        this.setAttackDamage(card.getAttackDamage());
        this.setHealth(card.getHealth());
        this.setPlaceX(card.getPlaceX());
        this.setPlaceY(card.getPlaceY());
    }
    public Hero(final Hero hero) {
        this.setColors(hero.getColors());
        this.setMana(hero.getMana());
        this.setName(hero.getName());
        this.setDescription(hero.getDescription());
        this.setFrozen(false);
        this.setTank(false);
        this.setAttackDamage(hero.getAttackDamage());
        this.setHealth(hero.getHealth());
        this.setPlaceX(hero.getPlaceX());
        this.setPlaceY(hero.getPlaceY());
    }
    public Hero() {
        this.setHealth(HERO_HEALTH);
    }
}
