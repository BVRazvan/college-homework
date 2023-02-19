package input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import observerdatabase.ObserverMovies;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties({"currentMoviesList", "active", "genres", "hashMap"})
public final class User implements ObserverMovies {
    public static final int NO_FREE_MOVIES = 15;

    public HashMap<Movie, Integer> getHashMap() {
        return hashMap;
    }

    public void setHashMap(final HashMap<Movie, Integer> hashMap) {
        this.hashMap = hashMap;
    }

    private HashMap<Movie, Integer> hashMap;
    private Credentials credentials;

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }

    private ArrayList<String> genres;

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    public User(final Credentials credentials) {
        this.credentials = credentials;
        this.ratedMovies = new ArrayList<>();
        this.likedMovies = new ArrayList<>();
        this.purchasedMovies = new ArrayList<>();
        this.watchedMovies = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.hashMap = new HashMap<>();
        this.notifications = new ArrayList<>();
    }

    private boolean active = false;
    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public User(final User user) {
        this.credentials = user.getCredentials();
        this.currentMoviesList = user.getCurrentMoviesList();
        this.tokensCount = user.getTokensCount();
        this.numFreePremiumMovies = user.getNumFreePremiumMovies();
        this.purchasedMovies = user.getPurchasedMovies();
        this.watchedMovies = user.getWatchedMovies();
        this.likedMovies = user.getLikedMovies();
        this.ratedMovies = user.getRatedMovies();
        this.genres = user.getGenres();
        this.hashMap = user.getHashMap();
        this.notifications = user.getNotifications();
    }
    public User() {
        this.credentials = new Credentials();
        this.ratedMovies = new ArrayList<>();
        this.likedMovies = new ArrayList<>();
        this.purchasedMovies = new ArrayList<>();
        this.watchedMovies = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.hashMap = new HashMap<>();
        this.notifications = new ArrayList<>();
    }

    /**
     * Replicate a user.
     *
     * @param user
     */
    public void copyUser(final User user) {
        this.credentials = user.getCredentials();
        this.currentMoviesList = user.getCurrentMoviesList();
        this.tokensCount = user.getTokensCount();
        this.numFreePremiumMovies = user.getNumFreePremiumMovies();
        this.purchasedMovies = user.getPurchasedMovies();
        this.watchedMovies = user.getWatchedMovies();
        this.likedMovies = user.getLikedMovies();
        this.ratedMovies = user.getRatedMovies();
        this.genres = user.getGenres();
        this.hashMap = user.getHashMap();
        this.notifications = user.getNotifications();
    }

    /**
     * Once a movie purchased by a user gets deleted, update his lists.
     *
     * @param movie
     */
    public void updateRemoveMovie(final String movie) {
        if (this.getCredentials().getAccountType().equals("premium")) {
            this.setNumFreePremiumMovies(this.getNumFreePremiumMovies() + 1);
        } else {
            this.setTokensCount(this.getTokensCount() + 2);
        }
        this.getWatchedMovies().removeIf(o -> o.getName().equals(movie));
        this.getLikedMovies().removeIf(o -> o.getName().equals(movie));
        this.getRatedMovies().removeIf(o -> o.getName().equals(movie));
        this.getPurchasedMovies().removeIf(o -> o.getName().equals(movie));

        NotificationType notificationType = new NotificationType();
        notificationType.setMessage("DELETE");
        notificationType.setMovieName(movie);
        this.getNotifications().add(notificationType);

    }

    /**
     * If new added movie has a genre of user's interest, notify him.
     *
     * @param movie
     */
    public void updateAddMovie(final String movie) {
        NotificationType notificationType = new NotificationType();
        notificationType.setMessage("ADD");
        notificationType.setMovieName(movie);
        if (!this.getNotifications().contains(notificationType)) {
            this.getNotifications().add(notificationType);
        }
    }
    private int tokensCount = 0;
    private int numFreePremiumMovies = NO_FREE_MOVIES;
    private ArrayList<Movie> purchasedMovies = new ArrayList<Movie>();
    private ArrayList<Movie> watchedMovies = new ArrayList<Movie>();
    private ArrayList<Movie> likedMovies = new ArrayList<Movie>();
    private ArrayList<Movie> ratedMovies = new ArrayList<Movie>();
    private ArrayList<NotificationType> notifications = new ArrayList<NotificationType>();
    private ArrayList<Movie> currentMoviesList = new ArrayList<Movie>();

    public ArrayList<Movie> getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(final ArrayList<Movie> watchedMovies) {
        this.watchedMovies = watchedMovies;
    }

    public ArrayList<Movie> getCurrentMoviesList() {
        return currentMoviesList;
    }

    public void setCurrentMoviesList(final ArrayList<Movie> currentMoviesList) {
        this.currentMoviesList = currentMoviesList;
    }

    public int getTokensCount() {
        return tokensCount;
    }

    public void setTokensCount(final int tokensCount) {
        this.tokensCount = tokensCount;
    }

    public int getNumFreePremiumMovies() {
        return numFreePremiumMovies;
    }

    public void setNumFreePremiumMovies(final int numFreePremiumMovies) {
        this.numFreePremiumMovies = numFreePremiumMovies;
    }

    public ArrayList<Movie> getPurchasedMovies() {
        return purchasedMovies;
    }

    public void setPurchasedMovies(final ArrayList<Movie> purchasedMovies) {
        this.purchasedMovies = purchasedMovies;
    }

    public ArrayList<Movie> getLikedMovies() {
        return likedMovies;
    }

    public void setLikedMovies(final ArrayList<Movie> likedMovies) {
        this.likedMovies = likedMovies;
    }

    public ArrayList<Movie> getRatedMovies() {
        return ratedMovies;
    }

    public void setRatedMovies(final ArrayList<Movie> ratedMovies) {
        this.ratedMovies = ratedMovies;
    }

    public ArrayList<NotificationType> getNotifications() {
        return notifications;
    }

    public void setNotifications(final ArrayList<NotificationType> notifications) {
        this.notifications = notifications;
    }
}
