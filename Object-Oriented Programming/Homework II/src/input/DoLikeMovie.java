package input;

public final class DoLikeMovie {
    /**
     * Increment number of likes for a certain movie.
     *
     * @param movie
     */
    public static void doLikeMovie(final Movie movie) {
        movie.setNumLikes(movie.getNumLikes() + 1);
    }

    private DoLikeMovie() { }
}
