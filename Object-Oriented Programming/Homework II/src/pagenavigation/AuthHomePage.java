package pagenavigation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Action;
import input.Credentials;
import input.Input;
import input.User;
import observerdatabase.EventManager;
import visitors.Visitor;

import java.util.Deque;

import static output.OutputDetected.outputDetected;
import static pagenavigation.AddInitialMovies.addInitialMovies;
import static output.ErrorDetected.errorDetected;

public final class AuthHomePage implements PageState {

    /**
     * Move visitor to actual page state.
     *
     * @param visitor;
     * @param input database;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     */
    public void acceptOnceAgain(final Visitor visitor, final Input input, final Action action,
                                final User actualUser, final ArrayNode output,
                                final EventManager eventManager) {
        visitor.visit(this, input, action, actualUser, output, this.getPageNavigator(),
                eventManager);
    }

    public PageNavigator getPageNavigator() {
        return pageNavigator;
    }

    public void setPageNavigator(final PageNavigator pageNavigator) {
        this.pageNavigator = pageNavigator;
    }

    private PageNavigator pageNavigator;

    public AuthHomePage(final PageNavigator pageNavigator) {
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
        this.pageNavigator.setPageState(this.pageNavigator.getMoviesPage());
        addInitialMovies(actualUser, input);
        outputDetected(actualUser, output, input);
        actionDeque.addFirst(action);
    }

    @Override
    public void goToUpgPage(final ArrayNode output, final User actualUse, final Input input,
                            final Action action, final Deque<Action> actionDeque) {
        this.pageNavigator.setPageState(this.pageNavigator.getUpgPage());
        actionDeque.addFirst(action);
    }

    @Override
    public void goToLogOutPage(final ArrayNode output, final User actualUser, final Input input,
                               final Action action, final Deque<Action> actionDeque) {
        actionDeque.clear();
        this.pageNavigator.setPageState(this.pageNavigator.getNoAuthHomePage());
        actualUser.setActive(false);
        actualUser.getCurrentMoviesList().clear();
        for (User user : input.getUsers()) {
            Credentials userCredentials = actualUser.getCredentials();
            if (userCredentials.getName().equals(user.getCredentials().getName())
                    && userCredentials.getPassword().equals(user.getCredentials().getPassword())) {
                user.copyUser(actualUser);
                return;
            }
        }
    }

    @Override
    public void goToDetailsPage(final ArrayNode output, final User actualUser, final Input input,
                                final Action action, final Deque<Action> actionDeque) {
        errorDetected(output);
    }
}
