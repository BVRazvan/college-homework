package pagenavigation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Action;
import input.Input;
import input.User;
import observerdatabase.EventManager;
import visitors.Visitor;

import java.util.Deque;

/**
 * This interface is implemented by every type of page, in order to change the page to a new one.
 *
 */
public interface PageState {

    /**
     * Apply visitor to particular page.
     *
     * @param visitor particular visitor(action to perform);
     * @param input database;
     * @param action to execute;
     * @param actualUser particular user;
     * @param output formal output to be printed;
     */
    void acceptOnceAgain(Visitor visitor,  Input input,  Action action,
                          User actualUser,  ArrayNode output, EventManager eventManager);

    /**
     * Change the page to the homepage of non-authentificated user.
     */
    void goToNoAuthHomePage(ArrayNode output,  User actualUser,  Input input,
                             Action action, Deque<Action> actionDeque);

    /**
     * Change the page to the register page.
     */
    void goToRegPage(ArrayNode output,  User actualUser,  Input input,
                      Action action, Deque<Action> actionDeque);

    /**
     * Change the page to the authentification page.
     */
    void goToAuthPage(ArrayNode output,  User actualUser,  Input input,
                       Action action, Deque<Action> actionDeque);

    /**
     * Change the page to the homepage of the authentificated user.
     */
    void goToAuthHomePage(ArrayNode output,  User actualUser,  Input input,
                           Action action, Deque<Action> actionDeque);

    /**
     * Change the page to the movies page.
     */
    void goToMoviesPage(ArrayNode output,  User actualUser,  Input input,
                         Action action, Deque<Action> actionDeque);

    /**
     * Change the page to the upgrade page.
     */
    void goToUpgPage(ArrayNode output,  User actualUser,  Input input,
                      Action action, Deque<Action> actionDeque);

    /**
     * Change the page to the logout page.
     */
    void goToLogOutPage(ArrayNode output,  User actualUser,  Input input,
                         Action action, Deque<Action> actionDeque);

    /**
     * Change the page to the details page.
     */
    void goToDetailsPage(ArrayNode output,  User actualUser,  Input input,
                          Action action, Deque<Action> actionDeque);
}
