package pagenavigation;

import input.Input;
import input.Movie;
import input.User;

import java.util.ArrayList;

public final class AddInitialMovies {

    /**
     * Once a user moves to 'movies' page, reinitialise his list of movies.
     *
     * @param actualUser;
     * @param input database.
     */
    public static void addInitialMovies(final User actualUser, final Input input) {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        for (Movie movie : input.getMovies()) {
            boolean found = false;
            for (String string : movie.getCountriesBanned()) {
                if (string.equals(actualUser.getCredentials().getCountry())) {
                    found = true;
                }
            }
            if (!found) {
                movies.add(movie);
            }
        }
        actualUser.setCurrentMoviesList(movies);
    }

    private AddInitialMovies() { }
}
