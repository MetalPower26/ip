import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * The tasks Emma is tracking.
 */
public class TaskList {

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final Storage storage;

    /**
     * Creates a list that saves itself whenever it changes.
     *
     * @param storage where the tasks are kept between runs
     * @param initialTasks the tasks already on disk; seeding does not re-save them
     */
    public TaskList(Storage storage, List<Task> initialTasks) {
        this.storage = storage;
        this.tasks.addAll(initialTasks);
    }

    /**
     * Adds a task to the end of the list and saves.
     *
     * @param task the task to store
     * @throws EmmaException if the tasks could not be saved
     */
    public void add(Task task) throws EmmaException {
        tasks.add(task);
        storage.save(this);
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
     * @throws EmmaException if the tasks could not be saved
     */
    public Task applyMark(int taskNumber, boolean isDone) throws EmmaException {
        if (!isValidTaskNumber(taskNumber)) {
            throw new IndexOutOfBoundsException("No task numbered " + taskNumber);
        }
        Task task = get(taskNumber);
        task.setDone(isDone);
        storage.save(this);
        return task;
    }

    /**
     * Removes the task from the list.
     *
     * @param taskNumber the 1-based task number
     * @return the task that was removed
     * @throws IndexOutOfBoundsException if no task has that number
     * @throws EmmaException if the tasks could not be saved
     */
    public Task delete(int taskNumber) throws EmmaException {
        if (!isValidTaskNumber(taskNumber)) {
            throw new IndexOutOfBoundsException("No task numbered " + taskNumber);
        }
        Task removed = tasks.remove(taskNumber - 1);
        storage.save(this);
        return removed;
    }

    /**
     * Returns the tasks in order, for saving. The list cannot be modified
     * through this view; use add, applyMark and delete instead.
     *
     * @return a read-only view of the stored tasks
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
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
        return format(task -> true);
    }

    /**
     * Builds a numbered list of the matching tasks, keeping each task's number from
     * the full list so that it can still be marked or deleted by that number.
     *
     * @param matches the test a task must pass to be listed
     * @return the matching tasks, one per line; empty if none match
     */
    public String format(Predicate<Task> matches) {
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (!matches.test(tasks.get(i))) {
                continue;
            }
            if (list.length() > 0) {
                list.append("\n");
            }
            list.append(i + 1).append(". ").append(tasks.get(i));
        }
        return list.toString();
    }
}
