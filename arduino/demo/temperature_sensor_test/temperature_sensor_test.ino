#include <DallasTemperature.h>
#include <OneWire.h>

// Data wire is plugged into pin 3 on the Arduino
#define ONE_WIRE_BUS 7
 
// Setup a oneWire instance to communicate with any OneWire devices 
// (not just Maxim/Dallas temperature ICs)
OneWire oneWire(ONE_WIRE_BUS);
 
// Pass our oneWire reference to Dallas Temperature.
DallasTemperature sensors(&oneWire);
 
void setup(void)
{
   // Start up the library
  Serial.begin(9600);
  sensors.begin();
}
 
 
void loop(void)
{
    sensors.requestTemperatures(); // Send the command to get temperatures
    Serial.println(sensors.getTempCByIndex(0));
    delay(500);
}
