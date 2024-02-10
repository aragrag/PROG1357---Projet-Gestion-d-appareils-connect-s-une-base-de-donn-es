package com.gestionobjetsconn.models;

// Classe représentant un capteur
public class Capteur extends ObjetConnecte {
    private int id;
    private String typeMesure;
    private String uniteMesure;

    public Capteur(String nom, String deviceID, String adresseIP, boolean etat, String typeMesure, String uniteMesure) {
        super(nom, deviceID, adresseIP, etat);
        this.typeMesure = typeMesure;
        this.uniteMesure = uniteMesure;
    }

    public void mesurer() {
        System.out.println("Capteur " + getNom() + " mesure " + typeMesure);
    }

    public int getId() {
        return id;
    }

    public String getTypeMesure() {
        return typeMesure;
    }

    public String getUniteMesure() {
        return uniteMesure;
    }

    // Méthodes spécifiques aux capteurs
}