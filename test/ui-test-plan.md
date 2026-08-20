# Emma UI test plan

Manual-style end-to-end tests for Emma's console interface, run by the `test-ui`
skill. Each case starts a fresh chatbot session, feeds it a list of commands, and
compares what Emma prints against the expected output.

## Conventions

An **Input** block holds one command per line, exactly as a user would type it.
Every case ends with `bye`.

An **Expected output** block holds *only Emma's responses*, in order. The runner
strips the following before comparing, so leave them out:

- the startup banner and the greeting
- the coloured `Emma` and `user` speaker labels
- blank lines between blocks
- trailing whitespace on any line

Indentation *within* a response is significant — the two-space indent before a
task in an "added"/"marked" confirmation is part of the expected output.

Each session starts with an empty task list, so a case that needs existing tasks
must add them itself.

---

## TC1: Greeting and exit

**Aim:** Emma exits cleanly on `bye` and shows the farewell message.

**Input**

```
bye
```

**Expected output**

```
Bye for now! Hope to see you again soon.
```

---

## TC2: Listing with nothing tracked

**Aim:** `list` on an empty task list explains there is nothing to show rather
than printing an empty response.

**Input**

```
list
bye
```

**Expected output**

```
You haven't given me anything to track yet!
Bye for now! Hope to see you again soon.
```

---

## TC3: Adding each type of task

**Aim:** `todo`, `deadline` and `event` each create a task and confirm it with
the correct type icon and timing details.

**Input**

```
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] return book (by: Sunday)
Got it, I've added this:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Bye for now! Hope to see you again soon.
```

---

## TC4: Listing tasks in order

**Aim:** `list` numbers tasks from 1 in the order they were added, showing each
type's own rendering.

**Input**

```
todo read book
deadline return book /by Sunday
list
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [D][ ] return book (by: Sunday)
Here's your tasks:
1. [T][ ] read book
2. [D][ ] return book (by: Sunday)
Bye for now! Hope to see you again soon.
```

---

## TC5: Marking and unmarking

**Aim:** `mark` and `unmark` change the status of the numbered task only, and
the change persists into the next `list`.

**Input**

```
todo read book
todo return book
mark 2
list
unmark 2
list
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [T][ ] return book
Nice! I've marked this as done:
  [T][x] return book
Here's your tasks:
1. [T][ ] read book
2. [T][x] return book
Okay, I've marked this as not done yet:
  [T][ ] return book
Here's your tasks:
1. [T][ ] read book
2. [T][ ] return book
Bye for now! Hope to see you again soon.
```

---

## TC6: Task numbers that do not exist

**Aim:** `mark` rejects a non-numeric argument and an out-of-range number
without crashing the session.

**Input**

```
todo read book
mark abc
mark 9
mark 0
list
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
I need a task number, like "mark 1".
You don't have a task numbered 9.
You don't have a task numbered 0.
Here's your tasks:
1. [T][ ] read book
Bye for now! Hope to see you again soon.
```

---

## TC7: Incomplete task commands

**Aim:** each task command explains its own syntax when a required part is
missing, and adds nothing to the list.

**Input**

```
todo
deadline return book
event project meeting /from Mon 2pm
list
bye
```

**Expected output**

```
A todo needs a description, like "todo read book".
A deadline needs a description and a time, like "deadline return book /by Sunday".
An event needs a description, a start and an end, like "event project meeting /from Mon 2pm /to 4pm".
You haven't given me anything to track yet!
Bye for now! Hope to see you again soon.
```

---

## TC8: Deleting a task

**Aim:** `delete` reports the task it removed, the task is gone from the next
`list`, and the tasks after it are renumbered.

**Input**

```
todo read book
todo return book
todo join club
delete 2
list
delete 5
delete abc
bye
```

**Expected output**

```
Got it, I've added this:
  [T][ ] read book
Got it, I've added this:
  [T][ ] return book
Got it, I've added this:
  [T][ ] join club
Okay, I've removed this:
  [T][ ] return book
Here's your tasks:
1. [T][ ] read book
2. [T][ ] join club
You don't have a task numbered 5.
I need a task number, like "delete 1".
Bye for now! Hope to see you again soon.
```

---

## TC9: Unrecognised command

**Aim:** unknown input is reported as unknown rather than silently stored as a
task, and the session carries on afterwards.

**Input**

```
blah blah
deadlin return book /by Sunday
list
bye
```

**Expected output**

```
Sorry, I don't know what that means!
Sorry, I don't know what that means!
You haven't given me anything to track yet!
Bye for now! Hope to see you again soon.
```
