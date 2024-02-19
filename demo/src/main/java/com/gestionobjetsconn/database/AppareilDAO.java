package com.gestionobjetsconn.database;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.gestionobjetsconn.models.Actionneur;
import com.gestionobjetsconn.models.Capteur;
import com.gestionobjetsconn.models.ObjetConnecte;
import com.gestionobjetsconn.models.Data;

public class AppareilDAO {

    private Connection connection;

    public AppareilDAO(Connection connection) {
        this.connection = connection;
    }
    
    public String getTypeAppareil(int idAppareil) throws SQLException {
        String typeAppareil = null;
    
        String[] tables = {"Actionneur", "Capteur"};
    
        for (String table : tables) {
            String sql = "SELECT id FROM " + table + " WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, idAppareil);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        typeAppareil = table;
                        break;
                    }
                }
            }
        }
    
        return typeAppareil;
    }
    
    public ObjetConnecte getObjetConnecteById(int idAppareil) throws SQLException {
        ObjetConnecte objetConnecte = null;

        if ( getTypeAppareil(idAppareil).equals("Capteur") ) {
            String sqlCapteur = "SELECT * FROM Capteur INNER JOIN ObjetConnecte ON Capteur.id = ObjetConnecte.id WHERE Capteur.id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlCapteur)) {
                pstmt.setInt(1, idAppareil);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // Construisez un Capteur à partir des résultats
                    objetConnecte = new Capteur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("deviceID"),
                        rs.getString("adresseIP"),
                        rs.getBoolean("etat"),
                        rs.getString("typeMesure"),
                        rs.getString("uniteMesure")
                    );
                }
            }         
        }

    
        if ( getTypeAppareil(idAppareil).equals("Actionneur") ) {
            String sqlActionneur = "SELECT * FROM Actionneur INNER JOIN ObjetConnecte ON Actionneur.id = ObjetConnecte.id WHERE Actionneur.id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlActionneur)) {
                pstmt.setInt(1, idAppareil);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // Construisez un Actionneur à partir des résultats
                    objetConnecte = new Actionneur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("deviceID"),
                        rs.getString("adresseIP"),
                        rs.getBoolean("etat"),
                        rs.getString("typeAction"),
                        rs.getString("emplacement")
                    );
                }
            }
        }
    
        return objetConnecte;
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
                String adresseIP = resultSet.getString("adresseip");
                boolean etat = resultSet.getBoolean("etat");
                String etatString = etat ? "actif" : "inactif";
                
                // Vérifier si c'est un actionneur ou un capteur
                if (resultSet.getString("typeaction") != null) {
                    String typeAction = resultSet.getString("typeaction");
                    String emplacement = resultSet.getString("emplacement");
                    
                    System.out.printf("| %-2d | %-12s | %-14s | %-16s | %-17s | %-14s | %-14s | %-17s |\n", id, nom, adresseIP, etatString, typeAction, emplacement, "-", "-");
                } else if (resultSet.getString("typemesure") != null) {
                    String typeMesure = resultSet.getString("typemesure");
                    String uniteMesure = resultSet.getString("unitemesure");
                    
                    System.out.printf("| %-2d | %-12s | %-14s | %-16s | %-17s | %-14s | %-14s | %-17s |\n", id, nom, adresseIP, etatString, "-", "-", typeMesure, uniteMesure);
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

    public void mettreAJourAppareil(ObjetConnecte appareil, int idAppareil) throws SQLException {
        // Mettre à jour d'abord dans la table ObjetConnecte
        String sqlObjetConnecte = "UPDATE ObjetConnecte SET nom = ?, deviceID = ?, adresseIP = ?, etat = ? WHERE id = ?";
        try (PreparedStatement pstmtObjetConnecte = connection.prepareStatement(sqlObjetConnecte)) {
            pstmtObjetConnecte.setString(1, appareil.getNom());
            pstmtObjetConnecte.setString(2, appareil.getdeviceID());
            pstmtObjetConnecte.setString(3, appareil.getadresseIP());
            pstmtObjetConnecte.setBoolean(4, appareil.getEtat());
            pstmtObjetConnecte.setInt(5, idAppareil);
            pstmtObjetConnecte.executeUpdate();
        }

        // Mettre à jour ensuite dans la table correspondante (Capteur ou Actionneur)
        if (appareil instanceof Capteur) {
            Capteur capteur = (Capteur) appareil;
            String sqlCapteur = "UPDATE Capteur SET typeMesure = ?, uniteMesure = ? WHERE id = ?";
            try (PreparedStatement pstmtCapteur = connection.prepareStatement(sqlCapteur)) {
                pstmtCapteur.setString(1, capteur.getTypeMesure());
                pstmtCapteur.setString(2, capteur.getUniteMesure());
                pstmtCapteur.setInt(3, idAppareil);
                pstmtCapteur.executeUpdate();
            }
        } else if (appareil instanceof Actionneur) {
            Actionneur actionneur = (Actionneur) appareil;
            String sqlActionneur = "UPDATE Actionneur SET typeAction = ?, emplacement = ? WHERE id = ?";
            try (PreparedStatement pstmtActionneur = connection.prepareStatement(sqlActionneur)) {
                pstmtActionneur.setString(1, actionneur.getTypeAction());
                pstmtActionneur.setString(2, actionneur.getEmplacement());
                pstmtActionneur.setInt(3, idAppareil);
                pstmtActionneur.executeUpdate();
            }
        }
    }

        
    public void supprimerAppareil(int idAppareil) throws SQLException {
        String sql = "DELETE FROM ObjetConnecte WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idAppareil);
            pstmt.executeUpdate();
        }
    }    

    public void insererData(int objetConnecteId, List<Data> donnees) throws SQLException {
        String sql = "INSERT INTO Data (objetConnecteId, typeData, valeur) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for ( Data donnee : donnees) {
                pstmt.setInt(1, objetConnecteId);
                pstmt.setString(2, donnee.getTypeData());
                pstmt.setString(3, donnee.getValeur());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

}
