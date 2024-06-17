/*
 * Copyright (c) 2015 by Thomas Trojer <thomas@trojer.net>
 * Decawave DW1000 library for arduino.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @file RangingAnchor.ino
 * Use this to test two-way ranging functionality with two
 * DW1000. This is the anchor component's code which computes range after
 * exchanging some messages. Addressing and frame filtering is currently done
 * in a custom way, as no MAC features are implemented yet.
 *
 * Complements the "RangingTag" example sketch.
 *
 * @todo
 *  - weighted average of ranging results based on signal quality
 *  - use enum instead of define
 *  - move strings to flash (less RAM consumption)
 */

#include <SPI.h>
#include <DW1000.h>
#include <ESP8266WiFi.h>

// connection pins
const uint8_t PIN_RST = 5;  // reset pin
const uint8_t PIN_IRQ = 4;  // irq pin
const uint8_t PIN_SS1 = 0;  // spi select pin
const uint8_t PIN_SS2 = 2;  // spi select pin

// messages used in the ranging protocol
// TODO replace by enum
#define POLL 0
#define POLL_ACK 1
#define RANGE 2
#define RANGE_REPORT 3
#define RANGE_FAILED 255
// message flow state
volatile byte expectedMsgId = POLL;
// message sent/received state
volatile boolean sentAck = false;
volatile boolean receivedAck = false;
// protocol error state
boolean protocolFailed = false;
// timestamps to remember
DW1000Time timePollSent;
DW1000Time timePollReceived;
DW1000Time timePollAckSent;
DW1000Time timePollAckReceived;
DW1000Time timeRangeSent;
DW1000Time timeRangeReceived;
// last computed range/time
DW1000Time timeComputedRange;
// data buffer
#define LEN_DATA 16
byte data[LEN_DATA];
// watchdog and reset period
uint32_t lastActivity;
uint32_t resetPeriod = 250;
// reply times (same on both sides for symm. ranging)
uint16_t replyDelayTimeUS = 3000;
// ranging counter (per second)
uint16_t successRangingCount = 0;
uint32_t rangingCountPeriod = 0;
float samplingRate = 0;



String Id;
struct Entry {
  String LamppostID;
  String VehicleID;
  float Distance;
};
Entry rx_data[4];
int data_count = 0;
bool stop_trans = false;
String anchor_A;
String server_msg;
String default_msg = "[anchor_A;Tag_A;0],[anchor_A;Tag_B;0],[anchor_B;Tag_A;0],[anchor_B;Tag_B;0]";

//server setting
#include <WiFiClient.h>
#include <ESP8266WiFi.h>

const char* ssid = "TRAMS_Fire";
const char* password = "Fire8131113";
const char* serverIP = "192.168.85.240";  // IP address of the TCP server
const uint16_t serverPort = 42069;        // Port of the TCP server

WiFiClient client;

void setup() {
  // DEBUG monitoring
  Serial.begin(74880);
  delay(1000);
  DWM_setup(PIN_SS2);
  // anchor starts in receiving mode, awaiting a ranging poll message
  receiver();
  noteActivity();
  // for first time ranging frequency computation
  rangingCountPeriod = millis();

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

  // if (client.connect(serverIP, serverPort)) {
  //   Serial.println("Connected to server");
  //   WriteServer(default_msg);
  //   // Do something with the connected client
  // } else {
  //   Serial.println("Connection to server failed");
  // }
}

void noteActivity() {
  // update activity timestamp, so that we do not reach "resetPeriod"
  lastActivity = millis();
}

void resetInactive() {
  // anchor listens for POLL
  expectedMsgId = POLL;
  receiver();
  noteActivity();
}

void handleSent() {
  // status change on sent success
  sentAck = true;
}

void handleReceived() {
  // status change on received success
  receivedAck = true;
}

void transmitPollAck() {
  DW1000.newTransmit();
  DW1000.setDefaults();
  data[0] = POLL_ACK;
  // delay the same amount as ranging tag
  DW1000Time deltaTime = DW1000Time(replyDelayTimeUS, DW1000Time::MICROSECONDS);
  DW1000.setDelay(deltaTime);
  DW1000.setData("1");
  DW1000.startTransmit();
}

