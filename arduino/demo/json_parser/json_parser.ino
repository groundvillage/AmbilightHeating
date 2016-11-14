#include <ArduinoJson.h>
#include <Bridge.h>
#include <BridgeServer.h>
#include <BridgeClient.h>

#define PORT 666

BridgeServer server(PORT);
DynamicJsonBuffer jsonBuffer;

void setup() {
  Serial.begin(9600);
  Bridge.begin();
  server.noListenOnLocalhost();
  server.begin();
}

void loop() {

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
      const char* sensor = root["sensor"];
      long time = root["time"];
      double latitude = root["data"][0];
      double longitude = root["data"][1];

      // Print values.
      Serial.println(sensor);
      Serial.println(time);
      Serial.println(latitude, 6);
      Serial.println(longitude, 6);
    } else {
      Serial.println("parseObject() failed");
      return;
    }
  }

  delay(1000);
}
