/**
 * A task that runs between a start and an end time.
 */
public class Event extends Task {

    private final String from;
    private final String to;

    /**
     * Creates an event.
     *
     * @param description task description
     * @param from when the event starts
     * @param to when the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /** Appends the start and end times to the standard rendering. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
