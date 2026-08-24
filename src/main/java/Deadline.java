import java.time.LocalDate;

/**
 * A task that must be finished by a given date.
 */
public class Deadline extends Task {

    private final LocalDate by;

    /**
     * Creates a deadline.
     *
     * @param description what the user typed
     * @param by the date the task is due
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /** Adds the due date to the fields every task saves. */
    @Override
    public String toJson() {
        return jsonObject(Json.field("by", by.toString()));
    }

    /** Appends the due date to the standard rendering. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + Dates.format(by) + ")";
    }
}
