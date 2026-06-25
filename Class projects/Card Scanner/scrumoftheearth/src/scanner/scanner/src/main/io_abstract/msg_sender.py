# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Send POST requests to API endpoints.
# ----------------------------------------------------------------------------------------------

import requests
import json

class msg_sender:

    # Handle POST request to API
    # @param msg_data:  Tuple containing
    # 0 - url:          URL endpoint to which the POST request is sent
    # 1 - header:       Header for the JSON string
    # 2 - data:         Message to be sent to the API, a JSON string
    @staticmethod
    def send_message(msg_data):
        response = requests.post(msg_data[0], headers=msg_data[1], json=msg_data[2])
        return response
