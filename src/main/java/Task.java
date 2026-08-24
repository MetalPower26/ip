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
     * Returns the text the user gave for this task.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reports whether this task is done.
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

    /** Renders the task as "[icon][x] description"; subclasses append their own details. */
    @Override
    public String toString() {
        String statusIcon = isDone ? "x" : " ";
        return "[" + getTypeIcon() + "][" + statusIcon + "] " + description;
    }
}
