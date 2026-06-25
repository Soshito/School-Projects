# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Interface definition to create JSON messages for API.
# ----------------------------------------------------------------------------------------------

from abc import ABC, abstractmethod

class JSON_msg_assembler(ABC):

    # Abstract method to set header field for implementations
    @abstractmethod
    def set_header(self, header):
        pass

    # Abstract method to set url field for implementations
    @abstractmethod
    def set_url(self, url):
        pass
