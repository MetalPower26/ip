import java.util.List;
import java.util.Scanner;

public class Emma {

    private static final String ANSI_ESC = String.valueOf((char) 27);
    private static final String COLOR_BLUE = ANSI_ESC + "[34m";
    private static final String COLOR_ORANGE = ANSI_ESC + "[38;5;208m";
    private static final String COLOR_RESET = ANSI_ESC + "[0m";

    /**
     * Prints "Emma" signifying the sender and Emma's response
     *
     * @param response Emma's response
     */
    private static void printResponse(String response) {
        System.out.println();
        System.out.println(COLOR_BLUE + "Emma" + COLOR_RESET);
        System.out.println(response);
    }

     /**
     * Prints "User" signifying the sender
     */
    private static void printUserPrompt() {
        System.out.println();
        System.out.println(COLOR_ORANGE + "user" + COLOR_RESET);
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
     * Turns a "mark"/"unmark" command into Emma's response.
     *
     * @param tasks the task list to update
     * @param arguments the arguments after the command word
     * @param isDone true for "mark", false for "unmark"
     * @return Emma's response
     * @throws EmmaException if the argument is not a number, or names no task
     */
    private static String handleMark(TaskList tasks, String arguments, boolean isDone)
            throws EmmaException {
        int taskNumber = parseTaskNumber(arguments, isDone ? "mark" : "unmark");
        try {
            Task task = tasks.applyMark(taskNumber, isDone);
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
     * @param tasks the task list to remove from
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the argument is not a number, or names no task
     */
    private static String handleDelete(TaskList tasks, String arguments) throws EmmaException {
        int taskNumber = parseTaskNumber(arguments, "delete");
        try {
            Task task = tasks.delete(taskNumber);
            return "Okay, I've removed this:\n  " + task;
        } catch (IndexOutOfBoundsException e) {
            throw new EmmaException("You don't have a task numbered " + taskNumber + ".");
        }
    }

    /**
     * Returns Emma's response to the "list" command
     *
     * @param tasks the task list to show
     * @return Emma's response
     */
    private static String handleList(TaskList tasks) {
        if (tasks.isEmpty()) {
            return "You haven't given me anything to track yet!";
        }
        return "Here's your tasks:\n" + tasks.format();
    }

    /**
     * Stores a newly created task and reports it back.
     *
     * @param tasks the task list to add to
     * @param task the task to store
     * @return Emma's response
     * @throws EmmaException if the tasks could not be saved
     */
    private static String addTask(TaskList tasks, Task task) throws EmmaException {
        tasks.add(task);
        return "Got it, I've added this:\n  " + task;
    }

    /**
     * Returns Emma's response to the "todo" command.
     *
     * @param tasks the task list to add to
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the description is missing
     */
    private static String handleTodo(TaskList tasks, String arguments) throws EmmaException {
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new EmmaException("A todo needs a description, like \"todo read book\".");
        }
        return addTask(tasks, new Todo(description));
    }

    /**
     * Returns Emma's response to the "deadline" command.
     *
     * @param tasks the task list to add to
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the description or the time is missing
     */
    private static String handleDeadline(TaskList tasks, String arguments) throws EmmaException {
        String usage = "A deadline needs a description and a time, "
                + "like \"deadline return book /by Sunday\".";
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        return addTask(tasks, new Deadline(parts[0].trim(), parts[1].trim()));
    }

    /**
     * Returns Emma's response to the "event" command.
     *
     * @param tasks the task list to add to
     * @param arguments the arguments after the command word
     * @return Emma's response
     * @throws EmmaException if the description, the start or the end is missing
     */
    private static String handleEvent(TaskList tasks, String arguments) throws EmmaException {
        String usage = "An event needs a description, a start and an end, "
                + "like \"event project meeting /from Mon 2pm /to 4pm\".";
        String[] fromParts = arguments.split(" /from ", 2);
        if (fromParts.length < 2 || fromParts[0].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        return addTask(tasks, new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim()));
    }

    /**
     * Runs the chatbot: greets the user, then tracks tasks until "bye".
     *
     * @param args empty
     */
    public static void main(String[] args) {
        String banner = " _____\n"
                + "| ____|  _ __ ___   _ __ ___    __ _ \n"
                + "|  _|   | '_ ` _ \\  | '_ ` _ \\   / _` |\n"
                + "| |___  | | | | | | | | | | | | | (_| |\n"
                + "|_____| |_| |_| |_| |_| |_| |_|  \\__,_|";
        String greeting = "Hey there! I'm Emma.\n"
                + "What can I do for you?";
        String exit = "Bye for now! Hope to see you again soon.";
        System.out.println(banner);
        printResponse(greeting);

        Storage storage = new Storage();
        TaskList tasks;
        try {
            tasks = new TaskList(storage, storage.load());
        } catch (EmmaException e) {
            printResponse(e.getMessage() + "\nI'll start with an empty list.");
            tasks = new TaskList(storage, List.of());
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printUserPrompt();
                if (!scanner.hasNextLine()) {
                    return;
                }
                String input = scanner.nextLine();
                // Everything up to the first space is the command; the rest is its argument.
                String[] parts = input.trim().split(" ", 2);
                String command = parts[0];
                String argument = parts.length > 1 ? parts[1] : "";

                try {
                    // TODO: We should handle commands in a different class.
                    //  We can do this by creating a Command class that ties
                    //  each command with its implementation and a Parser class
                    //  to parse the command and call the correct implementation.
                    if (command.equals("bye")) {
                        printResponse(exit);
                        break;
                    } else if (command.equals("list")) {
                        printResponse(handleList(tasks));
                    } else if (command.equals("mark")) {
                        printResponse(handleMark(tasks, argument, true));
                    } else if (command.equals("unmark")) {
                        printResponse(handleMark(tasks, argument, false));
                    } else if (command.equals("delete")) {
                        printResponse(handleDelete(tasks, argument));
                    } else if (command.equals("todo")) {
                        printResponse(handleTodo(tasks, argument));
                    } else if (command.equals("deadline")) {
                        printResponse(handleDeadline(tasks, argument));
                    } else if (command.equals("event")) {
                        printResponse(handleEvent(tasks, argument));
                    } else {
                        throw new EmmaException("Sorry, I don't know what that means!");
                    }
                } catch (EmmaException e) {
                    printResponse(e.getMessage());
                }
            }
        }
    }
}
