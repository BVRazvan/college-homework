package output;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Movie;

import java.util.ArrayList;

public final class ErrorDetected {

    /**
     * Output for errors.
     *
     * @param output formal output to be printed;
     */
    public static void errorDetected(final ArrayNode output) {
        ArrayList<Movie> currentMoviesList = new ArrayList<>();
        GeneralOutput generalOutput = new GeneralOutput("Error",
                currentMoviesList, null, null);
        try {
            output.addPOJO(generalOutput);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private ErrorDetected() { }
}
