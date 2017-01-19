#include <ArduinoJson.h>
#include <Bridge.h>
#include <BridgeServer.h>
#include <BridgeClient.h>
#include <Adafruit_NeoPixel.h>
#include <DallasTemperature.h>
#include <OneWire.h>
#include <Wire.h>
#include "TSL2561.h"
#include <math.h>
#include "ambiControl.h"
#include "ambiConfig.h"
 
// Setup a oneWire instance to communicate with any OneWire devices 
// (not just Maxim/Dallas temperature ICs)
OneWire oneWire(ONE_WIRE_BUS);
// Pass our oneWire reference to Dallas Temperature.
DallasTemperature tempSensor(&oneWire);
BridgeServer server(PORT);
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIXELS_PIN, NEO_GRB + NEO_KHZ800); // LED
TSL2561 tsl(TSL2561_ADDR_FLOAT); //Light sensor

int r[MAX_COLORS], g[MAX_COLORS], b[MAX_COLORS];
int r_adj[2], g_adj[2], b_adj[2]; // [0] = startColor, [1] = endColor

byte numColors, startColor, endColor;
byte fade = false;
byte timer = 0;
int counter = 0;
int redDiff, greenDiff, blueDiff;
byte treshHigh = TRESH_HIGH_DEFAULT;
byte treshLow = TRESH_LOW_DEFAULT;
byte brightness_mode = BRIGHTNESS_MODE_DEFAULT;

void setup() {
  Serial.begin(9600);
  #if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif
  pixels.begin();
  Bridge.begin();
  if (!tsl.begin()) {
    Serial.println("Error - no luminosity sensor found!");
    while(1);
  }
  tsl.setGain(TSL2561_GAIN_16X);
  tsl.setTiming(TSL2561_INTEGRATIONTIME_101MS);
  
  tempSensor.begin();
  server.noListenOnLocalhost();
  server.begin();
}

void loop() {

  DynamicJsonBuffer jsonBuffer;
  BridgeClient client = server.accept();
  
  if(client.connected()){

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

    #ifdef LOGGING
    Serial.println("Connected with Client!");
    Serial.println(response);
    #endif
    
    JsonObject& root = jsonBuffer.parseObject(response);
    
    if (root.success()) {
      numColors = root["color"]["count"];
      for (int i = 0; i < numColors; i++) {
        r[i] = root["color"]["colors"][i]["r"];
        g[i] = root["color"]["colors"][i]["g"];
        b[i] = root["color"]["colors"][i]["b"];
      }

      treshHigh = root["treshhigh"];
      treshLow = root["treshlow"];
      if (treshHigh < treshLow) {
        Serial.println("Error - invalid temperature tresholds");
        treshHigh = treshLow;
      }
      brightness_mode = root["brightness"];
      if (brightness_mode < 1 || brightness_mode > 3) {
        Serial.println("Error - invalid brightness mode");
        brightness_mode = BRIGHTNESS_MODE_DEFAULT;
      }
      
      if (numColors > 1) {
        iniFader();
      } else {
        fade = false;
      }
      
    } else {
      Serial.println("parseObject() failed");
      return;
    }
  }
  
  controlLED();
  pixels.show(); // This sends the updated pixel color to the hardware.
  
  delay(DELAY);
}

void iniFader() {

  #ifdef LOGGING
  Serial.println("iniFader()");
  #endif
  
  fade = true;
  startColor = 0;
  endColor = 1;
  adjustColor(startColor,0);
  adjustColor(endColor,1);
  redDiff = r_adj[1] - r_adj[0];
  greenDiff = g_adj[1] - g_adj[0];
  blueDiff = b_adj[1] - b_adj[0];
}

void controlLED() {
  
  if (!fade) {

    adjustColor(0,0);
    
    for(int i=0;i<NUMPIXELS;i++){
      // pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
      pixels.setPixelColor(i, pixels.Color(r_adj[0],g_adj[0],b_adj[0]));
    }    
    
  } else if (timer == FADE_DELAY){
    if (counter == FADE_STEPS) {
      startColor = (startColor + 1) % numColors;
      endColor = (endColor + 1) % numColors;

      adjustColor(startColor, 0);
      adjustColor(endColor, 1);
      
      redDiff = r_adj[1] - r_adj[0];
      greenDiff = g_adj[1] - g_adj[0];
      blueDiff = b_adj[1] - b_adj[0];
      counter = 0;
    }

    for(int i = 0; i < NUMPIXELS; i++) {
      pixels.setPixelColor(i, pixels.Color((int16_t)r_adj[0] + (redDiff * counter / FADE_STEPS),
                                           (int16_t)g_adj[0] + (greenDiff * counter / FADE_STEPS),
                                           (int16_t)b_adj[0] + (blueDiff * counter / FADE_STEPS)));
    }
    timer = 0;
    counter++;
  } else {
    timer++;
  }

  adjustBrightness();
  
}

void adjustColor(byte src_idx, byte tgt_idx) {
  
  tempSensor.requestTemperatures(); // Send the command to get temperatures
  double temperature = tempSensor.getTempCByIndex(0);
   
  #ifdef LOGGING
  Serial.print("Temperature: "); Serial.println(temperature);
  #endif

  if (temperature <= treshHigh && temperature >= treshLow) {
    r_adj[tgt_idx] = r[src_idx];
    g_adj[tgt_idx] = g[src_idx];
    b_adj[tgt_idx] = b[src_idx];
  } else if (temperature > treshHigh) {
    adjustCooler(src_idx, tgt_idx, round(temperature - treshHigh));
  } else {
    adjustWarmer(src_idx, tgt_idx, round(treshLow - temperature));
  }
}

