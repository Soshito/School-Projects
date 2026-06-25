# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Validate functionality of start_manager class
# ----------------------------------------------------------------------------------------------

import sys

sys.path.append('/home/orpheus/Documents/scrumoftheearth/src/scanner/scanner')
from unittest import TestCase
from src.main.startup.start_manager import start_manager

class test_swipe_parser(TestCase):

    # Test checkin while student not enrolled in class session
    def test_handle_card_not_enrolled(self):
        sm = start_manager()

        self.assertEqual(400, sm.handle_card())

    # Test checkin while student enrolled in class session
    def test_handle_card_enrolled(self):
        sm = start_manager()

        self.assertEqual(201, sm.handle_card())

    # Test checkin with no card near scanner
    def test_handle_card_no_card(self):
        sm = start_manager()

        self.assertEqual(-1, sm.handle_card())