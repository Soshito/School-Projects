# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Listens on USB port for microcontroller message that indicates a card is on the
#          physical scanner.
# ----------------------------------------------------------------------------------------------

import subprocess
import serial

class swipe_listener:

    usb         = None           # USB port from which swipe_listener receives data
    usb_addr    = "/dev/ttyACM0" # Port to which microcontroller is attached
    baud_rate   = 9600           # Baud rate to communicate to microcontroller

    # Constructor
    def __init__(self):
        self.usb = serial.Serial(self.usb_addr, 9600)

    # Call nfc-list and return read data
    # @return: Card data as read by nfc-list call
    def nfc_list(self):
        card_data = subprocess.getoutput("nfc-list")
        if(-1 == card_data.find("UID")):
            card_data = ""

        return card_data

    # Get data on USB port corresponding to button press
    # @return: Data from port
    def scanner_listen(self):
        output = 0
        output = self.usb.read()

        return output

    # Send an HTTP response code over USB for microcontroller to respond to
    # @param code: HTTP response code to be sent
    def response_phy(self, code):
        code = bytes(code, "utf-8")

        self.usb.write(code)