void adjustWarmer(byte src_idx, byte tgt_idx, int diff) {

  int r_new, g_new, b_new;

  if (r[src_idx] == 255) {
    if (g[src_idx] == 0) {
       r_new = r[src_idx];
       g_new = g[src_idx];
       b_new = MAX((b[src_idx] - (diff * 40)),0);
    } else if (b[src_idx] == 0) {
       r_new = r[src_idx];
       g_new = MAX((g[src_idx] - (diff * 40)), 0);
       b_new = b[src_idx];
    }
  } else if (b[src_idx] == 255) {
    if (g[src_idx] == 0) {
      r_new = MIN((r[src_idx] + (diff * 40)),255);
      g_new = g[src_idx];
      b_new = b[src_idx];
    } else if (r[src_idx] == 0){
      r_new = MIN((r[src_idx] + (diff * 40)),255);
      g_new = MAX((g[src_idx] - (diff * 20)), 0);
      b_new = b[src_idx];
    }
  } else if (g[src_idx] == 255) {
    if (r[src_idx] == 0) {
      r_new = MIN((r[src_idx] + (diff * 40)),255);
      g_new = g[src_idx];
      b_new = MAX((b[src_idx] - (diff * 40)), 0);
    } else if (b[src_idx] == 0){
      r_new = MIN((b[src_idx] + (diff * 40)), 255);
      g_new = g[src_idx];
      b_new = b[src_idx];
    }
  }

  r_adj[tgt_idx] = r_new;
  g_adj[tgt_idx] = g_new;
  b_adj[tgt_idx] = b_new;

  #ifdef LOGGING
  Serial.println("adjustWarmer()");
  Serial.print("Adjusted RGB value: ("); Serial.print(r_new); Serial.print(", "); Serial.print(g_new); Serial.print(", "); Serial.print(b_new); Serial.print(")\n");
  #endif
}

void adjustCooler(byte src_idx, byte tgt_idx, int diff) {

  int r_new, g_new, b_new;

  if (r[src_idx] == 255) {
    if (g[src_idx] == 0) {
       r_new = r[src_idx];
       g_new = MIN((g[src_idx] + (diff * 20)), 255);
       b_new = MIN((b[src_idx] + (diff * 40)), 255);
    } else if (b[src_idx] == 0) {
       r_new = r[src_idx];
       g_new = MIN((g[src_idx] + (diff * 40)), 255);
       b_new = MIN((b[src_idx] + (diff * 20)), 255);
    }
  } else if (b[src_idx] == 255) {
    if (g[src_idx] == 0) {
      r_new = MAX((r[src_idx] - (diff * 40)),0);
      g_new = g[src_idx];
      b_new = b[src_idx];
    } else if (r[src_idx] == 0){
      r_new = r[src_idx];
      g_new = MIN((g[src_idx] + (diff * 40)), 255);
      b_new = b[src_idx];
    }
  } else if (g[src_idx] == 255) {
    if (r[src_idx] == 0) {
      r_new = r[src_idx];
      g_new = g[src_idx];
      b_new = MIN((b[src_idx] + (diff * 40)), 255);
    } else if (b[src_idx] == 0){
      r_new = MAX((b[src_idx] - (diff * 40)), 0);
      g_new = g[src_idx];
      b_new = MIN((b[src_idx] + (diff * 40)), 255);
    } else {
      //Should never happen!
      Serial.println("Error - invalid colour!");
      r_new = r[src_idx];
      g_new = g[src_idx];
      b_new = b[src_idx];
    }
  }
  r_adj[tgt_idx] = r_new;
  g_adj[tgt_idx] = g_new;
  b_adj[tgt_idx] = b_new;

  #ifdef LOGGING
  Serial.println("adjustCooler()");
  Serial.print("Adjusted RGB value: ("); Serial.print(r_new); Serial.print(", "); Serial.print(g_new); Serial.print(", "); Serial.print(b_new); Serial.print(")\n");
  #endif
}

void adjustBrightness() {
  
  uint32_t lum = tsl.getFullLuminosity();
  uint16_t ir, full;
  ir = lum >> 16;
  full = lum & 0xFFFF;
  uint16_t lux = MIN(tsl.calculateLux(full, ir), MAX_LUX);
  int brightness;
  
  if(brightness_mode == DIM_MODE) {
    brightness = MIN(BRIGHTNESS(lux, (MAX_LUX/255)), 200);
  } else if (brightness_mode == NORMAL_MODE) {
    brightness = BRIGHTNESS(lux, (MAX_LUX/255));
  } else {
    brightness = MAX(BRIGHTNESS(lux, (MAX_LUX/255)), 100);
  }
  
  pixels.setBrightness(brightness);

  #ifdef LOGGING
  Serial.println("adjustBrightness()");
  Serial.print("Lux: "); Serial.print(lux); Serial.print("\n");
  Serial.print("LED brightness: "); Serial.print(brightness); Serial.print("\n");
  #endif
  
  if (lux < 300) {
    tsl.setTiming(TSL2561_INTEGRATIONTIME_402MS);
    tsl.setGain(TSL2561_GAIN_16X);
  } else if (lux > 500) {
    tsl.setTiming(TSL2561_INTEGRATIONTIME_13MS);
    tsl.setGain(TSL2561_GAIN_0X);
  } else {
    tsl.setTiming(TSL2561_INTEGRATIONTIME_101MS);
    tsl.setGain(TSL2561_GAIN_16X);
  }
}

