package com.gestionobjetsconn.models;

import java.sql.SQLException;

public abstract class Dispositif {
    private int id;
    private String nom;
    private boolean etat; // Actif ou inactif
    private int objetConnecteId;

    public Dispositif(int id, String nom, boolean etat, int objetConnecteId) {
        this.id = id;
        this.nom = nom;
        this.etat = etat;
        this.objetConnecteId = objetConnecteId;
    }
    public Dispositif(String nom, boolean etat, int objetConnecteId) {
        this.nom = nom;
        this.etat = etat;
        this.objetConnecteId = objetConnecteId;

    }
    // Getters et setters
    public int getID() {   
        return id;
    }  

    public String getNom() {
        return nom;
    }
    public int getObjetConnecteId() {
        return objetConnecteId;
    }

    public void setObjetConnecteId(int objetConnecteId) {
        this.objetConnecteId = objetConnecteId;
    }
    public boolean getEtat() {
        return etat;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void isActif(boolean etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return String.format("Nom: %s, État: %s", nom, etat ? "actif" : "inactif");
    }

    // Méthode abstraite pour définir l'action spécifique du dispositif
    public abstract void insererDonnees() throws SQLException;
    // public abstract void action();
}
