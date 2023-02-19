package input;

public final class Filter {
    private Movie contains;
    private SortMovie sort;

    public Movie getContains() {
        return contains;
    }

    public void setContains(final Movie contains) {
        this.contains = contains;
    }

    public SortMovie getSort() {
        return sort;
    }

    public void setSort(final SortMovie sort) {
        this.sort = sort;
    }
}
