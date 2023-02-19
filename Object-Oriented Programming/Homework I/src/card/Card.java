package card;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fileio.CardInput;

import java.util.ArrayList;
@JsonPropertyOrder({"mana", "attackDamage", "health", "description", "colors", "name"})
@JsonIgnoreProperties({"frozen", "tank", "hasAttacked", "dead",
        "minion", "environment", "hero", "placeX", "placeY"})
public class Card {
    private int mana;

    public int getPlaceX() {
        return placeX;
    }

    public void setPlaceX(final int placeX) {
        this.placeX = placeX;
    }

    public int getPlaceY() {
        return placeY;
    }

    public void setPlaceY(final int placeY) {
        this.placeY = placeY;
    }

    private int placeX;
    private int placeY;
    private boolean isDead;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean hasAttacked;
    private boolean isFrozen;
    private boolean isTank;

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public boolean isTank() {
        return isTank;
    }

    public Card() {
    }

    public Card(final Card card) {
        this.setColors(card.getColors());
        this.setMana(card.getMana());
        this.setName(card.getName());
        this.setDescription(card.getDescription());
        this.setAttackDamage(card.attackDamage);
        this.setHealth(card.getHealth());
        this.setTank(card.isTank());
        this.setFrozen(card.isFrozen());
        this.setHasAttacked(card.isHasAttacked());
        this.setPlaceX(card.getPlaceX());
        this.setPlaceY(card.getPlaceY());
        this.setDead(card.isDead());
    }

    public Card(final CardInput cardInput) {
        this.setColors(cardInput.getColors());
        this.setMana(cardInput.getMana());
        this.setName(cardInput.getName());
        this.setDescription(cardInput.getDescription());
        this.setFrozen(false);
        this.setHealth(cardInput.getHealth());
        this.setAttackDamage(cardInput.getAttackDamage());
        if (cardInput.getName().equals("Warden") || cardInput.getName().equals("Goliath")) {
            this.setTank(true);
        }
    }

    public void setTank(final boolean tank) {
        isTank = tank;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(final boolean frozen) {
        isFrozen = frozen;
    }

    public static final int WEAK_KNESS_DAMAGE = 2;
    public static final int GODS_PLAN_HEALTH = 2;
    public static final int FIRESTORM_HEALTH = 1;
    public static final int EARTH_BORN_HEALTH = 1;
    public static final int BLOOD_THIRST_ATTACK_DAMAGE = 1;
    public static final int HERO_HEALTH = 30;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(final boolean dead) {
        isDead = dead;
    }
}
