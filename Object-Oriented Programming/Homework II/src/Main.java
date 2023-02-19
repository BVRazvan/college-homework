import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Input;
import pagenavigation.PageNavigation;

import java.io.File;

public final class Main {
    /**
     * Main method executed for every test.
     * Read input and simulate page navigation including actions.
     *
     * @param args args[0] = input test file, args[1] = output test file
     */
    public static void main(final String[] args) {

        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = readInput(args, objectMapper);
        ArrayNode output = objectMapper.createArrayNode();

        PageNavigation pageNavigation = new PageNavigation();
        pageNavigation.simulateNavigation(inputData, output);

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        try {
            objectWriter.writeValue(new File(args[1]), output);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private static Input readInput(final String[] args, final ObjectMapper objectMapper) {
        Input inputData = null;
        try {
            inputData = objectMapper.readValue(new File(args[0]),
                    Input.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return inputData;
    }
    private Main() { }
}

