import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Splits a line the user typed into the command word and the arguments after it.
 */
public class Parser {

    private final String command;
    private final String arguments;

    /**
     * Reads one line of input.
     *
     * @param input the line the user typed
     */
    public Parser(String input) {
        String[] parts = input.trim().split(" ", 2);
        this.command = parts[0];
        this.arguments = parts.length > 1 ? parts[1] : "";
    }

    /**
     * Returns the command word, which is everything up to the first space.
     *
     * @return the command word
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the rest of the line after the command word.
     *
     * @return the arguments, empty if the line held none
     */
    public String getArguments() {
        return arguments;
    }

    /**
     * Reads the task number that a command like "mark 2" or "delete 2" was given.
     *
     * @param arguments the arguments after the command word
     * @param command the command word, used to word the error message
     * @return the number the user typed, not yet checked against the list
     * @throws EmmaException if the argument is not a whole number
     */
    public static int parseTaskNumber(String arguments, String command) throws EmmaException {
        try {
            return Integer.parseInt(arguments.trim());
        } catch (NumberFormatException e) {
            throw new EmmaException("I need a task number, like \"" + command + " 1\".");
        }
    }

    /**
     * Reads a date written as yyyy-mm-dd, rejecting anything that is not a real date.
     *
     * @param text the date the user typed
     * @param field the part of the command it came from, used to word the error message
     * @return the date the user typed
     * @throws EmmaException if the text is not a real date in yyyy-mm-dd form
     */
    public static LocalDate parseDate(String text, String field) throws EmmaException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new EmmaException("I need " + field + " as a date like 2019-10-15, "
                    + "but I got \"" + text + "\".");
        }
    }
}
