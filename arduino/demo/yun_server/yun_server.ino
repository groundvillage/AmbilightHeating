
#include <Bridge.h>
#include <YunServer.h>
#include <YunClient.h>

#define PORT 666
BridgeServer server(PORT);

void setup() {
  Serial.begin(9600);
  Bridge.begin();
  server.noListenOnLocalhost();
  server.begin();
}

void loop() {
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
  }

  delay(1000);
}
