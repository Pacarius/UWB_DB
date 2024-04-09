#include <WiFi.h>
#include <WiFiClient.h>

const char* ssid = "Fugu";
const char* password = "96007778Chankachun";
const char* serverIP = "192.168.50.84"; // IP address of the TCP server
const uint16_t serverPort = 42069; // Port of the TCP server

WiFiClient client;

void setup() {
  Serial.begin(115200);

  // Connect to Wi-Fi
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  
  Serial.println("Connected to WiFi");

  // Connect to the TCP server
  Serial.print("Connecting to server: ");
  Serial.print(serverIP);
  Serial.print(":");
  Serial.println(serverPort);

  if (client.connect(serverIP, serverPort)) {
    Serial.println("Connected to server");
    WriteServer();
    // Do something with the connected client
  } else {
    Serial.println("Connection to server failed");
  }
}
void WriteServer(){
  client.println("ChiFatEntry");
  client.flush();
  while(!client.available()) sleep(0.5);
  client.println("[TEST0;00000000;35],[TEST1;00000000;2.5],[TEST0;11111111;35],[TEST1;11111111;35]");
}
/*
struct Entry{
  String LamppostID;
  String VehicleID;
  float Distance;
};
String ToString(Entry entry){
  String tmp = "[";
  tmp += entry.LamppostID;
  tmp += ";";
  tmp += entry.VehicleID;
  tmp += ";";
  tmp += entry.Distance;
  tmp += "]";
  return tmp;
}
*/
void loop() {
  // Your code here
}
