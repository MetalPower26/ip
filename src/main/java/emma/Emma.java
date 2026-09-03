package emma;

import java.util.List;

import emma.command.Command;

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
     * @param filePath where the tasks are saved between runs.
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
            try {
                Command command = Parser.parse(input);
                ui.showResponse(command.execute(tasks, storage));
                if (command.isExit()) {
                    break;
                }
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
     * Starts Emma with the usual save file.
     *
     * @param args empty.
     */
    public static void main(String[] args) {
        new Emma(DEFAULT_SAVE_PATH).run();
    }
}
