package observerdatabase;

public interface ObserverMovies {

    /**
     * Once a movie gets deleted, update users' lists of purchased, watched, liked, rated movies and
     * notifications lists.
     *
     * @param movie
     */
    void updateRemoveMovie(String movie);

    /**
     * Once a movie gets added, update users' notifications lists based on genres.
     *
     * @param movie
     */
    void updateAddMovie(String movie);
}
