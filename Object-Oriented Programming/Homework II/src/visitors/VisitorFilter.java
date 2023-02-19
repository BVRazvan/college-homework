package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Action;
import input.Input;
import input.Movie;
import input.User;
import observerdatabase.EventManager;
import pagenavigation.NoAuthHomePage;
import pagenavigation.AuthHomePage;
import pagenavigation.DetailsPage;
import pagenavigation.UpgPage;
import pagenavigation.AuthPage;
import pagenavigation.PageNavigator;
import pagenavigation.LogOutPage;
import pagenavigation.MoviesPage;
import pagenavigation.RegPage;
import java.util.ArrayList;
import java.util.Comparator;
import static output.ErrorDetected.errorDetected;
import static output.OutputDetected.outputDetected;
import static pagenavigation.AddInitialMovies.addInitialMovies;

/**
 * Using Visitor design pattern in order to implement 'filter' operation on a particular page.
 *
 */
public final class VisitorFilter implements Visitor {

    private static VisitorFilter instanceFilter = null;

    private VisitorFilter() { }

    /**
     * Use singleton pattern to instantiate visitor.
     *
     * @return class instance
     */
    public static VisitorFilter getVisitorFilter() {
        if (instanceFilter == null) {
            instanceFilter = new VisitorFilter();
        }
        return instanceFilter;
    }

    /**
     * Algorithm for watch visitor on a particular page.
     * @param authHomePage particular page;
     * @param input database input;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     * @param pageNavigator keeps track of the current page.
     */
    @Override
    public void visit(final AuthHomePage authHomePage, final Input input, final Action action,
               final User actualUser, final ArrayNode output, final PageNavigator pageNavigator,
                      final EventManager eventManager) {
        errorDetected(output);
    }

    /**
     * Algorithm for watch visitor on a particular page.
     * @param authPage particular page;
     * @param input database input;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     * @param pageNavigator keeps track of the current page.
     */
    @Override
    public void visit(final AuthPage authPage, final Input input, final Action action,
               final User actualUser, final ArrayNode output, final PageNavigator pageNavigator,
                      final EventManager eventManager) {
        errorDetected(output);
    }

    /**
     * Algorithm for watch visitor on a particular page.
     * @param detailsPage particular page;
     * @param input database input;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     * @param pageNavigator keeps track of the current page.
     */
    @Override
    public void visit(final DetailsPage detailsPage, final Input input, final Action action,
               final User actualUser, final ArrayNode output, final PageNavigator pageNavigator,
                      final EventManager eventManager) {
        errorDetected(output);
    }

    /**
     * Algorithm for watch visitor on a particular page.
     * @param logOutPage particular page;
     * @param input database input;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     * @param pageNavigator keeps track of the current page.
     */
    @Override
    public void visit(final LogOutPage logOutPage, final Input input, final Action action,
               final User actualUser, final ArrayNode output, final PageNavigator pageNavigator,
                      final EventManager eventManager) {
        errorDetected(output);
    }

    /**
     * Algorithm for watch visitor on a particular page.
     * @param moviesPage particular page;
     * @param input database input;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     * @param pageNavigator keeps track of the current page.
     */
    @Override
    public void visit(final MoviesPage moviesPage, final Input input, final Action action,
               final User actualUser, final ArrayNode output, final PageNavigator pageNavigator,
                      final EventManager eventManager) {
        addInitialMovies(actualUser, input);

        if (action.getFilters().getContains() != null) {
            ArrayList<Movie> newList = new ArrayList<>();
            for (Movie movie : actualUser.getCurrentMoviesList()) {
                boolean ok1 = false, ok2 = false;
                for (String string : action.getFilters().getContains().getGenre()) {
                    if (movie.getGenres().contains(string)) {
                        ok1 = true;
                    }
                }
                for (String string : action.getFilters().getContains().getActors()) {
                    if (movie.getActors().contains(string)) {
                        ok2 = true;
                    }
                }

                if (action.getFilters().getContains().getGenre().size() == 0) {
                    ok1 = true;
                }
                if (action.getFilters().getContains().getActors().size() == 0) {
                    ok2 = true;
                }
                if (ok1 && ok2) {
                    newList.add(movie);
                }
            }
            actualUser.setCurrentMoviesList(newList);
        }
        if (action.getFilters().getSort() == null) {
            outputDetected(actualUser, output, input);
            return;
        }

        actualUser.getCurrentMoviesList().sort(new Comparator<Movie>() {
            @Override
            public int compare(final Movie m1, final Movie m2) {
                String s1 = action.getFilters().getSort().getDuration();
                String s2 = action.getFilters().getSort().getRating();
                int comp1 = 0;
                double comp2 = 0;
                if (s1 != null) {
                    if (s1.equals("increasing")) {
                        comp1 = Integer.compare(m1.getDuration(), m2.getDuration());
                    } else {
                        comp1 = Integer.compare(m2.getDuration(), m1.getDuration());
                    }
                }
                if (s2 != null) {
                    if (s2.equals("increasing")) {
                        comp2 = Double.compare(m1.getRating(), m2.getRating());
                    } else {
                        comp2 = Double.compare(m2.getRating(), m1.getRating());
                    }
                }
                if (comp1 != 0) {
                    return comp1;
                }
                return (int) comp2;
            }
        });

        outputDetected(actualUser, output, input);
    }

    /**
     * Algorithm for watch visitor on a particular page.
     * @param noAuthHomePage particular page;
     * @param input database input;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     * @param pageNavigator keeps track of the current page.
     */
    @Override
    public void visit(final NoAuthHomePage noAuthHomePage, final Input input, final Action action,
               final User actualUser, final ArrayNode output, final PageNavigator pageNavigator,
                      final EventManager eventManager) {
        errorDetected(output);
    }

    /**
     * Algorithm for watch visitor on a particular page.
     * @param regPage particular page;
     * @param input database input;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     * @param pageNavigator keeps track of the current page.
     */
    @Override
    public void visit(final RegPage regPage, final Input input, final Action action,
               final User actualUser, final ArrayNode output, final PageNavigator pageNavigator,
                      final EventManager eventManager) {
        errorDetected(output);
    }

    /**
     * Algorithm for watch visitor on a particular page.
     * @param upgPage particular page;
     * @param input database input;
     * @param action actual action to execute;
     * @param actualUser user to execute action;
     * @param output formal output to be printed;
     * @param pageNavigator keeps track of the current page.
     */
    @Override
    public void visit(final UpgPage upgPage, final Input input, final Action action,
               final User actualUser, final ArrayNode output, final PageNavigator pageNavigator,
                      final EventManager eventManager) {
        errorDetected(output);
    }
}
