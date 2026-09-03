package emma.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.EmmaException;
import emma.Storage;
import emma.TaskList;

/**
 * Checks that the three add commands store the task they were built with, report it,
 * and save it.
 */
public class AddCommandTest {

    @TempDir
    Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("emma.json").toString());
    }

    @Test
    public void execute_addTodo_storesAndReportsTheTask() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        String response = new AddTodoCommand("read book").execute(tasks, storage());
        assertEquals("Got it, I've added this:\n  [T][ ] read book", response);
        assertEquals("1. [T][ ] read book", tasks.format());
    }

    @Test
    public void execute_addDeadline_showsTheDueDateInWords() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        String response = new AddDeadlineCommand("return book", LocalDate.of(2019, 10, 15))
                .execute(tasks, storage());
        assertEquals("Got it, I've added this:\n  [D][ ] return book (by: Oct 15 2019)", response);
        assertEquals("1. [D][ ] return book (by: Oct 15 2019)", tasks.format());
    }

    @Test
    public void execute_addEvent_showsBothDatesInWords() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        String response = new AddEventCommand("meeting", LocalDate.of(2019, 10, 15),
                LocalDate.of(2019, 10, 16)).execute(tasks, storage());
        assertEquals("Got it, I've added this:\n"
                + "  [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", response);
    }

    @Test
    public void execute_severalAdds_appendToTheEndInOrder() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddTodoCommand("first").execute(tasks, storage);
        new AddDeadlineCommand("second", LocalDate.of(2019, 10, 15)).execute(tasks, storage);
        new AddTodoCommand("third").execute(tasks, storage);
        assertEquals(3, tasks.size());
        assertEquals("1. [T][ ] first\n"
                + "2. [D][ ] second (by: Oct 15 2019)\n"
                + "3. [T][ ] third", tasks.format());
    }

    @Test
    public void execute_addTask_writesItToStorage() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        new AddDeadlineCommand("return book", LocalDate.of(2019, 10, 15)).execute(tasks, storage);

        TaskList reloaded = new TaskList(storage.load());
        assertEquals("1. [D][ ] return book (by: Oct 15 2019)", reloaded.format());
    }

    @Test
    public void execute_addedTask_startsNotDone() throws EmmaException {
        TaskList tasks = new TaskList(List.of());
        new AddTodoCommand("read book").execute(tasks, storage());
        assertEquals(false, tasks.get(1).isDone());
    }
}
