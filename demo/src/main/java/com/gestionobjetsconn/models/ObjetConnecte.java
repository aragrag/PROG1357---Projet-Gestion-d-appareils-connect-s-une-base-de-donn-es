package com.gestionobjetsconn.models;

import java.sql.SQLException;

// Classe de base pour les objets connectés

public abstract class ObjetConnecte {
    private int id;
    private String deviceID;
    private String nom;
    private String adresseIP;
    private boolean etat;

    public ObjetConnecte(String nom, String deviceID, String adresseIP, boolean etat) {
        this.nom = nom;
        this.deviceID = deviceID;
        this.adresseIP = adresseIP;
        this.etat = etat;
    }
    public ObjetConnecte(int id, String nom, String deviceID, String adresseIP, boolean etat) {
        this.id = id;
        this.nom = nom;
        this.deviceID = deviceID;
        this.adresseIP = adresseIP;
        this.etat = etat;
    }
    public void seConnecter() {
        System.out.println("Connexion à " + nom + " via l'adresse IP " + adresseIP);
    }

    // Méthodes communes à tous les objets connectés
    public int getID() {   
        return id;
    }  
    public String getNom() {   
        return nom;
    }       
    public String getdeviceID() {   
        return deviceID;
    }       
    public String getadresseIP() {   
        return adresseIP;
    } 
    public boolean getEtat() {   
        return etat;
    }
    @Override
    public String toString() {
        return String.format("ID: %d, Nom: %s, DeviceID: %s, Adresse IP: %s, État: %s", 
            this.getID(), nom, deviceID, adresseIP, etat ? "actif" : "inactif");
    }
    public abstract void insererDonnees() throws SQLException;
      
       
}
