package input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties({"sumRatings"})
public final class Movie {

    public Movie(final Movie movie) {
        this.setSumRatings(movie.getSumRatings());
        this.setNumRatings(movie.getNumRatings());
        this.setRating(movie.getRating());
        this.setNumLikes(movie.getNumLikes());
        this.setDuration(movie.getDuration());
        this.setActors(movie.getActors());
        this.setGenre(movie.getGenre());
        this.setYear(movie.getYear());
        this.setCountriesBanned(movie.getCountriesBanned());
        this.setGenres(movie.getGenres());
        this.setName(movie.getName());
    }
    public Movie() {
        this.actors = new ArrayList<>();
        this.countriesBanned = new ArrayList<>();
        this.genre = new ArrayList<>();
        this.genres = new ArrayList<>();
    }
    private String name;
    private String year;
    private int duration;
    private ArrayList<String> genres;
    @JsonIgnore
    public ArrayList<String> getGenre() {
        return genre;
    }
    @JsonProperty
    public void setGenre(final ArrayList<String> genre) {
        this.genre = genre;
    }
    @JsonIgnore
    private ArrayList<String> genre;
    private ArrayList<String> actors;

    private ArrayList<String> countriesBanned;
    private int numLikes = 0;
    private double rating = 0;
    private int numRatings = 0;

    public int getSumRatings() {
        return sumRatings;
    }

    public void setSumRatings(final int sumRatings) {
        this.sumRatings = sumRatings;
    }

    private int sumRatings = 0;

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(final int numLikes) {
        this.numLikes = numLikes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(final double rating) {
        this.rating = rating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(final int numRatings) {
        this.numRatings = numRatings;
    }

    public ArrayList<String> getCountriesBanned() {
        return countriesBanned;
    }

    public void setCountriesBanned(final ArrayList<String> countriesBanned) {
        this.countriesBanned = countriesBanned;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(final int duration) {
        this.duration = duration;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<String> getActors() {
        return actors;
    }

    public void setActors(final ArrayList<String> actors) {
        this.actors = actors;
    }

}
