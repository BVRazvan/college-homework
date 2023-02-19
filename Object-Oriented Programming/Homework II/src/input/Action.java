package input;

public final class Action {
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getPage() {
        return page;
    }

    public void setPage(final String page) {
        this.page = page;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(final String feature) {
        this.feature = feature;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(final boolean back) {
        this.back = back;
    }

    private boolean back;
    private String type;
    private int count;
    private int rate;

    public int getRate() {
        return rate;
    }

    public void setRate(final int rate) {
        this.rate = rate;
    }

    private String objectType;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    private String startsWith;

    public String getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(final String startsWith) {
        this.startsWith = startsWith;
    }

    private String page;
    private String subscribedGenre;

    public Filter getFilters() {
        return filters;
    }

    public void setFilters(final Filter filters) {
        this.filters = filters;
    }

    private String feature;
    private Credentials credentials;
    private Filter filters;
    private String movie;

    public String getMovie() {
        return movie;
    }

    public void setMovie(final String movie) {
        this.movie = movie;
    }

    public String getSubscribedGenre() {
        return subscribedGenre;
    }

    public void setSubscribedGenre(final String subscribedGenre) {
        this.subscribedGenre = subscribedGenre;
    }

    private Movie addedMovie;

    public Movie getAddedMovie() {
        return addedMovie;
    }

    public void setAddedMovie(final Movie addedMovie) {
        this.addedMovie = addedMovie;
    }
    private String deletedMovie;

    public String getDeletedMovie() {
        return deletedMovie;
    }

    public void setDeletedMovie(final String deletedMovie) {
        this.deletedMovie = deletedMovie;
    }
}
