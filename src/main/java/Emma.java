import java.util.ArrayList;
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

    /** Builds a 1-based numbered list of the stored texts, one per line. 
     * 
     * @param texts A list of all the text the user has sent
     */
    private static String formatTexts(ArrayList<String> texts) {
        if (texts.isEmpty()) {
            return "You haven't given anything to track yet!";
        }
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < texts.size(); i++) {
            if (i > 0) {
                list.append("\n");
            }
            list.append(i + 1).append(". ").append(texts.get(i));
        }
        return list.toString();
    }

     /**
     * Prints "User" signifying the sender
     */
    private static void printUserPrompt() {
        System.out.println();
        System.out.println(ORANGE + "user" + RESET);
    }

    /**
     * Runs the chatbot: greets the user, then echoes input until "bye".
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

        ArrayList<String> texts = new ArrayList<>();

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
                    printResponse(formatTexts(texts));
                } else {
                    texts.add(input);
                    printResponse("added: " + input);
                }
            }
        }
    }
}
