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

## Running Emma

The project has no build tool yet, so compile and run it with the JDK directly.
From the project root:

```
javac -d out src/main/java/*.java
java -cp out Emma
```

`javac -d out` puts the compiled `.class` files in an `out/` folder (which is
git-ignored) instead of mixing them in with the source, and `java -cp out` tells
Java to look there for the `Emma` class to start.

### In VS Code

With the Extension Pack for Java installed, open the project folder and use the
**Run** code lens above `main` in [Emma.java](src/main/java/Emma.java). Make sure
VS Code is configured to use JDK 25 (`Java: Configure Java Runtime`).

## Commands

| Command | What it does | Example |
|---|---|---|
| `todo DESCRIPTION` | Adds a task with no timing | `todo read book` |
| `deadline DESCRIPTION /by TIME` | Adds a task due at a time | `deadline return book /by Sunday` |
| `event DESCRIPTION /from START /to END` | Adds a task spanning two times | `event project meeting /from Mon 2pm /to 4pm` |
| `list` | Shows every task, numbered from 1 | `list` |
| `mark NUMBER` | Marks a task done | `mark 2` |
| `unmark NUMBER` | Marks a task not done | `unmark 2` |
| `delete NUMBER` | Removes a task | `delete 2` |
| `bye` | Exits | `bye` |

Times are stored as free text — Emma does not yet interpret them as dates.

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
    "by": "Sunday"
  }
]
```

The `data/` folder is git-ignored, since the tasks are personal state rather
than part of the project. If the file is missing Emma starts with an empty list;
if it cannot be understood she says which line looks wrong and starts empty
rather than crashing.

## Project structure

All source lives in [src/main/java/](src/main/java/):

| File | Responsibility |
|---|---|
| `Emma.java` | The main loop: reads a line, picks the command, prints the reply |
| `Task.java` | The shared behaviour of a task: description, done status, rendering |
| `Todo.java`, `Deadline.java`, `Event.java` | The three task types and their extra timing details |
| `TaskList.java` | Holds the tasks and saves after every change |
| `Storage.java` | Reads and writes `data/emma.json` |
| `Json.java` | Quoting and escaping helpers for writing JSON |
| `EmmaException.java` | An error carrying the message Emma should show the user |

**Warning:** Keep `src/main/java` as the root folder for Java files (i.e., don't
rename those folders or move Java files elsewhere), as this is the default
location some tools (e.g., Gradle) expect to find Java files.

## Testing

[test/ui-test-plan.md](test/ui-test-plan.md) holds end-to-end cases for the
console interface: each one starts a fresh session, feeds Emma a list of
commands and compares what she prints against the expected output. They are run
by the `test-ui` Claude Code skill in [.claude/skills/test-ui/](.claude/skills/test-ui/).
