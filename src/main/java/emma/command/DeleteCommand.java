package emma.command;

import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;

/**
 * Removes a numbered task from the list.
 */
public class DeleteCommand implements Command {

    private final int taskNumber;

    /**
     * Creates the command.
     *
     * @param taskNumber the 1-based task number the user gave
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) throws EmmaException {
        try {
            Task task = tasks.delete(taskNumber);
            storage.saveOrUndo(tasks, () -> tasks.insert(taskNumber, task));
            return "Okay, I've removed this:\n  " + task;
        } catch (IndexOutOfBoundsException e) {
            throw new EmmaException("You don't have a task numbered " + taskNumber + ".");
        }
    }
}
