package pagenavigation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Input;
import input.User;
import observerdatabase.EventManager;
import visitors.Visitable;
import visitors.Visitor;
import input.Action;

import java.util.Deque;

public final class PageNavigator implements Visitable {

    /**
     * Moves visitor to actual page.
     *
     * @param visitor particular visitor(action to perform);
     * @param input database;
     * @param action to execute;
     * @param actualUser particular user;
     * @param output formal output to be printed;
     */
    public void accept(final Visitor visitor, final Input input, final Action action,
                       final User actualUser, final ArrayNode output,
                       final EventManager eventManager) {
        this.getPageState().acceptOnceAgain(visitor, input, action, actualUser, output,
                eventManager);
    }
    public PageState getNoAuthHomePage() {
        return noAuthHomePage;
    }

    public void setNoAuthHomePage(final PageState noAuthHomePage) {
        this.noAuthHomePage = noAuthHomePage;
    }

    public PageState getRegPage() {
        return regPage;
    }

    public void setRegPage(final PageState regPage) {
        this.regPage = regPage;
    }

    public PageState getAuthPage() {
        return authPage;
    }

    public void setAuthPage(final PageState authPage) {
        this.authPage = authPage;
    }

    public PageState getAuthHomePage() {
        return authHomePage;
    }

    public void setAuthHomePage(final PageState authHomePage) {
        this.authHomePage = authHomePage;
    }

    public PageState getMoviesPage() {
        return moviesPage;
    }

    public void setMoviesPage(final PageState moviesPage) {
        this.moviesPage = moviesPage;
    }

    public PageState getUpgPage() {
        return upgPage;
    }

    public void setUpgPage(final PageState upgPage) {
        this.upgPage = upgPage;
    }

    public PageState getLogOutPage() {
        return logOutPage;
    }

    public void setLogOutPage(final PageState logOutPage) {
        this.logOutPage = logOutPage;
    }

    public PageState getDetailsPage() {
        return detailsPage;
    }

    public void setDetailsPage(final PageState detailsPage) {
        this.detailsPage = detailsPage;
    }

    public PageState getPageState() {
        return pageState;
    }

    public void setPageState(final PageState pageState) {
        this.pageState = pageState;
    }

    public boolean isHasUser() {
        return hasUser;
    }

    public void setHasUser(final boolean hasUser) {
        this.hasUser = hasUser;
    }

    private PageState noAuthHomePage;
    private PageState regPage;
    private PageState authPage;
    private PageState authHomePage;
    private PageState moviesPage;
    private PageState upgPage;
    private PageState logOutPage;
    private PageState detailsPage;

    private PageState pageState;

    private boolean hasUser;

    public PageNavigator() {
        noAuthHomePage = new NoAuthHomePage(this);
        regPage = new RegPage(this);
        authPage = new AuthPage(this);
        authHomePage = new AuthHomePage(this);
        moviesPage = new MoviesPage(this);
        upgPage = new UpgPage(this);
        logOutPage = new LogOutPage(this);
        detailsPage = new DetailsPage(this);

        pageState = noAuthHomePage;
        hasUser = false;
    }

    /**
     * Change the page to the homepage of non-authentificated user.
     */
    public void goToNoAuthHomePage(final ArrayNode output, final User actualUser,
                                   final Input input, final Action action,
                                   final Deque<Action> actionDeque) {
        pageState.goToNoAuthHomePage(output, actualUser, input, action, actionDeque);
    }

    /**
     * Change the page to the register page.
     */
    public void goToRegPage(final ArrayNode output, final User actualUser, final Input input,
                            final Action action, final Deque<Action> actionDeque) {
        pageState.goToRegPage(output, actualUser, input, action, actionDeque);
    }

    /**
     * Change the page to the authentification page.
     */
    public void goToAuthPage(final ArrayNode output, final User actualUser, final Input input,
                             final Action action, final Deque<Action> actionDeque) {
        pageState.goToAuthPage(output, actualUser, input, action, actionDeque);
    }

    /**
     * Change the page to the homepage of the authentificated user.
     */
    public void goToAuthHomePage(final ArrayNode output, final User actualUser, final Input input,
                                 final Action action, final Deque<Action> actionDeque) {
        pageState.goToAuthHomePage(output, actualUser, input, action, actionDeque);
    }

    /**
     * Change the page to the movies page.
     */
    public void goToMoviesPage(final ArrayNode output, final User actualUser, final Input input,
                               final Action action, final Deque<Action> actionDeque) {
        pageState.goToMoviesPage(output, actualUser, input, action, actionDeque);
    }

    /**
     * Change the page to the upgrade page.
     */
    public void goToUpgPage(final ArrayNode output, final User actualUser, final Input input,
                            final Action action, final Deque<Action> actionDeque) {
        pageState.goToUpgPage(output, actualUser, input, action, actionDeque);
    }

    /**
     * Change the page to the logout page.
     */
    public void goToLogOutPage(final ArrayNode output, final User actualUser, final Input input,
                               final Action action, final Deque<Action> actionDeque) {
        pageState.goToLogOutPage(output, actualUser, input, action, actionDeque);
    }

    /**
     * Change the page to the details page.
     */
    public void goToDetailsPage(final ArrayNode output, final User actualUser, final Input input,
                                final Action action, final Deque<Action> actionDeque) {
        pageState.goToDetailsPage(output, actualUser, input, action, actionDeque);
    }
}
