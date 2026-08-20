import java.util.Scanner;

public class Emma {

    private static final String ESC = String.valueOf((char) 27);
    private static final String BLUE = ESC + "[34m";
    private static final String ORANGE = ESC + "[38;5;208m";
    private static final String RESET = ESC + "[0m";

    /**
     * Prints "Emma" signifying the sender and Emma's response
     *
     * @param response Emma's response
     */
    private static void printResponse(String response) {
        System.out.println();
        System.out.println(BLUE + "Emma" + RESET);
        System.out.println(response);
    }

     /**
     * Prints "User" signifying the sender
     */
    private static void printUserPrompt() {
        System.out.println();
        System.out.println(ORANGE + "user" + RESET);
    }

    /**
     * Marks the task the user named as done or not done.
     *
     * @param tasks the task list to update
     * @param argument the text the user typed after the command word
     * @param isDone true for "mark", false for "unmark"
     * @return Emma's response
     */
    private static String applyMark(TaskList tasks, String argument, boolean isDone) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            return "I need a task number, like \"mark 1\".";
        }
        if (!tasks.isValidTaskNumber(taskNumber)) {
            return "You don't have a task numbered " + taskNumber + ".";
        }
        Task task = tasks.get(taskNumber);
        task.setDone(isDone);
        String message = isDone
                ? "Nice! I've marked this as done:"
                : "Okay, I've marked this as not done yet:";
        return message + "\n  " + task;
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
                    printResponse(tasks.format());
                } else if (input.startsWith("mark ")) {
                    printResponse(applyMark(tasks, input.substring("mark ".length()), true));
                } else if (input.startsWith("unmark ")) {
                    printResponse(applyMark(tasks, input.substring("unmark ".length()), false));
                } else {
                    tasks.add(new Task(input));
                    printResponse("added: " + input);
                }
            }
        }
    }
}
