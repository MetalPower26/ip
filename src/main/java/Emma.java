import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

/**
 * A command-line chatbot that keeps track of the user's tasks.
 */
public class Emma {

    private static final String DEFAULT_SAVE_PATH = "data/emma.json";

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * @param filePath where the tasks are saved between runs
     */
    public Emma(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(List.of());
    }

    /** Greets the user, then answers commands until "bye" or the end of the input. */
    public void run() {
        ui.showWelcome();
        loadTasks();
        while (true) {
            String input = ui.readCommand();
            if (input == null) {
                break;
            }
            Parser parser = new Parser(input);
            if (parser.getCommand().equals("bye")) {
                ui.showGoodbye();
                break;
            }
            try {
                ui.showResponse(execute(parser));
            } catch (EmmaException e) {
                ui.showResponse(e.getMessage());
            }
        }
        ui.close();
    }

    /** Replaces the empty starting list with the saved tasks, if they can be read. */
    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (EmmaException e) {
            ui.showResponse(e.getMessage() + "\nI'll start with an empty list.");
        }
    }

    /**
     * Carries out one command.
     *
     * @param parser the line the user typed, already split up
     * @return Emma's response
     * @throws EmmaException if the command cannot be carried out
     */
    private String execute(Parser parser) throws EmmaException {
        String command = parser.getCommand();
        String arguments = parser.getArguments();
        // TODO: We should handle commands in a different class. We can do this by
        //  creating a Command class that ties each command with its implementation,
        //  and having Parser hand back the matching Command.
        if (command.equals("list")) {
            return handleList();
        } else if (command.equals("mark")) {
            return handleMark(arguments, true);
        } else if (command.equals("unmark")) {
            return handleMark(arguments, false);
        } else if (command.equals("delete")) {
            return handleDelete(arguments);
        } else if (command.equals("todo")) {
            return handleTodo(arguments);
        } else if (command.equals("deadline")) {
            return handleDeadline(arguments);
        } else if (command.equals("event")) {
            return handleEvent(arguments);
        } else if (command.equals("filter")) {
            return handleFilter(arguments);
        } else {
            throw new EmmaException("Sorry, I don't know what that means!");
        }
    }

    /**
     * Saves the tasks, putting the list back the way it was if the save fails, so that
     * a change is either both made and saved or neither.
     *
     * @param undo reverses the change that was just made
     * @throws EmmaException if the tasks could not be saved
     */
    private void saveOrUndo(Runnable undo) throws EmmaException {
        try {
            storage.save(tasks);
        } catch (EmmaException e) {
            undo.run();
            throw e;
        }
    }

    /**
     * Turns a "mark"/"unmark" command into Emma's response.
     *
     * @param arguments the arguments after the command word
     * @param isDone true for "mark", false for "unmark"
     * @return Emma's response
     * @throws EmmaException if the argument is not a number, names no task, or the
     *     change could not be saved
     */
    private String handleMark(String arguments, boolean isDone) throws EmmaException {
        int taskNumber = Parser.parseTaskNumber(arguments, isDone ? "mark" : "unmark");
        try {
            Task task = tasks.get(taskNumber);
            boolean wasDone = task.isDone();
            tasks.applyMark(taskNumber, isDone);
            saveOrUndo(() -> task.setDone(wasDone));
            String message = isDone
                    ? "Nice! I've marked this as done:"
                    : "Okay, I've marked this as not done yet:";
            return message + "\n  " + task;
        } catch (IndexOutOfBoundsException e) {
            throw new EmmaException("You don't have a task numbered " + taskNumber + ".");
        }
    }

    /**
     * Turns a "delete" command into Emma's response.
     *
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the argument is not a number, names no task, or the
     *     change could not be saved
     */
    private String handleDelete(String arguments) throws EmmaException {
        int taskNumber = Parser.parseTaskNumber(arguments, "delete");
        try {
            Task task = tasks.delete(taskNumber);
            saveOrUndo(() -> tasks.insert(taskNumber, task));
            return "Okay, I've removed this:\n  " + task;
        } catch (IndexOutOfBoundsException e) {
            throw new EmmaException("You don't have a task numbered " + taskNumber + ".");
        }
    }

    /**
     * Returns Emma's response to the "list" command.
     *
     * @return Emma's response
     */
    private String handleList() {
        if (tasks.isEmpty()) {
            return "You haven't given me anything to track yet!";
        }
        return "Here's your tasks:\n" + tasks.format();
    }

    /**
     * Stores a newly created task and reports it back.
     *
     * @param task the task to store
     * @return Emma's response
     * @throws EmmaException if the task could not be saved
     */
    private String addTask(Task task) throws EmmaException {
        tasks.add(task);
        saveOrUndo(() -> tasks.delete(tasks.size()));
        return "Got it, I've added this:\n  " + task;
    }

    /**
     * Returns Emma's response to the "todo" command.
     *
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the description is missing, or the task could not be saved
     */
    private String handleTodo(String arguments) throws EmmaException {
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new EmmaException("A todo needs a description, like \"todo read book\".");
        }
        return addTask(new Todo(description));
    }

    /**
     * Returns Emma's response to the "deadline" command.
     *
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the description or the date is missing, is not a real date,
     *     or the task could not be saved
     */
    private String handleDeadline(String arguments) throws EmmaException {
        String usage = "A deadline needs a description and a date, "
                + "like \"deadline return book /by 2019-10-15\".";
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        LocalDate by = Parser.parseDate(parts[1].trim(), "a due date");
        return addTask(new Deadline(parts[0].trim(), by));
    }

    /**
     * Returns Emma's response to the "event" command.
     *
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the description, the start or the end is missing, is not a
     *     real date, the event ends before it starts, or the task could not be saved
     */
    private String handleEvent(String arguments) throws EmmaException {
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
        LocalDate from = Parser.parseDate(toParts[0].trim(), "a start date");
        LocalDate to = Parser.parseDate(toParts[1].trim(), "an end date");
        if (from.isAfter(to)) {
            throw new EmmaException("An event has to end on or after it starts, but "
                    + Dates.format(to) + " is before " + Dates.format(from) + ".");
        }
        return addTask(new Event(fromParts[0].trim(), from, to));
    }

    /**
     * Returns Emma's response to the "filter" command.
     *
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the type is missing or unknown, or an option does not
     *     belong to that type or is not followed by a real date
     */
    private String handleFilter(String arguments) throws EmmaException {
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
                LocalDate dueBy = Parser.parseDate(date, "a cutoff date");
                matches = task -> task instanceof Deadline deadline && deadline.isDueBy(dueBy);
            } else if (flag.equals("/at") && type.equals("event")) {
                LocalDate at = Parser.parseDate(date, "an event date");
                matches = task -> task instanceof Event event && event.isOn(at);
            } else if (flag.equals("/due-by") || flag.equals("/at")) {
                String owner = flag.equals("/due-by") ? "a deadline" : "an event";
                throw new EmmaException("Only " + owner + " filter takes \"" + flag + "\".");
            } else {
                throw new EmmaException("I don't know what \"" + flag + "\" means in a filter.");
            }
        }

        String matched = tasks.format(matches);
        if (matched.isEmpty()) {
            return "Nothing matches that filter.";
        }
        return "Here's what matches:\n" + matched;
    }

    /**
     * Starts Emma with the usual save file.
     *
     * @param args empty
     */
    public static void main(String[] args) {
        new Emma(DEFAULT_SAVE_PATH).run();
    }
}
