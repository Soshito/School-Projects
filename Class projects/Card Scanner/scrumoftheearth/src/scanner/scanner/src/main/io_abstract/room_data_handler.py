# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Interact with a room_data.txt file to read data about corresponding room.
# ----------------------------------------------------------------------------------------------

class room_data_handler:

    # Read room number from file at given location
    # @param filepath: location of file to be read
    @staticmethod
    def read_room_num(filepath):
        room_data = open(filepath, "r")
        out = -1

        curr_line = room_data.readline()
        while(curr_line != ""):
            if(-1 != curr_line.find("room number")):
                out = int(curr_line[0:curr_line.find(" room number")])
                break
            curr_line = room_data.readline()

        room_data.close()
        return out
