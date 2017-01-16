#include <ArduinoJson.h>
#include <Bridge.h>
#include <BridgeServer.h>
#include <BridgeClient.h>
#include <Adafruit_NeoPixel.h>

#define PORT 666
#define PIXELS_PIN 8
// How many NeoPixels are attached to the Arduino?
#define NUMPIXELS 12 // LEDs

BridgeServer server(PORT);
//DynamicJsonBuffer jsonBuffer;
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIXELS_PIN, NEO_GRB + NEO_KHZ800); // LED

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
      int numColors = root["color"]["count"];
      int r[numColors];
      int g[numColors];
      int b[numColors];
      int i;
      for (i = 0; i < numColors; i++) {
        r[i] = root["color"]["colors"][i]["r"];
        g[i] = root["color"]["colors"][i]["g"];
        b[i] = root["color"]["colors"][i]["b"];
      }

      for(int i=0;i<NUMPIXELS;i++){
        // pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
        pixels.setPixelColor(i, pixels.Color(r[0],g[0],b[0]));
        pixels.show(); // This sends the updated pixel color to the hardware.
  }

      // Print values.
      //Serial.println(numColors);
      //for (i = 0; i < numColors; i++) {
      //  Serial.println(r[i]);
      //}
    } else {
      Serial.println("parseObject() failed");
      return;
    }
  }

  delay(1000);
}
