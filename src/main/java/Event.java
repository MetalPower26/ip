import java.time.LocalDate;

/**
 * A task that runs between a start and an end date.
 */
public class Event extends Task {

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event.
     *
     * @param description task description
     * @param from the date the event starts
     * @param to the date the event ends
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /** Adds the start and end dates to the fields every task saves. */
    @Override
    public String toJson() {
        return jsonObject(Json.field("from", from.toString()), Json.field("to", to.toString()));
    }

    /** Appends the start and end dates to the standard rendering. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + Dates.format(from) + " to: " + Dates.format(to) + ")";
    }
}
