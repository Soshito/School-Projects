# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Data parser implementation to parse UID field from an NFC tag.
# ----------------------------------------------------------------------------------------------

import sys

sys.path.append('/home/orpheus/Documents/scrumoftheearth/src/scanner/scanner')
from src.main.parse.i_data_parser import data_parser

class swipe_parser(data_parser):

    # Parse UID data field out of a string of arbitrary length
    # @param input_str: String to be searched for UID field
    # @return UID parsed out as string
    def parse_string(self, input_str):
        uid = ""
        start_parse = input_str.find("UID (NFCID1): ") + len("UID (NFCID1): ")

        if (len(input_str)) < 265:          # Scanning a provided card
            end_parse = start_parse + 14    # Short data range
        else:
            end_parse = start_parse + 26    # Long data range

        for i in range(start_parse, end_parse):
            if(ord(input_str[i]) != ord(" ")):
                uid += input_str[i]

        return uid