void transmitRangeReport(float curRange) {
  DW1000.newTransmit();
  DW1000.setDefaults();
  data[0] = RANGE_REPORT;
  // write final ranging result
  memcpy(data + 1, &curRange, 4);
  DW1000.setData(data, LEN_DATA);
  DW1000.startTransmit();
}

void transmitRangeFailed() {
  DW1000.newTransmit();
  DW1000.setDefaults();
  data[0] = RANGE_FAILED;
  DW1000.setData(data, LEN_DATA);
  DW1000.startTransmit();
}

void receiver() {
  DW1000.newReceive();
  DW1000.setDefaults();
  // so we don't need to restart the receiver manually
  DW1000.receivePermanently(true);
  DW1000.startReceive();
}

/*
 * RANGING ALGORITHMS
 * ------------------
 * Either of the below functions can be used for range computation (see line "CHOSEN
 * RANGING ALGORITHM" in the code).
 * - Asymmetric is more computation intense but least error prone
 * - Symmetric is less computation intense but more error prone to clock drifts
 *
 * The anchors and tags of this reference example use the same reply delay times, hence
 * are capable of symmetric ranging (and of asymmetric ranging anyway).
 */

void computeRangeAsymmetric() {
  // asymmetric two-way ranging (more computation intense, less error prone)
  DW1000Time round1 = (timePollAckReceived - timePollSent).wrap();
  DW1000Time reply1 = (timePollAckSent - timePollReceived).wrap();
  DW1000Time round2 = (timeRangeReceived - timePollAckSent).wrap();
  DW1000Time reply2 = (timeRangeSent - timePollAckReceived).wrap();
  DW1000Time tof = (round1 * round2 - reply1 * reply2) / (round1 + round2 + reply1 + reply2);
  // set tof timestamp
  timeComputedRange.setTimestamp(tof);
}

void computeRangeSymmetric() {
  // symmetric two-way ranging (less computation intense, more error prone on clock drift)
  DW1000Time tof = ((timePollAckReceived - timePollSent) - (timePollAckSent - timePollReceived) + (timeRangeReceived - timePollAckSent) - (timeRangeSent - timePollAckReceived)) * 0.25f;
  // set tof timestamp
  timeComputedRange.setTimestamp(tof);
}

/*
 * END RANGING ALGORITHMS
 * ----------------------
 */

void loop() {
  if (!stop_trans)
    transfer_data();
}

