package com.gestionobjetsconn.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.gestionobjetsconn.models.Actionneur;
import com.gestionobjetsconn.models.Capteur;

public class AppareilDAO {

    private Connection connection;

    public AppareilDAO(Connection connection) {
        this.connection = connection;
    }
    public void afficherAppareils() throws SQLException {
        String sql = "SELECT * FROM ObjetConnecte LEFT JOIN Actionneur ON ObjetConnecte.id = Actionneur.id LEFT JOIN Capteur ON ObjetConnecte.id = Capteur.id ORDER BY ObjetConnecte.id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery()) {
    
            System.out.println("+----+--------------+----------------+------------------+-------------------+----------------+----------------+-------------------+");
            System.out.println("| ID |     Nom      |   Adresse IP   |      État        |    Type Action    |   Emplacement  |  Type Mesure   |  Unité Mesure     |");
            System.out.println("+----+--------------+----------------+------------------+-------------------+----------------+----------------+-------------------+");
    
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String adresseIP = resultSet.getString("adresseIP");
                boolean etat = resultSet.getBoolean("etat");
                String etatString = etat ? "actif" : "inactif";
    
                // Vérifier si c'est un actionneur ou un capteur
                if (resultSet.getString("typeAction") != null) {
                    String typeAction = resultSet.getString("typeAction");
                    String emplacement = resultSet.getString("emplacement");
                    
                    System.out.printf("| %-2d | %-12s | %-14s | %-16s | %-17s | %-14s | %-14s | %-17s |\n", id, nom, adresseIP, etatString, typeAction, emplacement, "-", "-");
                } else if (resultSet.getString("typeMesure") != null) {
                    String typeMesure = resultSet.getString("typeMesure");
                    String uniteMesure = resultSet.getString("uniteMesure");
                    
                    System.out.printf("| %-2d | %-12s | %-14s | %-16s | %-17s | %-14s | %-14s | %-17s |\n", id, nom, adresseIP, etatString, "-", "-", "-", typeMesure, uniteMesure);
                }
            }
            System.out.println("+----+--------------+----------------+------------------+-------------------+----------------+----------------+-------------------+");
        }
    }
    
    
    public void ajouterActionneur(Actionneur actionneur) throws SQLException {
        // Insérer d'abord dans la table ObjetConnecte
        String sqlObjetConnecte = "INSERT INTO ObjetConnecte (nom, deviceID, adresseIP, etat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmtObjetConnecte = connection.prepareStatement(sqlObjetConnecte, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmtObjetConnecte.setString(1,actionneur.getNom());
            pstmtObjetConnecte.setString(2,actionneur.getdeviceID());
            pstmtObjetConnecte.setString(3, actionneur.getadresseIP());
            pstmtObjetConnecte.setBoolean(4,  actionneur.getEtat());
            pstmtObjetConnecte.executeUpdate();

            // Récupérer l'id généré pour ObjetConnecte
            ResultSet generatedKeys = pstmtObjetConnecte.getGeneratedKeys();
            if (generatedKeys.next()) {
                int objetConnecteId = generatedKeys.getInt(1);

                // Insérer ensuite dans la table Actionneur avec l'id d'ObjetConnecte
                String sqlActionneur = "INSERT INTO Actionneur (id, typeAction, emplacement) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtActionneur = connection.prepareStatement(sqlActionneur)) {
                    pstmtActionneur.setInt(1, objetConnecteId);
                    pstmtActionneur.setString(2, actionneur.getTypeAction());
                    pstmtActionneur.setString(3, actionneur.getEmplacement());
                    pstmtActionneur.executeUpdate();
                }
            }
        }
    }

    public void ajouterCapteur(Capteur capteur) throws SQLException {
        // Insérer d'abord dans la table ObjetConnecte
        String sqlObjetConnecte = "INSERT INTO ObjetConnecte (nom, deviceID, adresseIP, etat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmtObjetConnecte = connection.prepareStatement(sqlObjetConnecte, Statement.RETURN_GENERATED_KEYS)) {

            pstmtObjetConnecte.setString(1,capteur.getNom());
            pstmtObjetConnecte.setString(2,capteur.getdeviceID());
            pstmtObjetConnecte.setString(3, capteur.getadresseIP());
            pstmtObjetConnecte.setBoolean(4,  capteur.getEtat());
            pstmtObjetConnecte.executeUpdate();

            // Récupérer l'id généré pour ObjetConnecte
            ResultSet generatedKeys = pstmtObjetConnecte.getGeneratedKeys();
            if (generatedKeys.next()) {
                int objetConnecteId = generatedKeys.getInt(1);

                // Insérer ensuite dans la table Actionneur avec l'id d'ObjetConnecte
                String sqlActionneur = "INSERT INTO Capteur (id, typeMesure, uniteMesure) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtCapteur = connection.prepareStatement(sqlActionneur)) {
                    pstmtCapteur.setInt(1, objetConnecteId);
                    pstmtCapteur.setString(2, capteur.getTypeMesure());
                    pstmtCapteur.setString(3, capteur.getUniteMesure());
                    pstmtCapteur.executeUpdate();
                }
            }
        }
    } 

    public void mettreAJourEtatAppareil(int idAppareil, boolean nouvelEtat) throws SQLException {
        
        // Écriver le code SQL pour mettre à jour l'état de l'appareil avec l'ID spécifié
        String sql = "UPDATE ObjetConnecte SET etat = ? WHERE id = ?";
        
        // Utiliser un PreparedStatement pour exécuter la mise à jour
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, nouvelEtat);
            pstmt.setInt(2, idAppareil);
            pstmt.executeUpdate();
        }
    }
        
    public void supprimerAppareil(int idAppareil) throws SQLException {
        String sql = "DELETE FROM ObjetConnecte WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idAppareil);
            pstmt.executeUpdate();
        }
    }    

}
