# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Validate functionality of swipe_msg_assembler class.
# ----------------------------------------------------------------------------------------------

import sys

sys.path.append('/home/orpheus/Documents/scrumoftheearth/src/scanner/scanner')
from unittest import TestCase
from src.main.message_assembly.swipe_msg_assembler import swipe_msg_assembler

class test_swipe_msg_assembler(TestCase):

    # Test assemble_msg builds JSON string message
    def test_assemble_msg(self):
        sma = swipe_msg_assembler()

        json_msg = sma.assemble_msg(123, 123)

        self.assertEqual(json_msg[0], "http://localhost:8080/attendancelogs")
        self.assertEqual(json_msg[1], { "Content-Type": "application/json" })
        self.assertEqual(json_msg[2], { "studentCardId":123, "roomNum":123 })

    # Test set_url correctly sets url attribute
    def test_set_url(self):
        sma = swipe_msg_assembler()

        new_url = "gabagoo"
        sma.set_url(new_url)

        self.assertEqual(sma.url, new_url)

    # Test set_header correctly sets header attribute
    def test_set_header(self):
        sma = swipe_msg_assembler();

        new_header = "gabagoo"
        sma.set_header(new_header)

        self.assertEqual(sma.header, new_header)