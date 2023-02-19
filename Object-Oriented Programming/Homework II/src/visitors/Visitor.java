package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
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
import input.Action;
public interface Visitor {
    /**
     * Use visitor pattern to apply actions on particular page.
     * @param authHomePage particular page
     */
    void visit(AuthHomePage authHomePage,  Input input,  Action action,
                User actualUser,  ArrayNode output,  PageNavigator pageNavigator,
               EventManager eventManager);

    /**
     * Use visitor pattern to apply actions on particular page.
     * @param authPage particular page
     */
    void visit(AuthPage authPage,  Input input,  Action action,
                User actualUser,  ArrayNode output,  PageNavigator pageNavigator,
               EventManager eventManager);

    /**
     * Use visitor pattern to apply actions on particular page.
     * @param detailsPage particular page
     */
    void visit(DetailsPage detailsPage,  Input input,  Action action,
                User actualUser,  ArrayNode output,  PageNavigator pageNavigator,
               EventManager eventManager);

    /**
     * Use visitor pattern to apply actions on particular page.
     * @param logOutPage particular page
     */
    void visit(LogOutPage logOutPage,  Input input,  Action action,
                User actualUser,  ArrayNode output,  PageNavigator pageNavigator,
               EventManager eventManager);

    /**
     * Use visitor pattern to apply actions on particular page.
     * @param moviesPage particular page
     */
    void visit(MoviesPage moviesPage,  Input input,  Action action,
                User actualUser,  ArrayNode output,  PageNavigator pageNavigator,
               EventManager eventManager);

    /**
     * Use visitor pattern to apply actions on particular page.
     * @param noAuthHomePage particular page
     */
    void visit(NoAuthHomePage noAuthHomePage,  Input input,  Action action,
                User actualUser,  ArrayNode output,  PageNavigator pageNavigator,
               EventManager eventManager);

    /**
     * Use visitor pattern to apply actions on particular page.
     * @param regPage particular page
     */
    void visit(RegPage regPage,  Input input,  Action action,
                User actualUser,  ArrayNode output,  PageNavigator pageNavigator,
               EventManager eventManager);

    /**
     * Use visitor pattern to apply actions on particular page.
     * @param upgPage particular page
     */
    void visit(UpgPage upgPage,  Input input,  Action action,
                User actualUser,  ArrayNode output,  PageNavigator pageNavigator,
               EventManager eventManager);
}
