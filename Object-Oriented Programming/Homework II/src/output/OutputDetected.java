package output;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Input;
import input.User;

public final class OutputDetected {

    /**
     * General output after a particular page change / action.
     *
     * @param actualUser user to execute page change / action;
     * @param output formal output to be printed;
     * @param input database.
     */
    public static void outputDetected(final User actualUser, final ArrayNode output,
                                      final Input input) {

        User outputUser = new User(actualUser);
        GeneralOutput generalOutput = new GeneralOutput(null,
                outputUser.getCurrentMoviesList(), outputUser, input);
        try {
            output.addPOJO(generalOutput);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private OutputDetected() { }
}
