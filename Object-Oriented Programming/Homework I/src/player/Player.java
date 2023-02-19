package player;

import errors.ErrorIsFrozen;
import errors.ErrorHasTank;
import errors.ErrorHasAttacked;
import gametable.GameTable;
import card.Hero;
import card.Card;
import card.FindTypeCard;
import card.IsEnvironment;
import errors.ErrorPlaceEnv;
import errors.ErrorPlaceMana;
import output.UseCardEnv;
import output.UseCardEnvRow;
import output.GetEnvironmentCardsInHand;
import output.UseHeartHound;
import output.MinionAttackAlly;
import output.DiscipleAttackEnemy;
import output.AttackedHasTank;
import output.AttackedNotEnemy;
import output.AttackerHasAttacked;
import output.AttackerIsFrozen;
import output.HeroHasAttacked;
import output.HeroAttackedAlly;
import output.NoAbilityMana;
import errors.ErrorHeroHasAttacked;
import errors.ErrorHeroAttackAlly;
import errors.ErrorHeroAttackedEnemy;
import errors.ErrorIsDisciple;
import output.PlaceCardEnv;
import errors.ErrorAttackAlly;
import output.GetPlayerDeck;
import output.PlaceCardRow;
import errors.ErrorPlaceRow;
import output.PlaceCardMana;
import output.GetPlayerMana;
import errors.ErrorChosenRow;
import errors.ErrorAbilityMana;
import output.PlaceCardEnvMana;
import errors.ErrorUseHeartHound;
import output.GetPlayerHero;
import output.GetCardsInHand;
import errors.ErrorChosenRowAttack;
import output.HeroAttackedEnemy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.GameInput;
import fileio.Input;
import fileio.Coordinates;
import fileio.CardInput;
import fileio.ActionsInput;
import fileio.DecksInput;
import fileio.StartGameInput;
import card.FindTypeInput;
import card.UseEnvAbility;
import card.PhysicalAttack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import card.UseCardAbility;
import card.UseHeroAbility;

public final class Player {
    static final int MAX_MANA = 10;
    static final int START_MANA = 0;
    static final int ERROR = -1;
    static final int START_TURNS = 0;
    private int playedTurns;

    public int getPlayedTurns() {
        return playedTurns;
    }

    public void setPlayedTurns(final int playedTurns) {
        this.playedTurns = playedTurns;
    }

    private ArrayList<Card> usedDeck = new ArrayList<>();
    private ArrayList<ArrayList<Card>> decks;
    private ArrayList<Card> cardsInHand = new ArrayList<>();

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(final int deckId) {
        this.deckId = deckId;
    }

    public Player(final int playerId) {
        this.setPlayerId(playerId);
    }

