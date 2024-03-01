
package com.gestionobjetsconn;

import com.fasterxml.jackson.core.JsonParseException;
import com.gestionobjetsconn.database.AppareilDAO;
import com.gestionobjetsconn.database.DatabaseConnection;
import com.gestionobjetsconn.models.DonneObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AppHttpServer {

    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                // Read JSON data from the request body
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }
                
                String json = sb.toString();

                // Log received data
                System.out.println("Received data: " + json);

                // Parse JSON into DonneObject
                DonneObject donneObject = parseJson(json);

                // Process the received data
                String response;
                if (donneObject != null) {
                    response = "Data received successfully: " + donneObject.toString();
                    try (DatabaseConnection dbConnection = new DatabaseConnection()) {
                        AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());        
                        appareilDAO.enqueueData(donneObject);
                    
                    } catch (SQLException e) {
                        System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
                    }                     
                } else {
                    response = "Failed to parse JSON data";
                }

                // Send response
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }

        private DonneObject parseJson(String json) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(json, DonneObject.class);
            } catch (JsonSyntaxException e) {
                System.out.println("JSON syntax error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("General error: " + e.getMessage());
            }
            return null;
        }

        
    }
}
