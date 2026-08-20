import java.util.ArrayList;

/**
 * The tasks Emma is tracking.
 */
public class TaskList {

    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to store
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Checks whether a user-facing task number refers to an existing task.
     *
     * @param taskNumber the 1-based number the user typed
     * @return true if a task with that number exists
     */
    public boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Returns the task at a user-facing number. Converts to a 0-based index internally.
     *
     * @param taskNumber the 1-based number the user typed; must be valid
     * @return the matching task
     */
    public Task get(int taskNumber) {
        return tasks.get(taskNumber - 1);
    }

    /** Builds a 1-based numbered list of the tasks, one per line. */
    public String format() {
        if (tasks.isEmpty()) {
            return "You haven't given me anything to track yet!";
        }
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                list.append("\n");
            }
            list.append(i + 1).append(". ").append(tasks.get(i));
        }
        return list.toString();
    }
}
