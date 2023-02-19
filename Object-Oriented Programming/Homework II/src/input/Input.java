package input;

import java.util.ArrayList;

public final class Input {

    public static final int PREMIUM_PRICE = 10;
    public static final int MIN_RATE = 1;
    public static final int MAX_RATE = 5;
    private ArrayList<User> users;

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(final ArrayList<Action> actions) {
        this.actions = actions;
    }

    public void setUsers(final ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(final ArrayList<Movie> movies) {
        this.movies = movies;
    }

    private ArrayList<Movie> movies;
    private ArrayList<Action> actions;
}
