package com.gestionobjetsconn;

import com.fasterxml.jackson.core.JsonParseException;
import com.gestionobjetsconn.database.AppareilDAO;
import com.gestionobjetsconn.database.DatabaseConnection;
import com.gestionobjetsconn.models.Actionneur;
import com.gestionobjetsconn.models.Capteur;
import com.gestionobjetsconn.models.DonneObject;
import com.gestionobjetsconn.models.ObjetConnecte;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AppHttpServer {



    static class GeneralHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String json = readRequestBody(exchange);
            System.out.println("Received data: " + json);

            try (DatabaseConnection dbConnection = new DatabaseConnection()) {
                AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());
                Object entity = null;
                Gson gson = new Gson();
                switch (path) {
                    case "/receive-data":
                        
                        DonneObject donneObject =  gson.fromJson(json, DonneObject.class);
                        appareilDAO.enqueueData(donneObject);
                        break;
                    case "/objetsconnecte":
                        entity = gson.fromJson(json, ObjetConnecte.class);
                        appareilDAO.ajouterObjetConnecte((ObjetConnecte) entity);
                        break;
                    case "/actionneur":
                        entity = gson.fromJson(json, Actionneur.class);
                        appareilDAO.ajouterActionneur((Actionneur) entity);
                        break;
                    case "/capteur":
                        Capteur capteur =  gson.fromJson(json, Capteur.class);
                        appareilDAO.ajouterCapteur(capteur);
                        break;
                    default:
                        sendResponse(exchange, 404, "Not Found");
                        return;
                }

                sendResponse(exchange, 200, gson.toJson(entity) + " processed successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Database error: " + e.getMessage());
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, 400, "JSON syntax error: " + e.getMessage());
            }
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
