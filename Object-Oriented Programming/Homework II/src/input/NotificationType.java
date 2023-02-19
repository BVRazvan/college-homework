package input;

public final class NotificationType {
    private String message;
    private String movieName;

    public String getMovieName() {
        return movieName;
    }
    public NotificationType(final NotificationType notificationType) {
        this.message = notificationType.getMessage();
        this.movieName = notificationType.getMovieName();
    }
    public NotificationType(final String message, final String movieName) {
        this.message = message;
        this.movieName = movieName;
    }
    public NotificationType() { }
    public void setMovieName(final String movieName) {
        this.movieName = movieName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
