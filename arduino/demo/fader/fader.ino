#include <ArduinoJson.h>
#include <Bridge.h>
#include <BridgeServer.h>
#include <BridgeClient.h>
#include <Adafruit_NeoPixel.h>

#define PORT 666
#define PIXELS_PIN 8
// How many NeoPixels are attached to the Arduino?
#define NUMPIXELS 12 // LEDs
#define MAX_COLORS 5
#define FADE_STEPS 20
#define FADE_DELAY 1 //seconds

BridgeServer server(PORT);
//DynamicJsonBuffer jsonBuffer;
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIXELS_PIN, NEO_GRB + NEO_KHZ800); // LED

int r[MAX_COLORS];
int g[MAX_COLORS];
int b[MAX_COLORS];
byte numColors;
byte fade = false;
byte timer = 0;
int counter = 0;
int16_t startColor;
int16_t endColor;
int16_t redDiff;
int16_t greenDiff;
int16_t blueDiff;

void setup() {
  Serial.begin(9600);
  #if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif
  pixels.begin();
  Bridge.begin();
  server.noListenOnLocalhost();
  server.begin();
}

void loop() {

  DynamicJsonBuffer jsonBuffer;
  BridgeClient client = server.accept();
  
  if(client.connected()){
    Serial.println("Connected with Client!");
    String response;
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
    
    JsonObject& root = jsonBuffer.parseObject(response);
    
    if (root.success()) {
      numColors = root["color"]["count"];
      
      int i;
      for (i = 0; i < numColors; i++) {
        r[i] = root["color"]["colors"][i]["r"];
        g[i] = root["color"]["colors"][i]["g"];
        b[i] = root["color"]["colors"][i]["b"];
      }

      if (numColors > 1) {
        fade = true;
        startColor = 0;
        endColor = 1;
        redDiff = r[endColor] - r[startColor];
        greenDiff = g[endColor] - g[startColor];
        blueDiff = b[endColor] - b[startColor];
      }
    } else {
      Serial.println("parseObject() failed");
      return;
    }
  }

  if (!fade) {
    for(int i=0;i<NUMPIXELS;i++){
      // pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
      pixels.setPixelColor(i, pixels.Color(r[0],g[0],b[0]));
    }
  } else if (timer == FADE_DELAY){
    if (counter == FADE_STEPS) {
      startColor = (startColor + 1) % numColors;
      endColor = (endColor + 1) % numColors;
      redDiff = r[endColor] - r[startColor];
      greenDiff = g[endColor] - g[startColor];
      blueDiff = b[endColor] - b[startColor];
      counter = 0;
    }

    for(int i=0;i<NUMPIXELS;i++){
      pixels.setPixelColor(i, pixels.Color((int16_t)r[startColor] + (redDiff * counter / FADE_STEPS),
                                           (int16_t)g[startColor] + (greenDiff * counter / FADE_STEPS),
                                           (int16_t)b[startColor] + (blueDiff * counter / FADE_STEPS)));
    }
    timer = 0;
    counter++;
  } else {
    timer++;
  }
   
  pixels.show(); // This sends the updated pixel color to the hardware.
  delay(1000);
}
