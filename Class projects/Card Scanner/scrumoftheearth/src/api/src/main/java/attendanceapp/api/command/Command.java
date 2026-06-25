//----------------------------------------------------------------------------------------------
// Name: Gavin Papenthien
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Provide an interface to define Command Pattern behavior.
// Necessary for Homework assignment
//----------------------------------------------------------------------------------------------

package attendanceapp.api.command;

//---------------------------------------------------------------
// Provide an interface to define Command Pattern behavior.
//---------------------------------------------------------------
public interface Command {
    void execute();
    void unExecute();
}
