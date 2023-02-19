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

/**
 * Using Visitor design pattern in order to implement 'buy tokens' operation on a particular page.
 *
 */
public final class VisitorBuyTokens implements Visitor {

    private static VisitorBuyTokens instanceBuyTokens = null;

    private VisitorBuyTokens() { }

    /**
     * Use singleton pattern to instantiate visitor.
     *
     * @return class instance
     */
    public static VisitorBuyTokens getVisitorBuyTokens() {
        if (instanceBuyTokens == null) {
            instanceBuyTokens = new VisitorBuyTokens();
        }
        return instanceBuyTokens;
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
        int balance = Integer.parseInt(actualUser.getCredentials().getBalance());
        if (balance < action.getCount()) {
            errorDetected(output);
        } else {
            balance -= action.getCount();
            actualUser.getCredentials().setBalance(String.valueOf(balance));
            actualUser.setTokensCount(action.getCount() + actualUser.getTokensCount());
        }
    }
}
