#include <DallasTemperature.h>
#include <Wire.h>
#include <OneWire.h>
#include "TSL2561.h"
#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

#define LUMI_PIN 8
// How many NeoPixels are attached to the Arduino?
#define NUMPIXELS 12 // LEDs
#define TEMPERATURE_PIN 7 // Temperature
#define DELAY_VAL 2000
#define BRIGHTNESS(a,b) ((((a)/(b))- 255) * (-1))

Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800); // LED
TSL2561 tsl(TSL2561_ADDR_FLOAT); // Light sensor
OneWire oneWire(TEMPERATURE_PIN); // Temperature
DallasTemperature sensors(&oneWire); // Temperature
float brightness_mapping = 1000/255;

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
}

void loop(void) {
  
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
  Serial.println(sensors.getTempCByIndex(0));

  
  for(int i=0;i<NUMPIXELS;i++){

    // pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
    pixels.setPixelColor(i, pixels.Color(50,0,0)); // Moderately bright green color.
    pixels.show(); // This sends the updated pixel color to the hardware.
  }
  
  delay(DELAY_VAL); 
}