void transfer_data() {
  int32_t curMillis = millis();
  if (!sentAck && !receivedAck) {
    // check if inactive
    if (curMillis - lastActivity > resetPeriod) {
      resetInactive();
    }
    return;
  }
  // continue on any success confirmation
  if (sentAck) {
    sentAck = false;
    byte msgId = data[0];
    if (msgId == POLL_ACK) {
      DW1000.getTransmitTimestamp(timePollAckSent);
      noteActivity();
    }
  }
  if (receivedAck) {
    receivedAck = false;
    // get message and parse
    DW1000.getData(data, LEN_DATA);
    byte msgId = data[0];
    String msgStr;
    DW1000.getData(msgStr);
    //Serial.println((String)msgId + " " + msgStr);
    // if (msgId != expectedMsgId) {
    //   // unexpected message, start over again (except if already POLL)
    //   protocolFailed = true;
    // }
    String temp_msg = msgStr.substring(0, 1);
    if (temp_msg == (String)POLL) {
      // on POLL we (re-)start, so no protocol failure
      Id = msgStr.substring(1, 9);
      //Serial.println((String)"before: " + msgStr);
      msgStr.remove(0, 9);
      msgStr.remove(msgStr.length() - 1, 1);
      B_process(msgStr);
      //Serial.println((String)"after: " + msgStr);
      protocolFailed = false;
      DW1000.getReceiveTimestamp(timePollReceived);
      expectedMsgId = RANGE;
      transmitPollAck();
      noteActivity();
    } else if (msgId == RANGE) {
      DW1000.getReceiveTimestamp(timeRangeReceived);
      expectedMsgId = POLL;
      if (!protocolFailed) {
        timePollSent.setTimestamp(data + 1);
        timePollAckReceived.setTimestamp(data + 6);
        timeRangeSent.setTimestamp(data + 11);
        // (re-)compute range as two-way ranging is done
        computeRangeAsymmetric();  // CHOSEN RANGING ALGORITHM
        transmitRangeReport(timeComputedRange.getAsMicroSeconds());
        float distance = (timeComputedRange.getAsMeters() - 0.6 );
        distance = (distance >= 50 || distance < 0) ? 0 : distance *= 100;
        distance = (distance >= 80) ? distance *= (distance - 80) * (0.6708 - 0.625) / (175.4212 - 80) + 0.625: distance;
        distance /= 3.2;
        // Serial.print("Name: ");
        // Serial.print(Id);
        // Serial.print(" Range: ");
        // Serial.print(distance);
        // Serial.println(" cm");
        if (Id == "11111111" && distance != 0) {
          rx_data[0] = { "TEST0", Id, distance };
          data_count++;
        } else if (Id == "00000000" && distance != 0) {
          rx_data[1] = { "TEST0", Id, distance };
          data_count++;
        }
        if (data_count == 2) {
          // for (int i = 0; i < data_count; i++) {
          //   Serial.println((String) "[" + rx_data[i].LamppostID + ";" + rx_data[i].VehicleID + ";" + rx_data[i].Distance + "]");
          // }
          anchor_A = (String) "[" + rx_data[0].LamppostID + ";" + rx_data[0].VehicleID + ";" + rx_data[0].Distance + "]" + "," + "[" + rx_data[1].LamppostID + ";" + rx_data[1].VehicleID + ";" + rx_data[1].Distance + "]";
          data_count = 0;
          //Serial.println(anchor_A);
        }
        successRangingCount++;
        if (curMillis - rangingCountPeriod > 1000) {
          samplingRate = (1000.0f * successRangingCount) / (curMillis - rangingCountPeriod);
          rangingCountPeriod = curMillis;
          successRangingCount = 0;
        }
      } else {
        transmitRangeFailed();
      }

      noteActivity();
    }
  }
}

void DWM_setup(int pin) {
  DW1000.end();
  DW1000.begin(PIN_IRQ, PIN_RST);
  DW1000.select(pin);
  DW1000.newConfiguration();
  DW1000.setDefaults();
  DW1000.setDeviceAddress(01);
  DW1000.setNetworkId(10);
  DW1000.enableMode(DW1000.MODE_LONGDATA_RANGE_LOWPOWER);
  DW1000.commitConfiguration();

  char msg[128];
  DW1000.getPrintableDeviceIdentifier(msg);
  Serial.print("Device ID: ");
  Serial.println(msg);
  DW1000.getPrintableExtendedUniqueIdentifier(msg);
  Serial.print("Unique ID: ");
  Serial.println(msg);
  DW1000.getPrintableNetworkIdAndShortAddress(msg);
  Serial.print("Network ID & Device Address: ");
  Serial.println(msg);
  DW1000.getPrintableDeviceMode(msg);
  Serial.print("Device mode: ");
  Serial.println(msg);
  
  DW1000.attachSentHandler(handleSent);
  DW1000.attachReceivedHandler(handleReceived);
}

void B_process(String _msg) {
  // Serial.print(anchor_A);
  // Serial.print(",");
  // Serial.println(_msg);
  if (_msg != "") {
    server_msg = anchor_A + "," + _msg;
    Serial.println(server_msg);
    if (client.connect(serverIP, serverPort)) {
      Serial.println("Send to server: " + server_msg);
      WriteServer(server_msg);
    } else {
      Serial.println("Connection to server failed");
    }
  }
}

void WriteServer(String _msg) {
  client.println("ChiFatEntry");
  client.flush();
  //while(!client.available()) sleep(0.5);
  delay(100);
  client.println(_msg);
  client.flush();
}