# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Validate functionality of swipe_parser class
# ----------------------------------------------------------------------------------------------

import sys

sys.path.append('/home/orpheus/Documents/scrumoftheearth/src/scanner/scanner')
from unittest import TestCase
from src.main.parse.swipe_parser import swipe_parser

class test_swipe_parser(TestCase):

    # Test parse_string with data containing correct string
    def test_parse_string_contains_uid(self):
        sp = swipe_parser()

        input_str = "UID (NFCID1): ab  cd  ef  01 gabagoo"
        searching_uid = "abcdef01"

        self.assertEqual(sp.parse_string(input_str), searching_uid)

    # Test parse_string with data containing correct string with length > 265
    def test_parse_string_contains_uid_long_data(self):
        sp = swipe_parser()

        input_str = "UID (NFCID1): ab  cd  ef  01  ab  cd  ef  gabagoo                                                                                                                                                                                                                                                         "
        searching_uid = "abcdef01abcdef"

        self.assertEqual(searching_uid, sp.parse_string(input_str))

    # Test parse_string with data not containing correct string
    def test_parse_string_no_uid(self):
        sp = swipe_parser()

        input_str = "hamburgercheeseburgerbigmacwhopper hamburgercheeseburgerbigmacwhopper"

        self.assertNotEqual(sp.parse_string(input_str), "")

