#include <TinyGPSPlus.h>
#include <SoftwareSerial.h>
#include <ETH.h>
#include <WiFi.h>
#include <Wire.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>
#include "SPIFFS.h"

static const int RXPin = 16, TXPin = 17;
static const uint32_t GPSBaud = 9600;

// The TinyGPSPlus object
TinyGPSPlus gps;

// The serial connection to the GPS device
SoftwareSerial ss(RXPin, TXPin);
String JSON;
#define MSG_BUFFER_SIZE  (1000)
char msg[MSG_BUFFER_SIZE];
const char* ssid ="_______16A____________";
const char* password ="12344321";
const char* mqtt_server = "petweio.online";
const int mqtt_port = 1883;
const char *mqtt_username = "";
const char *mqtt_password = "";

WiFiClient esp32client;
PubSubClient client(esp32client);

void reconnect() {
  // Loop until we're reconnected
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
  while (!client.connected()) {
     String client_id = "esp32-client-";
     client_id += String(WiFi.macAddress());
     Serial.printf("The client %s connects to the public mqtt broker\n", client_id.c_str());
     if (client.connect(client_id.c_str(), mqtt_username, mqtt_password)) {
         Serial.println("Public emqx mqtt broker connected");
     } else {
         Serial.print("failed with state ");
         Serial.print(client.state());
         delay(2000);
     }
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

//  // Switch on the LED if an 1 was received as first character
//  if ((char)payload[0] == '1') {
//    digitalWrite(BUILTIN_LED, LOW);   // Turn the LED on (Note that LOW is the voltage level
//    // but actually the LED is on; this is because
//    // it is active low on the ESP-01)
//  } else {
//    digitalWrite(BUILTIN_LED, HIGH);  // Turn the LED off by making the voltage HIGH
//  }

}
void initWiFi() {
  WiFi.mode(WIFI_STA); //Optional
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi ..");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print('.');
    delay(1000);
  }
  Serial.println(WiFi.localIP());
}

void setup()
{
  Serial.begin(115200);
   initWiFi();
    Wire.begin();
  ss.begin(GPSBaud);
    delay(100);

  //init mqtt 
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
 
}


void displayInfo()
{
  Serial.print(F("Location: ")); 
  if (gps.location.isValid())
  {
    Serial.print(gps.location.lat(), 6);
    Serial.print(F(","));
    Serial.print(gps.location.lng(), 6);
  }
  else
  {
    Serial.print(F("INVALID"));
  }

  Serial.print(F("  Date/Time: "));
  if (gps.date.isValid())
  {
    Serial.print(gps.date.month());
    Serial.print(F("/"));
    Serial.print(gps.date.day());
    Serial.print(F("/"));
    Serial.print(gps.date.year());
  }
  else
  {
    Serial.print(F("INVALID"));
  }

  Serial.print(F(" "));
  if (gps.time.isValid())
  {
    if (gps.time.hour() < 10) Serial.print(F("0"));
    Serial.print(gps.time.hour());
    Serial.print(F(":"));
    if (gps.time.minute() < 10) Serial.print(F("0"));
    Serial.print(gps.time.minute());
    Serial.print(F(":"));
    if (gps.time.second() < 10) Serial.print(F("0"));
    Serial.print(gps.time.second());
    Serial.print(F("."));
    if (gps.time.centisecond() < 10) Serial.print(F("0"));
    Serial.print(gps.time.centisecond());
  }
  else
  {
    Serial.print(F("INVALID"));
  }

  Serial.println();
}

void dataAdd(){
     StaticJsonDocument<2000> doc;
  JsonArray data = doc.createNestedArray("data");
  if (!client.connected()) {
    reconnect();
  }
  data.add(gps.location.lat()); // VD
  data.add(gps.location.lng());  // KD
  serializeJson(data, JSON);
  Serial.println(JSON);
    
//  snprintf (msg, MSG_BUFFER_SIZE, "hello world #%ld", value);
  client.publish("home/esp32", (char*) JSON.c_str());
  JSON = "";
}



void loop()
{
  // This sketch displays information every time a new sentence is correctly encoded.
  while (ss.available() > 0)
    if (gps.encode(ss.read()))
      displayInfo();
      dataAdd();

  if (millis() > 5000 && gps.charsProcessed() < 10)
  {
    Serial.println(F("No GPS detected: check wiring."));
    while(true);
  }
}
