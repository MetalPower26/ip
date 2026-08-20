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
     * Checks whether a task number refers to an existing task.
     *
     * @param taskNumber the 1-based task number
     * @return true if a task with that number exists
     */
    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Returns the task corresponding to the number. 
     * 
     * @param taskNumber the 1-based task number; must be valid
     * @return the matching task
     */
    private Task get(int taskNumber) {
        return tasks.get(taskNumber - 1);
    }

    /**
     * Marks the numbered task as done or not done.
     *
     * @param taskNumber the 1-based task number
     * @param isDone true for "mark", false for "unmark"
     * @return the task that was changed
     * @throws IndexOutOfBoundsException if no task has that number
     */
    public Task applyMark(int taskNumber, boolean isDone) {
        if (!isValidTaskNumber(taskNumber)) {
            throw new IndexOutOfBoundsException("No task numbered " + taskNumber);
        }
        Task task = get(taskNumber);
        task.setDone(isDone);
        return task;
    }

    /**
     * Removes the task from the list.
     *
     * @param taskNumber the 1-based task number
     * @return the task that was removed
     * @throws IndexOutOfBoundsException if no task has that number
     */
    public Task delete(int taskNumber) {
        if (!isValidTaskNumber(taskNumber)) {
            throw new IndexOutOfBoundsException("No task numbered " + taskNumber);
        }
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Checks whether any tasks are stored.
     *
     * @return true if there are no tasks
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Builds a 1-based numbered list of the tasks, one per line; empty if there are none. */
    public String format() {
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
