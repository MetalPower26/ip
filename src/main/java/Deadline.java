/**
 * A task that must be finished by a given time.
 */
public class Deadline extends Task {

    private final String by;

    /**
     * Creates a deadline.
     *
     * @param description what the user typed
     * @param by when the task is due
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns when the task is due.
     *
     * @return the due time as the user typed it
     */
    public String getBy() {
        return by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /** Appends the due time to the standard rendering. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
