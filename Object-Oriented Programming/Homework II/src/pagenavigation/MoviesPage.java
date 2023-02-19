package pagenavigation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.*;
import observerdatabase.EventManager;
import visitors.Visitor;

import java.util.ArrayList;
import java.util.Deque;

import static output.ErrorDetected.errorDetected;
import static output.OutputDetected.outputDetected;
import static pagenavigation.AddInitialMovies.addInitialMovies;

public final class MoviesPage implements PageState {

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

    public PageNavigator getPageNavigator() {
        return pageNavigator;
    }

    public void setPageNavigator(final PageNavigator pageNavigator) {
        this.pageNavigator = pageNavigator;
    }

    private PageNavigator pageNavigator;

    public MoviesPage(final PageNavigator pageNavigator) {
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
        this.pageNavigator.setPageState(this.pageNavigator.getAuthHomePage());
        actionDeque.addFirst(action);
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
        ArrayList<Movie> movies = new ArrayList<>();
        for (Movie movie : actualUser.getCurrentMoviesList()) {
            if (movie.getName().equals(action.getMovie())) {
                this.pageNavigator.setPageState(this.pageNavigator.getDetailsPage());
                movies.add(movie);
                actualUser.setCurrentMoviesList(movies);
                outputDetected(actualUser, output, input);
                actionDeque.addFirst(action);
                return;
            }
        }
        errorDetected(output);
        addInitialMovies(actualUser, input);
    }
}
