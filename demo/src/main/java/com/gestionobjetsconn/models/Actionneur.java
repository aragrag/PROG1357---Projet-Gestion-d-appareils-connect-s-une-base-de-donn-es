package com.gestionobjetsconn.models;

public class Actionneur extends ObjetConnecte {
    private String typeAction;
    private String emplacement;
    
    public Actionneur(String nom, String deviceID,String adresseIP, boolean etat, String typeAction, String emplacement) {
        super(nom, deviceID, adresseIP, etat);
        this.typeAction = typeAction;
        this.emplacement = emplacement;
    }

    public void actionner() {
        System.out.println("Actionneur " + getNom() + " effectue une action de type " + typeAction);
    }

    public String getTypeAction() {
        return typeAction;    
    }

    public String getEmplacement() {
        return emplacement;    
    }

    // public void afficherActionneur() {
    //     System.out.println("Actionneur ID : " + getID());
    //     System.out.println("Nom : " + getNom());
    //     System.out.println("Adresse IP : " + getadresseIP());
    //     System.out.println("Type d'action : " + getTypeAction());
    //     System.out.println("État : " + getEtat());
    //     System.out.println("Emplacement : " + getEmplacement());
    // }
    
}
