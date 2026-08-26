package emma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Saves the task list to disk and reads it back.
 *
 * <p>The file is JSON so that it stays readable and editable by hand:
 *
 * <pre>
 * [
 *   {
 *     "type": "D",
 *     "done": false,
 *     "description": "return book",
 *     "by": "2019-10-15"
 *   }
 * ]
 * </pre>
 */
public class Storage {

    private final Path file;

    /**
     * Creates storage backed by a file.
     *
     * @param filePath where the tasks are kept, as in "data/emma.json"
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    /**
     * Reads the saved tasks, or returns an empty list if nothing is saved yet.
     *
     * @return the tasks that were on disk
     * @throws EmmaException if the file exists but cannot be read or understood
     */
    public List<Task> load() throws EmmaException {
        if (!Files.exists(file)) {
            return List.of();
        }
        String text;
        try {
            text = Files.readString(file);
        } catch (IOException e) {
            throw new EmmaException("I couldn't read " + file + ": " + e.getMessage());
        }
        return new JsonReader(text, file).readTasks();
    }

    /**
     * Writes the whole task list out, creating the data folder if needed.
     *
     * @param tasks the tasks to save
     * @throws EmmaException if the file cannot be written
     */
    public void save(TaskList tasks) throws EmmaException {
        List<Task> all = tasks.getTasks();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < all.size(); i++) {
            json.append(i > 0 ? ",\n" : "\n").append(all.get(i).toJson());
        }
        json.append(all.isEmpty() ? "]\n" : "\n]\n");
        try {
            Path folder = file.getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }
            Files.writeString(file, json.toString());
        } catch (IOException e) {
            throw new EmmaException("I couldn't save to " + file + ": " + e.getMessage());
        }
    }

    /**
     * Saves the tasks, putting the list back the way it was if the save fails, so that
     * a change is either both made and saved or neither.
     *
     * @param tasks the tasks to save
     * @param undo reverses the change that was just made
     * @throws EmmaException if the tasks could not be saved
     */
    public void saveOrUndo(TaskList tasks, Runnable undo) throws EmmaException {
        try {
            save(tasks);
        } catch (EmmaException e) {
            undo.run();
            throw e;
        }
    }

    /**
     * A small JSON reader for the one shape this file uses: an array of objects
     * whose values are strings or booleans. It is deliberately forgiving about
     * whitespace so the file stays comfortable to edit by hand.
     */
    private static final class JsonReader {

        private final String text;
        private final Path file;
        private int pos;

        private JsonReader(String text, Path file) {
            this.text = text;
            this.file = file;
            this.pos = 0;
        }

        private List<Task> readTasks() throws EmmaException {
            List<Task> tasks = new ArrayList<>();
            expect('[');
            if (!tryConsume(']')) {
                do {
                    tasks.add(readTask());
                } while (tryConsume(','));
                expect(']');
            }
            skipWhitespace();
            if (pos < text.length()) {
                throw error("there is extra text after the closing ']'");
            }
            return tasks;
        }

        private Task readTask() throws EmmaException {
            expect('{');
            Map<String, String> fields = new HashMap<>();
            boolean isDone = false;
            do {
                String key = readString();
                expect(':');
                if (key.equals("done")) {
                    isDone = readBoolean();
                } else {
                    fields.put(key, readString());
                }
            } while (tryConsume(','));
            expect('}');

            String description = require(fields, "description");
            Task task = switch (require(fields, "type")) {
            case "T" -> new Todo(description);
            case "D" -> new Deadline(description, readDate(fields, "by"));
            case "E" -> new Event(description, readDate(fields, "from"), readDate(fields, "to"));
            default -> throw error("\"type\" must be \"T\", \"D\" or \"E\"");
            };
            task.setDone(isDone);
            return task;
        }

        private String require(Map<String, String> fields, String key) throws EmmaException {
            String value = fields.get(key);
            if (value == null) {
                throw error("a task is missing its \"" + key + "\" field");
            }
            return value;
        }

        /** Reads a field that must hold a date written as yyyy-mm-dd. */
        private LocalDate readDate(Map<String, String> fields, String key) throws EmmaException {
            String value = require(fields, key);
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                throw error("\"" + key + "\" must be a date like \"2019-10-15\", not \"" + value + "\"");
            }
        }

        private String readString() throws EmmaException {
            expect('"');
            StringBuilder value = new StringBuilder();
            while (pos < text.length()) {
                char c = text.charAt(pos++);
                if (c == '"') {
                    return value.toString();
                }
                if (c != '\\') {
                    value.append(c);
                    continue;
                }
                if (pos >= text.length()) {
                    break;
                }
                char escaped = text.charAt(pos++);
                switch (escaped) {
                case 'n' -> value.append('\n');
                case 't' -> value.append('\t');
                default -> value.append(escaped);
                }
            }
            throw error("a piece of text is missing its closing quote");
        }

        private boolean readBoolean() throws EmmaException {
            skipWhitespace();
            if (text.startsWith("true", pos)) {
                pos += "true".length();
                return true;
            }
            if (text.startsWith("false", pos)) {
                pos += "false".length();
                return false;
            }
            throw error("\"done\" must be true or false");
        }

        private void skipWhitespace() {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }

        private boolean tryConsume(char expected) {
            skipWhitespace();
            if (pos < text.length() && text.charAt(pos) == expected) {
                pos++;
                return true;
            }
            return false;
        }

        private void expect(char expected) throws EmmaException {
            if (!tryConsume(expected)) {
                throw error("expected '" + expected + "'");
            }
        }

        /** Reports where in the file the problem is, so it can be fixed by hand. */
        private EmmaException error(String problem) {
            int line = 1;
            for (int i = 0; i < pos && i < text.length(); i++) {
                if (text.charAt(i) == '\n') {
                    line++;
                }
            }
            return new EmmaException(file + " looks wrong on line " + line + ": " + problem + ".");
        }
    }
}
