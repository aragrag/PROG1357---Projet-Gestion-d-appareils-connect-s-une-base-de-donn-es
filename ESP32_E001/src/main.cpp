#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ESP32Servo.h>

#define DEVICE_ID "E001"

// Remplacez par vos informations de connexion Wi-Fi
const char* ssid = "MA_4G";
const char* password = "Morocco2023!";

// Remplacez par l'URL de votre serveur et le chemin d'accès à l'endpoint
const char* serverName = "http://10.0.0.12:8000/receive-data";

// Pin auquel le servo est connecte
const int servoPin = 18;

// Creez un objet servo
Servo myservo;

// Fonction pour envoyer l etat open ou close
void sendDoorState(String state) {
  // Construire la chaîne JSON pour envoyer l'etat
  String jsonData = "{\"deviceID\": \"" + String(DEVICE_ID)  + "\", \"typeData\": \"doorState\", \"valeur\": \"" + state + "\"}";

  // Envoyer les donnees au serveur
  if(WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(serverName);
    http.addHeader("Content-Type", "application/json");
    int httpResponseCode = http.POST(jsonData);

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.println(httpResponseCode);
      Serial.println(response);
    }
    else {
      Serial.print("Error on sending POST: ");
      Serial.println(httpResponseCode);
    }
    http.end();
  }
  else {
    Serial.println("Error in WiFi connection");
  }
}
void setup() {
  Serial.begin(9600);

  // Initialiser le servo
  myservo.attach(servoPin);

  // Connexion au reseau Wi-Fi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");

  // Initialiser le servo à une position close par defaut 
  myservo.write(0);
}

void loop() {
  // Open the Door
  int angleOpen = 90; 
  myservo.write(angleOpen);
  sendDoorState("open"); // send "open"

  delay(10000); // Delai avant la fermeture

  // Cliose the Door
  int angleClose = 0; 
  myservo.write(angleClose);
  sendDoorState("close"); // send "close"

  delay(10000); // Delai avant la prochaine action
}