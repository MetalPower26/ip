package emma.command;

import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;

/**
 * The shared work of every command that appends a task into TaskList:
 * store it, save it, and report it.
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
        try {
            storage.save(tasks);
        } catch (EmmaException e) {
            tasks.delete(tasks.size());
            throw e;
        }
        return "Got it, I've added this:\n  " + task;
    }
}
