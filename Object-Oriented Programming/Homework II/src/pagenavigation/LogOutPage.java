package pagenavigation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Action;
import input.Input;
import input.User;
import observerdatabase.EventManager;
import visitors.Visitor;

import java.util.Deque;

import static output.ErrorDetected.errorDetected;

public final class LogOutPage implements PageState {

    /**
     * Apply visitor to particular page.
     *
     * @param visitor particular visitor(action to perform);
     * @param input database;
     * @param action to execute;
     * @param actualUser particular user;
     * @param output formal output to be printed;
     */
    public void acceptOnceAgain(final Visitor visitor, final Input input, final Action action,
                                final User actualUser, final ArrayNode output,
                                final EventManager eventManager) {
        visitor.visit(this, input, action, actualUser, output, this.getPageNavigator(),
                eventManager);
    }

    private PageNavigator pageNavigator;

    public PageNavigator getPageNavigator() {
        return pageNavigator;
    }

    public void setPageNavigator(final PageNavigator pageNavigator) {
        this.pageNavigator = pageNavigator;
    }

    public LogOutPage(final PageNavigator pageNavigator) {
        this.pageNavigator = pageNavigator;
    }

    @Override
    public void goToNoAuthHomePage(final ArrayNode output, final User actualUser,
                                   final Input input, final Action action,
                                   final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToRegPage(final ArrayNode output, final User actualUser, final Input input,
                            final Action action, final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToAuthPage(final ArrayNode output, final User actualUser, final Input input,
                             final Action action, final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToAuthHomePage(final ArrayNode output, final User actualUser, final Input input,
                                 final Action action, final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToMoviesPage(final ArrayNode output, final User actualUser, final Input input,
                               final Action action, final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToUpgPage(final ArrayNode output, final User actualUser, final Input input,
                            final Action action, final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToLogOutPage(final ArrayNode output, final User actualUser, final Input input,
                               final Action action, final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToDetailsPage(final ArrayNode output, final User actualUser, final Input input,
                                final Action action, final Deque<Action> actionDeque) {
        errorDetected(output);
    }
}
