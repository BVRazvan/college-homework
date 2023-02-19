package pagenavigation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.*;
import observerdatabase.EventManager;
import visitors.*;

import java.util.*;
import java.util.stream.Collectors;

import static output.ErrorDetected.errorDetected;
import static output.OutputDetected.outputDetected;
import static pagenavigation.AddInitialMovies.addInitialMovies;

public final class PageNavigation {
    private PageNavigation pageNavigation;

    public PageNavigation getPageNavigation() {
        return pageNavigation;
    }

    public void setPageNavigation(final PageNavigation pageNavigation) {
        this.pageNavigation = pageNavigation;
    }

    private User actualUser = new User();

    public User getActualUser() {
        return actualUser;
    }

    public void setActualUser(final User actualUser) {
        this.actualUser = actualUser;
    }
    private Deque<Action> actionDeque = new ArrayDeque<>();

    public Deque<Action> getActionDeque() {
        return actionDeque;
    }

    public void setActionDeque(final Deque<Action> actionDeque) {
        this.actionDeque = actionDeque;
    }

    /**
     * Core method, where actions from input are executed.
     *
     * @param input data from input
     * @param output results for actual test
     */
    public void simulateNavigation(final Input input, final ArrayNode output) {
        PageNavigator pageNavigator = new PageNavigator();
        EventManager eventManager = new EventManager();

        initiateObserverDependency(eventManager, input);

        for (Action action : input.getActions()) {
            if (action.getType().equals("change page")) {
                this.changePage(pageNavigator, input, action, action.getPage(), output,
                        this.actionDeque);
            } else if (action.getType().equals("on page")) {
                this.doAction(pageNavigator, input, action.getFeature(), action, output,
                        eventManager);
            } else if (action.getType().equals("back")) {
                this.goBack(pageNavigator, input, action.getFeature(), action, output,
                        this.actionDeque);
            } else if (action.getType().equals("subscribe")) {
                this.doSubscribe(pageNavigator, input, action.getFeature(), action, output,
                        eventManager);
            } else if (action.getType().equals("database")) {
                this.databaseAction(pageNavigator, input, action.getFeature(), action, output,
                        eventManager);
            }
        }


        if (actualUser.isActive() && actualUser.getCredentials().getAccountType().
                equals("premium")) {
            this.giveRecommandation(actualUser, input, output);
        }


    }
    private void changePage(final PageNavigator pageNavigator, final Input input,
                            final Action action, final String page,
                            final ArrayNode output, final Deque<Action> actionDeque) {
        switch (page) {
            case "login":
                pageNavigator.goToAuthPage(output, this.getActualUser(), input, action,
                        actionDeque);
                break;
            case "register":
                pageNavigator.goToRegPage(output, this.getActualUser(), input, action,
                        actionDeque);
                break;
            case "logout":
                pageNavigator.goToLogOutPage(output, this.getActualUser(), input, action,
                        actionDeque);
                break;
            case "movies":
                pageNavigator.goToMoviesPage(output, this.getActualUser(), input, action,
                        actionDeque);
                break;
            case "see details":
                pageNavigator.goToDetailsPage(output, this.getActualUser(), input, action,
                        actionDeque);
                break;
            case "upgrades":
                pageNavigator.goToUpgPage(output, this.getActualUser(), input, action,
                        actionDeque);
                break;
            default:
                break;
        }
    }
    private void doAction(final PageNavigator pageNavigator, final Input input,
                          final String command, final Action action,
                          final ArrayNode output, final EventManager eventManager) {
        switch (command) {
            case "login":
                Visitor visitor = VisitorLogin.getVisitorLogin();
                pageNavigator.accept(visitor, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "register":
                Visitor visitorRegister = VisitorRegister.getVisitorRegister();
                pageNavigator.accept(visitorRegister, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "search":
                Visitor visitorSearch = VisitorSearch.getVisitorSearch();
                pageNavigator.accept(visitorSearch, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "filter":
                Visitor visitorFilter = VisitorFilter.getVisitorFilter();
                pageNavigator.accept(visitorFilter, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "buy tokens":
                Visitor visitorBuyTokens = VisitorBuyTokens.getVisitorBuyTokens();
                pageNavigator.accept(visitorBuyTokens, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "buy premium account":
                Visitor visitorPremiumAcc = VisitorPremiumAcc.getVisitorPremiumAcc();
                pageNavigator.accept(visitorPremiumAcc, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "purchase":
                Visitor visitorPurchase = VisitorPurchase.getVisitorPurchase();
                pageNavigator.accept(visitorPurchase, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "watch":
                Visitor visitorWatch = VisitorWatch.getVisitorWatch();
                pageNavigator.accept(visitorWatch, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "like":
                Visitor visitorLike = VisitorLike.getVisitorLike();
                pageNavigator.accept(visitorLike, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "rate":
                Visitor visitorRate = VisitorRate.getVisitorRate();
                pageNavigator.accept(visitorRate, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            case "subscribe":
                Visitor visitorSubscribe = VisitorSubscribe.getVisitorSubscribe();
                pageNavigator.accept(visitorSubscribe, input, action, this.getActualUser(),
                        output, eventManager);
                break;
            default:
                break;
        }
    }
    private void goBack(final PageNavigator pageNavigator, final Input input,
                        final String command, final Action action,
                        final ArrayNode output, final Deque<Action> actionDeque) {
        if (!this.actualUser.isActive() || actionDeque.isEmpty()) {
            errorDetected(output);
            return;
        }
        actionDeque.pop();
        if (actionDeque.isEmpty()) {
            pageNavigator.goToAuthHomePage(output, this.getActualUser(), input, action,
                    actionDeque);
            return;
        }
        Action lastAction = actionDeque.pop();
        changePage(pageNavigator, input, lastAction, lastAction.getPage(), output, actionDeque);
    }

    private void doSubscribe(final PageNavigator pageNavigator, final Input input,
                             final String command, final Action action,
                             final ArrayNode output, final EventManager eventManager) {
        Visitor visitorSubscribe = VisitorSubscribe.getVisitorSubscribe();
        pageNavigator.accept(visitorSubscribe, input, action, this.getActualUser(),
                output, eventManager);

        eventManager.subscribeGenres(action.getSubscribedGenre(), actualUser);
        actualUser.getGenres().add(action.getSubscribedGenre());
    }

    private void initiateObserverDependency(final EventManager eventManager, final Input input) {
        for (Movie movie : input.getMovies()) {
            HashMap<String, ArrayList<User>> hashMap = eventManager.getEventManagerMovies();
            hashMap.put(movie.getName(), new ArrayList<>());
            eventManager.setEventManagerMovies(hashMap);
            hashMap = eventManager.getEventManagerGenres();
            for (String genre : movie.getGenres()) {
                if (!hashMap.containsKey(genre)) {
                    hashMap.put(genre, new ArrayList<>());
                }
            }
            eventManager.setEventManagerGenres(hashMap);
        }
    }
    private void databaseAction(final PageNavigator pageNavigator, final Input input,
                                final String command, final Action action,
                                final ArrayNode output, final EventManager eventManager) {
        if (action.getFeature().equals("add")) {
            eventManager.notifyAddMovie(actualUser, input, action, output);
        } else {
            eventManager.notifyRemoveMovie(actualUser, input, action, output);
        }
    }
    private void giveRecommandation(final User actualUser, final Input input,
                                    final ArrayNode output) {
        addInitialMovies(actualUser, input);
        HashMap<String, Integer> hashMapGenres = new HashMap<>();
        for (Movie movie : actualUser.getLikedMovies()) {
            for (String genre : movie.getGenres()) {
                if (!hashMapGenres.containsKey(genre)) {
                    hashMapGenres.put(genre, 1);
                } else {
                    hashMapGenres.put(genre, hashMapGenres.get(genre) + 1);
                }
            }
        }
        HashMap<String, Integer> temp
                = hashMapGenres.entrySet()
                .stream()
                .sorted((i1, i2)
                        -> i2.getValue().compareTo(
                        i1.getValue()))
                .sorted(Map.Entry.comparingByKey()
                        )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        actualUser.getCurrentMoviesList().sort((o1, o2) -> ((Integer) o2.getNumLikes()).
                compareTo((Integer) o1.getNumLikes()));
        for (Map.Entry<String, Integer> it : temp.entrySet()) {
            for (Movie movie : actualUser.getCurrentMoviesList()) {
                if (movie.getGenres().contains(it.getKey()) && !actualUser.getLikedMovies().
                        contains(movie)) {
                    NotificationType notificationType = new NotificationType("Recommendation",
                            movie.getName());
                    actualUser.getNotifications().add(notificationType);
                    ArrayList<Movie> movies = null;
                    actualUser.setCurrentMoviesList(movies);
                    outputDetected(actualUser, output, input);
                    return;
                }
            }
        }

        ArrayList<Movie> movies = null;
        actualUser.setCurrentMoviesList(movies);

        NotificationType notificationType = new NotificationType("Recommendation",
                "No recommendation");
        ArrayList<NotificationType> notificationTypes = new ArrayList<>();
        notificationTypes.add(notificationType);
        actualUser.setNotifications(notificationTypes);
        outputDetected(actualUser, output, input);
    }
}
