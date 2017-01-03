#include <ArduinoJson.h>
#include <Bridge.h>
#include <BridgeServer.h>
#include <BridgeClient.h>
#include <DallasTemperature.h>
#include <Wire.h>
#include <OneWire.h>
#include "TSL2561.h"
#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

#define PIXELS_PIN 8
// How many NeoPixels are attached to the Arduino?
#define NUMPIXELS 12 // LEDs
#define TEMPERATURE_PIN 7 // Temperature
#define DELAY_VAL 2000
#define BRIGHTNESS(a,b) ((((a)/(b))- 255) * (-1))
#define PORT 666

BridgeServer server(PORT);
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIXELS_PIN, NEO_GRB + NEO_KHZ800); // LED
TSL2561 tsl(TSL2561_ADDR_FLOAT); // Light sensor
OneWire oneWire(TEMPERATURE_PIN); // Temperature
DallasTemperature sensors(&oneWire); // Temperature
float brightness_mapping = 1000/255;
uint8_t r = 255;
uint8_t g = 255;
uint8_t b = 255;

void setup(void) {
  Serial.begin(9600);

  //+++++++++++ TEMPERATURE SENSOR ++++++++++++++++++++++++++++++++++++++++++++++++++
  sensors.begin();

  //+++++++++++ LIGHT SENSOR ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  if (tsl.begin()) {
    Serial.println("Found light sensor");
  } else {
    Serial.println("No light sensor?");
    while (1);
  }
  tsl.setGain(TSL2561_GAIN_16X);      // set 16x gain (for dim situations)
  tsl.setTiming(TSL2561_INTEGRATIONTIME_13MS);  // shortest integration time (bright light)

  //++++++++++ LEDs ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  #if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif
  pixels.begin();

  //++++++++++ TCP Server ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  Bridge.begin();
  server.noListenOnLocalhost();
  server.begin();
  }

void loop(void) {

  // ++++++++++ LISTEN FOR COMMANDS ++++++++++++++++++++++++++++++++++++++++++++++++
  BridgeClient client = server.accept();

  String response;
  if(client.connected()){
    Serial.println("Connected with Client!");
    while(client.connected()){
      if(client.available()){
        char cmd = client.read();
        if(cmd == '\n' ||  cmd == '\0'){
          break;
        } else {
          response += String(cmd);
        }
      }
    }
    client.stop();
    Serial.println(response);
    r = (uint8_t)((response >> 16) & 0xFF) / 255;  // Extract the RR byte
    g = (uint8_t) ((response >> 8) & 0xFF) / 255;   // Extract the GG byte
    b = (uint8_t) ((response) & 0xFF) / 255;
  }
  
  // ++++++++++ LIGHT SENSOR ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  uint16_t x = tsl.getLuminosity(TSL2561_VISIBLE);
  //Serial.println(x, DEC);
  uint8_t red;
  if (x >= 1000) {
    red = 10;
  } else {
    red = (uint8_t) BRIGHTNESS(x, brightness_mapping);
  }
  //Serial.println(red, DEC);*/

  // +++++++++ TEMPERATURE SENSOR +++++++++++++++++++++++++++++++++++++++++++++++++++
  sensors.requestTemperatures(); // Send the command to get temperatures
  //Serial.println(sensors.getTempCByIndex(0));

  
  for(int i=0;i<NUMPIXELS;i++){

    // pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
    pixels.setPixelColor(i, pixels.Color(r,g,b));
    pixels.show(); // This sends the updated pixel color to the hardware.
  }
  
  delay(DELAY_VAL); 
}

