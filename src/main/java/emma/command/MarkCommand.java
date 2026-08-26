package emma.command;

import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;

/**
 * Marks a numbered task as done or as not done yet.
 */
public class MarkCommand implements Command {

    private final int taskNumber;
    private final boolean isDone;

    /**
     * Creates the command.
     *
     * @param taskNumber the 1-based task number the user gave
     * @param isDone true for "mark", false for "unmark"
     */
    public MarkCommand(int taskNumber, boolean isDone) {
        this.taskNumber = taskNumber;
        this.isDone = isDone;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) throws EmmaException {
        Task task;
        boolean wasDone;
        try {
            task = tasks.get(taskNumber);
            wasDone = task.isDone();
            tasks.applyMark(taskNumber, isDone);
        } catch (IndexOutOfBoundsException e) {
            throw new EmmaException("You don't have a task numbered " + taskNumber + ".");
        }
        try {
            storage.save(tasks);
        } catch (EmmaException e) {
            task.setDone(wasDone);
            throw e;
        }
        String message = isDone
                ? "Nice! I've marked this as done:"
                : "Okay, I've marked this as not done yet:";
        return message + "\n  " + task;
    }
}
