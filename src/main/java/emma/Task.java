package emma;

import java.util.ArrayList;
import java.util.List;

/**
 * A single thing the user wants to keep track of, and whether it is done.
 * Subclasses decide the type icon and any extra timing details.
 */
public abstract class Task {

    private final String description;
    private boolean isDone;

    /**
     * Creates a task that starts out not done.
     *
     * @param description what the user typed
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done or not done.
     *
     * @param isDone true to mark done, false to mark not done
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Checks whether this task has been marked done.
     *
     * @return true if the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the type icon shown in the first bracket.
     *
     * @return task type icon
     */
    protected abstract String getTypeIcon();

    /**
     * Builds this task's JSON object: the fields every task has, followed by
     * any the subclass passes in. Commas and indenting are handled here, so
     * subclasses never deal with separators.
     *
     * @param extraFields field pairs built with {@link Json#field}
     * @return this task as an indented JSON object
     */
    protected String jsonObject(String... extraFields) {
        List<String> fields = new ArrayList<>();
        fields.add(Json.field("type", getTypeIcon()));
        fields.add(Json.field("done", isDone));
        fields.add(Json.field("description", description));
        fields.addAll(List.of(extraFields));
        return "  {\n    " + String.join(",\n    ", fields) + "\n  }";
    }

    /**
     * Renders the task as an indented JSON object holding the fields needed to
     * rebuild it. Subclasses with timing of their own override this.
     *
     * @return this task as JSON
     */
    public String toJson() {
        return jsonObject();
    }

    /** Renders the task as "[icon][x] description"; subclasses append their own details. */
    @Override
    public String toString() {
        String statusIcon = isDone ? "x" : " ";
        return "[" + getTypeIcon() + "][" + statusIcon + "] " + description;
    }
}
