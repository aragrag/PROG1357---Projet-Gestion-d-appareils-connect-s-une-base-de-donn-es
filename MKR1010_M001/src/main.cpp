#include <Arduino.h>
#include <SPI.h>
#include <WiFiNINA.h>

#define DEVICE_ID "M001"

// réseau Wi-Fi
const char* ssid     = "MA_4G";
const char* password = "Morocco2023!";

// Remplacez par l'URL de votre serveur
const char* server = "10.0.0.12"; // Utilisez l'adresse IP du serveur dans le réseau local
const int port = 8000; // Port sur lequel votre serveur écoute
const char* resource = "/receive-data"; // Chemin de l'endpoint

WiFiClient client;

void setup() {
  Serial.begin(9600);

  // Vérifiez si le module WiFi fonctionne
  if (WiFi.status() == WL_NO_MODULE) {
    Serial.println("Communication with WiFi module failed!");
    while (true);
  }

  // Connexion au réseau Wi-Fi
  Serial.print("Connecting to ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, password) != WL_CONNECTED) {
    Serial.print(".");
    delay(5000);
  }
  Serial.println("Connected to WiFi network");
}

void loop() {
    float sensorData = -20.0 + (float)random(1000) / 1000.0 * 40.0;  

  // Données à envoyer
  String jsonData = "{\"deviceID\": '" + String(DEVICE_ID)  + "' , \"typeData\": 'temp', \"valeur\": " + String(sensorData, 2) + "}";

  if (client.connect(server, port)) {
    client.println("POST " + String(resource) + " HTTP/1.1");
    client.println("Host: " + String(server));
    client.println("Content-Type: application/json");
    client.print("Content-Length: ");
    client.println(jsonData.length());
    client.println(); // Ligne vide entre les headers et le corps de la requête
    client.println(jsonData); // Corps de la requête

    Serial.println("Data sent");

    // Attendre et afficher la réponse du serveur
    delay(1000);
    while (client.available()) {
      char c = client.read();
      Serial.write(c);
    }
    client.stop();
  } else {
    Serial.println("Connection failed");
  }

  // Pause avant la prochaine requête
  delay(10000);
}
