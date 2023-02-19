package output;

import input.Input;
import input.Movie;
import input.NotificationType;
import input.User;

import java.util.ArrayList;

public final class GeneralOutput {
    private String error;
    private ArrayList<Movie> currentMoviesList;
    private User currentUser;

    public GeneralOutput(final String error, final ArrayList<Movie> currentMoviesList,
                         final User currentUser, final Input input) {
        this.error = error;
        this.currentMoviesList = null;
        if (currentMoviesList != null) {
            this.currentMoviesList = new ArrayList<>();
            for (Movie moviz : currentMoviesList) {
                Movie movie = new Movie(moviz);
                this.currentMoviesList.add(movie);
            }
        }

        if (currentUser != null) {
            this.currentUser = new User();
            this.currentUser.getCredentials().setBalance(currentUser.getCredentials().
                    getBalance());
            this.currentUser.getCredentials().setAccountType(currentUser.getCredentials().
                    getAccountType());
            this.currentUser.getCredentials().setCountry(currentUser.getCredentials().
                    getCountry());
            this.currentUser.getCredentials().setName(currentUser.getCredentials().
                    getName());
            this.currentUser.getCredentials().setPassword(currentUser.getCredentials().
                    getPassword());

            for (Movie movie : currentUser.getPurchasedMovies()) {
                Movie newMovie = new Movie(movie);
                this.currentUser.getPurchasedMovies().add(newMovie);

            }
            for (Movie movie : currentUser.getWatchedMovies()) {
                Movie newMovie = new Movie(movie);
                this.currentUser.getWatchedMovies().add(newMovie);
            }
            for (Movie movie : currentUser.getLikedMovies()) {
                Movie newMovie = new Movie(movie);
                this.currentUser.getLikedMovies().add(newMovie);
            }
            for (Movie movie : currentUser.getRatedMovies()) {
                Movie newMovie = new Movie(movie);
                this.currentUser.getRatedMovies().add(newMovie);
            }
            for (NotificationType notificationType : currentUser.getNotifications()) {
                NotificationType newNotification = new NotificationType(notificationType);
                this.currentUser.getNotifications().add(newNotification);
            }

            this.currentUser.setTokensCount(currentUser.getTokensCount());
            this.currentUser.setNumFreePremiumMovies(currentUser.getNumFreePremiumMovies());
        }
    }
    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public ArrayList<Movie> getCurrentMoviesList() {
        return currentMoviesList;
    }

    public void setCurrentMoviesList(final ArrayList<Movie> currentMoviesList) {
        this.currentMoviesList = currentMoviesList;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(final User currentUser) {
        this.currentUser = currentUser;
    }
}
