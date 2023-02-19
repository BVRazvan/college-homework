package input;

public final class DoRateMovie {

    /**
     * Rate a movie (1-5).
     *
     * @param movie
     * @param action
     */
    public static void doRateMovie(final User actualUser, final Movie movie, final Action action) {
        if (actualUser.getHashMap().containsKey(movie)) {
            movie.setSumRatings(movie.getSumRatings() + action.getRate() -
                    actualUser.getHashMap().get(movie));
            movie.setRating(1.0 * movie.getSumRatings() / movie.getNumRatings());
            actualUser.getHashMap().remove(movie);
            actualUser.getHashMap().put(movie, action.getRate());
            return;
        }


        movie.setNumRatings(movie.getNumRatings() + 1);
        movie.setSumRatings(movie.getSumRatings() + action.getRate());
        movie.setRating(1.0 * movie.getSumRatings() / movie.getNumRatings());
        actualUser.getRatedMovies().add(movie);
        actualUser.getHashMap().put(movie, action.getRate());
    }

    private DoRateMovie() { }
}
