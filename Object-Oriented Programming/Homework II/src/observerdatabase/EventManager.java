package observerdatabase;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Action;
import input.Input;
import input.User;

import java.util.ArrayList;
import java.util.HashMap;

import static output.ErrorDetected.errorDetected;

/**
 * Class implemented to use observer design pattern, where:
 *  -"subjects": movies and genres;
 *  -"observers": users.
 *  -"subscribe" options:
 *       -when a user subscribes to a genre;
 *       -when a user purchases a movie.
 *  -"unsubscribe" options:
 *      -when a movie gets deleted.
 *  -"notify(update)" options:
 *      -when a new movie gets added to database;
 *      -when a movie gets deleted from database.
 *
 */
public final class EventManager {
    private HashMap<String, ArrayList<User>> eventManagerMovies = new HashMap<>();
    private HashMap<String, ArrayList<User>> eventManagerGenres = new HashMap<>();

    /**
     * Once a user purchases a movie, he has a subscription to it.
     *
     * @param movie
     * @param user
     */
    public void subscribeMovies(final String movie, final User user) {
        ArrayList<User> users = this.eventManagerMovies.get(movie);
        users.add(user);
    }

    /**
     * Once a movie gets deleted, unsubscribe all of users from it.
     *
     * @param movie
     */
    public void unsubscribeMovies(final String movie) {
        ArrayList<User> users = this.eventManagerMovies.get(movie);
        users.clear();
    }

    /**
     * Subscribe an user to a particular genre he chose.
     *
     * @param genre
     * @param user
     */
    public void subscribeGenres(final String genre, final User user) {
        ArrayList<User> users = this.eventManagerGenres.get(genre);
        users.add(user);
    }

    /**
     * Once a new movie gets added to database, notify users who did subscribe to at least one
     * genre of its.
     *
     * @param actualUser
     * @param input
     * @param action
     * @param output
     */
    public void notifyAddMovie(final User actualUser, final Input input, final Action action,
                               final ArrayNode output) {
        if (input.getMovies().stream().anyMatch(o -> action.getAddedMovie().getName().
                equals(o.getName()))) {
                    errorDetected(output);
                    return;
        }
        HashMap<String, ArrayList<User>> hashMap = this.getEventManagerMovies();
        input.getMovies().add(action.getAddedMovie());
        hashMap.put(action.getAddedMovie().getName(), new ArrayList<>());
        hashMap = this.getEventManagerGenres();
        for (String genre : action.getAddedMovie().getGenres()) {
            if (!hashMap.containsKey(genre)) {
                hashMap.put(genre, new ArrayList<>());
            }
        }
        this.setEventManagerGenres(hashMap);
        for (User user : input.getUsers()) {
            boolean outerbreak = false;
            for (String genre : action.getAddedMovie().getGenres()) {
                if (outerbreak) {
                    break;
                }
                for (User user1 : this.getEventManagerGenres().get(genre)) {
                    if (user1.getCredentials().getName().equals(user.getCredentials().getName())) {
                        if (user.getCredentials().getName().equals(actualUser.getCredentials().
                                getName())) {
                                    actualUser.updateAddMovie(action.getAddedMovie().getName());
                        } else {
                            user.updateAddMovie(action.getAddedMovie().getName());
                        }
                        outerbreak = true;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Once a movie gets deleted from database, notify users who at least purchased it.
     *
     * @param actualUser
     * @param input
     * @param action
     * @param output
     */
    public void notifyRemoveMovie(final User actualUser, final Input input, final Action action,
                                  final ArrayNode output) {
        if (input.getMovies().stream().noneMatch(o -> action.getDeletedMovie().
                equals(o.getName()))) {
            errorDetected(output);
            return;
        }
        input.getMovies().removeIf(o -> o.getName().equals(action.getDeletedMovie()));

        for (User user : this.getEventManagerMovies().get(action.getDeletedMovie())) {
            user.updateRemoveMovie(action.getDeletedMovie());
        }
        this.unsubscribeMovies(action.getDeletedMovie());
    }
    public HashMap<String, ArrayList<User>> getEventManagerMovies() {
        return eventManagerMovies;
    }

    public void setEventManagerMovies(final HashMap<String, ArrayList<User>> eventManagerMovies) {
        this.eventManagerMovies = eventManagerMovies;
    }

    public HashMap<String, ArrayList<User>> getEventManagerGenres() {
        return eventManagerGenres;
    }

    public void setEventManagerGenres(final HashMap<String, ArrayList<User>> eventManagerGenres) {
        this.eventManagerGenres = eventManagerGenres;
    }
}
