/**
 * A single thing the user wants to keep track of, and whether it is done.
 */
public class Task {

    private final String description;
    private boolean isDone;

    /**
     * Creates a task that starts out not done.
     *
     * @param description
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

    /** Renders the task as "[x] description" when done, "[ ] description" otherwise. */
    @Override
    public String toString() {
        String statusIcon = isDone ? "x" : " ";
        return "[" + statusIcon + "] " + description;
    }
}
