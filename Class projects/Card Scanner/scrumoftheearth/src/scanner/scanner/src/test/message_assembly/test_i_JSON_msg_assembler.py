# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Validate functionality of i_JSON_msg_assembler
# ----------------------------------------------------------------------------------------------

import sys
from unittest.mock import patch

sys.path.append('/home/orpheus/Documents/scrumoftheearth/src/scanner/scanner')
from unittest import TestCase
from src.main.message_assembly.i_JSON_msg_assembler import JSON_msg_assembler

class test_i_JSON_data_handler(TestCase):

    # Nothing to test, this is an abstract class. Test satisfies framework
    @patch.multiple(JSON_msg_assembler, __abstractmethods__=set())
    def test(self):
        self.instance = JSON_msg_assembler()

        self.instance.set_header("test")
        self.instance.set_url("test")