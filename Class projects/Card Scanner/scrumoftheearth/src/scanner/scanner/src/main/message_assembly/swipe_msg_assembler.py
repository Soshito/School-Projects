# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: JSON message assembler implementation to build POST messages with UID NFC data.
# ----------------------------------------------------------------------------------------------

import sys

sys.path.append('/home/orpheus/Documents/scrumoftheearth/src/scanner/scanner')
from src.main.message_assembly.i_JSON_msg_assembler import JSON_msg_assembler

class swipe_msg_assembler(JSON_msg_assembler):

    url     = "http://localhost:8080/attendancelogs" # URL to which API calls will be made
    header  = { "Content-Type": "application/json" } # header for all POST requests of this type

    # Assemble content into full JSON string message for sending
    # @param content: Internal content of the message to be sent (UID field of NFC scan)
    def assemble_msg(self, uid, room_num):
        payload = { "studentCardId":uid, "roomNum":room_num }

        full_msg = (self.url, self.header, payload)
        return full_msg

    # Implementation of set_url function from JSON_msg_assembler interface
    def set_url(self, url):
        self.url = url

    # Implementation of set_header function from JSON_msg_assembler interface
    # @param header: New header for JSON string
    def set_header(self, header):
        self.header = header
