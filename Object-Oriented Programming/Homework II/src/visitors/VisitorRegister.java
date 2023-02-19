package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Action;
import input.Input;
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
import static output.ErrorDetected.errorDetected;
import static output.OutputDetected.outputDetected;

/**
 * Using Visitor design pattern in order to implement 'register' operation on a particular page.
 *
 */
public final class VisitorRegister implements Visitor {

    private static VisitorRegister instanceRegister = null;

    private VisitorRegister() { }

    /**
     * Use singleton pattern to instantiate visitor.
     *
     * @return class instance
     */
    public static VisitorRegister getVisitorRegister() {
        if (instanceRegister == null) {
            instanceRegister = new VisitorRegister();
        }
        return instanceRegister;
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
        errorDetected(output);
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
        for (User user : input.getUsers()) {
            if (user.getCredentials().getName().equals(action.getCredentials().getName())) {
                actualUser.setActive(false);
                errorDetected(output);
                pageNavigator.setPageState(pageNavigator.getNoAuthHomePage());
                return;
            }
        }
        User newUser = new User(action.getCredentials());
        pageNavigator.setPageState(pageNavigator.getAuthHomePage());
        actualUser.setActive(true);
        input.getUsers().add(newUser);
        actualUser.copyUser(newUser);
        outputDetected(actualUser, output, input);
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
