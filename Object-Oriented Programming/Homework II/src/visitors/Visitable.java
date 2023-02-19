package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import input.Action;
import input.Input;
import input.User;
import observerdatabase.EventManager;

public interface Visitable {

    /**
     * Allow using actions on a particular page.
     *
     * @param visitor particular visitor(action to perform);
     * @param input database;
     * @param action to execute;
     * @param actualUser particular user;
     * @param output formal output to be printed;
     */
    void accept(Visitor visitor,  Input input,  Action action,
                 User actualUser,  ArrayNode output, EventManager eventManager);
}
