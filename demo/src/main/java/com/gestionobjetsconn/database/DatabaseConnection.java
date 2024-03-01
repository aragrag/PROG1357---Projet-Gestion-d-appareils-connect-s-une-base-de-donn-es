package com.gestionobjetsconn.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection implements AutoCloseable {

    private Connection connection;

    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String TARGET_DB_URL = "jdbc:postgresql://localhost:5432/gestion_objets_intelligents";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";

    public DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");

            // Connexion à la base de données par défaut
            try (Connection defaultConnection = DriverManager.getConnection(DEFAULT_DB_URL, DB_USER, DB_PASSWORD);
                 Statement statement = defaultConnection.createStatement()) {

                // Vérifier l'existence de la base de données cible
                String sqlCheck = "SELECT 1 FROM pg_database WHERE datname='gestion_objets_intelligents'";
                ResultSet resultSet = statement.executeQuery(sqlCheck);
                if (!resultSet.next()) {
                    // Créer la base de données si elle n'existe pas
                    String sqlCreate = "CREATE DATABASE gestion_objets_intelligents";
                    statement.executeUpdate(sqlCreate);
                }
                // System.out.println("Connexion à la base de données réussie.");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Erreur lors de la création de la base de données", e);
            }

            // Connexion à la base de données cible
            this.connection = DriverManager.getConnection(TARGET_DB_URL, DB_USER, DB_PASSWORD);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Driver JDBC introuvable", e);
        }

        // Créer les tables si elles n'existent pas
        try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS ObjetConnecte (id SERIAL PRIMARY KEY, nom VARCHAR(255), deviceID VARCHAR(255), adresseIP VARCHAR(255), etat BOOLEAN);");
                statement.execute("CREATE TABLE IF NOT EXISTS Dispositif (id SERIAL PRIMARY KEY, nom VARCHAR(255), etat BOOLEAN, objetConnecteId INT, FOREIGN KEY (objetConnecteId) REFERENCES ObjetConnecte(id) ON DELETE CASCADE);");
                statement.execute("CREATE TABLE IF NOT EXISTS Capteur (dispositifId INT PRIMARY KEY, typeMesure VARCHAR(255), uniteMesure VARCHAR(255), FOREIGN KEY (dispositifId) REFERENCES Dispositif(id) ON DELETE CASCADE);");
                statement.execute("CREATE TABLE IF NOT EXISTS Actionneur (dispositifId INT PRIMARY KEY, typeAction VARCHAR(255), emplacement VARCHAR(255), FOREIGN KEY (dispositifId) REFERENCES Dispositif(id) ON DELETE CASCADE);");
                statement.execute("CREATE TABLE IF NOT EXISTS Data (id SERIAL PRIMARY KEY, objetConnecteId INT, typeData VARCHAR(255), valeur VARCHAR(255), timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (objetConnecteId) REFERENCES ObjetConnecte(id) ON DELETE CASCADE);");
            } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la création des tables", e);
        }        
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
