package com.gestionobjetsconn;

import com.gestionobjetsconn.database.AppareilDAO;
import com.gestionobjetsconn.database.DatabaseConnection;
import com.gestionobjetsconn.models.Actionneur;
import com.gestionobjetsconn.models.Capteur;
import com.gestionobjetsconn.models.DonneObject;
import com.gestionobjetsconn.models.ObjetConnecte;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.UIDefaults.ActiveValue;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AppHttpServer {



    static class GeneralHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);

            String method = exchange.getRequestMethod();
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            Gson gson = new Gson();
            String responseJson = "";
            int statusCode = 200;

            try (DatabaseConnection dbConnection = new DatabaseConnection()) {
                AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());

                // Handle OPTIONS method for CORS pre-flight request
                if ("OPTIONS".equalsIgnoreCase(method)) {
                    sendResponse(exchange, 204, ""); // No content for pre-flight response
                    return;
                }

                // Route handling
                if (path.equals("/receive-data") && "POST".equalsIgnoreCase(method)) {
                    String requestBody = readRequestBody(exchange);
                    DonneObject donneObject = gson.fromJson(requestBody, DonneObject.class);
                    appareilDAO.enqueueData(donneObject);
                    responseJson = gson.toJson(donneObject) + " enqueued successfully.";
                } else if (path.matches("^/objetsconnecte/(\\d+)/actionneurs$")) {
                    Matcher matcher = Pattern.compile("^/objetsconnecte/(\\d+)/actionneurs$").matcher(path);
                    if (matcher.find()) {
                        int objetConnecteId = Integer.parseInt(matcher.group(1));
                        List<Actionneur> actionneurs = appareilDAO.fetchActionneursByObjetConnecteId(objetConnecteId);
                        String jsonResponse = new Gson().toJson(actionneurs);
                        sendResponse(exchange, 200, jsonResponse);
                    } else {
                        sendResponse(exchange, 404, "Not Found");
                    }
                } else if (path.matches("^/objetsconnecte/(\\d+)/capteurs$")) {
                    Matcher matcher = Pattern.compile("^/objetsconnecte/(\\d+)/capteurs$").matcher(path);
                    if (matcher.find()) {
                        int objetConnecteId = Integer.parseInt(matcher.group(1));
                        List<Capteur> capteurs = appareilDAO.fetchCapteursByObjetConnecteId(objetConnecteId);
                        String jsonResponse = new Gson().toJson(capteurs);
                        sendResponse(exchange, 200, jsonResponse);
                    } else {
                        sendResponse(exchange, 404, "Not Found");
                    }
                }  else if (path.matches("^/objetsconnecte(/\\d+)?$")) {
                    handleObjetsConnecte(exchange, method, path, appareilDAO, gson);
                }  else if (path.matches("^/actionneur(/\\d+)?$")) {
                    handleActionneurs(exchange, method, path, appareilDAO, gson);
                } else if (path.matches("^/actionneur")) {
                    handleActionneurs(exchange, method, path, appareilDAO, gson);
                } else if (path.equals("/capteur") && "POST".equalsIgnoreCase(method)) {
                        String requestBody = readRequestBody(exchange);
                        Capteur capteur = gson.fromJson(requestBody, Capteur.class);
                        appareilDAO.ajouterCapteur(capteur);
                        responseJson = gson.toJson(capteur) + " added successfully.";                    
                }  else if (path.equals("/objetsconnectes") && "GET".equalsIgnoreCase(method)) {
                    List<ObjetConnecte> objetsConnectes = appareilDAO.afficherObjetConnectes();
                    responseJson = gson.toJson(objetsConnectes);
                } else {
                    statusCode = 404;
                    responseJson = "Not Found 22";
                }

                sendResponse(exchange, statusCode, responseJson);
            } catch (SQLException e) {
                sendResponse(exchange, 500, "Database error: " + e.getMessage());
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, 400, "JSON syntax error: " + e.getMessage());
            }
        }

        private void handleObjetsConnecte(HttpExchange exchange, String method, String path, AppareilDAO appareilDAO, Gson gson) throws IOException, SQLException {
            String responseJson;
            int statusCode = 200;
            // Specific handling for /objetsconnecte/:id
            if (path.matches("^/objetsconnecte/\\d+$")) {
                
                int id = Integer.parseInt(path.split("/")[2]);
                if ("GET".equalsIgnoreCase(method)) {
                    ObjetConnecte objetConnecte = appareilDAO.fetchObjetConnecteById(id); // Implement this method in your DAO
                    String jsonResponse = new Gson().toJson(objetConnecte);
                    sendResponse(exchange, 200, jsonResponse);
                } else if ("PUT".equalsIgnoreCase(method)) {
                    // Read the request body to get updated object data
                    String requestBody = readRequestBody(exchange);
                    ObjetConnecte updatedObjetConnecte = new Gson().fromJson(requestBody, ObjetConnecte.class);
                    // Set the ID for the updated object
                    updatedObjetConnecte.setID(id);
                    // Update the object in the database
                    boolean updated = appareilDAO.updateObjetConnecte(updatedObjetConnecte);
                    if (updated) {
                        sendResponse(exchange, 200, "Object Updated Successfully");
                    } else {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    // Delete the object from the database
                    boolean deleted = appareilDAO.deleteObjetConnecteById(id);
                    if (deleted) {
                        sendResponse(exchange, 200, "Object Deleted Successfully");
                    } else {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else {
                    statusCode = 405;
                    responseJson = "Method Not Allowed";
                }
            } else if (path.equals("/objetsconnecte") && "POST".equalsIgnoreCase(method)) {
                // Add new objet connecte logic here
                String requestBody = readRequestBody(exchange);
                ObjetConnecte objetConnecte = gson.fromJson(requestBody, ObjetConnecte.class);
                appareilDAO.ajouterObjetConnecte(objetConnecte);
                responseJson = gson.toJson(objetConnecte) + " added successfully.";
                sendResponse(exchange, statusCode, responseJson);
            } else {
                statusCode = 404;
                responseJson = "Not Found 11";
                sendResponse(exchange, statusCode, responseJson);
            }
        }
        private void handleActionneurs(HttpExchange exchange, String method, String path, AppareilDAO appareilDAO, Gson gson) throws IOException, SQLException {
            String responseJson;
            int statusCode = 200;
            // Specific handling for /objetsconnecte/:id
            if (path.matches("^/actionneur/\\d+$")) {
                
                int id = Integer.parseInt(path.split("/")[2]);
                if ("GET".equalsIgnoreCase(method)) {
                    Actionneur actionneur = appareilDAO.fetchActionneurById(id); // Implement this method in your DAO
                    String jsonResponse = new Gson().toJson(actionneur);
                    sendResponse(exchange, 200, jsonResponse);
                } else if ("PUT".equalsIgnoreCase(method)) {

                    String requestBody = readRequestBody(exchange);
                    Actionneur updatedActionneur = new Gson().fromJson(requestBody, Actionneur.class);
                    updatedActionneur.setID(id);
                    boolean updated = appareilDAO.updateActionneur(updatedActionneur);
                    if (updated) {
                        sendResponse(exchange, 200, "Object Updated Successfully");
                    } else {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    // Delete the object from the database
                    boolean deleted = appareilDAO.deleteActionneurById(id);
                    if (deleted) {
                        sendResponse(exchange, 200, "Object Deleted Successfully");
                    } else {
                        sendResponse(exchange, 500, "Internal Server Error");
                    }
                } else {
                    statusCode = 405;
                    responseJson = "Method Not Allowed";
                }
            } else if (path.equals("/objetsconnecte") && "POST".equalsIgnoreCase(method)) {
                // Add new objet connecte logic here
                String requestBody = readRequestBody(exchange);
                ObjetConnecte objetConnecte = gson.fromJson(requestBody, ObjetConnecte.class);
                appareilDAO.ajouterObjetConnecte(objetConnecte);
                responseJson = gson.toJson(objetConnecte) + " added successfully.";
                sendResponse(exchange, statusCode, responseJson);
            } else if (path.matches("^/actionneur")) {
                if ("POST".equalsIgnoreCase(method)) {
                    String requestBody = readRequestBody(exchange);
                    Actionneur actionneur = gson.fromJson(requestBody, Actionneur.class);
                    appareilDAO.ajouterActionneur(actionneur);
                    responseJson = gson.toJson(actionneur) + " added successfully.";
                }  
            } else {
                statusCode = 404;
                responseJson = "Not Found 11";
                sendResponse(exchange, statusCode, responseJson);
            }
        }      


    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        // Set proper headers for CORS
        setCorsHeaders(exchange);
    

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    private static void setCorsHeaders(HttpExchange exchange) {
        // Set proper CORS headers
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
    } 
}
