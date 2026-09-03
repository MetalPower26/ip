# Emma

Emma is a command-line chatbot that keeps track of your tasks. You talk to her in
a terminal, and she remembers your tasks between runs by saving them to a file.

```
 _____
| ____|  _ __ ___   _ __ ___    __ _
|  _|   | '_ ` _ \  | '_ ` _ \   / _` |
| |___  | | | | | | | | | | | | | (_| |
|_____| |_| |_| |_| |_| |_| |_|  \__,_|

Emma
Hey there! I'm Emma.
What can I do for you?

user
todo read book

Emma
Got it, I've added this:
  [T][ ] read book
```

## Requirements

- **JDK 25.** The code uses switch expressions with `->` arms, so an older JDK
  will not compile it.

## Building and running

The project is built with Gradle, through the wrapper committed here, so nothing
needs installing beyond the JDK. Use `./gradlew` in Git Bash or `.\gradlew.bat`
in PowerShell. The first command downloads Gradle itself and takes a minute;
later ones are quick.

```
./gradlew run --console=plain -q
```

`--console=plain` keeps Gradle's progress bar from fighting Emma for the
terminal, and `-q` hides the task list, so you see only the conversation.

```
./gradlew build     compile, run the tests, and package the JAR
./gradlew test      run the tests only
./gradlew clean     delete build/ and start fresh
```

## Building a JAR

`shadowJar` packages Emma and everything it needs into one runnable file:

```
./gradlew shadowJar
```

The JAR lands at **`build/libs/emma.jar`**. Run it from anywhere with:

```
java -jar build/libs/emma.jar
```

It is a *fat* JAR, so that single file is all another machine needs — no
classpath, no project folder, just a JDK 25 runtime. Emma saves to `data/emma.json`
**relative to the folder you run it from**, so running the JAR elsewhere gives it
its own separate task list.

### Without Gradle

The JDK alone still works if you want it:

```
javac -d out src/main/java/emma/*.java src/main/java/emma/command/*.java
java -cp out emma.Emma
```

### In VS Code

With the Extension Pack for Java installed, open the project folder and use the
**Run** code lens above `main` in [Emma.java](src/main/java/emma/Emma.java). Make sure
VS Code is configured to use JDK 25 (`Java: Configure Java Runtime`).

## Commands

| Command | What it does | Example |
|---|---|---|
| `todo DESCRIPTION` | Adds a task with no timing | `todo read book` |
| `deadline DESCRIPTION /by DATE` | Adds a task due on a date | `deadline return book /by 2019-10-15` |
| `event DESCRIPTION /from START /to END` | Adds a task spanning two dates | `event project meeting /from 2019-10-15 /to 2019-10-16` |
| `list` | Shows every task, numbered from 1 | `list` |
| `mark NUMBER` | Marks a task done | `mark 2` |
| `unmark NUMBER` | Marks a task not done | `unmark 2` |
| `delete NUMBER` | Removes a task | `delete 2` |
| `filter /type TYPE` | Shows only `todo`, `deadline` or `event` tasks | `filter /type deadline` |
| `filter /type deadline /due-by DATE` | Shows deadlines due on or before a date | `filter /type deadline /due-by 2019-10-15` |
| `filter /type event /at DATE` | Shows events running on a date | `filter /type event /at 2019-10-15` |
| `find TEXT` | Shows tasks with that text in their description | `find book` |
| `bye` | Exits | `bye` |

Tasks are shown as `[TYPE][STATUS] description`, where the type is `T`, `D` or
`E`, and the status is `x` once the task is done.

## Saved data

Emma writes her tasks to `data/emma.json` in the folder she is run from, and
reloads them on the next run. The file is saved after every change, and is
plain JSON so you can read or edit it by hand:

```json
[
  {
    "type": "D",
    "done": false,
    "description": "return book",
    "by": "2019-10-15"
  }
]
```

The `data/` folder is git-ignored, since the tasks are personal state rather
than part of the project. If the file is missing Emma starts with an empty list;
if it cannot be understood she says which line looks wrong and starts empty
rather than crashing.

## Project structure

All source lives in [src/main/java/](src/main/java/), split into two packages.

[emma/](src/main/java/emma/) — the app and the things it works with:

| File | Responsibility |
|---|---|
| `Emma.java` | Wires up the parts and runs the conversation |
| `Ui.java` | Everything printed to and read from the console |
| `Parser.java` | Turns a typed line into the command it asks for |
| `Task.java` | The shared behaviour of a task: description, done status, rendering |
| `Todo.java`, `Deadline.java`, `Event.java` | The three task types and their extra dates |
| `Dates.java` | Renders a `LocalDate` the way Emma shows it |
| `TaskList.java` | Holds the tasks in memory |
| `Storage.java` | Reads and writes the save file |
| `Json.java` | Quoting and escaping helpers for writing JSON |
| `EmmaException.java` | An error carrying the message Emma should show the user |

[emma/command/](src/main/java/emma/command/) — one class per command, each knowing
how to carry itself out:

| File | Responsibility |
|---|---|
| `Command.java` | What every command can do: `execute`, and whether it ends the session |
| `AddCommand.java` | The shared add-save-report work; subclasses only build the task |
| `AddTodoCommand.java`, `AddDeadlineCommand.java`, `AddEventCommand.java` | The three ways to add a task |
| `MarkCommand.java`, `DeleteCommand.java` | Change a numbered task |
| `ListCommand.java`, `FilterCommand.java` | Show tasks without changing any |
| `ByeCommand.java` | Ends the conversation |

**Warning:** Keep `src/main/java` as the root folder for Java files (i.e., don't
rename those folders or move Java files elsewhere), as this is the default
location some tools (e.g., Gradle) expect to find Java files.

## Testing

[test/ui-test-plan.md](test/ui-test-plan.md) holds end-to-end cases for the
console interface: each one starts a fresh session, feeds Emma a list of
commands and compares what she prints against the expected output. They are run
by the `test-ui` Claude Code skill in [.claude/skills/test-ui/](.claude/skills/test-ui/).
