import java.util.Scanner;

public class Emma {

    private static final String ESC = String.valueOf((char) 27);
    private static final String BLUE = ESC + "[34m";
    private static final String ORANGE = ESC + "[38;5;208m";
    private static final String RESET = ESC + "[0m";

    private static void printResponse(String response) {
        System.out.println();
        System.out.println(BLUE + "Emma" + RESET);
        System.out.println(response);
    }

    private static void printUserPrompt() {
        System.out.println();
        System.out.println(ORANGE + "user" + RESET);
    }

    /**
     * Runs the chatbot: greets the user, then echoes input until "bye".
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

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printUserPrompt();
                if (!scanner.hasNextLine()) {
                    return;
                }
                String input = scanner.nextLine();
                if (input.equals("bye")) {
                    printResponse(exit);
                    return;
                }
                printResponse(input);
            }
        }
    }
}
