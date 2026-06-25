# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Validate functionality of swipe_listener class
# ----------------------------------------------------------------------------------------------

import sys

sys.path.append('/home/orpheus/Documents/scrumoftheearth/src/scanner/scanner')
from unittest import TestCase
from src.main.io_physical.swipe_listener import swipe_listener

class test_room_data_hanlder(TestCase):

    # Test object/USB port initialization
    def test_constructor(self):
        sl = swipe_listener()

        self.assertNotEqual(sl.usb, None)

    # Test without NFC card reader attached
    def test_nfc_list_no_reader(self):
        sl = swipe_listener()

        self.assertEqual(sl.nfc_list(), "")

    # Test with NFC card reader attached
    def test_nfc_list_with_reader(self):
        sl = swipe_listener()

        self.assertNotEqual(sl.nfc_list().find("UID"), -1)

    # Test with NFC card reader attached, no card present
    def test_nfc_list_with_reader_no_card(self):
        sl = swipe_listener()

        self.assertEqual(sl.nfc_list(), "")

    # Test with constant input 1
    def test_scanner_listen_one_card(self):
        sl = swipe_listener()

        self.assertEqual(sl.scanner_listen(), b'1')

    # Test with constant input 0
    def test_scanner_listen_zero_nocard(self):
        sl = swipe_listener()

        self.assertEqual(b'0', sl.scanner_listen())

    # Test that valid response is received correctly
    # @note: Requires TESTING jumper to be set between 3v3 and pin 12
    def test_response_phy_valid(self):
        sl = swipe_listener()

        sl.response_phy(str(201))
        output = sl.usb.readline()

        self.assertEqual(b'504849\r\n', output)

    # Test that invalid response is received correctly
    # @note: Requires TESTING jumper to be set between 3v3 and pin 12
    def test_response_phy_invalid(self):
        sl = swipe_listener()

        sl.response_phy(str(400))
        output = sl.usb.readline()

        self.assertEqual(b'524848\r\n', output)

    # Test that forbidden response is received correctly
    # @note: Requires TESTING jumper to be set between 3v3 and pin 12
    def test_response_phy_forbidden(self):
        sl = swipe_listener()

        sl.response_phy(str(403))
        output = sl.usb.readline()

        self.assertEqual(b'524851\r\n', output)

    # Test that other response is received correctly
    # @note: Requires TESTING jumper to be set between 3v3 and pin 12
    def test_response_phy_other(self):
        sl = swipe_listener()

        sl.response_phy(str(222))
        output = sl.usb.readline()

        self.assertEqual(b'505050\r\n', output)
