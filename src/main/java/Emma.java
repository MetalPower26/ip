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
     * Turns a "mark"/"unmark" command into Emma's response.
     *
     * @param tasks the task list to update
     * @param argument the text the user typed after the command word
     * @param isDone true for "mark", false for "unmark"
     * @return Emma's response
     */
    private static String handleMark(TaskList tasks, String argument, boolean isDone) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            return "I need a task number, like \"mark 1\".";
        }
        try {
            Task task = tasks.applyMark(taskNumber, isDone);
            String message = isDone
                    ? "Nice! I've marked this as done:"
                    : "Okay, I've marked this as not done yet:";
            return message + "\n  " + task;
        } catch (IndexOutOfBoundsException e) {
            return "You don't have a task numbered " + taskNumber + ".";
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

        TaskList tasks = new TaskList();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printUserPrompt();
                if (!scanner.hasNextLine()) {
                    return;
                }
                String input = scanner.nextLine();
                if (input.equals("bye")) {
                    printResponse(exit);
                    break;
                } else if (input.equals("list")) {
                    printResponse(handleList(tasks));
                } else if (input.startsWith("mark ")) {
                    printResponse(handleMark(tasks, input.substring("mark ".length()), true));
                } else if (input.startsWith("unmark ")) {
                    printResponse(handleMark(tasks, input.substring("unmark ".length()), false));
                } else {
                    tasks.add(new Task(input));
                    printResponse("added: " + input);
                }
            }
        }
    }
}
