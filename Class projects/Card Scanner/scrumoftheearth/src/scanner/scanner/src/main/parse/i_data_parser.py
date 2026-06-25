# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Interface definition to parse fields out of NFC scanner data.
# ----------------------------------------------------------------------------------------------

from abc import ABC, abstractmethod

class data_parser(ABC):

    # Abstract method to parse string for data
    @abstractmethod
    def parse_string(self, input_str):
        output = ""
        return output
