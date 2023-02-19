package playthegame;

import card.Card;
import errors.ErrorCardAtPosition;
import errors.ErrorIsFrozen;
import errors.ErrorHasTank;
import errors.ErrorDeadHero;
import errors.ErrorHasAttacked;
import gametable.GameTable;
import output.GetCardsOnTheTable;
import output.GetPlayerTurn;
import output.NoCardAtPosition;
import output.GetFrozenCardsOnTable;
import output.AttackerIsFrozenHero;
import output.GetCardAtPosition;
import output.AttackerHasAttackedHero;
import output.AttackedHasTankHero;
import output.DeadHero;
import output.GetTotalGamesPlayed;
import output.GetWonGames;
import player.Player;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.GameInput;
import fileio.Input;
import fileio.ActionsInput;
import fileio.DecksInput;
import fileio.StartGameInput;
import fileio.CardInput;
import fileio.Coordinates;
import card.FindTypeInput;
import card.Hero;
import gametable.CleanTable;
import java.util.ArrayList;
import card.UseAttackHero;

public final class PlayTheGame {
        
        public void playGame(final Input inputData, final ArrayNode output) {
        GameTable gameTable = new GameTable();
        Player player1 = new Player(1);
        Player player2 = new Player(2);
        ArrayList<GameInput> gameInput = inputData.getGames();
        getPlayerDecks(player1, player2, inputData);
        this.getPlayerDecks(player1, player2, inputData);
        for (int game = 0; game < gameInput.size(); ++game) {
            ArrayList<ActionsInput> actionsInput = gameInput.get(game).getActions();
            startGame(player1, player2, inputData, game);
            player1.setup(inputData, game);
            player2.setup(inputData, game);
            gameTable.cleanBoard();
            boolean finishGame = false;
            for (int act = 0; act < actionsInput.size() && !finishGame; ++act) {
                ActionsInput action = actionsInput.get(act);
                switch (action.getCommand()) {
                    case "getPlayerDeck":
                        getCommandPlayer(player1, player2, action).getPlayerDeck(output,
                                action.getCommand());
                        break;
                    case "getPlayerHero":
                        getCommandPlayer(player1, player2, action).getPlayerHero(output,
                                action.getCommand());
                        break;
                    case "getPlayerTurn":
                        getPlayerTurn(player1, player2, output, action.getCommand());
                        break;
                    case "endPlayerTurn":
                        endPlayerTurn(player1, player2, gameTable);
                        checkRoundEnd(player1, player2);
                        break;
                    case "placeCard":
                        getActivePlayer(player1, player2).checkPlaceCard(gameTable, output,
                                action);
                        break;
                    case "getCardsInHand":
                        getCommandPlayer(player1, player2,
                                action).getCardsInHandOut(action.getCommand(), output);
                        break;
                    case "getPlayerMana":
                        getCommandPlayer(player1, player2, action).getManaOut(action.getCommand(),
                                output);
                        break;
                    case "getCardsOnTable":
                        getCardsOnTheTable(gameTable, output, action.getCommand());
                        break;
                    case "getEnvironmentCardsInHand":
                        getCommandPlayer(player1, player2,
                                action).getEnvironmentCardsInHand(action.getCommand(), output);
                        break;
                    case "useEnvironmentCard":
                        getActivePlayer(player1, player2).checkEnvironmentCard(gameTable,
                                output, action);
                        break;
                    case "getCardAtPosition":
                        checkCardAtPosition(gameTable, action.getCommand(), action, output);
                        break;
                    case "getFrozenCardsOnTable":
                        getFrozenCardsOnTheTable(gameTable, action.getCommand(), output);
                        break;
                    case "cardUsesAttack":
                        getActivePlayer(player1, player2).checkAttack(gameTable, output, action);
                        break;
                    case "cardUsesAbility":
                        getActivePlayer(player1, player2).checkAbility(gameTable, output, action);
                        break;
                    case "useAttackHero":
                        if (checkAttackHero(gameTable, action, output,
                                getActivePlayer(player1, player2)) == 0) {
                            attackHero(player1, player2, gameTable, action);
                            checkHeroHealth(player1, player2, output);
                        }
                        break;
                    case "useHeroAbility":
                        getActivePlayer(player1, player2).checkAbilityHero(gameTable, output,
                                action);
                        break;
                    case "getTotalGamesPlayed":
                        getTotalGamesPlayed(getActivePlayer(player1, player2), output, action);
                        break;
                    case "getPlayerOneWins":
                        getPlayerOneWins(player1, output, action);
                        break;
                    case "getPlayerTwoWins":
                        getPlayerTwoWins(player2, output, action);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    void getCardsOnTheTable(final GameTable gameTable,
                            final ArrayNode output, final String command) {
        ArrayList<Card> cardsOnTheTable = new ArrayList<>();
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        for (int i = 0; i < GameTable.ROWS; ++i) {
            for (int j = 0; j < GameTable.COLUMNS; ++j) {
                if (table.get(i).get(j) != null && !table.get(i).get(j).isDead()) {
                    Card card = new Card(table.get(i).get(j));
                    cardsOnTheTable.add(card);
                }
            }

        }
        GetCardsOnTheTable getCardsOnTheTable = new GetCardsOnTheTable(command, cardsOnTheTable);
        try {
            output.addPOJO(getCardsOnTheTable);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Player getCommandPlayer(final Player player1, final Player player2,
                            final ActionsInput action) {
        if (action.getPlayerIdx() == 1) {
            return player1;
        } else {
            return player2;
        }
    }

    Player getActivePlayer(final Player player1, final Player player2) {
        return player1.isActive() ? player1 : player2;
    }

    void getPlayerDecks(final Player player1, final Player player2, final Input inputData) {
        DecksInput playerDecks = inputData.getPlayerOneDecks();
        player1.addDecksToPlayer(playerDecks);
        playerDecks = inputData.getPlayerTwoDecks();
        player2.addDecksToPlayer(playerDecks);
    }

    void startGame(final Player player1, final Player player2,
                   final Input inputData, final int game) {
        ArrayList<GameInput> gameInput = inputData.getGames();
        StartGameInput startGameInput = gameInput.get(game).getStartGame();
        int idx1 = startGameInput.getPlayerOneDeckIdx();
        int idx2 = startGameInput.getPlayerTwoDeckIdx();
        player1.setDeckId(idx1);
        player2.setDeckId(idx2);
        player1.setHero(addUserHero(startGameInput.getPlayerOneHero()));
        player2.setHero(addUserHero(startGameInput.getPlayerTwoHero()));

        if (startGameInput.getStartingPlayer() == 1) {
            player1.setActive(true);
            player2.setActive(false);
        } else {
            player2.setActive(true);
            player1.setActive(false);
        }
    }

    Hero addUserHero(final CardInput hero) {
        return (Hero) FindTypeInput.findTypeInput(hero);
    }

    void checkRoundEnd(final Player player1, final Player player2) {
        if (player1.getPlayedTurns() == player2.getPlayedTurns()) {
            player1.addManaToPlayer();
            player2.addManaToPlayer();
            player1.addCardToHand();
            player2.addCardToHand();
        }
    }

    void getPlayerTurn(final Player player1, final Player player2,
                       final ArrayNode output, final String command) {
        int playerTurn = player1.isActive() ? player1.getPlayerId() : player2.getPlayerId();
        GetPlayerTurn getPlayerTurn = new GetPlayerTurn(command, playerTurn);
        try {
            output.addPOJO(getPlayerTurn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void endPlayerTurn(final Player player1, final Player player2, final GameTable gameTable) {
        if (player1.getIsActive()) {
            player1.resetFreezeAttack(gameTable);

            player1.setPlayedTurns(player1.getPlayedTurns() + 1);
            player1.setActive(false);
            player2.setActive(true);
        } else {
            player2.resetFreezeAttack(gameTable);
            player2.setPlayedTurns(player2.getPlayedTurns() + 1);
            player1.setActive(true);
            player2.setActive(false);
        }
    }

    void checkCardAtPosition(final GameTable gameTable, final String command,
                             final ActionsInput action, final ArrayNode output) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        if (ErrorCardAtPosition.errorCardAtPosition(gameTable, action)) {
            NoCardAtPosition noCardAtPosition =
                    new NoCardAtPosition(command, action.getX(), action.getY());
            try {
                output.addPOJO(noCardAtPosition);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            int x = action.getX();
            int y = action.getY();
            Card card = new Card(table.get(x).get(y));
            GetCardAtPosition getCardAtPosition = new GetCardAtPosition(command, card);
            try {
                output.addPOJO(getCardAtPosition);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void getFrozenCardsOnTheTable(final GameTable gameTable,
                                  final String command, final ArrayNode output) {
        ArrayList<Card> frozenCards = new ArrayList<>();
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();

        for (int i = 0; i < GameTable.ROWS; ++i) {
            for (int j = 0; j < GameTable.COLUMNS; ++j) {
                Card card = table.get(i).get(j);
                if (card == null) {
                    continue;
                } else if (card.isDead()) {
                    CleanTable.cleanTable(gameTable);
                } else if (card.isFrozen()) {
                    Card newCard = new Card(card);
                    frozenCards.add(newCard);
                }
            }
        }
        GetFrozenCardsOnTable getFrozenCardsOnTheTable =
                new GetFrozenCardsOnTable(command, frozenCards);
        try {
            output.addPOJO(getFrozenCardsOnTheTable);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int checkAttackHero(final GameTable gameTable, final ActionsInput action,
                               final ArrayNode output, final Player player) {
        ArrayList<ArrayList<Card>> table = gameTable.getGameTable();
        int allyX = action.getCardAttacker().getX();
        int allyY = action.getCardAttacker().getY();
        Coordinates ally = new Coordinates();
        ally.setX(allyX);
        ally.setY(allyY);

        if (ErrorIsFrozen.errorIsFrozen(table.get(allyX).get(allyY))) {
            AttackerIsFrozenHero attackerIsFrozenHero =
                    new AttackerIsFrozenHero(action.getCommand(), ally);
            try {
                output.addPOJO(attackerIsFrozenHero);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return GameTable.ERROR;
        } else if (ErrorHasAttacked.errorHasAttacked(table.get(allyX).get(allyY))) {
            AttackerHasAttackedHero attackerHasAttackedHero =
                    new AttackerHasAttackedHero(action.getCommand(), ally);
            try {
                output.addPOJO(attackerHasAttackedHero);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return GameTable.ERROR;
        } else if (ErrorHasTank.errorHasTank(gameTable, player, action)) {
            AttackedHasTankHero attackedHasTankHero =
                    new AttackedHasTankHero(action.getCommand(), ally);
            try {
                output.addPOJO(attackedHasTankHero);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return GameTable.ERROR;
        } else {
            table.get(allyX).get(allyY).setHasAttacked(true);
            return 0;
        }
    }

    void attackHero(final Player player1, final Player player2,
                    final GameTable gameTable, final ActionsInput action) {
        if (player1.isActive()) {
            UseAttackHero.useAttackHero(player2, gameTable, action);
        } else {
            UseAttackHero.useAttackHero(player1, gameTable, action);
        }
    }

    void checkHeroHealth(final Player player1, final Player player2, final ArrayNode output) {
        if (ErrorDeadHero.errorDeadHero(player2)) {
            player1.setWonGames(player1.getWonGames() + 1);

            String command = "Player one killed the enemy hero.";
            DeadHero deadHero = new DeadHero(command);
            try {
                output.addPOJO(deadHero);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            player1.setPlayedGames(player1.getPlayedGames() + 1);
            player2.setPlayedGames(player2.getPlayedGames() + 1);
        } else if (ErrorDeadHero.errorDeadHero(player1)) {
            player2.setWonGames(player2.getWonGames() + 1);

            String command = "Player two killed the enemy hero.";
            DeadHero deadHero = new DeadHero(command);
            try {
                output.addPOJO(deadHero);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            player1.setPlayedGames(player1.getPlayedGames() + 1);
            player2.setPlayedGames(player2.getPlayedGames() + 1);
        }
    }
    void getTotalGamesPlayed(final Player player, final ArrayNode output,
                             final ActionsInput action) {
        String command = action.getCommand();
        int outputt = player.getPlayedGames();
        GetTotalGamesPlayed getTotalGamesPlayed = new GetTotalGamesPlayed(command, outputt);
        try {
            output.addPOJO(getTotalGamesPlayed);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    void getPlayerOneWins(final Player player, final ArrayNode output, final ActionsInput action) {
        String command = action.getCommand();
        int outputt = player.getWonGames();
        GetWonGames getWonGames = new GetWonGames(command, outputt);
        try {
            output.addPOJO(getWonGames);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    void getPlayerTwoWins(final Player player, final ArrayNode output, final ActionsInput action) {
        String command = action.getCommand();
        int outputt = player.getWonGames();
        GetWonGames getWonGames = new GetWonGames(command, outputt);
        try {
            output.addPOJO(getWonGames);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
