package com.gestionobjetsconn.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gestionobjetsconn.database.DatabaseConnection;

public class DonneObject {
    private String deviceID;
    private String typeData;
    private String valeur;
    private String date_entry;

    public DonneObject() {
    }

    public DonneObject(String typeData, String valeur) {
        this.typeData = typeData;
        this.valeur = valeur;
    }
    public DonneObject(String typeData, String valeur, String date_entry) {
        this.typeData = typeData;
        this.valeur = valeur;
        this.date_entry = date_entry;
    }    
    public String getdeviceID() {
        return deviceID;
    }

    public void setdeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getTypeData() {
        return typeData;
    }

    public void setTypeData(String typeData) {
        this.typeData = typeData;
    }

    public String getValeur() {
        return valeur;
    }
    public String getEntryDate() {
        return date_entry;
    }
    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
    public void setEntryDate(String string) {
        this.date_entry = date_entry;
    }
    @Override
    public String toString() {
        return "DonneObject{" +
                "deviceID='" + deviceID + '\'' +
                "typeData='" + typeData + '\'' +
                ", valeur='" + valeur + '\'' +
                '}';
    }

    public int getIDbyName(String nom) throws SQLException {
        String sql = "SELECT id FROM ObjetConnecte WHERE deviceID = ?";
        try (DatabaseConnection dbConnection = new DatabaseConnection(); 
             PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {  
            pstmt.setString(1, nom);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    throw new SQLException("Aucun objet trouvé avec le nom: " + nom);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la récupération de l'ID par le nom", e);
        }
    }


    

}
