package com.gestionobjetsconn.database;

import java.util.Scanner; 
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.gestionobjetsconn.models.Actionneur;
import com.gestionobjetsconn.models.Capteur;
import com.gestionobjetsconn.models.Dispositif;
import com.gestionobjetsconn.models.ObjetConnecte;
import com.gestionobjetsconn.models.DonneObject;

public class AppareilDAO {

    private Connection connection;

    private static Queue<DonneObject> queue = new LinkedList<>();

    public AppareilDAO(Connection connection) {
        this.connection = connection;
    }

    public String getTypeAppareil(int idAppareil) throws SQLException {
        // Vérifier dans la table Capteur
        String sqlCapteur = "SELECT dispositifId FROM Capteur WHERE dispositifId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCapteur)) {
            pstmt.setInt(1, idAppareil);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "Capteur";
                }
            }
        }
        
        // Vérifier dans la table Actionneur
        String sqlActionneur = "SELECT dispositifId FROM Actionneur WHERE dispositifId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlActionneur)) {
            pstmt.setInt(1, idAppareil);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "Actionneur";
                }
            }
        }
    
        // Si l'ID n'est trouvé ni dans Capteur ni dans Actionneur, c'est peut-être un ObjetConnecte
        String sqlObjetConnecte = "SELECT id FROM ObjetConnecte WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlObjetConnecte)) {
            pstmt.setInt(1, idAppareil);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "ObjetConnecte";
                }
            }
        }
        
        // Si l'ID n'est trouvé dans aucune table
        return null;
    }
        
    public String getTypeDispositif(int idDispositif) throws SQLException {
        String typeAppareil = null;
    
        String[] tables = {"Actionneur", "Capteur"};
    
        for (String table : tables) {
            String sql = "SELECT dispositifId FROM " + table + " WHERE dispositifId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, idDispositif);
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
    
    public Dispositif getObjetConnecteById(int idAppareil) throws SQLException {
        Dispositif Dispositif = null;

        if ( getTypeDispositif(idAppareil).equals("Capteur") ) {
            String sqlCapteur = "SELECT * FROM Capteur INNER JOIN Dispositif ON Capteur.dispositifId = Dispositif.id WHERE Capteur.dispositifId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlCapteur)) {
                pstmt.setInt(1, idAppareil);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // Construisez un Capteur à partir des résultats
                    Dispositif = new Capteur(
                        rs.getInt("dispositifId"),
                        rs.getString("nom"),
                        rs.getBoolean("etat"),
                        rs.getString("typeMesure"),
                        rs.getString("uniteMesure"),
                        rs.getInt("objetConnecteId")
                    );
                }
            }         
        }

    
        if ( getTypeDispositif(idAppareil).equals("Actionneur") ) {
            String sqlActionneur = "SELECT * FROM Actionneur INNER JOIN Dispositif ON Actionneur.dispositifId = Dispositif.id WHERE Actionneur.dispositifId = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sqlActionneur)) {
                pstmt.setInt(1, idAppareil);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // Construisez un Actionneur à partir des résultats
                    Dispositif = new Actionneur(
                        rs.getInt("dispositifId"),
                        rs.getString("nom"),
                        rs.getBoolean("etat"),
                        rs.getString("typeAction"),
                        rs.getString("emplacement"),
                        rs.getInt("objetConnecteId")

                    );
                }
            }
        }
    
        return Dispositif;
    }

    public Integer getIdParNomAppareil(String nom) throws SQLException {
        // Essayez d'abord dans la table ObjetConnecte
        String sqlObjetConnecte = "SELECT id FROM ObjetConnecte WHERE nom = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlObjetConnecte)) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        
        // Ensuite, vérifiez la table Dispositif si nécessaire
        String sqlActionneur = "SELECT id FROM Dispositif  WHERE Dispositif.nom = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlActionneur)) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        
        
        // Retournez null si aucun appareil n'a été trouvé avec ce nom
        return null;
    }

    
    public List<ObjetConnecte> afficherObjetConnectes() throws SQLException {
        // SQL query to select all from ObjetConnecte table
        String sql = "SELECT * FROM ObjetConnecte ORDER BY id";
        List<ObjetConnecte> objetsConnectes = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery()) {
    
            // Print the header for better readability
            System.out.println("+----+-------------------+-----------------+-------------+---------+");
            System.out.println("| ID | Nom               | DeviceID        | Adresse IP  | État    |");
            System.out.println("+----+-------------------+-----------------+-------------+---------+");
    
            // Iterate through the result set and print each record
            while (resultSet.next()) {
                
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String deviceID = resultSet.getString("deviceID");
                String adresseIP = resultSet.getString("adresseIP");
                boolean etat = resultSet.getBoolean("etat");
                String etatString = etat ? "actif" : "inactif";
                
                objetsConnectes.add(new ObjetConnecte(
                    id,
                    nom,
                    deviceID,
                    adresseIP,
                    etat
                ));

                // Print each row
                System.out.printf("| %-2d | %-17s | %-15s | %-11s | %-7s |\n", id, nom, deviceID, adresseIP, etatString);
            }
            System.out.println("+----+-------------------+-----------------+-------------+---------+");
        }
        
        return objetsConnectes;
    }
        
    public void afficherDispositif() throws SQLException {
        String sql = "SELECT Dispositif.id, Dispositif.nom, ObjetConnecte.deviceID, ObjetConnecte.adresseIP, Dispositif.etat, Actionneur.typeAction, Actionneur.emplacement, Capteur.typeMesure, Capteur.uniteMesure " +
                     "FROM Dispositif " +
                     "LEFT JOIN ObjetConnecte ON Dispositif.objetConnecteId = ObjetConnecte.id " +
                     "LEFT JOIN Actionneur ON Dispositif.id = Actionneur.dispositifId " +
                     "LEFT JOIN Capteur ON Dispositif.id = Capteur.dispositifId " +
                     "ORDER BY Dispositif.id";
    
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery()) {
    
            System.out.println("+----+----------+-------------------+-------------+--------+-------------+-------------+------------+-------------+");
            System.out.println("|ID  | Nom      |Device ID          | Adresse IP  | État   |Type Action  | Emplacement |Type Mesure |Unité Mesure |");
            System.out.println("+----+----------+-------------------+-------------+--------+-------------+-------------+------------+-------------+");
    
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String deviceID = resultSet.getString("deviceID"); // Ajusté selon la nomenclature correcte
                String adresseIP = resultSet.getString("adresseIP"); // Ajusté selon la nomenclature correcte
                boolean etat = resultSet.getBoolean("etat");
                String etatString = etat ? "actif" : "inactif";
                String typeAction = resultSet.getString("typeAction");
                String emplacement = resultSet.getString("emplacement");
                String typeMesure = resultSet.getString("typeMesure");
                String uniteMesure = resultSet.getString("uniteMesure");
    
                // Formatter correctement selon que le dispositif est un actionneur ou un capteur
                System.out.printf("|%-4d|%-10s|%-19s|%-13s|%-8s|%-13s|%-13s|%-12s|%-13s|\n",
                                  id, nom, deviceID, adresseIP, etatString, 
                                  typeAction != null ? typeAction : "-", 
                                  emplacement != null ? emplacement : "-", 
                                  typeMesure != null ? typeMesure : "-", 
                                  uniteMesure != null ? uniteMesure : "-");
            }
            System.out.println("+----+----------+-------------------+-------------+--------+-------------+-------------+------------+-------------+");
        }
    }
    
    public void ajouterObjetConnecte(ObjetConnecte objetConnecte) throws SQLException {
        // Insérer d'abord dans la table ObjetConnecte
        String sqlDispositif = "INSERT INTO ObjetConnecte (nom, deviceID, adresseIP, etat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmtObjetConnecte = connection.prepareStatement(sqlDispositif, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmtObjetConnecte.setString(1,objetConnecte.getNom());
            pstmtObjetConnecte.setString(2,  objetConnecte.getdeviceID());
            pstmtObjetConnecte.setString(3, objetConnecte.getadresseIP());
            pstmtObjetConnecte.setBoolean(4, objetConnecte.getEtat());

            pstmtObjetConnecte.executeUpdate();
        }
    }
   
    
    public void ajouterActionneur(Actionneur actionneur) throws SQLException {
        // Insérer d'abord dans la table ObjetConnecte
        String sqlDispositif = "INSERT INTO Dispositif (nom, etat, objetConnecteId) VALUES (?, ?, ?)";
        try (PreparedStatement pstmtObjetConnecte = connection.prepareStatement(sqlDispositif, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmtObjetConnecte.setString(1,actionneur.getNom());
            pstmtObjetConnecte.setBoolean(2,  actionneur.getEtat());
            pstmtObjetConnecte.setInt(3, actionneur.getObjetConnecteId());

            pstmtObjetConnecte.executeUpdate();

            // Récupérer l'id généré pour ObjetConnecte
            ResultSet generatedKeys = pstmtObjetConnecte.getGeneratedKeys();
            if (generatedKeys.next()) {
                int dispositifId = generatedKeys.getInt(1);

                // Insérer ensuite dans la table Actionneur avec l'id d'ObjetConnecte
                String sqlActionneur = "INSERT INTO Actionneur (dispositifId, typeAction, emplacement) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtActionneur = connection.prepareStatement(sqlActionneur)) {
                    pstmtActionneur.setInt(1, dispositifId);
                    pstmtActionneur.setString(2, actionneur.getTypeAction());
                    pstmtActionneur.setString(3, actionneur.getEmplacement());
                    pstmtActionneur.executeUpdate();
                }
            }
        }
    }

    public void ajouterCapteur(Capteur capteur) throws SQLException {
        // Insérer d'abord dans la table ObjetConnecte
        String sqlDispositif = "INSERT INTO Dispositif (nom, etat, objetConnecteId) VALUES (?, ?, ?)";
        try (PreparedStatement pstmtObjetConnecte = connection.prepareStatement(sqlDispositif, Statement.RETURN_GENERATED_KEYS)) {

            pstmtObjetConnecte.setString(1,capteur.getNom());
            pstmtObjetConnecte.setBoolean(2,  capteur.getEtat());
            pstmtObjetConnecte.setInt(3, capteur.getObjetConnecteId());

            pstmtObjetConnecte.executeUpdate();

            // Récupérer l'id généré pour ObjetConnecte
            ResultSet generatedKeys = pstmtObjetConnecte.getGeneratedKeys();
            if (generatedKeys.next()) {
                int dispositifId = generatedKeys.getInt(1);

                // Insérer ensuite dans la table Actionneur avec l'id d'ObjetConnecte
                String sqlCapteur = "INSERT INTO Capteur (dispositifId, typeMesure, uniteMesure) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtCapteur = connection.prepareStatement(sqlCapteur)) {
                    pstmtCapteur.setInt(1, dispositifId);
                    pstmtCapteur.setString(2, capteur.getTypeMesure());
                    pstmtCapteur.setString(3, capteur.getUniteMesure());
                    pstmtCapteur.executeUpdate();
                }
            }
        }
    } 

    public boolean mettreAJourAppareil(Object appareil, int idAppareil) throws SQLException {
        
        if (appareil instanceof ObjetConnecte) {
            // Mettre à jour un ObjetConnecte
            ObjetConnecte objetConnecte = (ObjetConnecte) appareil;
            String sqlObjetConnecte = "UPDATE ObjetConnecte SET nom = ?, deviceID = ?, adresseIP = ?, etat = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlObjetConnecte)) {
                pstmt.setString(1, objetConnecte.getNom());
                pstmt.setString(2, objetConnecte.getdeviceID()); // Assurez-vous que c'est getDeviceID() et non getdeviceID()
                pstmt.setString(3, objetConnecte.getadresseIP());
                pstmt.setBoolean(4, objetConnecte.getEtat());
                pstmt.setInt(5, idAppareil);
                int rowsUpdated = pstmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } else if (appareil instanceof Dispositif) {
            // Mettre à jour un Dispositif (Capteur ou Actionneur)
            Dispositif dispositif = (Dispositif) appareil;
            // Mettre à jour les informations générales du dispositif
            String sqlDispositif = "UPDATE Dispositif SET nom = ?, etat = ? WHERE id = ?";
            try (PreparedStatement pstmtDispositif = connection.prepareStatement(sqlDispositif)) {
                pstmtDispositif.setString(1, dispositif.getNom());
                pstmtDispositif.setBoolean(2, dispositif.getEtat());
                pstmtDispositif.setInt(3, idAppareil);
                
            }    

            if (dispositif instanceof Capteur) {
                Capteur capteur = (Capteur) dispositif;
                // Assurez-vous d'avoir une colonne ou une manière de lier Capteur à ObjetConnecte si nécessaire
                String sqlCapteur = "UPDATE Capteur SET typeMesure = ?, uniteMesure = ? WHERE dispositifId = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlCapteur)) {
                    pstmt.setString(1, capteur.getTypeMesure());
                    pstmt.setString(2, capteur.getUniteMesure());
                    pstmt.setInt(3, idAppareil);
                    int rowsUpdated = pstmt.executeUpdate();
                    return rowsUpdated > 0;
                }
            } else if (dispositif instanceof Actionneur) {
                Actionneur actionneur = (Actionneur) dispositif;
                // Assurez-vous d'avoir une colonne ou une manière de lier Actionneur à ObjetConnecte si nécessaire
                String sqlActionneur = "UPDATE Actionneur SET typeAction = ?, emplacement = ? WHERE dispositifId = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlActionneur)) {
                    pstmt.setString(1, actionneur.getTypeAction());
                    pstmt.setString(2, actionneur.getEmplacement());
                    pstmt.setInt(3, idAppareil);
                    int rowsUpdated = pstmt.executeUpdate();
                    return rowsUpdated > 0;
                }
            }
        } else {
            System.out.println("Type d'appareil non reconnu pour la mise à jour.");
        }
        return false;
    }
    

    

    public void supprimerAppareilParNom(String nomAppareil) throws SQLException {
    
        // get l'ID de l'appareil par son nom.
        String table = "";
        Integer idAppareil = getIdParNomAppareil(nomAppareil);
        System.out.println(idAppareil);

        if (idAppareil != null) {
            
            //Déterminer le type d'appareil pour l'ID récupéré.
            String typeAppareil = getTypeAppareil(idAppareil);
            
            if (typeAppareil == null) {
                System.out.println("Type appareil non trouvé pour: " + nomAppareil);
                return;
            }else{
                if (typeAppareil == "Actionneur" || typeAppareil == "Capteur") {
                    table = "Dispositif";
                }else if(typeAppareil == "ObjetConnecte"){
                    table = "ObjetConnecte";
                }
            }

            System.out.println("T'es sur de supprimer l'appareil avec le nom: " + nomAppareil + " et le ID " + idAppareil + "? (yes/no)");
            Scanner scanner2 = new Scanner(System.in);  
            String userInput = scanner2.nextLine();
            
            if ("yes".equalsIgnoreCase(userInput)) {
                // Suppression de l'appareil en fonction du type.
                String sqlDelete = "DELETE FROM " + table + " WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDelete)) {
                    pstmt.setInt(1, idAppareil);
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        System.out.println("L'appareil '" + nomAppareil + "' de type '" + typeAppareil + "' a été supprimé avec succès.");
                    } else {
                        System.out.println("La suppression a échoué. Aucun appareil trouvé avec le nom '" + nomAppareil + "'.");
                    }
                }
            } else {
                System.out.println("La suppression est annule.");
            }
            

        } else {
            System.out.println("Aucune Appareil n'existe avec ce nom.\n\n");
        }
    }
    

    public void insererDonneesEnMasse(Collection<DonneObject> donnees) throws SQLException {
        String sql = "INSERT INTO Data (objetConnecteId, typeData, valeur) VALUES (?, ?, ?)";
        connection.setAutoCommit(false); // Pour gérer la transaction manuellement

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (DonneObject donnee : donnees) {
                pstmt.setInt(1, donnee.getIDbyName(donnee.getdeviceID()));
                pstmt.setString(2, donnee.getTypeData());
                pstmt.setString(3, donnee.getValeur());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit(); // Commit la transaction si tout s'est bien passé
        } catch (SQLException e) {
            connection.rollback(); // Rollback la transaction en cas d'erreur
            throw e;
        } finally {
            connection.setAutoCommit(true); // Remet le comportement de commit par défaut
        }
    }
 
    public synchronized void enqueueData(DonneObject donneObject) throws SQLException {
        queue.offer(donneObject);
        System.err.println(queue.size());

        // Vérifier si la taille de la queue dépasse le seuil pour l'insertion en masse
        if (queue.size() >= 20) {
            insererDonneesEnMasse(queue);
            queue.clear(); // Nettoyer la queue après l'insertion
        }
    }

    // public void insererData(int objetConnecteId, Collection<DonneObject> donnees) throws SQLException {
    //     String sql = "INSERT INTO Data (objetConnecteId, typeData, valeur) VALUES (?, ?, ?)";
    //     try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
    //         for ( DonneObject donnee : donnees) {
    //             pstmt.setInt(1, objetConnecteId);
    //             pstmt.setString(2, donnee.getTypeData());
    //             pstmt.setString(3, donnee.getValeur());
    //             pstmt.addBatch();
    //         }
    //         pstmt.executeBatch();
    //     }
    // }
    public ObjetConnecte fetchObjetConnecteById(int id) throws SQLException {
        String sql = "SELECT * FROM ObjetConnecte WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ObjetConnecte(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("deviceID"),
                        rs.getString("adresseIP"),
                        rs.getBoolean("etat")
                    );
                }
            }
        }
        return null;
    }
    
    public boolean updateObjetConnecte(ObjetConnecte objetConnecte) throws SQLException {
        String sql = "UPDATE ObjetConnecte SET nom = ?, deviceID = ?, adresseIP = ?, etat = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, objetConnecte.getNom());
            pstmt.setString(2, objetConnecte.getdeviceID());
            pstmt.setString(3, objetConnecte.getadresseIP());
            pstmt.setBoolean(4, objetConnecte.getEtat());
            pstmt.setInt(5, objetConnecte.getID());
    
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }
    
    public boolean deleteObjetConnecteById(int id) throws SQLException {
        String sql = "DELETE FROM ObjetConnecte WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
    
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        }
    }
    
    public List<Actionneur> afficherActionneurs() throws SQLException {
        // SQL query to select all from ObjetConnecte table
        String sql = "SELECT * FROM ObjetConnecte ORDER BY id";
        List<Actionneur> Actionneurs = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery()) {

            while (resultSet.next()) {
                
                int id = resultSet.getInt("id");
                int objetConnecteId = resultSet.getInt("objetConnecteId");
                String nom = resultSet.getString("nom");
                String typeAction = resultSet.getString("typeAction");
                String emplacement = resultSet.getString("emplacement");
                boolean etat = resultSet.getBoolean("etat");
                String etatString = etat ? "actif" : "inactif";
                
                Actionneurs.add(new Actionneur(
                    id,
                    nom,
                    etat,
                    typeAction,
                    emplacement,
                    objetConnecteId
                ));

            }
        }
        
        return Actionneurs;
    }
        
    public List<Actionneur> fetchActionneursByObjetConnecteId(int objetConnecteId) throws SQLException {
        List<Actionneur> actionneurs = new ArrayList<>();
        String sql = "SELECT a.dispositifId, d.nom, d.etat, a.typeAction, a.emplacement, d.objetConnecteId FROM Actionneur a INNER JOIN Dispositif d ON a.dispositifId = d.id WHERE d.objetConnecteId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, objetConnecteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    Actionneur actionneur = new Actionneur(
                        rs.getInt("dispositifId"),
                        rs.getString("nom"),
                        rs.getBoolean("etat"),
                        rs.getString("typeAction"),
                        rs.getString("emplacement"),
                        objetConnecteId 
                    );
                    actionneurs.add(actionneur);
                }
            }
        }
        return actionneurs;
    }
    
    public List<Capteur> fetchCapteursByObjetConnecteId(int objetConnecteId) throws SQLException {
        List<Capteur> capteurs = new ArrayList<>();
        String sql = "SELECT c.dispositifId, d.nom, d.etat, c.typeMesure, c.uniteMesure, d.objetConnecteId FROM Capteur c INNER JOIN Dispositif d ON c.dispositifId = d.id WHERE d.objetConnecteId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, objetConnecteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    Capteur capteur = new Capteur(
                        rs.getInt("dispositifId"),
                        rs.getString("nom"),
                        rs.getBoolean("etat"),
                        rs.getString("typeMesure"),
                        rs.getString("uniteMesure"),
                        objetConnecteId 
                    );
                    capteurs.add(capteur);
                }
            }
        }
        return capteurs;
    }

    public Actionneur fetchActionneurById(int id) throws SQLException {
        String sql = "SELECT a.dispositifId, d.nom, d.etat, a.typeAction, a.emplacement, d.objetConnecteId FROM Actionneur a INNER JOIN Dispositif d ON a.dispositifId = d.id WHERE d.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Actionneur(
                        rs.getInt("dispositifId"),
                        rs.getString("nom"),
                        rs.getBoolean("etat"),
                        rs.getString("typeAction"),
                        rs.getString("emplacement"),
                        rs.getInt("objetConnecteId")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateActionneur(Actionneur actionneur) throws SQLException {
        boolean updated = false;
        String updateDispositifSql = "UPDATE Dispositif SET nom = ?, etat = ? , objetConnecteId = ? WHERE id = ?";
        String updateActionneurSql = "UPDATE Actionneur SET typeAction = ?, emplacement = ? WHERE dispositifId = ?";
        
        try (
            PreparedStatement updateDispositifStmt = connection.prepareStatement(updateDispositifSql);
            PreparedStatement updateActionneurStmt = connection.prepareStatement(updateActionneurSql);
        ) {
            // Mise à jour dans la table Dispositif
            updateDispositifStmt.setString(1, actionneur.getNom());
            updateDispositifStmt.setBoolean(2, actionneur.getEtat());
            updateDispositifStmt.setInt(3, actionneur.getobjetConnecteId()); // Ici, 'id' est l'ID du dispositif
            updateDispositifStmt.setInt(4, actionneur.getID()); // Ici, 'id' est l'ID du dispositif
            int dispositifRowsUpdated = updateDispositifStmt.executeUpdate();
    
            // Mise à jour dans la table Actionneur
            updateActionneurStmt.setString(1, actionneur.getTypeAction());
            updateActionneurStmt.setString(2, actionneur.getEmplacement());
            updateActionneurStmt.setInt(3, actionneur.getID()); // Ici, 'id' est l'ID du dispositif
            int actionneurRowsUpdated = updateActionneurStmt.executeUpdate();
    
            // Vérifie si les deux mises à jour ont réussi
            updated = (dispositifRowsUpdated > 0 && actionneurRowsUpdated > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception comme vous le jugez nécessaire
        }
    
        return updated;
    }
    

    
    public boolean deleteActionneurById(int id) throws SQLException {
        String sql = "DELETE FROM Actionneur WHERE dispositifId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
    
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        }
    }

         
}
