package emma.command;

import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;

/**
 * The shared work of every command that adds a task: store it, save it, and report it.
 * Subclasses only decide which task to build.
 */
public abstract class AddCommand implements Command {

    /**
     * Builds the task this command adds.
     *
     * @return the new task
     */
    protected abstract Task createTask();

    @Override
    public String execute(TaskList tasks, Storage storage) throws EmmaException {
        Task task = createTask();
        tasks.add(task);
        storage.saveOrUndo(tasks, () -> tasks.delete(tasks.size()));
        return "Got it, I've added this:\n  " + task;
    }
}
