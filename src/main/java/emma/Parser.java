package emma;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

import emma.command.AddDeadlineCommand;
import emma.command.AddEventCommand;
import emma.command.AddTodoCommand;
import emma.command.ByeCommand;
import emma.command.Command;
import emma.command.DeleteCommand;
import emma.command.FilterCommand;
import emma.command.FindCommand;
import emma.command.ListCommand;
import emma.command.MarkCommand;

/**
 * Turns a line the user typed into the command it asks for, checking the arguments
 * so that a command is only built once its arguments are known to be good.
 */
public class Parser {

    /**
     * Reads one line of input.
     *
     * @param input the line the user typed
     * @return the command that line asks for
     * @throws EmmaException if the line names no command Emma knows, or its arguments are wrong
     */
    public static Command parse(String input) throws EmmaException {
        String[] parts = input.trim().split(" ", 2);
        String command = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";
        return switch (command) {
        case "bye" -> new ByeCommand();
        case "list" -> new ListCommand();
        case "mark" -> new MarkCommand(parseTaskNumber(arguments, "mark"), true);
        case "unmark" -> new MarkCommand(parseTaskNumber(arguments, "unmark"), false);
        case "delete" -> new DeleteCommand(parseTaskNumber(arguments, "delete"));
        case "todo" -> parseTodo(arguments);
        case "deadline" -> parseDeadline(arguments);
        case "event" -> parseEvent(arguments);
        case "filter" -> parseFilter(arguments);
        case "find" -> parseFind(arguments);
        default -> throw new EmmaException("Sorry, I don't know what that means!");
        };
    }

    /**
     * Reads a "todo" command.
     *
     * @param arguments the arguments after the command word
     * @return the command
     * @throws EmmaException if the description is missing
     */
    private static Command parseTodo(String arguments) throws EmmaException {
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new EmmaException("A todo needs a description, like \"todo read book\".");
        }
        return new AddTodoCommand(description);
    }

    /**
     * Reads a "find" command.
     *
     * @param arguments the arguments after the command word
     * @return the command
     * @throws EmmaException if there is nothing to look for
     */
    private static Command parseFind(String arguments) throws EmmaException {
        String keyword = arguments.trim();
        if (keyword.isEmpty()) {
            throw new EmmaException("A find needs something to look for, like \"find book\".");
        }
        return new FindCommand(keyword);
    }

    /**
     * Reads a "deadline" command.
     *
     * @param arguments the arguments after the command word
     * @return the command
     * @throws EmmaException if the description or the date is missing, or is not a real date
     */
    private static Command parseDeadline(String arguments) throws EmmaException {
        String usage = "A deadline needs a description and a date, "
                + "like \"deadline return book /by 2019-10-15\".";
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        LocalDate by = parseDate(parts[1].trim(), "a due date");
        return new AddDeadlineCommand(parts[0].trim(), by);
    }

    /**
     * Reads an "event" command.
     *
     * @param arguments the arguments after the command word
     * @return the command
     * @throws EmmaException if a part is missing, is not a real date, or the event ends
     *     before it starts
     */
    private static Command parseEvent(String arguments) throws EmmaException {
        String usage = "An event needs a description, a start date and an end date, "
                + "like \"event project meeting /from 2019-10-15 /to 2019-10-16\".";
        String[] fromParts = arguments.split(" /from ", 2);
        if (fromParts.length < 2 || fromParts[0].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        LocalDate from = parseDate(toParts[0].trim(), "a start date");
        LocalDate to = parseDate(toParts[1].trim(), "an end date");
        if (from.isAfter(to)) {
            throw new EmmaException("An event has to end on or after it starts, but "
                    + Dates.format(to) + " is before " + Dates.format(from) + ".");
        }
        return new AddEventCommand(fromParts[0].trim(), from, to);
    }

    /**
     * Reads a "filter" command, working out the test its results must pass.
     *
     * @param arguments the arguments after the command word
     * @return the command
     * @throws EmmaException if the type is missing or unknown, or an option does not
     *     belong to that type or is not followed by a real date
     */
    private static Command parseFilter(String arguments) throws EmmaException {
        String usage = "A filter needs a type, like \"filter /type deadline\".";
        String trimmed = arguments.trim();
        if (!trimmed.startsWith("/type ")) {
            throw new EmmaException(usage);
        }
        String[] parts = trimmed.substring("/type ".length()).trim().split(" ", 2);
        String type = parts[0];
        Predicate<Task> matches = switch (type) {
        case "todo" -> task -> task instanceof Todo;
        case "deadline" -> task -> task instanceof Deadline;
        case "event" -> task -> task instanceof Event;
        default -> throw new EmmaException("I can only filter by todo, deadline or event.");
        };

        String option = parts.length > 1 ? parts[1].trim() : "";
        if (!option.isEmpty()) {
            String[] optionParts = option.split(" ", 2);
            String flag = optionParts[0];
            String date = optionParts.length > 1 ? optionParts[1].trim() : "";
            if (flag.equals("/due-by") && type.equals("deadline")) {
                LocalDate dueBy = parseDate(date, "a cutoff date");
                matches = task -> task instanceof Deadline deadline && deadline.isDueBy(dueBy);
            } else if (flag.equals("/at") && type.equals("event")) {
                LocalDate at = parseDate(date, "an event date");
                matches = task -> task instanceof Event event && event.isOn(at);
            } else if (flag.equals("/due-by") || flag.equals("/at")) {
                String owner = flag.equals("/due-by") ? "a deadline" : "an event";
                throw new EmmaException("Only " + owner + " filter takes \"" + flag + "\".");
            } else {
                throw new EmmaException("I don't know what \"" + flag + "\" means in a filter.");
            }
        }
        return new FilterCommand(matches);
    }

    /**
     * Reads the task number that a command like "mark 2" or "delete 2" was given.
     *
     * @param arguments the arguments after the command word
     * @param command the command word, used to word the error message
     * @return the number the user typed, not yet checked against the list
     * @throws EmmaException if the argument is not a whole number
     */
    private static int parseTaskNumber(String arguments, String command) throws EmmaException {
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
    private static LocalDate parseDate(String text, String field) throws EmmaException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new EmmaException("I need " + field + " as a date like 2019-10-15, "
                    + "but I got \"" + text + "\".");
        }
    }
}