    private int deckId;
    private int playerId;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void setCardsInHand(final ArrayList<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    private int mana;

    public int getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(final int playedGames) {
        this.playedGames = playedGames;
    }

    private int playedGames;
    private int wonGames;
    private boolean isActive;

    private Hero hero;

    public Hero getHero() {
        return hero;
    }

    public void setHero(final Hero hero) {
        this.hero = hero;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(final boolean active) {
        isActive = active;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(final boolean isActive) {
        this.isActive = isActive;
    }

    public ArrayList<Card> getUsedDeck() {
        return usedDeck;
    }

    public void setUsedDeck(final ArrayList<Card> usedDeck) {
        this.usedDeck = usedDeck;
    }

    public ArrayList<ArrayList<Card>> getDecks() {
        return decks;
    }

    public void setDecks(final ArrayList<ArrayList<Card>> decks) {
        this.decks = decks;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getWonGames() {
        return wonGames;
    }

    public void setWonGames(final int wonGames) {
        this.wonGames = wonGames;
    }

    public void addManaToPlayer() {
        this.setMana(this.getMana() + Math.min(this.getPlayedTurns() + 1, MAX_MANA));
    }

    public Player() { }

    public void setup(final Input inputData, final int game) {
        ArrayList<GameInput> gameInput = inputData.getGames();
        StartGameInput startGameInput = gameInput.get(game).getStartGame();

        this.setUsedDeck(this.addUsedDeck());
        int seed = startGameInput.getShuffleSeed();
        Random random = new Random(seed);
        Collections.shuffle(this.getUsedDeck(), random);

        this.cardsInHand.clear();
        this.addCardToHand();
        this.setPlayedTurns(START_TURNS);
        this.setMana(START_MANA);
        this.addManaToPlayer();
    }

    public void addDecksToPlayer(final DecksInput playerDecks) {
        ArrayList<ArrayList<Card>> decks = new ArrayList<>();
        ArrayList<ArrayList<CardInput>> decksInput = playerDecks.getDecks();

        for (int i = 0; i < playerDecks.getNrDecks(); ++i) {
            ArrayList<Card> deck = new ArrayList<>();
            ArrayList<CardInput> deckInput = decksInput.get(i);

            for (int j = 0; j < playerDecks.getNrCardsInDeck(); ++j) {
                CardInput cardInput = deckInput.get(j);
                Card card = new Card(FindTypeInput.findTypeInput(cardInput));
                deck.add(card);
            }
            decks.add(deck);
        }
        this.setDecks(decks);
    }

    public void addCardToHand() {
        if (this.getUsedDeck().size() > 0) {
            Card card = new Card(this.getUsedDeck().get(0));
            this.getCardsInHand().add(card);
            this.getUsedDeck().remove(0);
        }
    }

    public void getPlayerDeck(final ArrayNode output, final String command) {
        ArrayList<Card> outputt = this.getUsedDeck();
        GetPlayerDeck getPlayerDeck = new GetPlayerDeck(command, this.getPlayerId(), outputt);
        try {
            output.addPOJO(getPlayerDeck);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getPlayerHero(final ArrayNode output, final String command) {
        GetPlayerHero getPlayerHero = new GetPlayerHero(command, this.getPlayerId(),
                new Hero(this.getHero()));
        try {
            output.addPOJO(getPlayerHero);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<Card> addUsedDeck() {
        ArrayList<Card> deck = new ArrayList<>();
        ArrayList<ArrayList<Card>> playerDecks = this.getDecks();
        for (int i = 0; i < this.getDecks().get(this.getDeckId()).size(); ++i) {
            Card particularCard = playerDecks.get(this.getDeckId()).get(i);

            Card card = new Card(FindTypeCard.findTypeCard(particularCard));
            deck.add(card);
        }
        return deck;
    }

    public void checkPlaceCard(final GameTable gameTable, final ArrayNode output,
                              final ActionsInput action) {
        int selectedRow;
        int idx = action.getHandIdx();
        if (ErrorPlaceEnv.errorPlaceEnv(this.getCardsInHand().get(idx))) {
            PlaceCardEnv placeCardEnv = new PlaceCardEnv(action.getAffectedRow(),
                    action.getCommand(), idx);
            try {
                output.addPOJO(placeCardEnv);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorPlaceMana.errorPlaceMana(this, this.getCardsInHand().get(idx)) != 0) {
            PlaceCardMana placeCardMana = new PlaceCardMana(action.getCommand(),
                    action.getHandIdx());
            try {
                output.addPOJO(placeCardMana);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            selectedRow = ErrorPlaceRow.errorPlaceRow(this, this.getCardsInHand().get(idx),
                    gameTable);
            if (selectedRow == ERROR) {
                PlaceCardRow placeCardRow = new PlaceCardRow(action.getCommand(), idx);
                try {
                    output.addPOJO(placeCardRow);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                this.placeCard(this.getCardsInHand().get(idx), gameTable, selectedRow);
                this.getCardsInHand().remove(idx);
            }
        }
    }

    void placeCard(final Card card, final GameTable gameTable, final int row) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        if (card.getName().equals("Disciple")) {
            System.out.println(card.getHealth());
        }
        for (int j = 0; j < GameTable.COLUMNS; ++j) {
            if (table.get(row).get(j) == null || table.get(row).get(j).isDead()) {
                Card newCard = new Card(card);
                newCard.setPlaceX(row);
                newCard.setPlaceY(j);
                table.get(row).set(j, newCard);
                break;
            }
        }
        this.setMana(this.getMana() - card.getMana());
    }
    public void getCardsInHandOut(final String command, final ArrayNode output) {
        ArrayList<Card> outputt = this.getCardsInHand();
        GetCardsInHand getCardsInHand = new GetCardsInHand(command, this.getPlayerId(), outputt);
        try {
            output.addPOJO(getCardsInHand);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void getManaOut(final String command, final ArrayNode output) {
        int outputt = this.getMana();
        GetPlayerMana getPlayerMana = new GetPlayerMana(command, this.getPlayerId(), outputt);
        try {
            output.addPOJO(getPlayerMana);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void getEnvironmentCardsInHand(final String command, final ArrayNode output) {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < this.getCardsInHand().size(); ++i) {
            if (IsEnvironment.isEnvironment(this.getCardsInHand().get(i))) {
                Card card = new Card(this.getCardsInHand().get(i));
                cards.add(card);
            }
        }
        GetEnvironmentCardsInHand getEnvironmentCardsInHand =
                new GetEnvironmentCardsInHand(command, this.getPlayerId(), cards);
        try {
            output.addPOJO(getEnvironmentCardsInHand);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void checkEnvironmentCard(final GameTable gameTable, final ArrayNode output,
                                    final ActionsInput action) {
        int idx = action.getHandIdx();
        if (!ErrorPlaceEnv.errorPlaceEnv(this.getCardsInHand().get(idx))) {
            int row = action.getAffectedRow();
            UseCardEnv useCardEnv = new UseCardEnv(row, action.getCommand(), idx);
            try {
                output.addPOJO(useCardEnv);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorPlaceMana.errorPlaceMana(this, this.getCardsInHand().get(idx)) != 0) {
            int row = action.getAffectedRow();
            PlaceCardEnvMana placeCardEnvMana = new PlaceCardEnvMana(row, action.getCommand(),
                    action.getHandIdx());
            try {
                output.addPOJO(placeCardEnvMana);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorChosenRow.errorChosenRow(this, action.getAffectedRow()) != 0) {
            idx = action.getAffectedRow();
            int handidx = action.getHandIdx();
            UseCardEnvRow useCardEnvRow = new UseCardEnvRow(action.getCommand(), idx, handidx);
            try {
                output.addPOJO(useCardEnvRow);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorUseHeartHound.errorUseHeartHound(this.getCardsInHand().get(idx),
                gameTable, action.getHandIdx()) != 0) {
            int row = action.getAffectedRow();
            int handidx = action.getHandIdx();
            UseHeartHound useHeartHound = new UseHeartHound(action.getCommand(), row, handidx);
            try {
                output.addPOJO(useHeartHound);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            this.useEnvAbility(this.getCardsInHand().get(idx), gameTable, action);
        }
    }
    public void useEnvAbility(final Card card, final GameTable gameTable,
                              final ActionsInput action) {
        int idx = action.getHandIdx();
        UseEnvAbility.useEnvAbility(this.getCardsInHand().get(idx), gameTable,
                action.getAffectedRow());
        this.getCardsInHand().remove(idx);
        this.setMana(this.getMana() - card.getMana());
    }
    public void checkAttack(final GameTable gameTable, final ArrayNode output,
                           final ActionsInput action) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        int allyX = action.getCardAttacker().getX();
        int allyY = action.getCardAttacker().getY();
        int enemyX = action.getCardAttacked().getX();
        int enemyY = action.getCardAttacked().getY();
        Coordinates ally = new Coordinates();
        Coordinates enemy = new Coordinates();
        ally.setX(allyX);
        ally.setY(allyY);
        enemy.setX(enemyX);
        enemy.setY(enemyY);
        if (ErrorChosenRowAttack.errorChosenRowAttack(this, enemyX)) {
            AttackedNotEnemy attackedNotEnemy = new AttackedNotEnemy(action.getCommand(),
                    enemy, ally);
            try {
                output.addPOJO(attackedNotEnemy);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorHasAttacked.errorHasAttacked(table.get(allyX).get(allyY))) {
            AttackerHasAttacked attackerHasAttacked = new AttackerHasAttacked(action.getCommand(),
                    enemy, ally);
            try {
                output.addPOJO(attackerHasAttacked);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorIsFrozen.errorIsFrozen(table.get(allyX).get(allyY))) {
            AttackerIsFrozen attackerIsFrozen = new AttackerIsFrozen(action.getCommand(),
                    enemy, ally);
            try {
                output.addPOJO(attackerIsFrozen);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorHasTank.errorHasTank(gameTable, this, action)) {
            AttackedHasTank attackedHasTank = new AttackedHasTank(action.getCommand(), enemy,
                    ally);
            try {
                output.addPOJO(attackedHasTank);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            PhysicalAttack.physicalAttack(gameTable, action);
        }
    }
    public void checkAbility(final GameTable gameTable, final ArrayNode output,
                            final ActionsInput action) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        System.out.println(table.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY()).getName());
        int allyX = action.getCardAttacker().getX();
        int allyY = action.getCardAttacker().getY();
        int enemyX = action.getCardAttacked().getX();
        int enemyY = action.getCardAttacked().getY();
        Coordinates ally = new Coordinates();
        Coordinates enemy = new Coordinates();
        ally.setX(allyX);
        ally.setY(allyY);
        enemy.setX(enemyX);
        enemy.setY(enemyY);
        if (ErrorIsFrozen.errorIsFrozen(table.get(allyX).get(allyY))) {
            AttackerIsFrozen attackerIsFrozen = new AttackerIsFrozen(action.getCommand(),
                    enemy, ally);
            try {
                output.addPOJO(attackerIsFrozen);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorHasAttacked.errorHasAttacked(table.get(allyX).get(allyY))) {
            AttackerHasAttacked attackerHasAttacked = new AttackerHasAttacked(action.getCommand(),
                    enemy, ally);
            try {
                output.addPOJO(attackerHasAttacked);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (table.get(allyX).get(allyY).getName().equals("Disciple")) {
            if (ErrorIsDisciple.errorIsDisciple(this, action)) {
                DiscipleAttackEnemy discipleAttackEnemy =
                        new DiscipleAttackEnemy(action.getCommand(), enemy, ally);
                try {
                    output.addPOJO(discipleAttackEnemy);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                table.get(allyX).get(allyY).setHasAttacked(true);
                UseCardAbility.useCardAbility(gameTable, action);
            }
        } else if (table.get(allyX).get(allyY).getName().equals("The Ripper")
                || table.get(allyX).get(allyY).getName().equals("Miraj")
                || table.get(allyX).get(allyY).getName().equals("The Cursed One")) {
            if (ErrorAttackAlly.errorAttackAlly(this, action)) {
                MinionAttackAlly minionAttackAlly = new MinionAttackAlly(action.getCommand(),
                        enemy, ally);
                try {
                    output.addPOJO(minionAttackAlly);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (ErrorHasTank.errorHasTank(gameTable, this, action)) {
                AttackedHasTank attackedHasTank = new AttackedHasTank(action.getCommand(), enemy,
                        ally);
                try {
                    output.addPOJO(attackedHasTank);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                table.get(allyX).get(allyY).setHasAttacked(true);
                UseCardAbility.useCardAbility(gameTable, action);
            }
        } else {
            table.get(allyX).get(allyY).setHasAttacked(true);
            UseCardAbility.useCardAbility(gameTable, action);
        }
    }
    public void resetFreezeAttack(final GameTable gameTable) {
        int startRow = GameTable.PLAYER_ONE_START_ROW, endRow = GameTable.PLAYER_ONE_END_ROW;
        if (this.getPlayerId() == 2) {
            endRow = GameTable.PLAYER_TWO_END_ROW;
            startRow = GameTable.PLAYER_TWO_START_ROW;
        }
        this.hero.setHasAttacked(false);
        for (int i = startRow; i <= endRow; ++i) {
            for (int j = 0; j < GameTable.COLUMNS; ++j) {
                if (gameTable.getGameTable().get(i).get(j) != null) {
                    gameTable.getGameTable().get(i).get(j).setFrozen(false);
                    gameTable.getGameTable().get(i).get(j).setHasAttacked(false);
                }
            }
        }
    }
    public void checkAbilityHero(final GameTable gameTable, final ArrayNode output,
                                final ActionsInput action) {
        String command = action.getCommand();
        if (ErrorAbilityMana.errorAbilityMana(this, this.getHero())) {
            NoAbilityMana noAbilityMana = new NoAbilityMana(action.getAffectedRow(), command);
            try {
                output.addPOJO(noAbilityMana);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorHeroHasAttacked.errorHeroHasAttacked(this.getHero())) {
            HeroHasAttacked heroHasAttacked = new HeroHasAttacked(action.getAffectedRow(),
                    command);
            try {
                output.addPOJO(heroHasAttacked);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorHeroAttackAlly.errorHeroAttackAlly(this, this.getHero(),
                action.getAffectedRow())) {
            HeroAttackedAlly heroAttackedAlly = new HeroAttackedAlly(action.getAffectedRow(),
                    action.getCommand());
            try {
                output.addPOJO(heroAttackedAlly);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (ErrorHeroAttackedEnemy.errorHeroAttackedEnemy(this, this.getHero(),
                action.getAffectedRow())) {
            HeroAttackedEnemy heroAttackedEnemy = new HeroAttackedEnemy(action.getAffectedRow(),
                    action.getCommand());
            try {
                output.addPOJO(heroAttackedEnemy);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            UseHeroAbility.useHeroAbility(gameTable, this.getHero(), action);
            this.hero.setHasAttacked(true);
            this.setMana(this.getMana() - this.hero.getMana());
        }
    }
}
