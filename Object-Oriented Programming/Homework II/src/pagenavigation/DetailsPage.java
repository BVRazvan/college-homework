package pagenavigation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Action;
import input.Credentials;
import input.Input;
import input.User;
import observerdatabase.EventManager;
import output.OutputDetected;
import visitors.Visitor;

import java.util.Deque;

import static pagenavigation.AddInitialMovies.addInitialMovies;
import static output.ErrorDetected.errorDetected;

public final class DetailsPage implements PageState {

    public PageNavigator getPageNavigator() {
        return pageNavigator;
    }

    public void setPageNavigator(final PageNavigator pageNavigator) {
        this.pageNavigator = pageNavigator;
    }

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

    public DetailsPage(final PageNavigator pageNavigator) {
        this.pageNavigator = pageNavigator;
    }
    @Override
    public void goToNoAuthHomePage(final ArrayNode output, final User actualUser,
                                   final Input input, final Action action,
                                   final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToRegPage(final ArrayNode output, final User actualUser,
                            final Input input, final Action action,
                            final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToAuthPage(final ArrayNode output, final User actualUser,
                             final Input input, final Action action,
                             final Deque<Action> actionDeque) {
        errorDetected(output);
    }

    @Override
    public void goToAuthHomePage(final ArrayNode output, final User actualUser, final Input input,
                                 final Action action, final Deque<Action> actionDeque) {
        this.pageNavigator.setPageState(this.pageNavigator.getAuthHomePage());
        actionDeque.addFirst(action);
    }

    @Override
    public void goToMoviesPage(final ArrayNode output, final User actualUser, final Input input,
                               final Action action, final Deque<Action> actionDeque) {
        this.pageNavigator.setPageState(this.pageNavigator.getMoviesPage());
        addInitialMovies(actualUser, input);
        actionDeque.addFirst(action);
        OutputDetected.outputDetected(actualUser, output, input);
    }

    @Override
    public void goToUpgPage(final ArrayNode output, final User actualUser, final Input input,
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
