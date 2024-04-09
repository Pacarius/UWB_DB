#include <ESP8266WiFi.h>
class TCPConnector{
  char *ssid, *pass, *host;
  int port;
  public:
    TCPConnector(char *_ssid, char *_pass, char *_host, int _port = 42069):
    ssid(_ssid), pass(_pass), host(_host), port(_port){
    }
};
void setup(){

}
void loop(){}