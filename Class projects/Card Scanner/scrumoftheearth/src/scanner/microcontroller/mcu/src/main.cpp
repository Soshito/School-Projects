// ----------------------------------------------------------------------------------------------
// Name: Johnathan Trachte
// Project: Attendance App - This is a full stack attendance tracking and management software.
// Purpose: Firmware run on the microcontroller to signal presence of an NFC tag
// ----------------------------------------------------------------------------------------------

#include <Arduino.h>
 
#define RUN_MODE 12           // Pin whose condition selects whether 
                              // presence serial data is sent (for production, no resistor 3v3 -> pin12) 
                              // or HTTP data is sent (for testing, resistor 3v3 -> pin12)

#define BRICK_DELAY 1000      // Bricked another microcontroller by not including a delay, provides separation between messages
#define BAUD_RATE   9600      // Baud rate for connection

#define VALID_PIN     1       // Pin to which green LED is connected
#define INVALID_PIN   2       // Pin to which red LED is connected
#define UNDEFINED_PIN 3       // Pin to which blue LED is connected
#define UNKNOWN_PIN   4       // Pin to which yellow LED is connected

#define HTTP_VALID     504849 // 201 Valid request HTTP response code
#define HTTP_INVALID   524848 // 400 Invalid request HTTP response code
#define HTTP_FORBIDDEN 524851 // 403 Forbidden request HTTP response code (invalid credentials, etc.)

#define PRESENCE_PIN 14       // Pin to which presence indicator is connected (wire, photoresistor, TBD)
#define RESP_DELAY 1000       // Miliseconds delay when light is lit

int test;

int response = 0;

void handleResponse(int response);

int bytesToInt(void);

void setup()
{
  pinMode(RUN_MODE, INPUT);
  test = digitalRead(RUN_MODE);

  pinMode(PRESENCE_PIN, INPUT);

  pinMode(VALID_PIN, OUTPUT);
  pinMode(INVALID_PIN, OUTPUT);
  pinMode(UNDEFINED_PIN, OUTPUT);
  pinMode(UNKNOWN_PIN, OUTPUT);

  Serial.begin(BAUD_RATE);
}

void loop()
{
  response = 0;
  delay(BRICK_DELAY);

  if(!test)
  {
    int cardPresent = digitalRead(PRESENCE_PIN);
    Serial.println(cardPresent);
    Serial.flush();
  }

  if(Serial.available())
    response = bytesToInt();

  handleResponse(response);
}

void handleResponse(int response)
{
  switch (response)
  {
  case 0:
    break;

  case HTTP_VALID:
    digitalWrite(VALID_PIN, HIGH);
    delay(RESP_DELAY);
    digitalWrite(VALID_PIN, LOW);
    break;

  case HTTP_INVALID:
    digitalWrite(INVALID_PIN, HIGH);
    delay(RESP_DELAY);
    digitalWrite(INVALID_PIN, LOW);
    break;

  case HTTP_FORBIDDEN:
    digitalWrite(UNDEFINED_PIN, HIGH);
    delay(RESP_DELAY);
    digitalWrite(UNDEFINED_PIN, LOW);
    break;

  default:
    digitalWrite(UNKNOWN_PIN, HIGH);
    delay(RESP_DELAY);
    digitalWrite(UNKNOWN_PIN, LOW);
    break;
  }

  if(test && response != 0)
  {
    Serial.println(response);
  }

  response = 0;
}

int bytesToInt(void)
{
  int output = 0;

  while (Serial.available())
  {
    output *= 100;

    output += Serial.read();
  }

  return output;
}
