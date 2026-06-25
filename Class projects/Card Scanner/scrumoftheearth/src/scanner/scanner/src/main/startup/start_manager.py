# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Manage instantiations and hold reused objects.
# ----------------------------------------------------------------------------------------------

import sys

sys.path.append('/home/orpheus/Documents/scrumoftheearth/src/scanner/scanner')
from src.main.io_abstract.msg_sender import msg_sender
from src.main.io_abstract.room_data_handler import room_data_handler
from src.main.io_physical.swipe_listener import swipe_listener
from src.main.message_assembly.swipe_msg_assembler import swipe_msg_assembler
from src.main.parse.swipe_parser import swipe_parser

class start_manager:

    room_data_h = room_data_handler()       # room_data_handler to be reused by class
    room_num = -1                           # Room number of this scanner (-1 indicates value not found)
    swipe_listen = swipe_listener()         # swipe_listener to be reused by class
    swipe_p = swipe_parser()                # swipe_parser to be reused by class
    swipe_assembler = swipe_msg_assembler() # swipe_msg_assembler to be reused by class
    sender = msg_sender()                   # msg_sender to be reused by class

    # Main loop function to scan NFC tags, parse the data, and handle POST requests
    def run(self):
        self.room_num = self.room_data_h.read_room_num("/usr/roomData.txt")
        if (-1 == self.room_num):
            exit("Room number error")

        while (True):
            card_ready = self.swipe_listen.scanner_listen()

            if (b'0' == card_ready):
                continue

            elif (b'1' == card_ready):
                http_resp = self.handle_card()
                if (http_resp != -1):
                    print("New http response:  ", http_resp, "\n\n")
                    pass

            elif (-1 == card_ready):
                break

    def handle_card(self):
            card_data = self.swipe_listen.nfc_list()
            if (-1 == card_data.find("UID")):
                return -1

            uid = self.swipe_p.parse_string(card_data)

            message = self.swipe_assembler.assemble_msg(uid, self.room_num)

            http_resp = self.sender.send_message(message).status_code
            self.swipe_listen.response_phy(str(http_resp))
            return http_resp
