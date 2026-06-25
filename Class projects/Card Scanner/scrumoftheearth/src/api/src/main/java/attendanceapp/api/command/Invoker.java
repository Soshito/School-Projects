//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Command Pattern Invoker to store Command Stacks.
//----------------------------------------------------------------------------------------------

package attendanceapp.api.command;

import org.springframework.stereotype.Service;

import java.util.Stack;

//---------------------------------------------------------------
// Command Pattern Invoker to store Command Stacks.
//---------------------------------------------------------------
@Service
public class Invoker {
    private final Stack<Command> done;
    private final Stack<Command> unDone;

    public Invoker() {
        done = new Stack<>();
        unDone = new Stack<>();
    }

    /**
     * First execution of a Command
     * execute the Command and add to Done stack
     * "do" not a valid method name in Java
     *
     * @param command Command to execute
     */
    public void firstDo(Command command) {
        command.execute();
        done.push(command);
    }

    /**
     * Undo the most recent Command
     *
     * @return true if the Command was unExecuted, false if there was nothing to undo
     */
    public boolean undo() {
        if (done.isEmpty()) {
            return false;
        }

        Command command = done.pop();
        command.unExecute();
        unDone.push(command);

        return true;
    }

    /**
     * Redo the most recently undone Command
     *
     * @return true if the Command was executed, false if there was nothing to redo
     */
    public boolean redo() {
        if (unDone.isEmpty()) {
            return false;
        }

        Command command = unDone.pop();
        command.execute();
        done.push(command);

        return true;
    }
}
